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
 * FloatEditor.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.core.Utils;
import adams.gui.core.NumberTextField;

/**
 * A custom editor for Floats.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FloatEditor
  extends AbstractFloatingPointNumberEditor {

  /**
   * Initializes the editor.
   */
  public FloatEditor() {
    super();

    m_CurrentValue = new Float(0.0f);
  }

  /**
   * Returns the type of number to check for.
   *
   * @return		the type of number
   */
  @Override
  protected NumberTextField.Type getType() {
    return NumberTextField.Type.FLOAT;
  }

  /**
   * Set (or change) the object that is to be edited.  Primitive types such
   * as "int" must be wrapped as the corresponding object type such as
   * "java.lang.Float".
   *
   * @param value The new target object to be edited.  Note that this
   *     object should not be modified by the PropertyEditor, rather
   *     the PropertyEditor should create a new object to hold any
   *     modified value.
   */
  @Override
  public void setValue(Object value) {
    m_CurrentValue = new Float(((Number) value).floatValue());
    firePropertyChange();
  }

  /**
   * Gets the property value.
   *
   * @return The value of the property.  Primitive types such as "int" will
   * be wrapped as the corresponding object type such as "java.lang.Float".
   */

  @Override
  public Object getValue() {
    return m_CurrentValue;
  }

  /**
   * Parses the string and returns an object of the correct class.
   *
   * @param text	the string to parse
   * @return		the generated object
   * @throws IllegalArgumentException	if parsing fails
   */
  @Override
  protected Object parse(String text) throws IllegalArgumentException {
    Object	result;

    try {
      if (text.length() == 0)
	text = "0";
      result = Utils.toFloat(text);
    }
    catch (Exception e) {
      throw new IllegalArgumentException(e);
    }

    return result;
  }
}
