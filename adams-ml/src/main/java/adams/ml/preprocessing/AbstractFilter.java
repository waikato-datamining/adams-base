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

import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.ml.data.Dataset;
import gnu.trove.list.TIntList;

/**
 * Ancestor for filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFilter
  extends AbstractOptionHandler
  implements Filter {

  private static final long serialVersionUID = -7832232995060446187L;

  /** whether the filter has been initialized. */
  protected boolean m_Initialized;

  /** the output format. */
  protected Dataset m_OutputFormat;

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Initialized  = false;
    m_OutputFormat = null;
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
      data.addCell("" + colName + i).setNative(input.getCell(cols.get(i)).getNative());
    }
  }
}
