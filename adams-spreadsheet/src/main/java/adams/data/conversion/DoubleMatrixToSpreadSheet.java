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
 * DoubleMatrixToSpreadSheet.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Converts a double matrix into a SpreadSheet object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-data-row-type &lt;adams.data.spreadsheet.DataRow&gt; (property: dataRowType)
 * &nbsp;&nbsp;&nbsp;The type of row to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.DenseDataRow
 * </pre>
 * 
 * <pre>-spreadsheet-type &lt;adams.data.spreadsheet.SpreadSheet&gt; (property: spreadSheetType)
 * &nbsp;&nbsp;&nbsp;The type of spreadsheet to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.SpreadSheet
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DoubleMatrixToSpreadSheet
  extends AbstractMatrixToSpreadSheet {

  /** for serialization. */
  private static final long serialVersionUID = -2047404866165517428L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a double matrix into a SpreadSheet object.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Double[][].class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet		result;
    Double[][]		matrix;
    int			i;
    int			n;
    Row			row;

    result = newInstance();
    matrix = (Double[][]) getInput();

    // header
    row = result.getHeaderRow();
    for (i = 0; i < matrix[0].length; i++)
      row.addCell("" + (i+1)).setContent("col-" + (i+1));

    // data
    for (n = 0; n < matrix.length; n++) {
      row = result.addRow("" + (n+1));
      for (i = 0; i < matrix[0].length; i++)
	row.addCell("" + (i+1)).setContent(matrix[n][i]);
    }

    return result;
  }
}
