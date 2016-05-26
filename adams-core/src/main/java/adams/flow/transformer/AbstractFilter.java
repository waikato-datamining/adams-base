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
 * AbstractFilter.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.data.container.DataContainer;
import adams.data.filter.BatchFilter;
import adams.data.filter.TrainableBatchFilter;
import adams.db.DatabaseConnectionHandler;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeEvent.Type;
import adams.flow.core.Token;
import adams.flow.core.VariableMonitor;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ancestor for domain-specific filter transformers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFilter
  extends AbstractDataContainerTransformer
  implements ProvenanceSupporter, VariableMonitor {

  /** for serialization. */
  private static final long serialVersionUID = 4527040722924866539L;

  /** the filter to apply. */
  protected adams.data.filter.Filter m_Filter;

  /** the variable to listen to. */
  protected VariableName m_VariableName;

  /** whether the database connection has been updated. */
  protected boolean m_DatabaseConnectionUpdated;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Filters data using the specified filter.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "filter", "filter",
      new adams.data.filter.PassThrough());

    m_OptionManager.add(
      "var-name", "variableName",
      new VariableName());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_DatabaseConnectionUpdated = false;
  }

  /**
   * Sets the filter to use.
   *
   * @param value	the filter
   */
  public void setFilter(adams.data.filter.Filter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter in use.
   *
   * @return		the filter
   */
  public adams.data.filter.Filter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter to use for filtering the data.";
  }

  /**
   * Sets the name of the variable to monitor.
   *
   * @param value	the name
   */
  public void setVariableName(VariableName value) {
    m_VariableName = value;
    reset();
  }

  /**
   * Returns the name of the variable to monitor.
   *
   * @return		the name
   */
  public VariableName getVariableName() {
    return m_VariableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNameTipText() {
    return "The variable to monitor for resetting trainable batch filters.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "filter", m_Filter);
    result += QuickInfoHelper.toString(this, "variableName", m_VariableName.paddedValue(), ", monitor: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the default DataContainer class for the project
   */
  public Class[] accepts() {
    List<Class> 	result;

    result = new ArrayList<>();
    result.addAll(Arrays.asList(super.accepts()));

    if (m_Filter instanceof BatchFilter) {
      result.add(DataContainer[].class);
      result.add(Array.newInstance(getDataContainerClass(), 0).getClass());
    }

    return result.toArray(new Class[result.size()]);
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the default DataContainer class for the project
   */
  public Class[] generates() {
    List<Class> 	result;

    result = new ArrayList<>();
    result.addAll(Arrays.asList(super.generates()));

    if (m_Filter instanceof BatchFilter) {
      result.add(DataContainer[].class);
      result.add(Array.newInstance(getDataContainerClass(), 0).getClass());
    }

    return result.toArray(new Class[result.size()]);
  }

  /**
   * Returns the data container class in use.
   *
   * @return		the container class
   */
  @Override
  protected abstract Class getDataContainerClass();

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  protected abstract adams.db.AbstractDatabaseConnection getDatabaseConnection();

  /**
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  @Override
  public void variableChanged(VariableChangeEvent e) {
    super.variableChanged(e);
    if ((e.getType() == Type.MODIFIED) || (e.getType() == Type.ADDED)) {
      if (e.getName().equals(m_VariableName.getValue())) {
	if (m_Filter instanceof TrainableBatchFilter)
	  ((TrainableBatchFilter) m_Filter).resetFilter();
        if (isLoggingEnabled())
          getLogger().info("Reset 'trainable filter'");
      }
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    DataContainer 		cont;
    DataContainer[]		conts;
    BatchFilter			bfilter;
    TrainableBatchFilter 	tfilter;

    result = null;

    if (!m_DatabaseConnectionUpdated) {
      m_DatabaseConnectionUpdated = true;
      if (m_Filter instanceof DatabaseConnectionHandler)
	((DatabaseConnectionHandler) m_Filter).setDatabaseConnection(getDatabaseConnection());
    }

    if (m_InputToken.getPayload() instanceof DataContainer) {
      cont = m_Filter.filter((DataContainer) m_InputToken.getPayload());
      if (cont == null)
	result = "No data obtained from filter: " + m_InputToken;
      else
	m_OutputToken = new Token(cont);
    }
    else if ((m_Filter instanceof BatchFilter) && (m_InputToken.getPayload().getClass().isArray())) {
      bfilter = (BatchFilter) m_Filter;
      if (bfilter instanceof TrainableBatchFilter) {
	tfilter = (TrainableBatchFilter) bfilter;
	if (!tfilter.isTrained()) {
	  if (isLoggingEnabled())
	    getLogger().info("Training filter with input data");
	  tfilter.trainFilter((DataContainer[]) m_InputToken.getPayload());
	}
      }
      conts = bfilter.batchFilter((DataContainer[]) m_InputToken.getPayload());
      if (conts == null)
	result = "No data obtained from filter: " + m_InputToken;
      else
	m_OutputToken = new Token(conts);
    }

    if (m_OutputToken != null)
      updateProvenance(m_OutputToken);

    m_Filter.cleanUp();

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
