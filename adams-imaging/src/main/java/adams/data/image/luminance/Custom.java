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
 * Custom.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.image.luminance;

import adams.data.statistics.StatUtils;

/**
 * Custom luminance parameters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Custom
  extends AbstractLuminanceParameters {

  private static final long serialVersionUID = -8226279974532958311L;

  /** the value for R. */
  protected double m_R;

  /** the value for G. */
  protected double m_G;

  /** the value for B. */
  protected double m_B;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Custom luminance parameters.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "r", "R",
      0.2989, 0.0, 1.0);

    m_OptionManager.add(
      "g", "G",
      0.5870, 0.0, 1.0);

    m_OptionManager.add(
      "b", "B",
      0.1140, 0.0, 1.0);
  }

  /**
   * Sets the R parameter.
   *
   * @param value	the value
   */
  public void setR(double value) {
    if (getOptionManager().isValid("R", value)) {
      m_R = value;
      reset();
    }
  }

  /**
   * Returns the R parameter.
   *
   * @return		the value
   */
  public double getR() {
    return m_R;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String RTipText() {
    return "The R parameter.";
  }

  /**
   * Sets the R parameter.
   *
   * @param value	the value
   */
  public void setG(double value) {
    if (getOptionManager().isValid("G", value)) {
      m_G = value;
      reset();
    }
  }

  /**
   * Returns the G parameter.
   *
   * @return		the value
   */
  public double getG() {
    return m_G;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String GTipText() {
    return "The G parameter.";
  }

  /**
   * Sets the B parameter.
   *
   * @param value	the value
   */
  public void setB(double value) {
    if (getOptionManager().isValid("B", value)) {
      m_B = value;
      reset();
    }
  }

  /**
   * Returns the B parameter.
   *
   * @return		the value
   */
  public double getB() {
    return m_B;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String BTipText() {
    return "The B parameter.";
  }

  /**
   * Returns the parameters for R, G and B (sum up to 1).
   *
   * @return		the parameters
   */
  @Override
  public double[] getParameters() {
    return StatUtils.normalize(new double[]{m_R, m_G, m_B});
  }
}
