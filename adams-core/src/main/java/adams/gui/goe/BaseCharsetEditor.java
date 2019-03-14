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
 *    BaseCharsetEditor.java
 *    Copyright (C) 2013-2019 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.base.BaseCharset;
import adams.core.management.CharsetHelper;
import adams.core.option.AbstractOption;
import adams.gui.core.GUIHelper;

import java.awt.Container;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.Array;
import java.util.List;

/**
 * A PropertyEditor that displays charsets. {@link CharsetHelper#CHARSET_DEFAULT}
 * is a placeholder for the system's default charset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseCharsetEditor
  extends PropertyEditorSupport
  implements MultiSelectionEditor {
  
  /**
   * Returns the charset as string.
   *
   * @param option	the current option
   * @param object	the locale object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((BaseCharset) object).stringValue();
  }

  /**
   * Returns a charset generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a locale
   * @return		the generated locale
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new BaseCharset(str);
  }

  /**
   * Returns a description of the property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    String	result;

    result = BaseCharset.class.getName() + "(" + ((BaseCharset) getValue()).stringValue() + ")";

    return result;
  }

  /**
   * Gets the current value as text.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getAsText() {
    return ((BaseCharset) getValue()).stringValue();
  }

  /**
   * Sets the current property value as text.
   *
   * @param text 	the text of the selected tag.
   */
  @Override
  public void setAsText(String text) {
    setValue(new BaseCharset(text));
  }

  /**
   * Gets the list of tags that can be selected from.
   *
   * @return 		an array of string tags.
   */
  @Override
  public String[] getTags() {
    return CharsetHelper.getIDs();
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
    int				i;

    if (GUIHelper.getParentDialog(parent) != null)
      dialog = new MultiLineValueDialog(GUIHelper.getParentDialog(parent));
    else
      dialog = new MultiLineValueDialog(GUIHelper.getParentFrame(parent));
    dialog.setInfoText("Enter the string representations, one per line:");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    lines  = dialog.getValues();
    result = (Object[]) Array.newInstance(BaseCharset.class, lines.size());
    for (i = 0; i < lines.size(); i++)
      Array.set(result, i, new BaseCharset(lines.get(i)));

    return result;
  }
}
