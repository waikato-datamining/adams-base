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
 * AbstractQuirks.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.db.quirks;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.db.SQLIntf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ancestor for SQL quirks classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDatabaseQuirks
  implements Serializable {

  private static final long serialVersionUID = -1376610984946740944L;

  /** for caching types on URLs. */
  protected static Map<String, AbstractDatabaseQuirks> m_Cache;

  /** the available types. */
  protected static List<AbstractDatabaseQuirks> m_Types;

  /**
   * Whether the {@link SQLIntf#tableExists(String)} method checks the catalog as well.
   *
   * @return		true if to check
   */
  public abstract boolean tableExistsChecksCatalog();

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
   * @return		the handler
   * @throws IllegalArgumentException	if JDBC connection type not supported
   */
  public static synchronized AbstractDatabaseQuirks getHandler(String url) {
    if (m_Cache == null) {
      m_Cache = new HashMap<>();
      m_Types = new ArrayList<>();
      for (Class cls: ClassLister.getSingleton().getClasses(AbstractDatabaseQuirks.class)) {
        try {
          m_Types.add((AbstractDatabaseQuirks) cls.getDeclaredConstructor().newInstance());
	}
	catch (Exception e) {
          System.err.println("Failed to instantiate quirks class: " + Utils.classToString(cls));
	}
      }
    }

    if (!m_Cache.containsKey(url)) {
      for (AbstractDatabaseQuirks types: m_Types) {
        if (types.handles(url)) {
	  m_Cache.put(url, types);
	  break;
	}
      }
    }

    if (!m_Cache.containsKey(url))
      throw new IllegalStateException("Unsupported JDBC connection: " + url);

    return m_Cache.get(url);
  }
}
