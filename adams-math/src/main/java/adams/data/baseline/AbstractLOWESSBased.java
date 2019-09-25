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
 * AbstractLOWESSBased.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.baseline;

import adams.core.TechnicalInformation;
import adams.data.container.DataContainer;
import adams.data.container.DataPoint;
import adams.data.filter.AbstractLOWESS;
import adams.data.utils.LOWESS;

/**
 * Ancestor for LOWESS-based baseline correction schemes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractLOWESSBased<T extends DataContainer>
  extends AbstractBaselineCorrection<T> {
  
  /** for serialization. */
  private static final long serialVersionUID = 1662223546956780389L;
  
  /** the filter to use. */
  protected AbstractLOWESS m_Filter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A baseline correction scheme that uses LOWESS smoothing to determine the baseline.\n\n"
      + "For more information see:\n\n"
      + getTechnicalInformation().toString();
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
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "window-size", "windowSize",
	    20, LOWESS.MIN_WINDOW_SIZE, null);
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
   * Returns a new instance of a LOWESS filter.
   * 
   * @return		the filter
   */
  protected abstract AbstractLOWESS getFilter();

  /**
   * Sets the polynomial order.
   *
   * @param value 	the order
   */
  public void setWindowSize(int value) {
    m_Filter.setWindowSize(value);
  }

  /**
   * Returns the polynominal order.
   *
   * @return 		the order
   */
  public int getWindowSize() {
    return m_Filter.getWindowSize();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String windowSizeTipText() {
    return m_Filter.windowSizeTipText();
  }
  
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
    for (i = 0; i < data.size(); i++) {
      oldPoint = (DataPoint) data.toList().get(i);
      newPoint = subtract(oldPoint, (DataPoint) filtered.toList().get(i));
      result.add(newPoint);
    }
    
    return result;
  }
}
