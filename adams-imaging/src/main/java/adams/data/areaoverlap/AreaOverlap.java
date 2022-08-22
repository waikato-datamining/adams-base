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
 * AreaOverlap.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.areaoverlap;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.Map;

/**
 * Interface for classes that compute the overlapping areas between annotations and predictions.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface AreaOverlap
  extends OptionHandler, QuickInfoSupporter {

  /**
   * Computes the overlapping areas between the matches.
   *
   * @param matches 	the computed matches
   * @param errors 	for collecting errors
   * @return		the overlapping areas, null in case of error
   */
  public LocatedObjects calculate(Map<LocatedObject, Map<LocatedObject,Double>> matches, MessageCollection errors);
}
