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
 * AbstractArraySubsetGenerator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.arraysubsetgeneration;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;

import java.lang.reflect.Array;

/**
 * Ancestor for schemes that generate array subsets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractArraySubsetGenerator
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = -9189810905729858606L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * For checking the input data.
   *
   * @param array	the array to check
   * @return		null if checks passed, otherwise error message
   */
  protected String check(Object array) {
    if (array == null)
      return "No data provided!";
    if (!array.getClass().isArray())
      return "No array object provided: " + Utils.classToString(array);
    return null;
  }

  /**
   * Returns a new array instance of the specified length.
   *
   * @param array	the old array
   * @param length	the length of the new array
   * @return		the new array
   */
  protected Object newArray(Object array, int length) {
    return Array.newInstance(array.getClass().getComponentType(), length);
  }

  /**
   * Generates the subset.
   *
   * @param array	the array to generate the subset from
   * @param errors	for collecting errors
   * @return		null in case of an error
   */
  protected abstract Object doGenerateSubset(Object array, MessageCollection errors);

  /**
   * Generates the subset.
   *
   * @param array	the array to generate the subset from
   * @param errors	for collecting errors
   * @return		null in case of an error
   */
  public Object generateSubset(Object array, MessageCollection errors) {
    Object	result;
    String	msg;

    result = null;
    msg    = check(array);
    if (msg != null)
      errors.add(msg);
    else
      result = doGenerateSubset(array, errors);

    if (!errors.isEmpty())
      result = null;

    return result;
  }
}
