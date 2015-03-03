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
 * AbstractSavitzkyGolayBased.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.baseline;

import adams.core.TechnicalInformation;
import adams.data.container.DataContainer;
import adams.data.container.DataPoint;
import adams.data.filter.AbstractSavitzkyGolay;

/**
 * Ancestor for SavitzkyGolay-based baseline correction schemes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSavitzkyGolayBased<T extends DataContainer>
  extends AbstractBaselineCorrection<T> {
  
  /** for serialization. */
  private static final long serialVersionUID = 1662223546956780389L;
  
  /** the filter to use. */
  protected AbstractSavitzkyGolay m_Filter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A baseline correction scheme that uses SavitzkyGolay smoothing to determine the baseline.\n\n"
      + "For more information see:\n\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "polynomial", "polynomialOrder",
	    2, 2, null);

    m_OptionManager.add(
	    "left", "numPointsLeft",
	    3, 0, null);

    m_OptionManager.add(
	    "right", "numPointsRight",
	    3, 0, null);
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    return getFilter().getTechnicalInformation();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Filter = getFilter();
  }

  /**
   * Returns a new instance of a SavitzkyGolay filter.
   * 
   * @return		the filter
   */
  protected abstract AbstractSavitzkyGolay getFilter();

  /**
   * Sets the polynomial order.
   *
   * @param value 	the order
   */
  public void setPolynomialOrder(int value) {
    m_Filter.setPolynomialOrder(value);
  }

  /**
   * Returns the polynominal order.
   *
   * @return 		the order
   */
  public int getPolynomialOrder() {
    return m_Filter.getPolynomialOrder();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String polynomialOrderTipText() {
    return m_Filter.polynomialOrderTipText();
  }

  /**
   * Sets the number of points to the left of a data point.
   *
   * @param value 	the number of points
   */
  public void setNumPointsLeft(int value) {
    m_Filter.setNumPointsLeft(value);
  }

  /**
   * Returns the number of points to the left of a data point.
   *
   * @return 		the number of points
   */
  public int getNumPointsLeft() {
    return m_Filter.getNumPointsLeft();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numPointsLeftTipText() {
    return m_Filter.numPointsLeftTipText();
  }

  /**
   * Sets the number of points to the right of a data point.
   *
   * @param value 	the number of points
   */
  public void setNumPointsRight(int value) {
    m_Filter.setNumPointsRight(value);
  }

  /**
   * Returns the number of points to the right of a data point.
   *
   * @return 		the number of points
   */
  public int getNumPointsRight() {
    return m_Filter.getNumPointsRight();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numPointsRightTipText() {
    return m_Filter.numPointsRightTipText();
  }
  
  /**
   * Retrieves the data point from the original signal that corresponds to
   * the provided new one.
   * 
   * @param newPoint	the point to obtain the corresponding one for
   * @param original	the original signal
   * @return		the corresponding data point, null if not found
   */
  protected abstract DataPoint getOriginalPoint(DataPoint newPoint, T original);
  
  /**
   * Subtracts the baseline from the old data point and creates a new
   * data point.
   * 
   * @param old		the old data point to subtract the baseline from
   * @param baseline	the baseline value to subtract
   * @return		the new corrected data point
   */
  protected abstract DataPoint subtract(DataPoint old, DataPoint baseline);
  
  /**
   * Performs the actual correcting.
   *
   * @param data	the data to correct
   * @return		the corrected data
   */
  @Override
  protected T processData(T data) {
    T			result;
    T			filtered;
    int			i;
    DataPoint		oldPoint;
    DataPoint		newPoint;
    
    // create baseline
    filtered = (T) m_Filter.filter(data);
    
    // correct data
    result = (T) data.getHeader();
    for (i = 0; i < filtered.size(); i++) {
      oldPoint = getOriginalPoint((DataPoint) data.toList().get(i), data);
      newPoint = subtract(oldPoint, (DataPoint) filtered.toList().get(i));
      result.add(newPoint);
    }
    
    return result;
  }
}
