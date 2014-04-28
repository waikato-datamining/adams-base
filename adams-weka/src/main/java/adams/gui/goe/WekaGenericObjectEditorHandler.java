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
 * AdamsGenericObjectEditorHandler.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import java.beans.PropertyEditor;

import javax.swing.JPanel;

import adams.core.ClassLocator;

/**
 * Handler for the WEKA GenericObjectEditor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaGenericObjectEditorHandler
  extends AbstractGenericObjectEditorHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7963512759650622657L;

  /**
   * Sets the class type to use.
   *
   * @param editor	the editor to update
   * @param cls		the class to set
   * @return		true if successfully set
   */
  public boolean setClassType(PropertyEditor editor, Class cls) {
    ((weka.gui.GenericObjectEditor) editor).setClassType(cls);
    return true;
  }

  /**
   * Returns the class type currently in use.
   *
   * @param editor	the editor to query
   * @return		always Object.class
   */
  public Class getClassType(PropertyEditor editor) {
    return Object.class;
  }

  /**
   * Sets whether the class can be changed in the dialog.
   *
   * @param editor	the editor to update
   * @param canChange	if true the class can be changed in the dialog
   * @return		true if successfully set
   */
  public boolean setCanChangeClassInDialog(PropertyEditor editor, boolean canChange) {
    ((weka.gui.GenericObjectEditor) editor).setCanChangeClassInDialog(canChange);
    return true;
  }

  /**
   * Returns whether the class can be changed in the dialog.
   *
   * @param editor	the editor to query
   * @return		true if the class can be changed in the dialog
   */
  public boolean getCanChangeClassInDialog(PropertyEditor editor) {
    return ((weka.gui.GenericObjectEditor) editor).getCanChangeClassInDialog();
  }

  /**
   * Sets the editor value.
   *
   * @param editor	the editor to update
   * @param value	the object to set
   * @return		true if successfully set
   */
  public boolean setValue(PropertyEditor editor, Object value) {
    ((weka.gui.GenericObjectEditor) editor).setValue(value);
    return true;
  }

  /**
   * Returns the value currently being edited.
   *
   * @param editor	the editor to query
   * @return		the current editor value
   */
  public Object getValue(PropertyEditor editor) {
    return ((weka.gui.GenericObjectEditor) editor).getValue();
  }

  /**
   * Checks whether the given class can be processed.
   *
   * @param cls		the class to inspect
   * @return		always true
   */
  public boolean handles(Class cls) {
    return ClassLocator.isSubclass(weka.gui.GenericObjectEditor.class, cls);
  }

  /**
   * Checks whether the editor supplies its own panel.
   *
   * @param editor	the editor to check
   * @return		true if the editor provides a panel
   */
  public boolean hasCustomPanel(PropertyEditor editor) {
    return (editor instanceof weka.gui.CustomPanelSupplier);
  }

  /**
   * Returns the custom panel of the editor.
   *
   * @param editor	the editor to obtain the panel from
   * @return		the custom panel, null if none available
   */
  public JPanel getCustomPanel(PropertyEditor editor) {
    if (editor instanceof weka.gui.CustomPanelSupplier)
      return ((weka.gui.CustomPanelSupplier) editor).getCustomPanel();
    else
      return null;
  }
}
