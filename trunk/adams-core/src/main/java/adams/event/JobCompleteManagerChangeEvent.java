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
 * JobCompleteManagerChangeEvent.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.event;

import adams.multiprocess.JobCompleteManager;
import adams.multiprocess.JobCompleteManager.JobCompleteInformation;

import java.util.EventObject;
import java.util.Vector;

/**
 * An event sent by the JobCompleteManager in case the list of JobEvents
 * being held has changed.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JobCompleteManagerChangeEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -1057261141916098683L;

  /**
   * Enumeration of possible event types.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Type {
    /** the list got reset. */
    RESET,
    /** the list got appended. */
    APPEND;
  }
  
  /** the type of event. */
  protected Type m_Type;
  
  /**
   * Initializes the event.
   * 
   * @param source	the manager that sent this event
   * @param type	the type of event
   */
  public JobCompleteManagerChangeEvent(Object source, Type type) {
    super(source);
    
    m_Type = type;
  }
  
  /**
   * Returns the type of the event.
   * 
   * @return		the type of event
   */
  public Type getType() {
    return m_Type;
  }
  
  /**
   * Returns the manager that sent this event.
   * 
   * @return		the manager instance
   */
  public JobCompleteManager getManager() {
    return (JobCompleteManager) getSource();
  }
  
  /**
   * Returns the informations currently hold by the manager.
   * 
   * @return		the informations
   */
  public Vector<JobCompleteInformation> getInformations() {
    return getManager().getInformations();
  }
  
  /**
   * Returns a string representation of the event.
   * 
   * @return		a string representation
   */
  public String toString() {
    return getClass().getName() + ": " + getType();
  }
}
