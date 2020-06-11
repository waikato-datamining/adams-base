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
 * DefaultValueDefinition.java
 * Copyright (C) 2013-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source.valuedefinition;

import adams.core.io.ConsoleHelper;
import adams.core.io.ForwardSlashSupporter;
import adams.flow.source.EnterManyValues;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyHint;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

/**
 * Combines name, type and default value for a single value.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see EnterManyValues
 */
public class DefaultValueDefinition
  extends AbstractValueDefinition
  implements ForwardSlashSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1003051563895321458L;

  /** the type of the value. */
  protected PropertyType m_Type;
  
  /** the default value (string representation). */
  protected String m_DefaultValue;

  /** whether to output forward slashes. */
  protected boolean m_UseForwardSlashes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Defintition for a value: name, type and default value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "type",
	    PropertyType.STRING);

    m_OptionManager.add(
	    "default-value", "defaultValue",
	    "");

    m_OptionManager.add(
	    "use-forward-slashes", "useForwardSlashes",
	    false);
  }

  /**
   * Sets the type of the value.
   *
   * @param value	the type
   */
  public void setType(PropertyType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of the value.
   *
   * @return 		the type
   */
  public PropertyType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of the value.";
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
    return "The default of the value in its string representation.";
  }

  /**
   * Sets the default value as string.
   *
   * @param value	the default value
   */
  public void setDefaultValueAsString(String value) {
    setDefaultValue(value);
  }

  /**
   * Returns the default of the value as string.
   *
   * @return 		the default
   */
  public String getDefaultValueAsString() {
    return getDefaultValue();
  }

  /**
   * Sets whether to use forward slashes in the output.
   *
   * @param value	if true then use forward slashes
   */
  public void setUseForwardSlashes(boolean value) {
    m_UseForwardSlashes = value;
    reset();
  }

  /**
   * Returns whether to use forward slashes in the output.
   *
   * @return		true if forward slashes are used
   */
  public boolean getUseForwardSlashes() {
    return m_UseForwardSlashes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useForwardSlashesTipText() {
    return
	"If enabled and the type is a file- or directory-based one, then forward slashes are used in the output (but "
	+ "the '\\\\' prefix of UNC paths is not converted).";
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
  public boolean addToPanel(PropertiesParameterPanel panel) {
    if (!check())
      return false;

    panel.addPropertyType(getName(), getType());
    if (!getDisplay().trim().isEmpty())
      panel.setLabel(getName(), getDisplay());
    if (!getHelp().trim().isEmpty())
      panel.setHelp(getName(), getHelp());
    if (getUseForwardSlashes())
      panel.addPropertyHint(getName(), PropertyHint.FORWARD_SLASHES);
    return true;
  }

  /**
   * Prompts the user to enter a value in headless mode and returns it.
   *
   * @return		the entered value, null if canceled
   */
  public String headlessInteraction() {
    String	msg;

    if (!check())
      return null;

    msg = "Please enter " + (getDisplay().trim().isEmpty() ? getName() : getDisplay())
      + " (type: " + getType() + "): ";

    return ConsoleHelper.enterValue(msg, getDefaultValueAsString());
  }

  /**
   * Returns whether this definition can be restored from a restoration file.
   *
   * @return		true if can be restored
   */
  public boolean canBeRestored() {
    switch (m_Type) {
      case COMMA_SEPARATED_LIST:
      case COMMA_SEPARATED_LIST_FIXED:
      case BLANK_SEPARATED_LIST:
      case BLANK_SEPARATED_LIST_FIXED:
        return false;
      default:
        return true;
    }
  }
}
