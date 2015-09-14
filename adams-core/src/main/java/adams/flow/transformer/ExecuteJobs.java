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
 * Deserialize.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Pausable;
import adams.event.FlowPauseStateEvent;
import adams.event.FlowPauseStateEvent.Type;
import adams.event.FlowPauseStateListener;
import adams.multiprocess.JobRunner;

/**
 <!-- globalinfo-start -->
 * Executes the incoming jobs.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.multiprocess.JobRunner<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.multiprocess.JobRunner<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ExecuteJobs
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExecuteJobs
  extends AbstractTransformer
  implements Pausable, FlowPauseStateListener {

  /** for serialization. */
  private static final long serialVersionUID = 7491100983182267771L;

  /** the job runner. */
  protected JobRunner m_JobRunner;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the incoming jobs.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.multiprocess.JobRunner.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{JobRunner.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.multiprocess.JobRunner.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{JobRunner.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    try {
      m_JobRunner = (JobRunner) m_InputToken.getPayload();
      m_JobRunner.start();
      m_JobRunner.stop();
    }
    catch (Exception e) {
      result = handleException("Failed to execute jobs!", e);
    }

    return result;
  }

  /**
   * Gets called when the pause state of the flow changes.
   *
   * @param e		the event
   */
  public void flowPauseStateChanged(FlowPauseStateEvent e) {
    if (e.getType() == Type.PAUSED)
      pauseExecution();
    else
      resumeExecution();
  }

  /**
   * Pauses the execution.
   */
  public void pauseExecution() {
    if (m_JobRunner != null)
      m_JobRunner.pauseExecution();
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  public boolean isPaused() {
    if (m_JobRunner != null)
      return m_JobRunner.isPaused();
    else
      return false;
  }

  /**
   * Resumes the execution.
   */
  public void resumeExecution() {
    if (m_JobRunner != null)
      m_JobRunner.resumeExecution();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();

    if (m_JobRunner != null)
      m_JobRunner.terminate();
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    m_JobRunner = null;
  }
}
