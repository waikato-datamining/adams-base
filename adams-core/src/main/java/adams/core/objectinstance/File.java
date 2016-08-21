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
 * File.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.objectinstance;

/**
 * Instantiates File objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class File
  extends AbstractObjectInstanceHandler {

  private static final long serialVersionUID = -427462544120343152L;

  /**
   * Returns whether this handler handles the specified class.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Class cls) {
    return (cls == java.io.File.class);
  }

  /**
   * Creates a new instance of the class.
   *
   * @param cls		the class to create a new instance from
   * @return		the instance, null if failed to instantiate
   */
  @Override
  public Object newInstance(Class cls) {
    return new java.io.File(".");
  }
}
