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
 * AbstractEquiDistance.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.container.DataContainer;

/**
 * Abstract ancestor for filters that equi-distance the data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to filter
 */
public abstract class AbstractEquiDistance<T extends DataContainer>
  extends AbstractFilter<T> {

  /** for serialization. */
  private static final long serialVersionUID = -2590871295104049256L;

  /** the number of points to output ("-1" uses the same amount of points as
   * currently in the data). */
  protected int m_NumPoints;

  /** indicates whether oversampling is allowed, i.e., generating more points
   * than were available in the original data. */
  protected boolean m_AllowOversampling;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-points", "numPoints",
	    -1, -1, null);

    m_OptionManager.add(
	    "allow-oversampling", "allowOversampling",
	    false);
  }

  /**
   * Sets the number of points to use.
   *
   * @param value	the number of points
   */
  public void setNumPoints(int value) {
    if ((value > 0) || (value == -1)) {
      m_NumPoints = value;
      reset();
    }
    else {
      getLogger().severe(
	  this.getClass().getName() + ": only '-1' (uses the number of points "
	  + "currently in the data) or positive numbers are allowed!");
    }
  }

  /**
   * Returns the number of points to output.
   *
   * @return		the number of points
   */
  public int getNumPoints() {
    return m_NumPoints;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String numPointsTipText() {
    return
        "The number of points to generate, '-1' will use the same amount of "
      + "points as currently in the input data.";
  }

  /**
   * Sets whether oversampling is allowed (i.e., generate more data points
   * than available in the original data).
   *
   * @param value	if true oversampling is allowed
   */
  public void setAllowOversampling(boolean value) {
    m_AllowOversampling = value;
    reset();
  }

  /**
   * Returns whether oversampling is allowed (i.e., generate more data points
   * than available in the original data).
   *
   * @return		true if oversampling is allowed
   */
  public boolean getAllowOversampling() {
    return m_AllowOversampling;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String allowOversamplingTipText() {
    return
        "If set to true, then over-sampling is allowed, ie, generating more "
      + "data points than in the original data.";
  }
}
