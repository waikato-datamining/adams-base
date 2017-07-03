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

/**
 * InterpolationUtils.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data;

/**
 * Helper class for interpolation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InterpolationUtils {

  /**
   * Calculates the interpolation weights for the left and right X.
   *
   * @param x		the X to calculate the interpolation weights
   * @param xLeft	the X to the left
   * @param xRight	the X to the right
   * @return		the weights (left=0, right=1)
   */
  public static double[] weights(double x, double xLeft, double xRight) {
    double[]	result;
    double 	xdiff;
    double 	weightLeft;
    double 	weightRight;

    xdiff       = xRight - xLeft;
    weightLeft  = 1.0 - (x - xLeft) / xdiff;
    weightRight = 1.0 - (xRight - x) / xdiff;

    result = new double[]{weightLeft, weightRight};

    return result;
  }

  /**
   * Interpolates the Y value for a given X and the surrounding x/y pairs.
   *
   * @param x		the X to generate the interpolated Y for
   * @param xLeft	the X to the left
   * @param yLeft	the Y to the left
   * @param xRight	the X to the right
   * @param yRight	the Y to the right
   * @param weights 	array of length 2 to store the weights in (left/right)
   * @return		the interpolated Y
   */
  public static double interpolate(double x, double xLeft, double yLeft, double xRight, double yRight, double[] weights) {
    double	result;
    double[]	w;

    w          = weights(x, xLeft, xRight);
    weights[0] = w[0];
    weights[1] = w[1];
    result     = yLeft*weights[0] + yRight*weights[1];

    return result;
  }

  /**
   * Interpolates the Y value for a given X and the surrounding x/y pairs.
   *
   * @param x		the X to generate the interpolated Y for
   * @param xLeft	the X to the left
   * @param yLeft	the Y to the left
   * @param xRight	the X to the right
   * @param yRight	the Y to the right
   * @return		the interpolated Y
   */
  public static double interpolate(double x, double xLeft, double yLeft, double xRight, double yRight) {
    return interpolate(x, xLeft, yLeft, xRight, yRight, new double[2]);
  }
}
