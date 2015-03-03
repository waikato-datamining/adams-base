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
 * Triangular.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.distribution;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.TriangularDistribution;

/**
 * Triangular distribution.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see BetaDistribution
 */
public class Triangular
  extends AbstractRealDistribution {

  /** for serialization. */
  private static final long serialVersionUID = -1708992443868275973L;

  /** the a parameter. */
  protected double m_A;

  /** the c parameter. */
  protected double m_C;

  /** the b parameter. */
  protected double m_B;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The Triangular distribution.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "a", "a",
	    0.0);

    m_OptionManager.add(
	    "c", "c",
	    0.0);

    m_OptionManager.add(
	    "b", "b",
	    0.0);
  }

  /**
   * Sets the a.
   *
   * @param value	the a
   */
  public void setA(double value) {
    m_A = value;
    reset();
  }

  /**
   * Returns the a.
   *
   * @return		the a
   */
  public double getA() {
    return m_A;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String aTipText() {
    return "The a parameter.";
  }

  /**
   * Sets the c parameter.
   *
   * @param value	the c
   */
  public void setC(double value) {
    m_C = value;
    reset();
  }

  /**
   * Returns the c parameter.
   *
   * @return		the c
   */
  public double getC() {
    return m_C;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cTipText() {
    return "The c parameter.";
  }

  /**
   * Sets the b.
   *
   * @param value	the b
   */
  public void setB(double value) {
    m_B = value;
    reset();
  }

  /**
   * Returns the b.
   *
   * @return		the b
   */
  public double getB() {
    return m_B;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bTipText() {
    return "The b parameter.";
  }

  /**
   * Returns the configured distribution.
   *
   * @return		the distribution
   */
  @Override
  public RealDistribution getRealDistribution() {
    return new TriangularDistribution(m_A, m_C, m_B);
  }
}
