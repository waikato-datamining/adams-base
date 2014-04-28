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
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import java.util.HashMap;

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
  
  /** the meta-data. */
  protected HashMap<String,Object> m_MetaData;
  
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
      m_MetaData = (HashMap<String,Object>) point.getMetaData().clone();
    }
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
  
  /**
   * Sets the meta-data to use.
   * 
   * @param value	the meta-data
   */
  public void setMetaData(HashMap<String,Object> value) {
    m_MetaData = value;
  }
  
  /**
   * Returns the stored meta-data.
   * 
   * @return		the meta-data, null if none available
   */
  public HashMap<String,Object> getMetaData() {
    return m_MetaData;
  }
  
  /**
   * Checks if any meta-data is available.
   * 
   * @return		true if meta-data available
   */
  public boolean hasMetaData() {
    return (m_MetaData != null) && (m_MetaData.size() > 0);
  }
}
