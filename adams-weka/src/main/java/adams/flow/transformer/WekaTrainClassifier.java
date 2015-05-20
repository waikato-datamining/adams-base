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
 * WekaTrainClassifier.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;

import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.flow.source.WekaClassifierSetup;

/**
 <!-- globalinfo-start -->
 * Trains a classifier based on the incoming dataset and outputs the built classifier alongside the training header (in a model container).<br>
 * Incremental training is performed, if the input are weka.core.Instance objects and the classifier implements weka.classifiers.UpdateableClassifier.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaTrainClassifier
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
 * &nbsp;&nbsp;&nbsp;The Weka classifier to train on the input data.
 * &nbsp;&nbsp;&nbsp;default: WekaClassifierSetup
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaTrainClassifier
  extends AbstractTransformer 
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the key for storing the current incremental classifier in the backup. */
  public final static String BACKUP_INCREMENTALCLASSIFIER = "incremental classifier";

  /** the name of the callable weka classifier. */
  protected CallableActorReference m_Classifier;

  /** the actual weka classifier. */
  protected weka.classifiers.Classifier m_ActualClassifier;

  /** the classifier to use when training incrementally. */
  protected weka.classifiers.Classifier m_IncrementalClassifier;

  /** whether to skip the buildClassifier call for incremental classifiers. */
  protected boolean m_SkipBuild;

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
      + "objects and the classifier implements " + UpdateableClassifier.class.getName() + ".";
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
    return "The Weka classifier to train on the input data.";
  }

  /**
   * Sets whether to skip the buildClassifier call for incremental classifiers.
   *
   * @param value	true if to skip the buildClassifier call
   */
  public void setSkipBuild(boolean value) {
    m_SkipBuild = value;
    reset();
  }

  /**
   * Returns whether to skip the buildClassifier call for incremental classifiers.
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
    return "If enabled, the buildClassifier call gets skipped in case of incremental classifiers, eg, if the model only needs updating after being loaded from disk.";
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

    result = QuickInfoHelper.toString(this, "classifier", m_Classifier);
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
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class, weka.core.Instance.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class, Instance.class};
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

    try {
      cls = null;
      if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instances)) {
	cls  = getClassifierInstance();
	data = (Instances) m_InputToken.getPayload();
	cls.buildClassifier(data);
	m_OutputToken = new Token(new WekaModelContainer(cls, new Instances(data, 0), data));
      }
      else if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instance)) {
	if (m_IncrementalClassifier == null) {
	  cls = getClassifierInstance();
	  if (!(cls instanceof UpdateableClassifier))
	    result = m_Classifier + "/" + cls.getClass().getName() + " is not an incremental classifier!";
	}
	if (result == null) {
	  inst = (Instance) m_InputToken.getPayload();
	  if (m_IncrementalClassifier == null) {
	    m_IncrementalClassifier = cls;
            if (m_SkipBuild) {
	      ((UpdateableClassifier) m_IncrementalClassifier).updateClassifier(inst);
            }
            else {
              data = new Instances(inst.dataset(), 1);
              data.add((Instance) inst.copy());
              m_IncrementalClassifier.buildClassifier(data);
            }
	  }
	  else {
	    ((UpdateableClassifier) m_IncrementalClassifier).updateClassifier(inst);
	  }
	  m_OutputToken = new Token(new WekaModelContainer(m_IncrementalClassifier, new Instances(inst.dataset(), 0)));
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
      cont.addProvenance(new ProvenanceInformation(ActorType.MODEL_GENERATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
