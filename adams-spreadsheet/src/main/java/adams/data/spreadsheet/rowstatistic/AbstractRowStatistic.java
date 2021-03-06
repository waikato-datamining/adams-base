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
 * AbstractRowStatistic.java
 * Copyright (C) 2014-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet.rowstatistic;

import adams.core.ErrorProvider;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;

/**
 * Ancestor for row statistic generators.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRowStatistic
  extends AbstractOptionHandler
  implements ErrorProvider {

  /** for serialization. */
  private static final long serialVersionUID = -7187115330070305271L;

  /** the columns to operate on. */
  protected SpreadSheetColumnRange m_Columns;

  /** the column indices to operate on. */
  protected transient int[] m_ColumnIndices;

  /** the last error that was generated. */
  protected String m_LastError;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "columns", "columns",
      new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL));
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ColumnIndices = null;
  }

  /**
   * Sets the columns to operate on.
   *
   * @param value 	the columns
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the columns to operate on.
   *
   * @return 		the columns
   */
  public SpreadSheetColumnRange getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsTipText() {
    return "The columns to include.";
  }

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
   * @param rowIndex	the row index
   * @return		null if everythin ok, otherwise error message
   */
  protected String check(SpreadSheet sheet, int rowIndex) {
    String	result;

    result = null;

    if (sheet == null)
      result = "No data provided!";

    if (result == null) {
      if (rowIndex >= sheet.getRowCount())
	result = "Row index out of bounds: " + rowIndex;
    }

    if (result == null) {
      m_Columns.setData(sheet);
      m_ColumnIndices = m_Columns.getIntIndices();
      if (m_ColumnIndices.length == 0)
        result = "Failed to locate columns: " + m_Columns.getRange();
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

    result = new DefaultSpreadSheet();

    row = result.getHeaderRow();
    row.addCell("K").setContent("Key");
    row.addCell("V").setContent("Value");

    return result;
  }

  /**
   * Performs initialization before the cells are being visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @param rowIndex	the row index
   */
  protected abstract void preVisit(SpreadSheet sheet, int rowIndex);

  /**
   * Gets called with every cell in the row for generating the stats.
   * 
   * @param row		the current row
   * @param colIndex	the column index
   */
  protected abstract void doVisit(Row row, int colIndex);

  /**
   * Finishes up the stats generation after all the cells have been visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @param rowIndex	the row index
   * @return		the generated stats
   */
  protected abstract SpreadSheet postVisit(SpreadSheet sheet, int rowIndex);

  /**
   * Performs the actual generation of statistics for the specified
   * spreadsheet row.
   *
   * @param sheet	the spreadsheet to generate the stats for
   * @param rowIndex	the row index
   * @return		the generated statistics, null in case of an error
   */
  protected SpreadSheet doGenerate(SpreadSheet sheet, int rowIndex) {
    int		i;
    Row		row;

    preVisit(sheet, rowIndex);
    row = sheet.getRow(rowIndex);
    for (i = 0; i < m_ColumnIndices.length; i++)
      doVisit(row, m_ColumnIndices[i]);
    return postVisit(sheet, rowIndex);
  }

  /**
   * Generates statistics for the specified spreadsheet row.
   *
   * @param sheet	the spreadsheet to generate the stats for
   * @param rowIndex	the row index
   * @return		the generated statistics, null in case of an error
   */
  public SpreadSheet generate(SpreadSheet sheet, int rowIndex) {
    SpreadSheet 	result;

    result = null;

    m_LastError = check(sheet, rowIndex);
    if (m_LastError == null) {
      result = doGenerate(sheet, rowIndex);
      if (result == null) {
	if (m_LastError == null)
	  m_LastError = "Error occurred generating statistics!";
      }
    }

    return result;
  }
}
