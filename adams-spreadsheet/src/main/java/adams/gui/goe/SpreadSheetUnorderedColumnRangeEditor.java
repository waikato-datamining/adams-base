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
 * SpreadSheetUnorderedColumnRangeEditor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.option.parsing.SpreadSheetUnorderedColumnRangeParsing;
import adams.data.spreadsheet.SpreadSheetUnorderedColumnRange;

/**
 * A PropertyEditor for {@link SpreadSheetUnorderedColumnRange} objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetUnorderedColumnRangeEditor
  extends UnorderedRangeEditor {

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  @Override
  public String toCustomStringRepresentation(Object obj) {
    return SpreadSheetUnorderedColumnRangeParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  @Override
  public Object fromCustomStringRepresentation(String str) {
    return new SpreadSheetUnorderedColumnRange(str);
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
  protected SpreadSheetUnorderedColumnRange parse(String s) {
    SpreadSheetUnorderedColumnRange	result;

    result = null;
    
    if (s.length() == 0) {
      result = new SpreadSheetUnorderedColumnRange();
    }
    else {
      try {
	result = new SpreadSheetUnorderedColumnRange(s);
      }
      catch (Exception e) {
	e.printStackTrace();
	result = null;
      }
    }

    return result;
  }
}
