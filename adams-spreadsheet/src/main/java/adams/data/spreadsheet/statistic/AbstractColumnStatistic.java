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
 * AbstractColumnStatistic.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet.statistic;

import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for column statistic generators.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractColumnStatistic
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -7187115330070305271L;

  /** the last error that was generated. */
  protected String m_LastError;

  /**
   * Checks whether there was an error with the last stats generation.
   *
   * @return		true if there was an error
   * @see		#getLastError()
   */
  public boolean hasLastError() {
    return (m_LastError != null);
  }

  /**
   * Returns the last error that occurred.
   *
   * @return		the last error, null if none occurred
   */
  public String getLastError() {
    return m_LastError;
  }

  /**
   * Checks whether the spreadsheet can be handled.
   *
   * @param sheet	the spreadsheet to check
   * @param colIndex	the column index
   * @return		null if everythin ok, otherwise error message
   */
  protected String check(SpreadSheet sheet, int colIndex) {
    String	result;

    result = null;

    if (sheet == null)
      result = "No data provided!";

    if (result == null) {
      if (colIndex >= sheet.getColumnCount())
	result = "Column index out of bounds: " + colIndex;
    }

    return result;
  }

  /**
   * Generates the header for the statistics result.
   *
   * @return		the generated header
   */
  protected SpreadSheet createOutputHeader() {
    SpreadSheet	result;
    Row		row;

    result = new SpreadSheet();

    row = result.getHeaderRow();
    row.addCell("K").setContent("Key");
    row.addCell("V").setContent("Value");

    return result;
  }

  /**
   * Performs initialization before the cells are being visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @param colIndex	the column index
   */
  protected abstract void preVisit(SpreadSheet sheet, int colIndex);

  /**
   * Gets called with every row in the spreadsheet for generating the stats.
   * 
   * @param row		the current row
   * @param colIndex	the column index
   */
  protected abstract void doVisit(Row row, int colIndex);

  /**
   * Finishes up the stats generation after all the cells have been visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @param colIndex	the column index
   * @return		the generated stats
   */
  protected abstract SpreadSheet postVisit(SpreadSheet sheet, int colIndex);

  /**
   * Performs the actual generation of statistics for the specified
   * spreadsheet column.
   *
   * @param sheet	the spreadsheet to generate the stats for
   * @param colIndex	the column index
   * @return		the generated statistics, null in case of an error
   */
  protected SpreadSheet doGenerate(SpreadSheet sheet, int colIndex) {
    preVisit(sheet, colIndex);
    for (Row row: sheet.rows())
      doVisit(row, colIndex);
    return postVisit(sheet, colIndex);
  }

  /**
   * Generates statistics for the specified spreadsheet column.
   *
   * @param sheet	the spreadsheet to generate the stats for
   * @param colIndex	the column index
   * @return		the generated statistics, null in case of an error
   */
  public SpreadSheet generate(SpreadSheet sheet, int colIndex) {
    SpreadSheet 	result;

    result = null;

    m_LastError = check(sheet, colIndex);
    if (m_LastError == null) {
      result = doGenerate(sheet, colIndex);
      if (result == null) {
	if (m_LastError == null)
	  m_LastError = "Error occurred generating statistics!";
      }
    }

    return result;
  }
}
