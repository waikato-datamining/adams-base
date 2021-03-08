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
 * MultiSpreadSheetOperation.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.flow.transformer.multispreadsheetoperation.AbstractMultiSpreadSheetOperation;
import adams.flow.transformer.multispreadsheetoperation.PassThrough;

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
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiSpreadSheetOperation
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 3363405805013155845L;

  /** the operation to use. */
  protected AbstractMultiSpreadSheetOperation m_Operation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified operation the incoming spreadsheet array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "operation", "operation",
      new PassThrough());
  }

  /**
   * Sets the operation to apply.
   *
   * @param value	the operation
   */
  public void setOperation(AbstractMultiSpreadSheetOperation value) {
    m_Operation = value;
    reset();
  }

  /**
   * Returns the operation to apply.
   *
   * @return		the operation
   */
  public AbstractMultiSpreadSheetOperation getOperation() {
    return m_Operation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String operationTipText() {
    return "The operation to apply to the incoming spreadsheets.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "operation", m_Operation, "operation: ");
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{m_Operation.generates()};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    SpreadSheet[] 	sheets;
    MessageCollection	errors;
    Object		output;

    result = null;
    sheets = null;

    if (m_InputToken.hasPayload(SpreadSheet[].class))
      sheets = m_InputToken.getPayload(SpreadSheet[].class);
    else
      result = m_InputToken.unhandledData();

    if (result == null) {
      errors = new MessageCollection();
      try {
        output = m_Operation.process(sheets, errors);
        if (!errors.isEmpty())
          result = errors.toString();
        else
	  m_OutputToken = new Token(output);
      }
      catch (Exception e) {
        result = handleException("Failed to process spreadsheets using: " + m_Operation.toCommandLine(), e);
      }
    }

    return result;
  }
}
