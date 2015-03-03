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
 * FlowSetupChangeEvent.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.event;

import java.util.EventObject;

import adams.flow.setup.FlowSetup;

/**
 * Event that gets sent by a FlowSetup when the execution of a flow has 
 * started, finished, etc.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowSetupStateEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -113405042251910190L;

  /**
   * The type of event.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static enum Type {
    /** flow was modified. */
    MODIFIED,
    /** flow started. */
    STARTED,
    /** successfully finished. */
    FINISHED,
    /** an error occurred. */
    ERROR;
  }
  
  /** the type of event. */
  protected Type m_Type;
  
  /**
   * Initializes the event.
   * 
   * @param source	the setup that triggered the event
   * @param type	the type of event
   */
  public FlowSetupStateEvent(FlowSetup source, Type type) {
    super(source);
    
    m_Type = type;
  }
  
  /**
   * Returns the setup that triggered the event.
   * 
   * @return		the setup
   */
  public FlowSetup getFlowSetup() {
    return (FlowSetup) getSource();
  }
  
  /**
   * Returns the type of event.
   * 
   * @return		the type
   */
  public Type getType() {
    return m_Type;
  }
}
