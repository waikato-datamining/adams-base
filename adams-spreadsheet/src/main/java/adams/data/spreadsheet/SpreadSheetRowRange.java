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
 * SpreadSheetRowRange.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import adams.core.AbstractDataBackedRange;
import adams.core.Range;

import java.util.ArrayList;
import java.util.List;

/**
 * Extended {@link Range} class for SpreadSheet objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetRowRange
  extends AbstractDataBackedRange<SpreadSheet> {

  /** for serialization. */
  private static final long serialVersionUID = 5215987200366396733L;

  /**
   * Initializes with no range.
   */
  public SpreadSheetRowRange() {
    super();
  }

  /**
   * Initializes with the given range, but no maximum.
   *
   * @param range	the range to use
   */
  public SpreadSheetRowRange(String range) {
    super(range);
  }

  /**
   * Initializes with the given range and maximum.
   *
   * @param range	the range to use
   * @param max		the maximum of the 1-based index (e.g., use "10" to
   * 			allow "1-10" or -1 for uninitialized)
   */
  public SpreadSheetRowRange(String range, int max) {
    super(range, max);
  }

  /**
   * Sets the spreadsheet to use for interpreting the row name.
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
  public SpreadSheetRowRange getClone() {
    return (SpreadSheetRowRange) super.getClone();
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
   * Returns the names.
   *
   * @return		the names
   */
  @Override
  protected List<String> getNames() {
    return new ArrayList<>();
  }
}
