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
 * MOAClassifier.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.Hashtable;

import moa.classifiers.trees.DecisionStump;
import moa.options.ClassOption;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.MOAUtils;
import adams.core.QuickInfoHelper;
import adams.core.annotation.DeprecatedClass;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
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
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.ModelContainer<br>
 * &nbsp;&nbsp;&nbsp;moa.classifiers.Classifier<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: MOAClassifier
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-classifier &lt;moa.options.ClassOption&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The MOA classifier to train on the input data and outputs the built classifier
 * &nbsp;&nbsp;&nbsp;alongside the training header (in a model container).
 * &nbsp;&nbsp;&nbsp;If the incoming token does not encapsulate an weka.core.Instance, then only
 * &nbsp;&nbsp;&nbsp;a new instance of the classifier is sent around.
 * &nbsp;&nbsp;&nbsp;default: moa.classifiers.DecisionStump
 * </pre>
 *
 * <pre>-output-interval &lt;int&gt; (property: outputInterval)
 * &nbsp;&nbsp;&nbsp;The number of tokens to wait before forwarding the trainined classifier.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@Deprecated
@DeprecatedClass(useInstead = {MOAClassifierSetup.class, MOATrainClassifier.class})
public class MOAClassifier
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 1410487605033307517L;

  /** the key for storing the current classifier in the backup. */
  public final static String BACKUP_CLASSIFIER = "classifier";

  /** the MOA classifier. */
  protected moa.options.ClassOption m_Classifier;

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
        "Trains a MOA classifier based on the incoming data.\n\n"
        + "DEPRECATED\n\n"
        + "- Use " + MOAClassifierSetup.class.getName() + " for definined a classifier setup.\n"
        + "- Use " + MOATrainClassifier.class.getName() + " for building a classifier.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "classifier", "classifier",
	    getDefaultOption());

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
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Classifier = getDefaultOption();
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
   * Sets the classifier to use.
   *
   * @param value	the classifier
   */
  public void setClassifier(ClassOption value) {
    m_Classifier.setValueViaCLIString(value.getValueAsCLIString());
    reset();
  }

  /**
   * Returns the classifier in use.
   *
   * @return		the classifier
   */
  public ClassOption getClassifier() {
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
        "The MOA classifier to train on the input data and outputs the "
      + "built classifier alongside the training header (in a model container).\n"
      + "If the incoming token does not encapsulate an weka.core.Instance, then "
      + "only a new instance of the classifier is sent around.";
  }

  /**
   * Returns the current classifier, based on the class option.
   *
   * @return		the classifier
   * @see		#getClassifier()
   */
  protected moa.classifiers.Classifier getCurrentClassifier() {
    return (moa.classifiers.Classifier) MOAUtils.fromOption(m_Classifier);
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
    return "The number of tokens to wait before forwarding the trainined classifier.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "classifier", getCurrentClassifier().getClass());
    result += QuickInfoHelper.toString(this, "outputInterval", m_OutputInterval, "/");

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
   * @return		<!-- flow-accepts-start -->weka.core.Instance.class, adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instance.class, Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.ModelContainer.class, moa.classifiers.Classifier.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaModelContainer.class, moa.classifiers.Classifier.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Instance			inst;
    moa.classifiers.Classifier	cls;

    result = null;

    try {
      if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instance)) {
	// train
	inst = (Instance) m_InputToken.getPayload();
	if (m_ActualClassifier == null)
	  m_ActualClassifier = getCurrentClassifier();
	m_ActualClassifier.trainOnInstance(inst);

	// generate output
	m_Count++;
	if (m_Count % m_OutputInterval == 0) {
	  m_Count = 0;
	  m_OutputToken = new Token(new WekaModelContainer(m_ActualClassifier, new Instances(inst.dataset(), 0)));
	}
      }
      else {
	cls = (moa.classifiers.Classifier) MOAUtils.fromOption(m_Classifier);
	m_OutputToken = new Token(cls);
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
