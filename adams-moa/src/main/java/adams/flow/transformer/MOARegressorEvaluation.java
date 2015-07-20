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
 * MOAClassifierEvaluation.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Regressor;
import moa.core.Measurement;
import moa.evaluation.BasicRegressionPerformanceEvaluator;
import moa.evaluation.ClassificationPerformanceEvaluator;
import moa.evaluation.RegressionPerformanceEvaluator;
import moa.options.ClassOption;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.MOAUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Evaluates a MOA regressor using prequential evaluation. With each incoming instance, the regressor is first evaluated, then trained.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;moa.core.Measurement[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: MOARegressorEvaluation
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
 * <pre>-regressor &lt;adams.flow.core.CallableActorReference&gt; (property: regressor)
 * &nbsp;&nbsp;&nbsp;The name of the callable MOA regressor to train&#47;evaluate.
 * &nbsp;&nbsp;&nbsp;default: MOARegressor
 * </pre>
 * 
 * <pre>-evaluator &lt;moa.options.ClassOption&gt; (property: evaluator)
 * &nbsp;&nbsp;&nbsp;The MOA evaluator to use for evaluating a trained MOA regressor.
 * &nbsp;&nbsp;&nbsp;default: moa.evaluation.BasicRegressionPerformanceEvaluator
 * </pre>
 * 
 * <pre>-output-interval &lt;int&gt; (property: outputInterval)
 * &nbsp;&nbsp;&nbsp;The number of tokens to skip before evaluating the regressor stored in the 
 * &nbsp;&nbsp;&nbsp;token (only used when receiving Instance objects).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOARegressorEvaluation
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1410487605033307517L;

  /** the key for storing the current regressor in the backup. */
  public final static String BACKUP_REGRESSOR = "regressor";

  /** the name of the callable regressor to use. */
  protected CallableActorReference m_Regressor;

  /** the model to use for prediction/training. */
  protected AbstractClassifier m_ActualRegressor;

  /** the evaluation to use. */
  protected ClassOption m_Evaluator;

  /** the actual evaluator to use. */
  protected ClassificationPerformanceEvaluator m_ActualEvaluator;

  /** the output interval. */
  protected int m_OutputInterval;

  /** the current count of tokens that have passed through this actor. */
  protected int m_Count;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates a MOA regressor using prequential evaluation. With each "
      + "incoming instance, the regressor is first evaluated, then trained.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "regressor", "regressor",
	    new CallableActorReference("MOARegressor"));

    m_OptionManager.add(
	    "evaluator", "evaluator",
	    getDefaultOption());

    m_OptionManager.add(
	    "output-interval", "outputInterval",
	    1, 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualEvaluator = null;
    m_Count           = 0;
    m_ActualRegressor = null;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Evaluator = getDefaultOption();
  }

  /**
   * Sets the callable regressor to use.
   *
   * @param value	the regressor name
   */
  public void setRegressor(CallableActorReference value) {
    m_Regressor = value;
    reset();
  }

  /**
   * Returns the callable regressor to use.
   *
   * @return		the regressor name
   */
  public CallableActorReference getRegressor() {
    return m_Regressor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regressorTipText() {
    return "The name of the callable MOA regressor to train/evaluate.";
  }

  /**
   * Returns the default evaluator.
   *
   * @return		the evaluator
   */
  protected RegressionPerformanceEvaluator getDefaultEvaluator() {
    return new BasicRegressionPerformanceEvaluator();
  }

  /**
   * Returns the default class option.
   *
   * @return		the option
   */
  protected ClassOption getDefaultOption() {
    return new ClassOption(
	"evaluator",
	'e',
	"The MOA regressor performance evaluator to use from within ADAMS.",
	RegressionPerformanceEvaluator.class,
	getDefaultEvaluator().getClass().getName().replace("moa.evaluation.", ""),
	getDefaultEvaluator().getClass().getName());
  }

  /**
   * Sets the evaluator to use.
   *
   * @param value	the evaluator
   */
  public void setEvaluator(ClassOption value) {
    m_Evaluator.setValueViaCLIString(value.getValueAsCLIString());
    reset();
  }

  /**
   * Returns the evaluator in use.
   *
   * @return		the evaluator
   */
  public ClassOption getEvaluator() {
    return m_Evaluator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String evaluatorTipText() {
    return "The MOA evaluator to use for evaluating a trained MOA regressor.";
  }

  /**
   * Returns the current evaluator, based on the class option.
   *
   * @return		the evaluator
   * @see		#getEvaluator()
   */
  protected RegressionPerformanceEvaluator getCurrentEvaluator() {
    return (RegressionPerformanceEvaluator) MOAUtils.fromOption(m_Evaluator);
  }

  /**
   * Sets the number of tokens after which to evaluate the regressor.
   *
   * @param value	the interval
   */
  public void setOutputInterval(int value) {
    m_OutputInterval = value;
    reset();
  }

  /**
   * Returns the number of tokens after which to evaluate the regressor.
   *
   * @return		the interval
   */
  public int getOutputInterval() {
    return m_OutputInterval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputIntervalTipText() {
    return "The number of tokens to skip before evaluating the regressor stored in the token (only used when receiving Instance objects).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "regressor", m_Regressor);
    result += QuickInfoHelper.toString(this, "evaluator", getCurrentEvaluator().getClass(), ", ");
    result += QuickInfoHelper.toString(this, "outputInterval", ((m_OutputInterval == 1) ? "always" : m_OutputInterval), "/");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instance.class, weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instance.class, Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->moa.core.Measurement[].class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Measurement[].class};
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_REGRESSOR);
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

    if (m_ActualRegressor != null)
      result.put(BACKUP_REGRESSOR, m_ActualRegressor);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_REGRESSOR)) {
      m_ActualRegressor = (AbstractClassifier) state.get(BACKUP_REGRESSOR);
      state.remove(BACKUP_REGRESSOR);
    }

    super.restoreState(state);
  }

  /**
   * Returns an instance of the callable regressor.
   *
   * @return		the classifier
   */
  protected AbstractClassifier getRegressorInstance() {
    return (AbstractClassifier) CallableActorHelper.getSetup(Regressor.class, m_Regressor, this);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Instance		testInst;
    int 		trueClass;
    double[] 		prediction;
    List<Instance>	data;
    boolean		single;

    result = null;

    if (m_ActualEvaluator == null)
      m_ActualEvaluator = getCurrentEvaluator();
    if (m_ActualRegressor == null) {
      m_ActualRegressor = getRegressorInstance();
      if (m_ActualRegressor == null) {
	result = "Failed to located regressor '" + m_Regressor + "'!";
	return result;
      }
    }
    
    data = new ArrayList<Instance>();
    if (m_InputToken.getPayload() instanceof Instances) {
      data.addAll((Instances) m_InputToken.getPayload());
      single = false;
    }
    else {
      data.add((Instance) m_InputToken.getPayload());
      single = true;
    }

    for (Instance inst: data) {
      // test
      testInst  = (Instance) inst.copy();
      trueClass = (int) testInst.classValue();
      testInst.setClassMissing();
      prediction = m_ActualRegressor.getVotesForInstance(testInst);
      if (isLoggingEnabled())
	getLogger().info("trueClass=" + trueClass + ", prediction=" + prediction[0] + ", weight=" + testInst.weight());
      m_ActualEvaluator.addResult(inst, prediction);

      // train
      m_ActualRegressor.trainOnInstance(inst);
    }

    if (single) {
      m_Count++;
      if (m_Count % m_OutputInterval == 0) {
	m_Count = 0;
	m_OutputToken = new Token(m_ActualEvaluator.getPerformanceMeasurements());
	updateProvenance(m_OutputToken);
      }
    }
    else {
      m_OutputToken = new Token(m_ActualEvaluator.getPerformanceMeasurements());
      updateProvenance(m_OutputToken);
    }

    return result;
  }
  
  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.EVALUATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();

    m_ActualEvaluator  = null;
    m_ActualRegressor = null;
  }
}
