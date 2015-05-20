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
 * TimeseriesPoint.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.timeseries;

import java.util.Date;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.data.container.AbstractDataPoint;
import adams.data.container.DataPoint;

/**
 * Encapsulates a single data point of a timeseries.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesPoint
  extends AbstractDataPoint {

  /** for serialization. */
  private static final long serialVersionUID = 2381133774648848800L;

  /** for formatting dates. */
  protected static DateFormat m_Formatter = new DateFormat(Constants.TIMESTAMP_FORMAT);

  /** the teimstamp of the data point. */
  protected Date m_Timestamp;

  /** the timeseries value. */
  protected double m_Value;

  /**
   * Initializes the point with the current date and 0.0 value.
   */
  public TimeseriesPoint() {
    this(new Date(), 0.0);
  }

  /**
   * Initializes the point with the specified timestamp and value.
   *
   * @param timestamp	the date of the data point
   * @param value	the value of the data point
   */
  public TimeseriesPoint(Date timestamp, double value) {
    super();

    setTimestamp(timestamp);
    setValue(value);
  }

  /**
   * Sets the timestamp.
   *
   * @param value	the timestamp
   */
  public void setTimestamp(Date value) {
    if (value == null)
      throw new IllegalArgumentException("Timestamp cannot be null!");

    m_Timestamp = value;
  }

  /**
   * Returns the timestamp.
   *
   * @return		the timestamp
   */
  public Date getTimestamp() {
    return m_Timestamp;
  }

  /**
   * Sets the value.
   *
   * @param value	the value
   */
  public void setValue(double value) {
    m_Value = value;
  }

  /**
   * Returns the value.
   *
   * @return		the value
   */
  public double getValue() {
    return m_Value;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Only uses the timestamp for comparison.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  @Override
  public int compareTo(Object o) {
    TimeseriesPoint	t;

    t = (TimeseriesPoint) o;

    return getTimestamp().compareTo(t.getTimestamp());
  }

  /**
   * Obtains the stored variables from the other data point.
   *
   * @param other	the data point to get the values from
   */
  @Override
  public void assign(DataPoint other) {
    TimeseriesPoint	point;

    super.assign(other);

    point = (TimeseriesPoint) other;

    setTimestamp((Date) point.getTimestamp().clone());
    setValue(point.getValue());
  }

  /**
   * Returns a string representation of the point.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return m_Formatter.format(m_Timestamp) + ": " + m_Value;
  }
}
