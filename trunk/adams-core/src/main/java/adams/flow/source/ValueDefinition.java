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
 * ValueDefinition.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.option.AbstractOptionHandler;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

/**
 * Combines name, type and default value for a single value.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see EnterManyValues
 */
public class ValueDefinition
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 1003051563895321458L;

  /** the name of the value. */
  protected String m_Name;

  /** the display text. */
  protected String m_Display;

  /** the type of the value. */
  protected PropertyType m_Type;
  
  /** the default value (string representation). */
  protected String m_DefaultValue;
  
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
	    "name", "name",
	    "");

    m_OptionManager.add(
	    "display", "display",
	    "");

    m_OptionManager.add(
	    "type", "type",
	    PropertyType.STRING);

    m_OptionManager.add(
	    "default-value", "defaultValue",
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
}
