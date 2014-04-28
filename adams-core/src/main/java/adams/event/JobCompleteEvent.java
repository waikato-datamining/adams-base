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
 * JobCompleteEvent.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.event;

import adams.multiprocess.Job;
import adams.multiprocess.JobResult;

import java.util.EventObject;

/**
 * Event object that gets sent after a job finished.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Job
 */
public class JobCompleteEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -5448976051820797751L;

  /** the job that finished. */
  protected Job m_Job;
  
  /** the result of the job. */
  protected JobResult m_Result;
  
  /** a payload object (can be arbitrary object). */
  protected Object m_Payload;
  
  /**
   * Initializes the event.
   * 
   * @param source	the object that triggered the event
   * @param job		the job that got finished
   * @param result	the result of the job
   */
  public JobCompleteEvent(Object source, Job job, JobResult result) {
    this(source, job, result, null);
  }
  
  /**
   * Initializes the event with an additional payload object.
   * 
   * @param source	the object that triggered the event
   * @param job		the job that got finished
   * @param result	the result of the job
   * @param payload	an additional object
   */
  public JobCompleteEvent(Object source, Job job, JobResult result, Object payload) {
    super(source);
    
    m_Job     = job;
    m_Result  = result;
    m_Payload = payload;
  }
  
  /**
   * Returns the job that finished.
   * 
   * @return		the job
   */
  public Job getJob() {
    return m_Job;
  }
  
  /**
   * Returns the result of the job.
   * 
   * @return		the result
   */
  public JobResult getResult() {
    return m_Result;
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
