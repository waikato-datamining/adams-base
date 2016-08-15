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
 * AbstractMatrixStatistic.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet.matrixstatistic;

import adams.core.Range;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetView;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Ancestor for matrix statistic generators, i.e., ones that take the
 * specified subset of the spreadsheet into account.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9765 $
 */
public abstract class AbstractMatrixStatistic
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -7187115330070305271L;

  /** the rows of the subset to obtain. */
  protected Range m_Rows;

  /** the columns of the subset to obtain. */
  protected SpreadSheetColumnRange m_Columns;

  /** the last error that was generated. */
  protected String m_LastError;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "row", "rows",
      new Range(Range.ALL));

    m_OptionManager.add(
      "col", "columns",
      new SpreadSheetColumnRange(Range.ALL));
  }

  /**
   * Sets the rows of the subset.
   *
   * @param value	the rows
   */
  public void setRows(Range value) {
    m_Rows = value;
    reset();
  }

  /**
   * Returns the rows of the subset.
   *
   * @return		the rows
   */
  public Range getRows() {
    return m_Rows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowsTipText() {
    return "The rows of the subset to retrieve.";
  }

  /**
   * Sets the columns of the subset.
   *
   * @param value	the columns
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the columns of the subset.
   *
   * @return		the columns
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
    return "The columns of the subset to retrieve; " + m_Columns.getExample();
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
   * @return		null if everythin ok, otherwise error message
   */
  protected String check(SpreadSheet sheet) {
    String	result;

    result = null;

    if (sheet == null)
      result = "No data provided!";

    if (sheet != null) {
      m_Rows.setMax(sheet.getRowCount());
      m_Columns.setData(sheet);
      if (m_Rows.getIntIndices().length == 0)
	result = "No rows selected!";
      else if (m_Columns.getIntIndices().length == 0)
	result = "No columns selected!";
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
   * Returns all the numeric values in the matrix, skipping NaN and infinity.
   *
   * @param sheet	the spreadsheet to process
   * @return		the values
   */
  protected TDoubleList getNumericValues(SpreadSheet sheet) {
    TDoubleArrayList 	result;
    Row			row;
    double		val;
    int			i;

    result = new TDoubleArrayList();
    for (i = 0; i < sheet.getRowCount(); i++) {
      row = sheet.getRow(i);
      for (Cell cell: row.cells()) {
	if (!cell.isMissing() && cell.isNumeric()) {
	  val = cell.toDouble();
	  if (!Double.isNaN(val) && !Double.isInfinite(val))
	    result.add(val);
	}
      }
    }

    return result;
  }

  /**
   * Performs the actual generation of statistics for the specified
   * spreadsheet.
   *
   * @param sheet	the spreadsheet subset to generate the stats for
   * @return		the generated statistics, null in case of an error
   */
  protected abstract SpreadSheet doGenerate(SpreadSheet sheet);

  /**
   * Generates statistics for the specified spreadsheet.
   *
   * @param sheet	the spreadsheet to generate the stats for
   * @return		the generated statistics, null in case of an error
   */
  public SpreadSheet generate(SpreadSheet sheet) {
    SpreadSheet 	result;

    result = null;

    m_LastError = check(sheet);

    if (m_LastError == null) {
      // subset?
      m_Rows.setMax(sheet.getRowCount());
      m_Columns.setData(sheet);
      if (!m_Rows.isAllRange() || !m_Columns.isAllRange()) {
	sheet = new SpreadSheetView(
	  sheet,
	  m_Rows.isAllRange() ? null : m_Rows.getIntIndices(),
	  m_Columns.isAllRange() ? null : m_Columns.getIntIndices());
      }

      result = doGenerate(sheet);

      if (result == null) {
	if (m_LastError == null)
	  m_LastError = "Error occurred generating statistics!";
      }
    }

    return result;
  }
}
