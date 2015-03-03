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
 * Normal.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.distribution;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

/**
 * Normal distribution.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see NormalDistribution
 */
public class Normal
  extends AbstractRealDistribution {

  /** for serialization. */
  private static final long serialVersionUID = -1708992443868275973L;

  /** the mean. */
  protected double m_Mean;

  /** the standard deviation. */
  protected double m_StandardDeviation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The Normal distribution.";
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
	    "standard-deviation", "standardDeviation",
	    1.0);
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
   * Sets the standard deviation.
   *
   * @param value	the standard deviation
   */
  public void setStandardDeviation(double value) {
    m_StandardDeviation = value;
    reset();
  }

  /**
   * Returns the standard deviation.
   *
   * @return		the standard deviation
   */
  public double getStandardDeviation() {
    return m_StandardDeviation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String standardDeviationTipText() {
    return "The standard deviation.";
  }

  /**
   * Returns the configured distribution.
   *
   * @return		the distribution
   */
  @Override
  public RealDistribution getRealDistribution() {
    return new NormalDistribution(m_Mean, m_StandardDeviation);
  }
}
