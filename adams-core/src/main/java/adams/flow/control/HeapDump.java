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
 * HeapDump.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;
import adams.flow.core.ControlActor;
import adams.flow.core.Unknown;
import adams.flow.transformer.AbstractTransformer;

/**
 <!-- globalinfo-start -->
 * Generates a heapdump and saves it to the specified file (with .hprof extension) whenever a token passes through.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
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
 * &nbsp;&nbsp;&nbsp;default: HeapDump
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
 * <pre>-output-file &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The file to save the heapdump to (must have .hprof extension).
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-live &lt;boolean&gt; (property: live)
 * &nbsp;&nbsp;&nbsp;If enabled, only live objects are output.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class HeapDump
  extends AbstractTransformer
  implements ControlActor, FileWriter {

  /** for serialization. */
  private static final long serialVersionUID = -7376953294946451589L;

  public static final String EXT_HPROF = ".hprof";

  /** the log file. */
  protected PlaceholderFile m_OutputFile;

  /** whether to output only live objects. */
  protected boolean m_Live;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Generates a heapdump and saves it to the specified file (with " + EXT_HPROF + " extension) whenever a token passes through.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-file", "outputFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "live", "live",
      true);
  }

  /**
   * Sets the output file.
   *
   * @param value	the file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output file.
   *
   * @return		the file
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
    return "The file to save the heapdump to (must have " + EXT_HPROF + " extension).";
  }

  /**
   * Sets whether to output only live objects.
   *
   * @param value	true if only live
   */
  public void setLive(boolean value) {
    m_Live = value;
    reset();
  }

  /**
   * Returns whether to output only live objects.
   *
   * @return		true if only live
   */
  public boolean getLive() {
    return m_Live;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String liveTipText() {
    return "If enabled, only live objects are output.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "outputFile", m_OutputFile, "output: ");
    result += QuickInfoHelper.toString(this, "live", m_Live, "live", ", ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String doExecute() {
    String	result;

    result = null;

    if (m_OutputFile.isDirectory())
      result = "Output file points to a directory!";
    else if (!m_OutputFile.getName().toLowerCase().endsWith(EXT_HPROF))
      result = "Output file does not have " + EXT_HPROF + " extension!";

    if (result == null)
      result = adams.core.management.HeapDump.generate(m_OutputFile, m_Live);

    if (result == null)
      m_OutputToken = m_InputToken;

    return result;
  }
}
