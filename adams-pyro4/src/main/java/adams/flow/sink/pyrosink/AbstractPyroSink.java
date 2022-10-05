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
 * AbstractPyroSink.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink.pyrosink;

import adams.flow.core.AbstractPyroCall;
import adams.flow.core.PyroInputConsumer;

/**
 * Ancestor for Pyro calls that consume data but don't produce any output.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPyroSink
  extends AbstractPyroCall
  implements PyroInputConsumer {

  private static final long serialVersionUID = -2647962184125430789L;

  /** the input object. */
  protected Object m_Input;

  /**
   * The method that accepts the input object.
   *
   * @param obj		the object to accept and process
   */
  @Override
  public void input(Object obj) {
    m_Input = obj;
  }

  /**
   * After performing the actual call.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String postExecute() {
    String	result;

    result  = super.postExecute();
    m_Input = null;

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();
    m_Input = null;
  }
}
