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
 * Cauchy.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.distribution;

import org.apache.commons.math3.distribution.CauchyDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

/**
 * Cauchy distribution.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see CauchyDistribution
 */
public class Cauchy
  extends AbstractRealDistribution {

  /** for serialization. */
  private static final long serialVersionUID = -1708992443868275973L;

  /** the median parameter. */
  protected double m_Median;

  /** the scale parameter. */
  protected double m_Scale;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The Cauchy distribution.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "median", "median",
	    0.0);

    m_OptionManager.add(
	    "scale", "scale",
	    1.0);
  }

  /**
   * Sets the median.
   *
   * @param value	the median
   */
  public void setMedian(double value) {
    m_Median = value;
    reset();
  }

  /**
   * Returns the median.
   *
   * @return		the median
   */
  public double getMedian() {
    return m_Median;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String medianTipText() {
    return "The median parameter.";
  }

  /**
   * Sets the scale.
   *
   * @param value	the scale
   */
  public void setScale(double value) {
    m_Scale = value;
    reset();
  }

  /**
   * Returns the scale.
   *
   * @return		the scale
   */
  public double getScale() {
    return m_Scale;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scaleTipText() {
    return "The scale parameter.";
  }

  /**
   * Returns the configured distribution.
   *
   * @return		the distribution
   */
  @Override
  public RealDistribution getRealDistribution() {
    return new CauchyDistribution(m_Median, m_Scale);
  }
}
