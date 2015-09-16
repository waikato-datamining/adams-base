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
 * AbstractJobRunner.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.multiprocess;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for jobrunner classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of job to handle
 */
public abstract class AbstractJobRunner<T extends Job>
  extends AbstractOptionHandler
  implements JobRunner<T> {

  private static final long serialVersionUID = -4255112994297592401L;

  /** whether the jobs are being executed. */
  protected boolean m_Running;

  /** whether the execution is paused. */
  protected boolean m_Paused;

  /**
   * Before actual start up.
   *
   * @return		null if successful, otherwise error message
   */
  protected String preStart() {
    m_Running = false;
    m_Paused  = false;
    return null;
  }

  /**
   * Performing actual start up.
   * Only gets executed if {@link #preStart()} was successful.
   *
   * @return		null if successful, otherwise error message
   * @see		#preStart()
   */
  protected abstract String doStart();

  /**
   * After actual start up.
   * Only gets executed if {@link #preStart()} was successful.
   * <br>
   * Default implementation does nothing
   *
   * @return		null if successful, otherwise error message
   */
  protected String postStart() {
    return null;
  }

  /**
   * Starts the execution of jobs.
   */
  public void start() {
    String	msg;

    msg = preStart();
    if (msg == null) {
      msg = doStart();
      if (msg != null)
	getLogger().severe("doStart failed (skipping rest): " + msg);

      msg = postStart();
      if (msg != null)
	getLogger().severe("postStart failed: " + msg);
    }
    else {
      getLogger().severe("preStart failed: " + msg);
    }
  }

  /**
   * Before actual stop.
   * <br>
   * Default implementation does nothing
   *
   * @return		null if successful, otherwise error message
   */
  protected String preStop() {
    return null;
  }

  /**
   * Performing actual stop.
   *
   * @return		null if successful, otherwise error message
   */
  protected abstract String doStop();

  /**
   * After actual stop.
   *
   * @return		null if successful, otherwise error message
   */
  protected String postStop() {
    m_Running = false;
    m_Paused  = false;
    return null;
  }

  /**
   * Stops the execution after all currently queued jobs have been executed.
   */
  public void stop() {
    String	msg;

    msg = preStop();
    if (msg != null)
      getLogger().severe("preStop failed: " + msg);

    msg = doStop();
    if (msg != null)
      getLogger().severe("doStop failed: " + msg);

    msg = postStop();
    if (msg != null)
      getLogger().severe("postStop failed: " + msg);
  }

  /**
   * Before actual terminate up.
   * <br>
   * Default implementation does nothing
   *
   * @return		null if successful, otherwise error message
   */
  protected String preTerminate() {
    return null;
  }

  /**
   * Performing actual terminate up.
   *
   * @return		null if successful, otherwise error message
   */
  protected abstract String doTerminate();

  /**
   * After actual terminate up.
   *
   * @return		null if successful, otherwise error message
   */
  protected String postTerminate() {
    m_Running = false;
    m_Paused  = false;
    return null;
  }

  /**
   * Stops the execution immediately.
   */
  public void terminate() {
    String	msg;

    msg = preTerminate();
    if (msg != null)
      getLogger().severe("preTerminate failed: " + msg);

    msg = doTerminate();
    if (msg != null)
      getLogger().severe("doTerminate failed: " + msg);

    msg = postTerminate();
    if (msg != null)
      getLogger().severe("postTerminate failed: " + msg);
  }

  /**
   * Returns whether the job are being executed.
   *
   * @return		true if jobs are being executed
   */
  public boolean isRunning() {
    return m_Running;
  }

  /**
   * Pauses the execution.
   */
  public void pauseExecution() {
    if (m_Running)
      m_Paused = true;
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  public boolean isPaused() {
    return m_Running && m_Paused;
  }

  /**
   * Resumes the execution.
   */
  public void resumeExecution() {
    m_Paused = false;
  }
}
