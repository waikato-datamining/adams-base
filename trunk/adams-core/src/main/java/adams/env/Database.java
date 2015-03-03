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
 * Database.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.env;

import adams.db.AbstractDatabaseConnection;
import adams.db.FieldProvider;

/**
 * For environment-specific database stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Database {

  /**
   * Returns the Field provider instance if available.
   *
   * @param dbcon	the database context
   * @return		the Field provider or null if none available
   */
  public FieldProvider getFieldProvider(AbstractDatabaseConnection dbcon) {
    return null;
  }
}
