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
 * AbstractObjectInstanceHandler.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe.objectinstance;

import adams.core.ClassLister;
import adams.gui.goe.DefaultGenericObjectEditorHandler;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * Ancestor for classes that handle object instantiations, mainly for classes
 * that don't have a default constructor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractObjectInstanceHandler
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 5769901293009589319L;

  /** the cache for object class / handler relation. */
  protected static Hashtable<Class,Class> m_Cache;

  /** the cache for classes without a handler. */
  protected static HashSet<Class> m_NoHandlers;

  /** the handlers (classnames) currently available. */
  protected static String[] m_Handlers;

  /** the handlers (classes) currently available. */
  protected static Class[] m_HandlerClasses;

  static {
    m_Cache          = new Hashtable<>();
    m_NoHandlers     = new HashSet<>();
    m_Handlers       = null;
    m_HandlerClasses = null;
  }

  /**
   * Returns whether this handler handles the specified class.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  public abstract boolean handles(Class cls);

  /**
   * Creates a new instance of the class.
   *
   * @param cls		the class to create a new instance from
   * @return		the instance, null if failed to instantiate
   */
  public abstract Object newInstance(Class cls);

  /**
   * Initializes the handlers.
   */
  protected static synchronized void initHandlers() {
    int		i;

    if (m_Handlers != null)
      return;

    m_Handlers       = ClassLister.getSingleton().getClassnames(AbstractObjectInstanceHandler.class);
    m_HandlerClasses = new Class[m_Handlers.length];
    for (i = 0; i < m_Handlers.length; i++) {
      try {
	m_HandlerClasses[i] = Class.forName(m_Handlers[i]);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate GOE handler '" + m_Handlers[i] + "': ");
	e.printStackTrace();
      }
    }
  }

  /**
   * Returns a handler for the specified class.
   *
   * @param cls		the class to get a handler for
   * @return		the handler
   */
  public static synchronized AbstractObjectInstanceHandler getHandler(Class cls) {
    AbstractObjectInstanceHandler result;
    AbstractObjectInstanceHandler handler;
    int				i;

    result = null;

    initHandlers();

    // no handler?
    if (m_NoHandlers.contains(cls))
      return null;

    // already cached?
    if (m_Cache.containsKey(cls)) {
      try {
	result = (AbstractObjectInstanceHandler) m_Cache.get(cls).newInstance();
	return result;
      }
      catch (Exception e) {
	// ignored
	result = null;
      }
    }

    // find suitable handler
    for (i = 0; i < m_HandlerClasses.length; i++) {
      if (m_HandlerClasses[i] == DefaultGenericObjectEditorHandler.class)
	continue;
      try {
	handler = (AbstractObjectInstanceHandler) m_HandlerClasses[i].newInstance();
	if (handler.handles(cls)) {
	  result = handler;
	  break;
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    // store in cache
    if (result != null) {
      m_Cache.put(cls, result.getClass());
    }
    else {
      System.err.println("No handler for instantiating default objects of class: " + cls.getName());
      m_NoHandlers.add(cls);
    }

    return result;
  }

  /**
   * Returns a list with classnames of handlers.
   *
   * @return		the handler classnames
   */
  public static synchronized String[] getHandlers() {
    initHandlers();
    return m_Handlers;
  }
}
