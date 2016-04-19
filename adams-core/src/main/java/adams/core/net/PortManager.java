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

/**
 * Keeps track of used ports.
 * <br>
 * NB: Is no guarantee that the port isn't being used already, as not all
 * code might use this class!
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PortManager {

  /** the minimum (non-privileged) port. */
  public final static int MIN_PORT = 1025;

  /** the maximum port. */
  public final static int MAX_PORT = 65535;

  /** no port available. */
  public final static int NO_PORT = -1;

  /** the singleton. */
  protected static PortManager m_Singleton;

  /** the list of ports. */
  protected SortedList<Integer> m_Ports;

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
    m_Ports = new SortedList<>();
  }

  /**
   * Returns the next available port.
   *
   * @return		the next available port
   */
  public synchronized Integer next() {
    return next(MIN_PORT);
  }

  /**
   * Returns the next available port, starting with the given starting port.
   *
   * @param start	the first port to start with
   * @return		the next available port,
   */
  public synchronized Integer next(int start) {
    int		result;
    int		i;
    int		current;
    int		prior;

    result = NO_PORT;

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
    m_Ports.remove(port);
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
