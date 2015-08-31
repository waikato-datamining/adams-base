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
 * WekaFilter.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import weka.filters.AllFilter;
import weka.filters.Filter;

import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Filters Instances/Instance objects using the specified filter.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaFilter
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
 * <pre>-filter &lt;weka.filters.Filter [options]&gt; (property: filter)
 * &nbsp;&nbsp;&nbsp;The filter to use for filtering the Instances/Instance objects.
 * &nbsp;&nbsp;&nbsp;default: weka.filters.AllFilter
 * </pre>
 *
 * <pre>-init-once (property: initializeOnce)
 * &nbsp;&nbsp;&nbsp;If set to true, then the filter will get initialized only with the first
 * &nbsp;&nbsp;&nbsp;batch of data; otherwise every time data gets passed through.
 * </pre>
 *
 * <pre>-keep (property: keepRelationName)
 * &nbsp;&nbsp;&nbsp;If set to true, then the filter won't change the relation name of the incoming
 * &nbsp;&nbsp;&nbsp;dataset.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaFilter
  extends AbstractWekaInstanceAndWekaInstancesTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 9078845385089445202L;

  /** the key for storing the current initialized state in the backup. */
  public final static String BACKUP_INITIALIZED = "initialized";

  /** the filter to apply. */
  protected weka.filters.Filter m_Filter;

  /** the actual filter used. */
  protected weka.filters.Filter m_ActualFilter;

  /** whether to initialize filter only with the first batch. */
  protected boolean m_InitializeOnce;

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
    return "Filters Instances/Instance objects using the specified filter.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filter",
	    new AllFilter());

    m_OptionManager.add(
	    "init-once", "initializeOnce",
	    false);

    m_OptionManager.add(
	    "keep", "keepRelationName",
	    false);
  }

  /**
   * Sets the filter to use.
   *
   * @param value	the filter
   */
  public void setFilter(weka.filters.Filter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter in use.
   *
   * @return		the filter
   */
  public weka.filters.Filter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter to use for filtering the Instances/Instance objects.";
  }

  /**
   * Sets whether the filter gets initialized only with the first batch.
   *
   * @param value	true if the filter gets only initialized once
   */
  public void setInitializeOnce(boolean value) {
    m_InitializeOnce = value;
    reset();
  }

  /**
   * Returns whether the filter gets initialized only with the first batch.
   *
   * @return		true if the filter gets only initialized once
   */
  public boolean getInitializeOnce() {
    return m_InitializeOnce;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String initializeOnceTipText() {
    return
        "If set to true, then the filter will get initialized only with the "
      + "first batch of data; otherwise every time data gets passed through.";
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

    result  = QuickInfoHelper.toString(this, "filter", Utils.shorten(OptionUtils.getShortCommandLine(m_Filter), 40));
    result += QuickInfoHelper.toString(this, "keepRelationName", m_KeepRelationName, "keep relation name", ", ");

    return result;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_INITIALIZED);
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    weka.core.Instances			data;
    weka.core.Instances			filteredData;
    weka.core.Instance			inst;
    adams.data.instance.Instance	instA;
    weka.core.Instance			filteredInst;
    String				relation;

    result = null;

    data = null;
    inst = null;
    if (m_InputToken.getPayload() instanceof weka.core.Instance) {
      inst = (weka.core.Instance) m_InputToken.getPayload();
    }
    else if (m_InputToken.getPayload() instanceof adams.data.instance.Instance) {
      inst = ((adams.data.instance.Instance) m_InputToken.getPayload()).toInstance();
    }
    else if (m_InputToken.getPayload() instanceof weka.core.Instances) {
      data = (weka.core.Instances) m_InputToken.getPayload();
    }
    else {
      result = "Unhandled data type: " + m_InputToken.getPayload().getClass().getName();
    }

    if (result == null) {
      try {
	// initialize filter?
	if ((m_InitializeOnce && !m_Initialized) || (!m_InitializeOnce)) {
	  if (data == null) {
	    data = new weka.core.Instances(inst.dataset(), 0);
	    data.add(inst);
	  }
	  if (isLoggingEnabled())
	    getLogger().info("Creating copy of: " + OptionUtils.getCommandLine(m_Filter));
	  m_ActualFilter = (Filter) OptionUtils.forAnyCommandLine(Filter.class, OptionUtils.getCommandLine(m_Filter));
	  m_ActualFilter.setInputFormat(data);
	}

	// filter data
	filteredData = null;
	filteredInst = null;
	if (data != null) {
	  relation     = data.relationName();
	  filteredData = weka.filters.Filter.useFilter(data, m_ActualFilter);
	  if (m_KeepRelationName) {
	    filteredData.setRelationName(relation);
	    if (isLoggingEnabled())
	      getLogger().info("Setting relation name: " + relation);
	  }
	  m_Initialized = true;
	}
	else {
	  relation = inst.dataset().relationName();
	  m_ActualFilter.input(inst);
	  m_ActualFilter.batchFinished();
	  filteredInst = m_ActualFilter.output();
	  if (m_KeepRelationName) {
	    filteredInst.dataset().setRelationName(relation);
	    if (isLoggingEnabled())
	      getLogger().info("Setting relation name: " + relation);
	  }
	}

	// build output token
	if (inst != null) {
	  if (filteredInst != null) {
	    if (m_InputToken.getPayload() instanceof weka.core.Instance) {
	      m_OutputToken = new Token(filteredInst);
	    }
	    else {
	      instA = new adams.data.instance.Instance();
	      instA.set(filteredInst);
	      m_OutputToken = new Token(instA);
	    }
	  }
	  else if ((filteredData != null) && (filteredData.numInstances() > 0)) {
	    m_OutputToken = new Token(filteredData.instance(0));
	  }
	}
	else {
	  m_OutputToken = new Token(filteredData);
	}
      }
      catch (Exception e) {
	result = handleException("Failed to filter data: ", e);
      }
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
