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
 * Mean.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.attribute.detrend;

import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Performs the correction using simply the mean.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Mean
  extends AbstractDetrend {

  private static final long serialVersionUID = -6754404982002787538L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs the correction using simply the mean.";
  }

  /**
   * Corrects the spectrum.
   *
   * @param waveno 	the wave numbers
   * @param data 	the spectrum to process
   * @return		the processed spectrum
   */
  @Override
  public double[] correct(double[] waveno, double[] data) {
    double[]		result;
    int			i;
    double		mean;

    // create copy of spectrum
    result = data.clone();

    // calculate mean
    mean = StatUtils.mean(data);

    if (isLoggingEnabled())
      getLogger().info(getClass().getName() + ": mean=" + mean);

    // correct spectrum
    for (i = 0; i < result.length; i++)
      result[i] -= mean;

    return result;
  }
}
