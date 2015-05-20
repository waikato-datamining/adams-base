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
 * WekaClusterer.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.Hashtable;

import weka.clusterers.UpdateableClusterer;
import weka.core.Instance;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.annotation.DeprecatedClass;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.source.WekaClustererSetup;
import adams.flow.transformer.wekaclusterer.AbstractClustererPostProcessor;
import adams.flow.transformer.wekaclusterer.PassThrough;

/**
 <!-- globalinfo-start -->
 * Trains a clusterer based on the incoming dataset and output the built clusterer alongside the training header (in a model container).<br>
 * Incremental training is performed, if the input are weka.core.Instance objects and the clusterer implements weka.clusterers.UpdateableClusterer.<br>
 * If the incoming token does not encapsulate a dataset, then only a new instance of the clusterer is sent around.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.ModelContainer<br>
 * &nbsp;&nbsp;&nbsp;weka.clusterers.Clusterer<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Clusterer
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
 * <pre>-clusterer &lt;weka.clusterers.Clusterer&gt; (property: clusterer)
 * &nbsp;&nbsp;&nbsp;The Weka clusterer to train on the input data.
 * &nbsp;&nbsp;&nbsp;default: weka.clusterers.SimpleKMeans -N 2 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 10
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@Deprecated
@DeprecatedClass(useInstead = {WekaClustererSetup.class, WekaTrainClusterer.class})
public class WekaClusterer
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the key for storing the current incremental clusterer in the backup. */
  public final static String BACKUP_INCREMENTALCLUSTERER = "incremental clusterer";

  /** the weka clusterer. */
  protected weka.clusterers.Clusterer m_Clusterer;

  /** the clusterer used when training incrementally. */
  protected weka.clusterers.Clusterer m_IncrementalClusterer;
  
  /** the post-processor. */
  protected AbstractClustererPostProcessor m_PostProcessor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Trains a clusterer based on the incoming dataset and output the "
      + "built clusterer alongside the training header (in a model container).\n"
      + "Incremental training is performed, if the input are weka.core.Instance "
      + "objects and the clusterer implements " + UpdateableClusterer.class.getName() + ".\n"
      + "If the incoming token does not encapsulate a dataset, then only a "
      + "new instance of the clusterer is sent around.\n\n"
      + "DEPRECATED\n\n"
      + "- Use " + WekaClustererSetup.class.getName() + " for definined a clusterer setup.\n"
      + "- Use " + WekaTrainClusterer.class.getName() + " for building a clusterer.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "clusterer", "clusterer",
	    new weka.clusterers.SimpleKMeans());

    m_OptionManager.add(
	    "post-processor", "postProcessor",
	    new PassThrough());
  }

  /**
   * Sets the clusterer to use.
   *
   * @param value	the clusterer
   */
  public void setClusterer(weka.clusterers.Clusterer value) {
    m_Clusterer = value;
    reset();
  }

  /**
   * Returns the clusterer in use.
   *
   * @return		the clusterer
   */
  public weka.clusterers.Clusterer getClusterer() {
    return m_Clusterer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String clustererTipText() {
    return "The Weka clusterer to train on the input data.";
  }

  /**
   * Sets the post-processor to use.
   *
   * @param value	the post-processor
   */
  public void setPostProcessor(AbstractClustererPostProcessor value) {
    m_PostProcessor = value;
    reset();
  }

  /**
   * Returns the post-processor in use.
   *
   * @return		the post-processor
   */
  public AbstractClustererPostProcessor getPostProcessor() {
    return m_PostProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String postProcessorTipText() {
    return "The post-processor to use on model containers.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "clusterer", m_Clusterer.getClass());
    result += QuickInfoHelper.toString(this, "postProcessor", m_PostProcessor, ", post-processor");
    
    return result;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_INCREMENTALCLUSTERER);
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

    if (m_IncrementalClusterer != null)
      result.put(BACKUP_INCREMENTALCLUSTERER, m_IncrementalClusterer);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_INCREMENTALCLUSTERER)) {
      m_IncrementalClusterer = (weka.clusterers.Clusterer) state.get(BACKUP_INCREMENTALCLUSTERER);
      state.remove(BACKUP_INCREMENTALCLUSTERER);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_IncrementalClusterer = null;
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
   * @return		<!-- flow-generates-start -->adams.flow.container.ModelContainer.class, weka.clusterers.Clusterer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaModelContainer.class, weka.clusterers.Clusterer.class};
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
    weka.clusterers.Clusterer	cls;
    WekaModelContainer		cont;

    result = null;

    try {
      if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instances)) {
	data = (Instances) m_InputToken.getPayload();
	cls  = (weka.clusterers.Clusterer) Utils.deepCopy(m_Clusterer);
	cls.buildClusterer(data);
	cont = new WekaModelContainer(cls, new Instances(data, 0), data);
	cont = m_PostProcessor.postProcess(cont);
	m_OutputToken = new Token(cont);
      }
      else if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instance)) {
	if (!(m_Clusterer instanceof UpdateableClusterer)) {
	  result = m_Clusterer.getClass().getName() + " is not an incremental clusterer!";
	}
	else {
	  inst = (Instance) m_InputToken.getPayload();
	  if (m_IncrementalClusterer == null) {
	    m_IncrementalClusterer = weka.clusterers.AbstractClusterer.makeCopy(m_Clusterer);
	    data = new Instances(inst.dataset(), 1);
	    data.add((Instance) inst.copy());
	    m_IncrementalClusterer.buildClusterer(data);
	  }
	  else {
	    ((UpdateableClusterer) m_IncrementalClusterer).updateClusterer(inst);
	    ((UpdateableClusterer) m_IncrementalClusterer).updateFinished();
	  }
	  m_OutputToken = new Token(new WekaModelContainer(m_IncrementalClusterer, new Instances(inst.dataset(), 0)));
	}
      }
      else {
	cls = (weka.clusterers.Clusterer) Utils.deepCopy(m_Clusterer);
	m_OutputToken = new Token(cls);
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to process input: " + m_InputToken.getPayload(), e);
    }

    return result;
  }
}
