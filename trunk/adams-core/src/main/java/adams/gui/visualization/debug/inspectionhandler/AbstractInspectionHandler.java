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
 * AbstractInspectionHandler.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.inspectionhandler;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import adams.core.ClassLister;

/**
 * Ancestor for handlers that provide further insight into certain types of
 * objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInspectionHandler {

  /** the cache for object class / handler relation. */
  protected static Hashtable<Class,List<Class>> m_Cache;

  /** the handlers (classnames) currently available. */
  protected static String[] m_Handlers;

  /** the handlers (classes) currently available. */
  protected static Class[] m_HandlerClasses;

  static {
    m_Cache          = new Hashtable<Class,List<Class>>();
    m_Handlers       = null;
    m_HandlerClasses = null;
  }

  /**
   * Initializes the handlers.
   */
  protected static synchronized void initHandlers() {
    int		i;

    if (m_Handlers != null)
      return;

    m_Handlers       = ClassLister.getSingleton().getClassnames(AbstractInspectionHandler.class);
    m_HandlerClasses = new Class[m_Handlers.length];
    for (i = 0; i < m_Handlers.length; i++) {
      try {
	m_HandlerClasses[i] = Class.forName(m_Handlers[i]);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate inspection handler '" + m_Handlers[i] + "': ");
	e.printStackTrace();
      }
    }
  }

  /**
   * Returns a handler for the specified object.
   *
   * @param obj		the object to get a commandline handler for
   * @return		the handler
   */
  public static synchronized List<AbstractInspectionHandler> getHandler(Object obj) {
    return getHandler(obj.getClass());
  }

  /**
   * Instantiates the handlers.
   * 
   * @param handlers	the handlers to instantiate
   * @return		the instances
   */
  protected static List<AbstractInspectionHandler> instantiate(List<Class> handlers) {
    List<AbstractInspectionHandler>	result;
    int					i;
    
    result = new ArrayList<AbstractInspectionHandler>();
    for (i = 0; i < handlers.size(); i++) {
      try {
	result.add((AbstractInspectionHandler) handlers.get(i).newInstance());
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate inspection handler '" + handlers.get(i).getName() + "':");
	e.printStackTrace();
      }
    }
    
    return result;
  }
  
  /**
   * Returns a handler for the specified class.
   *
   * @param cls		the class to get a commandline handler for
   * @return		the handler
   */
  public static synchronized List<AbstractInspectionHandler> getHandler(Class cls) {
    AbstractInspectionHandler		handler;
    List<Class>			handlers;
    int					i;

    initHandlers();

    // already cached?
    if (m_Cache.containsKey(cls))
      return instantiate(m_Cache.get(cls));

    // find suitable handler
    handlers = new ArrayList<Class>();
    for (i = 0; i < m_HandlerClasses.length; i++) {
      if (m_HandlerClasses[i] == DefaultInspectionHandler.class)
	continue;
      try {
	handler = (AbstractInspectionHandler) m_HandlerClasses[i].newInstance();
	if (handler.handles(cls)) {
	  handlers.add(m_HandlerClasses[i]);
	  break;
	}
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate inspection handler '" + m_HandlerClasses[i].getName() + "':");
	e.printStackTrace();
      }
    }

    if (handlers.size() == 0)
      handlers.add(DefaultInspectionHandler.class);

    // store in cache
    m_Cache.put(cls, handlers);

    return instantiate(handlers);
  }

  /**
   * Checks whether the handler can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the handler can handle this type of object
   */
  public abstract boolean handles(Class cls);

  /**
   * Returns further inspection values.
   *
   * @param obj		the object to further inspect
   * @return		the named inspected values
   */
  public abstract Hashtable<String,Object> inspect(Object obj);

  /**
   * Returns a list with classnames of handlers.
   *
   * @return		the handler classnames
   */
  public static String[] getHandlers() {
    return ClassLister.getSingleton().getClassnames(AbstractInspectionHandler.class);
  }
}
