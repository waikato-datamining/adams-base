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
 * AbstractSavitzkyGolayBased.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.smoothing;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.data.container.DataContainer;
import adams.data.filter.AbstractSavitzkyGolay;

/**
 * Abstract ancestor for Savitzky-Golay-based smoothers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to smooth
 */
public abstract class AbstractSavitzkyGolayBased<T extends DataContainer>
  extends AbstractSmoother<T>
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4052647569528377770L;

  /** the Savitzly-Golay filter in use. */
  protected AbstractSavitzkyGolay m_SavitzkyGolay;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A Savitzky-Golay based smoothing algorithm.\n"
      + "It uses a Savitzky-Golay filter with derivative order 0 and adding of "
      + "mass-spec data turned on.\n\n"
      + "For more information on Savitzky-Golay see:\n\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Returns the default Savitzky-Golay filter.
   *
   * @return		the default filter
   */
  protected abstract AbstractSavitzkyGolay getDefault();

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    return getDefault().getTechnicalInformation();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_SavitzkyGolay = getDefault();
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
   * Sets the polynomial order.
   *
   * @param value 	the order
   */
  public void setPolynomialOrder(int value) {
    m_SavitzkyGolay.setPolynomialOrder(value);
  }

  /**
   * Returns the polynominal order.
   *
   * @return 		the order
   */
  public int getPolynomialOrder() {
    return m_SavitzkyGolay.getPolynomialOrder();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String polynomialOrderTipText() {
    return m_SavitzkyGolay.polynomialOrderTipText();
  }

  /**
   * Sets the number of points to the left of a data point.
   *
   * @param value 	the number of points
   */
  public void setNumPointsLeft(int value) {
    m_SavitzkyGolay.setNumPointsLeft(value);
  }

  /**
   * Returns the number of points to the left of a data point.
   *
   * @return 		the number of points
   */
  public int getNumPointsLeft() {
    return m_SavitzkyGolay.getNumPointsLeft();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numPointsLeftTipText() {
    return m_SavitzkyGolay.numPointsLeftTipText();
  }

  /**
   * Sets the number of points to the right of a data point.
   *
   * @param value 	the number of points
   */
  public void setNumPointsRight(int value) {
    m_SavitzkyGolay.setNumPointsRight(value);
  }

  /**
   * Returns the number of points to the right of a data point.
   *
   * @return 		the number of points
   */
  public int getNumPointsRight() {
    return m_SavitzkyGolay.getNumPointsRight();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numPointsRightTipText() {
    return m_SavitzkyGolay.numPointsRightTipText();
  }

  /**
   * Performs the actual smoothing.
   *
   * @param data	the to smooth
   * @return		the smoothed data
   */
  @Override
  protected T processData(T data) {
    T	result;

    result = (T) m_SavitzkyGolay.filter(data);
    m_SavitzkyGolay.cleanUp();

    return result;
  }
}
