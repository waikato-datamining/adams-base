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
 * AbstractLuminanceParameters.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.image.luminance;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for classes that return luminance parameters for RGB into grayscale
 * conversions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLuminanceParameters
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 8927292193236439325L;

  /**
   * Returns the parameters for R, G and B (sum up to 1).
   *
   * @return		the parameters
   */
  public abstract double[] getParameters();
}
