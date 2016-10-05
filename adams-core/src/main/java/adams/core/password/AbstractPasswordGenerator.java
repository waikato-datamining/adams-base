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
 * AbstractPasswordGenerator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.password;

import adams.core.logging.CustomLoggingLevelObject;

/**
 * Ancestor for password generators..
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPasswordGenerator
  extends CustomLoggingLevelObject
  implements PasswordGenerator {

  private static final long serialVersionUID = 1785797168166556572L;

  /**
   * Checks whether there is another password available.
   *
   * @return		true if another password available
   */
  public abstract boolean hasNext();

  /**
   * Returns the next password.
   *
   * @return		the next password, null if no more available
   */
  public abstract String next();
}
