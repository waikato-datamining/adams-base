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
 * Gamma.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.distribution;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

/**
 * Gamma distribution.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see GammaDistribution
 */
public class Gamma
  extends AbstractRealDistribution {

  /** for serialization. */
  private static final long serialVersionUID = -1708992443868275973L;

  /** the shape parameter. */
  protected double m_Shape;

  /** the scale parameter. */
  protected double m_Scale;

  /** the inverse cumulative accuracy. */
  protected double m_InverseCumAccuracy;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The Gamma distribution.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "shape", "shape",
	    0.0);

    m_OptionManager.add(
	    "scale", "scale",
	    1.0);

    m_OptionManager.add(
	    "inverse-cum-accuracy", "inverseCumAccuracy",
	    GammaDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
  }

  /**
   * Sets the shape.
   *
   * @param value	the shape
   */
  public void setShape(double value) {
    m_Shape = value;
    reset();
  }

  /**
   * Returns the shape.
   *
   * @return		the shape
   */
  public double getShape() {
    return m_Shape;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shapeTipText() {
    return "The shape parameter.";
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
    return new GammaDistribution(m_Shape, m_Scale, m_InverseCumAccuracy);
  }
}
