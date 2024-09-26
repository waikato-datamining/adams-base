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
 * LicenseParsing.java
 * Copyright (C) 2012-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option.parsing;

import adams.core.License;
import adams.core.option.AbstractOption;

/**
 * For parsing license options.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class LicenseParsing
  extends AbstractParsing {

  /**
   * Returns the enum as string.
   *
   * @param option	the current option
   * @param object	the enum object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((License) object).toRaw();
  }

  /**
   * Returns an enum generated from the string.
   *
   * @param option	the current option
   * @param str	the string to convert to an enum
   * @return		the generated enum or null in case of error
   */
  public static License valueOf(AbstractOption option, String str) {
    License result;

    result = null;

    // default parsing
    try {
      result = License.valueOf(str);
    }
    catch (Exception e) {
      // ignored
    }

    // try display
    if (result == null) {
      for (License l : License.values()) {
	if (l.toDisplay().equals(str)) {
	  result = l;
	  break;
	}
      }
    }

    return result;
  }
}
