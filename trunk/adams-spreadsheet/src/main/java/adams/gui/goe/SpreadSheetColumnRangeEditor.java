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
 * SpreadSheetColumnRangeEditor.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.option.AbstractOption;
import adams.data.spreadsheet.SpreadSheetColumnRange;

/**
 * A PropertyEditor for {@link SpreadSheetColumnRange} objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetColumnRangeEditor
  extends RangeEditor {

  /**
   * Returns the Compound as string.
   *
   * @param option	the current option
   * @param object	the Compound object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((SpreadSheetColumnRange) object).getRange();
  }
  
  /**
   * Returns a Compound generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a Compound
   * @return		the generated Compound
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new SpreadSheetColumnRange(str);
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  @Override
  public String toCustomStringRepresentation(Object obj) {
    return toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  @Override
  public Object fromCustomStringRepresentation(String str) {
    return new SpreadSheetColumnRange(str);
  }

  /**
   * Parses the given string and returns the generated object. The string
   * has to be a valid one, i.e., the isValid(String) check has been
   * performed already and succeeded.
   *
   * @param s		the string to parse
   * @return		the generated object, or null in case of an error
   */
  @Override
  protected SpreadSheetColumnRange parse(String s) {
    SpreadSheetColumnRange	result;

    result = null;
    
    if (s.length() == 0) {
      result = new SpreadSheetColumnRange();
    }
    else {
      try {
	result = new SpreadSheetColumnRange(s);
      }
      catch (Exception e) {
	e.printStackTrace();
	result = null;
      }
    }

    return result;
  }
}
