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
 * WekaTestSetEvaluator.java
 * Copyright (C) 2009-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.CallableActorReference;
import adams.flow.core.FlowContextUtils;
import adams.flow.core.Token;
import adams.flow.source.CallableSource;
import adams.flow.standalone.JobRunnerInstance;
import adams.multiprocess.AbstractJob;
import adams.multiprocess.JobRunnerSupporter;
import weka.classifiers.Classifier;
import weka.classifiers.StoppableEvaluation;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.Null;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Evaluates a trained classifier (obtained from input) on the dataset obtained from the callable actor.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Classifier<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaModelContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaModelContainer: Model, Header, Dataset<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaTestSetEvaluator
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
 * <pre>-testset &lt;adams.flow.core.CallableActorReference&gt; (property: testset)
 * &nbsp;&nbsp;&nbsp;The callable actor to use for obtaining the test set.
 * &nbsp;&nbsp;&nbsp;default: Testset
 * </pre>
 *
 * <pre>-no-predictions &lt;boolean&gt; (property: discardPredictions)
 * &nbsp;&nbsp;&nbsp;If enabled, the collection of predictions during evaluation is suppressed,
 * &nbsp;&nbsp;&nbsp; which will conserve memory.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-prefer-jobrunner &lt;boolean&gt; (property: preferJobRunner)
 * &nbsp;&nbsp;&nbsp;If enabled, tries to offload the processing onto a adams.flow.standalone.JobRunnerInstance;
 * &nbsp;&nbsp;&nbsp; applies only to training.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaTestSetEvaluator
  extends AbstractWekaClassifierEvaluator
  implements JobRunnerSupporter {

  public static class EvaluateJob
    extends AbstractJob {

    private static final long serialVersionUID = 6406892820872772446L;

    /** the evaluation object to use. */
    protected StoppableEvaluation m_Evaluation;

    /** the classifier to evaluate. */
    protected Classifier m_Classifier;

    /** the data to use for testing. */
    protected Instances m_Test;

    /** the output to use. */
    protected AbstractOutput m_Output;

    /**
     * Initializes the job.
     *
     * @param evaluation	the evaluation object to use
     * @param classifier  	the classifier to evaluate
     * @param test 		the test data
     * @param output 		the output to use
     */
    public EvaluateJob(StoppableEvaluation evaluation, Classifier classifier, Instances test, AbstractOutput output) {
      super();
      m_Evaluation = evaluation;
      m_Classifier = classifier;
      m_Test       = test;
      m_Output     = output;
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_Classifier == null)
	return "No classifier to evaluate!";
      if (m_Test == null)
	return "No test data!";
      return null;
    }

    /**
     * Does the actual execution of the job.
     *
     * @throws Exception if fails to execute job
     */
    @Override
    protected void process() throws Exception {
      m_Evaluation.evaluateModel(m_Classifier, m_Test, m_Output);
    }

    /**
     * Stops the execution.
     */
    @Override
    public void stopExecution() {
      m_Evaluation.stopExecution();
      super.stopExecution();
    }

    /**
     * Checks whether all post-conditions have been met.
     *
     * @return null if everything is OK, otherwise an error message
     */
    @Override
    protected String postProcessCheck() {
      return null;
    }

    /**
     * Returns a string representation of this job.
     *
     * @return the job as string
     */
    @Override
    public String toString() {
      return OptionUtils.getCommandLine(m_Classifier) + "\n" + m_Test.relationName();
    }

    /**
     * Cleans up data structures, frees up memory.
     * Removes dependencies and job parameters.
     */
    @Override
    public void cleanUp() {
      m_Evaluation = null;
      m_Classifier = null;
      m_Test       = null;
      m_Output     = null;
      super.cleanUp();
    }
  }

  /** for serialization. */
  private static final long serialVersionUID = -8528709957864675275L;

  /** the name of the callable trainset provider. */
  protected CallableActorReference m_Testset;

  /** whether to discard predictions. */
  protected boolean m_DiscardPredictions;

  /** whether to offload training into a JobRunnerInstance. */
  protected boolean m_PreferJobRunner;

  /** the JobRunnerInstance to use. */
  protected transient JobRunnerInstance m_JobRunnerInstance;

  /** the current evaluation. */
  protected transient StoppableEvaluation m_CurrentEvaluation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Evaluates a trained classifier (obtained from input) on the dataset "
	+ "obtained from the callable actor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "testset", "testset",
      new CallableActorReference("Testset"));

    m_OptionManager.add(
      "no-predictions", "discardPredictions",
      false);

    m_OptionManager.add(
      "prefer-jobrunner", "preferJobRunner",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "testset", m_Testset);
    result += QuickInfoHelper.toString(this, "discardPredictions", m_DiscardPredictions, "discarding predictions", ", ");
    result += QuickInfoHelper.toString(this, "preferJobRunner", m_PreferJobRunner, ", jobrunner");

    return result;
  }

  /**
   * Sets the name of the callable classifier to use.
   *
   * @param value	the name
   */
  public void setTestset(CallableActorReference value) {
    m_Testset = value;
    reset();
  }

  /**
   * Returns the name of the callable classifier in use.
   *
   * @return		the name
   */
  public CallableActorReference getTestset() {
    return m_Testset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String testsetTipText() {
    return "The callable actor to use for obtaining the test set.";
  }

  /**
   * Sets whether to discard the predictions instead of collecting them
   * for future use, in order to conserve memory.
   *
   * @param value	true if to discard predictions
   */
  public void setDiscardPredictions(boolean value) {
    m_DiscardPredictions = value;
    reset();
  }

  /**
   * Returns whether to discard the predictions in order to preserve memory.
   *
   * @return		true if predictions discarded
   */
  public boolean getDiscardPredictions() {
    return m_DiscardPredictions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String discardPredictionsTipText() {
    return
      "If enabled, the collection of predictions during evaluation is "
	+ "suppressed, which will conserve memory.";
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
    return "If enabled, tries to offload the processing onto a " + Utils.classToString(JobRunnerInstance.class) + "; applies only to training.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.classifiers.Classifier.class, adams.flow.container.WekaModelContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{weka.classifiers.Classifier.class, WekaModelContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Instances			test;
    weka.classifiers.Classifier	cls;
    CallableSource		gs;
    Token			output;
    EvaluateJob			job;

    result = null;
    test   = null;

    try {
      // get test set
      test = null;
      gs   = new CallableSource();
      gs.setCallableName(m_Testset);
      gs.setParent(getParent());
      gs.setUp();
      gs.execute();
      output = gs.output();
      if (output != null)
	test = (Instances) output.getPayload();
      else
	result = "No test set available!";
      gs.wrapUp();

      // evaluate classifier
      if (result == null) {
	if (m_InputToken.getPayload() instanceof weka.classifiers.Classifier)
	  cls = (weka.classifiers.Classifier) m_InputToken.getPayload();
	else
	  cls = (weka.classifiers.Classifier) ((WekaModelContainer) m_InputToken.getPayload()).getValue(WekaModelContainer.VALUE_MODEL);
	if (FlowContextUtils.isHandler(cls))
	  FlowContextUtils.update(cls, this);
	initOutputBuffer();
	m_Output.setHeader(test);
	m_CurrentEvaluation = new StoppableEvaluation(test);
	m_CurrentEvaluation.setDiscardPredictions(m_DiscardPredictions);
	if (m_JobRunnerInstance != null) {
	  job    = new EvaluateJob(m_CurrentEvaluation, cls, test, m_Output);
	  result = m_JobRunnerInstance.executeJob(job);
	  job.cleanUp();
	  if (result != null)
	    throw new Exception(result);
	}
	else {
	  m_CurrentEvaluation.evaluateModel(cls, test, m_Output);
	}

	// broadcast result
	if (m_Output instanceof Null) {
	  m_OutputToken = new Token(new WekaEvaluationContainer(m_CurrentEvaluation, cls));
	}
	else {
	  if (m_AlwaysUseContainer)
	    m_OutputToken = new Token(new WekaEvaluationContainer(m_CurrentEvaluation, cls, m_Output.getBuffer().toString()));
	  else
	    m_OutputToken = new Token(m_Output.getBuffer().toString());
	}
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

    m_CurrentEvaluation = null;

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_CurrentEvaluation != null)
      m_CurrentEvaluation.stopExecution();
    super.stopExecution();
  }
}
