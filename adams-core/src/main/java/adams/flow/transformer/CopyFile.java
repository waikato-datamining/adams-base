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
 * CopyFile.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Copies the file received on its input port to the target directory if it matches the provided regular expression.<br>
 * In case of a directory, the directory gets copied recursively.<br>
 * If required, you can also provide a new filename (just the name, no path).<br>
 * The generated target file&#47;directory gets forwarded in the flow.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: CopyFile
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-create-sub-dirs &lt;boolean&gt; (property: createSubDirectories)
 * &nbsp;&nbsp;&nbsp;If set to true, sub directories from the last path component of the inputs 
 * &nbsp;&nbsp;&nbsp;are created below the target directory (eg: &#47;some&#47;where&#47;blah -&gt; TARGET&#47;blah
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression to match the filename against.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 * <pre>-target-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: targetDirectory)
 * &nbsp;&nbsp;&nbsp;The target directory to copy the files to.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-filename &lt;java.lang.String&gt; (property: filename)
 * &nbsp;&nbsp;&nbsp;The (optional) new filename in the target directory, using the old name 
 * &nbsp;&nbsp;&nbsp;if left empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CopyFile
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 4670761846363281951L;

  /** whether the input is a directory and directory should be re-created below
   * target directory (only the last path component: /some/where/blah -&gt;
   * TARGET/blah will get created). */
  protected boolean m_CreateSubDirectories;

  /** the regular expression for the files to match. */
  protected BaseRegExp m_RegExp;

  /** the target directory. */
  protected PlaceholderDirectory m_TargetDirectory;

  /** the (optional) new filename. */
  protected String m_Filename;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Copies the file received on its input port to the target directory if "
      + "it matches the provided regular expression.\n"
      + "In case of a directory, the directory gets copied recursively.\n"
      + "If required, you can also provide a new filename (just the name, no path).\n"
      + "The generated target file/directory gets forwarded in the flow.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "create-sub-dirs", "createSubDirectories",
	    false);

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "target-dir", "targetDirectory",
	    new PlaceholderDirectory("."));

    m_OptionManager.add(
	    "filename", "filename",
	    "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "regExp", (m_RegExp.isMatchAll() ? "all" : m_RegExp.getValue()));
    result += QuickInfoHelper.toString(this, "targetDirectory", m_TargetDirectory, " -> ");
    result += QuickInfoHelper.toString(this, "filename", (m_Filename.isEmpty() ? "-keep old-" : m_Filename), ", new name: ");

    return result;
  }

  /**
   * Sets whether to re-create the sub-directory (last path component of input)
   * below the target directory.
   *
   * @param value	if true sub-dirs are created
   */
  public void setCreateSubDirectories(boolean value) {
    m_CreateSubDirectories = value;
    reset();
  }

  /**
   * Returns whether to re-create the sub-directory (last path component of input)
   * below the target directory.
   *
   * @return 		true if sub-dires are created
   */
  public boolean getCreateSubDirectories() {
    return m_CreateSubDirectories;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String createSubDirectoriesTipText() {
    return
        "If set to true, sub directories from the last path component of the "
      + "inputs are created below the target directory (eg: /some/where/blah "
      + "-> TARGET/blah).";
  }

  /**
   * Sets the regular expressions to use.
   *
   * @param value	the regular expressions
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expressions in use.
   *
   * @return 		the regular expressions
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to match the filename against.";
  }

  /**
   * Sets the target directory for the files.
   *
   * @param value	the target directory
   */
  public void setTargetDirectory(PlaceholderDirectory value) {
    m_TargetDirectory = value;
    reset();
  }

  /**
   * Returns the target directory for the files.
   *
   * @return 		the target directory
   */
  public PlaceholderDirectory getTargetDirectory() {
    return m_TargetDirectory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String targetDirectoryTipText() {
    return "The target directory to copy the files to.";
  }

  /**
   * Sets the new filename to use.
   *
   * @param value	the new filename, leave empty to use old
   */
  public void setFilename(String value) {
    m_Filename = value;
    reset();
  }

  /**
   * Returns the new filename to use.
   *
   * @return 		the new filename, ignored if empty
   */
  public String getFilename() {
    return m_Filename;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String filenameTipText() {
    return 
	"The (optional) new filename in the target directory, using the old "
	+ "name if left empty.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class, java.io.File.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    File	file;
    File	target;
    String	type;

    if (m_InputToken.getPayload() instanceof File)
      file = new PlaceholderFile((File) m_InputToken.getPayload());
    else
      file = new PlaceholderFile((String) m_InputToken.getPayload());
    type = (file.isDirectory() ? "directory" : "file");

    try {
      getLogger().info(type + " '" + file + "' exists: " + file.exists());
      if (file.exists()) {
	getLogger().info(type + " '" + file + "' matches '" + m_RegExp + "': " + m_RegExp.isMatch(file.getName()));
	if (m_RegExp.isMatch(file.getName())) {
	  if (m_CreateSubDirectories && file.isDirectory())
	    target = new File(m_TargetDirectory.getAbsolutePath() + File.separator + file.getName() + (m_Filename.isEmpty() ? "" : File.separator + m_Filename));
	  else
	    target = new File(m_TargetDirectory.getAbsolutePath() + (m_Filename.isEmpty() ? "" : File.separator + m_Filename));
	  getLogger().info("Target: " + target);
	  FileUtils.copy(file, target);

	  // create output
	  if (target.isDirectory())
	    m_OutputToken = new Token(target.getAbsolutePath() + File.separator + file.getName());
	  else
	    m_OutputToken = new Token(target.getAbsolutePath());
	}
      }
      result = null;
    }
    catch (Exception e) {
      result = handleException("Problem copying " + type + " '" + file + "': ", e);
    }

    return result;
  }
}
