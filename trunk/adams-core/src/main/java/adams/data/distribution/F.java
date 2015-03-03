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
 * FDistribution.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.distribution;

import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

/**
 * FDistribution distribution.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see FDistributionDistribution
 */
public class F
  extends AbstractRealDistribution {

  /** for serialization. */
  private static final long serialVersionUID = -1708992443868275973L;

  /** the numerator degrees of freedom. */
  protected double m_NumeratorDegreesOfFreedom;

  /** the denominator degrees of freedom. */
  protected double m_DenominatorDegreesOfFreedom;

  /** the inverse cumulative accuracy. */
  protected double m_InverseCumAccuracy;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The F distribution.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "numerator-degrees-of-freedom", "numeratorDegreesOfFreedom",
	    0.0);

    m_OptionManager.add(
	    "denominator-degrees-of-freedom", "denominatorDegreesOfFreedom",
	    0.0);

    m_OptionManager.add(
	    "inverse-cum-accuracy", "inverseCumAccuracy",
	    FDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
  }

  /**
   * Sets the numerator degrees of freedom.
   *
   * @param value	the degrees
   */
  public void setNumeratorDegreesOfFreedom(double value) {
    m_NumeratorDegreesOfFreedom = value;
    reset();
  }

  /**
   * Returns the numerator degrees of freedom.
   *
   * @return		the degrees
   */
  public double getNumeratorDegreesOfFreedom() {
    return m_NumeratorDegreesOfFreedom;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numeratorDegreesOfFreedomTipText() {
    return "The numerator degrees of freedom.";
  }

  /**
   * Sets the denominator degrees of freedom.
   *
   * @param value	the degrees
   */
  public void setDenominatorDegreesOfFreedom(double value) {
    m_DenominatorDegreesOfFreedom = value;
    reset();
  }

  /**
   * Returns the denominator degrees of freedom.
   *
   * @return		the degrees
   */
  public double getDenominatorDegreesOfFreedom() {
    return m_DenominatorDegreesOfFreedom;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String denominatorDegreesOfFreedomTipText() {
    return "The denominator degrees of freedom.";
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
    return new FDistribution(m_NumeratorDegreesOfFreedom, m_DenominatorDegreesOfFreedom, m_InverseCumAccuracy);
  }
}
