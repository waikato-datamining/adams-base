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
 * AbstractColumnSubsetBatchFilter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.ml.preprocessing;

import adams.ml.data.Dataset;

/**
 * Ancestor for batch filters with column subset handling.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractColumnSubsetBatchFilter
  extends AbstractColumnSubsetFilter
  implements BatchFilter {

  private static final long serialVersionUID = -3113520581439940331L;

  /**
   * Before the actual filter initialization. Initializes the columns.
   *
   * @param data 	the data to initialize with
   * @see		#initColumns(Dataset)
   * @throws Exception	if initialization fails
   */
  protected void preInitFilter(Dataset data) throws Exception {
    reset();
    initColumns(data);
  }

  /**
   * Filter-specific initialization.
   *
   * @param data 	the data to initialize with
   * @throws Exception	if initialization fails
   */
  protected abstract void doInitFilter(Dataset data) throws Exception;

  /**
   * Initializes the output format.
   *
   * @param data	the output format
   * @throws Exception	if initialization fails
   */
  protected abstract Dataset initOutputFormat(Dataset data) throws Exception;

  /**
   * After the filter has been initialized. Sets the initialized flag.
   *
   * @param data 	the data to initialize with
   * @see		#initOutputFormat(Dataset)
   * @see		#isInitialized()
   * @throws Exception	if initialization fails
   */
  protected void postInitFilter(Dataset data) throws Exception {
    m_OutputFormat = initOutputFormat(data);
    m_Initialized = true;
  }

  /**
   * Initializes the filter.
   *
   * @param data 	the data to initialize with
   * @see		#preInitFilter(Dataset)
   * @see		#doInitFilter(Dataset)
   * @see		#postInitFilter(Dataset)
   * @throws Exception	if initialization fails
   */
  protected synchronized void initFilter(Dataset data) throws Exception {
    preInitFilter(data);
    doInitFilter(data);
    postInitFilter(data);
  }

  /**
   * Filters the dataset coming through.
   *
   * @param data	the data to filter
   * @return		the filtered data
   * @throws Exception	if filtering fails
   */
  protected abstract Dataset doFilter(Dataset data) throws Exception;

  /**
   * Filters the dataset coming through.
   *
   * @param data	the data to filter
   * @return		the filtered data
   * @throws Exception	if filtering fails
   */
  public synchronized Dataset filter(Dataset data) throws Exception {
    if (!isInitialized())
      initFilter(data);
    return doFilter(data);
  }
}
