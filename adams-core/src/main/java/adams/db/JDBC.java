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
 * JDBC.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.db;

/**
 * Utility class for JDBC.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JDBC {

  /** the expression to match a MySQL JDBC URL. */
  public final static String URL_MYSQL = "jdbc:mysql:.*";

  /** the expression to match a PostgreSQL JDBC URL. */
  public final static String URL_POSTGRESQL = "jdbc:postgresql:.*";

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
}
