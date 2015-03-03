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
 * FlowExecutionListeningSupporter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;


/**
 * Interface for classes that support listening using {@link FlowExecutionListener}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface FlowExecutionListeningSupporter {
  
  /**
   * Sets whether flow execution listening is enabled.
   * 
   * @param value	true if to enable listening
   */
  public void setFlowExecutionListeningEnabled(boolean value);
  
  /**
   * Returns whether flow execution listening is enabled.
   * 
   * @return		true if listening is enabled
   */
  public boolean isFlowExecutionListeningEnabled();
  
  /**
   * Sets the listener to use.
   * 
   * @param l		the listener to use
   */
  public void setFlowExecutionListener(FlowExecutionListener l);
  
  /**
   * Returns the current listener in use.
   * 
   * @return		the listener
   */
  public FlowExecutionListener getFlowExecutionListener();
  
  /**
   * Returns whether listeners can be attached at runtime.
   * 
   * @return		true if listeners can be attached dynamically
   */
  public boolean canStartListeningAtRuntime();
  
  /**
   * Attaches the listener and starts listening.
   * 
   * @param l		the listener to attach and use immediately
   * @return		true if listening could be started successfully
   */
  public boolean startListeningAtRuntime(FlowExecutionListener l);
}
