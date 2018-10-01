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
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
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
public abstract class AbstractFilter
  extends AbstractOptionHandler
  implements Filter {

  private static final long serialVersionUID = -7832232995060446187L;

  /**
   * Defines how to determine columns to use in the filtering process.
   */
  public enum Columns {
    RANGE,
    REGEXP,
  }

  /** how to determine columns to use for filtering. */
  protected Columns m_Columns;

  /** the columns to operate on (if {@link Columns#RANGE}). */
  protected SpreadSheetColumnRange m_ColRange;

  /** the column names to operate on (if {@link Columns#REGEXP}). */
  protected BaseRegExp m_ColRegExp;

  /** whether to drop the unprocessed columns (excl class columns). */
  protected boolean m_DropOtherColumns;

  /** whether the filter has been initialized. */
  protected boolean m_Initialized;

  /** the indices of the columns to use in the filtering process. */
  protected TIntList m_DataColumns;

  /** the indices of the class columns to use in the filtering process. */
  protected TIntList m_ClassColumns;

  /** the indices of the other columns not to be used in the filtering process. */
  protected TIntList m_OtherColumns;

  /** the output format. */
  protected Dataset m_OutputFormat;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "columns", "columns",
      Columns.RANGE);

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

    m_Initialized  = false;
    m_DataColumns  = new TIntArrayList();
    m_ClassColumns = new TIntArrayList();
    m_OtherColumns = new TIntArrayList();
    m_OutputFormat = null;
  }

  /**
   * Sets how to determine columns for filtering.
   *
   * @param value 	the type
   */
  public void setColumns(Columns value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns how to determine columns for filtering.
   *
   * @return 		the type
   */
  public Columns getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsTipText() {
    return "Defines how to determine the columns to use for filtering.";
  }

  /**
   * Sets the range of columns to use for filtering (if {@link Columns#RANGE}).
   *
   * @param value 	the range
   */
  public void setColRange(SpreadSheetColumnRange value) {
    m_ColRange = value;
    reset();
  }

  /**
   * Returns the range of columns to use for filtering (if {@link Columns#RANGE}).
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
   *    * to use a column for filtering (if {@link Columns#REGEXP}).
   *
   * @param value 	the expression
   */
  public void setColRegExp(BaseRegExp value) {
    m_ColRegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to use on the column names to determine whether
   * to use a column for filtering (if {@link Columns#REGEXP}).
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
   * Returns whether the filter has been initialized.
   *
   * @return		true if initialized
   */
  public boolean isInitialized() {
    return m_Initialized;
  }

  /**
   * Returns the output format.
   *
   * @return		the format, null if not yet defined
   */
  public Dataset getOutputFormat() {
    return m_OutputFormat;
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
    switch (m_Columns) {
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
        throw new Exception("Unhandled column determination: " + m_Columns);
    }

    caps = getCapabilities();
    if (cols != null) {
      for (int col : cols.toArray()) {
	msg = CapabilitiesHelper.handles(caps, data, col);
	if (msg == null) {
	  if (data.isClassAttribute(col))
	    m_ClassColumns.add(col);
	  else
	    m_DataColumns.add(col);
	}
      }
    }
    else {
      for (i = 0; i < data.getColumnCount(); i++) {
	msg = CapabilitiesHelper.handles(caps, data, i);
	if (msg == null) {
	  if (data.isClassAttribute(i))
	    m_ClassColumns.add(i);
	  else
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

  /**
   * Appends the column names to the header.
   *
   * @param input	the input data
   * @param header	the header so far
   * @param cols	the columns to append
   */
  protected void appendHeader(SpreadSheet input, Row header, TIntList cols) {
    int		i;
    String	colName;

    for (i = 0; i < cols.size(); i++) {
      colName = input.getColumnName(cols.get(i));
      header.addCell("" + colName + i).setContentAsString(colName);
    }
  }

  /**
   * Appends the column values to the row.
   *
   * @param input	the input data
   * @param data	the row so far
   * @param cols	the columns to append
   */
  protected void appendData(Row input, Row data, TIntList cols) {
    int		i;
    String	colName;

    for (i = 0; i < cols.size(); i++) {
      colName = input.getOwner().getColumnName(cols.get(i));
      data.addCell("" + colName + i).setNative(input.getCell(cols.get(i)));
    }
  }
}
