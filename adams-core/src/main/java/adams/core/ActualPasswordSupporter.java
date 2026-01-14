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
 * ActualPasswordSupporter.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.base.BasePassword;

/**
 * Interface for classes that allow setting the actual password.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ActualPasswordSupporter {

  /**
   * Sets the actual password to use.
   *
   * @param value	the password
   */
  public void setActualPassword(BasePassword value);

  /**
   * Returns the current actual password in use.
   *
   * @return		the password
   */
  public BasePassword getActualPassword();
}
