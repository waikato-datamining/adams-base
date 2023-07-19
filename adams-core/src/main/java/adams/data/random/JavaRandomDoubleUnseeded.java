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
 * JavaRandomDoubleUnseeded.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

/**
 <!-- globalinfo-start -->
 * Random generator that generates random doubles (0-1) using Java's java.util.Random class (unseeded).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see java.util.Random#nextDouble()
 */
public class JavaRandomDoubleUnseeded
  extends AbstractRandomNumberGenerator<Double> {

  /** for serialization. */
  private static final long serialVersionUID = -9108188550486580598L;

  /** the random number generator to use. */
  protected java.util.Random m_Random;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Random generator that generates random doubles (0-1) using Java's java.util.Random class (unseeded).";
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
   * <br><br>
   * Initializes the random number generator.
   */
  protected void check() {
    super.check();

    if (m_Random == null)
      m_Random = new java.util.Random();
  }

  /**
   * Returns the next random number. Does the actual computation.
   *
   * @return		the next number
   */
  protected Double doNext() {
    return m_Random.nextDouble();
  }
}
