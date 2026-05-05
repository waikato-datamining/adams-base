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
 * ActualVsPredictedProcessor.java
 * Copyright (C) 2026 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ActualVsPredictedProcessor
  extends AbstractTransformer {

  private static final long serialVersionUID = -2575293379884905336L;

  /** the processor to use. */
  protected adams.flow.transformer.actualvspredictedprocessor.ActualVsPredictedProcessor m_Processor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified processor to the incoming actual vs predicted data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "processor", "processor",
      new adams.flow.transformer.actualvspredictedprocessor.PassThrough());
  }

  /**
   * Sets the processor to use.
   *
   * @param value	the processor
   */
  public void setProcessor(adams.flow.transformer.actualvspredictedprocessor.ActualVsPredictedProcessor value) {
    m_Processor = value;
    reset();
  }

  /**
   * Returns the processor in use.
   *
   * @return		the processor
   */
  public adams.flow.transformer.actualvspredictedprocessor.ActualVsPredictedProcessor getProcessor() {
    return m_Processor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String processorTipText() {
    return "The processor to use for processoring the Dataset objects.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    if (m_Processor != null)
      return new Class[]{m_Processor.generates()};
    else
      return new Class[]{Unknown.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "processor", m_Processor, "processor: ");
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    SpreadSheet		sheet;
    MessageCollection	errors;
    Object		output;

    result = null;
    sheet  = m_InputToken.getPayload(SpreadSheet.class);
    errors = new MessageCollection();

    try {
      output = m_Processor.process(sheet, errors);
      if (output != null)
	m_OutputToken = new Token(output);
      else if (!errors.isEmpty())
	result = errors.toString();
      else
	result = "Failed to generate output for unknown reason!";
    }
    catch (Exception e) {
      result = handleException("Failed to process actual vs predicted data!", e);
    }

    return result;
  }
}
