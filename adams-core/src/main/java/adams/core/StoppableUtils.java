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
 * StoppableUtils.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.discovery.PropertyPath.Path;
import adams.core.discovery.PropertyTraversal;
import adams.core.discovery.PropertyTraversal.Observer;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Methods for objects implementing the {@link Stoppable} interface.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class StoppableUtils {

  /** the logger in use. */
  protected static Logger LOGGER = LoggingHelper.getLogger(StoppableUtils.class);

  /**
   * Observer that records all the objects that implement the {@link Stoppable} interface.
   */
  public static class StoppableObserver
    implements Observer {

    /** the set of objects. */
    protected Set<Object> m_Recorded;

    /** the list to populate. */
    protected List<Object> m_StoppableObjects;

    /**
     * Initializes the observer.
     *
     * @param stoppableObjects	for collecting the objects
     */
    public StoppableObserver(List<Object> stoppableObjects) {
      m_Recorded         = new HashSet<>();
      m_StoppableObjects = stoppableObjects;
    }

    /**
     * Presents the current path, descriptor and object to the observer.
     *
     * @param path	the path
     * @param desc	the property descriptor
     * @param parent	the parent object
     * @param child	the child object
     */
    @Override
    public void observe(Path path, PropertyDescriptor desc, Object parent, Object child) {
      if (isStoppable(child) && !m_Recorded.contains(child))
	m_StoppableObjects.add(child);
    }
  }

  /**
   * Determines all the objects that implement the {@link Stoppable} interface.
   *
   * @param obj		the object to inspect, can be null
   * @return		the list of objects
   */
  public static List<Object> listStoppable(Object obj) {
    List<Object>	result;
    PropertyTraversal	traversal;
    StoppableObserver	observer;

    result = new ArrayList<>();

    if (obj == null)
      return result;

    observer = new StoppableObserver(result);
    traversal = new PropertyTraversal();
    traversal.traverse(observer, obj);

    return result;
  }

  /**
   * Checks whether the object is stoppable.
   *
   * @param obj		the object to check, can be null
   * @return		true if stoppable
   */
  public static boolean isStoppable(Object obj) {
    return (obj instanceof Stoppable);
  }

  /**
   * Checks whether the object or any of its sub-objects are stoppable.
   *
   * @param obj		the object to check, can be null
   * @return		true if stoppable
   */
  public static boolean isAnyStoppable(Object obj) {
    return !listStoppable(obj).isEmpty();
  }

  /**
   * Stops the execution of the object if itself implements the {@link Stoppable} interface.
   *
   * @param obj		the object to stop, can be null
   * @return		true if stopped
   */
  public static boolean stopExecution(Object obj) {
    if (isStoppable(obj)) {
      ((Stoppable) obj).stopExecution();
      return true;
    }

    return false;
  }

  /**
   * Stops the execution of the object and its children of they implement the {@link Stoppable} interface.
   *
   * @param obj		the object to stop itself and its children, can be null
   * @return		true if at least one execution was stopped
   */
  public static boolean stopAnyExecution(Object obj) {
    List<Object> objects;

    objects = listStoppable(obj);
    if (!objects.isEmpty()) {
      if (LoggingHelper.isAtLeast(LOGGER, Level.FINE))
	LOGGER.info(obj.getClass().getName() + ": " + Utils.classesToString(objects.toArray()));
      else if (LoggingHelper.isAtLeast(LOGGER, Level.INFO))
	LOGGER.info(obj.getClass().getName() + ": #" + objects.size());
      for (Object object : objects)
	((Stoppable) object).stopExecution();
      return true;
    }

    return false;
  }
}
