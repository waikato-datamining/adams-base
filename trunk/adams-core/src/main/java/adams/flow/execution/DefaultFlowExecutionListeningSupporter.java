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
 * DefaultFlowExecutionListeningSupporter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import java.io.Serializable;

import adams.flow.control.Flow;

/**
 * Default supporter that can be used instead of {@link Flow} in case of 
 * running actors outside a flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultFlowExecutionListeningSupporter
  implements Serializable, FlowExecutionListeningSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 2686287560522068702L;
  
  /** whether listening is enabled. */
  protected boolean m_FlowExecutionListeningEnabled;
  
  /** the listener to use. */
  protected FlowExecutionListener m_Listener;

  /**
   * Default constructor.
   */
  public DefaultFlowExecutionListeningSupporter() {
    m_Listener                      = new NullListener();
    m_FlowExecutionListeningEnabled = false;
  }
  
  /**
   * Sets whether flow execution listening is enabled.
   * 
   * @param value	true if to enable listening
   */
  @Override
  public void setFlowExecutionListeningEnabled(boolean value) {
    m_FlowExecutionListeningEnabled = value;
  }

  /**
   * Returns whether flow execution listening is enabled.
   * 
   * @return		true if listening is enabled
   */
  @Override
  public boolean isFlowExecutionListeningEnabled() {
    return m_FlowExecutionListeningEnabled;
  }

  /**
   * Sets the listener to use.
   * 
   * @param l		the listener to use
   */
  @Override
  public void setFlowExecutionListener(FlowExecutionListener l) {
    m_Listener = l;
  }

  /**
   * Returns the current listener in use.
   * 
   * @return		the listener
   */
  @Override
  public FlowExecutionListener getFlowExecutionListener() {
    return m_Listener;
  }
  
  /**
   * Returns whether listeners can be attached at runtime.
   * 
   * @return		always false
   */
  public boolean canStartListeningAtRuntime() {
    return false;
  }
  
  /**
   * Attaches the listener and starts listening.
   * 
   * @param l		the listener to attach and use immediately
   * @return		always false
   */
  public boolean startListeningAtRuntime(FlowExecutionListener l) {
    return false;
  }
}
