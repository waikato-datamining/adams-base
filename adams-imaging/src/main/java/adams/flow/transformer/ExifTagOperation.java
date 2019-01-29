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
 * ExifTagOperation.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.env.Environment;
import adams.flow.core.Token;
import adams.flow.transformer.exiftagoperation.AbstractExifTagOperation;
import adams.flow.transformer.exiftagoperation.ApacheCommonsExifTagRead;

/**
 <!-- globalinfo-start -->
 * Performs the specified EXIF operation on the incoming data.
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
 * &nbsp;&nbsp;&nbsp;default: ExifTagOperation
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
 * <pre>-operation &lt;adams.flow.transformer.exiftagoperation.AbstractExifTagOperation&gt; (property: operation)
 * &nbsp;&nbsp;&nbsp;The operation to execute.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.exiftagoperation.ApacheCommonsExifTagRead
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ExifTagOperation
  extends AbstractTransformer {

  private static final long serialVersionUID = 1884823351778616872L;

  /** the operation to use. */
  protected AbstractExifTagOperation m_Operation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs the specified EXIF operation on the incoming data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "operation", "operation",
      new ApacheCommonsExifTagRead());
  }

  /**
   * Sets the operation to perform.
   *
   * @param value	the operation
   */
  public void setOperation(AbstractExifTagOperation value) {
    m_Operation = value;
    reset();
  }

  /**
   * Returns the operation to perform.
   *
   * @return		the operation
   */
  public AbstractExifTagOperation getOperation() {
    return m_Operation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String operationTipText() {
    return "The operation to execute.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return m_Operation.accepts();
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return m_Operation.generates();
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "operation", m_Operation, "operation: ");
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Object		input;
    Object		output;
    MessageCollection	errors;

    result = null;
    input  = m_InputToken.getPayload();
    errors = new MessageCollection();

    try {
      output = m_Operation.process(input, errors);
      if (errors.isEmpty() && (output != null)) {
	m_OutputToken = new Token(output);
      }
      else {
        if (errors.isEmpty())
	  result = "Failed to perform EXIF operation " + m_Operation + " on: " + input;
        else
	  result = "Failed to perform EXIF operation " + m_Operation + " on: " + input + "\n" + errors;
      }
    }
    catch (Exception e) {
      result = handleException("Failed to perform EXIF operation " + m_Operation + " on: " + input, e);
    }

    return result;
  }

  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    new ExifTagOperation();
  }
}
