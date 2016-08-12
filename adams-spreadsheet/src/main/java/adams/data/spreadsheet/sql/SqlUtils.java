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

/**
 * SqlUtils.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.sql;

import adams.data.spreadsheet.Cell.ContentType;

import java.sql.Types;

/**
 * Helper class for SQL related functionality for spreadsheets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SqlUtils {

  /**
   * Determines the {@link ContentType} from the SQL column type.
   * See {@link Types}.
   * 
   * @param colType	the SQL column type to interpret
   * @return		the type, default is {@link ContentType#STRING}
   */
  public static ContentType sqlTypeToContentType(int colType) {
    switch (colType) {
      case Types.TIME:
	return ContentType.TIME;
      case Types.DATE:
	return ContentType.DATE;
      case Types.TIMESTAMP:
	return ContentType.DATETIMEMSEC;
      case Types.INTEGER:
	return ContentType.LONG;
      case Types.BIGINT:
	return ContentType.LONG;
      case Types.FLOAT:
	return ContentType.DOUBLE;
      case Types.DOUBLE:
	return ContentType.DOUBLE;
      default:
	return ContentType.STRING;
    }
  }
  
  /**
   * Returns the SQL type corresponding to the cell content type.
   * 
   * @param contentType	the type to convert
   * @return		the associated SQL type
   * @see		Types
   */
  public static int contentTypeToSqlType(ContentType contentType) {
    switch (contentType) {
      case DATE:
	return Types.DATE;
      case DATETIME:
	return Types.TIMESTAMP;
      case DATETIMEMSEC:
	return Types.TIMESTAMP;
      case TIME:
	return Types.TIME;
      case TIMEMSEC:
	return Types.TIME;
      case DOUBLE:
	return Types.DOUBLE;
      case LONG:
	return Types.INTEGER;
      case BOOLEAN:
	return Types.BOOLEAN;
      default:
	return Types.VARCHAR;
    }
  }
}