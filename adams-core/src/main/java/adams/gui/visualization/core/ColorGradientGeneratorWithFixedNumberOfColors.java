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
 * ColorGradientGeneratorWithFixedNumberOfColors.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;

/**
 * Interface for color gradient generators that use a user-defined, fixed number of colors.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ColorGradientGeneratorWithFixedNumberOfColors
  extends ColorGradientGenerator {

  /**
   * Sets the number of gradient colors to use.
   *
   * @param value	the number of colors
   */
  public void setNumColors(int value);

  /**
   * Returns the number of gradient colors to use.
   *
   * @return		the number of colors
   */
  public int getNumColors();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numColorsTipText();
}
