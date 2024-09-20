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
 * ShortEditor.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import javax.swing.SpinnerNumberModel;

/**
 * A custom editor for Shorts.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ShortEditor
  extends AbstractIntegralNumberEditor {

  /**
   * Initializes the editor.
   */
  public ShortEditor() {
    super();

    m_CurrentValue = (short) 0;
  }

  /**
   * Set (or change) the object that is to be edited.  Primitive types such
   * as "int" must be wrapped as the corresponding object type such as
   * "java.lang.Short".
   *
   * @param value The new target object to be edited.  Note that this
   *     object should not be modified by the PropertyEditor, rather
   *     the PropertyEditor should create a new object to hold any
   *     modified value.
   */
  public void setValue(Object value) {
    m_CurrentValue = ((Number) value).shortValue();
    firePropertyChange();
  }

  /**
   * Gets the property value.
   *
   * @return The value of the property.  Primitive types such as "int" will
   * be wrapped as the corresponding object type such as "java.lang.Short".
   */

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
  protected Object parse(String text) throws IllegalArgumentException {
    Object	result;

    try {
      if (text.isEmpty())
	text = "0";
      result = Short.parseShort(text);
    }
    catch (Exception e) {
      throw new IllegalArgumentException(e);
    }

    return result;
  }

  /**
   * Updates the bounds of the spinner model.
   *
   * @param model	the model to update
   */
  protected void updateBounds(SpinnerNumberModel model) {
    if (m_LowerBound == null)
      model.setMinimum(Short.MIN_VALUE);
    else
      model.setMinimum(m_LowerBound.intValue());

    if (m_UpperBound == null)
      model.setMaximum(Short.MAX_VALUE);
    else
      model.setMaximum(m_UpperBound.intValue());
  }
}
