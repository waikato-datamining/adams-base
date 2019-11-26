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
 * AbstractObjectOverlap.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.objectoverlap;

import adams.core.option.AbstractOptionHandler;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Ancestor for schemes that calculate image overlaps.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractObjectOverlap
  extends AbstractOptionHandler
  implements ObjectOverlap {

  private static final long serialVersionUID = -6700493470621873334L;

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
   * Computes the overlapping objects between the annotations and the predictions.
   *
   * @param annotations the annotations (ground truth)
   * @param predictions the predictions to compare with
   * @return		the overlapping objects
   */
  protected String check(LocatedObjects annotations, LocatedObjects predictions) {
    if (annotations == null)
      return "No annotations provided!";
    if (predictions == null)
      return "No predictions provided!";
    return null;
  }

  /**
   * Computes the overlapping objects between the annotations and the predictions.
   *
   * @param annotations the annotations (ground truth)
   * @param predictions the predictions to compare with
   * @return		the overlapping objects
   */
  protected abstract LocatedObjects doCalculate(LocatedObjects annotations, LocatedObjects predictions);

  /**
   * Computes the overlapping objects between the annotations and the predictions.
   *
   * @param annotations the annotations (ground truth)
   * @param predictions the predictions to compare with
   * @return		the overlapping objects
   */
  @Override
  public LocatedObjects calculate(LocatedObjects annotations, LocatedObjects predictions) {
    String	msg;

    msg = check(annotations, predictions);
    if (msg != null)
      throw new IllegalStateException(msg);
    return doCalculate(annotations, predictions);
  }
}
