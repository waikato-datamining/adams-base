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
 * JenericCmdline.java
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.Properties;
import adams.core.classmanager.ClassManager;
import adams.core.logging.LoggingObject;
import adams.env.Environment;
import adams.env.JenericCmdlineDefinition;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Manages what classes should be enhanced with with a generic commandline.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JenericCmdline
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -200830732847780663L;

  /** the props file with the static class name lists. */
  public final static String FILENAME = "JenericCmdline.props";

  /** the singleton. */
  protected static JenericCmdline m_Singleton;

  /** the classes/interfaces to manage. */
  protected List<Class> m_Managed;

  /** the cache of classes that have been checked (Class - true|false). */
  protected Map<Class,Boolean> m_Cache;

  /**
   * Initializes the command-line handling.
   */
  protected JenericCmdline() {
    super();
    initialize();
  }

  /**
   * Initializes the caches.
   */
  protected void initialize() {
    Properties 		props;
    Class		cls;

    m_Managed = new ArrayList<>();
    m_Cache   = new HashMap<>();
    props     = Environment.getInstance().read(JenericCmdlineDefinition.KEY);
    for (String key: props.keySetAll()) {
      if (!props.getBoolean(key, false)) {
	continue;
      }
      try {
	cls = ClassManager.getSingleton().forName(key);
	m_Managed.add(cls);
	m_Cache.put(cls, true);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to instantiate class: " + key, e);
      }
    }
  }

  /**
   * Checks whether this class should be enhanced with a generic commandline.
   *
   * @param cls		the class to check
   * @return		true if to enhance
   */
  public boolean isHandled(Class cls) {
    Boolean	result;

    result = m_Cache.get(cls);
    if (result == null) {
      for (Class managed: m_Managed) {
	if (ClassLocator.isSubclass(managed, cls) || ClassLocator.hasInterface(managed, cls)) {
	  result = true;
	  break;
	}
      }

      if (result == null)
	result = false;

      m_Cache.put(cls, result);
    }

    return result;
  }

  /**
   * Returns the list of classes/interfaces that are managed.
   *
   * @return		the managed classes
   */
  public List<Class> getManaged() {
    List<Class>		result;

    result = new ArrayList<>();
    result.addAll(m_Managed);

    return result;
  }

  /**
   * Returns the singleton.
   *
   * @return		the singleton
   */
  public static synchronized JenericCmdline getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new JenericCmdline();
    return m_Singleton;
  }
}
