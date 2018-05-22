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
 * ColorSelectionShapePainter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.selectionshape;

import java.awt.Color;

/**
 * Interface for selection shape painters that use a single color.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface ColorSelectionShapePainter
  extends SelectionShapePainter {

  /**
   * Sets the color to use.
   *
   * @param value 	the color
   */
  public void setColor(Color value);

  /**
   * Returns the color to use.
   *
   * @return 		the color
   */
  public Color getColor();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText();
}
