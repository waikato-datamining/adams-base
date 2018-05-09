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
 * CCIR601.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.image.luminance;

/**
 * Parameters for BT 601: https://en.wikipedia.org/wiki/Rec._601
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BT601
  extends AbstractLuminanceParameters {

  private static final long serialVersionUID = -8226279974532958311L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Parameters for BT 601:\n"
      + "https://en.wikipedia.org/wiki/Rec._601";
  }

  /**
   * Returns the parameters for R, G and B (sum up to 1).
   *
   * @return		the parameters
   */
  @Override
  public double[] getParameters() {
    return new double[]{0.2989, 0.5870, 0.1140};
  }
}
