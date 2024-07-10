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
 * PassThrough.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence.pointpreprocessor;

import adams.data.sequence.XYSequencePoint;
import adams.gui.visualization.core.AxisPanel;

/**
 * Dummy, does nothing.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PassThrough
  extends AbstractPointPreprocessor {

  private static final long serialVersionUID = -659617717662676735L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, does nothing.";
  }

  /**
   * Preprocesses the point.
   *
   * @param point 	the point to process
   * @param axisX 	the X axis to use
   * @param axisY 	the Y axis to use
   * @return 		the new point
   */
  @Override
  protected XYSequencePoint doPreprocess(XYSequencePoint point, AxisPanel axisX, AxisPanel axisY) {
    return point;
  }
}
