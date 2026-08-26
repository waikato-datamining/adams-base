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
 * CenteredMovingAverage.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.utils;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.Utils;
import adams.data.statistics.StatUtils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods for Centered Moving Average.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class CenteredMovingAverage {

  /**
   * Applies Centered Moving Average to the data points.
   *
   * @param points	the data to smooth
   * @param window	the window size (uneven number)
   * @return		the smoothed points
   */
  public static double[] calculate(double[] points, int window) {
    return calculate(points, window, false);
  }

  /**
   * Applies Centered Moving Average to the data points.
   *
   * @param points	the data to smooth
   * @param window	the window size (uneven number)
   * @param debug 	whether to output debugging information
   * @return		the smoothed points
   */
  public static double[] calculate(double[] points, int window, boolean debug) {
    double[]	result;
    int		left;
    int		right;
    int		i;
    double[]	data;
    int		len;

    result = new double[points.length];
    left   = window / 2;
    right  = window - 1 - left;

    if (debug)
      System.out.println("=== window=" + window + ", #points=" + points.length);

    // left edge
    for (i = 0; i < left + 1; i++) {
      len  = i + right + 1;
      data = new double[len];
      System.arraycopy(points, 0, data, 0, len);
      result[i] = StatUtils.sum(data) / len;
      if (debug)
	System.out.println(i + "/" + len + ": " + Utils.arrayToString(data) + " = " + result[i]);
    }

    if (debug)
      System.out.println("---");

    data = new double[window];
    for (i = left + 1; i < points.length - (right + 1); i++) {
      System.arraycopy(points, i - left, data, 0, window);
      result[i] = StatUtils.sum(data) / window;
      if (debug && ((i == left+1) || (i+1 == points.length - (right + 1))))
	System.out.println(i + ": " + Utils.arrayToString(data) + " = " + result[i]);
    }

    if (debug)
      System.out.println("---");

    // right edge
    for (i = points.length - (right + 1); i < points.length; i++) {
      len  = points.length - i + left;
      data = new double[len];
      System.arraycopy(points, i - left, data, 0, len);
      result[i] = StatUtils.sum(data) / len;
      if (debug)
	System.out.println(i + "/" + len + ": " + Utils.arrayToString(data) + " = " + result[i]);
    }

    return result;
  }

  /**
   * Applies Centered Moving Average to the Y of the data points.
   *
   * @param points	the data to smooth
   * @param window	the window size (uneven number)
   * @return		the smoothed points
   */
  public static List<Point2D> calculate(List<Point2D> points, int window) {
    return calculate(points, window, false);
  }

  /**
   * Applies Centered Moving Average to the Y of the data points.
   *
   * @param points	the data to smooth
   * @param window	the window size (uneven number)
   * @param debug 	whether to output debugging information
   * @return		the smoothed points
   */
  public static List<Point2D> calculate(List<Point2D> points, int window, boolean debug) {
    List<Point2D>	result;
    double[]		dpoints;
    int			i;

    dpoints = new double[points.size()];
    for (i = 0; i < points.size(); i++)
      dpoints[i] = points.get(i).getY();
    dpoints = calculate(dpoints, window, debug);
    result  = new ArrayList<>();
    for (i = 0; i < points.size(); i++)
      result.add(new Point2D.Double(points.get(i).getX(), dpoints[i]));

    return result;
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public static TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "WikiPedia");
    result.setValue(Field.TITLE, "Moving average");
    result.setValue(Field.URL, "https://en.wikipedia.org/wiki/Moving_average");

    return result;
  }
}
