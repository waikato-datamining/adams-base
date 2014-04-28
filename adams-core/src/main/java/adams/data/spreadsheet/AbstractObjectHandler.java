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
 * AbstractObjectHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import java.util.HashMap;
import java.util.HashSet;

import adams.core.ClassLister;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Cell.ContentType;

/**
 * Ancestor for handlers that manage the {@link ContentType#OBJECT} type of
 * cells.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of object to handle
 */
public abstract class AbstractObjectHandler<T>
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3675851527684921384L;

  /** the cache for object class/object handler relation. */
  protected static HashMap<Class,Class> m_Handlers;

  /** the cache for object classes without object handlers. */
  protected static HashSet<Class> m_NoHandlers;
  
  static {
    m_Handlers   = new HashMap<Class,Class>();
    m_NoHandlers = new HashSet<Class>();
  }
  
  /**
   * Checks whether the handler can process the given object.
   * 
   * @param obj 	the object to check
   * @return		true if handler can process the object
   * @see		#handles(Class)
   */
  public boolean handles(Object obj) {
    return handles(obj.getClass());
  }
  
  /**
   * Checks whether the handler can process the given class.
   * 
   * @param cls 	the class to check
   * @return		true if handler can process the class
   */
  public abstract boolean handles(Class cls);
  
  /**
   * Parses the given string.
   * 
   * @param s		the string
   * @return		the generated object, null if failed to convert
   */
  public abstract T parse(String s);
  
  /**
   * Turns the given object back into a string.
   * 
   * @param obj		the object to convert into a string
   * @return		the string representation
   */
  public abstract String format(T obj);
  
  /**
   * Returns the handler for the specific object.
   * 
   * @param obj		the object to get the handler for
   * @return		the handler class or null if none available
   */
  public static AbstractObjectHandler getHandler(Object obj) {
    return getHandler(obj.getClass());
  }
  
  /**
   * Returns the handler for the specific object class.
   * 
   * @param cls		the class to get the handler for
   * @return		the handler or null if none available
   */
  public static AbstractObjectHandler getHandler(Class cls) {
    AbstractObjectHandler	result;
    String[]			handlers;
    AbstractObjectHandler	handler;
    
    result = null;
    
    if (m_NoHandlers.contains(cls))
      return result;
    
    // already cached?
    if (m_Handlers.containsKey(cls)) {
      try {
	result = (AbstractObjectHandler) m_Handlers.get(cls).newInstance();
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate handler: " + m_Handlers.get(cls).getName());
	e.printStackTrace();
      }
    }
    else {
      // check all handlers
      handlers = getHandlers();
      for (String h: handlers) {
	try {
	  handler = (AbstractObjectHandler) Class.forName(h).newInstance();
	  if (handler.handles(cls)) {
	    m_Handlers.put(cls, handler.getClass());
	    result = handler;
	  }
	}
	catch (Exception e) {
	  System.err.println("Failed to process handler: " + h);
	  e.printStackTrace();
	}
      }
    }
    
    // none found?
    if (result == null)
      m_NoHandlers.add(cls);
    
    return result;
  }

  /**
   * Returns a list with classnames of handlers.
   *
   * @return		the handler classnames
   */
  public static String[] getHandlers() {
    return ClassLister.getSingleton().getClassnames(AbstractObjectHandler.class);
  }
}
