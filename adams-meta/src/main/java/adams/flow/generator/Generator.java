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
 * Generator.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.generator;

import adams.flow.core.Actor;

/**
 * Generator interface for flow generators.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of actor that owns this generator
 */
public interface Generator<T extends Actor> {

  /**
   * Sets the owner.
   *
   * @param value	the owner of this generator
   */
  public void setOwner(T value);

  /**
   * Returns the owner.
   *
   * @return		the owner, null if none set
   */
  public T getOwner();

  /**
   * Generates the flow and returns it.
   *
   * @return		the generated flow
   */
  public Actor generate();
}
