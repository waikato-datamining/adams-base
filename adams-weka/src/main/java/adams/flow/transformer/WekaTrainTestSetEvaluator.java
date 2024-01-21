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
 * WekaTrainTestSetEvaluator.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.flow.core.Token;
import adams.flow.standalone.JobRunnerInstance;
import adams.multiprocess.AbstractJob;
import adams.multiprocess.JobRunnerSupporter;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.Null;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Trains a classifier on an incoming training dataset (from a container) and then evaluates it on the test set (also from a container).<br>
 * The classifier setup being used in the evaluation is a callable 'Classifier' actor.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaTrainTestSetContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaTrainTestSetContainer: Train, Test, Seed, FoldNumber, FoldCount, Train original indices, Test original indices<br>
 * - adams.flow.container.WekaEvaluationContainer: Evaluation, Model, Prediction output, Original indices, Test data
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
 * &nbsp;&nbsp;&nbsp;default: WekaTrainTestSetEvaluator
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
 * <pre>-output &lt;weka.classifiers.evaluation.output.prediction.AbstractOutput&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The class for generating prediction output; if 'Null' is used, then an Evaluation
 * &nbsp;&nbsp;&nbsp;object is forwarded instead of a String.
 * &nbsp;&nbsp;&nbsp;default: weka.classifiers.evaluation.output.prediction.Null
 * </pre>
 *
 * <pre>-always-use-container &lt;boolean&gt; (property: alwaysUseContainer)
 * &nbsp;&nbsp;&nbsp;If enabled, always outputs an evaluation container.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-classifier &lt;adams.flow.core.CallableActorReference&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The callable classifier actor to train and evaluate on the test data.
 * &nbsp;&nbsp;&nbsp;default: WekaClassifierSetup
 * </pre>
 *
 * <pre>-no-predictions &lt;boolean&gt; (property: discardPredictions)
 * &nbsp;&nbsp;&nbsp;If enabled, the collection of predictions during evaluation is suppressed,
 * &nbsp;&nbsp;&nbsp; wich will conserve memory.
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
public class WekaTrainTestSetEvaluator
  extends AbstractCallableWekaClassifierEvaluator
  implements JobRunnerSupporter {

  public static class EvaluateJob
    extends AbstractJob {

    private static final long serialVersionUID = 6406892820872772446L;

    /** the classifier to train. */
    protected Classifier m_Classifier;

    /** the data to use for training. */
    protected Instances m_Train;

    /** the data to use for testing. */
    protected Instances m_Test;

    /** the evaluation object to use. */
    protected Evaluation m_Evaluation;

    /** for collecting the output. */
    protected AbstractOutput m_Output;

    /**
     * Initializes the job.
     *
     * @param classifier	the classifier to train
     * @param train		the training data
     * @param test 		the test data
     * @param eval 		the evaluation object to use
     * @param output 		for collecting the output
     */
    public EvaluateJob(Classifier classifier, Instances train, Instances test, Evaluation eval, AbstractOutput output) {
      super();
      m_Classifier = classifier;
      m_Train      = train;
      m_Test       = test;
      m_Evaluation = eval;
      m_Output     = output;
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_Classifier == null)
	return "No classifier defined!";
      if (m_Train == null)
	return "No training data!";
      if (m_Train.classIndex() == -1)
	return "No class attribute set in training data!";
      if (m_Test == null)
	return "No test data!";
      if (m_Test.classIndex() == -1)
	return "No class attribute set in test data!";
      if (!m_Train.equalHeaders(m_Test))
	return m_Train.equalHeadersMsg(m_Test);
      if (m_Evaluation == null)
	return "No evaluation object set!";
      if (m_Output == null)
	return "No evaluation output set!";
      return null;
    }

    /**
     * Does the actual execution of the job.
     *
     * @throws Exception if fails to execute job
     */
    @Override
    protected void process() throws Exception {
      m_Classifier.buildClassifier(m_Train);
      m_Evaluation.evaluateModel(m_Classifier, m_Test, m_Output);
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
      return OptionUtils.getCommandLine(m_Classifier) + "\n" + m_Train.relationName();
    }

    /**
     * Cleans up data structures, frees up memory.
     * Removes dependencies and job parameters.
     */
    @Override
    public void cleanUp() {
      m_Classifier = null;
      m_Train      = null;
      m_Test       = null;
      m_Evaluation = null;
      m_Output     = null;
      super.cleanUp();
    }
  }

  /** for serialization. */
  private static final long serialVersionUID = -1092101024095887007L;

  /** whether to offload train/evaluate onto a JobRunnerInstance. */
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
      "Trains a classifier on an incoming training dataset (from a container) "
	+ "and then evaluates it on the test set (also from a container).\n"
	+ "The classifier setup being used in the evaluation is a callable 'Classifier' actor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefer-jobrunner", "preferJobRunner",
      false);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String classifierTipText() {
    return "The callable classifier actor to train and evaluate on the test data.";
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
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.container.WekaTrainTestSetContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{WekaTrainTestSetContainer.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "preferJobRunner", m_PreferJobRunner, "jobrunner", ", ");

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
    Instances			train;
    Instances			test;
    weka.classifiers.Classifier	cls;
    Evaluation			eval;
    WekaTrainTestSetContainer	cont;
    EvaluateJob			job;

    result = null;
    test   = null;

    try {
      // cross-validate classifier
      cls = getClassifierInstance();
      if (cls == null)
	throw new IllegalStateException("Classifier '" + getClassifier() + "' not found!");

      cont  = (WekaTrainTestSetContainer) m_InputToken.getPayload();
      train = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN);
      test  = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TEST);
      initOutputBuffer();
      m_Output.setHeader(train);
      eval  = new Evaluation(train);
      eval.setDiscardPredictions(m_DiscardPredictions);
      if (m_JobRunnerInstance != null) {
	job = new EvaluateJob(cls, train, test, eval, m_Output);
	result = m_JobRunnerInstance.executeJob(job);
	if (result != null)
	  throw new Exception(result);
	job.cleanUp();
      }
      else {
	cls.buildClassifier(train);
	eval.evaluateModel(cls, test, m_Output);
      }

      // broadcast result
      if (m_Output instanceof Null) {
	m_OutputToken = new Token(new WekaEvaluationContainer(eval, cls));
      }
      else {
	if (m_AlwaysUseContainer)
	  m_OutputToken = new Token(new WekaEvaluationContainer(eval, cls, m_Output.getBuffer().toString()));
	else
	  m_OutputToken = new Token(m_Output.getBuffer().toString());
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to evaluate: ", e);
    }

    if (m_OutputToken != null) {
      if (m_OutputToken.getPayload() instanceof WekaEvaluationContainer) {
	if (test != null)
	  ((WekaEvaluationContainer) m_OutputToken.getPayload()).setValue(WekaEvaluationContainer.VALUE_TESTDATA, test);
      }
    }

    cleanOutputBuffer();

    return result;
  }
}
