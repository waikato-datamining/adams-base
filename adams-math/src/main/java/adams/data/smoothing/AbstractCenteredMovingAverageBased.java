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
 * AbstractCenteredMovingAverageBased.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.smoothing;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.data.container.DataContainer;
import adams.data.filter.AbstractCenteredMovingAverage;

/**
 * Abstract ancestor for Centered Moving Average based smoothers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of data to smooth
 */
public abstract class AbstractCenteredMovingAverageBased<T extends DataContainer>
  extends AbstractSmoother<T>
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4052647569528377770L;

  /** the CenteredMovingAverage filter in use. */
  protected AbstractCenteredMovingAverage m_CenteredMovingAverage;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "A CenteredMovingAverage based smoothing algorithm.\n"
	+ "For more information on CenteredMovingAverage see:\n\n"
	+ getTechnicalInformation().toString();
  }

  /**
   * Returns the default CenteredMovingAverage filter.
   *
   * @return		the default filter
   */
  protected abstract AbstractCenteredMovingAverage getDefault();

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

    m_CenteredMovingAverage = getDefault();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "window-size", "windowSize",
      15, 1, null);
  }

  /**
   * Sets the window size.
   *
   * @param value 	the window size
   */
  public void setWindowSize(int value) {
    m_CenteredMovingAverage.setWindowSize(value);
  }

  /**
   * Returns the window size.
   *
   * @return 		the window size
   */
  public int getWindowSize() {
    return m_CenteredMovingAverage.getWindowSize();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String windowSizeTipText() {
    return m_CenteredMovingAverage.windowSizeTipText();
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

    result = (T) m_CenteredMovingAverage.filter(data);
    m_CenteredMovingAverage.cleanUp();

    return result;
  }
}
