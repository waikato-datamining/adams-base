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
 * PortManager.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.net;

import adams.data.SortedList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Keeps track of used ports.
 * <br>
 * NB: Using this class is no guarantee that the port isn't being used already,
 * as not all code fragments might use this class!
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PortManager
  implements Serializable {

  private static final long serialVersionUID = -4542968209872930882L;

  /** the minimum (non-privileged) port. */
  public final static int MIN_PORT = 1025;

  /** the maximum port. */
  public final static int MAX_PORT = 65535;

  /** no port available. */
  public final static int NO_PORT = -1;

  /** the singleton. */
  protected static PortManager m_Singleton;

  /** the list of ports. */
  protected List<Integer> m_Ports;

  /** the port-class association. */
  protected Map<Integer,Class> m_PortClass;

  /** the class-ports association. */
  protected Map<Class,HashSet<Integer>> m_ClassPorts;

  /**
   * Initializes the port manager.
   */
  protected PortManager() {
    super();
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_Ports      = new SortedList<>();
    m_PortClass  = new HashMap<>();
    m_ClassPorts = new HashMap<>();
  }

  /**
   * Returns the number of ports used.
   *
   * @return		the number of ports
   */
  public synchronized int size() {
    return m_Ports.size();
  }

  /**
   * Removes all ports.
   */
  public synchronized void clear() {
    m_Ports.clear();
    m_PortClass.clear();
    m_ClassPorts.clear();
  }

  /**
   * Returns the next available port.
   *
   * @param obj		the object requesting the port
   * @return		the next available port
   * @throws IllegalArgumentException	if object is null
   */
  public synchronized Integer next(Object obj) {
    if (obj == null)
      throw new IllegalArgumentException("Object is null!");
    return next(obj.getClass());
  }

  /**
   * Returns the next available port.
   *
   * @param cls		the class requesting the port
   * @return		the next available port
   * @throws IllegalArgumentException	if class is null
   */
  public synchronized Integer next(Class cls) {
    return next(cls, MIN_PORT);
  }

  /**
   * Returns the next available port, starting with the given starting port.
   *
   * @param obj		the object requesting the port
   * @param start	the first port to start with
   * @return		the next available port,
   * @throws IllegalArgumentException	if object is null
   */
  public synchronized Integer next(Object obj, int start) {
    if (obj == null)
      throw new IllegalArgumentException("Object is null!");
    return next(obj.getClass(), start);
  }

  /**
   * Returns the next available port, starting with the given starting port.
   *
   * @param cls		the class requesting the port
   * @param start	the first port to start with
   * @return		the next available port,
   * @throws IllegalArgumentException	if class is null
   */
  public synchronized Integer next(Class cls, int start) {
    int		result;
    int		i;
    int		current;
    int		prior;

    result = NO_PORT;

    if (cls == null)
      throw new IllegalArgumentException("Class is null!");

    if ((start < MIN_PORT) || (start > MAX_PORT))
      throw new IllegalArgumentException(
	"Starting port must satisfy " + MIN_PORT + " < x <= " + MAX_PORT + ", provided: " + start);

    if (!m_Ports.contains(start)) {
      m_Ports.add(start);
      result = start;
    }
    else {
      i       = m_Ports.indexOf(start);
      current = start;
      while ((i < m_Ports.size()) && (current < MAX_PORT)) {
	i++;
	prior   = current;
	current = m_Ports.get(i);
	// found a free slot?
	if (current - prior > 1) {
	  result = prior + 1;
	  break;
	}
      }
    }

    if (result != NO_PORT) {
      m_PortClass.put(result, cls);
      if (!m_ClassPorts.containsKey(cls))
	m_ClassPorts.put(cls, new HashSet<>());
      m_ClassPorts.get(cls).add(result);
    }

    return result;
  }

  /**
   * Checks whether the specified port is already in use.
   *
   * @param port	the port to check
   */
  public synchronized boolean isAvailable(int port) {
    return !m_Ports.contains(port);
  }

  /**
   * Removes the specified port from the internal list of used ports.
   *
   * @param port	the port to free
   */
  public synchronized void release(int port) {
    Class	cls;

    m_Ports.remove(port);
    cls = m_PortClass.remove(port);
    if (cls != null)
      m_ClassPorts.get(cls).remove(port);
  }

  /**
   * Returns the associated class for the port.
   *
   * @param port	the port to get the class for
   * @return		the class, null if not available
   */
  public synchronized Class usedBy(int port) {
    return m_PortClass.get(port);
  }

  /**
   * Returns the ports associated with a class.
   *
   * @param cls		the class to get the used ports for
   * @return		the ports
   */
  public synchronized List<Integer> uses(Class cls) {
    List<Integer>	result;

    result = new ArrayList<>();
    if (m_ClassPorts.containsKey(cls))
      result.addAll(m_ClassPorts.get(cls));

    return result;
  }

  /**
   * Returns the singleton instance of the port manager.
   *
   * @return		the port manager
   */
  public synchronized static PortManager getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new PortManager();
    return m_Singleton;
  }
}
