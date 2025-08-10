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
 * TypesH2.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.db.types;

import adams.core.Constants;
import adams.db.JDBC;

import java.sql.Types;

/**
 * Column types for H2.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TypesH2
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
	return "TINYINT";

      case Types.SMALLINT:
	return "SMALLINT";

      case Types.INTEGER:
	return "INTEGER";

      case Types.BIGINT:
	return "BIGINT";

      case Types.BOOLEAN:
	return "BOOLEAN";

      case Types.TIMESTAMP:
	if (!compare)
	  return "TIMESTAMP" + (size != -1 ? "(" + size + ")" : "") + " NOT NULL DEFAULT '" + Constants.TIMESTAMP_DEFAULT_MYSQL + "'";
	else
	  return "TIMESTAMP";

      case Types.DATE:
	return "DATE";

      case Types.TIME:
	if (size == -1)
	  return "TIME";
	else
	  return "TIME(" + size + ")";

      case Types.DOUBLE:
	return "DOUBLE PRECISION";

      case Types.FLOAT:
	return "FLOAT(" + size + ")";

      case Types.REAL:
	return "REAL";

      case Types.LONGVARCHAR:
	return "CHARACTER LARGE OBJECT";

      case Types.VARCHAR:
	return "CHARACTER VARYING";

      case Types.LONGVARBINARY:
      case Types.BLOB:
	return "BLOB";

      default:
	throw new IllegalStateException("No TYPE for " + type);
    }
  }

  /**
   * Returns the keyword for regular expression matching in queries.
   *
   * @return		the keyword
   */
  public String regexpKeyword() {
    return "REGEXP";
  }

  /**
   * Checks whether this URL is handled.
   *
   * @param url		the URL to check
   * @return		true if handled by this type class
   */
  public boolean handles(String url) {
    return JDBC.isH2(url);
  }
}
