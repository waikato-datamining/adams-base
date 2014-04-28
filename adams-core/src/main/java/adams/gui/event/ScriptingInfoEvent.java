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
 * ScriptingInfoEvent.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;


import java.util.EventObject;

import adams.gui.scripting.AbstractScriptingEngine;

/**
 * Gets sent when a scripting event happened.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScriptingInfoEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 5373990735194119669L;

  /** the command that was run. */
  protected String m_Cmd;

  /**
   * Initializes the object.
   *
   * @param engine	the scripting engine that ran the command
   * @param cmd		the command that was run
   */
  public ScriptingInfoEvent(AbstractScriptingEngine engine, String cmd) {
    super(engine);

    m_Cmd = cmd;
  }

  /**
   * Returns the scripting engine that triggered the event.
   *
   * @return		the engine
   */
  public AbstractScriptingEngine getScriptingEngine() {
    return (AbstractScriptingEngine) getSource();
  }

  /**
   * Checks whether a command is available or not (= idle).
   *
   * @return		true if command available
   */
  public boolean hasCmd() {
    return (m_Cmd != null);
  }

  /**
   * Returns the command that was run.
   *
   * @return		the command
   */
  public String getCmd() {
    return m_Cmd;
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
    result.append(",cmd=" + m_Cmd);
    result.append("]");

    return result.toString();
  }
}
