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

/**
 * WekaStreamEvaluator.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;
import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.evaluation.output.prediction.Null;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Evaluates an incremental classifier on a data stream using prequential evaluation (first evaluate, then train).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaEvaluationContainer: Evaluation, Model, Prediction output
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
 * &nbsp;&nbsp;&nbsp;default: WekaStreamEvaluator
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
 * &nbsp;&nbsp;&nbsp;The callable source with the incremental classifier to evaluate.
 * &nbsp;&nbsp;&nbsp;default: WekaClassifierSetup
 * </pre>
 * 
 * <pre>-no-predictions &lt;boolean&gt; (property: discardPredictions)
 * &nbsp;&nbsp;&nbsp;If enabled, the collection of predictions during evaluation is suppressed,
 * &nbsp;&nbsp;&nbsp; wich will conserve memory.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-interval &lt;int&gt; (property: interval)
 * &nbsp;&nbsp;&nbsp;The interval (number of instance objects processed) after which to output 
 * &nbsp;&nbsp;&nbsp;evaluation or buffer.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaStreamEvaluator
  extends AbstractCallableWekaClassifierEvaluator {

  /** for serialization. */
  private static final long serialVersionUID = -1346633748934963999L;

  /** the backup key for the current counter. */
  public final static String BACKUP_CURRENT = "current";

  /** the backup key for the current header. */
  public final static String BACKUP_HEADER = "header";

  /** the backup key for the evaluation. */
  public final static String BACKUP_EVALUATION = "evaluation";

  /** the backup key for the classifier. */
  public final static String BACKUP_CLaSSIFIER = "classifier";
  
  /** the interval at which to output the evaluation. */
  protected int m_Interval;
  
  /** the current counter of instances. */
  protected int m_Current;
  
  /** the current header. */
  protected Instances m_Header;

  /** the evaluation to use. */
  protected Evaluation m_Evaluation;

  /** the classifier to use. */
  protected weka.classifiers.Classifier m_Classifier;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Evaluates an incremental classifier on a data stream using "
	+ "prequential evaluation (first evaluate, then train).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "interval", "interval",
	    100, 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Current    = 0;
    m_Header     = null;
    m_Evaluation = null;
    m_Classifier = null;
  }
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String classifierTipText() {
    return "The callable source with the incremental classifier to evaluate.";
  }

  /**
   * Sets the output interval.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    if (value >= 1) {
      m_Interval = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Number of interval must be >=1, provided: " + value);
    }
  }

  /**
   * Returns the output interval.
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
    return 
	"The interval (number of instance objects processed) after which to "
	+ "output evaluation or buffer.";
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
    result += QuickInfoHelper.toString(this, "interval", m_Interval, ", interval: ");

    return result;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_CURRENT);
    pruneBackup(BACKUP_HEADER);
    pruneBackup(BACKUP_EVALUATION);
    pruneBackup(BACKUP_CLaSSIFIER);
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
    
    if (m_Current > 0)
      result.put(BACKUP_CURRENT, m_Current);
    
    if (m_Header != null)
      result.put(BACKUP_HEADER, m_Header);
    
    if (m_Evaluation != null)
      result.put(BACKUP_EVALUATION, m_Evaluation);
    
    if (m_Classifier != null)
      result.put(BACKUP_CLaSSIFIER, m_Classifier);
    
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
    
    if (state.containsKey(BACKUP_CURRENT)) {
      m_Current = (Integer) state.get(BACKUP_CURRENT);
      state.remove(BACKUP_CURRENT);
    }
    
    if (state.containsKey(BACKUP_HEADER)) {
      m_Header = (Instances) state.get(BACKUP_HEADER);
      state.remove(BACKUP_HEADER);
    }
    
    if (state.containsKey(BACKUP_EVALUATION)) {
      m_Evaluation = (Evaluation) state.get(BACKUP_EVALUATION);
      state.remove(BACKUP_EVALUATION);
    }
    
    if (state.containsKey(BACKUP_CLaSSIFIER)) {
      m_Classifier = (weka.classifiers.Classifier) state.get(BACKUP_CLaSSIFIER);
      state.remove(BACKUP_CLaSSIFIER);
    }
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instance.class};
  }

  /**
   * Returns an instance of the callable classifier.
   *
   * @return		the classifier
   */
  @Override
  protected weka.classifiers.Classifier getClassifierInstance() {
    weka.classifiers.Classifier	result;
    
    result = super.getClassifierInstance();
    if (!(result instanceof UpdateableClassifier))
      throw new IllegalStateException(
	  "The classifier does not implement " + UpdateableClassifier.class.getName() + "!");
    
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
    Instance				inst;
    Instances				data;
    
    result = null;

    inst = (Instance) m_InputToken.getPayload();
    data = inst.dataset();

    if (m_Evaluation == null) {
      try {
	m_Evaluation = new Evaluation(data);
	m_Current    = 0;
	m_Header     = data;
        initOutputBuffer();
        m_Output.setHeader(m_Header);
      }
      catch (Exception e) {
	result = handleException("Failed to set up evaluation!", e);
      }
    }

    // evaluate/train
    if (result == null) {
      try {
	if (m_Classifier == null) {
	  m_Classifier = getClassifierInstance();
	  m_Classifier.buildClassifier(data);
	}
	
	if (m_Current > 0) {
	  if (m_DiscardPredictions)
	    m_Evaluation.evaluateModelOnce(m_Classifier, inst);
	  else
	    m_Evaluation.evaluateModelOnceAndRecordPrediction(m_Classifier, inst);
	}

	((UpdateableClassifier) m_Classifier).updateClassifier(inst);
      }
      catch (Exception e) {
	result = handleException("Failed to evaluate/update the classifier!", e);
      }
    }

    // output?
    m_Current++;
    if (m_Current % m_Interval == 0) {
	if (m_Output instanceof Null) {
          m_OutputToken = new Token(new WekaEvaluationContainer(m_Evaluation));
        }
	else {
          if (m_AlwaysUseContainer)
            m_OutputToken = new Token(new WekaEvaluationContainer(m_Evaluation, null, m_Output.getBuffer().toString()));
          else
            m_OutputToken = new Token(m_Output.getBuffer().toString());
        }
    }
    
    return result;
  }
}
