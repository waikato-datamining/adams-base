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
 * AbstractDatabaseScriptlet.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import adams.db.AbstractDatabaseConnection;

/**
 * Ancestor for scriptlets that need access to the database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDatabaseScriptlet
  extends AbstractScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = -7051048031205784713L;

  /**
   * Returns the database connection instance.
   *
   * @return		the connection object
   */
  protected AbstractDatabaseConnection getDatabaseConnection() {
    return getOwner().getDatabaseConnection();
  }
}
