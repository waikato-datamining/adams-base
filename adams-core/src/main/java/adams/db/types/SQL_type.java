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
 * SQL_type.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db.types;

import java.sql.Types;

/**
 * A MySQL SQL type.
 *
 * @author dale
 * @version $Revision$
 */
public class SQL_type {

  /** max size of varchar. */
  static public final int MAX_VARCHAR = 255;

  /** max size of TEXT. */
  static public final int MAX_TEXT = 65535;

  /** max size of MEDIUMTEXT. */
  static public final int MAX_MEDIUMTEXT = 16777215;

  /** java sql type. */
  protected int m_type = Types.NULL;

  /** size of type. */
  protected int m_size = -1; // unset

  /**
   * Constructor.
   *
   * @param sqlt	java sql type
   */
  public SQL_type(int sqlt) {
    m_type = sqlt;
  }

  /**
   * Constructor.
   *
   * @param sqlt	java sql type
   * @param size	size  e.g for varchar
   */
  public SQL_type(int sqlt, int size) {
    m_type = sqlt;

    if (sqlt == Types.VARCHAR || sqlt == Types.LONGVARCHAR)
      m_size = size;
  }

  /**
   * Get the size of type (bytes).
   *
   * @return		size
   */
  public int getSize() {
    //return m_size, or default
    if (m_size != -1) {
      return m_size;
    }

    switch (m_type) {
      case Types.BIGINT :
	return 20;

      case Types.SMALLINT:
	return 6;

      case Types.TIMESTAMP:
	return 14;

      case Types.VARCHAR:
	if (m_size== -1) {
	  return 255;
	}
	if (m_size <= MAX_VARCHAR) {
	  return m_size; //VARCHAR(m_size)
	} else if (m_size <= MAX_TEXT) {
	  return MAX_TEXT;
	} else if (m_size <= MAX_MEDIUMTEXT) {
	  return MAX_MEDIUMTEXT;//MEDIUMTEXT
	} else {
	  return MAX_MEDIUMTEXT+1;
	}

      case Types.LONGVARCHAR:
	if (m_size == -1) {
	  return(MAX_MEDIUMTEXT); // MEDIUM
	}
	if (m_size <= MAX_VARCHAR) {
	  return m_size; //VARCHAR(m_size)
	} else if (m_size <= MAX_TEXT) {
	  return MAX_TEXT;
	} else if (m_size <= MAX_MEDIUMTEXT) {
	  return MAX_MEDIUMTEXT; //MEDIUMTEXT
	} else {
	  return MAX_MEDIUMTEXT+1;
	}

      default:
	return -1;
    }
  }

  /**
   * Get a string representation of this type for comparison purposes.
   *
   * @return 		string representation of this type
   */
  public String getCompareType() {
    switch (m_type) {
      case Types.BIGINT :
	return "BIGINT";

      case Types.BLOB :
	return "BLOB";

      case Types.BIT:
      case Types.BOOLEAN :
      case Types.TINYINT:
	return "TINYINT";

      case Types.DOUBLE:
	return "DOUBLE";

      case Types.FLOAT:
      case Types.REAL:
	return "FLOAT";

      case Types.SMALLINT:
	return "SMALLINT("+getSize()+")";

      case Types.INTEGER:
	return "INTEGER";

      case Types.LONGVARCHAR:
      case Types.VARCHAR:
	int s = getSize();
	if (s <= MAX_VARCHAR) {
	  return "VARCHAR("+s+")";
	} else if (s <= MAX_TEXT) {
	  return "TEXT";
	} else if (s <= MAX_MEDIUMTEXT) {
	  return "MEDIUMTEXT";
	} else {
	  return "LONGTEXT";
	}

      case Types.TIMESTAMP:
	return "TIMESTAMP";

      case Types.DATE:
	return "DATE";

      case Types.TIME:
	return "TIME";

      case Types.LONGVARBINARY:
	return "LONG VARBINARY";

      default:
	throw new IllegalStateException("No TYPE for " + m_type);
    }
  }

  /**
   * True if given type if equivalent to this object.
   *
   * @param sqt		sql type
   * @return		equivalent?
   */
  public boolean equivalentTo(SQL_type sqt) {
    return getCompareType().equals(sqt.getCompareType());
  }

  /**
   * Get String for creating this type.
   *
   * @return		creation string
   */
  public String getCreateType() {
    String	result;

    result = getCompareType();

    if (m_type == java.sql.Types.TIMESTAMP)
      result += " NOT NULL DEFAULT '0000-00-00 00:00:00'";

    return result;
  }

  /**
   * toString.
   *
   * @return 		string representation
   */
  public String toString() {
    return m_type + ": " + getCreateType();
  }
}
