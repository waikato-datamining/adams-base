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
 * AbstractDataContainerPostProcessor.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.datacontainer;

import adams.core.option.AbstractOptionHandler;
import adams.data.container.DataContainer;

/**
 * Ancestor for {@link DataContainer} post-processors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataContainerPostProcessor<T extends DataContainer>
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4489141650668803307L;

  /**
   * Checks the data to post-process.
   * <br><br>
   * Default implementation only checks whether data is present.
   * 
   * @param data	the data to check
   */
  protected void check(T data) {
    if (data == null)
      throw new IllegalStateException("No data provided!");
  }
  
  /**
   * Performs the actual post-processing.
   * 
   * @param data	the data to process
   * @return		the processed data
   */
  protected abstract T doPostProcess(T data);
  
  /**
   * Post-processes the data.
   * 
   * @param data	the data to post-process
   * @return		the processed data
   */
  public T postProcess(T data) {
    check(data);
    return doPostProcess(data);
  }
}
