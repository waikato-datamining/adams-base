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
 * Cauchy.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

/**
 <!-- globalinfo-start -->
 * Generates random numbers from a cauchy random variable with specified mean and standard deviation.
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
 * <pre>-mean &lt;double&gt; (property: mean)
 * &nbsp;&nbsp;&nbsp;The mean to use for the distribution.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 *
 * <pre>-stdev &lt;double&gt; (property: stdev)
 * &nbsp;&nbsp;&nbsp;The standard deviation to use for the distribution.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Random#cauchy(double, double)
 */
public class Cauchy
  extends AbstractDistributionBasedRandomNumberGenerator<Double> {

  /** for serialization. */
  private static final long serialVersionUID = -3764923792006993164L;

  /** the actual generator. */
  protected Random m_Random;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Generates random numbers from a cauchy random variable with specified mean and standard deviation.";
  }

  /**
   * Resets the generator.
   */
  public void reset() {
    super.reset();

    m_Random = null;
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
    return m_Random.cauchy(m_Mean, m_Stdev);
  }
}
