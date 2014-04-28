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
 * DataPoint.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.data.container;

/**
 * Superclass for data points.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataPoint
  implements DataPoint {

  /** for serialization. */
  private static final long serialVersionUID = -7649691865561959944L;

  /** the container this data point belongs to. */
  protected DataContainer m_Parent;

  /**
   * Sets the spectrum this point belongs to.
   *
   * @param value	the spectrum
   */
  public void setParent(DataContainer value) {
    m_Parent = value;
  }

  /**
   * Returns the spectrum this point belongs to.
   *
   * @return		the spectrum, can be null
   */
  public DataContainer getParent() {
    return m_Parent;
  }

  /**
   * Returns whether the point belongs to a spectrum.
   *
   * @return		true if the point belongs to a spectrum
   */
  public boolean hasParent() {
    return (m_Parent != null);
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
  public abstract int compareTo(Object o);

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  public boolean equals(Object obj) {
    if (obj instanceof DataPoint)
      return (compareTo(obj) == 0);
    else
      return false;
  }

  /**
   * Returns a clone of itself. Parent gets set to null!
   *
   * @return		the clone
   */
  public Object getClone() {
    DataPoint	result;

    if (m_Parent == null)
      throw new IllegalStateException("Need parent for getClone() - use manual duplication of data point!");

    result = m_Parent.newPoint();
    result.assign(this);
    result.setParent(null);

    return result;
  }

  /**
   * Obtains the stored variables from the other data point.
   *
   * @param other	the data point to get the values from
   */
  public void assign(DataPoint other) {
    setParent(other.getParent());
  }

  /**
   * Returns a string representation of the point.
   *
   * @return		the string representation
   */
  public abstract String toString();

  /**
   * Turns the Short into a double, if possible.
   *
   * @param value	the value to convert
   * @return		the double or null if not a number
   */
  public static Double toDouble(Object value) {
    if (value instanceof Number)
      return new Double(((Number) value).doubleValue());
    else
      return null;
  }

  /**
   * Turns the Number object into a double.
   *
   * @param value	the value to convert
   * @return		the double
   */
  public static Double toDouble(Number value) {
    return new Double(value.doubleValue());
  }

  /**
   * Turns the Byte object into a double.
   *
   * @param value	the value to convert
   * @return		the double
   */
  public static Double toDouble(Byte value) {
    return new Double(value.doubleValue());
  }

  /**
   * Turns the Short object into a double.
   *
   * @param value	the value to convert
   * @return		the double
   */
  public static Double toDouble(Short value) {
    return new Double(value.doubleValue());
  }

  /**
   * Turns the Integer object into a double.
   *
   * @param value	the value to convert
   * @return		the double
   */
  public static Double toDouble(Integer value) {
    return new Double(value.doubleValue());
  }

  /**
   * Turns the Long object into a double.
   *
   * @param value	the value to convert
   * @return		the double
   */
  public static Double toDouble(Long value) {
    return new Double(value.doubleValue());
  }

  /**
   * Turns the Float object into a double.
   *
   * @param value	the value to convert
   * @return		the double
   */
  public static Double toDouble(Float value) {
    return new Double(value.doubleValue());
  }

  /**
   * Turns the Double object into a double.
   *
   * @param value	the value to convert
   * @return		the double
   */
  public static Double toDouble(Double value) {
    return new Double(value.doubleValue());
  }
}
