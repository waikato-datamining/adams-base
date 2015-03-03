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

/**
 * RunnableWithLogging.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.Stoppable;
import adams.core.Utils;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingLevelHandler;
import adams.core.logging.LoggingObject;

/**
 * Extended {@link Runnable} class that offers logging and can be stopped.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class RunnableWithLogging
  extends LoggingObject
  implements Runnable, Stoppable, LoggingLevelHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5192907871210498502L;

  /** whether the runnable has been stopped. */
  protected boolean m_Stopped;

  /** whether the runnable is still running. */
  protected boolean m_Running;

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public synchronized void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel = value;
    m_Logger       = null;
  }

  /**
   * Hook method before the run is started.
   * <p/>
   * Default implementation does nothing.
   */
  protected void preRun() {
  }

  /**
   * Performs the actual execution.
   */
  protected abstract void doRun();

  /**
   * Hook method after the run finished.
   * <p/>
   * Default implementation does nothing.
   */
  protected void postRun() {
  }
  
  /**
   * Starts the execution.
   */
  @Override
  public void run() {
    m_Stopped = false;

    preRun();
    
    m_Running = true;

    if (isLoggingEnabled())
      getLogger().fine("Running...");
    
    try {
      doRun();
    }
    catch (Throwable t) {
      Utils.handleException(this, "Exception occurred on run!", t);
    }
    
    if (isLoggingEnabled())
      getLogger().fine("Finished");
    
    m_Running = false;
    
    postRun();
  }
  
  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
    if (isLoggingEnabled())
      getLogger().fine("Stopped");
  }
  
  /**
   * Returns whether the runnable has been stopped.
   * 
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }
  
  /**
   * Returns whether the runnable is still running.
   * 
   * @return		true if still running
   */
  public boolean isRunning() {
    return m_Running;
  }
}
