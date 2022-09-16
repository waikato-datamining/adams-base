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
 * ListSelectionValueDefinition.java
 * Copyright (C) 2019-2022 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.valuedefinition;

import adams.core.Utils;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.io.ConsoleHelper;
import adams.core.option.OptionUtils;
import adams.flow.source.EnterManyValues;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Specialized definition for lists, allowing to define a default value.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see EnterManyValues
 */
public class ListSelectionValueDefinition
    extends AbstractValueDefinition {

  private static final long serialVersionUID = -7010111763801708574L;

  /** the list values to choose from. */
  protected BaseString[] m_Values;

  /** the default value. */
  protected String m_DefaultValue;

  /** whether to use multi-select. */
  protected boolean m_MultiSelect;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to select an item from a predefined list of values.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"value", "values",
	new BaseString[0]);

    m_OptionManager.add(
	"default-value", "defaultValue",
	"");

    m_OptionManager.add(
	"multi-select", "multiSelect",
	false);
  }

  /**
   * Sets the available list items to choose from.
   *
   * @param value	the items
   */
  public void setValues(BaseString[] value) {
    m_Values = value;
    reset();
  }

  /**
   * Returns the available list items to choose from.
   *
   * @return 		the items
   */
  public BaseString[] getValues() {
    return m_Values;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String valuesTipText() {
    return "The list values to choose from.";
  }

  /**
   * Sets the default list value.
   *
   * @param value	the default
   */
  public void setDefaultValue(String value) {
    m_DefaultValue = value;
    reset();
  }

  /**
   * Returns the default list value.
   *
   * @return 		the default
   */
  public String getDefaultValue() {
    return m_DefaultValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String defaultValueTipText() {
    return "The default list item.";
  }

  /**
   * Sets whether to allow selection of multiple options.
   *
   * @param value	true if multi-select enabled
   */
  public void setMultiSelect(boolean value) {
    m_MultiSelect = value;
    reset();
  }

  /**
   * Returns whether multiple options can be selected.
   *
   * @return		true if multi-select enabled
   */
  public boolean isMultiSelect() {
    return m_MultiSelect;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String multiSelectTipText() {
    return "Whether to allow multiple items to be selected.";
  }

  /**
   * Returns the type of the value.
   *
   * @return 		the type
   */
  @Override
  public PropertyType getType() {
    if (m_MultiSelect)
      return PropertyType.LIST_MULTI_SELECTION;
    else
      return PropertyType.LIST;
  }

  /**
   * Sets the default value as string.
   *
   * @param value	the default value
   */
  @Override
  public void setDefaultValueAsString(String value) {
    m_DefaultValue = value;
  }

  /**
   * Returns the default of the value as string.
   *
   * @return 		the default
   */
  @Override
  public String getDefaultValueAsString() {
    return m_DefaultValue;
  }

  /**
   * Returns whether flow context is required.
   *
   * @return		true if required
   */
  protected boolean requiresFlowContext() {
    return false;
  }

  /**
   * Adds the value to the panel.
   *
   * @param panel	the panel to add to
   * @return		true if successfully added
   */
  @Override
  public boolean addToPanel(PropertiesParameterPanel panel) {
    boolean	found;
    String	defValue;

    if (!check())
      return false;

    // no items to choose from?
    if (m_Values.length == 0) {
      getLogger().severe("No list items defined!");
      return false;
    }

    // esnure that default is item of list
    found = false;
    for (BaseString value: m_Values) {
      if (value.getValue().equals(m_DefaultValue)) {
	found = true;
	break;
      }
    }

    if (!found) {
      if (!m_DefaultValue.isEmpty()) {
	getLogger().severe("Failed to located default value '" + m_DefaultValue + "' in list values '" + Utils.arrayToString(m_Values) + "'!");
	return false;
      }
      else {
	defValue = m_Values[0].getValue();
      }
    }
    else {
      defValue = m_DefaultValue;
    }

    panel.addPropertyType(getName(), getType());
    panel.setList(getName(), BaseObject.toStringArray(m_Values));
    panel.setListDefault(getName(), defValue);
    if (!getDisplay().trim().isEmpty())
      panel.setLabel(getName(), getDisplay());
    if (!getHelp().isEmpty())
      panel.setHelp(getName(), getHelp());
    return true;
  }

  /**
   * Prompts the user to enter a value in headless mode and returns it.
   *
   * @return		the entered value, null if canceled
   */
  @Override
  public String headlessInteraction() {
    String	msg;
    String	choice;
    String 	result;
    Set<String> choiceSet;
    String[]	values;

    result = null;

    if (!check())
      return null;

    values = BaseObject.toStringArray(m_Values);
    do {
      msg = "Please select " + (getDisplay().trim().isEmpty() ? getName() : getDisplay())
	  + " from " + Utils.arrayToString(m_Values)
	  + " (type: " + getType() + ", " + (m_MultiSelect ? "separate multiple values with blank" : "") + "): ";

      choice = ConsoleHelper.enterValue(msg, getDefaultValueAsString());
      if (choice == null)
	break;

      // valid?
      choiceSet = new HashSet<>();
      if (m_MultiSelect) {
        try {
	  choiceSet.addAll(Arrays.asList(OptionUtils.splitOptions(choice)));
	}
        catch (Exception e) {
          ConsoleHelper.printlnOut("Failed to split string into separate choices: " + e.getMessage());
          continue;
	}
      }
      else {
        choiceSet.add(choice);
      }
      choiceSet.removeAll(Arrays.asList(values));
      if (choiceSet.size() == 0)
        result = choice;
    }
    while (result == null);

    return result;
  }
}
