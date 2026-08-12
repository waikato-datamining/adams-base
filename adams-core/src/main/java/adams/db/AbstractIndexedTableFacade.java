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
 * AbstractIndexedTableFacade.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.db;

/**
 * Ancestor for facades for indexed tables.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractIndexedTableFacade
  extends AbstractTableFacade
  implements IndexedTableInterface {

  private static final long serialVersionUID = 8295544107483769413L;

  /**
   * Constructor.
   *
   * @param dbcon     the database context to use
   * @param tableName the name of the table
   */
  public AbstractIndexedTableFacade(AbstractDatabaseConnection dbcon, String tableName) {
    super(dbcon, tableName);
  }
}
