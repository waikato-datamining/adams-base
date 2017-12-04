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
 * TypesPostgreSQL.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.db.types;

import java.sql.Types;

/**
 * Column types for PostgreSQL.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TypesPostgreSQL
  extends AbstractTypes {

  private static final long serialVersionUID = -4264141965314359770L;

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
    // clean up size
    switch (type) {
      case Types.TIME:
      case Types.TIMESTAMP:
        if (compare)
          size = -1;
        else if ((size != 3) && (size != 6) && (size != 0))
          size = -1;
    }

    size = actualSize(type, size);

    switch (type) {
      case Types.BIT:
      case Types.BOOLEAN:
      case Types.TINYINT:
	return "BOOLEAN";

      case Types.BIGINT:
	return "BIGINT";

      case Types.SMALLINT:
	return "SMALLINT";

      case Types.INTEGER:
	return "INTEGER";

      case Types.FLOAT:
      case Types.REAL:
	return "REAL";

      case Types.DOUBLE:
	return "DOUBLE PRECISION";

      case Types.LONGVARCHAR:
      case Types.VARCHAR:
	if (size <= MAX_VARCHAR) {
	  return "VARCHAR(" + size + ")";
	}
	else{
	  return "TEXT";
	}

      case Types.TIMESTAMP:
        if (!compare)
	  return "TIMESTAMP" + (size != -1 ? "(" + size + ")" : "") + " NOT NULL DEFAULT '0000-00-00 00:00:00'";
	else
	  return "TIMESTAMP";

      case Types.DATE:
	return "DATE";

      case Types.TIME:
	if (size == -1)
	  return "TIME";
	else
	  return "TIME(" + size + ")";

      case Types.BLOB:
      case Types.LONGVARBINARY:
	return "BYTEA";

      default:
	throw new IllegalStateException("No TYPE for " + type);
    }
  }
}
