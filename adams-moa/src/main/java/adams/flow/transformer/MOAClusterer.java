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
 * MOAClusterer.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.Hashtable;

import moa.clusterers.CobWeb;
import moa.options.ClassOption;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.MOAUtils;
import adams.core.QuickInfoHelper;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.source.MOAClustererSetup;

/**
 <!-- globalinfo-start -->
 * Trains a MOA clusterer based on the incoming data.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.ModelContainer<br/>
 * &nbsp;&nbsp;&nbsp;moa.clusterers.Clusterer<br/>
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
 * &nbsp;&nbsp;&nbsp;default: MOAClusterer
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
 * <pre>-clusterer &lt;moa.options.ClassOption&gt; (property: clusterer)
 * &nbsp;&nbsp;&nbsp;The MOA clusterer to train on the input data and outputs the built clusterer
 * &nbsp;&nbsp;&nbsp;alongside the training header (in a model container).
 * &nbsp;&nbsp;&nbsp;If the incoming token does not encapsulate an weka.core.Instance, then only
 * &nbsp;&nbsp;&nbsp;a new instance of the clusterer is sent around.
 * &nbsp;&nbsp;&nbsp;default: moa.clusterers.CobWeb
 * </pre>
 *
 * <pre>-output-interval &lt;int&gt; (property: outputInterval)
 * &nbsp;&nbsp;&nbsp;The number of tokens to wait before forwarding the trainined clusterer.
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
public class MOAClusterer
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -6015760924658904729L;

  /** the key for storing the current clusterer in the backup. */
  public final static String BACKUP_CLUSTERER = "clusterer";

  /** the MOA clusterer. */
  protected moa.options.ClassOption m_Clusterer;

  /** the actual clusterer to use. */
  protected moa.clusterers.Clusterer m_ActualClusterer;

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
        "Trains a MOA clusterer based on the incoming data.\n\n"
        + "DEPRECATED\n\n"
        + "- Use " + MOAClustererSetup.class.getName() + " for definined a clusterer setup.\n"
        + "- Use " + MOATrainClusterer.class.getName() + " for building a clusterer.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "clusterer", "clusterer",
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

    m_ActualClusterer = null;
    m_Count           = 0;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Clusterer = getDefaultOption();
  }
  
  /**
   * Returns the default clusterer.
   *
   * @return		the clusterer
   */
  protected moa.clusterers.Clusterer getDefaultClusterer() {
    return new CobWeb();
  }

  /**
   * Returns the default class option.
   *
   * @return		the option
   */
  protected ClassOption getDefaultOption() {
    return new ClassOption(
	"clusterer",
	'c',
	"The MOA clusterer to use from within ADAMS.",
	moa.clusterers.Clusterer.class,
	getDefaultClusterer().getClass().getName().replace("moa.clusterers.", ""),
	getDefaultClusterer().getClass().getName());
  }

  /**
   * Sets the clusterer to use.
   *
   * @param value	the clusterer
   */
  public void setClusterer(ClassOption value) {
    m_Clusterer.setValueViaCLIString(value.getValueAsCLIString());
    reset();
  }

  /**
   * Returns the clusterer in use.
   *
   * @return		the clusterer
   */
  public ClassOption getClusterer() {
    return m_Clusterer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String clustererTipText() {
    return
        "The MOA clusterer to train on the input data and outputs the "
      + "built clusterer alongside the training header (in a model container).\n"
      + "If the incoming token does not encapsulate an weka.core.Instance, then "
      + "only a new instance of the clusterer is sent around.";
  }

  /**
   * Returns the current clusterer, based on the class option.
   *
   * @return		the clusterer
   * @see		#getClusterer()
   */
  protected moa.clusterers.Clusterer getCurrentClusterer() {
    return (moa.clusterers.Clusterer) MOAUtils.fromOption(m_Clusterer);
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
    return "The number of tokens to wait before forwarding the trainined clusterer.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "clusterer", getCurrentClusterer().getClass());
    result += QuickInfoHelper.toString(this, "outputInterval", m_OutputInterval, "/");

    return result;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_CLUSTERER);
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

    if (m_ActualClusterer != null)
      result.put(BACKUP_CLUSTERER, m_ActualClusterer);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_CLUSTERER)) {
      m_ActualClusterer = (moa.clusterers.Clusterer) state.get(BACKUP_CLUSTERER);
      state.remove(BACKUP_CLUSTERER);
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
   * @return		<!-- flow-generates-start -->adams.flow.container.ModelContainer.class, moa.clusterers.Clusterer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaModelContainer.class, moa.clusterers.Clusterer.class};
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
    moa.clusterers.Clusterer	cls;

    result = null;

    try {
      if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instance)) {
	// train
	inst = (Instance) m_InputToken.getPayload();
	if (m_ActualClusterer == null)
	  m_ActualClusterer = getCurrentClusterer();
	m_ActualClusterer.trainOnInstance(inst);

	// generate output
	m_Count++;
	if (m_Count % m_OutputInterval == 0) {
	  m_Count = 0;
	  m_OutputToken = new Token(new WekaModelContainer(m_ActualClusterer, new Instances(inst.dataset(), 0)));
	}
      }
      else {
	cls = (moa.clusterers.Clusterer) MOAUtils.fromOption(m_Clusterer);
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

    m_ActualClusterer = null;
  }
}
