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

/**
 * AbstractBatchFilter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.filter;

import adams.data.NotesHandler;
import adams.data.container.DataContainer;

/**
 * Ancestor for batch filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBatchFilter<T extends DataContainer>
  extends AbstractFilter<T>
  implements BatchFilter<T> {

  private static final long serialVersionUID = 2531570968585605571L;

  /**
   * The default implementation only checks whether there is any data set.
   *
   * @param data	the data to filter
   */
  protected void checkBatchData(T[] data) {
    if (data == null)
      throw new IllegalStateException("No input data provided (null)!");
    if (data.length == 0)
      throw new IllegalStateException("No input data provided (zero-length array)!");
  }

  /**
   * Performs the actual batch filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  protected abstract T[] processBatchData(T[] data);

  /**
   * Batch filters the data.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  public T[] batchFilter(T[] data) {
    T[]	result;

    checkBatchData(data);
    result = processBatchData(data);

    if (!m_DontUpdateID) {
      for (T r : result)
	r.setID(r.getID() + "'");
    }

    for (T r: result) {
      if (r instanceof NotesHandler)
	((NotesHandler) r).getNotes().addProcessInformation(this);
    }

    return result;
  }
}
