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
 *    Copyright (C) 2008-2014 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.Array;

import javax.swing.ListSelectionModel;

import adams.core.EnumHelper;
import adams.gui.core.BaseList;
import adams.gui.core.BaseScrollPane;
import adams.gui.dialog.ApprovalDialog;

/**
 * A PropertyEditor that displays Enums. Based on Weka's SelectedTagEditor.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @version $Revision$
 * @see weka.gui.SelectedTagEditor
 */
public class EnumEditor
  extends PropertyEditorSupport
  implements MultiSelectionEditor {

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
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    Object[]			result;
    ApprovalDialog		dialog;
    BaseList			list;
    Class			cls;

    cls  = EnumHelper.determineClass(getValue());
    list = new BaseList(EnumHelper.getValues(cls));
    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    dialog = new ApprovalDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
    dialog.getContentPane().add(new BaseScrollPane(list), BorderLayout.CENTER);
    dialog.pack();
    dialog.setTitle(cls.getSimpleName());
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION)
      result = list.getSelectedValuesList().toArray();
    else
      result = (Object[]) Array.newInstance(cls, 0);

    return result;
  }
}
