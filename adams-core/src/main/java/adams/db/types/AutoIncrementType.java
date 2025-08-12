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
 * AutoIncrementType.java
 * Copyright (C) 2008-2025 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db.types;

import adams.db.AbstractDatabaseConnection;

import java.sql.Types;

/**
 * Autoincrement SQL type
 * 
 * @author dale
 */
public class AutoIncrementType
  extends ColumnType {

  /**
   * Constructor: it's by default a BIGINT, but may change based on database connection.
   *
   */
  public AutoIncrementType() {
    super(Types.BIGINT);
  }

  /**
   * Return creation string.
   */
  public String getCreateType(AbstractDatabaseConnection conn) {
    return AbstractTypes.getHandler(conn.getURL()).getAutoIncrementCreateType(m_Type);
  }
}
