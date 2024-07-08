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
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.data.sequence;

import adams.core.Utils;
import adams.data.container.AbstractDataPoint;
import adams.data.container.DataPoint;
import adams.data.container.DataPointWithMetaData;

import java.util.HashMap;

/**
 * A 2-dimensional point. With an optional ID string and meta-data attached to it.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class XYSequencePoint
  extends AbstractDataPoint
  implements DataPointWithMetaData {

  /** for serialization. */
  private static final long serialVersionUID = 2354312871454097142L;

  /** an optional ID. */
  protected String m_ID;

  /** the X value. */
  protected double m_X;

  /** the Y value. */
  protected double m_Y;

  /** the meta-data. */
  protected HashMap<String,Object> m_MetaData;

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
   * Returns the minimum for X.
   *
   * @return		the minimum
   */
  public double getMinX() {
    return m_X;
  }

  /**
   * Returns the maximum for X.
   *
   * @return		the maximum
   */
  public double getMaxX() {
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
   * Returns the minimum for Y.
   *
   * @return		the minimum
   */
  public double getMinY() {
    return m_Y;
  }

  /**
   * Returns the maximum for Y.
   *
   * @return		the maximum
   */
  public double getMaxY() {
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
    return !m_ID.isEmpty();
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

    if (!(o instanceof XYSequencePoint))
      return -1;

    other = (XYSequencePoint) o;

    result = getID().compareTo(other.getID());

    if (result == 0)
      result = Double.compare(getX(), other.getX());

    if (result == 0)
      result = Double.compare(getY(), other.getY());

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

    m_MetaData = (HashMap<String, Object>) point.getMetaData().clone();
  }

  /**
   * Sets the meta-data to use.
   *
   * @param value	the meta-data
   */
  @Override
  public void setMetaData(HashMap<String,Object> value) {
    m_MetaData = value;
  }

  /**
   * Returns the stored meta-data.
   *
   * @return		the meta-data, null if none available
   */
  @Override
  public HashMap<String,Object> getMetaData() {
    return m_MetaData;
  }

  /**
   * Checks if any meta-data is available.
   *
   * @return		true if meta-data available
   */
  @Override
  public boolean hasMetaData() {
    return (m_MetaData != null) && !m_MetaData.isEmpty();
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
