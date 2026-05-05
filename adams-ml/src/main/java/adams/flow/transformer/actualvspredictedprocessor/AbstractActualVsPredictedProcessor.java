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
 * AbstractActualVsPredictedProcessor.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.actualvspredictedprocessor;

import adams.core.MessageCollection;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;

/**
 * Ancestor for processors that generate output from a spreadsheet with
 * actual vs predicted data.
 * The 1st column in the spreadsheet contains the actual values and the 2nd the predicted ones.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractActualVsPredictedProcessor<T>
  extends AbstractOptionHandler
  implements ActualVsPredictedProcessor<T> {

  private static final long serialVersionUID = 6189283310735592329L;

  /**
   * Checks whether the data can be processed.
   *
   * @param sheet	the data to check
   * @return		null if checks passed, otherwise error message
   */
  public String check(SpreadSheet sheet) {
    if (sheet == null)
      return "No data provided!";
    return null;
  }

  /**
   * Returns the actual numeric values.
   *
   * @param sheet	the sheet to extract the values from
   * @return		the values
   */
  protected double[] getActualNumeric(SpreadSheet sheet) {
    return SpreadSheetUtils.getNumericColumn(sheet, 0);
  }

  /**
   * Returns the predicted numeric values.
   *
   * @param sheet	the sheet to extract the values from
   * @return		the values
   */
  protected double[] getPredictedNumeric(SpreadSheet sheet) {
    return SpreadSheetUtils.getNumericColumn(sheet, 1);
  }

  /**
   * Returns the additional values, row indices if no additional columns.
   *
   * @param sheet	the sheet to extract data from
   * @return		the values
   */
  protected String[] getAdditional(SpreadSheet sheet) {
    String[] 		result;
    int			i;
    int			n;
    Row 		row;
    StringBuilder	cell;

    if (sheet.getColumnCount() > 3) {
      result = new String[sheet.getRowCount()];
      for (i = 0; i < sheet.getRowCount(); i++) {
	row = sheet.getRow(i);
	cell = new StringBuilder("\"").append(row.getCell(2).getContent()).append(":");  // instance index
	for (n = 3; n < sheet.getColumnCount(); n++) {
	  if (n > 3)
	    cell.append(",");
	  cell.append(row.getCell(n).getContent().replace("\"", ""));
	}
	cell.append("\"");
	result[i] = cell.toString();
      }
    }
    else {
      result = new String[sheet.getRowCount()];
      for (i = 0; i < result.length; i++)
	result[i] = "" + (i+1);
    }

    return result;
  }

  /**
   * Processes the actual vs predicted data and returns
   * the output generated.
   *
   * @param sheet	the data to process
   * @return		the output
   */
  protected abstract T doProcess(SpreadSheet sheet);

  /**
   * Processes the actual vs predicted data and returns
   * the output generated.
   *
   * @param sheet	the data to process
   * @param errors 	for collecting errors
   * @return		the output, null in case of error
   */
  @Override
  public T process(SpreadSheet sheet, MessageCollection errors) {
    String	msg;

    msg = check(sheet);
    if (msg != null) {
      errors.add(msg);
      return null;
    }

    return doProcess(sheet);
  }
}
