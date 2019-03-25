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

package adams.data.weka.rowfinder;

import adams.core.base.BaseInteger;
import weka.core.Instances;

import java.util.Arrays;

/**
 * Row finder that finds a constant set of rows.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class Constant
  extends AbstractRowFinder {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = -8214035090974332457L;

  /** The constant set of rows to find. */
  protected BaseInteger[] m_Rows;

  /** The raw form of the rows. */
  protected int[] m_RawRows;

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "rows", "rows",
      new BaseInteger[0]);
  }

  /**
   * Gets the constant set of rows to find.
   *
   * @return  The rows to find.
   */
  public BaseInteger[] getRows() {
    return m_Rows;
  }

  /**
   * Sets the constant set of rows to find.
   *
   * @param value The rows to find.
   */
  public void setRows(BaseInteger[] value) {
    m_Rows = value;
    m_RawRows = new int[value.length];
    for (int i = 0; i < value.length; i++) {
      m_RawRows[i] = m_Rows[i].intValue();
    }
    reset();
  }

  /**
   * Gets the tip-text for the rows option.
   *
   * @return  The tip-text as a string.
   */
  public String rowsTipText() {
    return "The constant set of rows to find.";
  }

  /**
   * Returns the rows of interest in the dataset.
   *
   * @param data	the dataset to inspect
   * @return		the rows of interest
   */
  @Override
  protected int[] doFindRows(Instances data) {

    return Arrays.copyOf(m_RawRows, m_RawRows.length);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Row finder that finds a constant set of rows.";
  }
}
