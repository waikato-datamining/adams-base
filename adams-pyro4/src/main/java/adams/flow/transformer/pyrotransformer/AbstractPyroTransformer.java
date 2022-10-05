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
 * AbstractPyroTransformer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.pyrotransformer;

import adams.flow.core.AbstractPyroCall;
import adams.flow.core.PyroInputConsumer;
import adams.flow.core.PyroOutputProducer;

/**
 * Ancestor for Pyro calls that transform data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPyroTransformer
  extends AbstractPyroCall
  implements PyroInputConsumer, PyroOutputProducer {

  private static final long serialVersionUID = -1690927665997303089L;

  /** the input object. */
  protected Object m_Input;

  /** the generated output object. */
  protected Object m_Output;

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
   * Before performing the actual call.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String preExecute() {
    String	result;

    result   = super.preExecute();
    m_Output = null;

    return result;
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
   * Returns the generated object.
   *
   * @return		the generated object
   */
  public Object output() {
    Object  result;

    result   = m_Output;
    m_Output = null;

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_Output != null);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();
    m_Input  = null;
    m_Output = null;
  }
}
