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
 * DefaultGenericObjectEditorHandler.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import java.beans.PropertyEditor;
import java.lang.reflect.Method;

import javax.swing.JPanel;

/**
 * Dummy handler.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultGenericObjectEditorHandler
  extends AbstractGenericObjectEditorHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8738786085338970854L;

  /** the method name for custom panel suppliers. */
  public final static String METHOD_CUSTOMPANEL = "getCustomPanel";

  /**
   * Sets the class type to use.
   *
   * @param editor	the editor to update
   * @param cls		the class to set -- ignored
   * @return		always true
   */
  public boolean setClassType(PropertyEditor editor, Class cls) {
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
   * @param canChange	if true the class can be changed in the dialog -- ignored
   * @return		always true
   */
  public boolean setCanChangeClassInDialog(PropertyEditor editor, boolean canChange) {
    return true;
  }

  /**
   * Returns whether the class can be changed in the dialog.
   *
   * @param editor	the editor to query
   * @return		always false
   */
  public boolean getCanChangeClassInDialog(PropertyEditor editor) {
    return false;
  }

  /**
   * Sets the editor value.
   *
   * @param editor	the editor to update
   * @param value	the object to set
   * @return		always true
   */
  public boolean setValue(PropertyEditor editor, Object value) {
    editor.setValue(value);
    return true;
  }

  /**
   * Returns the value currently being edited.
   *
   * @param editor	the editor to query
   * @return		the object
   */
  public Object getValue(PropertyEditor editor) {
    return editor.getValue();
  }

  /**
   * Checks whether the given class can be processed.
   *
   * @param cls		the class to inspect
   * @return		always true
   */
  public boolean handles(Class cls) {
    return true;
  }

  /**
   * Checks whether the editor supplies its own panel.
   *
   * @param editor	the editor to check
   * @return		true if the editor provides a panel
   */
  public boolean hasCustomPanel(PropertyEditor editor) {
    boolean	result;

    try {
      editor.getClass().getMethod(METHOD_CUSTOMPANEL, new Class[0]);
      result = true;
    }
    catch (Exception e) {
      result = false;
      // ignored
    }

    return result;
  }

  /**
   * Returns the custom panel of the editor.
   *
   * @param editor	the editor to obtain the panel from
   * @return		the custom panel, null if none available
   */
  public JPanel getCustomPanel(PropertyEditor editor) {
    JPanel	result;
    Method	method;

    try {
      method = editor.getClass().getMethod(METHOD_CUSTOMPANEL, new Class[0]);
      result = (JPanel) method.invoke(editor, new Object[0]);
    }
    catch (Exception e) {
      result = null;
      System.err.println("Failed to obtain/invoke method '" + METHOD_CUSTOMPANEL + "':");
      e.printStackTrace();
    }

    return result;
  }
}
