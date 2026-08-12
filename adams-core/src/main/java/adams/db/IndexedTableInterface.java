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
 * IndexedTableInterface.java
 * Copyright (C) 2026 University of Waikato, Hamilton, NZ
 */

package adams.db;

import adams.db.queries.AbstractDatabaseQueries;

/**
 * Ancestor for indexed table interfaces.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface IndexedTableInterface
  extends TableInterface {

  /**
   * Returns whether ANSI quotes are to be used around table/column names.
   *
   * @return		true if to be used
   */
  public boolean useAnsiQuotes();

  /**
   * Returns the column/table quoted if ANSI quotes are to be used.
   *
   * @param name	the table/column name to quote (if necessary)
   * @return		the potentially quoted name
   * @see		#useAnsiQuotes()
   */
  public String quoteName(String name);

  /**
   * Returns the underlying queries helper instance.
   *
   * @return		the instance, null if not available
   */
  public AbstractDatabaseQueries getQueries();
}
