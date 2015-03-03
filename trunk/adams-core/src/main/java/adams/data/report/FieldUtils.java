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
 * FieldUtils.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.report;

/**
 * A helper class for field objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FieldUtils {

  /**
   * Creates a field object with the correct class.
   *
   * @param type	the type of field to create
   * @param field	the field to fix
   * @return		the fixed field
   */
  public static Field fixClass(FieldType type, AbstractField field) {
    return fixClass(type, new AbstractField[]{field})[0];
  }

  /**
   * Creates an array with the correct class.
   *
   * @param type	the type of field to create
   * @param fields	the array to fix
   * @return		the fixed array
   * @see		#m_FieldType
   */
  public static Field[] fixClass(FieldType type, AbstractField[] fields) {
    Field[]	result;
    int		i;

    result = new Field[fields.length];
    for (i = 0; i < fields.length; i++) {
      if (type == FieldType.FIELD)
        result[i] = new Field(fields[i]);
      else if (type == FieldType.PREFIX_FIELD)
        result[i] = new PrefixField(fields[i]);
      else if (type == FieldType.SUFFIX_FIELD)
        result[i] = new SuffixField(fields[i]);
      else
        throw new IllegalStateException("Unhandled field type '" + type + "'!");
    }

    return result;
  }

}
