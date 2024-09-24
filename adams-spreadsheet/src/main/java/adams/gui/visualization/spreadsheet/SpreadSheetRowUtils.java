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
 * SpreadSheetRowUtils.java
 * Copyright (C) 2016-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.spreadsheet;

import adams.data.container.DataContainerUtils;

import java.util.Collections;
import java.util.List;

/**
 * Utility class for rows.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4605 $
 */
public class SpreadSheetRowUtils
  extends DataContainerUtils {

  /** comparator for finding X values. */
  protected static SpreadSheetRowPointComparator m_Comparator;
  static {
    m_Comparator = new SpreadSheetRowPointComparator(false, true);
  }

  /**
   * Returns the comparator used for finding X values.
   *
   * @return		the comparator
   */
  public static SpreadSheetRowPointComparator getComparator() {
    return m_Comparator;
  }

  /**
   * Returns the index in m_Points of the given sequence point.
   *
   * @param points	the data to process
   * @param p		the point to get the index for
   * @return		the index
   */
  public static int findX(List<SpreadSheetRowPoint> points, SpreadSheetRowPoint p) {
    int		result;

    result = Collections.binarySearch(points, p, m_Comparator);
    if (result < 0)
      result = -1;

    return result;
  }

  /**
   * Returns the index in m_Points of the given x value.
   *
   * @param points	the data to process
   * @param x		the x value to get the index for
   * @return		the index
   */
  public static int findX(List<SpreadSheetRowPoint> points, int x) {
    return findX(points, new SpreadSheetRowPoint(x, 0.0));
  }

  /**
   * Returns the index in m_Points closest to the given x value.
   *
   * @param points	the data to process
   * @param x		the x value to get the closest index for
   * @return		the index
   */
  public static int findClosestX(List<SpreadSheetRowPoint> points, int x) {
    int			result;
    int			index;
    SpreadSheetRowPoint currPoint;
    double		currDist;
    double		dist;
    int			i;

    result = -1;

    if (points.isEmpty())
      return result;

    index = Collections.binarySearch(points, new SpreadSheetRowPoint(x, 0.0), m_Comparator);

    // no exact match -> find closest
    if (index < 0) {
      index = -index;
      if (index >= points.size())
	index = points.size() - 1;
      result = index;
      dist   = Math.abs(x - points.get(index).getX().doubleValue());

      for (i = index - 2; i <= index + 2; i++) {
	if ((i >= 0) && (i < points.size())) {
	  currPoint = points.get(i);
	  currDist  = Math.abs(x - currPoint.getX().doubleValue());

	  if (currDist < dist) {
	    dist   = currDist;
	    result = i;
	  }
	}
      }
    }
    else {
      result = index;
    }

    return result;
  }

  /**
   * Returns the indices of points in m_Points that enclose the given x value.
   * If the given x value happens to be an exact point, then this points will
   * be stored at index 0. If no index could be determined, then -1 will be
   * stored.
   *
   * @param points	the data to process
   * @param x		the x value to get the enclosing indices for
   * @return		the indices
   */
  public static int[] findEnclosingXs(List<SpreadSheetRowPoint> points, int x) {
    int[]	result;
    int		index;

    result = new int[]{-1, -1};

    index = findClosestX(points, x);
    if (index > -1) {
      // found exact x value (or left of x value) -> store at position 0
      if (points.get(index).getX().doubleValue() <= x) {
	result[0] = index;
	if (index < points.size() - 1)
	  result[1] = index + 1;
      }
      // right of the x value
      else if (points.get(index).getX().doubleValue() > x) {
	result[1] = index;
	if (index > 0)
	  result[0] = index - 1;
      }
    }

    return result;
  }

  /**
   * Returns the points as double array.
   *
   * @param r		the row to turn into a double array
   * @return		the points as double array
   */
  public static double[] toDoubleArray(SpreadSheetRow r) {
    return toDoubleArray(r.toList());
  }

  /**
   * Returns the points as double array.
   *
   * @param data	the row points to turn into a double array
   * @return		the points as double array
   */
  public static double[] toDoubleArray(List<SpreadSheetRowPoint> data) {
    double[] 	result;
    int 	i;

    result = new double[data.size()];
    i      = 0;
    for (SpreadSheetRowPoint p : data)
      result[i++] = p.getY();

    return result;
  }
}
