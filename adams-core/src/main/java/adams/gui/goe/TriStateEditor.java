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
 * TriStateEditor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import adams.core.TriState;
import adams.core.option.AbstractOption;

import com.jidesoft.swing.TristateCheckBox;

/**
 * A custom editor for TriState enums.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4943 $
 */
public class TriStateEditor
  extends AbstractBasicTypePropertyEditor {

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
    TristateCheckBox	result;

    result = new TristateCheckBox();
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	TriState state = checkBoxToTriState((TristateCheckBox) e.getSource());
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
    state = checkBoxToTriState((TristateCheckBox) m_CustomEditor);
    if (!m_Current.equals(state))
      setCheckBoxState((TristateCheckBox) m_CustomEditor, m_Current);
  }
  
  /**
   * Turns the state of a {@link TristateCheckBox} into {@link TriState} enum.
   * 
   * @param checkbox	the checkbox to convert
   * @return		the state
   */
  public static TriState checkBoxToTriState(TristateCheckBox checkbox) {
    TriState 	result;
    
    switch (checkbox.getState()) {
      case TristateCheckBox.STATE_MIXED:
	result = TriState.NOT_SET;
	break;
      case TristateCheckBox.STATE_SELECTED:
	result = TriState.TRUE;
	break;
      default:
	result = TriState.FALSE;
    }
    
    return result;
  }
  
  /**
   * Sets the state of a {@link TristateCheckBox} based on the {@link TriState} enum.
   * 
   * @param checkbox	the checkbox to update
   * @param state	the state to set
   */
  public static void setCheckBoxState(TristateCheckBox checkbox, TriState state) {
    switch (state) {
      case NOT_SET:
	checkbox.setState(TristateCheckBox.STATE_MIXED);
	break;
      case TRUE:
	checkbox.setState(TristateCheckBox.STATE_SELECTED);
	break;
      default:
	checkbox.setState(TristateCheckBox.STATE_UNSELECTED);
    }
  }

  /**
   * Returns the field as string.
   *
   * @param option	the current option
   * @param object	the Field object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((TriState) object).toRaw();
  }

  /**
   * Returns a Field generated from the string. All "\t" strings are turned
   * automatically into tab characters.
   *
   * @param option	the current option
   * @param str		the string to convert to a field
   * @return		the generated Field object
   */
  public static Object valueOf(AbstractOption option, String str) {
    return TriState.valueOf(option, str);
  }
}
