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
 * WekaLabelRangeEditor.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.data.weka.WekaLabelRange;

/**
 * Editor for {@link WekaLabelRange} objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaLabelRangeEditor
  extends RangeEditor {

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  @Override
  public Object fromCustomStringRepresentation(String str) {
    return new WekaLabelRange(str);
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
  protected WekaLabelRange parse(String s) {
    WekaLabelRange	result;

    try {
      result = new WekaLabelRange(s);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }
}
