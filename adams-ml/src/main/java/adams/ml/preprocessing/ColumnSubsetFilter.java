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
 * ColumnSubsetFilter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.ml.preprocessing;

import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.ml.data.Dataset;

/**
 * Filter with column subset handling.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface ColumnSubsetFilter
  extends Filter {

  /**
   * Sets how to determine columns for filtering.
   *
   * @param value 	the type
   */
  public void setColumnSubset(ColumnSubset value);

  /**
   * Returns how to determine columns for filtering.
   *
   * @return 		the type
   */
  public ColumnSubset getColumnSubset();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnSubsetTipText();

  /**
   * Sets the range of columns to use for filtering (if {@link ColumnSubset#RANGE}).
   *
   * @param value 	the range
   */
  public void setColRange(SpreadSheetColumnRange value);

  /**
   * Returns the range of columns to use for filtering (if {@link ColumnSubset#RANGE}).
   *
   * @return 		the range
   */
  public SpreadSheetColumnRange getColRange();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colRangeTipText();

  /**
   * Sets the regular expression to use on the column names to determine whether
   *    * to use a column for filtering (if {@link ColumnSubset#REGEXP}).
   *
   * @param value 	the expression
   */
  public void setColRegExp(BaseRegExp value);

  /**
   * Returns the regular expression to use on the column names to determine whether
   * to use a column for filtering (if {@link ColumnSubset#REGEXP}).
   *
   * @return 		the expression
   */
  public BaseRegExp getColRegExp();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colRegExpTipText();

  /**
   * Sets whether to drop other columns that aren't used for filtering from
   * the output. Does not affect any class columns.
   *
   * @param value 	true if to drop
   */
  public void setDropOtherColumns(boolean value);

  /**
   * Returns whether to drop other columns that aren't used for filtering from
   * the output. Does not affect any class columns.
   *
   * @return 		true if to drop
   */
  public boolean getDropOtherColumns();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dropOtherColumnsTipText();

  /**
   * Returns whether the filter has been initialized.
   *
   * @return		true if initialized
   */
  public boolean isInitialized();

  /**
   * Returns the output format.
   *
   * @return		the format, null if not yet defined
   */
  public Dataset getOutputFormat();
}
