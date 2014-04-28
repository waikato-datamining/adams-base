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

/**
 * ChiSquare.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;


/**
 <!-- globalinfo-start -->
 * Random generator that generates random doubles (0-1) using Java's java.util.Random class.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the random number generator.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-df &lt;int&gt; (property: degreesFreedom)
 * &nbsp;&nbsp;&nbsp;The degrees of freedom to use.
 * &nbsp;&nbsp;&nbsp;default: 2
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Random#chi2(int)
 */
public class ChiSquare
  extends AbstractSeededRandomNumberGenerator<Double> {

  /** for serialization. */
  private static final long serialVersionUID = -8789721700340129969L;

  /** the degrees of freedom to use. */
  protected int m_DegreesFreedom;

  /** the random number generator to use. */
  protected Random m_Random;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Random generator that generates random doubles (0-1) using Java's java.util.Random class.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "df", "degreesFreedom",
	    2);
  }

  /**
   * Resets the generator.
   */
  public void reset() {
    super.reset();

    m_Random = null;
  }

  /**
   * Sets the degrees of freedom to use.
   *
   * @param value	the df
   */
  public void setDegreesFreedom(int value) {
    m_DegreesFreedom = value;
    reset();
  }

  /**
   * Returns the degrees of freedom to use.
   *
   * @return		the df
   */
  public int getDegreesFreedom() {
    return m_DegreesFreedom;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String degreesFreedomTipText() {
    return "The degrees of freedom to use.";
  }

  /**
   * Performs optional checks.
   * <p/>
   * Initializes the random number generator.
   */
  protected void check() {
    super.check();

    if (m_Random == null)
      m_Random = new Random(m_Seed);
  }

  /**
   * Returns the next random number. Does the actual computation.
   *
   * @return		the next number
   */
  protected Double doNext() {
    return m_Random.chi2(m_DegreesFreedom);
  }
}
