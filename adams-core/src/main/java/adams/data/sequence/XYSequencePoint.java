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
 * XYSequencePoint.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.sequence;

import adams.core.Utils;
import adams.data.container.AbstractDataPoint;
import adams.data.container.DataPoint;

/**
 * A 2-dimensional point. With an optional ID string attached to it.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <X> the type of X
 * @param <Y> the type of Y
 */
public class XYSequencePoint
  extends AbstractDataPoint {

  /** for serialization. */
  private static final long serialVersionUID = 2354312871454097142L;

  /** an optional ID. */
  protected String m_ID;

  /** the X value. */
  protected double m_X;

  /** the Y value. */
  protected double m_Y;

  /**
   * Initializes the point with no points and no ID.
   */
  public XYSequencePoint() {
    this(Double.NaN);
  }

  /**
   * Initializes the point with no ID and no y.
   *
   * @param x		the X value
   */
  public XYSequencePoint(double x) {
    this(null, x, Double.NaN);
  }

  /**
   * Initializes the point with no ID.
   *
   * @param x		the X value
   * @param y		the Y value
   */
  public XYSequencePoint(double x, double y) {
    this(null, x, y);
  }

  /**
   * Initializes the point.
   *
   * @param id		the ID, use null to ignore
   * @param x		the X value
   */
  public XYSequencePoint(String id, double x) {
    this(id, x, Double.NaN);
  }

  /**
   * Initializes the point.
   *
   * @param id		the ID, use null to ignore
   * @param x		the X value
   * @param y		the Y value
   */
  public XYSequencePoint(String id, double x, double y) {
    super();

    setID(id);
    setX(x);
    setY(y);
  }

  /**
   * Sets the X value.
   *
   * @param value	the new X value
   */
  public void setX(double value) {
    m_X = value;
  }

  /**
   * Returns the X value.
   *
   * @return		the X value
   */
  public double getX() {
    return m_X;
  }

  /**
   * Sets the Y value.
   *
   * @param value	the new Y value
   */
  public void setY(double value) {
    m_Y = value;
  }

  /**
   * Returns the Y value.
   *
   * @return		the Y value
   */
  public double getY() {
    return m_Y;
  }

  /**
   * Sets the ID.
   *
   * @param value	the new ID
   */
  public void setID(String value) {
    if (value == null)
      m_ID = "";
    else
      m_ID = value;
  }

  /**
   * Returns the ID.
   *
   * @return		the ID
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Checks whether an ID is set.
   *
   * @return		true if an ID is available
   */
  public boolean hasID() {
    return (m_ID.length() > 0);
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  @Override
  public int compareTo(Object o) {
    int			result;
    XYSequencePoint	other;

    if (o == null)
      return 1;
    else
      result = 0;

    if (!(o instanceof XYSequencePoint))
      return -1;

    other = (XYSequencePoint) o;

    result = getID().compareTo(other.getID());

    if (result == 0)
      result = new Double(getX()).compareTo(other.getX());

    if (result == 0)
      result = new Double(getY()).compareTo(other.getY());

    return result;
  }

  /**
   * Obtains the stored variables from the other data point.
   *
   * @param other	the data point to get the values from
   */
  @Override
  public void assign(DataPoint other) {
    XYSequencePoint	point;

    super.assign(other);

    point = (XYSequencePoint) other;

    setX(point.getX());
    setY(point.getY());
  }

  /**
   * Returns a string representation of the point.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return Utils.quote(getID()) + "," + Utils.doubleToString(getX(), 12) + "," + Utils.doubleToString(getY(), 12);
  }
}
