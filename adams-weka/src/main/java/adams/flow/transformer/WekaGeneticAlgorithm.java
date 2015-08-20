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
 * WekaGeneticAlgorithm.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Pausable;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.event.FitnessChangeEvent;
import adams.event.FitnessChangeListener;
import adams.event.FlowPauseStateEvent;
import adams.event.FlowPauseStateListener;
import adams.flow.container.WekaGeneticAlgorithmContainer;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorUser;
import adams.flow.core.InputConsumer;
import adams.flow.core.PauseStateHandler;
import adams.flow.core.PauseStateManager;
import adams.flow.core.Token;
import adams.genetic.AbstractClassifierBasedGeneticAlgorithm;
import adams.genetic.AbstractGeneticAlgorithm.GeneticAlgorithmJob;
import adams.genetic.DarkLord;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.HashSet;
import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Applies the genetic algorithm to the incoming dataset.<br>
 * Forwards the best setup after the algorithm finishes.<br>
 * A callable sink can be specified for receiving intermediate performance results.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaGeneticAlgorithmContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaGeneticAlgorithmContainer: Setup, Measure, Fitness
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
 * &nbsp;&nbsp;&nbsp;default: WekaGeneticAlgorithm
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
 * <pre>-algorithm &lt;adams.genetic.AbstractClassifierBasedGeneticAlgorithm&gt; (property: algorithm)
 * &nbsp;&nbsp;&nbsp;The genetic algorithm to apply to the dataset.
 * &nbsp;&nbsp;&nbsp;default: adams.genetic.DarkLord -stopping-criterion adams.genetic.stopping.MaxIterations -classifier weka.classifiers.rules.ZeroR
 * </pre>
 * 
 * <pre>-callable &lt;adams.flow.core.CallableActorReference&gt; (property: callableName)
 * &nbsp;&nbsp;&nbsp;The name of the callable sink to forward to the adams.flow.container.WekaGeneticAlgorithmContainer 
 * &nbsp;&nbsp;&nbsp;containers.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 * <pre>-optional &lt;boolean&gt; (property: optional)
 * &nbsp;&nbsp;&nbsp;If enabled, then the callable sink is optional, ie no error is raised if 
 * &nbsp;&nbsp;&nbsp;not found, merely ignored.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaGeneticAlgorithm
  extends AbstractTransformer
  implements FitnessChangeListener, CallableActorUser, FlowPauseStateListener, Pausable {

  /** for serialization. */
  private static final long serialVersionUID = 5071747277597147724L;

  /** the key for backing up the callable actor. */
  public final static String BACKUP_CALLABLEACTOR = "callable actor";

  /** the key for backing up the configured state. */
  public final static String BACKUP_CONFIGURED = "configured";

  /** whether to check the header. */
  protected AbstractClassifierBasedGeneticAlgorithm m_Algorithm;

  /** the actual algorithm in use. */
  protected transient AbstractClassifierBasedGeneticAlgorithm m_ActualAlgorithm;

  /** the callable name. */
  protected CallableActorReference m_CallableName;

  /** the callable actor. */
  protected AbstractActor m_CallableActor;

  /** whether the callable actor has been configured. */
  protected boolean m_Configured;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** whether the callable actor is optional. */
  protected boolean m_Optional;

  /** the pause state manager. */
  protected PauseStateManager m_PauseStateManager;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Applies the genetic algorithm to the incoming dataset.\n"
      + "Forwards the best setup after the algorithm finishes.\n"
      + "A callable sink can be specified for receiving intermediate performance results.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "algorithm", "algorithm",
      new DarkLord());

    m_OptionManager.add(
      "callable", "callableName",
      new CallableActorReference("unknown"));

    m_OptionManager.add(
      "optional", "optional",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CallableActor = null;
    m_Configured    = false;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "algorithm", m_Algorithm, "algorithm: ");
    result += QuickInfoHelper.toString(this, "callableName", m_CallableName, ", callable: ");
    result += QuickInfoHelper.toString(this, "optional", m_Optional, "optional", ", ");

    return result;
  }

  /**
   * Sets the genetic algorithm to apply to the dataset.
   *
   * @param value	the algorithm
   */
  public void setAlgorithm(AbstractClassifierBasedGeneticAlgorithm value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the genetic algorithm to apply to the dataset.
   *
   * @return		the algorithm
   */
  public AbstractClassifierBasedGeneticAlgorithm getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The genetic algorithm to apply to the dataset.";
  }

  /**
   * Sets the name of the callable sink to use.
   *
   * @param value 	the callable name
   */
  public void setCallableName(CallableActorReference value) {
    m_CallableName = value;
    reset();
  }

  /**
   * Returns the name of the callable sink in use.
   *
   * @return 		the callable name
   */
  public CallableActorReference getCallableName() {
    return m_CallableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String callableNameTipText() {
    return "The name of the callable sink to forward to the " + WekaGeneticAlgorithmContainer.class.getName() + " containers.";
  }

  /**
   * Sets whether the callable sink is optional.
   *
   * @param value 	true if optional
   */
  public void setOptional(boolean value) {
    m_Optional = value;
    reset();
  }

  /**
   * Returns whether the callable sink is optional.
   *
   * @return 		true if optional
   */
  public boolean getOptional() {
    return m_Optional;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optionalTipText() {
    return
	"If enabled, then the callable sink is optional, ie no error is "
	+ "raised if not found, merely ignored.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaGeneticAlgorithmContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaGeneticAlgorithmContainer.class};
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected AbstractActor findCallableActor() {
    AbstractActor	result;

    result = m_Helper.findCallableActorRecursive(this, getCallableName());

    if (result != null) {
      if (!(ActorUtils.isSink(result))) {
	getLogger().severe("Callable actor '" + result.getFullName() + "' is not a sink" + (m_CallableActor == null ? "!" : m_CallableActor.getClass().getName()));
	result = null;
      }
    }

    return result;
  }

  /**
   * Checks whether a reference to the callable actor is currently available.
   *
   * @return		true if a reference is available
   * @see		#getCallableActor()
   */
  public boolean hasCallableActor() {
    return (m_CallableActor != null);
  }

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  @Override
  public AbstractActor getCallableActor() {
    return m_CallableActor;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_CALLABLEACTOR);
    pruneBackup(BACKUP_CONFIGURED);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_CallableActor != null)
      result.put(BACKUP_CALLABLEACTOR, m_CallableActor);

    result.put(BACKUP_CONFIGURED, m_Configured);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    super.restoreState(state);

    if (state.containsKey(BACKUP_CALLABLEACTOR)) {
      m_CallableActor = (AbstractActor) state.get(BACKUP_CALLABLEACTOR);
      state.remove(BACKUP_CALLABLEACTOR);
    }

    if (state.containsKey(BACKUP_CONFIGURED)) {
      m_Configured = (Boolean) state.get(BACKUP_CONFIGURED);
      state.remove(BACKUP_CONFIGURED);
    }
  }

  /**
   * Configures the callable actor.
   *
   * @return		null if successful, otherwise error message
   */
  protected String setUpCallableActor() {
    String		result;
    HashSet<String> variables;

    result = null;

    m_CallableActor = findCallableActor();
    m_Configured    = true;
    if (m_CallableActor == null) {
      if (!m_Optional)
	result = "Couldn't find callable actor '" + getCallableName() + "'!";
      else
	getLogger().info("Callable actor '" + getCallableName() + "' not found, ignoring.");
    }
    else {
      variables = findVariables(m_CallableActor);
      m_DetectedVariables.addAll(variables);
      if (m_DetectedVariables.size() > 0)
	getVariables().addVariableChangeListener(this);
      if (getErrorHandler() != this)
	ActorUtils.updateErrorHandler(m_CallableActor, getErrorHandler(), isLoggingEnabled());
    }

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    String	variable;

    result = super.setUp();

    if (result == null) {
      // do we have to wait till execution time because of attached variable?
      variable = getOptionManager().getVariableForProperty("callableName");
      if (variable == null)
	result = setUpCallableActor();
    }

    if (getRoot() instanceof PauseStateHandler) {
      m_PauseStateManager = ((PauseStateHandler) getRoot()).getPauseStateManager();
      if (m_PauseStateManager != null)
	m_PauseStateManager.addListener(this);
    }
    else {
      m_PauseStateManager = null;
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String					result;
    Instances					data;
    WekaGeneticAlgorithmContainer		cont;

    result = null;

    data              = (Instances) m_InputToken.getPayload();
    cont              = null;
    m_ActualAlgorithm = (AbstractClassifierBasedGeneticAlgorithm) m_Algorithm.shallowCopy(true);
    m_ActualAlgorithm.addFitnessChangeListener(this);
    try {
      m_ActualAlgorithm.setInstances(data);
      m_ActualAlgorithm.run();
      if (m_ActualAlgorithm.isStopped())
	result = "Genetic algorithm stopped!";
      else
	cont = new WekaGeneticAlgorithmContainer(
	  (Classifier) m_ActualAlgorithm.getCurrentSetup(),
	  m_ActualAlgorithm.getMeasure(),
	  m_ActualAlgorithm.getCurrentFitness(),
          GeneticAlgorithmJob.weightsToString(m_ActualAlgorithm.getCurrentWeights()),
          m_ActualAlgorithm.getCurrentWeights());
      m_ActualAlgorithm.removeFitnessChangeListener(this);
      m_ActualAlgorithm = null;
    }
    catch (Exception e) {
      result = handleException("Failed to run genetic algorithm!", e);
    }

    if (cont != null)
      m_OutputToken = new Token(cont);

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_ActualAlgorithm != null) {
      m_ActualAlgorithm.stopExecution();
      while (m_ActualAlgorithm.isRunning()) {
	Utils.wait(this, 1000, 100);
      }
      m_ActualAlgorithm.removeFitnessChangeListener(this);
    }
    if (m_PauseStateManager != null) {
      if (m_PauseStateManager.isPaused())
	m_PauseStateManager.resume(this);
    }
    super.stopExecution();
  }

  /**
   * Gets called when the fitness of the genetic algorithm changed.
   *
   * @param e		the event
   */
  @Override
  public void fitnessChanged(FitnessChangeEvent e) {
    String				result;
    WekaGeneticAlgorithmContainer	cont;

    result = null;

    if (!m_Configured)
      result = setUpCallableActor();

    if (result == null) {
      if (m_CallableActor != null) {
	cont = new WekaGeneticAlgorithmContainer(
	  (Classifier) e.getSetup(),
	  ((AbstractClassifierBasedGeneticAlgorithm) e.getGeneticAlgorithm()).getMeasure(),
	  e.getFitness(),
          GeneticAlgorithmJob.weightsToString(e.getWeights()),
          e.getWeights());
	if (!m_CallableActor.getSkip() && !m_CallableActor.isStopped()) {
	  synchronized(m_CallableActor) {
	    if (isLoggingEnabled())
	      getLogger().info("Executing callable sink - start: " + m_CallableActor);
	    ((InputConsumer) m_CallableActor).input(new Token(cont));
	    result = m_CallableActor.execute();
	    if (isLoggingEnabled())
	      getLogger().info("Executing callable sink - end: " + result);
	  }
	}
      }
    }
  }

  /**
   * Pauses the execution.
   */
  @Override
  public void pauseExecution() {
    if (m_PauseStateManager != null)
      m_PauseStateManager.pause(this);
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  @Override
  public boolean isPaused() {
    if (m_PauseStateManager != null)
      return m_PauseStateManager.isPaused();
    else
      return false;
  }

  /**
   * Resumes the execution.
   */
  @Override
  public void resumeExecution() {
    if (m_PauseStateManager != null)
      m_PauseStateManager.resume(this);
  }

  /**
   * Gets called when the pause state of the flow changes.
   *
   * @param e		the event
   */
  @Override
  public void flowPauseStateChanged(FlowPauseStateEvent e) {
    if (m_ActualAlgorithm != null)
      m_ActualAlgorithm.pauseExecution();
  }
}
