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
 * AbstractAreaOverlap.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.areaoverlap;

import adams.core.MessageCollection;
import adams.core.option.AbstractOptionHandler;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.Map;

/**
 * Ancestor for area overlap computation schemes.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAreaOverlap
  extends AbstractOptionHandler
  implements AreaOverlap {

  private static final long serialVersionUID = 808278359478666680L;

  /**
   * Hook method for checks before calculating the areas.
   *
   * @param matches	the matches to check
   * @return		null if checks passed, otherwise error message
   */
  protected String check(Map<LocatedObject, Map<LocatedObject,Double>> matches) {
    if (matches == null)
      return "No matches provided!";
    return null;
  }

  /**
   * Computes the overlapping areas between the matches.
   *
   * @param matches 	the computed matches
   * @param errors 	for collecting errors
   * @return		the overlapping areas, null in case of error
   */
  protected abstract LocatedObjects doCalculate(Map<LocatedObject, Map<LocatedObject,Double>> matches, MessageCollection errors);

  /**
   * Computes the overlapping areas between the matches.
   *
   * @param matches 	the computed matches
   * @param errors 	for collecting errors
   * @return		the overlapping areas, null in case of error
   */
  public LocatedObjects calculate(Map<LocatedObject, Map<LocatedObject,Double>> matches, MessageCollection errors) {
    String	msg;

    msg = check(matches);
    if (msg != null) {
      errors.add(msg);
      return null;
    }

    return doCalculate(matches, errors);
  }
}
