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
 * Uniform.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.distribution;

import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import JSci.maths.statistics.UniformDistribution;

/**
 * Uniform distribution.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see UniformDistribution
 */
public class Uniform
  extends AbstractRealDistribution {

  /** for serialization. */
  private static final long serialVersionUID = -1708992443868275973L;

  /** the lower bound. */
  protected double m_LowerBound;

  /** the upper bound. */
  protected double m_UpperBound;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The Uniform distribution.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "lower-bound", "lowerBound",
	    0.0);

    m_OptionManager.add(
	    "upper-bound", "upperBound",
	    1.0);
  }

  /**
   * Sets the lower bound.
   *
   * @param value	the lower bound
   */
  public void setLowerBound(double value) {
    m_LowerBound = value;
    reset();
  }

  /**
   * Returns the lower bound.
   *
   * @return		the lower bound
   */
  public double getLowerBound() {
    return m_LowerBound;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String meanTipText() {
    return "The lower bound.";
  }

  /**
   * Sets the upper bound.
   *
   * @param value	the upper bound
   */
  public void setUpperBound(double value) {
    m_UpperBound = value;
    reset();
  }

  /**
   * Returns the upper bound.
   *
   * @return		the upper bound
   */
  public double getUpperBound() {
    return m_UpperBound;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String upperBoundTipText() {
    return "The upper bound.";
  }

  /**
   * Returns the configured distribution.
   *
   * @return		the distribution
   */
  @Override
  public RealDistribution getRealDistribution() {
    return new UniformRealDistribution(m_LowerBound, m_UpperBound);
  }
}
