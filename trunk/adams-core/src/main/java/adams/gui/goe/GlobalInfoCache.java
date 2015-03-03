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
 * GlobalInfoCache.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * For caching the global info of classes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GlobalInfoCache {

  /** the globalInfo method. */
  public final static String METHOD_GLOBALINFO = "globalInfo";

  /** the classname &lt;-&gt; global info relation. */
  protected Hashtable<String,String> m_GlobalInfo;

  /** the classname &lt;-&gt; global info available relation. */
  protected Hashtable<String,Boolean> m_Available;

  /** the singleton. */
  protected static GlobalInfoCache m_Singleton;

  /**
   * Initializes the cache.
   */
  private GlobalInfoCache() {
    super();

    m_GlobalInfo = new Hashtable<String,String>();
    m_Available  = new Hashtable<String,Boolean>();
  }

  /**
   * Checks whether the class offers a global info.
   *
   * @param cls		the class to check
   * @return		true if the class offers global info
   */
  public synchronized boolean has(Class cls) {
    return has(cls.getName());
  }

  /**
   * Checks whether the class offers a global info.
   *
   * @param clsname	the class to check
   * @return		true if the class offers global info
   */
  public synchronized boolean has(String clsname) {
    String	info;
    Class	cls;
    Object	obj;
    Method	method;

    if (!m_Available.containsKey(clsname)) {
      try {
	cls    = Class.forName(clsname);
	method = cls.getMethod(METHOD_GLOBALINFO, new Class[0]);
	obj    = cls.newInstance();
	info   = (String) method.invoke(obj, new Object[0]);
      }
      catch (Exception e) {
	info = null;
      }

      m_Available.put(clsname, (info != null));
      if (info != null)
	m_GlobalInfo.put(clsname, info);
    }

    return m_Available.get(clsname);
  }

  /**
   * Returns the global info for the specified class.
   *
   * @param cls		the class to get the info for
   * @return		the info, null if not available
   */
  public synchronized String get(Class cls) {
    return get(cls.getName());
  }

  /**
   * Returns the global info for the specified class.
   *
   * @param clsname	the class to check
   * @return		the info, null if not available
   */
  public synchronized String get(String clsname) {
    if (has(clsname))
      return m_GlobalInfo.get(clsname);
    else
      return null;
  }

  /**
   * Returns the singleton instance.
   *
   * @return		the singleton
   */
  public static synchronized GlobalInfoCache getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new GlobalInfoCache();

    return m_Singleton;
  }
}
