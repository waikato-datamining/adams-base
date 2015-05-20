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
 * AbstractFlowGenerator.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.generator;

import adams.core.option.AbstractOptionHandler;
import adams.flow.core.AbstractActor;

/**
 * Ancestor for generators that use model setups to generate flows.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of actor that owns this generator
 */
public abstract class AbstractFlowGenerator<T extends AbstractActor>
  extends AbstractOptionHandler
  implements Generator<T> {

  /** for serialization. */
  private static final long serialVersionUID = 7084115587699126182L;

  /** the owner. */
  protected T m_Owner;

  /**
   * Sets the owner.
   *
   * @param owner	the owner of this generator
   */
  public void setOwner(T value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner, null if none set
   */
  public T getOwner() {
    return m_Owner;
  }

  /**
   * Hook method for checks before generating the flow.
   * <br><br>
   * Checks for owner.
   */
  protected void check() {
    if (m_Owner == null)
      throw new IllegalStateException("No owner set!");
  }

  /**
   * Generates the flow.
   *
   * @return		the flow
   */
  protected abstract AbstractActor doGenerate();

  /**
   * Generates the flow and returns it.
   *
   * @return		the generated flow
   */
  public AbstractActor generate() {
    check();
    return doGenerate();
  }
}
