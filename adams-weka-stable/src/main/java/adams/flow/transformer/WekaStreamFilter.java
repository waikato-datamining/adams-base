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
 * WekaStreamFilter.java
 * Copyright (C) 2012-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;

import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Filters Instance objects using the specified filter.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * &nbsp;&nbsp;&nbsp;adams.data.instance.Instance<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * &nbsp;&nbsp;&nbsp;adams.data.instance.Instance<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaStreamFilter
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-property &lt;adams.core.base.BaseString&gt; [-property ...] (property: properties)
 * &nbsp;&nbsp;&nbsp;The properties to update with the values associated with the specified values.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-variable &lt;adams.core.VariableName&gt; [-variable ...] (property: variableNames)
 * &nbsp;&nbsp;&nbsp;The names of the variables to update the properties with.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-filter &lt;weka.filters.StreamableFilter&gt; (property: filter)
 * &nbsp;&nbsp;&nbsp;The stream filter to use for filtering the Instance objects.
 * &nbsp;&nbsp;&nbsp;default: weka.filters.unsupervised.attribute.Add -N unnamed -C last
 * </pre>
 *
 * <pre>-keep &lt;boolean&gt; (property: keepRelationName)
 * &nbsp;&nbsp;&nbsp;If set to true, then the filter won't change the relation name of the incoming
 * &nbsp;&nbsp;&nbsp;dataset.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaStreamFilter
  extends AbstractTransformerWithPropertiesUpdating
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 9078845385089445202L;

  /** the key for storing the current initialized state in the backup. */
  public final static String BACKUP_INITIALIZED = "initialized";

  /** the filter to apply. */
  protected weka.filters.StreamableFilter m_Filter;

  /** whether to keep the incoming relation name. */
  protected boolean m_KeepRelationName;

  /** whether the filter has been initialized. */
  protected boolean m_Initialized;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Filters Instance objects using the specified filter.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filter",
	    new Add());

    m_OptionManager.add(
      "keep", "keepRelationName",
      false);
  }

  /**
   * Sets the filter to use.
   *
   * @param value	the filter
   */
  public void setFilter(weka.filters.StreamableFilter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter in use.
   *
   * @return		the filter
   */
  public weka.filters.StreamableFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The stream filter to use for filtering the Instance objects.";
  }

  /**
   * Sets whether the filter doesn't change the relation name.
   *
   * @param value	true if the filter won't change the relation name
   */
  public void setKeepRelationName(boolean value) {
    m_KeepRelationName = value;
    reset();
  }

  /**
   * Returns whether the filter doesn't change the relation name.
   *
   * @return		true if the filter doesn't change the relation name
   */
  public boolean getKeepRelationName() {
    return m_KeepRelationName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keepRelationNameTipText() {
    return
        "If set to true, then the filter won't change the relation name of the "
      + "incoming dataset.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	info;

    result  = QuickInfoHelper.toString(this, "filter", m_Filter.getClass());
    result += QuickInfoHelper.toString(this, "keepRelationName", m_KeepRelationName, "keep relation name", ", ");
    info    = super.getQuickInfo();
    if (!info.isEmpty())
      result += ", " + info;

    return result;
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

    result.put(BACKUP_INITIALIZED, m_Initialized);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_INITIALIZED)) {
      m_Initialized = (Boolean) state.get(BACKUP_INITIALIZED);
      state.remove(BACKUP_INITIALIZED);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Initialized = false;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		weka.core.Instance, weka.core.Instances, adams.data.instance.Instance
   */
  public Class[] accepts() {
    return new Class[]{weka.core.Instance.class, weka.core.Instances.class, adams.data.instance.Instance.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		weka.core.Instance, weka.core.Instances, adams.data.instance.Instance
   */
  public Class[] generates() {
    return new Class[]{weka.core.Instance.class, weka.core.Instances.class, adams.data.instance.Instance.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    weka.core.Instances			data;
    weka.core.Instance			inst;
    adams.data.instance.Instance	instA;
    weka.core.Instance			filteredInst;
    weka.core.Instances			filteredData;
    String				relation;
    weka.filters.Filter			filter;

    result = null;

    inst         = null;
    data         = null;
    filteredInst = null;
    filteredData = null;
    filter       = (weka.filters.Filter) m_Filter;
    if (m_InputToken.getPayload() instanceof weka.core.Instance)
      inst = (weka.core.Instance) m_InputToken.getPayload();
    else if (m_InputToken.getPayload() instanceof weka.core.Instances)
      data = (weka.core.Instances) m_InputToken.getPayload();
    else
      inst = ((adams.data.instance.Instance) m_InputToken.getPayload()).toInstance();
    if (data == null)
      data = inst.dataset();

    try {
      // initialize filter?
      if (!m_Initialized) {
        result = setUpContainers(filter);
        if (result == null)
          result = updateObject(filter);
        filter.setInputFormat(new weka.core.Instances(data, 0));
      }

      if (result == null) {
        // filter data
        relation = data.relationName();
        if (inst == null) {
          filteredData = Filter.useFilter(data, filter);
          if (m_KeepRelationName)
            filteredData.setRelationName(relation);
        }
        else {
          filter.input(inst);
          filter.batchFinished();
          filteredInst = filter.output();
          if (m_KeepRelationName)
            filteredInst.dataset().setRelationName(relation);
        }

        // build output token
        if (m_InputToken.getPayload() instanceof weka.core.Instance) {
          m_OutputToken = new Token(filteredInst);
        }
        else if (m_InputToken.getPayload() instanceof weka.core.Instances) {
          m_OutputToken = new Token(filteredData);
        }
        else {
          instA = new adams.data.instance.Instance();
          instA.set(filteredInst);
          m_OutputToken = new Token(instA);
        }
      }
    }
    catch (Exception e) {
      result = handleException("Failed to filter data: ", e);
    }

    if (m_OutputToken != null)
      updateProvenance(m_OutputToken);

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
      cont.addProvenance(new ProvenanceInformation(ActorType.PREPROCESSOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
