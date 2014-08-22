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
 * Exponential.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.distribution;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

/**
 * Exponential distribution.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see ExponentialDistribution
 */
public class Exponential
  extends AbstractRealDistribution {

  /** for serialization. */
  private static final long serialVersionUID = -1708992443868275973L;

  /** the mean. */
  protected double m_Mean;

  /** the inverse cumulative accuracy. */
  protected double m_InverseCumAccuracy;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The Exponential distribution.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "mean", "mean",
	    0.0);

    m_OptionManager.add(
	    "inverse-cum-accuracy", "inverseCumAccuracy",
	    ExponentialDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
  }

  /**
   * Sets the mean.
   *
   * @param value	the mean
   */
  public void setMean(double value) {
    m_Mean = value;
    reset();
  }

  /**
   * Returns the mean.
   *
   * @return		the mean
   */
  public double getMean() {
    return m_Mean;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String meanTipText() {
    return "The mean.";
  }

  /**
   * Sets the inverse cumulative accuracy.
   *
   * @param value	the accuracy
   */
  public void setInverseCumAccuracy(double value) {
    m_InverseCumAccuracy = value;
    reset();
  }

  /**
   * Returns the inverse cumulative accuracy.
   *
   * @return		the accuracy
   */
  public double getInverseCumAccuracy() {
    return m_InverseCumAccuracy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inverseCumAccuracyTipText() {
    return "The inverse cumulative accuracy.";
  }

  /**
   * Returns the configured distribution.
   *
   * @return		the distribution
   */
  @Override
  public RealDistribution getRealDistribution() {
    return new ExponentialDistribution(m_Mean, m_InverseCumAccuracy);
  }
}
