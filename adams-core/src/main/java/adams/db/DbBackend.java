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
 * DbBackend.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.db;

/**
 * Interface for classes that return actual implementations of the
 * processing database interfaces.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface DbBackend
  extends BackendManager {

  /** the properties file containing the setup. */
  public final static String FILENAME = "DbBackend.props";

  /**
   * Returns whether this connection is supported.
   *
   * @param conn	the database connection
   * @return		true if supported
   */
  public boolean isSupported(AbstractDatabaseConnection conn);

  /**
   * Returns the generic SQL handler.
   *
   * @param conn	the database connection
   * @return		the handler
   */
  public SQLIntf getSQL(AbstractDatabaseConnection conn);

  /**
   * Returns the handler for the log table.
   *
   * @param conn	the database connection
   * @return		the handler
   */
  public LogIntf getLog(AbstractDatabaseConnection conn);
}
