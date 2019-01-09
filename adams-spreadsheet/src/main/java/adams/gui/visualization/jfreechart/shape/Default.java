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
 * Default.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.jfreechart.shape;

import java.awt.Shape;

/**
 * Generates no shape, ie leaves the default one intact.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Default
  extends AbstractShapeGenerator {

  private static final long serialVersionUID = 514268201924765348L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates no shape, ie leaves the default one intact.";
  }

  /**
   * Generates the shape.
   *
   * @return		the shape, null if none generated
   */
  @Override
  protected Shape doGenerate() {
    return null;
  }
}
