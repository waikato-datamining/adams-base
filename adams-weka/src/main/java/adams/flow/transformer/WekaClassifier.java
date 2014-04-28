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
 * WekaClassifier.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.Hashtable;

import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.source.WekaClassifierSetup;


/**
 <!-- globalinfo-start -->
 * Trains a classifier based on the incoming dataset and outputs the built classifier alongside the training header (in a model container).<br/>
 * Incremental training is performed, if the input are weka.core.Instance objects and the classifier implements weka.classifiers.UpdateableClassifier.<br/>
 * If the incoming token does not encapsulate a dataset or instance, then only a new instance of the classifier is sent around.<br/>
 * <br/>
 * DEPRECATED<br/>
 * <br/>
 * - Use adams.flow.source.WekaClassifierSetup for definined a classifier setup.<br/>
 * - Use adams.flow.transformer.WekaTrainClassifier for building a classifier.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaModelContainer<br/>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Classifier<br/>
 * <p/>
 * Container information:<br/>
 * - adams.flow.container.WekaModelContainer: Model, Header, Dataset
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
 * &nbsp;&nbsp;&nbsp;default: WekaClassifier
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
 * <pre>-classifier &lt;weka.classifiers.Classifier&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The Weka classifier to train on the input data.
 * &nbsp;&nbsp;&nbsp;default: weka.classifiers.rules.ZeroR
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@Deprecated
public class WekaClassifier
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the key for storing the current incremental classifier in the backup. */
  public final static String BACKUP_INCREMENTALCLASSIFIER = "incremental classifier";

  /** the weka classifier. */
  protected weka.classifiers.Classifier m_Classifier;

  /** the classifier to use when training incrementally. */
  protected weka.classifiers.Classifier m_IncrementalClassifier;

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
      + "objects and the classifier implements " + UpdateableClassifier.class.getName() + ".\n"
      + "If the incoming token does not encapsulate a dataset or instance, then "
      + "only a new instance of the classifier is sent around.\n\n"
      + "DEPRECATED\n\n"
      + "- Use " + WekaClassifierSetup.class.getName() + " for definined a classifier setup.\n"
      + "- Use " + WekaTrainClassifier.class.getName() + " for building a classifier.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "classifier", "classifier",
	    new weka.classifiers.rules.ZeroR());
  }

  /**
   * Sets the classifier to use.
   *
   * @param value	the classifier
   */
  public void setClassifier(weka.classifiers.Classifier value) {
    m_Classifier = value;
    reset();
  }

  /**
   * Returns the classifier in use.
   *
   * @return		the classifier
   */
  public weka.classifiers.Classifier getClassifier() {
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "classifier", m_Classifier.getClass());
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
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class, weka.core.Instance.class, adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class, Instance.class, Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaModelContainer.class, weka.classifiers.Classifier.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaModelContainer.class, weka.classifiers.Classifier.class};
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

    try {
      if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instances)) {
	data = (Instances) m_InputToken.getPayload();
	cls  = weka.classifiers.AbstractClassifier.makeCopy(m_Classifier);
	cls.buildClassifier(data);
	m_OutputToken = new Token(new WekaModelContainer(cls, new Instances(data, 0), data));
      }
      else if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instance)) {
	if (!(m_Classifier instanceof UpdateableClassifier)) {
	  result = m_Classifier.getClass().getName() + " is not an incremental classifier!";
	}
	else {
	  inst = (Instance) m_InputToken.getPayload();
	  if (m_IncrementalClassifier == null) {
	    m_IncrementalClassifier = weka.classifiers.AbstractClassifier.makeCopy(m_Classifier);
	    data = new Instances(inst.dataset(), 1);
	    data.add((Instance) inst.copy());
	    m_IncrementalClassifier.buildClassifier(data);
	  }
	  else {
	    ((UpdateableClassifier) m_IncrementalClassifier).updateClassifier(inst);
	  }
	  m_OutputToken = new Token(new WekaModelContainer(m_IncrementalClassifier, new Instances(inst.dataset(), 0)));
	}
      }
      else {
	cls = weka.classifiers.AbstractClassifier.makeCopy(m_Classifier);
	m_OutputToken = new Token(cls);
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
  }
}
