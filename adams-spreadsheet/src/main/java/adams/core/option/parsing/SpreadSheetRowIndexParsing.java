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
 * SpreadSheetRowIndexParsing.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.core.option.parsing;

import adams.core.option.AbstractOption;
import adams.data.spreadsheet.SpreadSheetRowIndex;

/**
 * For parsing SpreadSheetRowIndex options.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetRowIndexParsing {

  /**
   * Returns the Compound as string.
   *
   * @param option	the current option
   * @param object	the Compound object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((SpreadSheetRowIndex) object).getIndex();
  }

  /**
   * Returns a Compound generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a Compound
   * @return		the generated Compound
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new SpreadSheetRowIndex(str);
  }
}
