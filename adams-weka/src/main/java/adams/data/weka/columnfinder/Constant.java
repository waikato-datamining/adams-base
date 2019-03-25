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
 * Constant.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.weka.columnfinder;

import adams.core.base.BaseInteger;
import weka.core.Instances;

import java.util.Arrays;

/**
 * Column finder that finds a constant set of columns.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class Constant
  extends AbstractColumnFinder {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = 1729448295565014453L;

  /** The set of columns to find. */
  protected BaseInteger[] m_Columns;

  /** The raw representation of the columns to find. */
  protected int[] m_RawColumns;

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "columns", "columns",
      new BaseInteger[0]);
  }

  /**
   * Gets the constant set of columns to find.
   *
   * @return  The set of columns to find.
   */
  public BaseInteger[] getColumns() {
    return m_Columns;
  }

  /**
   * Sets the constant set of columns to find.
   *
   * @param value  The set of columns to find.
   */
  public void setColumns(BaseInteger[] value) {
    m_Columns = value;
    m_RawColumns = new int[value.length];
    for (int i = 0; i < value.length; i++) {
      m_RawColumns[i] = m_Columns[i].intValue();
    }
    reset();
  }

  /**
   * Gets the tip-text for the columns option.
   *
   * @return  The tip-text as a string.
   */
  public String columnsTipText() {
    return "The constant set of columns to find.";
  }

  /**
   * Returns the columns of interest in the dataset.
   *
   * @param data	the dataset to inspect
   * @return		the columns of interest
   */
  @Override
  protected int[] doFindColumns(Instances data) {
    return Arrays.copyOf(m_RawColumns, m_RawColumns.length);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Column finder that finds a constant set of columns.";
  }
}
