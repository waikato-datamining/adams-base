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
 * JavaRandomIntUnseeded.java
 * Copyright (C) 2010-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

/**
 <!-- globalinfo-start -->
 * Random generator that generates random integers using Java's java.util.Random class (unseeded).
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
 * <pre>-min-value &lt;int&gt; (property: minValue)
 * &nbsp;&nbsp;&nbsp;The smallest number that could be generated.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-max-value &lt;int&gt; (property: maxValue)
 * &nbsp;&nbsp;&nbsp;The largest number that could be generated.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see java.util.Random#nextInt(int)
 */
public class JavaRandomIntUnseeded
  extends AbstractRandomNumberGenerator<Integer>
  implements RandomIntegerRangeGenerator<Integer> {

  /** for serialization. */
  private static final long serialVersionUID = 8754317840175980117L;

  /** the minimum value for the numbers to generate. */
  protected int m_MinValue;

  /** the maximum value for the numbers to generate. */
  protected int m_MaxValue;

  /** the random number generator to use. */
  protected java.util.Random m_Random;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Random generator that generates random integers using Java's java.util.Random class (unseeded).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "min-value", "minValue",
	    1);

    m_OptionManager.add(
	    "max-value", "maxValue",
	    1000);
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
   * Sets the minimum value of the numbers to generate.
   *
   * @param value	the minimum
   */
  public void setMinValue(int value) {
    m_MinValue = value;
    reset();
  }

  /**
   * Returns the minimum value of the numbers to generate.
   *
   * @return		the minimum
   */
  public int getMinValue() {
    return m_MinValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minValueTipText() {
    return "The smallest number that could be generated.";
  }

  /**
   * Sets the maximum value of the numbers to generate.
   *
   * @param value	the maximum
   */
  public void setMaxValue(int value) {
    m_MaxValue = value;
    reset();
  }

  /**
   * Returns the maximum value of the numbers to generate.
   *
   * @return		the maximum
   */
  public int getMaxValue() {
    return m_MaxValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxValueTipText() {
    return "The largest number that could be generated.";
  }

  /**
   * Performs optional checks.
   * <br><br>
   * Initializes the random number generator.
   */
  @Override
  protected void check() {
    super.check();

    if (getMinValue() >= getMaxValue())
      throw new IllegalStateException(
	  "MinValue must be smaller than MaxValue: "
	  + "MinValue=" + getMinValue() + ", MaxValue=" + getMaxValue());

    if (m_Random == null)
      m_Random = new java.util.Random();
  }

  /**
   * Returns the next random number. Does the actual computation.
   *
   * @return		the next number
   */
  @Override
  protected Integer doNext() {
    return m_Random.nextInt(m_MaxValue - m_MinValue + 1) + m_MinValue;
  }
}
