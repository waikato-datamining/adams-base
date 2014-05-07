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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import moa.classifiers.AbstractClassifier;
import moa.core.Measurement;
import moa.evaluation.BasicClassificationPerformanceEvaluator;
import moa.evaluation.ClassificationPerformanceEvaluator;
import moa.options.ClassOption;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.MOAUtils;
import weka.core.Utils;
import adams.core.MOAHelper;
import adams.core.QuickInfoHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Evaluates a MOA classifier using prequential evaluation. With each incoming instance, the classifier is first evaluated, then trained.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;moa.core.Measurement[]<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: MOAClassifierEvaluation
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-classifier &lt;adams.flow.core.CallableActorReference&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The name of the callable MOA classifier to train&#47;evaluate.
 * &nbsp;&nbsp;&nbsp;default: MOAClassifier
 * </pre>
 * 
 * <pre>-evaluator &lt;moa.options.ClassOption&gt; (property: evaluator)
 * &nbsp;&nbsp;&nbsp;The MOA evaluator to use for evaluating a trained MOA classifier.
 * &nbsp;&nbsp;&nbsp;default: moa.evaluation.BasicClassificationPerformanceEvaluator
 * </pre>
 * 
 * <pre>-output-interval &lt;int&gt; (property: outputInterval)
 * &nbsp;&nbsp;&nbsp;The number of tokens to skip before evaluating the classifier stored in 
 * &nbsp;&nbsp;&nbsp;the token (only used when receiving Instance objects).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOAClassifierEvaluation
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1410487605033307517L;

  /** the key for storing the current classifier in the backup. */
  public final static String BACKUP_CLASSIFIER = "classifier";

  /** the name of the callable classifier to use. */
  protected CallableActorReference m_Classifier;

  /** the model to use for prediction/training. */
  protected AbstractClassifier m_ActualClassifier;

  /** the evaluation to use. */
  protected moa.options.ClassOption m_Evaluator;

  /** the actual evaluator to use. */
  protected moa.evaluation.ClassificationPerformanceEvaluator m_ActualEvaluator;

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
        "Evaluates a MOA classifier using prequential evaluation. With each "
      + "incoming instance, the classifier is first evaluated, then trained.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "classifier", "classifier",
	    new CallableActorReference("MOAClassifier"));

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

    m_ActualEvaluator  = null;
    m_Count            = 0;
    m_ActualClassifier = null;
  }

  /**
   * Sets the callable classifier to use.
   *
   * @param value	the classifier name
   */
  public void setClassifier(CallableActorReference value) {
    m_Classifier = value;
    reset();
  }

  /**
   * Returns the callable classifier to use.
   *
   * @return		the classifier name
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
    return "The name of the callable MOA classifier to train/evaluate.";
  }

  /**
   * Returns the default evaluator.
   *
   * @return		the evaluator
   */
  protected moa.evaluation.ClassificationPerformanceEvaluator getDefaultEvaluator() {
    return new BasicClassificationPerformanceEvaluator();
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
	"The MOA classifier performance evaluator to use from within ADAMS.",
	moa.evaluation.ClassificationPerformanceEvaluator.class,
	getDefaultEvaluator().getClass().getName().replace("moa.evaluation.", ""),
	getDefaultEvaluator().getClass().getName());
  }

  /**
   * Sets the evaluator to use.
   *
   * @param value	the evaluator
   */
  public void setEvaluator(ClassOption value) {
    m_Evaluator = (ClassOption) value.copy();
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
    return "The MOA evaluator to use for evaluating a trained MOA classifier.";
  }

  /**
   * Returns the current evaluator, based on the class option.
   *
   * @return		the evaluator
   * @see		#getEvaluator()
   */
  protected ClassificationPerformanceEvaluator getCurrentEvaluator() {
    return (moa.evaluation.ClassificationPerformanceEvaluator) MOAUtils.fromOption(m_Evaluator);
  }

  /**
   * Sets the number of tokens after which to evaluate the classifier.
   *
   * @param value	the interval
   */
  public void setOutputInterval(int value) {
    m_OutputInterval = value;
    reset();
  }

  /**
   * Returns the number of tokens after which to evaluate the classifier.
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
    return "The number of tokens to skip before evaluating the classifier stored in the token (only used when receiving Instance objects).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "classifier", m_Classifier);
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

    pruneBackup(BACKUP_CLASSIFIER);
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

    if (m_ActualClassifier != null)
      result.put(BACKUP_CLASSIFIER, m_ActualClassifier);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_CLASSIFIER)) {
      m_ActualClassifier = (AbstractClassifier) state.get(BACKUP_CLASSIFIER);
      state.remove(BACKUP_CLASSIFIER);
    }

    super.restoreState(state);
  }

  /**
   * Returns an instance of the callable classifier.
   *
   * @return		the classifier
   */
  protected AbstractClassifier getClassifierInstance() {
    return (AbstractClassifier) CallableActorHelper.getSetup(AbstractClassifier.class, m_Classifier, this);
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
    if (m_ActualClassifier == null) {
      m_ActualClassifier = getClassifierInstance();
      if (m_ActualClassifier == null) {
	result = "Failed to located classifier '" + m_Classifier + "'!";
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
      prediction = MOAHelper.fixVotes(m_ActualClassifier.getVotesForInstance(testInst), testInst);
      if (isLoggingEnabled())
	getLogger().info("trueClass=" + trueClass + ", prediction=" + Utils.arrayToString(prediction) + ", weight=" + testInst.weight());
      m_ActualEvaluator.addResult(inst, prediction);

      // train
      m_ActualClassifier.trainOnInstance(inst);
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
    m_ActualClassifier = null;
  }
}
