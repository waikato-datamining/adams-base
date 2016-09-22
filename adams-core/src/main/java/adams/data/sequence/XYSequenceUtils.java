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
 * XYSequenceUtils.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.sequence;

import adams.data.container.DataContainerUtils;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class for XY sequences.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XYSequenceUtils
  extends DataContainerUtils {

  /** comparator for finding wavenumbers. */
  protected static XYSequencePointComparator m_Comparator;
  static {
    m_Comparator = new XYSequencePointComparator(Comparison.X, true);
  }

  /**
   * Returns the comparator used for finding x values.
   *
   * @return		the comparator
   */
  public XYSequencePointComparator getComparator() {
    return m_Comparator;
  }

  /**
   * Creates a header based on the current sequence points.
   *
   * @param points	the data to process
   * @return		the generated header
   */
  protected static XYSequence getHeader(List<XYSequencePoint> points) {
    XYSequence	result;

    if ((points.size() > 0) && (points.get(0).getParent() != null)) {
      result = (XYSequence) points.get(0).getParent().getHeader();
    }
    else {
      result = new XYSequence();
      result.setID("unknown");
    }

    return result;
  }

  /**
   * Returns the index in m_Points of the given sequence point.
   *
   * @param points	the points to process
   * @param p		the point to get the index for
   * @return		the index
   */
  public static int findX(List<XYSequencePoint> points, XYSequencePoint p) {
    int		result;

    result = Collections.binarySearch(points, p, m_Comparator);
    if (result < 0)
      result = -1;

    return result;
  }

  /**
   * Returns the index in m_Points of the given x value.
   *
   * @param points	the points to process
   * @param x		the x value to get the index for
   * @return		the index
   */
  public static int findX(List<XYSequencePoint> points, double x) {
    return findX(points, new XYSequencePoint(x));
  }

  /**
   * Returns the index in m_Points closest to the given x value.
   *
   * @param points	the data to process
   * @param x		the x value to get the closest index for
   * @return		the index
   */
  public static int findClosestX(List<XYSequencePoint> points, double x) {
    int			result;
    int			index;
    XYSequencePoint	currPoint;
    double		currDist;
    double		dist;
    int			i;

    result = -1;

    if (points.size() == 0)
      return result;

    index = Collections.binarySearch(points, new XYSequencePoint(x), m_Comparator);

    // no exact match -> find closest
    if (index < 0) {
      index = -index;
      if (index >= points.size())
	index = points.size() - 1;
      result = index;
      dist   = Math.abs(x - points.get(index).getX());

      for (i = index - 2; i <= index + 2; i++) {
	if ((i >= 0) && (i < points.size())) {
	  currPoint = points.get(i);
	  currDist  = Math.abs(x - currPoint.getX());

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
   * Returns the indices of m_Points closest to the given x value.
   *
   * @param points	the data to process
   * @param x		the x value to get the closest index for
   * @param wobble 	the wobble factor to allow left and right
   * @return		the indices
   */
  public static int[] findClosestXs(List<XYSequencePoint> points, double x, double wobble) {
    TIntList 		result;
    int			index;
    double		dist;
    int			i;

    if (points.size() == 0)
      return new int[0];

    index = findClosestX(points, x);
    if (index == -1)
      return new int[0];

    result = new TIntArrayList();
    result.add(index);

    // search right
    i = index;
    while (i < points.size() - 1) {
      i++;
      dist = Math.abs(x - points.get(i).getX());
      if (dist <= wobble)
	result.add(i);
      else
	break;
    }

    // search left
    i = index;
    while (i > 0) {
      i--;
      dist = Math.abs(x - points.get(i).getX());
      if (dist <= wobble)
	result.add(i);
      else
	break;
    }

    result.sort();

    return result.toArray();
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
  public static int[] findEnclosingXs(List<XYSequencePoint> points, double x) {
    int[]	result;
    int		index;

    result = new int[]{-1, -1};

    index = findClosestX(points, x);
    if (index > -1) {
      // found exact x value (or left of x value) -> store at position 0
      if (points.get(index).getX() <= x) {
	result[0] = index;
	if (index < points.size() - 1)
	  result[1] = index + 1;
      }
      // right of the x value
      else if (points.get(index).getX() > x) {
	result[1] = index;
	if (index > 0)
	  result[0] = index - 1;
      }
    }

    return result;
  }

  /**
   * Returns a region for the given range, starting just after the
   * x value of "lastEnd" and ending (including) at "end".
   *
   * @param points	the data to process
   * @param lastEnd	the last end point, if null then the first sequence point
   * 			will be the first point included.
   * @param end		the last point to include in the region, if null then
   * 			the last point in the data is used.
   * @return		the generated region
   */
  public static XYSequence getConsecutiveRegion(List<XYSequencePoint> points, XYSequencePoint lastEnd, XYSequencePoint end) {
    XYSequence	result;
    int		indexStart;
    int		indexEnd;
    int		i;

    result = getHeader(points);

    if (lastEnd == null)
      indexStart = 0;
    else
      indexStart = findX(points, lastEnd) + 1;

    if (end == null)
      indexEnd = points.size() - 1;
    else
      indexEnd = findX(points, end);

    for (i = indexStart; i <= indexEnd; i++)
      result.add((XYSequencePoint) points.get(i).getClone());

    return result;
  }

  /**
   * Returns a region for the given range, including both, start and end point.
   *
   * @param points	the data to process
   * @param start	the starting point, if null the first point in the
   * 			data is used.
   * @param end		the last point to include in the region, if null then
   * 			the last point in the data is used.
   * @return		the generated region
   */
  public static XYSequence getRegion(List<XYSequencePoint> points, XYSequencePoint start, XYSequencePoint end) {
    XYSequence	result;
    int		indexStart;
    int		indexEnd;
    int		i;

    result = getHeader(points);

    if (start == null)
      indexStart = 0;
    else
      indexStart = findX(points, start);

    if (end == null)
      indexEnd = points.size() - 1;
    else
      indexEnd = findX(points, end);

    for (i = indexStart; i <= indexEnd; i++)
      result.add((XYSequencePoint) points.get(i).getClone());

    return result;
  }

  /**
   * Counts the sign changes in the given data between the given points (incl.
   * the borders).
   *
   * @param points	the data to process
   * @param start	the x value to start with
   * @param end		the last x value
   * @return		the number of changes in sign
   */
  public static int countSignChanges(List<XYSequencePoint> points, double start, double end) {
    int				result;
    int				startIndex;
    int				endIndex;
    int				i;
    double			y;
    XYSequencePoint	point;

    result     = 0;
    startIndex = findX(points, start);
    endIndex   = findX(points, end);
    y          = points.get(startIndex).getY();

    for (i = startIndex + 1; i <= endIndex; i++) {
      point = points.get(i);
      if (Math.signum(point.getY()) != Math.signum(y)) {
	result++;
	y = point.getY();
      }
    }

    return result;
  }

  /**
   * Counts the positive or negative regions between the given points (incl.
   * the borders).
   *
   * @param points	the data to process
   * @param start	the x value to start with
   * @param end		the last x value
   * @param positive	if true then positive regions are counted otherwise
   * 			negative ones
   * @return		the number of positive/negative regions
   */
  public static int countRegions(List<XYSequencePoint> points, double start, double end, boolean positive) {
    int			result;
    int			startIndex;
    int			endIndex;
    int			i;
    double		y;
    XYSequencePoint	point;

    result     = 0;
    startIndex = findX(points, start);
    endIndex   = findX(points, end);
    y          = points.get(startIndex).getY();
    if (positive && (y >= 0))
      result++;
    else if (!positive && (y < 0))
      result++;

    for (i = startIndex + 1; i <= endIndex; i++) {
      point = points.get(i);
      if (Math.signum(point.getY()) != Math.signum(y)) {
	y = point.getY();
	if (positive && (y >= 0))
	  result++;
	else if (!positive && (y < 0))
	  result++;
      }
    }

    return result;
  }

  /**
   * Generates data for a histogram display. It counts how many sequence points have
   * the same y value in the currently stored sequence.
   *
   * @param points	the data to process
   * @param numBins	the number of bins to generate
   * @return		the histogram data
   */
  public static double[] getHistogram(List<XYSequencePoint> points, int numBins) {
    double[]	result;
    double	min;
    double	max;
    int		i;
    double	scale;

    result = new double[numBins];

    min    = points.get(0).getY();
    max    = points.get(points.size() - 1).getY();
    scale  = 1.0 / ((double) (max - min + 1) / ((double) numBins));
    for (i = 0; i < points.size(); i++)
      result[(int) ((points.get(i).getY() - min)*scale)]++;

    return result;
  }

  /**
   * Merges the given sequence with the current data pool. New sequence
   * points don't override ones already in the pool, they only get added.
   *
   * @param pool	the current data pool
   * @param s		the sequence to merge with the pool
   */
  protected static void add(Hashtable<Number,XYSequencePoint> pool, XYSequence s) {
    Iterator<XYSequencePoint>	iter;
    XYSequencePoint		pointSP;
    XYSequencePoint		poolSP;

    iter = s.toList().iterator();
    while (iter.hasNext()) {
      pointSP = iter.next();
      poolSP  = pool.get(pointSP.getX());
      if (poolSP == null)
	poolSP = pointSP;
      pool.put(poolSP.getX(), poolSP);
    }
  }

  /**
   * Merges the two sequences. The header of the first one is used for the
   * output.
   *
   * @param s1		the first sequence
   * @param s2		the second sequence
   * @return		the merged sequence
   */
  public static XYSequence merge(XYSequence s1, XYSequence s2) {
    List<XYSequence>	list;

    list = new ArrayList<XYSequence>();
    list.add(s1);
    list.add(s2);

    return merge(list);
  }

  /**
   * Merges the given sequences. THe header of the first one is used for
   * the output.
   *
   * @param list	the sequences to merge
   * @return		the merged sequence
   */
  public static XYSequence merge(List<XYSequence> list) {
    XYSequence				result;
    int					i;
    Hashtable<Number,XYSequencePoint>	pool;
    Enumeration<XYSequencePoint>	elements;

    if (list.size() == 0)
      return null;
    else if (list.size() == 1)
      return list.get(0);

    result = (XYSequence) list.get(0).getHeader();
    pool   = new Hashtable<Number,XYSequencePoint>();
    for (i = 0; i < list.size(); i++) {
      add(pool, list.get(i));
    }

    // create output data
    elements = pool.elements();
    while (elements.hasMoreElements())
      result.add(elements.nextElement());

    return result;
  }
}
