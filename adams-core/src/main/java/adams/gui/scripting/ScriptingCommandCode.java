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
 * ScriptingCommandCode.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.scripting;

import adams.gui.core.BasePanel;

/**
 * Class for the optional code to execute when a command finishes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class ScriptingCommandCode {

  /** the command that was run. */
  protected String m_Cmd = null;
  
  /** the affected base panel. */
  protected BasePanel m_BasePanel = null;
  
  /** a potential error message. */
  protected String m_Error = null;
  
  /**
   * Sets the command that was executed.
   * 
   * @param value	the command
   */
  public void setCommand(String value) {
    m_Cmd = value;
  }
  
  /**
   * Returns the command that was run.
   * 
   * @return		the command
   */
  public String getCommand() {
    return m_Cmd;
  }
  
  /**
   * Sets the BasePanel that was affected.
   * 
   * @param value	the BasePanel
   */
  public void setBasePanel(BasePanel value) {
    m_BasePanel = value;
  }
  
  /**
   * Returns the BasePanel that was affected.
   * 
   * @return		the BasePanel
   */
  public BasePanel getBasePanel() {
    return m_BasePanel;
  }
  
  /**
   * Sets the error message, if any.
   * 
   * @param value	the error or null if none
   */
  public void setError(String value) {
    m_Error = value;
  }
  
  /**
   * Returns the error message, if any.
   * 
   * @return		the error, null if none available
   */
  public String getError() {
    return m_Error;
  }
  
  /**
   * Checks whether an error message exists.
   * 
   * @return		true if an error message exists
   */
  public boolean hasError() {
    return (m_Error != null);
  }
  
  /**
   * The code that gets executed after the command was run.
   */
  public abstract void execute();
}