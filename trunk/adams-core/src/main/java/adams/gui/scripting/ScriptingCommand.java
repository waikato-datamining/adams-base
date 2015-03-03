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
 * ScriptingCommand.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.scripting;

import adams.gui.core.BasePanel;

/**
 * A container object for a scripting command with optional code to execute
 * when finished.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScriptingCommand {

  /** the command to run. */
  protected String m_Cmd;
  
  /** the (optional) code to execute. */
  protected ScriptingCommandCode m_Code;
  
  /** the affected base panel. */
  protected BasePanel m_BasePanel;
  
  /**
   * Initializes the command with no code to execute.
   * 
   * @param panel	the affected panel
   * @param cmd		the command to execute
   */
  public ScriptingCommand(BasePanel panel, String cmd) {
    this(panel, cmd, null);
  }
  
  /**
   * Initializes the command with the code to execute.
   * 
   * @param panel	the affected panel
   * @param cmd		the command to execute
   * @param code	the code to execute, ignored if null
   */
  public ScriptingCommand(BasePanel panel, String cmd, ScriptingCommandCode code) {
    super();
    
    m_Cmd       = cmd;
    m_BasePanel = panel;
    m_Code      = code;
    if (m_Code != null) {
      m_Code.setCommand(cmd);
      m_Code.setBasePanel(panel);
    }
  }
  
  /**
   * Returns the underlying command to execute.
   * 
   * @return		the command
   */
  public String getCommand() {
    return m_Cmd;
  }
  
  /**
   * Returns the affected base panel.
   * 
   * @return		the panel
   */
  public BasePanel getBasePanel() {
    return m_BasePanel;
  }
  
  /**
   * Returns whether any code is available to execute.
   * 
   * @return		true if code is available
   */
  public boolean hasCode() {
    return (m_Code != null);
  }
  
  /**
   * Returns the optional code to execute.
   * 
   * @return		the code, can be null
   */
  public ScriptingCommandCode getCode() {
    return m_Code;
  }
  
  /**
   * Returns a string representation.
   * 
   * @return		a string
   */
  public String toString() {
    return "cmd=" + getCommand() + ", code=" + hashCode();
  }
}
