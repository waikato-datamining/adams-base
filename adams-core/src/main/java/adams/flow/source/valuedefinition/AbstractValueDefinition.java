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
 * AbstractValueDefinition.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source.valuedefinition;

import adams.core.option.AbstractOptionHandler;
import adams.flow.source.EnterManyValues;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

/**
 * Combines name, type and default value for a single value.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see EnterManyValues
 */
public abstract class AbstractValueDefinition
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 1003051563895321458L;

  /** the name of the value. */
  protected String m_Name;

  /** the display text. */
  protected String m_Display;

  /** the help text. */
  protected String m_Help;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "name", "name",
	    "");

    m_OptionManager.add(
	    "display", "display",
	    "");

    m_OptionManager.add(
	    "help", "help",
	    "");
  }

  /**
   * Sets the name of the value.
   *
   * @param value	the name
   */
  public void setName(String value) {
    m_Name = value;
    reset();
  }

  /**
   * Returns the name of the value.
   *
   * @return 		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String nameTipText() {
    return "The name of the value.";
  }

  /**
   * Sets the display text for the value.
   *
   * @param value	the display text
   */
  public void setDisplay(String value) {
    m_Display = value;
    reset();
  }

  /**
   * Returns the display text for the value.
   *
   * @return 		the display text
   */
  public String getDisplay() {
    return m_Display;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String displayTipText() {
    return "The text to use as label for the value.";
  }

  /**
   * Sets the help text for the value.
   *
   * @param value	the help text
   */
  public void setHelp(String value) {
    m_Help = value;
    reset();
  }

  /**
   * Returns the help text for the value.
   *
   * @return 		the help text
   */
  public String getHelp() {
    return m_Help;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String helpTipText() {
    return "The help text to use for the value.";
  }

  /**
   * Returns the type of the value.
   *
   * @return 		the type
   */
  public abstract PropertyType getType();

  /**
   * Returns the default of the value as string.
   *
   * @return 		the default
   */
  public abstract String getDefaultValueAsString();

  /**
   * Adds the value to the panel.
   *
   * @param panel	the panel to add to
   * @return		true if successfully added
   */
  public abstract boolean addToPanel(PropertiesParameterPanel panel);

  /**
   * Prompts the user to enter a value in headless mode and returns it.
   *
   * @return		the entered value, null if canceled
   */
  public abstract String headlessInteraction();
}
