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
 * Beta.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;


/**
 <!-- globalinfo-start -->
 * Generates random numbers from a Beta random variable.
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
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the random number generator.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-a &lt;double&gt; (property: a)
 * &nbsp;&nbsp;&nbsp;The first parameter for the beta random variable.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 *
 * <pre>-b &lt;double&gt; (property: b)
 * &nbsp;&nbsp;&nbsp;The second parameter for the beta random variable.
 * &nbsp;&nbsp;&nbsp;default: 2.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Random#beta(double, double)
 */
public class Beta
  extends AbstractSeededRandomNumberGenerator<Double> {

  /** for serialization. */
  private static final long serialVersionUID = 8058404120296659028L;

  /** the first parameter of the beta random variable. */
  protected double m_A;

  /** the second parameter of the beta random variable. */
  protected double m_B;

  /** the random number generator to use. */
  protected Random m_Random;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Generates random numbers from a Beta random variable.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "a", "a",
	    1.0);

    m_OptionManager.add(
	    "b", "b",
	    2.0);
  }

  /**
   * Resets the generator.
   */
  public void reset() {
    super.reset();

    m_Random = null;
  }

  /**
   * Sets the first parameter for the beta random variable.
   *
   * @param value	the first parameter
   */
  public void setA(double value) {
    m_A = value;
    reset();
  }

  /**
   * Returns the first parameter for the beta random variable.
   *
   * @return		the first parameter
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
    return "The first parameter for the beta random variable.";
  }

  /**
   * Sets the second parameter for the beta random variable.
   *
   * @param value	the second parameter
   */
  public void setB(double value) {
    m_B = value;
    reset();
  }

  /**
   * Returns the second parameter for the beta random variable.
   *
   * @return		the second parameter
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
    return "The second parameter for the beta random variable.";
  }

  /**
   * Performs optional checks.
   * <br><br>
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
    return m_Random.beta(m_A, m_B);
  }
}
