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
 * AbstractTypeMapper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet.sql;

import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Cell.ContentType;

import java.sql.Types;

/**
 * Ancestor for type mappers (spreadsheet to/from SQL).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTypeMapper
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 8949226268588950842L;

  /**
   * Determines the {@link ContentType} from the SQL column type.
   * See {@link Types}.
   *
   * @param colType	the SQL column type to interpret
   * @return		the type, default is {@link ContentType#STRING}
   */
  public abstract ContentType sqlTypeToContentType(int colType);

  /**
   * Returns the SQL type corresponding to the cell content type.
   *
   * @param contentType	the type to convert
   * @return		the associated SQL type
   * @see		Types
   */
  public abstract int contentTypeToSqlType(ContentType contentType);
}
