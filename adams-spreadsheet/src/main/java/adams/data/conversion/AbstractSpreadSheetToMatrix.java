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
 * AbstractSpreadSheetToMatrix.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.Range;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import gnu.trove.list.array.TIntArrayList;

/**
 * Ancestor for conversions that turn a spreadsheet into a matrix of some
 * data type.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the cell type of the matrix
 */
public abstract class AbstractSpreadSheetToMatrix<T>
  extends AbstractConversion {

  private static final long serialVersionUID = 487215942580922724L;

  /** the range of columns to operate on. */
  protected SpreadSheetColumnRange m_Columns;

  /** the range of rows to operate on. */
  protected Range m_Rows;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "rows", "rows",
	    new Range(Range.ALL));

    m_OptionManager.add(
	    "columns", "columns",
	    new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL));
  }

  /**
   * Sets the range of rows to use.
   *
   * @param value	the rows
   */
  public void setRows(Range value) {
    m_Rows = value;
    reset();
  }

  /**
   * Returns the range of rows to use.
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
    return "The range of rows to use.";
  }

  /**
   * Sets the range of columns to use.
   *
   * @param value	the columns
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the range of columns to use.
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
    return "The range of columns to use.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Determines whether to include this particular column.
   *
   * @param sheet	the spreadsheet to work on
   * @param col		the column to check
   * @return		true if to include in the matrix
   */
  protected abstract boolean includeColumn(SpreadSheet sheet, int col);

  /**
   * Generates a new matrix.
   *
   * @param rows	the number of rows
   * @param cols	the number of columns
   */
  protected abstract T[][] newMatrix(int rows, int cols);

  /**
   * Determines all the columns to include in the matrix.
   *
   * @param sheet	the spreadsheet to process
   * @return		the indices (0-based)
   */
  protected int[] determineColumns(SpreadSheet sheet) {
    TIntArrayList	result;
    int[] 		cols;

    result = new TIntArrayList();
    m_Columns.setData(sheet);
    cols = m_Columns.getIntIndices();
    for (int col : cols) {
      if (includeColumn(sheet, col))
	result.add(col);
    }

    return result.toArray();
  }

  /**
   * Returns the cell value at the specified location.
   *
   * @param sheet	the sheet to process
   * @param row		the row to work on
   * @param col		the column index in the row
   * @return		the cell value
   */
  protected abstract T getValue(SpreadSheet sheet, Row row, int col);

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    T[][]		result;
    SpreadSheet		sheet;
    int[]		rows;
    int[] 		cols;
    int			i;
    int			n;
    Row 		row;

    sheet = (SpreadSheet) m_Input;

    m_Rows.setMax(sheet.getRowCount());
    rows = m_Rows.getIntIndices();
    if (rows.length == 0)
      throw new IllegalStateException("No rows selected!");

    cols = determineColumns(sheet);
    if (cols.length == 0)
      throw new IllegalStateException("No columns selected!");

    result  = newMatrix(rows.length, cols.length);
    for (n = 0; n < rows.length; n++) {
      row = sheet.getRow(rows[n]);
      for (i = 0; i < cols.length; i++)
	result[n][i] = getValue(sheet, row, cols[i]);
    }

    return result;
  }
}
