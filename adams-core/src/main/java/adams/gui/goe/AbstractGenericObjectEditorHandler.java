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
 * AbstractGenericObjectEditorHandler.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import java.beans.PropertyEditor;
import java.io.Serializable;
import java.util.Hashtable;

import javax.swing.JPanel;

import adams.core.ClassLister;

/**
 * Ancestor for classes that handle commandline options for various frameworks.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGenericObjectEditorHandler
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 5769901293009589319L;

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
   * Sets the class type to use.
   *
   * @param editor	the editor to update
   * @param cls		the class to set
   * @return		true if successfully set
   */
  public abstract boolean setClassType(PropertyEditor editor, Class cls);

  /**
   * Returns the class type currently in use.
   *
   * @param editor	the editor to query
   * @return		the class type
   */
  public abstract Class getClassType(PropertyEditor editor);

  /**
   * Sets whether the class can be changed in the dialog.
   *
   * @param editor	the editor to update
   * @param canChange	if true the class can be changed in the dialog
   * @return		true if successfully set
   */
  public abstract boolean setCanChangeClassInDialog(PropertyEditor editor, boolean canChange);

  /**
   * Returns whether the class can be changed in the dialog.
   *
   * @param editor	the editor to query
   * @return		true if the class can be changed in the dialog
   */
  public abstract boolean getCanChangeClassInDialog(PropertyEditor editor);

  /**
   * Sets the editor value.
   *
   * @param editor	the editor to update
   * @param value	the object to set
   * @return		true if successfully set
   */
  public abstract boolean setValue(PropertyEditor editor, Object value);

  /**
   * Returns the value currently being edited.
   *
   * @param editor	the editor to query
   * @return		the current editor value
   */
  public abstract Object getValue(PropertyEditor editor);

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
   * Checks whether the editor supplies its own panel.
   *
   * @param editor	the editor to check
   * @return		true if the editor provides a panel
   */
  public abstract boolean hasCustomPanel(PropertyEditor editor);

  /**
   * Returns the custom panel of the editor.
   *
   * @param editor	the editor to obtain the panel from
   * @return		the custom panel, null if none available
   */
  public abstract JPanel getCustomPanel(PropertyEditor editor);

  /**
   * Initializes the handlers.
   */
  protected static synchronized void initHandlers() {
    int		i;

    if (m_Handlers != null)
      return;

    m_Handlers       = ClassLister.getSingleton().getClassnames(AbstractGenericObjectEditorHandler.class);
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
   * Returns a handler for the specified object.
   *
   * @param obj		the object to get a commandline handler for
   * @return		the handler
   */
  public static synchronized AbstractGenericObjectEditorHandler getHandler(Object obj) {
    return getHandler(obj.getClass());
  }

  /**
   * Returns a handler for the specified class.
   *
   * @param cls		the class to get a commandline handler for
   * @return		the handler
   */
  public static synchronized AbstractGenericObjectEditorHandler getHandler(Class cls) {
    AbstractGenericObjectEditorHandler	result;
    AbstractGenericObjectEditorHandler	handler;
    int				i;

    result = null;

    initHandlers();

    // already cached?
    if (m_Cache.containsKey(cls)) {
      try {
	result = (AbstractGenericObjectEditorHandler) m_Cache.get(cls).newInstance();
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
	handler = (AbstractGenericObjectEditorHandler) m_HandlerClasses[i].newInstance();
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
      result = new DefaultGenericObjectEditorHandler();

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
