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
 * EnumValueDefinition.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.valuedefinition;

import adams.core.EnumHelper;
import adams.core.Utils;
import adams.core.base.BaseClassname;
import adams.core.io.ConsoleHelper;
import adams.data.report.DataType;
import adams.flow.source.EnterManyValues;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import nz.ac.waikato.cms.locator.ClassLocator;

/**
 * Specialized definition for enums.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see EnterManyValues
 */
public class EnumValueDefinition
  extends AbstractValueDefinition {

  private static final long serialVersionUID = -7010111763801708574L;

  /** the enum to select. */
  protected BaseClassname m_EnumClass;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to select an enum.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "enum-class", "enumClass",
      new BaseClassname(DataType.class));
  }

  /**
   * Sets the enum class to select.
   *
   * @param value	the class
   */
  public void setEnumClass(BaseClassname value) {
    if ((value != null) && (value.classValue() != null)) {
      if (ClassLocator.isSubclass(Enum.class, value.classValue())) {
	m_EnumClass = value;
	reset();
      }
      else {
	getLogger().warning("Not an enum class: " + value);
      }
    }
  }

  /**
   * Returns the enum class to select.
   *
   * @return 		the class
   */
  public BaseClassname getEnumClass() {
    return m_EnumClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String enumClassTipText() {
    return "The enum class to select.";
  }

  /**
   * Returns the type of the value.
   *
   * @return 		the type
   */
  @Override
  public PropertyType getType() {
    return PropertyType.LIST;
  }

  /**
   * Sets the default value as string.
   *
   * @param value	the default value
   */
  @Override
  public void setDefaultValueAsString(String value) {
  }

  /**
   * Returns the default of the value as string.
   *
   * @return 		the default
   */
  @Override
  public String getDefaultValueAsString() {
    String[]	values;

    values = getEnumValues();
    if (values.length == 0)
      return "";
    else
      return values[0];
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
   * Returns the available enum values.
   *
   * @return		the values
   */
  protected String[] getEnumValues() {
    String[] 	result;
    Object[] 	values;
    int		i;

    values = EnumHelper.getValues(m_EnumClass.classValue());
    // no items to choose from?
    if (values.length == 0) {
      getLogger().severe("No enum values defined!");
      return new String[0];
    }

    result = new String[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = values[i].toString();

    return result;
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
    String[] 	values;

    if (!check())
      return false;

    values = getEnumValues();
    // no items to choose from?
    if (values.length == 0)
      return false;

    panel.addPropertyType(getName(), getType());
    panel.setList(getName(), values);
    panel.setListDefault(getName(), values[0]);
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
    String[]	values;

    result = null;
    values = getEnumValues();
    if (values.length == 0)
      return null;

    if (!check())
      return null;

    do {
      msg = "Please select " + (getDisplay().trim().isEmpty() ? getName() : getDisplay())
	+ " from " + Utils.arrayToString(values)
	+ " (type: " + getType() + "): ";

      choice = ConsoleHelper.enterValue(msg, getDefaultValueAsString());
      if (choice == null)
        break;

      // valid?
      for (String value: values) {
        if (value.equals(choice)) {
          result = choice;
          break;
	}
      }
    }
    while (result == null);

    return result;
  }
}
