/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * DeleteDir.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.lister.LocalDirectoryLister;
import adams.core.logging.LoggingLevel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Deletes the dirs that match the regular expression below the specified directory.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: DeleteDir
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: directory)
 * &nbsp;&nbsp;&nbsp;The directory to delete the dirs in.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the dirs must match (empty string matches all
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: .*
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-recursive &lt;boolean&gt; (property: recursive)
 * &nbsp;&nbsp;&nbsp;Whether to search recursively or not.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-max-depth &lt;int&gt; (property: maxDepth)
 * &nbsp;&nbsp;&nbsp;The maximum depth to search in recursive mode (1 = only watch directory,
 * &nbsp;&nbsp;&nbsp;-1 = infinite).
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DeleteDir
    extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = 4670761846363281951L;

  /** for listing the contents. */
  protected LocalDirectoryLister m_Lister;

  /** whether to delete the specified directory itself. */
  protected boolean m_DeleteItself;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Deletes the dirs that match the regular expression below the "
	    + "specified directory.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"dir", "directory",
	new PlaceholderDirectory("."));

    m_OptionManager.add(
	"regexp", "regExp",
	new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	"recursive", "recursive",
	false);

    m_OptionManager.add(
	"max-depth", "maxDepth",
	-1);

    m_OptionManager.add(
	"delete-itself", "deleteItself",
	false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Lister = new LocalDirectoryLister();
    m_Lister.setListFiles(false);
    m_Lister.setListDirs(true);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    result = QuickInfoHelper.toString(this, "directory", getDirectory());
    if (!m_DeleteItself) {
      result += QuickInfoHelper.toString(this, "regExp", (getRegExp().isEmpty() ? "-all-" : getRegExp().getValue()), File.separator);
      options = new ArrayList<>();
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "recursive", getRecursive(), "(recursive)"));
      result += QuickInfoHelper.flatten(options);
    }

    return result;
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public synchronized void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    m_Lister.setLoggingLevel(value);
  }

  /**
   * Sets the directory to delete the dirs in.
   *
   * @param value	the directory
   */
  public void setDirectory(PlaceholderDirectory value) {
    m_Lister.setWatchDir(value.getAbsolutePath());
    reset();
  }

  /**
   * Returns the directory to delete the dirs in.
   *
   * @return		the directory.
   */
  public PlaceholderDirectory getDirectory() {
    return new PlaceholderDirectory(m_Lister.getWatchDir());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String directoryTipText() {
    return "The directory to delete the dirs in.";
  }

  /**
   * Sets the regular expression for the dirs.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_Lister.setRegExp(value);
    reset();
  }

  /**
   * Returns the regular expression for the dirs.
   *
   * @return		the regular expression
   * @see		LocalDirectoryLister#getRegExp()
   */
  public BaseRegExp getRegExp() {
    return m_Lister.getRegExp();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression that the dirs must match (empty string matches all).";
  }

  /**
   * Sets whether to search recursively.
   *
   * @param value	true if search is recursively
   * @see		LocalDirectoryLister#setRecursive(boolean)
   */
  public void setRecursive(boolean value) {
    m_Lister.setRecursive(value);
    reset();
  }

  /**
   * Returns whether search is recursively.
   *
   * @return		true if search is recursively
   * @see		LocalDirectoryLister#getRecursive()
   */
  public boolean getRecursive() {
    return m_Lister.getRecursive();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String recursiveTipText() {
    return "Whether to search recursively or not.";
  }

  /**
   * Sets the maximum depth to search (in recursive mode). 1 = only watch
   * directory, -1 = infinite.
   *
   * @param value	the maximum number of directory levels to traverse
   */
  public void setMaxDepth(int value) {
    m_Lister.setMaxDepth(value);
    reset();
  }

  /**
   * Returns the maximum depth to search (in recursive mode). 1 = only watch
   * directory, -1 = infinite.
   *
   * @return		the maximum number of directory levels to traverse
   */
  public int getMaxDepth() {
    return m_Lister.getMaxDepth();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxDepthTipText() {
    return
	"The maximum depth to search in recursive mode (1 = only watch "
	    + "directory, -1 = infinite).";
  }

  /**
   * Sets whether to delete itself.
   *
   * @param value	true if to delete itself
   */
  public void setDeleteItself(boolean value) {
    m_DeleteItself = value;
    reset();
  }

  /**
   * Returns whether to delete itself.
   *
   * @return		true if to delete itself
   */
  public boolean getDeleteItself() {
    return m_DeleteItself;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String deleteItselfTipText() {
    return "Whether to delete the directory itself rather than matching sub-directories.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String[]	files;
    File 	dir;
    int		i;
    boolean	deleted;

    result = null;

    if (m_DeleteItself) {
      dir = getDirectory();
      try {
	if (isLoggingEnabled())
	  getLogger().info("directory '" + dir + "' exists: " + dir.exists());
	deleted = FileUtils.delete(dir);
	if (isLoggingEnabled())
	  getLogger().info("directory '" + dir + "' deleted: " + deleted);
      }
      catch (Exception e) {
	result = handleException("Problem deleting directory '" + dir + "': ", e);
      }
    }
    else {
      files = m_Lister.list();
      for (i = 0; i < files.length; i++) {
	dir = new File(files[i]);
	try {
	  if (isLoggingEnabled())
	    getLogger().info("directory '" + dir + "' exists: " + dir.exists());
	  if (dir.exists()) {
	    deleted = FileUtils.delete(dir);
	    if (isLoggingEnabled())
	      getLogger().info("directory '" + dir + "' deleted: " + deleted);
	    if (!deleted) {
	      if (result == null)
		result = "";
	      else
		result += "\n";
	      result += "Failed to delete directory: " + dir;
	    }
	  }
	}
	catch (Exception e) {
	  result = handleException("Problem deleting directory '" + dir + "': ", e);
	}
      }
    }

    return result;
  }
}
