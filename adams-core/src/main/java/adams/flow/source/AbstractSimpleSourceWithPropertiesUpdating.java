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
 * AbstractSimpleSourceWithPropertiesUpdating.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.VariableName;
import adams.core.base.BaseString;
import adams.core.discovery.PropertyPath.PropertyContainer;
import adams.flow.core.FlowContextHandler;
import adams.flow.core.PropertiesUpdater;
import adams.flow.core.PropertiesUpdaterHelper;

/**
 * Ancestor for simple sources that allow changing the object's properties
 * using variables.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSimpleSourceWithPropertiesUpdating
  extends AbstractSimpleSource
  implements PropertiesUpdater {

  private static final long serialVersionUID = 2693280797485493919L;

  /** the property paths. */
  protected BaseString[] m_Properties;

  /** the variables to update the properties with. */
  protected VariableName[] m_VariableNames;

  /** the property containers of the properties to update. */
  protected transient PropertyContainer[] m_Containers;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "property", "properties",
      new BaseString[0]);

    m_OptionManager.add(
      "variable", "variableNames",
      new VariableName[0]);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Containers = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    if (m_Properties.length > 0) {
      result = QuickInfoHelper.toString(this, "properties", (m_Properties.length == 1 ? m_Properties[0] : m_Properties.length), "props: ");
      result += QuickInfoHelper.toString(this, "variableNames", (m_VariableNames.length == 1 ? m_VariableNames[0] : m_VariableNames.length), ", vars: ");
    }
    else {
      result = "";
    }

    return result;
  }

  /**
   * Sets the properties to update.
   *
   * @param value	the properties
   */
  public void setProperties(BaseString[] value) {
    m_Properties    = value;
    m_VariableNames = (VariableName[]) Utils.adjustArray(m_VariableNames, m_Properties.length, new VariableName("unknown"));
    reset();
  }

  /**
   * Returns the properties to update.
   *
   * @return		the properties
   */
  public BaseString[] getProperties() {
    return m_Properties;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String propertiesTipText() {
    return "The properties to update with the values associated with the specified values.";
  }

  /**
   * Sets the variables to use.
   *
   * @param value	the variables
   */
  public void setVariableNames(VariableName[] value) {
    m_VariableNames = value;
    m_Properties    = (BaseString[]) Utils.adjustArray(m_Properties, m_VariableNames.length, new BaseString("property"));
    reset();
  }

  /**
   * Returns the variables to use.
   *
   * @return		the variables
   */
  public VariableName[] getVariableNames() {
    return m_VariableNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNamesTipText() {
    return "The names of the variables to update the properties with.";
  }

  /**
   * Initializes the property containers.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String setUpContainersIfNecessary(Object obj) {
    if (m_Containers == null)
      return setUpContainers(obj);
    else
      return null;
  }

  /**
   * Initializes the property containers.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String setUpContainers(Object obj) {
    String		result;
    MessageCollection 	errors;

    result = super.setUp();

    if (result == null) {
      errors       = new MessageCollection();
      m_Containers = PropertiesUpdaterHelper.configure(obj, m_Properties, errors);
      if (m_Containers == null) {
	if (!errors.isEmpty())
	  result = errors.toString();
	else
	  result = "Failed to configure property containers!";
      }
    }

    return result;
  }

  /**
   * Updates the object with the current variable values.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String updateObject(Object obj) {
    String		result;
    MessageCollection	errors;

    result = null;

    errors = new MessageCollection();
    PropertiesUpdaterHelper.update(this, obj, m_Properties, m_VariableNames, m_Containers, errors);
    if (!errors.isEmpty())
      result = errors.toString();

    if (result == null) {
      if (obj instanceof FlowContextHandler)
        ((FlowContextHandler) obj).setFlowContext(this);
    }

    return result;
  }
}
