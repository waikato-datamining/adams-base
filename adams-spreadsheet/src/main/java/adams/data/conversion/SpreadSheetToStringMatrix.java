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
 * SpreadSheetToStringMatrix.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Turns a spreadsheet into a string matrix.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-rows &lt;adams.core.Range&gt; (property: rows)
 * &nbsp;&nbsp;&nbsp;The range of rows to use.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The range of columns to use.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6556 $
 */
public class SpreadSheetToStringMatrix
  extends AbstractSpreadSheetToMatrix<String> {

  /** for serialization. */
  private static final long serialVersionUID = 4117708470154504868L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a spreadsheet into a string matrix.";
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return String[][].class;
  }

  /**
   * Generates a new matrix.
   *
   * @param rows	the number of rows
   * @param cols	the number of columns
   */
  protected String[][] newMatrix(int rows, int cols) {
    return new String[rows][cols];
  }

  /**
   * Determines whether to include this particular column.
   *
   * @param sheet	the spreadsheet to work on
   * @param col		the column to check
   * @return		true if to include in the matrix
   */
  protected boolean includeColumn(SpreadSheet sheet, int col) {
    return true;
  }

  /**
   * Returns the cell value at the specified location.
   *
   * @param sheet	the sheet to process
   * @param row		the row to work on
   * @param col		the column index in the row
   * @return		the cell value
   */
  protected String getValue(SpreadSheet sheet, Row row, int col) {
    if (!row.hasCell(col))
      return "";
    else
      return row.getCell(col).getContent();
  }
}
