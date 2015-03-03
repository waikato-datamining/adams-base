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
 * ChiSquared.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.distribution;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

/**
 * ChiSquared distribution.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see ChiSquaredDistribution
 */
public class ChiSquared
  extends AbstractRealDistribution {

  /** for serialization. */
  private static final long serialVersionUID = -1708992443868275973L;

  /** the degrees of freedom. */
  protected double m_DegreesOfFreedom;

  /** the inverse cumulative accuracy. */
  protected double m_InverseCumAccuracy;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The ChiSquared distribution.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "degrees-of-freedom", "degreesOfFreedom",
	    0.0);

    m_OptionManager.add(
	    "inverse-cum-accuracy", "inverseCumAccuracy",
	    ChiSquaredDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
  }

  /**
   * Sets the degrees of freedom.
   *
   * @param value	the degrees
   */
  public void setDegreesOfFreedom(double value) {
    m_DegreesOfFreedom = value;
    reset();
  }

  /**
   * Returns the degrees of freedom.
   *
   * @return		the degrees
   */
  public double getDegreesOfFreedom() {
    return m_DegreesOfFreedom;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String degreesOfFreedomTipText() {
    return "The degrees of freedom.";
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
    return new ChiSquaredDistribution(m_DegreesOfFreedom, m_InverseCumAccuracy);
  }
}
