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
 * FieldParsing.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.option.parsing;

import adams.core.option.AbstractOption;
import adams.data.report.Field;

/**
 * For parsing Field options.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FieldParsing {

  /**
   * Returns the field as string.
   *
   * @param option	the current option
   * @param object	the Field object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((Field) object).toParseableString();
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
    return Field.parseField(str);
  }
}
