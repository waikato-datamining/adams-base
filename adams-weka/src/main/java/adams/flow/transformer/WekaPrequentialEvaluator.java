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
 * WekaPrequentialEvaluator.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.flow.source.WekaClassifierSetup;
import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Evaluates and trains an incremental classifier (classifier must implement weka.classifiers.UpdateableClassifier) based on the incoming data and outputs a adams.flow.container.WekaEvaluationContainer container at the specified intervals.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer<br/>
 * <p/>
 * Container information:<br/>
 * - adams.flow.container.WekaEvaluationContainer: Evaluation, Model
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
 * &nbsp;&nbsp;&nbsp;default: WekaPrequentialEvaluator
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
 * <pre>-classifier &lt;adams.flow.core.CallableActorReference&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The Weka classifier to evaluate&#47;train on the input data.
 * &nbsp;&nbsp;&nbsp;default: WekaClassifierSetup
 * </pre>
 * 
 * <pre>-skip-build &lt;boolean&gt; (property: skipBuild)
 * &nbsp;&nbsp;&nbsp;If enabled, the buildClassifier call gets skipped, eg, if the model only 
 * &nbsp;&nbsp;&nbsp;needs updating after being loaded from disk.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-interval &lt;int&gt; (property: interval)
 * &nbsp;&nbsp;&nbsp;The number of tokens after which to forward the evaluation container.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaPrequentialEvaluator
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the key for storing the current incremental classifier in the backup. */
  public final static String BACKUP_INCREMENTALCLASSIFIER = "incremental classifier";

  /** the key for storing the current counter in the backup. */
  public final static String BACKUP_CURRENT = "current";

  /** the key for storing the current evaluation object in the backup. */
  public final static String BACKUP_EVALUATION = "evaluation";

  /** the name of the callable weka classifier. */
  protected CallableActorReference m_Classifier;

  /** the classifier to use when training incrementally. */
  protected weka.classifiers.Classifier m_IncrementalClassifier;

  /** whether to skip the buildClassifier call (pre-built models). */
  protected boolean m_SkipBuild;

  /** the interval when to output the model container. */
  protected int m_Interval;

  /** the current counter. */
  protected int m_Current;

  /** the evaluation in use. */
  protected Evaluation m_Evaluation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates and trains an incremental classifier (classifier must implement "
          + UpdateableClassifier.class.getName() + ") based on the incoming data and outputs a "
          + WekaEvaluationContainer.class.getName() + " container at the specified intervals.";
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
      "interval", "interval",
      1, 1, null);
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
    return "The Weka classifier to evaluate/train on the input data.";
  }

  /**
   * Sets whether to skip the buildClassifier call.
   *
   * @param value	true if to skip the buildClassifier call
   */
  public void setSkipBuild(boolean value) {
    m_SkipBuild = value;
    reset();
  }

  /**
   * Returns whether to skip the buildClassifier.
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
    return "If enabled, the buildClassifier call gets skipped, eg, if the model only needs updating after being loaded from disk.";
  }

  /**
   * Sets the interval after which to forward the evaluation container.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    if (value > 0) {
      m_Interval = value;
      reset();
    }
    else {
      getLogger().warning("Interval must >0, provided: " + value);
    }
  }

  /**
   * Returns the interval after which to forward the evaluation container.
   *
   * @return		the interval
   */
  public int getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return "The number of tokens after which to forward the evaluation container.";
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

    result  = QuickInfoHelper.toString(this, "classifier", m_Classifier);
    result += QuickInfoHelper.toString(this, "interval", m_Interval, ", interval: ");
    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "skipBuild", m_SkipBuild, "skip build"));
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
    pruneBackup(BACKUP_CURRENT);
    pruneBackup(BACKUP_EVALUATION);
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

    result.put(BACKUP_CURRENT, m_Current);

    if (m_Evaluation != null)
      result.put(BACKUP_EVALUATION, m_Evaluation);

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
    if (state.containsKey(BACKUP_CURRENT)) {
      m_Current = (Integer) state.get(BACKUP_CURRENT);
      state.remove(BACKUP_CURRENT);
    }
    if (state.containsKey(BACKUP_EVALUATION)) {
      m_Evaluation = (weka.classifiers.Evaluation) state.get(BACKUP_EVALUATION);
      state.remove(BACKUP_EVALUATION);
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
    m_Current               = 0;
    m_Evaluation            = null;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instance.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instance.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaEvaluationContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaEvaluationContainer.class};
  }

  /**
   * Returns an instance of the callable classifier.
   *
   * @return		the classifier
   * @throws Exception  if fails to obtain classifier
   */
  protected weka.classifiers.Classifier getClassifierInstance() throws Exception {
    weka.classifiers.Classifier   result;

    result = (weka.classifiers.Classifier) CallableActorHelper.getSetup(weka.classifiers.Classifier.class, m_Classifier, this);
    if (result == null)
      throw new IllegalStateException("Failed to obtain classifier from '" + m_Classifier + "'!");

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

    result = null;
    m_Current++;

    try {
      cls = null;
      if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instance)) {
	if (m_IncrementalClassifier == null) {
	  cls = getClassifierInstance();
	  if (!(cls instanceof UpdateableClassifier))
	    result = m_Classifier + "/" + cls.getClass().getName() + " is not an incremental classifier!";
	}
	if (result == null) {
	  inst = (Instance) m_InputToken.getPayload();
          data = null;
          if ((m_Evaluation == null) || (m_IncrementalClassifier == null)) {
            data = new Instances(inst.dataset(), 1);
            data.add((Instance) inst.copy());
            m_Evaluation = new Evaluation(data);
          }
	  if (m_IncrementalClassifier == null) {
	    m_IncrementalClassifier = cls;
            if (m_SkipBuild) {
              m_Evaluation.evaluateModelOnce(m_IncrementalClassifier, inst);
	      ((UpdateableClassifier) m_IncrementalClassifier).updateClassifier(inst);
            }
            else {
              m_IncrementalClassifier.buildClassifier(data);
            }
	  }
	  else {
            m_Evaluation.evaluateModelOnce(m_IncrementalClassifier, inst);
	    ((UpdateableClassifier) m_IncrementalClassifier).updateClassifier(inst);
	  }
          if (m_Current % m_Interval == 0) {
            m_Current     = 0;
            m_OutputToken = new Token(new WekaEvaluationContainer(m_Evaluation));
          }
	}
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result        = handleException("Failed to process data:", e);
    }

    if (m_OutputToken != null)
      updateProvenance(m_OutputToken);

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();

    m_IncrementalClassifier = null;
    m_Evaluation            = null;
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
}
