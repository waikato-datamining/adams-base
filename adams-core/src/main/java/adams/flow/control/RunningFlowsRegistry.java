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
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import java.util.HashSet;

import adams.core.logging.LoggingObject;

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
  protected HashSet<Flow> m_Flows;
  
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
    m_Flows = new HashSet<Flow>();
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
  public void addFlow(Flow flow) {
    m_Flows.add(flow);
  }
  
  /**
   * Removes the flow from the internal list of registered flows.
   * 
   * @param flow	the flow to remove
   */
  public void removeFlow(Flow flow) {
    m_Flows.remove(flow);
  }
  
  /**
   * Returns the currently registered of flows.
   * 
   * @return		the current flows
   */
  public synchronized Flow[] flows() {
    return m_Flows.toArray(new Flow[m_Flows.size()]);
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
