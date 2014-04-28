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
 * Space.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package weka.core.setupgenerator;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.meta.MultiSearch;

/**
 * Represents a multidimensional value space.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Space
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -481830810204401148L;

  /** the dimensions. */
  protected SpaceDimension[] m_Dimensions;

  /**
   * Initializes the space.
   *
   * @param dimensions	the dimensions of the space
   */
  public Space(SpaceDimension[] dimensions) {
    super();

    m_Dimensions = dimensions.clone();
  }

  /**
   * Returns the number of dimensions of this space.
   *
   * @return			the number of dimensions
   */
  public int dimensions() {
    return m_Dimensions.length;
  }

  /**
   * Returns the specified dimension.
   *
   * @param dimension		the dimension index
   * @return			the dimension
   */
  public SpaceDimension getDimension(int dimension) {
    return m_Dimensions[dimension];
  }

  /**
   * Returns the type of the dimension.
   *
   * @param dimension		the dimension index
   * @return			the type of the dimension
   * @see			MultiSearch#TYPE_FUNCTION
   * @see			MultiSearch#TYPE_LIST
   */
  public int getType(int dimension) {
    return getDimension(dimension).getType();
  }

  /**
   * Returns the double values (or list values) for the given position.
   *
   * @param locations		the location to get the double values for
   * @return			the double values (or list values) at the
   * 				specified position
   */
  public Point<Object> getValues(Point<Integer> locations) {
    Point<Object>	result;
    Object[]		values;
    int		i;

    if (locations.dimensions() != dimensions())
      throw new IllegalArgumentException(
          "Dimension mismatch: space=" + dimensions()
          + ", locations=" + locations.dimensions());

    values = new Object[locations.dimensions()];
    for (i = 0; i < values.length; i++)
      values[i] = getDimension(i).getValue(locations.getValue(i));

    result = new Point<Object>(values);

    return result;
  }

  /**
   * Returns the locations for the given values in the various dimensions.
   *
   * @param values		the double values to get the locations for
   * @return			the
   */
  public Point<Integer> getLocations(Point<Object> values) {
    Point<Integer>	result;
    Integer[]		locations;
    int		i;

    if (values.dimensions() != dimensions())
      throw new IllegalArgumentException(
          "Dimension mismatch: space=" + dimensions()
          + ", values=" + values.dimensions());

    locations = new Integer[values.dimensions()];
    for (i = 0; i < locations.length; i++)
      locations[i] = getDimension(i).getLocation(values.getValue(i));

    result = new Point<Integer>(locations);

    return result;
  }

  /**
   * checks whether the given locations/values are on the border of the space.
   *
   * @param points		the locations/values to check
   * @return			true if the the locations/values are on the border
   */
  public boolean isOnBorder(Point<?> points) {
    boolean		result;
    Point<Integer>	locations;
    int		i;

    if (points.dimensions() != dimensions())
      throw new IllegalArgumentException(
          "Dimension mismatch: space=" + dimensions()
          + ", points=" + points.dimensions());

    if (!(points.getValue(0) instanceof Integer))
      locations = getLocations((Point<Object>) points);
    else
      locations = (Point<Integer>) points;

    result = false;
    for (i = 0; i < dimensions(); i++) {
      if (getDimension(i).isOnBorder(locations.getValue(i))) {
        result = true;
        break;
      }
    }

    return result;
  }

  /**
   * Returns a subspace around the given point, with just one more
   * neighbor left and right on each dimension.
   *
   * @param center	the center of the new "universe" ;-)
   * @return		the new space
   */
  public Space subspace(Point<Integer> center) {
    Space		result;
    SpaceDimension[]	dimensions;
    int		i;

    dimensions = new SpaceDimension[dimensions()];
    for (i = 0; i < dimensions.length; i++)
      dimensions[i] = getDimension(i).subdimension(
          			center.getValue(i) - 1, center.getValue(i) + 1);

    result = new Space(dimensions);

    return result;
  }

  /**
   * Increments the location array by 1.
   *
   * @param locations		the position in the space
   * @param max		the maxima
   * @return			true if locations could be incremented
   */
  protected boolean inc(Integer[] locations, int[] max) {
    boolean	result;
    int	i;

    result = true;

    i = 0;
    while (i < locations.length) {
      if (locations[i] < max[i] - 1) {
        locations[i]++;
        break;
      }
      else {
        locations[i] = 0;
        i++;
        // adding was not possible!
        if (i == locations.length)
          result = false;
      }
    }

    return result;
  }

  /**
   * returns a Vector with all points in the space.
   *
   * @return			a Vector with all points
   */
  protected Vector<Point<Integer>> listPoints() {
    Vector<Point<Integer>>	result;
    int			i;
    int[]			max;
    Integer[]			locations;
    boolean			ok;

    result = new Vector<Point<Integer>>();

    // determine maximum locations per dimension
    max = new int[dimensions()];
    for (i = 0; i < max.length; i++)
      max[i] = getDimension(i).width();

    // create first point
    locations = new Integer[dimensions()];
    for (i = 0; i < locations.length; i++)
      locations[i] = 0;
    result.add(new Point<Integer>(locations));

    ok = true;
    while (ok) {
      ok = inc(locations, max);
      if (ok)
        result.add(new Point<Integer>(locations));
    }

    return result;
  }

  /**
   * Returns the size of the space.
   *
   * @return		the number of points in the space
   */
  public int size() {
    return listPoints().size();
  }

  /**
   * returns an Enumeration over all points.
   *
   * @return			an Enumeration over all points
   */
  public Enumeration<Point<Integer>> points() {
    return listPoints().elements();
  }

  /**
   * returns an Enumeration over all values.
   *
   * @return			an Enumeration over all values
   */
  public Enumeration<Point<Object>> values() {
    Vector<Point<Object>>	result;
    Vector<Point<Integer>>	points;
    int			i;

    result = new Vector<Point<Object>>();

    points = listPoints();
    for (i = 0; i < points.size(); i++)
      result.add(getValues(points.get(i)));

    return result.elements();
  }

  /**
   * Returns a string representation of the space.
   *
   * @return		 a string representation
   */
  public String toString() {
    String	result;
    int	i;

    result = dimensions() + "-dimensional space:";
    for (i = 0; i < dimensions(); i++)
      result += "\n - " + (i+1) + ". " + getDimension(i);

    return result;
  }
}