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
 * DeleteFile.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.core.io.DirectoryLister;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.logging.LoggingLevel;

/**
 <!-- globalinfo-start -->
 * Deletes the files&#47;dirs that match the regular expression below the specified directory.<br>
 * In case of directories, deletion is performed recursively (if a directory matches, then the content gets deleted regardless whether it matches the regular expression or not).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: DeleteFile
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: directory)
 * &nbsp;&nbsp;&nbsp;The directory to delete the files in.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the files&#47;dirs must match (empty string matches
 * &nbsp;&nbsp;&nbsp;all).
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 * <pre>-recursive (property: recursive)
 * &nbsp;&nbsp;&nbsp;Whether to search recursively or not.
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
 * @version $Revision$
 */
public class DeleteFile
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = 4670761846363281951L;

  /** for listing the contents. */
  protected DirectoryLister m_Lister;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Deletes the files/dirs that match the regular expression below the "
      + "specified directory.\n"
      + "In case of directories, deletion is performed recursively (if a directory "
      + "matches, then the content gets deleted regardless whether it matches "
      + "the regular expression or not).";
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
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Lister = new DirectoryLister();
    m_Lister.setListFiles(true);
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

    result  = QuickInfoHelper.toString(this, "directory", getDirectory());
    result += QuickInfoHelper.toString(this, "regExp", getRegExp(), File.separator);
    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "recursive", getRecursive(), "(recursive)"));
    result += QuickInfoHelper.flatten(options);

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
   * Sets the directory to delete the files in.
   *
   * @param value	the directory
   */
  public void setDirectory(PlaceholderDirectory value) {
    m_Lister.setWatchDir(value);
    reset();
  }

  /**
   * Returns the directory to delete the files in.
   *
   * @return		the directory.
   */
  public PlaceholderDirectory getDirectory() {
    return m_Lister.getWatchDir();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String directoryTipText() {
    return "The directory to delete the files in.";
  }

  /**
   * Sets the regular expression for the files/dirs.
   *
   * @param value	the regular expression
   * @see		DirectoryLister#setRegExp(String)
   */
  public void setRegExp(BaseRegExp value) {
    m_Lister.setRegExp(value);
    reset();
  }

  /**
   * Returns the regular expression for the files/dirs.
   *
   * @return		the regular expression
   * @see		DirectoryLister#getRegExp()
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
    return "The regular expression that the files/dirs must match (empty string matches all).";
  }

  /**
   * Sets whether to search recursively.
   *
   * @param value	true if search is recursively
   * @see		DirectoryLister#setRecursive(boolean)
   */
  public void setRecursive(boolean value) {
    m_Lister.setRecursive(value);
    reset();
  }

  /**
   * Returns whether search is recursively.
   *
   * @return		true if search is recursively
   * @see		DirectoryLister#getRecursive()
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String[]	files;
    File	file;
    int		i;
    boolean	deleted;
    String	type;

    result = null;

    files = m_Lister.list();
    for (i = 0; i < files.length; i++) {
      file = new File(files[i]);
      type = (file.isDirectory() ? "directory" : "file");
      try {
	if (isLoggingEnabled())
	  getLogger().info(type + " '" + file + "' exists: " + file.exists());
	if (file.exists()) {
	  deleted = FileUtils.delete(file);
	  if (isLoggingEnabled())
	    getLogger().info(type + " '" + file + "' deleted: " + deleted);
	  if (!deleted) {
	    if (result == null)
	      result = "";
	    else
	      result += "\n";
	    result += "Failed to delete " + type + ": " + file;
	  }
	}
      }
      catch (Exception e) {
	result = handleException("Problem deleting " + type + " '" + file + "': ", e);
      }
    }

    return result;
  }
}
