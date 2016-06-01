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
 * AbstractCommandLineHandler.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.ClassLister;
import adams.core.StaticClassLister;
import adams.core.logging.LoggingObject;

import java.util.Hashtable;

/**
 * Ancestor for classes that handle commandline options for various frameworks.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCommandLineHandler
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -200830732847780663L;

  /** the props file with the static class name lists. */
  public final static String FILENAME = "CommandLineHandlers.props";

  /** the key in the props file with the static class name lists. */
  public final static String KEY_HANDLERS = "Handlers";

  /** the cache for object class / handler relation. */
  protected static Hashtable<Class,Class> m_Cache;

  /** the handlers (classnames) currently available. */
  protected static String[] m_Handlers;

  /** the handlers (classes) currently available. */
  protected static Class[] m_HandlerClasses;

  static {
    m_Cache          = new Hashtable<Class,Class>();
    m_Handlers       = null;
    m_HandlerClasses = null;
  }

  /**
   * Generates an object from the specified commandline.
   *
   * @param cmd		the commandline to create the object from
   * @return		the created object, null in case of error
   */
  public abstract Object fromCommandLine(String cmd);

  /**
   * Generates an object from the commandline options.
   *
   * @param args	the commandline options to create the object from
   * @return		the created object, null in case of error
   */
  public abstract Object fromArray(String[] args);

  /**
   * Generates a commandline from the specified object.
   *
   * @param obj		the object to create the commandline for
   * @return		the generated commandline
   */
  public abstract String toCommandLine(Object obj);

  /**
   * Generates a commandline from the specified object. Uses a shortened
   * format, e.g., removing the package from the class.
   *
   * @param obj		the object to create the commandline for
   * @return		the generated commandline
   */
  public abstract String toShortCommandLine(Object obj);

  /**
   * Generates an options array from the specified object.
   *
   * @param obj		the object to create the array for
   * @return		the generated array
   */
  public abstract String[] toArray(Object obj);

  /**
   * Returns the commandline options (without classname) of the specified object.
   *
   * @param obj		the object to get the options from
   * @return		the options
   */
  public abstract String[] getOptions(Object obj);

  /**
   * Sets the options of the specified object.
   *
   * @param obj		the object to set the options for
   * @param args	the options
   * @return		true if options successfully set
   */
  public abstract boolean setOptions(Object obj, String[] args);

  /**
   * Splits the commandline into an array.
   *
   * @param cmdline	the commandline to split
   * @return		the generated array of options
   */
  public abstract String[] splitOptions(String cmdline);

  /**
   * Turns the option array back into a commandline.
   *
   * @param args	the options to turn into a commandline
   * @return		the generated commandline
   */
  public abstract String joinOptions(String[] args);

  /**
   * Checks whether the given objects can be processed.
   *
   * @param obj		the object to inspect
   * @return		true if the handler can process the object
   */
  public boolean handles(Object obj) {
    return handles(obj.getClass());
  }

  /**
   * Checks whether the given class can be processed.
   *
   * @param cls		the class to inspect
   * @return		true if the handler can process the class
   */
  public abstract boolean handles(Class cls);

  /**
   * Initializes the handlers.
   */
  protected static synchronized void initHandlers() {
    int		i;
    String	propsfile;

    if (m_Handlers != null)
      return;

    m_Handlers = ClassLister.getSingleton().getClassnames(AbstractCommandLineHandler.class);
    // no dynamic class discovery available?
    if (m_Handlers.length == 0) {
      propsfile  = "adams/core/option/" + FILENAME;
      m_Handlers = StaticClassLister.getSingleton().getClasses(propsfile, KEY_HANDLERS);
      System.err.println(
	  "WARNING: no commandline handlers determined using dynamic class discovery, "
	  + "falling back to using class names stored in '" + propsfile + "' files.");
    }

    m_HandlerClasses = new Class[m_Handlers.length];
    for (i = 0; i < m_Handlers.length; i++) {
      try {
	m_HandlerClasses[i] = Class.forName(m_Handlers[i]);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate commandline handler '" + m_Handlers[i] + "': ");
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
  public static synchronized AbstractCommandLineHandler getHandler(Object obj) {
    return getHandler(obj.getClass());
  }

  /**
   * Returns a handler for the specified class.
   *
   * @param cls		the class to get a commandline handler for
   * @return		the handler
   */
  public static synchronized AbstractCommandLineHandler getHandler(Class cls) {
    AbstractCommandLineHandler	result;
    AbstractCommandLineHandler	handler;
    int				i;

    result = null;

    initHandlers();

    // already cached?
    if (m_Cache.containsKey(cls)) {
      try {
	result = (AbstractCommandLineHandler) m_Cache.get(cls).newInstance();
	return result;
      }
      catch (Exception e) {
	// ignored
	result = null;
      }
    }

    // find suitable handler
    for (i = 0; i < m_HandlerClasses.length; i++) {
      if (m_HandlerClasses[i] == DefaultCommandLineHandler.class)
	continue;
      try {
	handler = (AbstractCommandLineHandler) m_HandlerClasses[i].newInstance();
	if (handler.handles(cls)) {
	  result = handler;
	  break;
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    if (result == null)
      result = new DefaultCommandLineHandler();

    // store in cache
    m_Cache.put(cls, result.getClass());

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
