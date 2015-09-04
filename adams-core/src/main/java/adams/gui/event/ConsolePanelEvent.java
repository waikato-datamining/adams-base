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
 * ConsolePanelEvent.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import adams.core.logging.LoggingLevel;
import adams.gui.core.ConsolePanel;

import java.util.EventObject;

/**
 * Event that gets sent when the {@link ConsolePanel} receives new data.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class ConsolePanelEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -3065555728637944776L;

  /** the logging level. */
  protected LoggingLevel m_Level;

  /** the message. */
  protected String m_Message;
  
  /**
   * Initializes the object.
   * 
   * @param src		the source that triggered the event
   * @param level	the logging level
   * @param msg		the message
   */
  public ConsolePanelEvent(Object src, LoggingLevel level, String msg) {
    super(src);
    
    m_Level   = level;
    m_Message = msg;
  }
  
  /**
   * Returns the logging level.
   * 
   * @return		the level
   */
  public LoggingLevel getLevel() {
    return m_Level;
  }
  
  /**
   * Returns the message.
   * 
   * @return		the message
   */
  public String getMessage() {
    return m_Message;
  }
}
