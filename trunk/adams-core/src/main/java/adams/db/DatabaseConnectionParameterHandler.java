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
 * DatabaseConnectionParameterHandler.java
 * Copyright (C) 2009-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.db;

import adams.core.base.BasePassword;

/**
 * For classes that explicitly allow to set database connection parameters,
 * like URL, user and password.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface DatabaseConnectionParameterHandler {

  /**
   * Sets the database URL to use (only if not connected).
   *
   * @param value	the URL to use
   */
  public void setURL(String value);

  /**
   * Returns the currently set database URL.
   *
   * @return		the current URL
   */
  public String getURL();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String URLTipText();

  /**
   * Sets the database user to use (only if not connected).
   *
   * @param value	the user to use
   */
  public void setUser(String value);

  /**
   * Returns the currently set database user.
   *
   * @return		the current user
   */
  public String getUser();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String userTipText();

  /**
   * Sets the database password to use (only if not connected).
   *
   * @param value	the password to use
   */
  public void setPassword(BasePassword value);

  /**
   * Returns the currently set database password.
   *
   * @return		the current password
   */
  public BasePassword getPassword();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String passwordTipText();
}
