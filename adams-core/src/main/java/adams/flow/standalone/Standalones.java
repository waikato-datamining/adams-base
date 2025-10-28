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
 * Standalones.java
 * Copyright (C) 2009-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.MessageCollection;
import adams.core.Performance;
import adams.core.QuickInfoHelper;
import adams.core.ThreadLimiter;
import adams.core.Utils;
import adams.flow.control.AbstractDirector;
import adams.flow.control.MutableControlActor;
import adams.flow.control.SequentialDirector;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.multiprocess.PausableFixedThreadPoolExecutor;

/**
 <!-- globalinfo-start -->
 * Container for standalone actors.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Standalones
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-finish-before-stopping &lt;boolean&gt; (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stopping-timeout &lt;int&gt; (property: stoppingTimeout)
 * &nbsp;&nbsp;&nbsp;The timeout in milliseconds when waiting for actors to finish (&lt;= 0 for
 * &nbsp;&nbsp;&nbsp;infinity; see 'finishBeforeStopping').
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-stopping-warning-interval &lt;int&gt; (property: stoppingWarningInterval)
 * &nbsp;&nbsp;&nbsp;The interval in milliseconds to output logging warnings if the actors haven't
 * &nbsp;&nbsp;&nbsp;stopped yet (and no stopping timeout set); no warning if &lt;= 0.
 * &nbsp;&nbsp;&nbsp;default: 10000
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-actor &lt;adams.flow.core.Actor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The standalone actors grouped by this container.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-num-threads &lt;int&gt; (property: numThreads)
 * &nbsp;&nbsp;&nbsp;The number of threads to use for parallel execution; &gt; 0: specific number
 * &nbsp;&nbsp;&nbsp;of cores to use (capped by actual number of cores available, 1 = sequential
 * &nbsp;&nbsp;&nbsp;execution); = 0: number of cores; &lt; 0: number of free cores (eg -2 means
 * &nbsp;&nbsp;&nbsp;2 free cores; minimum of one core is used)
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Standalones
  extends MutableControlActor
  implements ThreadLimiter {

  /** for serialization. */
  private static final long serialVersionUID = 4755216459759839030L;

  public static class ParallelDirector
    extends AbstractDirector {

    private static final long serialVersionUID = 1648329941422576328L;

    /** the thread pool. */
    protected PausableFixedThreadPoolExecutor m_Pool;

    /**
     * Executes the group of actors.
     *
     * @return null if everything went smooth
     */
    @Override
    public String execute() {
      MessageCollection	errors;
      int		numThreads;

      errors     = new MessageCollection();
      numThreads = Performance.determineNumThreads(((Standalones) m_ControlActor).getNumThreads());
      m_Pool     = new PausableFixedThreadPoolExecutor(numThreads);
      for (Actor actor: ((Standalones) m_ControlActor).getActors()) {
	m_Pool.execute(() -> {
	  String msg = actor.execute();
	  if (msg != null)
	    errors.add(msg);
	});
      }
      m_Pool.shutdown();
      while (!m_Pool.isTerminated()) {
	Utils.wait(this, 1000, 50);
      }
      m_Pool = null;

      if (errors.isEmpty())
	return null;
      else
	return errors.toString();
    }

    /**
     * Pauses the execution.
     */
    @Override
    public void pauseExecution() {
      super.pause();
      if (m_Pool != null)
	m_Pool.pauseExecution();
    }
  }

  /** the number of threads to use for parallel execution. */
  protected int m_NumThreads;

  /**
   * Default constructor.
   */
  public Standalones() {
    super();
  }

  /**
   * Initializes with the specified name.
   *
   * @param name      the name to use
   */
  public Standalones(String name) {
    this();
    setName(name);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Container for standalone actors.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-threads", "numThreads",
      1);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String actorsTipText() {
    return "The standalone actors grouped by this container.";
  }

  /**
   * Sets the number of threads to use for executing the branches.
   *
   * @param value 	the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  @Override
  public void setNumThreads(int value) {
    m_NumThreads = value;
    reset();
  }

  /**
   * Returns the number of threads to use for executing the branches.
   *
   * @return 		the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  @Override
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
   * Returns an instance of a director.
   *
   * @return		the director
   */
  protected AbstractDirector newDirector() {
    if (m_NumThreads == 1)
      return new SequentialDirector();
    else
      return new ParallelDirector();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String result;

    result = QuickInfoHelper.toString(this, "numThreads", Performance.getNumThreadsQuickInfo(m_NumThreads, true));

    if (super.getQuickInfo() != null) {
      if (result == null)
	result = "";
      if (!result.isEmpty())
	result += ", ";
      result += super.getQuickInfo();
    }

    return result;
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo()
	     .allowStandalones(true)
	     .actorExecution(ActorExecution.UNDEFINED)
	     .forwardsInput(false);
  }

  /**
   * Checks whether names of sub-actors are unique.
   *
   * @return		null if everything is fine, otherwise the offending
   * 			connection
   */
  protected String checkScope() {
    int		i;
    String	result;

    result = null;

    for (i = 0; i < size(); i++) {
      if (getScopeHandler() != null)
	result = getScopeHandler().addCallableName(this, get(i));
      if (result != null)
	break;
    }

    return result;
  }

  /**
   * Checks whether all actors a standalones and whether actor names are unique.
   *
   * @return		null if everything is fine, otherwise the offending
   * 			connection
   * @see		#checkScope()
   */
  @Override
  public String check() {
    String	result;
    int		i;

    result = super.check();

    if (result == null) {
      for (i = 0; i < size(); i++) {
	if (!ActorUtils.isStandalone(get(i)))
	  result = "Actor '" + get(i).getFullName() + "' is not a standalone!";
	if (result != null)
	  break;
      }
    }

    if (result == null)
      result = checkScope();

    return result;
  }
}
