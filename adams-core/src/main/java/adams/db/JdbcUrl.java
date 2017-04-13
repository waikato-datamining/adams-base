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
 * JdbcUrl.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.db;

import adams.core.base.AbstractBaseString;

import java.sql.DriverManager;

/**
 * Encapsulates a JDBC URL Performs some minimal checks.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JdbcUrl
  extends AbstractBaseString {

  private static final long serialVersionUID = 6676290784059523594L;

  public static final String DEFAULT_URL = "jdbc:mysql://somehost:3306/somedatabase";

  /**
   * Initializes with a default URL.
   */
  public JdbcUrl() {
    this(DEFAULT_URL);
  }

  /**
   * Initializes the object with the URL to parse.
   *
   * @param s		the URL to parse
   */
  public JdbcUrl(String s) {
    super(s);
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    String[]	parts;

    if ((value == null) || value.isEmpty())
      return false;

    parts = value.split(":");
    if (parts.length < 3)
      return false;

    // starts with 'jdbc'?
    if (!parts[0].equals("jdbc"))
      return false;

    // driver available?
    try {
      if (DriverManager.getDriver(value) == null)
	return false;
    }
    catch (Exception e) {
      return false;
    }

    return true;
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Format: 'jdbc:<sub-protocol>:<database url>'";
  }

  /**
   * Whether this object should have favorites support.
   *
   * @return		true if to support favorites
   */
  @Override
  public boolean hasFavoritesSupport() {
    return true;
  }
}
