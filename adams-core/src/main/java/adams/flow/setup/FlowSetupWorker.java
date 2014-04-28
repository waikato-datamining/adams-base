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
 * FlowSetupWorker.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.setup;

import javax.swing.SwingWorker;

import adams.core.CleanUpHandler;
import adams.core.Pausable;
import adams.core.Stoppable;
import adams.event.FlowSetupStateEvent;
import adams.flow.FlowRunner;
import adams.flow.core.AbstractActor;

/**
 * A specialized SwingWorker class for executing FlowSetups.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowSetupWorker
  extends SwingWorker
  implements CleanUpHandler, Stoppable, Pausable {

  /** the underlying flowsetup. */
  protected FlowSetup m_Setup;

  /** for executing the flow. */
  protected FlowRunner m_FlowRunner;

  /** the last actor that was run. */
  protected AbstractActor m_LastActor;

  /**
   * Initializes the worker.
   *
   * @param setup	the underlying flowsetup
   */
  public FlowSetupWorker(FlowSetup setup) {
    super();

    m_Setup      = setup;
    m_FlowRunner = null;
    m_LastActor  = null;
  }

  /**
   * Returns the underlying flow setup.
   *
   * @return		the setup
   */
  public FlowSetup getSetup() {
    return m_Setup;
  }

  /**
   * Returns the actor that was executed.
   *
   * @return		the actor
   */
  public AbstractActor getLastActor() {
    return m_LastActor;
  }

  /**
   * Runs the flow in the background.
   *
   * @throws Exception	if something goes wrong
   * @return		the last error or null if none
   */
  @Override
  protected Object doInBackground() throws Exception {
    m_LastActor  = null;
    m_FlowRunner = new FlowRunner();
    m_FlowRunner.setInput(m_Setup.getFile());
    m_FlowRunner.setHeadless(m_Setup.isHeadless());
    m_Setup.notifyFlowSetupStateChangeListeners(
	new FlowSetupStateEvent(m_Setup, FlowSetupStateEvent.Type.STARTED));
    String lastError = m_FlowRunner.execute();
    m_Setup.setLastError(lastError);
    return lastError;
  }

  /**
   * After executing the flow.
   */
  @Override
  protected void done() {
    m_LastActor = m_FlowRunner.getLastActor();

    if (m_Setup.hasLastError())
      m_Setup.notifyFlowSetupStateChangeListeners(
	  new FlowSetupStateEvent(m_Setup, FlowSetupStateEvent.Type.ERROR));
    else
      m_Setup.notifyFlowSetupStateChangeListeners(
	  new FlowSetupStateEvent(m_Setup, FlowSetupStateEvent.Type.FINISHED));

    m_FlowRunner.setInput(null);
    m_FlowRunner = null;
    m_Setup.finish();

    super.done();
  }

  /**
   * Pauses the execution.
   */
  @Override
  public void pauseExecution() {
    if (m_FlowRunner != null)
      m_FlowRunner.pauseExecution();
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  @Override
  public boolean isPaused() {
    if (m_FlowRunner != null)
      return m_FlowRunner.isPaused();
    else
      return false;
  }

  /**
   * Resumes the execution.
   */
  @Override
  public void resumeExecution() {
    if (m_FlowRunner != null)
      m_FlowRunner.resumeExecution();
  }

  /**
   * Stops the flow execution.
   */
  @Override
  public void stopExecution() {
    if (m_FlowRunner != null)
      m_FlowRunner.stopExecution();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_LastActor  = null;
    m_Setup      = null;
    m_FlowRunner = null;
  }
}
