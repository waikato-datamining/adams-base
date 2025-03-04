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
 * WekaTrainClusterer.java
 * Copyright (C) 2012-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.FlowContextUtils;
import adams.flow.core.Token;
import adams.flow.source.WekaClustererSetup;
import adams.flow.standalone.JobRunnerInstance;
import adams.flow.transformer.wekaclusterer.AbstractClustererPostProcessor;
import adams.flow.transformer.wekaclusterer.PassThrough;
import adams.multiprocess.AbstractJob;
import adams.multiprocess.JobRunnerSupporter;
import weka.clusterers.Clusterer;
import weka.clusterers.UpdateableClusterer;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Trains a clusterer based on the incoming dataset and output the built clusterer alongside the training header (in a model container).<br>
 * Incremental training is performed, if the input are weka.core.Instance objects and the clusterer implements weka.clusterers.UpdateableClusterer.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaModelContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaModelContainer: Model, Header, Dataset
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
 * &nbsp;&nbsp;&nbsp;default: WekaTrainClusterer
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
 * <pre>-clusterer &lt;adams.flow.core.CallableActorReference&gt; (property: clusterer)
 * &nbsp;&nbsp;&nbsp;The Weka clusterer to build on the input data.
 * &nbsp;&nbsp;&nbsp;default: WekaClustererSetup
 * </pre>
 *
 * <pre>-post-processor &lt;adams.flow.transformer.wekaclusterer.AbstractClustererPostProcessor&gt; (property: postProcessor)
 * &nbsp;&nbsp;&nbsp;The post-processor to use on model containers.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.wekaclusterer.PassThrough
 * </pre>
 *
 * <pre>-prefer-jobrunner &lt;boolean&gt; (property: preferJobRunner)
 * &nbsp;&nbsp;&nbsp;If enabled, tries to offload the processing onto a adams.flow.standalone.JobRunnerInstance;
 * &nbsp;&nbsp;&nbsp; applies only to batch training.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaTrainClusterer
  extends AbstractTransformer
  implements JobRunnerSupporter {

  public static class BatchTrainJob
    extends AbstractJob {

    private static final long serialVersionUID = 6406892820872772446L;

    /** the clusterer to train. */
    protected Clusterer m_Clusterer;

    /** the data to use for training. */
    protected Instances m_Data;

    /** the postprocessor. */
    protected AbstractClustererPostProcessor m_PostProcessor;

    /** the generated model container. */
    protected WekaModelContainer m_Container;

    /**
     * Initializes the job.
     *
     * @param cls		the clusterer to train
     * @param data		the training data
     * @param postProcessor 	the post-processor to use
     */
    public BatchTrainJob(Clusterer cls, Instances data, AbstractClustererPostProcessor postProcessor) {
      super();
      m_Clusterer     = cls;
      m_Data          = data;
      m_PostProcessor = postProcessor;
      m_Container     = null;
    }

    /**
     * Returns the generated model container.
     *
     * @return		the container, null if none available
     */
    public WekaModelContainer getContainer() {
      return m_Container;
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_Clusterer == null)
	return "No clusterer to train!";
      if (m_Data == null)
	return "No training data!";
      if (m_Data.classIndex() > -1)
	return "Class attribute set!";
      return null;
    }

    /**
     * Does the actual execution of the job.
     *
     * @throws Exception if fails to execute job
     */
    @Override
    protected void process() throws Exception {
      m_Clusterer.buildClusterer(m_Data);
      m_Container = new WekaModelContainer(m_Clusterer, new Instances(m_Data, 0), m_Data);
      m_Container = m_PostProcessor.postProcess(m_Container);
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
      return OptionUtils.getCommandLine(m_Clusterer) + "\n" + m_Data.relationName();
    }

    /**
     * Cleans up data structures, frees up memory.
     * Removes dependencies and job parameters.
     */
    @Override
    public void cleanUp() {
      m_Clusterer = null;
      m_Data       = null;
      super.cleanUp();
    }
  }

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the key for storing the current incremental clusterer in the backup. */
  public final static String BACKUP_INCREMENTALCLUSTERER = "incremental clusterer";

  /** the name of the callable weka clusterer. */
  protected CallableActorReference m_Clusterer;

  /** the weka clusterer. */
  protected weka.clusterers.Clusterer m_ActualClusterer;

  /** the clusterer used when training incrementally. */
  protected weka.clusterers.Clusterer m_IncrementalClusterer;

  /** the post-processor. */
  protected AbstractClustererPostProcessor m_PostProcessor;

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
      "Trains a clusterer based on the incoming dataset and output the "
	+ "built clusterer alongside the training header (in a model container).\n"
	+ "Incremental training is performed, if the input are weka.core.Instance "
	+ "objects and the clusterer implements " + UpdateableClusterer.class.getName() + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "clusterer", "clusterer",
      new CallableActorReference(WekaClustererSetup.class.getSimpleName()));

    m_OptionManager.add(
      "post-processor", "postProcessor",
      new PassThrough());

    m_OptionManager.add(
      "prefer-jobrunner", "preferJobRunner",
      false);
  }

  /**
   * Sets the clusterer to use.
   *
   * @param value	the clusterer
   */
  public void setClusterer(CallableActorReference value) {
    m_Clusterer = value;
    reset();
  }

  /**
   * Returns the clusterer in use.
   *
   * @return		the clusterer
   */
  public CallableActorReference getClusterer() {
    return m_Clusterer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String clustererTipText() {
    return "The Weka clusterer to build on the input data.";
  }

  /**
   * Sets the post-processor to use.
   *
   * @param value	the post-processor
   */
  public void setPostProcessor(AbstractClustererPostProcessor value) {
    m_PostProcessor = value;
    reset();
  }

  /**
   * Returns the post-processor in use.
   *
   * @return		the post-processor
   */
  public AbstractClustererPostProcessor getPostProcessor() {
    return m_PostProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String postProcessorTipText() {
    return "The post-processor to use on model containers.";
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
    return "If enabled, tries to offload the processing onto a " + Utils.classToString(JobRunnerInstance.class) + "; applies only to batch training.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    String		value;
    List<String> 	options;

    result = QuickInfoHelper.toString(this, "clusterer", m_Clusterer);
    value = QuickInfoHelper.toString(this, "postProcessor", (m_PostProcessor instanceof PassThrough ? null : m_PostProcessor), ", post-processor: ");
    if (value != null)
      result += value;
    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "preferJobRunner", m_PreferJobRunner, "jobrunner"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_INCREMENTALCLUSTERER);
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

    if (m_IncrementalClusterer != null)
      result.put(BACKUP_INCREMENTALCLUSTERER, m_IncrementalClusterer);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_INCREMENTALCLUSTERER)) {
      m_IncrementalClusterer = (weka.clusterers.Clusterer) state.get(BACKUP_INCREMENTALCLUSTERER);
      state.remove(BACKUP_INCREMENTALCLUSTERER);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_IncrementalClusterer = null;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class, weka.core.Instance.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class, Instance.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaModelContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaModelContainer.class};
  }

  /**
   * Returns an instance of the callable clusterer.
   *
   * @return		the clusterer
   * @throws Exception  if fails to obtain clusterer
   */
  protected weka.clusterers.Clusterer getClustererInstance() throws Exception {
    weka.clusterers.Clusterer   result;
    MessageCollection		errors;

    errors = new MessageCollection();
    result = (weka.clusterers.Clusterer) CallableActorHelper.getSetup(weka.clusterers.Clusterer.class, m_Clusterer, this, errors);
    if (result == null) {
      if (errors.isEmpty())
	throw new IllegalStateException("Failed to obtain clusterer from '" + m_Clusterer + "'!");
      else
	throw new IllegalStateException("Failed to obtain clusterer from '" + m_Clusterer + "':\n" + errors);
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
    Instance				inst;
    weka.clusterers.Clusterer		cls;
    WekaModelContainer			cont;
    AbstractClustererPostProcessor	postProcessor;
    BatchTrainJob			job;

    result = null;

    try {
      cls = null;
      if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instances)) {
	cls  = getClustererInstance();
	postProcessor = ObjectCopyHelper.copyObject(m_PostProcessor);
	data = (Instances) m_InputToken.getPayload();
	if (m_JobRunnerInstance != null) {
	  job    = new BatchTrainJob(cls, data, postProcessor);
	  result = m_JobRunnerInstance.executeJob(job);
	  if (result == null)
	    m_OutputToken = new Token(job.getContainer());
	  job.cleanUp();
	}
	else {
	  cls.buildClusterer(data);
	  cont = new WekaModelContainer(cls, new Instances(data, 0), data);
	  cont = postProcessor.postProcess(cont);
	  m_OutputToken = new Token(cont);
	}
      }
      else if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instance)) {
	if (m_IncrementalClusterer == null) {
	  cls = getClustererInstance();
	  if (!(cls instanceof UpdateableClusterer))
	    result = m_Clusterer + "/" + cls.getClass().getName() + " is not an incremental clusterer!";
	}
	if (result == null) {
	  inst = (Instance) m_InputToken.getPayload();
	  if (m_IncrementalClusterer == null) {
	    m_IncrementalClusterer = cls;
	    data = new Instances(inst.dataset(), 1);
	    data.add((Instance) inst.copy());
	    m_IncrementalClusterer.buildClusterer(data);
	  }
	  else {
	    ((UpdateableClusterer) m_IncrementalClusterer).updateClusterer(inst);
	    ((UpdateableClusterer) m_IncrementalClusterer).updateFinished();
	  }
	  m_OutputToken = new Token(new WekaModelContainer(m_IncrementalClusterer, new Instances(inst.dataset(), 0)));
	}
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to process input: " + m_InputToken.getPayload(), e);
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_JobRunnerInstance = null;
    super.wrapUp();
  }
}
