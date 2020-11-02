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
 * SpreadSheetUnorderedRowRange.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import adams.core.AbstractDataBackedUnorderedRange;
import adams.core.UnorderedRange;

/**
 * Extended {@link UnorderedRange} for handling spreadsheets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetUnorderedRowRange
  extends AbstractDataBackedUnorderedRange<SpreadSheet> {

  /** for serialization. */
  private static final long serialVersionUID = 5215987200366396733L;

  /**
   * Initializes with no range.
   */
  public SpreadSheetUnorderedRowRange() {
    super();
  }

  /**
   * Initializes with the given range, but no maximum.
   *
   * @param range	the range to use
   */
  public SpreadSheetUnorderedRowRange(String range) {
    super(range);
  }

  /**
   * Initializes with the given range and maximum.
   *
   * @param range	the range to use
   * @param max		the maximum of the 1-based index (e.g., use "10" to
   * 			allow "1-10" or -1 for uninitialized)
   */
  public SpreadSheetUnorderedRowRange(String range, int max) {
    super(range, max);
  }

  /**
   * Sets the spreadsheet to use for interpreting the column name.
   * 
   * @param value	the spreadsheet to use, can be null
   */
  public void setSpreadSheet(SpreadSheet value) {
    setData(value);
  }
  
  /**
   * Returns the underlying spreadsheet.
   * 
   * @return		the underlying spreadsheet, null if none set
   * @see		#getData()
   */
  public SpreadSheet getSpreadSheet() {
    return getData();
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  @Override
  public SpreadSheetUnorderedRowRange getClone() {
    return (SpreadSheetUnorderedRowRange) super.getClone();
  }

  /**
   * Returns the number of rows the dataset has.
   *
   * @param data	the dataset to retrieve the number of rows
   */
  @Override
  protected int getNumNames(SpreadSheet data) {
    return data.getRowCount();
  }

  /**
   * Unused.
   *
   * @param data	the dataset to use
   * @param colIndex	the row index
   * @return		the row name
   */
  @Override
  protected String getName(SpreadSheet data, int colIndex) {
    return "";
  }

  /**
   * Parses the subrange.
   *
   * @param subrange	the subrange
   * @return		the indices
   */
  protected int[] parseSubRange(String subrange) {
    SpreadSheetRowRange range;

    range = new SpreadSheetRowRange(subrange);
    if (m_Data == null)
      range.setMax(m_Max);
    else
      range.setData(m_Data);

    return range.getIntIndices();
  }
}
