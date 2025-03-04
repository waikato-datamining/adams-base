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
 * WekaTrainAssociator.java
 * Copyright (C) 2017-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaAssociatorContainer;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.FlowContextUtils;
import adams.flow.core.Token;
import adams.flow.source.WekaAssociatorSetup;
import adams.flow.standalone.JobRunnerInstance;
import adams.multiprocess.AbstractJob;
import adams.multiprocess.JobRunnerSupporter;
import weka.associations.AssociationRulesProducer;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Trains an associator based on the incoming dataset and outputs the built associator alongside the training header and rules (in a model container)..
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaAssociatorContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaAssociatorContainer: Model, Header, Dataset, Rules
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaTrainAssociator
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
 * <pre>-associator &lt;adams.flow.core.CallableActorReference&gt; (property: associator)
 * &nbsp;&nbsp;&nbsp;The Weka associator to train on the input data.
 * &nbsp;&nbsp;&nbsp;default: WekaAssociatorSetup
 * </pre>
 *
 * <pre>-prefer-jobrunner &lt;boolean&gt; (property: preferJobRunner)
 * &nbsp;&nbsp;&nbsp;If enabled, tries to offload the processing onto a adams.flow.standalone.JobRunnerInstance.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaTrainAssociator
  extends AbstractTransformer
  implements JobRunnerSupporter {

  public static class TrainJob
    extends AbstractJob {

    private static final long serialVersionUID = 6406892820872772446L;

    /** the associator to train. */
    protected weka.associations.Associator m_Associator;

    /** the data to use for training. */
    protected Instances m_Data;

    /**
     * Initializes the job.
     *
     * @param associator	the associator to train
     * @param data		the training data
     */
    public TrainJob(weka.associations.Associator associator, Instances data) {
      super();
      m_Associator = associator;
      m_Data       = data;
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_Associator == null)
	return "No associator to train!";
      if (m_Data == null)
	return "No training data!";
      return null;
    }

    /**
     * Does the actual execution of the job.
     *
     * @throws Exception if fails to execute job
     */
    @Override
    protected void process() throws Exception {
      m_Associator.buildAssociations(m_Data);
    }

    /**
     * Checks whether all post-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String postProcessCheck() {
      return null;
    }

    /**
     * Returns a string representation of this job.
     *
     * @return		the job as string
     */
    @Override
    public String toString() {
      return OptionUtils.getCommandLine(m_Associator) + "\n" + m_Data.relationName();
    }

    /**
     * Cleans up data structures, frees up memory.
     * Removes dependencies and job parameters.
     */
    @Override
    public void cleanUp() {
      m_Associator = null;
      m_Data       = null;
      super.cleanUp();
    }
  }

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the name of the callable weka associator. */
  protected CallableActorReference m_Associator;

  /** the actual weka associator. */
  protected weka.associations.Associator m_ActualAssociator;

  /** whether to offload training into a JobRunnerInstance. */
  protected boolean m_PreferJobRunner;

  /** the JobRunnerInstance to use. */
  protected transient JobRunnerInstance m_JobRunnerInstance;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Trains an associator based on the incoming dataset and outputs the "
	+ "built associator alongside the training header and rules (in a model container)..";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "associator", "associator",
      new CallableActorReference(WekaAssociatorSetup.class.getSimpleName()));

    m_OptionManager.add(
      "prefer-jobrunner", "preferJobRunner",
      false);
  }

  /**
   * Sets the name of the callable associator to use.
   *
   * @param value	the name
   */
  public void setAssociator(CallableActorReference value) {
    m_Associator = value;
    reset();
  }

  /**
   * Returns the name of the callable associator in use.
   *
   * @return		the name
   */
  public CallableActorReference getAssociator() {
    return m_Associator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String associatorTipText() {
    return "The Weka associator to train on the input data.";
  }

  /**
   * Sets whether to offload processing to a JobRunner instance if available.
   *
   * @param value	if true try to find/use a JobRunner instance
   */
  public void setPreferJobRunner(boolean value) {
    m_PreferJobRunner = value;
    reset();
  }

  /**
   * Returns whether to offload processing to a JobRunner instance if available.
   *
   * @return		if true try to find/use a JobRunner instance
   */
  public boolean getPreferJobRunner() {
    return m_PreferJobRunner;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String preferJobRunnerTipText() {
    return "If enabled, tries to offload the processing onto a " + Utils.classToString(JobRunnerInstance.class) + ".";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String> 	options;

    result = QuickInfoHelper.toString(this, "associator", m_Associator);
    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "preferJobRunner", m_PreferJobRunner, "jobrunner"));
    result += QuickInfoHelper.flatten(options);

    return result;
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
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaAssociatorContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaAssociatorContainer.class};
  }

  /**
   * Returns an instance of the callable associator.
   *
   * @return		the associator
   * @throws Exception  if fails to obtain associator
   */
  protected weka.associations.Associator getAssociatorInstance() throws Exception {
    weka.associations.Associator	result;
    MessageCollection 			errors;

    errors = new MessageCollection();
    result = (weka.associations.Associator) CallableActorHelper.getSetup(weka.associations.Associator.class, m_Associator, this, errors);
    if (result == null) {
      if (errors.isEmpty())
	throw new IllegalStateException("Failed to obtain associator from '" + m_Associator + "'!");
      else
	throw new IllegalStateException("Failed to obtain associator from '" + m_Associator + "':\n" + errors);
    }
    else {
      if (FlowContextUtils.isHandler(result))
	FlowContextUtils.update(result, this);
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

    result = super.setUp();

    if (result == null) {
      if (m_PreferJobRunner)
	m_JobRunnerInstance = JobRunnerInstance.locate(this, true);
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
    String				result;
    Instances				data;
    weka.associations.Associator	cls;
    TrainJob 				job;

    result = null;

    try {
      if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instances)) {
	cls  = getAssociatorInstance();
	data = (Instances) m_InputToken.getPayload();
	if (m_JobRunnerInstance != null) {
	  job    = new TrainJob(cls, data);
	  result = m_JobRunnerInstance.executeJob(job);
	  job.cleanUp();
	  if (result != null)
	    throw new Exception(result);
	}
	else {
	  cls.buildAssociations(data);
	}
	if ((cls instanceof AssociationRulesProducer) && ((AssociationRulesProducer) cls).canProduceRules())
	  m_OutputToken = new Token(new WekaAssociatorContainer(cls, new Instances(data, 0), data, ((AssociationRulesProducer) cls).getAssociationRules().getRules()));
	else
	  m_OutputToken = new Token(new WekaAssociatorContainer(cls, new Instances(data, 0), data));
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result        = handleException("Failed to process data:", e);
    }

    return result;
  }
}
