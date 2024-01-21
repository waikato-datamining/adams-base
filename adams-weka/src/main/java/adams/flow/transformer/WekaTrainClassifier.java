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
 * WekaTrainClassifier.java
 * Copyright (C) 2012-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Token;
import adams.flow.source.WekaClassifierSetup;
import adams.flow.standalone.JobRunnerInstance;
import adams.multiprocess.AbstractJob;
import adams.multiprocess.JobRunnerSupporter;
import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Trains a classifier based on the incoming dataset and outputs the built classifier alongside the training header (in a model container).<br>
 * Incremental training is performed, if the input are weka.core.Instance objects and the classifier implements weka.classifiers.UpdateableClassifier.
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
 * &nbsp;&nbsp;&nbsp;default: WekaTrainClassifier
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
 * <pre>-classifier &lt;adams.flow.core.CallableActorReference&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The Weka classifier to train on the input data.
 * &nbsp;&nbsp;&nbsp;default: WekaClassifierSetup
 * </pre>
 *
 * <pre>-skip-build &lt;boolean&gt; (property: skipBuild)
 * &nbsp;&nbsp;&nbsp;If enabled, the buildClassifier call gets skipped in case of incremental
 * &nbsp;&nbsp;&nbsp;classifiers, eg, if the model only needs updating after being loaded from
 * &nbsp;&nbsp;&nbsp;disk.
 * &nbsp;&nbsp;&nbsp;default: false
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
public class WekaTrainClassifier
  extends AbstractTransformer
  implements JobRunnerSupporter {

  public static class BatchTrainJob
    extends AbstractJob {

    private static final long serialVersionUID = 6406892820872772446L;

    /** the classifier to train. */
    protected Classifier m_Classifier;

    /** the data to use for training. */
    protected Instances m_Data;

    /** the model container. */
    protected WekaModelContainer m_Container;

    /**
     * Initializes the job.
     *
     * @param cls	the classifier to train
     * @param data	the training data
     */
    public BatchTrainJob(Classifier cls, Instances data) {
      super();
      m_Classifier = cls;
      m_Data       = data;
      m_Container  = null;
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
      if (m_Classifier == null)
	return "No classifier to train!";
      if (m_Data == null)
	return "No training data!";
      if (m_Data.classIndex() == -1)
	return "No class attribute set!";
      return null;
    }

    /**
     * Does the actual execution of the job.
     *
     * @throws Exception if fails to execute job
     */
    @Override
    protected void process() throws Exception {
      m_Classifier.buildClassifier(m_Data);
      m_Container = new WekaModelContainer(m_Classifier, new Instances(m_Data, 0), m_Data);
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
      return OptionUtils.getCommandLine(m_Classifier) + "\n" + m_Data.relationName();
    }

    /**
     * Cleans up data structures, frees up memory.
     * Removes dependencies and job parameters.
     */
    @Override
    public void cleanUp() {
      m_Classifier = null;
      m_Data       = null;
      super.cleanUp();
    }
  }

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the key for storing the current incremental classifier in the backup. */
  public final static String BACKUP_INCREMENTALCLASSIFIER = "incremental classifier";

  /** the name of the callable weka classifier. */
  protected CallableActorReference m_Classifier;

  /** the classifier to use when training incrementally. */
  protected weka.classifiers.Classifier m_IncrementalClassifier;

  /** whether to skip the buildClassifier call for incremental classifiers. */
  protected boolean m_SkipBuild;

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
      "Trains a classifier based on the incoming dataset and outputs the "
	+ "built classifier alongside the training header (in a model container).\n"
	+ "Incremental training is performed, if the input are weka.core.Instance "
	+ "objects and the classifier implements " + UpdateableClassifier.class.getName() + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "classifier", "classifier",
      new CallableActorReference(WekaClassifierSetup.class.getSimpleName()));

    m_OptionManager.add(
      "skip-build", "skipBuild",
      false);

    m_OptionManager.add(
      "prefer-jobrunner", "preferJobRunner",
      false);
  }

  /**
   * Sets the name of the callable classifier to use.
   *
   * @param value	the name
   */
  public void setClassifier(CallableActorReference value) {
    m_Classifier = value;
    reset();
  }

  /**
   * Returns the name of the callable classifier in use.
   *
   * @return		the name
   */
  public CallableActorReference getClassifier() {
    return m_Classifier;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classifierTipText() {
    return "The Weka classifier to train on the input data.";
  }

  /**
   * Sets whether to skip the buildClassifier call for incremental classifiers.
   *
   * @param value	true if to skip the buildClassifier call
   */
  public void setSkipBuild(boolean value) {
    m_SkipBuild = value;
    reset();
  }

  /**
   * Returns whether to skip the buildClassifier call for incremental classifiers.
   *
   * @return		true if to skip buildClassifier
   */
  public boolean getSkipBuild() {
    return m_SkipBuild;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipBuildTipText() {
    return "If enabled, the buildClassifier call gets skipped in case of incremental classifiers, eg, if the model only needs updating after being loaded from disk.";
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
    List<String>	options;

    result = QuickInfoHelper.toString(this, "classifier", m_Classifier);
    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "skipBuild", m_SkipBuild, "skip build"));
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

    pruneBackup(BACKUP_INCREMENTALCLASSIFIER);
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

    if (m_IncrementalClassifier != null)
      result.put(BACKUP_INCREMENTALCLASSIFIER, m_IncrementalClassifier);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_INCREMENTALCLASSIFIER)) {
      m_IncrementalClassifier = (weka.classifiers.Classifier) state.get(BACKUP_INCREMENTALCLASSIFIER);
      state.remove(BACKUP_INCREMENTALCLASSIFIER);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_IncrementalClassifier = null;
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
   * Returns an instance of the callable classifier.
   *
   * @return		the classifier
   * @throws Exception  if fails to obtain classifier
   */
  protected weka.classifiers.Classifier getClassifierInstance() throws Exception {
    weka.classifiers.Classifier	result;
    MessageCollection		errors;

    errors = new MessageCollection();
    result = (weka.classifiers.Classifier) CallableActorHelper.getSetup(weka.classifiers.Classifier.class, m_Classifier, this, errors);
    if (result == null) {
      if (errors.isEmpty())
	throw new IllegalStateException("Failed to obtain classifier from '" + m_Classifier + "'!");
      else
	throw new IllegalStateException("Failed to obtain classifier from '" + m_Classifier + "':\n" + errors);
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
    String			result;
    Instances			data;
    Instance			inst;
    weka.classifiers.Classifier	cls;
    BatchTrainJob		job;

    result = null;

    try {
      cls = null;
      if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instances)) {
	cls  = getClassifierInstance();
	data = (Instances) m_InputToken.getPayload();
	if (m_JobRunnerInstance != null) {
	  job    = new BatchTrainJob(cls, data);
	  result = m_JobRunnerInstance.executeJob(job);
	  if (result == null)
	    m_OutputToken = new Token(job.getContainer());
	  job.cleanUp();
	}
	else {
	  cls.buildClassifier(data);
	  m_OutputToken = new Token(new WekaModelContainer(cls, new Instances(data, 0), data));
	}
      }
      else if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instance)) {
	if (m_IncrementalClassifier == null) {
	  cls = getClassifierInstance();
	  if (!(cls instanceof UpdateableClassifier))
	    result = m_Classifier + "/" + cls.getClass().getName() + " is not an incremental classifier!";
	}
	if (result == null) {
	  inst = (Instance) m_InputToken.getPayload();
	  if (m_IncrementalClassifier == null) {
	    m_IncrementalClassifier = cls;
	    if (m_SkipBuild) {
	      ((UpdateableClassifier) m_IncrementalClassifier).updateClassifier(inst);
	    }
	    else {
	      data = new Instances(inst.dataset(), 1);
	      data.add((Instance) inst.copy());
	      m_IncrementalClassifier.buildClassifier(data);
	    }
	  }
	  else {
	    ((UpdateableClassifier) m_IncrementalClassifier).updateClassifier(inst);
	  }
	  m_OutputToken = new Token(new WekaModelContainer(m_IncrementalClassifier, new Instances(inst.dataset(), 0)));
	}
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result        = handleException("Failed to process data:", e);
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();

    m_IncrementalClassifier = null;
    m_JobRunnerInstance     = null;
  }
}
