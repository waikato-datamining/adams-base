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
 * AbstractDirector.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;


import adams.core.CleanUpHandler;
import adams.core.StoppableWithFeedback;
import adams.core.Utils;
import adams.core.Variables;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingObject;
import adams.event.FlowPauseStateEvent;
import adams.event.FlowPauseStateEvent.Type;
import adams.event.FlowPauseStateListener;

/**
 * Manages the execution of actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDirector
  extends LoggingObject
  implements CleanUpHandler, StoppableWithFeedback, FlowPauseStateListener {

  /** for serialization. */
  private static final long serialVersionUID = -1634725837304059804L;

  /** the control actor to execute. */
  protected AbstractControlActor m_ControlActor;

  /** the prefix for logging messages. */
  protected String m_LoggingPrefix;
  
  /** whether execution was stopped. */
  protected boolean m_Stopped;

  /** whether execution is in the process of being stopped. */
  protected boolean m_Stopping;

  /** whether the director has been paused. */
  protected boolean m_Paused;
  
  /** whether to flush the execution. */
  protected boolean m_Flushing;

  /**
   * Initializes the item.
   */
  public AbstractDirector() {
    super();

    initialize();
  }

  /**
   * Initializes the members.
   * <br><br>
   * Default implementation does nothing
   */
  protected void initialize() {
    m_LoggingPrefix = getClass().getSimpleName() + "/" + hashCode();
  }

  /**
   * Updates the prefix of the print objects.
   */
  public void updatePrefix() {
    if (m_ControlActor != null)
      m_LoggingPrefix = m_ControlActor.getFullName() + "." + getClass().getSimpleName() + "/" + hashCode();
    else
      m_LoggingPrefix = getClass().getSimpleName() + "/" + hashCode();
  }

  /**
   * Initializes the logger.
   */
  @Override
  protected void configureLogger() {
    m_Logger = LoggingHelper.getLogger(m_LoggingPrefix);
    m_Logger.setLevel(m_LoggingLevel.getLevel());
  }
  
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
   * Outputs the stacktrace along with the message on stderr and returns a 
   * combination of both of them as string.
   * 
   * @param msg		the message for the exception
   * @param t		the exception
   * @return		the full error message (message + stacktrace)
   */
  protected String handleException(String msg, Throwable t) {
    return Utils.handleException(this, msg, t, !hasControlActor() || m_ControlActor.getSilent());
  }

  /**
   * Sets the control actor to execute.
   *
   * @param value 	the control actor
   */
  public void setControlActor(AbstractControlActor value) {
    m_ControlActor = value;
    updatePrefix();
  }

  /**
   * Returns the control actor to execute.
   *
   * @return 		the control actor
   */
  public AbstractControlActor getControlActor() {
    return m_ControlActor;
  }

  /**
   * Returns whether a control actor is present.
   *
   * @return		true if available
   */
  public boolean hasControlActor() {
    return (m_ControlActor != null);
  }

  /**
   * Return the Variables instance used by the control actor.
   *
   * @return		the instance in use
   */
  protected Variables getVariables() {
    if (hasControlActor())
      return m_ControlActor.getVariables();
    else
      return new Variables();
  }

  /**
   * Executes the group of actors.
   *
   * @return		null if everything went smooth
   */
  public abstract String execute();

  /**
   * Pauses the execution.
   */
  public void pauseExecution() {
    m_Paused = true;
  }

  /**
   * The pause loop.
   */
  protected void pause() {
    // paused?
    while (isPaused() && !isStopped() && !isStopping()) {
      try {
	synchronized(this) {
	  wait(500);
	}
      }
      catch (Exception e) {
	// ignored
      }
    }
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  public boolean isPaused() {
    return m_Paused;
  }
  
  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    m_Flushing = true;
  }

  /**
   * Returns whether the execution is being flushed.
   *
   * @return		true if execution is being flushed
   */
  public boolean isFlushing() {
    return m_Flushing;
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    int		i;

    m_Stopping = true;

    getLogger().info("stop called");

    if (hasControlActor()) {
      for (i = m_ControlActor.size() - 1; i >= 0; i--) {
	if (!m_ControlActor.get(i).getSkip())
	  m_ControlActor.get(i).stopExecution();
      }
    }

    m_Stopped  = true;
    m_Stopping = false;
  }

  /**
   * Returns whether the execution was stopped.
   *
   * @return		true if execution was stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Returns whether the execution is being stopped.
   *
   * @return		true if the stop of the execution has been initiated
   */
  public boolean isStopping() {
    return m_Stopping;
  }

  /**
   * Checks whether the director has finished. Default implementation
   * always returns true.
   *
   * @return		true
   */
  public boolean isFinished() {
    return true;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
  }

  /**
   * Gets called when the pause state of the flow changes.
   * 
   * @param e		the event
   */
  public void flowPauseStateChanged(FlowPauseStateEvent e) {
    m_Paused = (e.getType() == Type.PAUSED);
  }

  /**
   * Returns a string representation of the director.
   *
   * @return		a string representation
   */
  @Override
  public String toString() {
    return getClass().getName() + "/" + hashCode() + ": " + (hasControlActor() ? "--" : m_ControlActor.getFullName());
  }
}
