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
 * AbstractDatabaseQueries.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.db.queries;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.db.SQLUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ancestor for SQL queries classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDatabaseQueries
  implements Serializable {

  private static final long serialVersionUID = -1376610984946740944L;

  /** for caching types on URLs. */
  protected static Map<String, AbstractDatabaseQueries> m_Cache;

  /** the available types. */
  protected static List<AbstractDatabaseQueries> m_Types;

  /**
   * Returns the keyword for regular expression matching in queries.
   *
   * @return		the keyword
   */
  public abstract String regexpKeyword();

  /**
   * Generates a regexp expression from the column and regular expression.
   *
   * @return		the expression
   */
  public String regexp(String col, BaseRegExp expr) {
    return regexp(col, expr.getValue());
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
   * Returns the keyword for limiting the number of rows.
   *
   * @return		the keyword
   */
  public abstract String limitKeyword();

  /**
   * Generates the row limiting expression.
   *
   * @param max		the maximum number of rows to return
   * @return		the expression
   */
  public String limit(int max) {
    return limitKeyword() + " " + max;
  }

  /**
   * Checks whether this URL is handled.
   *
   * @param url		the URL to check
   * @return		true if handled by this type class
   */
  public abstract boolean handles(String url);

  /**
   * Returns the handler for the JDBC url.
   *
   * @param url		the URL
   * @return		the handler, null if no support available
   */
  public static synchronized AbstractDatabaseQueries getHandler(String url) {
    if (m_Cache == null) {
      m_Cache = new HashMap<>();
      m_Types = new ArrayList<>();
      for (Class cls: ClassLister.getSingleton().getClasses(AbstractDatabaseQueries.class)) {
        try {
          m_Types.add((AbstractDatabaseQueries) cls.getDeclaredConstructor().newInstance());
	}
	catch (Exception e) {
          System.err.println("Failed to instantiate queries class: " + Utils.classToString(cls));
	}
      }
    }

    if (!m_Cache.containsKey(url)) {
      for (AbstractDatabaseQueries types: m_Types) {
        if (types.handles(url)) {
	  m_Cache.put(url, types);
	  break;
	}
      }
    }

    if (!m_Cache.containsKey(url))
      return null;

    return m_Cache.get(url);
  }
}
