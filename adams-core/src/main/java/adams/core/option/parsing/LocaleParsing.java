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
 * LocaleParsing.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.option.parsing;

import adams.core.management.LocaleHelper;
import adams.core.option.AbstractOption;

import java.util.Locale;

/**
 * For parsing Locale objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LocaleParsing
    extends AbstractParsing {

  /**
   * Returns the locale as string.
   *
   * @param option	the current option
   * @param object	the locale object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return LocaleHelper.toString((Locale) object);
  }

  /**
   * Returns a locale generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a locale
   * @return		the generated locale
   */
  public static Object valueOf(AbstractOption option, String str) {
    return LocaleHelper.valueOf(str);
  }
}
