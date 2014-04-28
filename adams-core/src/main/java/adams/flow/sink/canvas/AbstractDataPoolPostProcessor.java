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
 * AbstractDataPoolPostProcessor.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.canvas;

import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;

/**
 * For post-processing {@link DataPool}s, e.g., removing obsolete/expired 
 * elements.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataPoolPostProcessor
  extends AbstractOptionHandler 
  implements ShallowCopySupporter<AbstractDataPoolPostProcessor> {

  /** for serialization. */
  private static final long serialVersionUID = 4954702085142362993L;

  /**
   * Checks whether the pool can be processed,
   * 
   * @param pool	the pool to check
   * @return		null if check passed, otherwise error message
   */
  protected String check(DataPool pool) {
    return null;
  }

  /**
   * Does the actual post-processing.
   * 
   * @param pool	the pool to process
   * @return		null if processed successfully, otherwise error message
   */
  protected abstract String doPostProcess(DataPool pool);

  /**
   * Post-processes the data pool.
   * 
   * @param pool	the pool to process
   * @return		null if processed successfully, otherwise error message
   */
  public String postProcess(DataPool pool) {
    String	result;
    
    result = check(pool);
    if (result == null)
      result = doPostProcess(pool);
    
    return result;
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @return		the shallow copy
   */
  public AbstractDataPoolPostProcessor shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractDataPoolPostProcessor shallowCopy(boolean expand) {
    return (AbstractDataPoolPostProcessor) OptionUtils.shallowCopy(this, expand);
  }
}
