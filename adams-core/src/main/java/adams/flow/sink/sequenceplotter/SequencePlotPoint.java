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
 * SequencePlotPoint.java
 * Copyright (C) 2013-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.data.container.DataPoint;
import adams.data.sequence.XYSequencePoint;

/**
 * Extended {@link XYSequencePoint} which can store X/Y error information
 * as well.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SequencePlotPoint
  extends XYSequencePoint {

  /** for serialization. */
  private static final long serialVersionUID = -6220735479438426204L;

  /** the X errors. */
  protected Double[] m_ErrorX;

  /** the Y errors. */
  protected Double[] m_ErrorY;

  /**
   * Initializes the point.
   */
  public SequencePlotPoint() {
    this(Double.NaN, Double.NaN);
  }

  /**
   * Initializes the point.
   *
   * @param x		the X value
   * @param y		the Y value
   */
  public SequencePlotPoint(double x, double y) {
    this(null, x, y);
  }

  /**
   * Initializes the point.
   *
   * @param id		the ID, use null to ignore
   * @param x		the X value
   * @param y		the Y value
   */
  public SequencePlotPoint(String id, double x, double y) {
    this(id, x, y, null, null);
  }

  /**
   * Initializes the point.
   *
   * @param id		the ID, use null to ignore
   * @param x		the X value
   * @param y		the Y value
   * @param errorX	the error for X (either delta or min/max)
   * @param errorY	the error for Y (either delta or min/max)
   */
  public SequencePlotPoint(String id, double x, double y, Double[] errorX, Double[] errorY) {
    super(id, x, y);
    
    if (errorX != null) {
      if ((errorX.length < 1) || (errorX.length > 2))
	errorX = null;
    }
    if (errorY != null) {
      if ((errorY.length < 1) || (errorY.length > 2))
	errorY = null;
    }
    
    m_ErrorX   = errorX;
    m_ErrorY   = errorY;
    m_MetaData = null;
  }

  /**
   * Obtains the stored variables from the other data point.
   *
   * @param other	the data point to get the values from
   */
  @Override
  public void assign(DataPoint other) {
    SequencePlotPoint	point;

    super.assign(other);

    if (other instanceof SequencePlotPoint) {
      point = (SequencePlotPoint) other;

      m_ErrorX   = point.getErrorX().clone();
      m_ErrorY   = point.getErrorY().clone();
    }
  }

  /**
   * Returns the minimum for X.
   *
   * @return		the minimum
   */
  public double getMinX() {
    if (!hasErrorX())
      return m_X;
    if (m_ErrorX.length == 1)
      return Math.min(m_X, m_X - m_ErrorX[0]);  // delta
    else
      return Math.min(m_X, m_ErrorX[0]);
  }

  /**
   * Returns the maximum for X.
   *
   * @return		the maximum
   */
  public double getMaxX() {
    if (!hasErrorX())
      return m_X;
    if (m_ErrorX.length == 1)
      return Math.max(m_X, m_X + m_ErrorX[0]);  // delta
    else
      return Math.max(m_X, m_ErrorX[1]);
  }

  /**
   * Checks whether error information for X is available.
   * 
   * @return		true if available
   */
  public boolean hasErrorX() {
    return (m_ErrorX != null);
  }
  
  /**
   * Returns the error information for X.
   * 
   * @return		the error information, null if not available
   */
  public Double[] getErrorX() {
    return m_ErrorX;
  }
  
  /**
   * Returns the minimum for Y.
   *
   * @return		the minimum
   */
  public double getMinY() {
    if (!hasErrorY())
      return m_Y;
    if (m_ErrorY.length == 1)
      return Math.min(m_Y, m_Y - m_ErrorY[0]);  // delta
    else
      return Math.min(m_Y, m_ErrorY[0]);
  }

  /**
   * Returns the maximum for Y.
   *
   * @return		the maximum
   */
  public double getMaxY() {
    if (!hasErrorY())
      return m_Y;
    if (m_ErrorY.length == 1)
      return Math.max(m_Y, m_Y + m_ErrorY[0]);  // delta
    else
      return Math.max(m_Y, m_ErrorY[1]);
  }

  /**
   * Checks whether error information for Y is available.
   * 
   * @return		true if available
   */
  public boolean hasErrorY() {
    return (m_ErrorY != null);
  }
  
  /**
   * Returns the error information for Y.
   * 
   * @return		the error information, null if not available
   */
  public Double[] getErrorY() {
    return m_ErrorY;
  }
}
