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
 * PrefixFieldEditor.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import java.util.Vector;

import javax.swing.JComponent;

import adams.core.option.AbstractOption;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.FieldType;
import adams.data.report.PrefixField;

/**
 * A PropertyEditor for PrefixField objects that lets the user select a field.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see PrefixField
 */
public class PrefixFieldEditor
  extends FieldEditor {

  /**
   * Returns the field as string.
   *
   * @param option	the current option
   * @param object	the Field object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((PrefixField) object).toString();
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
    return PrefixField.parseField(str);
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return valueOf(null, str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  public String getJavaInitializationString() {
    String	result;
    PrefixField	field;

    field = (PrefixField) getValue();

    if (field == null)
      result = "null";
    else
      result = "new " + PrefixField.class.getName() + "(\"" + field.toString() + "\", " + DataType.class.getName() + "." + field.getDataType() + ")";

    return result;
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  protected JComponent createCustomEditor() {
    JComponent	result;

    result = super.createCustomEditor();

    m_SelectFieldPanel.setFieldType(FieldType.PREFIX_FIELD);

    return result;
  }

  /**
   * Creates a new array of prefix field objects from the strings.
   *
   * @param fields	the field names to use
   * @param type	the type of the fields
   * @return		the prefix field array
   */
  protected AbstractField[] newArray(Vector<String> fields, DataType type) {
    PrefixField[]	result;
    int			i;

    result = new PrefixField[fields.size()];
    for (i = 0; i < fields.size(); i++)
      result[i] = new PrefixField(fields.get(i), type);

    return result;
  }
}

