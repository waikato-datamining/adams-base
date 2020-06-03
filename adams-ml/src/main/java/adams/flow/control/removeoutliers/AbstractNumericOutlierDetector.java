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

/**
 * AbstractNumericOutlierDetector.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.removeoutliers;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

/**
 * Ancestor for numeric outlier detection.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractNumericOutlierDetector
  extends AbstractOutlierDetector {

  private static final long serialVersionUID = 8204585970761312700L;

  /**
   * Check method before detection.
   *
   * @param sheet	the spreadsheet to analyze
   * @param actual	the column with the actual values
   * @param predicted	the column with the predicted values
   * @return		null if check passed, otherwise error message
   */
  @Override
  public String check(SpreadSheet sheet, SpreadSheetColumnIndex actual, SpreadSheetColumnIndex predicted) {
    String	result;

    result = super.check(sheet, actual, predicted);

    if (result == null) {
      if (!sheet.isNumeric(actual.getIntIndex()))
	result = "'Actual' column is not numeric: " + actual;
      else if (!sheet.isNumeric(predicted.getIntIndex()))
	result = "'Predicted' column is not numeric: " + predicted;
    }

    return result;
  }

  /**
   * Extracts the double values from the specified column.
   *
   * @param sheet	the sheet to get the data from
   * @param col		the column
   * @return		the data, null entries for non-numeric or missing values
   */
  protected Double[] extractColumn(SpreadSheet sheet, SpreadSheetColumnIndex col) {
    Double[] 	result;
    Row		row;
    int		i;
    int		index;
    Double	value;

    result = new Double[sheet.getRowCount()];
    index  = col.getIntIndex();
    for (i = 0; i < sheet.getRowCount(); i++) {
      row   = sheet.getRow(i);
      value = null;
      if (row.hasCell(index) && !row.getCell(index).isMissing())
	value = row.getCell(index).toDouble();
      result[i] = value;
    }

    return result;
  }

  /**
   * Computes the difference between the two columns.
   *
   * @param actual	the column with the actual values
   * @param predicted	the column with the predicted values
   * @param useRelative whether to divide the difference by the actual value (NaN if 0)
   * @return		the difference, uses NaN if either column has non-numeric or missing value
   */
  protected double[] diff(Double[] actual, Double[] predicted, boolean useRelative) {
    double[] 		result;
    int			i;

    result = new double[actual.length];
    for (i = 0; i < actual.length; i++) {
      if ((actual[i] != null) && (predicted[i] != null)) {
	result[i] = actual[i] - predicted[i];
	if (useRelative) {
	  if (actual[i] != 0)
	    result[i] /= actual[i];
	  else
	    result[i] = Double.NaN;
	}
      }
      else {
	result[i] = Double.NaN;
      }
    }

    return result;
  }

  /**
   * Computes the difference between the two columns.
   *
   * @param sheet	the sheet to get the data from
   * @param actual	the column with the actual values
   * @param predicted	the column with the predicted values
   * @param useRelative whether to divide the difference by the actual value (NaN if 0)
   * @return		the difference, uses NaN if either column has non-numeric or missing value
   */
  protected double[] diff(SpreadSheet sheet, SpreadSheetColumnIndex actual, SpreadSheetColumnIndex predicted, boolean useRelative) {
    return diff(extractColumn(sheet, actual), extractColumn(sheet, predicted), useRelative);
  }
}
