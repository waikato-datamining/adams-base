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
 * ExecuteJobs.java
 * Copyright (C) 2013-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Pausable;
import adams.core.Performance;
import adams.core.QuickInfoHelper;
import adams.core.ThreadLimiter;
import adams.core.Utils;
import adams.event.FlowPauseStateEvent;
import adams.event.FlowPauseStateEvent.Type;
import adams.event.FlowPauseStateListener;
import adams.flow.core.Token;
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-update-num-threads &lt;boolean&gt; (property: updateNumThreads)
 * &nbsp;&nbsp;&nbsp;If enabled (and possible), the number of threads of the incoming jobrunner
 * &nbsp;&nbsp;&nbsp;get updated with the value specified here.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-num-threads &lt;int&gt; (property: numThreads)
 * &nbsp;&nbsp;&nbsp;The number of threads to use for parallel execution; &gt; 0: specific number
 * &nbsp;&nbsp;&nbsp;of cores to use (capped by actual number of cores available, 1 = sequential
 * &nbsp;&nbsp;&nbsp;execution); = 0: number of cores; &lt; 0: number of free cores (eg -2 means
 * &nbsp;&nbsp;&nbsp;2 free cores; minimum of one core is used)
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ExecuteJobs
  extends AbstractTransformer
  implements Pausable, FlowPauseStateListener, ThreadLimiter {

  /** for serialization. */
  private static final long serialVersionUID = 7491100983182267771L;

  /** the job runner. */
  protected JobRunner m_JobRunner;

  /** whether to update the number of threads to use (if possible). */
  protected boolean m_UpdateNumThreads;

  /** the number of threads to use for parallel execution. */
  protected int m_NumThreads;

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
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "update-num-threads", "updateNumThreads",
      false);

    m_OptionManager.add(
      "num-threads", "numThreads",
      -1);
  }

  /**
   * Sets whether to update the number of threads (if possible).
   *
   * @param value 	true if to update
   */
  public void setUpdateNumThreads(boolean value) {
    m_UpdateNumThreads = value;
    reset();
  }

  /**
   * Returns whether to update the number of threads (if possible).
   *
   * @return 		true if to update
   */
  public boolean getUpdateNumThreads() {
    return m_UpdateNumThreads;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String updateNumThreadsTipText() {
    return "If enabled (and possible), the number of threads of the incoming jobrunner get updated with the value specified here.";
  }

  /**
   * Sets the number of threads to use for parallel training of groups.
   *
   * @param value 	the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  public void setNumThreads(int value) {
    m_NumThreads = value;
    reset();
  }

  /**
   * Returns the number of threads to use for parallel training of groups.
   *
   * @return 		the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  public int getNumThreads() {
    return m_NumThreads;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numThreadsTipText() {
    return Performance.getNumThreadsHelp();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    if (m_UpdateNumThreads)
      return QuickInfoHelper.toString(this, "numThreads", "" + Performance.getNumThreadsQuickInfo(m_NumThreads));
    else
      return null;
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
      m_JobRunner.setFlowContext(this);
      if (m_UpdateNumThreads) {
	if (m_JobRunner instanceof ThreadLimiter)
	  ((ThreadLimiter) m_JobRunner).setNumThreads(m_NumThreads);
	else
	  getLogger().warning("JobRunner does not implement " + Utils.classToString(ThreadLimiter.class) + ", cannot update # of threads!");
      }
      m_JobRunner.start();
      m_JobRunner.stop();
      m_OutputToken = new Token(m_JobRunner);
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
