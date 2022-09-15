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
 * SheetIndex.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import adams.core.AbstractDataBackedIndex;
import adams.core.Index;

/**
 * Extended {@link Index} class that can use a sheet name to determine an
 * index of a sheet as well.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SheetIndex
  extends AbstractDataBackedIndex<String[]> {

  /** for serialization. */
  private static final long serialVersionUID = -4358263779315198808L;

  /**
   * Initializes with no index.
   */
  public SheetIndex() {
    super();
  }

  /**
   * Initializes with the given index, but no maximum.
   *
   * @param index	the index to use
   */
  public SheetIndex(String index) {
    super(index);
  }

  /**
   * Initializes with the given index and maximum.
   *
   * @param index	the index to use
   * @param max		the maximum of the 1-based index (e.g., use "10" to
   * 			allow "1-10" or -1 for uninitialized)
   */
  public SheetIndex(String index, int max) {
    super(index, max);
  }
  
  /**
   * Sets the sheet names to use for interpreting the sheet name.
   *
   * @param value	the sheet names to use, can be null
   */
  public void setSheetNames(String[] value) {
    setData(value);
  }
  
  /**
   * Returns the underlying sheet names.
   * 
   * @return		the underlying sheet names, null if none set
   * @see		#getData()
   */
  public String[] getSheetNames() {
    return getData();
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  @Override
  public SheetIndex getClone() {
    return (SheetIndex) super.getClone();
  }

  /**
   * Returns the number of columns the dataset has.
   * 
   * @param data	the dataset to retrieve the number of columns
   */
  @Override
  protected int getNumNames(String[] data) {
    return data.length;
  }
  
  /**
   * Returns the column name at the specified index.
   * 
   * @param data	the dataset to use
   * @param colIndex	the column index
   * @return		the column name
   */
  @Override
  protected String getName(String[] data, int colIndex) {
    return data[colIndex];
  }

  /**
   * Returns the example.
   *
   * @return		the example
   */
  @Override
  public String getExample() {
    return
        "An index is a number starting with 1; sheet names "
      + "(case-sensitive) as well as the following placeholders can be used: "
      + FIRST + ", " + SECOND + ", " + THIRD + ", " + LAST_2 + ", " + LAST_1 + ", " + LAST + "; "
      + "numeric indices can be enforced by preceding them with '#' (eg '#12'); "
      + "sheet names can be surrounded by double quotes.";
  }
}
