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
 * QueriesMSSQL.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.db.queries;

import adams.db.JDBC;
import adams.db.SQLUtils;

/**
 * MSSQL Server queries.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class QueriesMSSQL
  extends AbstractDatabaseQueries {

  private static final long serialVersionUID = -3757183677140056289L;

  /**
   * Returns the keyword for regular expression matching in queries.
   *
   * @return		the keyword
   */
  @Override
  public String regexpKeyword() {
    return "REGEXP_LIKE";
  }

  /**
   * Generates a regexp expression from the column and regular expression.
   *
   * @return		the expression
   */
  public String regexp(String col, String expr) {
    return col + " " + regexpKeyword() + SQLUtils.backquote(expr);
  }

  /**
   * Checks whether this URL is handled.
   *
   * @param url the URL to check
   * @return true if handled by this type class
   */
  @Override
  public boolean handles(String url) {
    return JDBC.isMSSQL(url);
  }
}
