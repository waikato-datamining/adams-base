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
 * JobListCompleteEvent.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.event;

import adams.multiprocess.JobList;

import java.util.EventObject;

/**
 * Event object that gets sent after all jobs in a queue have finished.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see JobList
 */
public class JobListCompleteEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -81425220563926478L;
  
  /** a payload object (can be arbitrary object). */
  protected Object m_Payload;
  
  /**
   * Initializes the event.
   * 
   * @param source	the object that triggered the event
   */
  public JobListCompleteEvent(Object source) {
    this(source, null);
  }
  
  /**
   * Initializes the event with an additional payload object.
   * 
   * @param source	the object that triggered the event
   * @param payload	an additional object
   */
  public JobListCompleteEvent(Object source, Object payload) {
    super(source);
    
    m_Payload = payload;
  }
  
  /**
   * Returns the queue that finished all its jobs.
   * 
   * @return		the queue
   */
  public JobList getQueue() {
    return (JobList) getSource();
  }
  
  /**
   * Checks whether there was any payload object provided.
   * 
   * @return		true if a payload object resides in this event
   * @see		#getPayload()
   */
  public boolean hasPayload() {
    return (m_Payload != null);
  }
  
  /**
   * Returns the payload object, if any.
   * 
   * @return		the payload object, can be null
   * @see		#hasPayload()
   */
  public Object getPayload() {
    return m_Payload;
  }
}
