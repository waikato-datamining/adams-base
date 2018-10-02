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
 * AbstractFilter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.ml.preprocessing;

import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.ml.capabilities.Capabilities;
import adams.ml.capabilities.CapabilitiesHelper;
import adams.ml.data.Dataset;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 * Ancestor for filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractColumnSubsetFilter
  extends AbstractFilter
  implements ColumnSubsetFilter {

  private static final long serialVersionUID = -7832232995060446187L;

  /** how to determine columns to use for filtering. */
  protected ColumnSubset m_ColumnSubset;

  /** the columns to operate on (if {@link ColumnSubset#RANGE}). */
  protected SpreadSheetColumnRange m_ColRange;

  /** the column names to operate on (if {@link ColumnSubset#REGEXP}). */
  protected BaseRegExp m_ColRegExp;

  /** whether to drop the unprocessed columns (excl class columns). */
  protected boolean m_DropOtherColumns;

  /** the indices of the columns to use in the filtering process. */
  protected TIntList m_DataColumns;

  /** the indices of the class columns to use in the filtering process. */
  protected TIntList m_ClassColumns;

  /** the indices of the other columns not to be used in the filtering process. */
  protected TIntList m_OtherColumns;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "column-subset", "columnSubset",
      ColumnSubset.RANGE);

    m_OptionManager.add(
      "col-range", "colRange",
      new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL));

    m_OptionManager.add(
      "col-regexp", "colRegExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "drop-other-columns", "dropOtherColumns",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_DataColumns  = new TIntArrayList();
    m_ClassColumns = new TIntArrayList();
    m_OtherColumns = new TIntArrayList();
  }

  /**
   * Sets how to determine columns for filtering.
   *
   * @param value 	the type
   */
  public void setColumnSubset(ColumnSubset value) {
    m_ColumnSubset = value;
    reset();
  }

  /**
   * Returns how to determine columns for filtering.
   *
   * @return 		the type
   */
  public ColumnSubset getColumnSubset() {
    return m_ColumnSubset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnSubsetTipText() {
    return "Defines how to determine the columns to use for filtering.";
  }

  /**
   * Sets the range of columns to use for filtering (if {@link ColumnSubset#RANGE}).
   *
   * @param value 	the range
   */
  public void setColRange(SpreadSheetColumnRange value) {
    m_ColRange = value;
    reset();
  }

  /**
   * Returns the range of columns to use for filtering (if {@link ColumnSubset#RANGE}).
   *
   * @return 		the range
   */
  public SpreadSheetColumnRange getColRange() {
    return m_ColRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colRangeTipText() {
    return "The range of columns to use in the filtering process.";
  }

  /**
   * Sets the regular expression to use on the column names to determine whether
   *    * to use a column for filtering (if {@link ColumnSubset#REGEXP}).
   *
   * @param value 	the expression
   */
  public void setColRegExp(BaseRegExp value) {
    m_ColRegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to use on the column names to determine whether
   * to use a column for filtering (if {@link ColumnSubset#REGEXP}).
   *
   * @return 		the expression
   */
  public BaseRegExp getColRegExp() {
    return m_ColRegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colRegExpTipText() {
    return "The regular expression to use on the column names to determine whether to use a column for filtering.";
  }

  /**
   * Sets whether to drop other columns that aren't used for filtering from
   * the output. Does not affect any class columns.
   *
   * @param value 	true if to drop
   */
  public void setDropOtherColumns(boolean value) {
    m_DropOtherColumns = value;
    reset();
  }

  /**
   * Returns whether to drop other columns that aren't used for filtering from
   * the output. Does not affect any class columns.
   *
   * @return 		true if to drop
   */
  public boolean getDropOtherColumns() {
    return m_DropOtherColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dropOtherColumnsTipText() {
    return "If enabled, other columns that aren't used for filtering get removed from the output; does not affect any class columns.";
  }

  /**
   * Initializes the columns to use for the filter.
   *
   * @param data	the data to check against
   * @throws Exception	if initialization fails
   */
  protected void initColumns(Dataset data) throws Exception {
    Capabilities	caps;
    TIntList		cols;
    int			i;
    String		msg;

    cols = null;
    switch (m_ColumnSubset) {
      case RANGE:
        if (!m_ColRange.isAllRange()) {
	  m_ColRange.setSpreadSheet(data);
	  cols = new TIntArrayList(m_ColRange.getIntIndices());
	}
        break;

      case REGEXP:
        if (!m_ColRegExp.isMatchAll()) {
          cols = new TIntArrayList();
	  for (i = 0; i < data.getColumnCount(); i++) {
	    if (m_ColRegExp.isMatch(data.getColumnName(i)))
	      cols.add(i);
	  }
	}
        break;

      default:
        throw new Exception("Unhandled column determination: " + m_ColumnSubset);
    }

    m_ClassColumns.add(data.getClassAttributeIndices());

    caps = getCapabilities();
    if (cols != null) {
      for (int col : cols.toArray()) {
	msg = CapabilitiesHelper.handles(caps, data, col);
	if (msg == null) {
	  if (!data.isClassAttribute(col))
	    m_DataColumns.add(col);
	}
      }
    }
    else {
      for (i = 0; i < data.getColumnCount(); i++) {
	msg = CapabilitiesHelper.handles(caps, data, i);
	if (msg == null) {
	  if (!data.isClassAttribute(i))
	    m_DataColumns.add(i);
	}
      }
    }

    if (!m_DropOtherColumns) {
      for (i = 0; i < data.getColumnCount(); i++) {
	if (!m_DataColumns.contains(i) && !m_ClassColumns.contains(i))
	  m_OtherColumns.add(i);
      }
    }

    if (isLoggingEnabled()) {
      getLogger().info("Data cols: " + m_DataColumns);
      getLogger().info("Class cols: " + m_ClassColumns);
      getLogger().info("Other cols: " + m_OtherColumns);
    }
  }
}
