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
 * TimeseriesUtils.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.timeseries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import adams.data.container.DataContainerUtils;

/**
 * Utility class for timeseries.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesUtils
  extends DataContainerUtils {

  /** comparator for finding wavenumbers. */
  protected static TimeseriesPointComparator m_Comparator;
  static {
    m_Comparator = new TimeseriesPointComparator(true);
  }

  /**
   * Returns the comparator used for finding wave numbers.
   *
   * @return		the comparator
   */
  public static TimeseriesPointComparator getComparator() {
    return m_Comparator;
  }

  /**
   * Creates a header based on the given timeseries points.
   *
   * @param points	the timeseries points to create the header for
   * @return		the generated header
   */
  protected static Timeseries getHeader(List<TimeseriesPoint> points) {
    Timeseries	result;

    if ((points.size() > 0) && (points.get(0).getParent() != null)) {
      result = ((Timeseries) points.get(0).getParent()).getHeader();
    }
    else {
      result = new Timeseries();
      result.setID("unknown");
    }

    return result;
  }

  /**
   * Returns the index in points of the given timeseries point, -1 if not found.
   *
   * @param points	the list of timeseries points to search in
   * @param p		the point to get the index for
   * @return		the index or -1 if not found
   */
  public static int findTimestamp(List<TimeseriesPoint> points, TimeseriesPoint p) {
    int		result;

    result = Collections.binarySearch(points, p, m_Comparator);
    if (result < 0)
      result = -1;

    return result;
  }

  /**
   * Returns the index in points of the given timestamp.
   *
   * @param points	the vector of profile points to search in
   * @param timestamp	the timestamp to get the index for
   * @return		the index
   */
  public static int findTimestamp(List<TimeseriesPoint> points, Date timestamp) {
    return findTimestamp(points, new TimeseriesPoint(timestamp, 0));
  }

  /**
   * Returns the index in points closest to the given timestamp.
   *
   * @param points	the list of timeseries points to search in
   * @param timestamp	the timestamp to get the closest index for
   * @return		the index
   */
  public static int findClosestTimestamp(List<TimeseriesPoint> points, Date timestamp) {
    int			result;
    int			index;
    TimeseriesPoint	currPoint;
    double		currDist;
    double		dist;
    int			i;

    result = -1;

    if (points.size() == 0)
      return result;

    index = Collections.binarySearch(points, new TimeseriesPoint(timestamp, 0), m_Comparator);

    // no exact match -> find closest
    if (index < 0) {
      index = -index;
      if (index >= points.size())
	index = points.size() - 1;
      result = index;
      dist   = Math.abs(timestamp.getTime() - points.get(index).getTimestamp().getTime());

      for (i = index - 2; i <= index + 2; i++) {
	if ((i >= 0) && (i < points.size())) {
	  currPoint = points.get(i);
	  currDist  = Math.abs(timestamp.getTime() - currPoint.getTimestamp().getTime());

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
   * Returns the indices of points in m_Points that enclose the given timestamp.
   * If the given timestamp happens to be an exact point, then this points will
   * be stored at index 0. If no index could be determined, then -1 will be
   * stored.
   *
   * @param points	the list of timeseries points to search in
   * @param timestamp	the timestamp to get the enclosing indices for
   * @return		the indices
   */
  public static int[] findEnclosingTimestamps(List<TimeseriesPoint> points, Date timestamp) {
    int[]	result;
    int		index;

    result = new int[]{-1, -1};

    index = findClosestTimestamp(points, timestamp);
    if (index > -1) {
      // found exact timestamp (or left of timestamp) -> store at position 0
      if (points.get(index).getTimestamp().getTime() <= timestamp.getTime()) {
	result[0] = index;
	if (index < points.size() - 1)
	  result[1] = index + 1;
      }
      // right of the timestamp
      else if (points.get(index).getTimestamp().getTime() > timestamp.getTime()) {
	result[1] = index;
	if (index > 0)
	  result[0] = index - 1;
      }
    }

    return result;
  }

  /**
   * Returns the union of the two timeseries "a" and "b". All timeseries points
   * from "a" are used, plus the ones from "b" that are not in "a".
   *
   * @param a		the first timeseries
   * @param b		the second timeseries
   * @return		the union
   */
  public static Timeseries union(Timeseries a, Timeseries b) {
    Timeseries		result;
    List<TimeseriesPoint>	points;
    int			i;

    result = (Timeseries) a.getClone();

    points = b.toList();
    for (i = 0; i < points.size(); i++) {
      if (a.find(points.get(i).getTimestamp()) == null)
	result.add((TimeseriesPoint) points.get(i).getClone());
    }

    return result;
  }

  /**
   * Returns a timeseries that contains all the points that are in "a"
   * but not in "b". It is assumed that "b" is a subset of "a"
   * and does not contain other wave numbers.
   *
   * @param a		the "full" timeseries
   * @param b		the "subset" timeseries
   * @return		the points missing in "b"
   */
  public static Timeseries minus(Timeseries a, Timeseries b) {
    Timeseries		result;
    List<TimeseriesPoint>	points;
    int			i;

    result = a.getHeader();

    points = a.toList();
    for (i = 0; i < points.size(); i++) {
      if (b.find(points.get(i).getTimestamp()) == null)
	result.add((TimeseriesPoint) points.get(i).getClone());
    }

    return result;
  }

  /**
   * Returns a timeseries that contains all the points that are in "a"
   * and in "b". The timeseries points in the result are taken from "a".
   *
   * @param a		the first timeseries
   * @param b		the second timeseries
   * @return		the points in "a" and "b"
   */
  public static Timeseries intersect(Timeseries a, Timeseries b) {
    Timeseries		result;
    List<TimeseriesPoint>	points;
    int			i;

    result = a.getHeader();

    points = a.toList();
    for (i = 0; i < points.size(); i++) {
      if (b.find(points.get(i).getTimestamp()) != null)
	result.add((TimeseriesPoint) points.get(i).getClone());
    }

    return result;
  }

  /**
   * Merges the given  with the current data pool. New timeseries
   * points don't override ones already in the pool, they only get added.
   *
   * @param pool	the current data pool
   * @param s		the timeseries to merge with the pool
   */
  protected static void add(Hashtable<Date,TimeseriesPoint> pool, Timeseries s) {
    Iterator<TimeseriesPoint>	iter;
    TimeseriesPoint		pointSP;
    TimeseriesPoint		poolSP;

    iter = s.toList().iterator();
    while (iter.hasNext()) {
      pointSP = iter.next();
      poolSP  = pool.get(pointSP.getTimestamp());
      if (poolSP == null)
	poolSP = pointSP;
      pool.put(poolSP.getTimestamp(), poolSP);
    }
  }

  /**
   * Merges the two timeseries ranges. The header of the first one is used for the
   * output.
   *
   * @param s1		the first timeseries
   * @param s2		the second timeseries
   * @return		the merged timeseries
   */
  public static Timeseries merge(Timeseries s1, Timeseries s2) {
    List<Timeseries>	list;

    list = new ArrayList<Timeseries>();
    list.add(s1);
    list.add(s2);

    return merge(list);
  }

  /**
   * Merges the given timeseries. The header of the first one is used for
   * the output.
   *
   * @param list	the timeseries to merge
   * @return		the merged timeseries
   */
  public static Timeseries merge(List<Timeseries> list) {
    Timeseries				result;
    int						i;
    Hashtable<Date,TimeseriesPoint>	pool;
    Enumeration<TimeseriesPoint>	elements;

    if (list.size() == 0)
      return null;
    else if (list.size() == 1)
      return list.get(0);

    result = list.get(0).getHeader();
    pool   = new Hashtable<Date,TimeseriesPoint>();
    for (i = 0; i < list.size(); i++) {
      add(pool, list.get(i));
    }

    // create output data
    elements = pool.elements();
    while (elements.hasMoreElements())
      result.add(elements.nextElement());

    return result;
  }

  /**
   * Returns the amplitudes as double array.
   *
   * @param c		the timeseries to turn into a double array
   * @return		the amplitudes as double array
   */
  public static double[] toDoubleArray(Timeseries c) {
    return toDoubleArray(c.toList());
  }

  /**
   * Returns the amplitudes as double array.
   *
   * @param data	the timeseries points to turn into a double array
   * @return		the amplitudes as double array
   */
  public static double[] toDoubleArray(List<TimeseriesPoint> data) {
    double[] 	result;
    int 	i;

    result = new double[data.size()];
    i      = 0;
    for (TimeseriesPoint gcp:data)
      result[i++] = new Double(gcp.getValue());

    return result;
  }
}
