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
 * RunningFlowsRegistry.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.core.logging.LoggingObject;

import java.util.HashMap;

/**
 * Used for registering running flows.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RunningFlowsRegistry
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = 2503815818320314740L;

  /** the registered flows. */
  protected HashMap<Integer,Flow> m_Flows;

  /** the counter. */
  protected int m_Counter;
  
  /** the singleton. */
  protected static RunningFlowsRegistry m_Singleton;
  
  /**
   * Initializes the registry.
   */
  private RunningFlowsRegistry() {
    super();
    initialize();
    finishInit();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_Flows   = new HashMap<>();
    m_Counter = 0;
  }

  /**
   * Gets called after the initialization has finished.
   */
  protected void finishInit() {
  }
  
  /**
   * Adds the flow to the internal list of registered flows.
   * 
   * @param flow	the flow to add
   */
  public synchronized void addFlow(Flow flow) {
    m_Counter++;
    removeFlow(flow);
    m_Flows.put(m_Counter, flow);
  }

  /**
   * Returns the flow associated with the ID.
   *
   * @param id		the ID of the flow to retrieve
   * @return		the associated flow, null if none available for that ID
   */
  public synchronized Flow getFlow(int id) {
    return m_Flows.get(id);
  }

  /**
   * Removes the flow from the internal list of registered flows.
   *
   * @param id		the if of the flow to remove
   */
  public synchronized void removeFlow(int id) {
    m_Flows.remove(id);
  }

  /**
   * Removes the flow from the internal list of registered flows.
   * 
   * @param flow	the flow to remove
   */
  public synchronized void removeFlow(Flow flow) {
    for (Integer id: m_Flows.keySet()) {
      if (getFlow(id) == flow) {
	m_Flows.remove(id);
	return;
      }
    }
  }
  
  /**
   * Returns the currently registered flows.
   * 
   * @return		the current flows
   */
  public synchronized Flow[] flows() {
    return m_Flows.values().toArray(new Flow[m_Flows.size()]);
  }

  /**
   * Returns the IDs of the currently registered flows.
   *
   * @return		the IDs of the current flows
   */
  public synchronized Integer[] ids() {
    return m_Flows.keySet().toArray(new Integer[m_Flows.size()]);
  }

  /**
   * Returns the singleton.
   * 
   * @return		the singleon
   */
  public static synchronized RunningFlowsRegistry getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new RunningFlowsRegistry();
    return m_Singleton;
  }
}
