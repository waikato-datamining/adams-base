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
 * QuirksMySQL.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.db.quirks;

import adams.db.JDBC;
import adams.db.SQLIntf;

/**
 * MySQL quirks.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class QuirksMySQL
  extends AbstractDatabaseQuirks {

  private static final long serialVersionUID = -3757183677140056289L;

  /**
   * Whether the {@link SQLIntf#tableExists(String)} method checks the catalog as well.
   *
   * @return true if to check
   */
  @Override
  public boolean tableExistsChecksCatalog() {
    return true;
  }

  /**
   * Checks whether this URL is handled.
   *
   * @param url the URL to check
   * @return true if handled by this type class
   */
  @Override
  public boolean handles(String url) {
    return JDBC.isMySQL(url);
  }
}
