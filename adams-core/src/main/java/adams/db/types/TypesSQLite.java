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
 * TypesSQLite.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.db.types;

import java.sql.Types;

/**
 * Column types for SQLite.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TypesSQLite
  extends AbstractTypes {

  private static final long serialVersionUID = 7842428772313177661L;

  /**
   * Get a string representation of this type for comparison or create purposes.
   *
   * @param type	the type
   * @param size	the size
   * @param compare	if true then a string for comparison is returned,
   *                    otherwise for creation
   * @return 		string representation of this type
   */
  public String toTypeString(int type, int size, boolean compare) {
    switch (type) {
      case Types.BIT:
      case Types.TINYINT:
      case Types.SMALLINT:
      case Types.INTEGER:
      case Types.BIGINT:
	return "INTEGER";

      case Types.BOOLEAN:
      case Types.TIMESTAMP:
      case Types.DATE:
      case Types.TIME:
	return "NUMERIC";

      case Types.DOUBLE:
      case Types.FLOAT:
      case Types.REAL:
	return "REAL";

      case Types.LONGVARCHAR:
      case Types.VARCHAR:
	return "TEXT";

      case Types.LONGVARBINARY:
      case Types.BLOB:
	return "BLOB";

      default:
	throw new IllegalStateException("No TYPE for " + type);
    }
  }
}
