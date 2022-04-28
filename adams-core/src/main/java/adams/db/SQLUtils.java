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
 * SQLUtils.java
 * Copyright (C) 2019-2022 University of Waikato, Hamilton, NZ
 */

package adams.db;

import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.logging.Level;

/**
 * Helper class for SQL related operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SQLUtils {

  /** the static logger. */
  protected static Logger LOGGER = LoggingHelper.getConsoleLogger(SQLUtils.class);

  /**
   * Checks whether the given column type is numeric.
   *
   * @param colType	the column type
   * @return		true if numeric
   */
  public static boolean isNumeric(int colType) {
    return
	   (colType == Types.BIGINT)
	|| (colType == Types.BIT)
	|| (colType == Types.DECIMAL)
	|| (colType == Types.DOUBLE)
	|| (colType == Types.FLOAT)
	|| (colType == Types.INTEGER)
	|| (colType == Types.NUMERIC)
	|| (colType == Types.REAL)
	|| (colType == Types.SMALLINT)
	|| (colType == Types.TINYINT)
	;
  }

  /**
   * Checks whether the given column type is an integer type.
   *
   * @param colType	the column type
   * @return		true if an integer type
   */
  public static boolean isInteger(int colType) {
    return
	   (colType == Types.BIGINT)
	|| (colType == Types.BIT)
	|| (colType == Types.INTEGER)
	|| (colType == Types.SMALLINT)
	|| (colType == Types.TINYINT)
	;
  }

  /**
   * Checks whether the given column type represents strings.
   *
   * @param colType	the column type
   * @return		true if a string type
   */
  public static boolean isString(int colType) {
    return
	   (colType == Types.CHAR)
	|| (colType == Types.CLOB)
	|| (colType == Types.LONGNVARCHAR)
	|| (colType == Types.LONGVARCHAR)
	|| (colType == Types.NCHAR)
	|| (colType == Types.NVARCHAR)
	|| (colType == Types.VARCHAR)
	;
  }

  /**
   * Checks whether the given column type represents a date-like type.
   *
   * @param colType	the column type
   * @return		true if a date-like type
   */
  public static boolean isDate(int colType) {
    return
	   (colType == Types.DATE)
	|| (colType == Types.TIME)
	|| (colType == Types.TIMESTAMP)
	;
  }

  /**
   * Close this statement to avoid memory leaks.
   *
   * @param s		the statement to close
   */
  public static void close(Statement s) {
    if (s != null) {
      try {
	s.close();
	s = null;
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE, "Error closing statement", e);
      }
    }
  }

  /**
   * Frees the memory associated with the blob object.
   *
   * @param blob	the blob to free up, ignored if null
   */
  public static void free(Blob blob) {
    if (blob != null) {
      try {
	blob.free();
      }
      catch (Exception e) {
	// ignored
      }
    }
  }

  /**
   * Frees the memory associated with the clob object.
   *
   * @param clob	the clob to free up, ignored if null
   */
  public static void free(Clob clob) {
    if (clob != null) {
      try {
	clob.free();
      }
      catch (Exception e) {
	// ignored
      }
    }
  }

  /**
   * Close objects related to this ResultSet. Important because some (most,all?) jdbc drivers
   * do not clean up after themselves, resulting in memory leaks.
   *
   * @param r  The ResultSet to clean up after
   */
  public static void closeAll(ResultSet r) {
    if (r != null) {
      try {
	Statement s = r.getStatement();
	r.close();
	close(s);
	s = null;
	r = null;
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE, "Error closing resultset", e);
      }
    }
  }

  /**
   * Close objects related to this ResultSet.
   *
   * @param r  The ResultSet to clean up after
   */
  public static void closeAll(SimpleResultSet r) {
    if (r != null) {
      try {
	r.close();
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE, "Error closing resultset/statement", e);
      }
    }
  }

  /**
   * MySQL boolean to tinyint.
   *
   * @param b	boolean
   * @return	tiny int value
   */
  public static int booleanToTinyInt(boolean b) {
    if (b) {
      return(1);
    } else {
      return(0);
    }
  }

  /**
   * MySQL tinyint to boolean.
   * @param i	tiny int
   * @return	boolean
   */
  public static boolean tinyIntToBoolean(int i) {
    if (i==0) {
      return(false);
    } else {
      return(true);
    }
  }

  /**
   * Backquotes the regular expression and ensures that it is surrounded by single
   * quotes.
   *
   * @param s		the regular expression to backquote and enclose
   * @return		the processed string
   */
  public static String backquote(BaseRegExp s) {
    return backquote(s.getValue());
  }

  /**
   * Backquotes the string and ensures that it is surrounded by single
   * quotes.
   *
   * @param s		the string to backquote and enclose
   * @return		the processed string
   */
  public static String backquote(String s) {
    String	result;

    result = Utils.backQuoteChars(s);
    if (!result.startsWith("'"))
      result = "'" + result + "'";

    return result;
  }

  /**
   * Determines the SQL column types used in the provided resultset.
   *
   * @param rs		the resultset to inspect
   * @return		the SQL column types
   * @see		Types
   * @throws SQLException        if querying the meta-data fails
   */
  public static int[] getColumnTypes(ResultSet rs) throws SQLException {
    return getColumnTypes(rs.getMetaData());
  }

  /**
   * Determines the SQL column types used in the provided resultset.
   *
   * @param rs		the metadata resultset to inspect
   * @return		the SQL column types
   * @see		Types
   * @throws SQLException	if querying the meta-data fails
   */
  public static int[] getColumnTypes(ResultSetMetaData rs) throws SQLException {
    int[]	result;
    int		i;

    result = new int[rs.getColumnCount()];
    for (i = 1; i <= rs.getColumnCount(); i++)
      result[i - 1] = rs.getColumnType(i);

    return result;
  }

  /**
   * Determines the SQL column names used in the provided resultset.
   *
   * @param rs		the resultset to inspect
   * @return		the SQL column names (or label if present)
   * @throws SQLException	if querying the meta-data fails
   */
  public static String[] getColumnNames(ResultSet rs) throws SQLException {
    return getColumnNames(rs.getMetaData());
  }

  /**
   * Determines the SQL column names used in the provided resultset.
   *
   * @param rs		the meta-data resultset to inspect
   * @return		the SQL column names (or label if present)
   * @throws SQLException	if querying the meta-data fails
   */
  public static String[] getColumnNames(ResultSetMetaData rs) throws SQLException {
    String[]	result;
    int		i;

    result = new String[rs.getColumnCount()];
    for (i = 1; i <= rs.getColumnCount(); i++)
      result[i - 1] = rs.getColumnLabel(i);

    return result;
  }
}
