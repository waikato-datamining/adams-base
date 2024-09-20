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
 * BooleanEditor.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.gui.core.BaseCheckBox;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A custom editor for Booleans.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BooleanEditor
  extends AbstractBasicTypePropertyEditor {

  /** the current value. */
  protected Boolean m_Current;

  /**
   * Initializes the editor.
   */
  public BooleanEditor() {
    super();

    m_Current = false;
  }

  /**
   * Creates the custom editor to use.
   *
   * @return		the custom editor
   */
  protected JComponent createCustomEditor() {
    BaseCheckBox	result;

    result = new BaseCheckBox();
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	JCheckBox checkbox = (JCheckBox) e.getSource();
	if (!getValue().equals(checkbox.isSelected()))
	  setValue(checkbox.isSelected());
      }
    });

    return result;
  }

  /**
   * Set (or change) the object that is to be edited.  Primitive types such
   * as "int" must be wrapped as the corresponding object type such as
   * "java.lang.Boolean".
   *
   * @param value The new target object to be edited.  Note that this
   *     object should not be modified by the PropertyEditor, rather
   *     the PropertyEditor should create a new object to hold any
   *     modified value.
   */
  public void setValue(Object value) {
    m_Current = (Boolean) value;
    firePropertyChange();
  }

  /**
   * Gets the property value.
   *
   * @return The value of the property.  Primitive types such as "int" will
   * be wrapped as the corresponding object type such as "java.lang.Boolean".
   */

  public Object getValue() {
    return m_Current;
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
	text = "" + false;
      result = Boolean.parseBoolean(text);
    }
    catch (Exception e) {
      throw new IllegalArgumentException(e);
    }

    return result;
  }

  /**
   * Initializes the display of the value.
   */
  protected void initForDisplay() {
    resetChosenOption();
    if (!m_Current.equals(((BaseCheckBox) m_CustomEditor).isSelected()))
      ((BaseCheckBox) m_CustomEditor).setSelected(m_Current);
  }
}
