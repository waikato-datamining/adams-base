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
 * JobResultManager.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.multiprocess;

import adams.core.Performance;
import adams.event.JobCompleteEvent;
import adams.event.JobCompleteListener;
import adams.event.JobCompleteManagerChangeEvent;
import adams.event.JobCompleteManagerChangeListener;
import adams.event.JobCompleteManagerChangeEvent.Type;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * A manager class that aggregates JobCompleteEvents.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JobCompleteManager
  implements JobCompleteListener {
  
  /**
   * A container class for information about a job and its result. Only retains
   * necessary information in order to avoid huge memory consumption.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class JobCompleteInformation {
    
    /** the job class. */
    protected String m_JobClass;
    
    /** the job details. */
    protected String m_JobDetails;
    
    /** the job result. */
    protected JobResult m_JobResult;
    
    /**
     * Initializes the container.
     * 
     * @param event	the event to get the data from
     */
    public JobCompleteInformation(JobCompleteEvent event) {
      super();
      
      m_JobClass   = event.getJob().getClass().getName();
      m_JobDetails = event.getJob().toString();
      m_JobResult  = event.getResult();
    }
    
    /**
     * Returns the classname of the job.
     * 
     * @return		the classname
     */
    public String getJobClass() {
      return m_JobClass;
    }
    
    /**
     * Returns the job details.
     * 
     * @return		the details
     */
    public String getJobDetails() {
      return m_JobDetails;
    }
    
    /**
     * Returns the job result.
     * 
     * @return		the result
     */
    public JobResult getJobResult() {
      return m_JobResult;
    }
  }
  
  /** for serialization. */
  private static final long serialVersionUID = 5145095894679891834L;

  /** the singleton. */
  protected static JobCompleteManager m_Singleton;

  /** the current list of Job informations. */
  protected Vector<JobCompleteInformation> m_Informations;
  
  /** the change listeners. */
  protected HashSet<JobCompleteManagerChangeListener> m_ChangeListeners;
  
  /**
   * Default constructor.
   */
  private JobCompleteManager() {
    super();
    
    m_Informations    = new Vector<JobCompleteInformation>();
    m_ChangeListeners = new HashSet<JobCompleteManagerChangeListener>();
  }

  /**
   * Clears the informations lists.
   */
  public synchronized void clear() {
    synchronized(m_Informations) {
      m_Informations.clear();
    }
    
    notifyChangeListeners(new JobCompleteManagerChangeEvent(this, Type.RESET));
  }
  
  /**
   * Returns the currently stored informations.
   * 
   * @return		the current informations
   */
  public synchronized Vector<JobCompleteInformation> getInformations() {
    Vector<JobCompleteInformation>	result;
    
    synchronized(m_Informations) {
      result = new Vector<JobCompleteInformation>();
      result.addAll(m_Informations);
    }
    
    return result;
  }
  
  /**
   * A job finished.
   * 
   * @param e		the event
   */
  public synchronized void jobCompleted(JobCompleteEvent e) {
    JobResult 				jr;
    boolean				reset;
    Vector<JobCompleteInformation>	informations;

    // do we only add failed jobs?
    if (Performance.getKeepOnlyFailedJobComplete() && e.getResult().getSuccess())
      return;
    
    reset = false;
    synchronized(m_Informations) {
      m_Informations.add(new JobCompleteInformation(e));

      // do we have to trim the list?
      if (m_Informations.size() > Performance.getMaxKeepJobComplete()) {
	reset        = true;
	informations = new Vector<JobCompleteInformation>(
	    		m_Informations.subList(
	    		    m_Informations.size() - Performance.getMinKeepJobComplete(), 
	    		    m_Informations.size() - 1));
	m_Informations = informations;
      }
      
      jr = e.getResult();
      if (!jr.getSuccess())
        System.err.println("Failed job:\n  " + e.getJob() + "\nwith message:\n  " + jr);
    }
    
    if (reset)
      notifyChangeListeners(new JobCompleteManagerChangeEvent(this, Type.RESET));
    else
      notifyChangeListeners(new JobCompleteManagerChangeEvent(this, Type.APPEND));
  }

  /**
   * Adds the listener.
   * 
   * @param l		the listener to add
   */
  public void addChangeListener(JobCompleteManagerChangeListener l) {
    synchronized(m_ChangeListeners) {
      m_ChangeListeners.add(l);
    }
  }

  /**
   * Removes the listener.
   * 
   * @param l		the listener to remove
   */
  public void removeChangeListener(JobCompleteManagerChangeListener l) {
    synchronized(m_ChangeListeners) {
      m_ChangeListeners.remove(l);
    }
  }
  
  /**
   * Notifies all listeners with the given event.
   * 
   * @param e		the event to send to the listeners
   */
  protected void notifyChangeListeners(JobCompleteManagerChangeEvent e) {
    Iterator<JobCompleteManagerChangeListener>	iter;
    
    synchronized(m_ChangeListeners) {
      iter = m_ChangeListeners.iterator();
      while (iter.hasNext())
	iter.next().stateChanged(e);
    }
  }
  
  /**
   * Returns the singleton of the manager.
   * 
   * @return		the singleton
   */
  public static synchronized JobCompleteManager getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new JobCompleteManager();
    
    return m_Singleton;
  }
}
