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
 * PasswordSupporter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.base.BasePassword;

/**
 * Interface for classes that handle a password.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface PasswordSupporter {

  /**
   * Sets the password.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value);

  /**
   * Returns the password.
   *
   * @return 		the password
   */
  public BasePassword getPassword();
}
