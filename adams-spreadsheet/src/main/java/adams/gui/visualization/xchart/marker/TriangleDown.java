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
 * TriangleDown.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.xchart.marker;

import org.knowm.xchart.style.markers.Marker;

/**
 * Paints a triangle down.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class TriangleDown
  extends AbstractMarkerGenerator {

  private static final long serialVersionUID = -8578456904248576926L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paints a triangle down.";
  }

  /**
   * Generates the marker.
   *
   * @return the shape, null if none generated
   */
  @Override
  protected Marker doGenerate() {
    return new org.knowm.xchart.style.markers.TriangleDown();
  }
}
