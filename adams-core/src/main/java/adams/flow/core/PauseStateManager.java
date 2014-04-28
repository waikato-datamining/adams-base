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
 * PauseStateManager.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import java.util.HashSet;

import adams.core.logging.LoggingObject;
import adams.event.FlowPauseStateEvent;
import adams.event.FlowPauseStateListener;
import adams.event.FlowPauseStateEvent.Type;

/**
 * Manages the pause state in the flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PauseStateManager
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -7910626028332275790L;
  
  /** the listeners. */
  protected HashSet<FlowPauseStateListener> m_Listeners;
  
  /** whether the state is paused. */
  protected boolean m_Paused;
  
  /**
   * Initializes the handler.
   */
  public PauseStateManager() {
    super();
    
    m_Listeners = new HashSet<FlowPauseStateListener>();
    m_Paused    = false;
  }
  
  /**
   * Adds the specified listener.
   * 
   * @param l		the listener to add
   */
  public synchronized void addListener(FlowPauseStateListener l) {
    m_Listeners.add(l);
  }
  
  /**
   * Removes the specified listener.
   * 
   * @param l		the listener to remove
   */
  public synchronized void removeListener(FlowPauseStateListener l) {
    m_Listeners.remove(l);
  }
  
  /**
   * Notifies all listeners.
   * 
   * @param e		the event to send
   */
  protected synchronized void notifyListeners(FlowPauseStateEvent e) {
    FlowPauseStateListener[]	listeners;
    
    listeners = m_Listeners.toArray(new FlowPauseStateListener[m_Listeners.size()]);
    for (FlowPauseStateListener l: listeners)
      l.flowPauseStateChanged(e);
  }
  
  /**
   * Pauses the flow.
   * 
   * @param source	the flow that triggers the pausing
   */
  public synchronized void pause(AbstractActor source) {
    if (m_Paused)
      return;
    
    m_Paused = true;
    
    notifyListeners(new FlowPauseStateEvent(source, Type.PAUSED));
  }
  
  /**
   * Resumes the flow.
   * 
   * @param source	the flow that triggers the resuming
   */
  public synchronized void resume(AbstractActor source) {
    if (!m_Paused)
      return;
    
    m_Paused = false;
    
    notifyListeners(new FlowPauseStateEvent(source, Type.RESUMED));
  }
  
  /**
   * Returns whether the handler is in paused state.
   * 
   * @return		true if in paused state
   */
  public synchronized boolean isPaused() {
    return m_Paused;
  }
}
