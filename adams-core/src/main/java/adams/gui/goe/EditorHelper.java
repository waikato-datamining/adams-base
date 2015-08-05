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
 * EditorHelper.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.core.Utils;
import adams.gui.core.GUIHelper;

import javax.swing.JComponent;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

/**
 * Helper class for GOE editors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EditorHelper {

  /**
   * Tries to determine a view for the editor.
   * 
   * @param editor	the editor to get the view for
   * @return		the view, null if failed to determine one
   */
  public static JComponent findView(PropertyEditor editor) {
    JComponent	result;

    result = null;
    
    if (editor.supportsCustomEditor() && editor.isPaintable()) {
      result = new PropertyPanel(editor);
    }
    else if (editor.supportsCustomEditor() && (editor.getCustomEditor() instanceof JComponent)) {
      result = (JComponent) editor.getCustomEditor();
    }
    else if (editor.getTags() != null) {
      result = new PropertyValueSelector(editor);
    }
    else if (editor.getAsText() != null) {
      result = new PropertyText(editor);
    }
    
    return result;
  }

  /**
   * Brings up the dialog for editing the Object.
   *
   * @param parent	the parent for the dialog
   * @param obj		the object to edit
   * @param title	the title of the dialog
   * @return		the new object, null if canceled
   */
  public static Object simpleEdit(Container parent, Object obj, String title) {
    GenericObjectEditorDialog 	dialogGOE;
    GenericArrayEditorDialog	dialogArray;
    Object result;
    PropertyEditor		editor;
    boolean			primitive;

    if (obj == null)
      return null;

    primitive = Utils.isPrimitive(obj);
    if (primitive) {
      obj = Utils.wrapPrimitive(obj);
      if (obj == null)
	return null;
    }
    editor = PropertyEditorManager.findEditor(obj.getClass());
    if (editor instanceof GenericObjectEditor) {
      if (GUIHelper.getParentDialog(parent) != null)
	dialogGOE = new GenericObjectEditorDialog(GUIHelper.getParentDialog(parent), ModalityType.DOCUMENT_MODAL);
      else
	dialogGOE = new GenericObjectEditorDialog(GUIHelper.getParentFrame(parent), true);
      dialogGOE.setTitle(title);
      dialogGOE.getGOEEditor().setClassType(obj.getClass());
      dialogGOE.getGOEEditor().setCanChangeClassInDialog(false);
      dialogGOE.setCurrent(obj);
      dialogGOE.pack();
      dialogGOE.setLocationRelativeTo(dialogGOE.getParent());
      dialogGOE.setVisible(true);
      if (dialogGOE.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
	return null;
      result = dialogGOE.getCurrent();
    }
    else if ((editor instanceof GenericArrayEditor) || obj.getClass().isArray()) {
      if (GUIHelper.getParentDialog(parent) != null)
	dialogArray = new GenericArrayEditorDialog(GUIHelper.getParentDialog(parent), ModalityType.DOCUMENT_MODAL);
      else
	dialogArray = new GenericArrayEditorDialog(GUIHelper.getParentFrame(parent), true);
      dialogArray.setTitle(title);
      dialogArray.setCurrent(obj);
      dialogArray.pack();
      dialogArray.setLocationRelativeTo(dialogArray.getParent());
      dialogArray.setVisible(true);
      if (dialogArray.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
	return null;
      result = dialogArray.getCurrent();
    }
    else {
      if (GUIHelper.getParentDialog(parent) != null)
	dialogGOE = new GenericObjectEditorDialog(GUIHelper.getParentDialog(parent), ModalityType.DOCUMENT_MODAL);
      else
	dialogGOE = new GenericObjectEditorDialog(GUIHelper.getParentFrame(parent), true);
      dialogGOE.setTitle(title);
      editor.setValue(obj);
      dialogGOE.setEditor(editor);
      dialogGOE.pack();
      dialogGOE.setLocationRelativeTo(dialogGOE.getParent());
      dialogGOE.setVisible(true);
      if (dialogGOE.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
	return null;
      result = editor.getValue();
    }

    if (primitive)
      result = Utils.unwrapPrimitive(result);

    return result;
  }
}
