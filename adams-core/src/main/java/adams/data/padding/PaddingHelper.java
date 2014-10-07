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
 * PaddingHelper.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.padding;

/**
 * Helper for padding operations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PaddingHelper {

  /**
   * Returns the next bigger number that's a power of 2. If the number is
   * already a power of 2 then this will be returned. The number will be at
   * least 2^2.
   *
   * @param n		the number to start from
   * @return		the next bigger number
   */
  public static int nextPowerOf2(int n) {
    int		exp;

    exp = (int) StrictMath.ceil(StrictMath.log(n) / StrictMath.log(2.0));
    exp = StrictMath.max(2, exp);

    return (int) StrictMath.pow(2, exp);
  }

  /**
   * Pads the data to the next power of 2.
   *
   * @param data	the data to pad
   * @return		the padded data
   */
  public static float[] padPow2(float[] data, PaddingType type) {
    return pad(data, nextPowerOf2(data.length), type);
  }

  /**
   * Pads the data to the specified number of data points.
   *
   * @param data	the data to pad
   * @param numPoints	the number of data points for the result
   * @return		the padded data
   */
  public static float[] pad(float[] data, int numPoints, PaddingType type) {
    float[] 	result;
    int 	i;
    
    if (numPoints < data.length)
      throw new IllegalArgumentException(
	  "Number of output data points is smaller than input data points: " + numPoints + " < " + data.length);
    
    result = new float[numPoints];
    System.arraycopy(data, 0, result, 0, data.length);

    switch (type) {
      case ZERO:
	for (i = data.length; i < numPoints; i++)
	  result[i] = 0;
	break;
      case LAST:
	for (i = data.length; i < numPoints; i++)
	  result[i] = result[data.length - 1];
	break;
      default:
	throw new IllegalStateException(
	    "Padding " + type  + " not implemented!");
    }

    return result;
  }

  /**
   * Pads the data to the next power of 2.
   *
   * @param data	the data to pad
   * @return		the padded data
   */
  public static double[] padPow2(double[] data, PaddingType type) {
    return pad(data, nextPowerOf2(data.length), type);
  }

  /**
   * Pads the data to the specified number of data points.
   *
   * @param data	the data to pad
   * @param numPoints	the number of data points for the result
   * @return		the padded data
   */
  public static double[] pad(double[] data, int numPoints, PaddingType type) {
    double[] 	result;
    int 	i;
    
    if (numPoints < data.length)
      throw new IllegalArgumentException(
	  "Number of output data points is smaller than input data points: " + numPoints + " < " + data.length);

    result = new double[numPoints];
    System.arraycopy(data, 0, result, 0, data.length);

    switch (type) {
      case ZERO:
	for (i = data.length; i < numPoints; i++)
	  result[i] = 0;
	break;
      case LAST:
	for (i = data.length; i < numPoints; i++)
	  result[i] = result[data.length - 1];
	break;
      default:
	throw new IllegalStateException(
	    "Padding " + type  + " not implemented!");
    }

    return result;
  }
}
