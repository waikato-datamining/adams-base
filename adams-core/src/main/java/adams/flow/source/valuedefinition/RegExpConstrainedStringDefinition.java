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

/**
 * RegExpConstrainedStringDefinition.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.valuedefinition;

import adams.core.base.BaseRegExp;
import adams.core.io.ConsoleHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

/**
 * Definition for a string that is constrained by a regular expression.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RegExpConstrainedStringDefinition
  extends AbstractValueDefinition {

  private static final long serialVersionUID = 5617527104506849371L;

  /** the regular expression to use. */
  protected BaseRegExp m_RegExp;

  /** the default value. */
  protected String m_DefaultValue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Allows the definition of string input that is constrained by a "
	+ "regular expression.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "default-value", "defaultValue",
      "");
  }

  /**
   * Sets the regular expression for constraining the input.
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression for constraining the input.
   *
   * @return 		the expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression for constraining the string input.";
  }

  /**
   * Sets the default of the value.
   *
   * @param value	the default
   */
  public void setDefaultValue(String value) {
    m_DefaultValue = value;
    reset();
  }

  /**
   * Returns the default of the value.
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
    return "The default value of the string.";
  }

  /**
   * Returns the type of the value.
   *
   * @return 		the type
   */
  @Override
  public PropertyType getType() {
    return PropertyType.REGEXP_CONSTRAINED_STRING;
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
   * Adds the value to the panel.
   *
   * @param panel	the panel to add to
   * @return		true if successfully added
   */
  @Override
  public boolean addToPanel(PropertiesParameterPanel panel) {
    panel.addPropertyType(m_Name, getType());
    panel.setRegExp(m_Name, m_RegExp);
    if (!getDisplay().trim().isEmpty())
      panel.setLabel(getName(), getDisplay());
    if (!getHelp().trim().isEmpty())
      panel.setHelp(getName(), getHelp());
    return true;
  }

  /**
   * Prompts the user to enter a value in headless mode and returns it.
   *
   * @return		the entered value, null if canceled
   */
  public String headlessInteraction() {
    String	result;
    String	msg;

    msg = "Please enter " + (getDisplay().trim().isEmpty() ? getName() : getDisplay())
      + " (type: " + getType() + "): ";

    do {
      result = ConsoleHelper.enterValue(msg, getDefaultValueAsString());
    }
    while ((result != null) && !m_RegExp.isMatch(result));

    return result;
  }
}
