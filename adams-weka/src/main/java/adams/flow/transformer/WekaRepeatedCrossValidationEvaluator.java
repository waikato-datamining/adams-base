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
 * WekaRepeatedCrossValidationEvaluator.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.ObjectCopyHelper;
import adams.core.Performance;
import adams.core.QuickInfoHelper;
import adams.core.ThreadLimiter;
import adams.core.option.OptionUtils;
import adams.data.weka.InstancesViewSupporter;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.standalone.JobRunnerSetup;
import adams.multiprocess.WekaCrossValidationExecution;
import weka.classifiers.CrossValidationFoldGenerator;
import weka.classifiers.DefaultCrossValidationFoldGenerator;
import weka.classifiers.evaluation.output.prediction.Null;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Performs repeated cross-validation a classifier on an incoming dataset. The classifier setup being used in the evaluation is a callable 'Classifier' actor.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaRepeatedCrossValidationEvaluator
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
 * &nbsp;&nbsp;&nbsp;The callable classifier actor to cross-validate on the input data.
 * &nbsp;&nbsp;&nbsp;default: WekaClassifierSetup
 * </pre>
 *
 * <pre>-no-predictions &lt;boolean&gt; (property: discardPredictions)
 * &nbsp;&nbsp;&nbsp;If enabled, the collection of predictions during evaluation is suppressed,
 * &nbsp;&nbsp;&nbsp; wich will conserve memory.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-runs &lt;int&gt; (property: runs)
 * &nbsp;&nbsp;&nbsp;The number of cross-validation runs to perform.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-folds &lt;int&gt; (property: folds)
 * &nbsp;&nbsp;&nbsp;The number of folds to use in the cross-validation; use -1 for leave-one-out
 * &nbsp;&nbsp;&nbsp;cross-validation (LOOCV); overrides the value defined by the fold generator
 * &nbsp;&nbsp;&nbsp;scheme.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-num-threads &lt;int&gt; (property: numThreads)
 * &nbsp;&nbsp;&nbsp;The number of threads to use for parallel execution; &gt; 0: specific number
 * &nbsp;&nbsp;&nbsp;of cores to use (capped by actual number of cores available, 1 = sequential
 * &nbsp;&nbsp;&nbsp;execution); = 0: number of cores; &lt; 0: number of free cores (eg -2 means
 * &nbsp;&nbsp;&nbsp;2 free cores; minimum of one core is used); overrides the value defined
 * &nbsp;&nbsp;&nbsp;by the fold generator scheme.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-generator &lt;weka.classifiers.CrossValidationFoldGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The scheme to use for generating the folds; the actor options take precedence
 * &nbsp;&nbsp;&nbsp;over the scheme's ones.
 * &nbsp;&nbsp;&nbsp;default: weka.classifiers.DefaultCrossValidationFoldGenerator
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaRepeatedCrossValidationEvaluator
  extends AbstractCallableWekaClassifierEvaluator
  implements ThreadLimiter, InstancesViewSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the number of runs to perform. */
  protected int m_Runs;

  /** the number of folds. */
  protected int m_Folds;

  /** the number of threads to use for parallel execution. */
  protected int m_NumThreads;

  /** whether to use views. */
  protected boolean m_UseViews;

  /** the fold generator. */
  protected CrossValidationFoldGenerator m_Generator;

  /** for performing cross-validation. */
  protected WekaCrossValidationExecution m_CrossValidation;

  /** the jobrunner setup. */
  protected transient JobRunnerSetup m_JobRunnerSetup;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs repeated cross-validation a classifier on an incoming dataset. The classifier "
	+ "setup being used in the evaluation is a callable 'Classifier' actor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.removeByProperty("output");
    m_OptionManager.removeByProperty("alwaysUseContainer");

    m_OptionManager.add(
      "runs", "runs",
      10, 1, null);

    m_OptionManager.add(
      "folds", "folds",
      10, -1, null);

    m_OptionManager.add(
      "num-threads", "numThreads",
      1);

    m_OptionManager.add(
      "generator", "generator",
      new DefaultCrossValidationFoldGenerator());
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_AlwaysUseContainer = true;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = super.getQuickInfo();

    result += QuickInfoHelper.toString(this, "runs", m_Runs, ", runs: ");
    result += QuickInfoHelper.toString(this, "folds", m_Folds, ", folds: ");
    result += QuickInfoHelper.toString(this, "numThreads", Performance.getNumThreadsQuickInfo(m_NumThreads), ", ");
    value  = QuickInfoHelper.toString(this, "useViews", m_UseViews, ", using views");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String classifierTipText() {
    return "The callable classifier actor to cross-validate on the input data.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputTipText() {
    return
      "The class for generating prediction output; if 'Null' is used, then "
	+ "an Evaluation object is forwarded instead of a String; not used when "
	+ "using parallel execution.";
  }

  /**
   * Sets the number of folds.
   *
   * @param value	the folds, -1 for LOOCV
   */
  public void setFolds(int value) {
    if ((value == -1) || (value >= 2)) {
      m_Folds = value;
      reset();
    }
    else {
      getLogger().severe(
	"Number of folds must be >=2 or -1 for LOOCV, provided: " + value);
    }
  }

  /**
   * Returns the number of folds.
   *
   * @return		the folds
   */
  public int getFolds() {
    return m_Folds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String foldsTipText() {
    return "The number of folds to use in the cross-validation; use -1 for "
	     + "leave-one-out cross-validation (LOOCV); overrides the value defined by the fold generator scheme.";
  }

  /**
   * Sets the number of runs to perform.
   *
   * @param value	the runs
   */
  public void setRuns(int value) {
    if (getOptionManager().isValid("runs", value)) {
      m_Runs = value;
      reset();
    }
  }

  /**
   * Returns the number of runs to perform.
   *
   * @return		the runs
   */
  public int getRuns() {
    return m_Runs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String runsTipText() {
    return "The number of cross-validation runs to perform.";
  }

  /**
   * Sets the number of threads to use for cross-validation.
   *
   * @param value 	the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  public void setNumThreads(int value) {
    m_NumThreads = value;
    reset();
  }

  /**
   * Returns the number of threads to use for cross-validation.
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
    return Performance.getNumThreadsHelp() + "; overrides the value defined by the fold generator scheme.";
  }

  /**
   * Sets whether to use views instead of dataset copies, in order to
   * conserve memory.
   *
   * @param value	true if to use views
   */
  public void setUseViews(boolean value) {
    m_UseViews = value;
    reset();
  }

  /**
   * Returns whether to use views instead of dataset copies, in order to
   * conserve memory.
   *
   * @return		true if using views
   */
  public boolean getUseViews() {
    return m_UseViews;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useViewsTipText() {
    return "If enabled, views of the dataset are being used instead of actual copies, to conserve memory; overrides the value defined by the fold generator scheme.";
  }

  /**
   * Sets the scheme for generating the folds.
   *
   * @param value	the generator
   */
  public void setGenerator(CrossValidationFoldGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the scheme for generating the folds.
   *
   * @return		the generator
   */
  public CrossValidationFoldGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The scheme to use for generating the folds; the actor options take precedence over the scheme's ones.";
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
   * @return		the output that it generates
   */
  public Class[] generates() {
    return new Class[]{WekaEvaluationContainer[].class};
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

    if (result == null)
      m_JobRunnerSetup = (JobRunnerSetup) ActorUtils.findClosestType(this, JobRunnerSetup.class);

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
    weka.classifiers.Classifier		cls;
    int[]				indices;
    int					run;
    WekaEvaluationContainer[]		conts;

    result = null;
    conts  = null;

    try {
      // evaluate classifier
      cls = getClassifierInstance();
      if (cls == null)
	throw new IllegalStateException("Classifier '" + getClassifier() + "' not found!");
      if (isLoggingEnabled())
	getLogger().info(OptionUtils.getCommandLine(cls));

      data  = (Instances) m_InputToken.getPayload();
      conts = new WekaEvaluationContainer[m_Runs];

      for (run = 0; run < m_Runs; run++) {
	try {
	  m_CrossValidation = new WekaCrossValidationExecution();
	  m_CrossValidation.setJobRunnerSetup(m_JobRunnerSetup);
	  m_CrossValidation.setClassifier(cls);
	  m_CrossValidation.setData(data);
	  m_CrossValidation.setFolds(m_Folds);
	  m_CrossValidation.setSeed(run + 1);
	  m_CrossValidation.setUseViews(m_UseViews);
	  m_CrossValidation.setDiscardPredictions(m_DiscardPredictions);
	  m_CrossValidation.setNumThreads(m_NumThreads);
	  m_CrossValidation.setGenerator(ObjectCopyHelper.copyObject(m_Generator));
	  m_CrossValidation.setOutput(new Null());
	  m_CrossValidation.setFlowContext(this);
	  result = m_CrossValidation.execute();

	  if (!m_CrossValidation.isStopped()) {
	    conts[run] = new WekaEvaluationContainer(m_CrossValidation.getEvaluation());
	    conts[run].setValue(WekaEvaluationContainer.VALUE_TESTDATA, data);
	    indices = m_CrossValidation.getOriginalIndices();
	    if (indices != null)
	      conts[run].setValue(WekaEvaluationContainer.VALUE_ORIGINALINDICES, indices);
	  }

	  if (m_CrossValidation != null) {
	    m_CrossValidation.cleanUp();
	    m_CrossValidation = null;
	  }
	}
	catch (Exception e) {
	  result = handleException("Failed to cross-validate classifier (run #" + run + "): ", e);
	  conts = null;
	  break;
	}
      }
    }
    catch (Exception e) {
      result = handleException("Failed to set up cross-validation!", e);
    }

    if (conts != null)
      m_OutputToken = new Token(conts);

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_CrossValidation != null)
      m_CrossValidation.stopExecution();
    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    if (m_CrossValidation != null) {
      m_CrossValidation.cleanUp();
      m_CrossValidation = null;
    }

    super.wrapUp();
  }
}
