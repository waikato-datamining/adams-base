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
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import java.util.EventObject;

import adams.gui.core.ConsolePanel;
import adams.gui.core.ConsolePanel.OutputType;

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

  /** the output type. */
  protected OutputType m_OutputType;

  /** the message. */
  protected String m_Message;
  
  /**
   * Initializes the object.
   * 
   * @param src		the source that triggered the event
   * @param outputType	the type of output
   * @param msg		the message
   */
  public ConsolePanelEvent(Object src, OutputType outputType, String msg) {
    super(src);
    
    m_OutputType = outputType;
    m_Message    = msg;
  }
  
  /**
   * Returns the output type.
   * 
   * @return		the type
   */
  public OutputType getOutputType() {
    return m_OutputType;
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
