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

import adams.core.base.BaseBoolean;
import adams.core.base.BaseByte;
import adams.core.base.BaseCharacter;
import adams.core.base.BaseDouble;
import adams.core.base.BaseFloat;
import adams.core.base.BaseInteger;
import adams.core.base.BaseLong;
import adams.core.base.BaseObject;
import adams.core.base.BaseShort;
import adams.core.base.BaseString;
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
   * Checks whether the class is a wrapper for a primitive.
   *
   * @param cls		the class to test
   * @return		true if primitve
   */
  public static boolean isPrimitive(Class cls) {
    if (cls == Boolean.class)
      return true;
    else if (cls == Character.class)
      return true;
    else if (cls == String.class)
      return true;
    else if (cls == Byte.class)
      return true;
    else if (cls == Short.class)
      return true;
    else if (cls == Integer.class)
      return true;
    else if (cls == Long.class)
      return true;
    else if (cls == Float.class)
      return true;
    else if (cls == Double.class)
      return true;
    return false;
  }

  /**
   * Returns the corresponding BaseObject-derived wrapper class.
   *
   * @param cls		the primitive class to wrap
   * @return		the wrapper class, null if not available
   */
  public static Class getWrapperClass(Class cls) {
    if (cls == Boolean.class)
      return BaseBoolean.class;
    else if (cls == Character.class)
      return BaseCharacter.class;
    else if (cls == String.class)
      return BaseString.class;
    else if (cls == Byte.class)
      return BaseByte.class;
    else if (cls == Short.class)
      return BaseShort.class;
    else if (cls == Integer.class)
      return BaseInteger.class;
    else if (cls == Long.class)
      return BaseLong.class;
    else if (cls == Float.class)
      return BaseFloat.class;
    else if (cls == Double.class)
      return BaseDouble.class;
    return null;
  }

  /**
   * Returns the corresponding BaseObject-derived wrapper class.
   *
   * @param cls		the primitive class to wrap
   * @return		the wrapper class, null if not available
   */
  public static Class getPrimitiveClass(Class cls) {
    if (cls == BaseBoolean.class)
      return Boolean.class;
    else if (cls == BaseCharacter.class)
      return Character.class;
    else if (cls == BaseString.class)
      return String.class;
    else if (cls == BaseByte.class)
      return Byte.class;
    else if (cls == BaseShort.class)
      return Short.class;
    else if (cls == BaseInteger.class)
      return Integer.class;
    else if (cls == BaseLong.class)
      return Long.class;
    else if (cls == BaseFloat.class)
      return Float.class;
    else if (cls == BaseDouble.class)
      return Double.class;
    return null;
  }

  /**
   * Checks whether the object is a wrapper for a primitive.
   *
   * @param obj		the object to test
   * @return		true if primitve
   */
  public static boolean isPrimitive(Object obj) {
    if (obj instanceof Boolean)
      return true;
    else if (obj instanceof Character)
      return true;
    else if (obj instanceof String)
      return true;
    else if (obj instanceof Byte)
      return true;
    else if (obj instanceof Short)
      return true;
    else if (obj instanceof Integer)
      return true;
    else if (obj instanceof Long)
      return true;
    else if (obj instanceof Float)
      return true;
    else if (obj instanceof Double)
      return true;
    return false;
  }

  /**
   * Wraps the primitive in a BaseObject-derived object.
   *
   * @param obj		the primitive to wrap
   * @return		the wrapped object, null if failed to wrap
   */
  public static BaseObject wrapPrimitive(Object obj) {
    if (obj instanceof Boolean)
      return new BaseBoolean((Boolean) obj);
    else if (obj instanceof Character)
      return new BaseCharacter((Character) obj);
    else if (obj instanceof String)
      return new BaseString((String) obj);
    else if (obj instanceof Byte)
      return new BaseByte((Byte) obj);
    else if (obj instanceof Short)
      return new BaseShort((Short) obj);
    else if (obj instanceof Integer)
      return new BaseInteger((Integer) obj);
    else if (obj instanceof Long)
      return new BaseLong((Long) obj);
    else if (obj instanceof Float)
      return new BaseFloat((Float) obj);
    else if (obj instanceof Double)
      return new BaseDouble((Double) obj);
    return null;
  }

  /**
   * Unwraps the primitve from the BaseObject-derived object.
   *
   * @param obj		the BaseObject to unwrap
   * @return		the primitve, null if failed to unwrap
   */
  public static Object unwrapPrimitive(Object obj) {
    if (obj instanceof BaseBoolean)
      return ((BaseBoolean) obj).booleanValue();
    else if (obj instanceof BaseCharacter)
      return ((BaseCharacter) obj).charValue();
    else if (obj instanceof BaseString)
      return ((BaseString) obj).stringValue();
    else if (obj instanceof BaseByte)
      return ((BaseByte) obj).byteValue();
    else if (obj instanceof BaseShort)
      return ((BaseShort) obj).shortValue();
    else if (obj instanceof BaseInteger)
      return ((BaseInteger) obj).intValue();
    else if (obj instanceof BaseLong)
      return ((BaseLong) obj).longValue();
    else if (obj instanceof BaseFloat)
      return ((BaseFloat) obj).floatValue();
    else if (obj instanceof BaseDouble)
      return ((BaseDouble) obj).doubleValue();
    return null;
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

    primitive = EditorHelper.isPrimitive(obj);
    if (primitive) {
      obj = EditorHelper.wrapPrimitive(obj);
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
      result = EditorHelper.unwrapPrimitive(result);

    return result;
  }
}
