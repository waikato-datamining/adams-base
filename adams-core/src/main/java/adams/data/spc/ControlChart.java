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
 * ControlChart.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.spc;

import adams.core.TechnicalInformationHandler;
import adams.core.option.OptionHandler;

/**
 * Ancestor for all control charts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ControlChart
  extends OptionHandler, TechnicalInformationHandler {

  /**
   * Returns the chart name.
   *
   * @return		the chart name
   */
  public String getName();

  /**
   * Sets the sample size.
   *
   * @param value	the sample size
   */
  public void setSampleSize(int value);

  /**
   * Returns the sample size.
   *
   * @return		the sample size
   */
  public int getSampleSize();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sampleSizeTipText();
}
