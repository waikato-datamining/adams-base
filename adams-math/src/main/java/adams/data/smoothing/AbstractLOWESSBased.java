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
 * AbstractLOWESSBased.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.smoothing;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.data.container.DataContainer;
import adams.data.filter.AbstractLOWESS;
import adams.data.utils.LOWESS;

/**
 * Abstract ancestor for LOWESS-based smoothers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to smooth
 */
public abstract class AbstractLOWESSBased<T extends DataContainer>
  extends AbstractSmoother<T>
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4052647569528377770L;

  /** the LOWESS filter in use. */
  protected AbstractLOWESS m_LOWESS;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A LOWESS based smoothing algorithm.\n"
      + "For more information on LOWESS see:\n\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Returns the default LOWESS filter.
   *
   * @return		the default filter
   */
  protected abstract AbstractLOWESS getDefault();

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

    m_LOWESS = getDefault();
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
   * Sets the window size.
   *
   * @param value 	the window size
   */
  public void setWindowSize(int value) {
    m_LOWESS.setWindowSize(value);
  }

  /**
   * Returns the window size.
   *
   * @return 		the window size
   */
  public int getWindowSize() {
    return m_LOWESS.getWindowSize();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String windowSizeTipText() {
    return m_LOWESS.windowSizeTipText();
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

    result = (T) m_LOWESS.filter(data);
    m_LOWESS.cleanUp();

    return result;
  }
}
