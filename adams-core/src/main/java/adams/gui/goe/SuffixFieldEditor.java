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
 * SuffixFieldEditor.java
 * Copyright (C) 2009-2019 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.option.parsing.SuffixFieldParsing;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.FieldType;
import adams.data.report.SuffixField;

import javax.swing.JComponent;
import java.util.Vector;

/**
 * A PropertyEditor for SuffixField objects that lets the user select a field.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see SuffixField
 */
public class SuffixFieldEditor
  extends FieldEditor {

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return SuffixFieldParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return SuffixFieldParsing.valueOf(null, str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  public String getJavaInitializationString() {
    String	result;
    SuffixField	field;

    field = (SuffixField) getValue();

    if (field == null)
      result = "null";
    else
      result = "new " + SuffixField.class.getName() + "(\"" + field.toString() + "\", " + DataType.class.getName() + "." + field.getDataType() + ")";

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

    m_SelectFieldPanel.setFieldType(FieldType.SUFFIX_FIELD);

    return result;
  }

  /**
   * Creates a new array of suffix field objects from the strings.
   *
   * @param fields	the field names to use
   * @param type	the type of the fields
   * @return		the suffix field array
   */
  protected AbstractField[] newArray(Vector<String> fields, DataType type) {
    SuffixField[]	result;
    int			i;

    result = new SuffixField[fields.size()];
    for (i = 0; i < fields.size(); i++)
      result[i] = new SuffixField(fields.get(i), type);

    return result;
  }
}

