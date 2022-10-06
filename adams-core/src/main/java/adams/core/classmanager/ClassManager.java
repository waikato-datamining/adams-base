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
 * ClassManager.java
 * Copyright (C) 2020-2022 University of Waikato, Hamilton, NZ
 */

package adams.core.classmanager;

import adams.core.ClassLister;
import adams.core.logging.CustomLoggingLevelObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Manages the loading of classes, delegates it to the responsible actual manager.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ClassManager
    extends CustomLoggingLevelObject {

  private static final long serialVersionUID = -25229737422104934L;

  /**
   * Dummy manager to be used in mapping if no manager was found.
   */
  public static class NoManagerFound
      extends CustomLoggingLevelObject
      implements CustomClassManager {

    private static final long serialVersionUID = 3626028390421240241L;

    @Override
    public Class forName(String classname) throws Exception {
      throw new ClassNotFoundException(classname);
    }

    @Override
    public Object deepCopy(Object o, boolean silent) {
      return null;
    }
  }

  /** the class managers. */
  protected List<CustomClassManager> m_Managers;

  /** the default manager. */
  protected DefaultClassManager m_DefaultManager;

  /** the "no manager found" dummy. */
  protected NoManagerFound m_NoManagerFound;

  /** the relation between classname and manager. */
  protected Map<String,CustomClassManager> m_Mapping;

  /** the singleton. */
  protected static ClassManager m_Singleton;

  /**
   * Initializes the manager.
   */
  protected ClassManager() {
    super();
    initialize();
  }

  /**
   * Configures the actual managers and data structures.
   */
  protected void initialize() {
    m_Mapping        = new HashMap<>();
    m_Managers       = new ArrayList<>();
    m_DefaultManager = new DefaultClassManager();
    m_NoManagerFound = new NoManagerFound();
    for (Class cls: ClassLister.getSingleton().getClasses(CustomClassManager.class)) {
      if (cls.equals(DefaultClassManager.class))
	continue;
      try {
	m_Managers.add((CustomClassManager) cls.newInstance());
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to instantiate: " + cls.getName(), e);
      }
    }
  }

  /**
   * Determines the manager to use for the class.
   *
   * @param classname	the class to get the manager for
   * @return		the manager
   */
  protected synchronized CustomClassManager determineManager(String classname) {
    CustomClassManager result;

    result = m_Mapping.get(classname);

    if (result == null) {
      // find a manager
      for (CustomClassManager m: m_Managers) {
	try {
	  m.forName(classname);
	  result = m;
	  break;
	}
	catch (Exception e) {
	  // ignored
	}
      }

      // try default one
      if (result == null) {
	try {
	  m_DefaultManager.forName(classname);
	  result = m_DefaultManager;
	}
	catch (Exception e) {
	  // ignored
	}
      }

      // cannot be instantiated
      if (result == null)
	m_Mapping.put(classname, m_NoManagerFound);
      else
	m_Mapping.put(classname, result);
    }

    return m_Mapping.get(classname);
  }

  /**
   * Obtains the Class for the name.
   *
   * @param classname 	the class to load
   * @return		the class
   * @throws Exception	if loading failed
   */
  public synchronized Class forName(String classname) throws Exception {
    return determineManager(classname).forName(classname);
  }

  /**
   * Checks whether the class can be loaded.
   *
   * @param classname	the class name to check
   * @return		true if the class exists
   */
  public synchronized boolean isAvailable(String classname) {
    try {
      forName(classname);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Creates a deep copy of the given object (must be serializable!). Returns
   * null in case of an error.
   *
   * @param o		the object to copy
   * @return		the deep copy
   */
  public Object deepCopy(Object o) {
    return deepCopy(o, false);
  }

  /**
   * Creates a deep copy of the given object (must be serializable!). Returns
   * null in case of an error.
   *
   * @param o		the object to copy
   * @param silent	whether to suppress error output
   * @return		the deep copy
   */
  public Object deepCopy(Object o, boolean silent) {
    return determineManager(o.getClass().getName()).deepCopy(o, silent);
  }

  /**
   * Returns counts per class manager.
   *
   * @return		the statistics
   */
  public synchronized Map<String,Integer> statistics() {
    Map<String,Integer>	result;
    String		key;

    result = new HashMap<>();
    result.put(DefaultClassManager.class.getName(), 0);
    result.put(NoManagerFound.class.getName(), 0);
    for (CustomClassManager manager: m_Managers)
      result.put(manager.getClass().getName(), 0);

    for (String classname: m_Mapping.keySet()) {
      key = m_Mapping.get(classname).getClass().getName();
      result.put(key, result.get(key) + 1);
    }

    return result;
  }

  /**
   * Returns the singleton of the class manager.
   *
   * @return		the singleton
   */
  public static synchronized ClassManager getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new ClassManager();
    return m_Singleton;
  }
}
