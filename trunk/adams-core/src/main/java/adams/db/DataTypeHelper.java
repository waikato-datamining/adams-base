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
 * DataTypeHelper.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import adams.data.report.Field;

/**
 * Helper for the DataType class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataTypeHelper {

  /**
   * Determines the type identifier for an object (S=String, B=Boolean,
   * N=Double; default is 'S').
   *
   * @param o		the object to get the type for
   * @return		the type
   */
  public static String typeFor(Object o) {
    String	result;

    result = "S";

    if (o instanceof Boolean)
      result = "B";
    else if (o instanceof Double)
      result = "N";

    return result;
  }

  /**
   * Converts the object into the appropriate string.
   *
   * @param o		the object to convert
   * @return		the generated string
   */
  public static String convert(Object o) {
    String	result;
    String	type;

    type = typeFor(o);

    if (type.equals("S"))
      result = Field.fixString(((String) o).replace("\\", "/"));
    else if (type.equals("B"))
      result = o.toString();
    else if (type.equals("N"))
      result = o.toString();
    else
      throw new IllegalArgumentException("Unhandled type '" + type + "'!");

    return result;
  }
}
