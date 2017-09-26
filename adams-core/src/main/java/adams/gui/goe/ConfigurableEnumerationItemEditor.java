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
 *    ConfigurableEnumerationItemEditor.java
 *    Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.ConfigurableEnumeration.AbstractItem;
import adams.core.Utils;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.gui.core.BaseList;
import adams.gui.core.BaseScrollPane;
import adams.gui.dialog.ApprovalDialog;

import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.Array;

/**
 * A PropertyEditor that displays Item objects used by
 * {@link adams.core.ConfigurableEnumeration}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConfigurableEnumerationItemEditor
  extends PropertyEditorSupport
  implements MultiSelectionEditor {

  /**
   * Returns the Item as string.
   *
   * @param option	the current option
   * @param object	the Item object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((AbstractItem) object).getLabel();
  }

  /**
   * Returns an Item object from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a Item
   * @return		the generated Item, null if failed to parse
   */
  public static Object valueOf(AbstractOption option, String str) {
    AbstractItem	defValue;
    Object		defCurrent;

    defCurrent = option.getDefaultValue();
    if (defCurrent.getClass().isArray()) {
      if (Array.getLength(defCurrent) == 0) {
        try {
	  defValue = (AbstractItem) ((AbstractArgumentOption) option).getBaseClass().newInstance();
	}
	catch (Exception e) {
          System.err.println("Failed to instantiate " + Utils.classToString(((AbstractArgumentOption) option).getBaseClass()) + "!");
          return null;
	}
      }
      else {
	defValue = (AbstractItem) Array.get(defCurrent, 0);
      }
    }
    else {
      defValue = (AbstractItem) defCurrent;
    }

    return defValue.getEnumeration().parse(str);
  }

  /**
   * Returns a description of the property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    return "null";
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
    AbstractItem	current;
    AbstractItem	value;

    current = (AbstractItem) getValue();
    value   = current.getEnumeration().parse(text);
    if (value != null)
      setValue(value);
    else
      System.err.println("Failed to parse '" + text + "' as configurable enum: " + current.getEnumeration());
  }

  /**
   * Gets the list of tags that can be selected from.
   *
   * @return 		an array of string tags.
   */
  @Override
  public String[] getTags() {
    AbstractItem	current;
    String[]		result;
    AbstractItem[]	items;
    int			i;

    current = (AbstractItem) getValue();
    items   = current.getEnumeration().values();
    result  = new String[items.length];
    for (i = 0; i < items.length; i++)
      result[i] = items[i].getDisplay();

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
    AbstractItem		current;
    ApprovalDialog		dialog;
    BaseList			list;

    current = (AbstractItem) getValue();
    list = new BaseList(current.getEnumeration().values());
    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    dialog = new ApprovalDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
    dialog.getContentPane().add(new BaseScrollPane(list), BorderLayout.CENTER);
    dialog.pack();
    dialog.setTitle(current.getEnumeration().getClass().getSimpleName());
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION)
      result = list.getSelectedValuesList().toArray();
    else
      result = (Object[]) Array.newInstance(current.getClass(), 0);

    return result;
  }
}
