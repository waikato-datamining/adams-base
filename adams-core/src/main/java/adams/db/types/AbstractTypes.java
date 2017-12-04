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
 * ColumnTypeMySQL.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.db.types;

import java.io.Serializable;
import java.sql.Types;

/**
 * Ancestor for SQL types classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractTypes
  implements Serializable {

  private static final long serialVersionUID = -1376610984946740944L;

  /** max size of varchar. */
  static public final int MAX_VARCHAR = 255;

  /** max size of TEXT. */
  static public final int MAX_TEXT = 65535;

  /** max size of MEDIUMTEXT. */
  static public final int MAX_MEDIUMTEXT = 16777215;

  /**
   * Get a string representation of this type for comparison or create purposes.
   *
   * @param type	the type
   * @param size	the size
   * @param compare	if true then a string for comparison is returned,
   *                    otherwise for creation
   * @return 		string representation of this type
   */
  public abstract String toTypeString(int type, int size, boolean compare);

  /**
   * Get the actual size of type.
   *
   * @return		size
   */
  public int actualSize(int type, int size) {
    //return m_size, or default
    if (size != -1) {
      return size;
    }

    switch (type) {
      case Types.BIGINT :
        return 20;

      case Types.SMALLINT:
        return 6;

      case Types.VARCHAR:
        if (size == -1) {
          return 255;
        }
        if (size <= MAX_VARCHAR) {
          return size; //VARCHAR(m_size)
        } else if (size <= MAX_TEXT) {
          return MAX_TEXT;
        } else if (size <= MAX_MEDIUMTEXT) {
          return MAX_MEDIUMTEXT;//MEDIUMTEXT
        } else {
          return MAX_MEDIUMTEXT+1;
        }

      case Types.LONGVARCHAR:
        if (size == -1) {
          return(MAX_MEDIUMTEXT); // MEDIUM
        }
        if (size <= MAX_VARCHAR) {
          return size; //VARCHAR(m_size)
        } else if (size <= MAX_TEXT) {
          return MAX_TEXT;
        } else if (size <= MAX_MEDIUMTEXT) {
          return MAX_MEDIUMTEXT; //MEDIUMTEXT
        } else {
          return MAX_MEDIUMTEXT+1;
        }

      default:
        return -1;
    }
  }
}
