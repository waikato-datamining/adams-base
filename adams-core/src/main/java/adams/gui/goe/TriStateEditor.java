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
 * TriStateEditor.java
 * Copyright (C) 2014-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.core.TriState;
import adams.core.option.AbstractOption;
import adams.gui.core.BaseComboBox;

import javax.swing.JComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A custom editor for TriState enums.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class TriStateEditor
  extends AbstractBasicTypePropertyEditor {

  /** the unset state. */
  public final static String STATE_UNSET = "---";

  /** the true state. */
  public final static String STATE_TRUE = "true";

  /** the false state. */
  public final static String STATE_FALSE = "false";

  /** the current value. */
  protected TriState m_Current;

  /**
   * Initializes the editor.
   */
  public TriStateEditor() {
    super();

    m_Current = TriState.FALSE;
  }

  /**
   * Creates the custom editor to use.
   *
   * @return		the custom editor
   */
  @Override
  protected JComponent createCustomEditor() {
    BaseComboBox<String> result;

    result = new BaseComboBox<>();
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	TriState state = comboBoxToTriState((BaseComboBox<String>) e.getSource());
	if (!getValue().equals(state))
	  setValue(state);
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
  @Override
  public void setValue(Object value) {
    m_Current = (TriState) value;
    firePropertyChange();
  }

  /**
   * Gets the property value.
   *
   * @return The value of the property.  Primitive types such as "int" will
   * be wrapped as the corresponding object type such as "java.lang.Boolean".
   */

  @Override
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
  @Override
  protected Object parse(String text) throws IllegalArgumentException {
    Object	result;

    try {
      if (text.length() == 0)
	text = "" + TriState.FALSE;
      result = TriState.valueOf((AbstractOption) null, text);
    }
    catch (Exception e) {
      throw new IllegalArgumentException(e);
    }

    return result;
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    TriState 	state;
    
    resetChosenOption();
    state = comboBoxToTriState((BaseComboBox<String>) m_CustomEditor);
    if (!m_Current.equals(state))
      selectComboBoxIndex((BaseComboBox<String>) m_CustomEditor, m_Current);
  }
  
  /**
   * Turns the state of the combobox into {@link TriState} enum.
   * 
   * @param comboBox	the checkbox to convert
   * @return		the state
   */
  public static TriState comboBoxToTriState(BaseComboBox<String> comboBox) {
    TriState 	result;

    if (comboBox.getSelectedItem() == null)
      return TriState.NOT_SET;

    switch (comboBox.getSelectedItem()) {
      case STATE_TRUE:
	result = TriState.TRUE;
	break;
      case STATE_FALSE:
        result = TriState.FALSE;
        break;
      default:
	result = TriState.NOT_SET;
    }
    
    return result;
  }
  
  /**
   * Sets the state of a combobox based on the {@link TriState} enum.
   * 
   * @param comboBox	the combobox to update
   * @param state	the state to set
   */
  public static void selectComboBoxIndex(BaseComboBox<String> comboBox, TriState state) {
    switch (state) {
      case TRUE:
        comboBox.setSelectedItem(STATE_TRUE);
	break;
      case FALSE:
        comboBox.setSelectedItem(STATE_UNSET);
        break;
      default:
        comboBox.setSelectedItem(STATE_FALSE);
    }
  }

}
