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
 * TypesMSSQL.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.db.types;

import adams.core.Constants;
import adams.db.JDBC;

import java.sql.Types;

/**
 * Column types for MSSQL.
 * Based on:
 * https://learn.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql?view=sql-server-ver17
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TypesMSSQL
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
    size = actualSize(type, size);

    switch (type) {
      case Types.BIT:
      case Types.BOOLEAN:
	return "BIT";

      case Types.TINYINT:
	return "TINYINT";

      case Types.BIGINT:
	return "BIGINT";

      case Types.SMALLINT:
	return "SMALLINT";

      case Types.INTEGER:
	return "INT";

      case Types.FLOAT:
	return "FLOAT";

      case Types.REAL:
	return "REAL";

      case Types.DOUBLE:
	return "NUMERIC";

      case Types.LONGVARCHAR:
      case Types.VARCHAR:
	if (size <= MAX_VARCHAR) {
	  return "VARCHAR(" + size + ")";
	}
	else{
	  return "NTEXT";  // ntext is unicode, text is non-unicode
	}

      case Types.TIMESTAMP:
        if (!compare)
	  return "DATETIME NOT NULL DEFAULT '" + getTimestampDefault() + "'";
	else
	  return "DATETIME";

      case Types.DATE:
	return "DATE";

      case Types.TIME:
	return "TIME";

      case Types.BLOB:
      case Types.LONGVARBINARY:
	return "VARBINARY";

      default:
	throw new IllegalStateException("No TYPE for " + type);
    }
  }

  /**
   * Returns the type definition for auto increment types.
   *
   * @param type	the preferred type
   * @return		the definition for creating the column
   */
  @Override
  public String getAutoIncrementCreateType(int type) {
    return toTypeString(type, -1, false) + " IDENTITY(1,1)";
  }

  /**
   * Whether a default is used for timestamps.
   *
   * @return		true if a default is used
   */
  @Override
  public boolean usesTimestampDefault() {
    return true;
  }

  /**
   * Returns the default used for timestamps.
   *
   * @return		the default, null if none used
   */
  @Override
  public String getTimestampDefault() {
    return Constants.TIMESTAMP_DEFAULT_MSSQL;
  }

  /**
   * Checks whether this URL is handled.
   *
   * @param url		the URL to check
   * @return		true if handled by this type class
   */
  public boolean handles(String url) {
    return JDBC.isMSSQL(url);
  }
}
