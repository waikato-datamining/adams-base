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
 * ObjectOverlap.java
 * Copyright (C) 2019-2022 University of Waikato, Hamilton, NZ
 */

package adams.data.objectoverlap;

import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.Map;

/**
 * Interface for schemes that calculate image overlaps.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface ObjectOverlap
  extends OptionHandler, QuickInfoSupporter {

  /** the additional objects boolean. */
  public final static String ADDITIONAL_OBJ = "additional_object";

  /** the placeholder for unknown label. */
  public static final String UNKNOWN_LABEL = "???";

  /**
   * Computes the overlapping objects between the annotations and the predictions.
   *
   * @param annotations the annotations (ground truth)
   * @param predictions the predictions to compare with
   * @return		the overlapping objects
   */
  public LocatedObjects calculate(LocatedObjects annotations, LocatedObjects predictions);

  /**
   * Computes the overlapping objects between the annotations and the predictions
   * and returns the matches.
   *
   * @param annotations the annotations (ground truth)
   * @param predictions the predictions to compare with
   * @return		the matches
   */
  public Map<LocatedObject, Map<LocatedObject,Double>> matches(LocatedObjects annotations, LocatedObjects predictions);
}
