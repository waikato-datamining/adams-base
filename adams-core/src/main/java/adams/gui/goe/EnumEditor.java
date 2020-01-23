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
 *    EnumEditor.java
 *    Copyright (C) 2008-2020 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.EnumHelper;
import adams.core.Utils;
import adams.core.option.EnumOption;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;

import java.awt.Container;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A PropertyEditor that displays Enums. Based on Weka's SelectedTagEditor.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @see weka.gui.SelectedTagEditor
 */
public class EnumEditor
  extends PropertyEditorSupport
  implements MultiSelectionEditor {

  /** whether the editor has been registered for the enum class. */
  protected static HashSet<Class> m_Registered;
  static {
    m_Registered = new HashSet<>();
  }

  /**
   * Returns a description of the property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    String	result;

    result = "Enum.valueOf("
      		+ getValue().getClass().getSimpleName() + ", "
      		+ getValue().toString() + ")";

    return result;
  }

  /**
   * Gets the current value as text.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getAsText() {
    return getValue().toString();
  }

  /**
   * Sets the current property value as text.
   *
   * @param text 	the text of the selected tag.
   */
  @Override
  public void setAsText(String text) {
    setValue(EnumHelper.parse(getValue().getClass(), text));
  }

  /**
   * Gets the list of tags that can be selected from.
   *
   * @return 		an array of string tags.
   */
  @Override
  public String[] getTags() {
    String[]	result;
    Object[]	values;
    int		i;

    values = EnumHelper.getValues(getValue());
    result = new String[values.length];
    for (i = 0; i < values.length; i++)
      result[i] = values[i].toString();

    return result;
  }

  /**
   * Creates a new array of field objects from the strings.
   *
   * @param values	the field names to use
   * @param type	the type of the fields
   * @return		the field array
   */
  protected Object[] newArray(List<String> values, Class type) {
    Object	result;
    int		i;

    result = Array.newInstance(type, 0);
    for (i = 0; i < values.size(); i++)
      Array.set(result, i, EnumHelper.parse(type, values.get(i)));

    return (Object[]) result;
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    Object[]			result;
    MultiLineValueDialog	dialog;
    List<String> 		lines;
    Class			cls;

    cls  = EnumHelper.determineClass(getValue());

    if (GUIHelper.getParentDialog(parent) != null)
      dialog = new MultiLineValueDialog(GUIHelper.getParentDialog(parent));
    else
      dialog = new MultiLineValueDialog(GUIHelper.getParentFrame(parent));
    dialog.setPrefixCount("Count: ");
    dialog.setInfoText("<html>Enter the enum values, one per line. Available options:<br>" + Utils.flatten(EnumHelper.getValues(cls), ", ") + "</html>");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      lines = dialog.getValues();
      result = newArray(lines, cls);
    }
    else {
      result = newArray(new ArrayList<>(), cls);
    }

    return result;

  }

  /**
   * Registers the EnumEditor for the enum option if necessary.
   *
   * @param option	the option to check
   */
  public static void registerEditor(EnumOption option) {
    Class	cls;

    cls = option.getBaseClass();
    if (!m_Registered.contains(cls)) {
      Editors.registerCustomEditor(cls, EnumEditor.class);
      m_Registered.add(cls);
    }
  }
}
