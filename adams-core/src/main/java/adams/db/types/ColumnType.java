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
 * ColumnType.java
 * Copyright (C) 2008-2017 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db.types;

import adams.db.AbstractDatabaseConnection;
import adams.db.JDBC;

import java.sql.Types;

/**
 * SQL column type.
 *
 * @author dale
 */
public class ColumnType {

  /** java sql type. */
  protected int m_type;

  /** size of type. */
  protected int m_size;

  /**
   * Constructor.
   *
   * @param type	java sql type
   */
  public ColumnType(int type) {
    m_type = type;
    m_size = -1;
  }

  /**
   * Constructor.
   *
   * @param type	java sql type
   * @param size	size  e.g for varchar
   */
  public ColumnType(int type, int size) {
    this(type);

    switch (type) {
      case Types.VARCHAR:
      case Types.LONGVARCHAR:
        m_size = size;
        break;

      case Types.TIME:
      case Types.TIMESTAMP:
        if ((size == 3) || (size == 6) || (size == 9))
        m_size = size;
        break;
    }
  }

  /**
   * Get a string representation of this type for comparison purposes.
   *
   * @return 		string representation of this type
   */
  public String getCompareType(AbstractDatabaseConnection conn) {
    return JDBC.getTypes(conn).toTypeString(m_type, m_size, true);
  }

  /**
   * True if given type if equivalent to this object.
   *
   * @param type		sql type
   * @return		equivalent?
   */
  public boolean equivalentTo(AbstractDatabaseConnection conn, ColumnType type) {
    return getCompareType(conn).equals(type.getCompareType(conn));
  }

  /**
   * Get String for creating this type.
   *
   * @return		creation string
   */
  public String getCreateType(AbstractDatabaseConnection conn) {
    return JDBC.getTypes(conn).toTypeString(m_type, m_size, false);
  }

  /**
   * toString.
   *
   * @return 		string representation
   */
  public String toString() {
    return m_type + ": " + JDBC.TYPES_MYSQL.toTypeString(m_type, m_size, false);
  }
}
