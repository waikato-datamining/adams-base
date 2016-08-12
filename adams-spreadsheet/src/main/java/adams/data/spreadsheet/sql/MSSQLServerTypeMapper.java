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
 * MSSQLServerTypeMapper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet.sql;

import adams.data.spreadsheet.Cell.ContentType;

/**
 <!-- globalinfo-start -->
 * MS SQL Server type mapper.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MSSQLServerTypeMapper
  extends DefaultTypeMapper {

  private static final long serialVersionUID = -4158991121487239476L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "MS SQL Server type mapper.";
  }

  /**
   * Returns the column type used for create table statements.
   *
   * @param contentType the type to get the type for
   * @param stringType  the default string type
   * @return		the associated create string
   */
  public String contentTypeToSqlCreateType(ContentType contentType, String stringType) {
    switch (contentType) {
      case LONG:
	return "INTEGER";
      case DOUBLE:
	return "DOUBLE PRECISION";
      case DATE:
	return "DATE";
      case DATETIME:
      case DATETIMEMSEC:
	return "DATETIME";
      case TIME:
      case TIMEMSEC:
	return "TIME";
      case BOOLEAN:
	return "BOOLEAN";
      default:
	return stringType;
    }
  }
}
