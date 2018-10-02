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
 * AbstractStreamFilter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.ml.preprocessing;

import adams.data.spreadsheet.Row;
import adams.ml.data.Dataset;

/**
 * Ancestor for stream filters with column subset handling.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractColumnSubsetStreamFilter
  extends AbstractColumnSubsetFilter
  implements StreamFilter {

  private static final long serialVersionUID = -3113520581439940331L;

  /**
   * Before the actual filter initialization. Initializes the columns.
   *
   * @param data 	the data to initialize with
   * @see		#initColumns(Dataset)
   * @throws Exception	if initialization fails
   */
  protected void preInitFilter(Row data) throws Exception {
    reset();
    initColumns((Dataset) data.getOwner());
  }

  /**
   * Filter-specific initialization.
   *
   * @param data 	the data to initialize with
   * @throws Exception	if initialization fails
   */
  protected abstract void doInitFilter(Row data) throws Exception;

  /**
   * Initializes the output format.
   *
   * @param data	the output format
   * @throws Exception	if initialization fails
   */
  protected abstract Dataset initOutputFormat(Row data) throws Exception;

  /**
   * After the filter has been initialized. Sets the initialized flag.
   *
   * @param data 	the data to initialize with
   * @see		#initOutputFormat(Row)
   * @see		#isInitialized()
   * @throws Exception	if initialization fails
   */
  protected void postInitFilter(Row data) throws Exception {
    m_OutputFormat = initOutputFormat(data);
    m_Initialized = true;
  }

  /**
   * Initializes the filter.
   *
   * @param data 	the data to initialize with
   * @see		#preInitFilter(Row)
   * @see		#doInitFilter(Row)
   * @see		#postInitFilter(Row)
   * @throws Exception	if initialization fails
   */
  protected synchronized void initFilter(Row data) throws Exception {
    preInitFilter(data);
    doInitFilter(data);
    postInitFilter(data);
  }

  /**
   * Filters the dataset row coming through.
   *
   * @param data	the data to filter
   * @return		the filtered data
   * @throws Exception	if filtering fails
   */
  protected abstract Row doFilter(Row data) throws Exception;

  /**
   * Filters the dataset row coming through.
   *
   * @param data	the data to filter
   * @return		the filtered data
   * @throws Exception	if filtering fails
   */
  public synchronized Row filter(Row data) throws Exception {
    if (!isInitialized())
      initFilter(data);
    return doFilter(data);
  }

  /**
   * Filters the dataset coming through.
   *
   * @param data	the data to filter
   * @return		the filtered data
   * @throws Exception	if filtering fails
   */
  @Override
  public synchronized Dataset filter(Dataset data) throws Exception {
    Dataset	result;
    Row		filtered;

    result = null;

    for (Row row: data.rows()) {
      filtered = filter(row);
      if (result == null)
        result = (Dataset) filtered.getOwner().getHeader();
      result.addRow().assign(filtered);
    }

    return result;
  }
}
