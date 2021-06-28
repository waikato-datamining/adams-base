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
 * RangeBased.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.attribute.multiplicativescattercorrection;

import adams.core.base.BaseInterval;
import adams.data.statistics.StatUtils;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Performs the correction using slopes/intercepts calculated for the defined ranges.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RangeBased
  extends AbstractMultiplicativeScatterCorrection {

  private static final long serialVersionUID = -6754404982002787538L;

  /** the ranges to calculate the intercept/slope for. */
  protected BaseInterval[] m_Ranges;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs the correction using slopes/intercepts calculated for the defined ranges.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "range", "ranges",
      new BaseInterval[]{new BaseInterval(BaseInterval.ALL)});
  }

  /**
   * Sets the wave number ranges.
   *
   * @param value 	the ranges
   */
  public void setRanges(BaseInterval[] value) {
    m_Ranges = value;
    reset();
  }

  /**
   * Returns the wave number ranges.
   *
   * @return 		the ranges
   */
  public BaseInterval[] getRanges() {
    return m_Ranges;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangesTipText() {
    return
      "The ranges of wave numbers to use for calculating the intercept/slope "
	+ "corrections.";
  }

  /**
   * Corrects the spectrum.
   *
   * @param waveno 	the wave numbers
   * @param average 	the average spectrum
   * @param data 	the spectrum to process
   * @return		the processed spectrum
   */
  @Override
  public double[] correct(double[] waveno, double[] average, double[] data) {
    double[]		result;
    TDoubleList 	x;
    TDoubleList		y;
    int			i;
    int			n;
    double[]		lr;
    double		inter;
    double		slope;

    // create copy of spectrum
    result = data.clone();

    // iterate ranges
    x = new TDoubleArrayList();
    y = new TDoubleArrayList();
    for (n = 0; n < m_Ranges.length; n++) {
      x.clear();
      y.clear();
      for (i = 0; i < average.length; i++) {
	if (m_Ranges[n].isInside(waveno[i])) {
	  y.add(data[i]);
	  x.add(average[i]);
	}
      }

      // perform linear regression
      lr    = StatUtils.linearRegression(x.toArray(), y.toArray());
      inter = lr[0];
      slope = lr[1];

      if (isLoggingEnabled())
	getLogger().info(m_Ranges[n] + ": intercept=" + inter + ", slope=" + slope);

      // correct spectrum
      for (i = 0; i < result.length; i++) {
	if (m_Ranges[n].isInside(waveno[i]))
	  result[i] = (result[i] - inter) / slope;
      }
    }

    return result;
  }
}
