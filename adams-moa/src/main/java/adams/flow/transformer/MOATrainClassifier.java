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
 * MOATrainClassifier.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import moa.classifiers.trees.DecisionStump;
import moa.options.ClassOption;
import weka.core.Instance;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.Token;
import adams.flow.source.MOAClassifierSetup;

/**
 <!-- globalinfo-start -->
 * Trains a MOA classifier based on the incoming data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
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
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: MOATrainClassifier
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
 * &nbsp;&nbsp;&nbsp;The callable MOA classifier to train on the input data and outputs the built 
 * &nbsp;&nbsp;&nbsp;classifier alongside the training header (in a model container).
 * &nbsp;&nbsp;&nbsp;default: MOAClassifierSetup
 * </pre>
 * 
 * <pre>-output-interval &lt;int&gt; (property: outputInterval)
 * &nbsp;&nbsp;&nbsp;The number of tokens to wait before forwarding the trained classifier.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOATrainClassifier
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 1410487605033307517L;

  /** the key for storing the current classifier in the backup. */
  public final static String BACKUP_CLASSIFIER = "classifier";

  /** the MOA classifier. */
  protected CallableActorReference m_Classifier;

  /** the actual classifier to use. */
  protected moa.classifiers.Classifier m_ActualClassifier;

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
        "Trains a MOA classifier based on the incoming data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "classifier", "classifier",
	    new CallableActorReference(MOAClassifierSetup.class.getSimpleName()));

    m_OptionManager.add(
	    "output-interval", "outputInterval",
	    1000, 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualClassifier = null;
    m_Count            = 0;
  }

  /**
   * Returns the default classifier.
   *
   * @return		the classifier
   */
  protected moa.classifiers.Classifier getDefaultClassifier() {
    return new DecisionStump();
  }

  /**
   * Returns the default class option.
   *
   * @return		the option
   */
  protected ClassOption getDefaultOption() {
    return new ClassOption(
	"classifier",
	'c',
	"The MOA classifier to use from within ADAMS.",
	moa.classifiers.Classifier.class,
	getDefaultClassifier().getClass().getName().replace("moa.classifiers.", ""),
	getDefaultClassifier().getClass().getName());
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
    return
        "The callable MOA classifier to train on the input data and outputs the "
      + "built classifier alongside the training header (in a model container).";
  }

  /**
   * Returns an instance of the callable classifier.
   *
   * @return		the classifier
   */
  protected moa.classifiers.Classifier getClassifierInstance() {
    return (moa.classifiers.Classifier) CallableActorHelper.getSetup(moa.classifiers.Classifier.class, m_Classifier, this);
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
    return "The number of tokens to wait before forwarding the trained classifier.";
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
    result += QuickInfoHelper.toString(this, "outputInterval", ((m_OutputInterval == 1) ? "always" : m_OutputInterval), "/");

    return result;
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
      m_ActualClassifier = (moa.classifiers.Classifier) state.get(BACKUP_CLASSIFIER);
      state.remove(BACKUP_CLASSIFIER);
    }

    super.restoreState(state);
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
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaModelContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaModelContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    List<Instance>	data;

    result = null;

    try {
      if (m_InputToken != null) {
	data = new ArrayList<Instance>();
	if (m_InputToken.getPayload() instanceof Instance)
	  data.add((Instance) m_InputToken.getPayload());
	else
	  data.addAll((Instances) m_InputToken.getPayload());
	
	if (m_ActualClassifier == null)
	  m_ActualClassifier = getClassifierInstance();
	if (m_ActualClassifier == null) {
	  result = "Failed to located classifier '" + m_Classifier + "'!";
	  return result;
	}

	// train
	for (Instance inst: data)
	  m_ActualClassifier.trainOnInstance(inst);

	// generate output
	m_Count++;
	if (m_Count % m_OutputInterval == 0) {
	  m_Count = 0;
	  m_OutputToken = new Token(new WekaModelContainer(m_ActualClassifier, new Instances(data.get(0).dataset(), 0)));
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
    super.wrapUp();

    m_ActualClassifier = null;
  }
}
