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
 * MoveFile.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Moves a file to a different location (or just renames it).<br>
 * Source and target can be swapped as well.<br>
 * Simply forwards the input token if moving was successful.
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
 * &nbsp;&nbsp;&nbsp;default: MoveFile
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-file &lt;adams.core.io.PlaceholderFile&gt; (property: file)
 * &nbsp;&nbsp;&nbsp;The target file or directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-input-is-target &lt;boolean&gt; (property: inputIsTarget)
 * &nbsp;&nbsp;&nbsp;If true, then the input token will be used as target and the file parameter 
 * &nbsp;&nbsp;&nbsp;as source.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MoveFile
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -1725398133887399010L;

  /** the target. */
  protected PlaceholderFile m_File;

  /** whether the input token is the target instead. */
  protected boolean m_InputIsTarget;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Moves a file to a different location (or just renames it).\n"
        + "Source and target can be swapped as well.\n"
        + "Simply forwards the input token if moving was successful.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "file", "file",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "input-is-target", "inputIsTarget",
	    false);
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

    result  = QuickInfoHelper.toString(this, "file", m_File);

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "inputIsTarget", m_InputIsTarget, "input is target"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the target file.
   *
   * @param value	the file
   */
  public void setFile(PlaceholderFile value) {
    m_File = value;
    reset();
  }

  /**
   * Returns the target file.
   *
   * @return 		the file
   */
  public PlaceholderFile getFile() {
    return m_File;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fileTipText() {
    return "The target file or directory.";
  }

  /**
   * Sets whether the input is the actual target, not the file.
   *
   * @param value	if true then the input will be used as target
   * 			and the file as source
   */
  public void setInputIsTarget(boolean value) {
    m_InputIsTarget = value;
    reset();
  }

  /**
   * Returns whether the input is the actual target, not the file.
   *
   * @return 		true if the input will be used as target and the
   * 			file as source
   */
  public boolean getInputIsTarget() {
    return m_InputIsTarget;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputIsTargetTipText() {
    return
        "If true, then the input token will be used as target and the file "
      + "parameter as source.";
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
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class};
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
    File	source;
    File	target;

    result = null;
    source = null;
    target = null;
    try {
      if (m_InputToken.getPayload() instanceof File)
	file = new PlaceholderFile((File) m_InputToken.getPayload());
      else
	file = new PlaceholderFile((String) m_InputToken.getPayload());

      if (m_InputIsTarget) {
	target = file;
	source = m_File;
      }
      else {
	target = m_File;
	source = file;
      }

      getLogger().info("Source '" + source + "' exists: " + source.exists());
      getLogger().info("Target '" + target + "' exists: " + target.exists());
      if (!FileUtils.move(source.getAbsoluteFile(), target.getAbsoluteFile()))
	result = "Failed to move file: " + source + " -> " + target;
    }
    catch (Exception e) {
      result = handleException("Failed to move file: " + source + " -> " + target, e);
    }

    if (result == null)
      m_OutputToken = new Token(m_InputToken.getPayload());

    return result;
  }
}
