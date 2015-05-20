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
 * Exponential.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;


/**
 <!-- globalinfo-start -->
 * Generates random numbers from an Exponential random variable (Mean = 1/lambda, variance = 1/lambda^2).
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
 * <pre>-lambda &lt;double&gt; (property: lambda)
 * &nbsp;&nbsp;&nbsp;The lambda parameter for the exponential random variable.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Random#exponential(double)
 */
public class Exponential
  extends AbstractSeededRandomNumberGenerator<Double> {

  /** for serialization. */
  private static final long serialVersionUID = -8911652004331474297L;

  /** the lambda parameter. */
  protected double m_Lambda;

  /** the random number generator to use. */
  protected Random m_Random;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates random numbers from an Exponential random variable "
      + "(Mean = 1/lambda, variance = 1/lambda^2).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "lambda", "lambda",
	    1.0);
  }

  /**
   * Resets the generator.
   */
  @Override
  public void reset() {
    super.reset();

    m_Random = null;
  }

  /**
   * Sets the first parameter for the beta random variable.
   *
   * @param value	the first parameter
   */
  public void setLambda(double value) {
    if (value != 0.0) {
      m_Lambda = value;
      reset();
    }
    else {
      getLogger().severe("Lambda cannot be zero!");
    }
  }

  /**
   * Returns the first parameter for the beta random variable.
   *
   * @return		the first parameter
   */
  public double getLambda() {
    return m_Lambda;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lambdaTipText() {
    return "The lambda parameter for the exponential random variable.";
  }

  /**
   * Performs optional checks.
   * <br><br>
   * Initializes the random number generator.
   */
  @Override
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
  @Override
  protected Double doNext() {
    return m_Random.exponential(m_Lambda);
  }
}
