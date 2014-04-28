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
 * ScriptingEvent.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import java.util.EventObject;

import adams.gui.scripting.ScriptingCommand;

/**
 * Gets sent when a scripting event happened.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScriptingEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 5373990735194119669L;

  /** the command that was run. */
  protected ScriptingCommand m_Cmd;
  
  /** whether the command was successfully run. */
  protected boolean m_Success;
  
  /** optional error message. */
  protected String m_Error;
  
  /**
   * Initializes the object.
   * 
   * @param source	the object that triggered the command
   * @param cmd		the command that was run
   * @param success	whether the command was successfully run
   */
  public ScriptingEvent(Object source, ScriptingCommand cmd, boolean success) {
    this(source, cmd, success, null);
  }
  
  /**
   * Initializes the object.
   * 
   * @param source	the object that triggered the command
   * @param cmd		the command that was run
   * @param success	whether the command was successfully run
   * @param error	an error message (or null if none)
   */
  public ScriptingEvent(Object source, ScriptingCommand cmd, boolean success, String error) {
    super(source);
    
    m_Cmd     = cmd;
    m_Success = success;
    m_Error   = error;
  }
  
  /**
   * Returns the command that was run.
   * 
   * @return		the command
   */
  public ScriptingCommand getCmd() {
    return m_Cmd;
  }
  
  /**
   * Returns whether the command was run successfully or not.
   * 
   * @return		true if the command was run successfully
   */
  public boolean getSuccess() {
    return m_Success;
  }
  
  /**
   * Returns whether an error message is available.
   * 
   * @return		true if an error message is available
   */
  public boolean hasError() {
    return (m_Error != null);
  }
  
  /**
   * Returns the error message.
   * 
   * @return		the error message, null if none available
   */
  public String getError() {
    return m_Error;
  }
  
  /**
   * Returns a string representation of the event object.
   * 
   * @return		the string representation
   */
  @Override
  public String toString() {
    StringBuilder	result;
    
    result = new StringBuilder(super.toString());
    result.deleteCharAt(result.length() - 1);
    
    result.append(",engine=" + getSource());
    result.append(",success=" + m_Success);
    result.append(",cmd=" + m_Cmd);
    result.append("]");
    
    return result.toString();
  }
}
