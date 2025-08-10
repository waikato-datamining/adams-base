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
 * JDBC.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.db;

import adams.db.queries.AbstractDatabaseQueries;
import adams.db.quirks.AbstractDatabaseQuirks;
import adams.db.types.AbstractTypes;

/**
 * Utility class for JDBC.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class JDBC {

  /** the expression to match a MySQL JDBC URL. */
  public final static String URL_MYSQL = "jdbc:mysql:.*";

  /** the expression to match a PostgreSQL JDBC URL. */
  public final static String URL_POSTGRESQL = "jdbc:postgresql:.*";

  /** the expression to match a SQLite JDBC URL. */
  public final static String URL_SQLITE = "jdbc:sqlite:.*";

  /** the expression to match a HSQLDB JDBC URL. */
  public final static String URL_HSQLDB = "jdbc:hsqldb:.*";

  /** the expression to match a H2 JDBC URL. */
  public final static String URL_H2 = "jdbc:h2:.*";

  /**
   * Checks whether this JDBC url represents a MySQL URL.
   *
   * @param url		the URL to check
   * @return		true if MySQL URL
   * @see		#URL_MYSQL
   */
  public static boolean isMySQL(String url) {
    return url.matches(URL_MYSQL);
  }

  /**
   * Checks whether this connection represents a MySQL one.
   *
   * @param conn	the connection to check
   * @return		true if MySQL URL
   * @see		#isMySQL(String)
   */
  public static boolean isMySQL(AbstractDatabaseConnection conn) {
    return isMySQL(conn.getURL());
  }

  /**
   * Checks whether this JDBC url represents a PostgreSQL URL.
   *
   * @param url		the URL to check
   * @return		true if PostgreSQL URL
   * @see		#URL_POSTGRESQL
   */
  public static boolean isPostgreSQL(String url) {
    return url.matches(URL_POSTGRESQL);
  }

  /**
   * Checks whether this connection represents a PostgreSQL one.
   *
   * @param conn	the connection to check
   * @return		true if PostgreSQL URL
   * @see		#isPostgreSQL(String)
   */
  public static boolean isPostgreSQL(AbstractDatabaseConnection conn) {
    return isPostgreSQL(conn.getURL());
  }

  /**
   * Checks whether this JDBC url represents a SQLite URL.
   *
   * @param url		the URL to check
   * @return		true if SQLite URL
   * @see		#URL_SQLITE
   */
  public static boolean isSQLite(String url) {
    return url.matches(URL_SQLITE);
  }

  /**
   * Checks whether this connection represents a SQLite one.
   *
   * @param conn	the connection to check
   * @return		true if SQLite URL
   * @see		#isSQLite(String)
   */
  public static boolean isSQLite(AbstractDatabaseConnection conn) {
    return isSQLite(conn.getURL());
  }

  /**
   * Checks whether this JDBC url represents a H2 URL.
   *
   * @param url		the URL to check
   * @return		true if H2 URL
   * @see		#URL_H2
   */
  public static boolean isH2(String url) {
    return url.matches(URL_H2);
  }

  /**
   * Checks whether this connection represents a H2 one.
   *
   * @param conn	the connection to check
   * @return		true if h2 URL
   * @see		#isH2(String)
   */
  public static boolean isH2(AbstractDatabaseConnection conn) {
    return isH2(conn.getURL());
  }

  /**
   * Checks whether this JDBC url represents a HSQLDB URL.
   *
   * @param url		the URL to check
   * @return		true if HSQLDB URL
   * @see		#URL_HSQLDB
   */
  public static boolean isHSQLDB(String url) {
    return url.matches(URL_HSQLDB);
  }

  /**
   * Checks whether this connection represents a HSQLDB one.
   *
   * @param conn	the connection to check
   * @return		true if HSQLDB URL
   * @see		#isHSQLDB(String)
   */
  public static boolean isHSQLDB(AbstractDatabaseConnection conn) {
    return isHSQLDB(conn.getURL());
  }

  /**
   * Returns the appropriate types.
   *
   * @param conn	the connection to use for identification
   * @return		the types
   * @throws IllegalArgumentException	if JDBC connection type not supported
   */
  public static AbstractTypes getTypes(AbstractDatabaseConnection conn) {
    return getTypes(conn.getURL());
  }

  /**
   * Returns the appropriate types.
   *
   * @param url		the URL to use for identification
   * @return		the types
   * @throws IllegalArgumentException	if JDBC connection type not supported
   */
  public static AbstractTypes getTypes(String url) {
    return AbstractTypes.getHandler(url);
  }

  /**
   * Returns the appropriate quirks.
   *
   * @param conn	the connection to use for identification
   * @return		the quirks
   * @throws IllegalArgumentException	if JDBC connection type not supported
   */
  public static AbstractDatabaseQuirks getQuirks(AbstractDatabaseConnection conn) {
    return getQuirks(conn.getURL());
  }

  /**
   * Returns the appropriate quirks.
   *
   * @param url		the URL to use for identification
   * @return		the quirks
   * @throws IllegalArgumentException	if JDBC connection type not supported
   */
  public static AbstractDatabaseQuirks getQuirks(String url) {
    return AbstractDatabaseQuirks.getHandler(url);
  }

  /**
   * Returns the appropriate queries helper.
   *
   * @param conn	the connection to use for identification
   * @return		the queries helper
   * @throws IllegalArgumentException	if JDBC connection type not supported
   */
  public static AbstractDatabaseQueries getQueries(AbstractDatabaseConnection conn) {
    return getQueries(conn.getURL());
  }

  /**
   * Returns the appropriate queries helper.
   *
   * @param url		the URL to use for identification
   * @return		the queries helper
   * @throws IllegalArgumentException	if JDBC connection type not supported
   */
  public static AbstractDatabaseQueries getQueries(String url) {
    return AbstractDatabaseQueries.getHandler(url);
  }
}
