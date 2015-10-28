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
 * CallableWithResult.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.multiprocess;

import java.util.concurrent.Callable;

/**
 * Callable that stores the result for future use.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class CallableWithResult<T>
  implements Callable<T> {

  /** the result. */
  protected T m_Result;

  /**
   * Performs the actual code execution.
   *
   * @return		the result
   * @throws Exception	if failed to execute
   */
  protected abstract T doCall() throws Exception;

  /**
   * Executes the code.
   *
   * @return		the result
   * @throws Exception	if failed to execute
   */
  @Override
  public T call() throws Exception {
    m_Result = null;
    m_Result = doCall();
    return m_Result;
  }

  /**
   * Returns the result of the execution.
   *
   * @return		the result
   */
  public T getResult() {
    return m_Result;
  }
}
