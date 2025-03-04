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
 * FlowContextUtils.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.discovery.PropertyPath.Path;
import adams.core.discovery.PropertyTraversal;
import adams.core.discovery.PropertyTraversal.Observer;

import java.beans.PropertyDescriptor;

/**
 * Helper class for flow contexts.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FlowContextUtils {

  /**
   * Observer that updates the context.
   */
  public static class UpdateObserver
    implements Observer {

    /** the context to set. */
    protected Actor m_Context;

    /** the number of flow contexts that were updated. */
    protected int m_Updates;

    /**
     * Initializes the observer.
     *
     * @param context	the context to set
     */
    public UpdateObserver(Actor context) {
      m_Context = context;
      m_Updates = 0;
    }

    /**
     * Presents the current path, descriptor and object to the observer.
     *
     * @param path	the path
     * @param desc	the property descriptor
     * @param parent	the parent object
     * @param child	the child object
     * @return 		true if to continue observing
     */
    @Override
    public boolean observe(Path path, PropertyDescriptor desc, Object parent, Object child) {
      if (child instanceof FlowContextHandler) {
	((FlowContextHandler) child).setFlowContext(m_Context);
	m_Updates++;
      }
      return true;
    }

    /**
     * Returns the number of updates.
     *
     * @return		the updates
     */
    public int getUpdates() {
      return m_Updates;
    }
  }

  /**
   * Observer for locating any FlowContextHandler instances.
   */
  public static class LocatorObserver
    implements Observer {

    /** whether a FlowContextHandler was found. */
    protected boolean m_Present;

    /**
     * Returns whether a FlowContextHandler was found.
     *
     * @return		true if found
     */
    public boolean isPresent() {
      return m_Present;
    }

    /**
     * Presents the current path, descriptor and object to the observer.
     *
     * @param path	the path
     * @param desc	the property descriptor
     * @param parent	the parent object
     * @param child	the child object
     * @return		true if to continue observing
     */
    @Override
    public boolean observe(Path path, PropertyDescriptor desc, Object parent, Object child) {
      if (child instanceof FlowContextHandler) {
	m_Present = true;
	return false;
      }
      return true;
    }
  }

  /**
   * Updates the flow context recursively in the object and its children.
   *
   * @param obj		the object to update
   * @param context 	the context to set
   * @return 		the number of contexts updated
   */
  public static int update(Object obj, Actor context) {
    UpdateObserver 	observer;
    PropertyTraversal	traversal;

    observer = new UpdateObserver(context);
    traversal = new PropertyTraversal();
    traversal.traverse(observer, obj);
    return observer.getUpdates();
  }

  /**
   * Returns whether the object or one of the object's children is a FlowContextHandler.
   *
   * @param obj		the object to check
   * @return		true if a handler
   */
  public static boolean isHandler(Object obj) {
    LocatorObserver 	observer;
    PropertyTraversal	traversal;

    if (obj instanceof FlowContextHandler)
      return true;

    observer = new LocatorObserver();
    traversal = new PropertyTraversal();
    traversal.traverse(observer, obj);
    return observer.isPresent();
  }
}
