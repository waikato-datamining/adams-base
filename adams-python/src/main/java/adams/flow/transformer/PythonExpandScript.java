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
 * PythonExpandScript.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.io.EncodingSupporter;
import adams.core.io.FileUtils;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Loads the Python script received as input, expands any variables and placeholders, saves it to the specified output file and forwards the output file name.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: PythonExpand
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
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when reading&#47;writing the script, use empty string
 * &nbsp;&nbsp;&nbsp;for default.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 *
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The file to save the expanded script to.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PythonExpandScript
  extends AbstractTransformer
  implements FileWriter, EncodingSupporter {

  private static final long serialVersionUID = -8993131926567885816L;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** the output file. */
  protected PlaceholderFile m_OutputFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Loads the Python script received as input, expands any variables "
      + "and placeholders, saves it to the specified output file and forwards "
      + "the output file name.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());

    m_OptionManager.add(
      "output", "outputFile",
      new PlaceholderFile("."));
  }

  /**
   * Sets the encoding to use.
   *
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }

  /**
   * Returns the encoding to use.
   *
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText() {
    return "The type of encoding to use when reading/writing the script, use empty string for default.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "encoding", m_Encoding, "enc: ");
    result += QuickInfoHelper.toString(this, "outputFile", m_OutputFile, ", out: ");

    return result;
  }

  /**
   * Set output file.
   *
   * @param value	file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Get output file.
   *
   * @return	file
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFileTipText() {
    return "The file to save the expanded script to.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
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
    String		result;
    PlaceholderFile	script;
    List<String> 	lines;
    String		line;
    int			i;

    result = null;

    script = new PlaceholderFile(m_InputToken.getPayload(String.class));
    if (!script.exists())
      result = "Script does not exist: " + script;
    else if (script.isDirectory())
      result = "Script points to a directory: " + script;

    lines = null;
    if (result == null) {
      lines = FileUtils.loadFromFile(script, m_Encoding.getValue());
      if (lines == null)
        result = "Failed to load script: " + script;
    }

    if (result == null) {
      for (i = 0; i < lines.size(); i++) {
	line = lines.get(i);
	line = getVariables().expand(line);
	line = Placeholders.getSingleton().expand(line);
	lines.set(i, line);
      }
      result = FileUtils.writeToFileMsg(m_OutputFile.getAbsolutePath(), Utils.flatten(lines, "\n"), false, m_Encoding.getValue());
    }

    return result;
  }
}
