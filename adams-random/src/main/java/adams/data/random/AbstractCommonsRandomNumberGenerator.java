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
 * AbstractCommonsRandomNumberGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

import org.apache.commons.math3.random.RandomGenerator;

/**
 * Ancestor Apache commons-based random number generators.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCommonsRandomNumberGenerator
  extends AbstractSeededRandomNumberGenerator<Number>
  implements CommonsRandomNumberGenerator, RandomIntegerRangeGenerator<Number> {

  /** for serialization. */
  private static final long serialVersionUID = 5972847638157742849L;

  /** whether to generate doubles or integers. */
  protected boolean m_GenerateDoubles;
  
  /** the minimum value for the numbers to generate. */
  protected int m_MinValue;

  /** the maximum value for the numbers to generate. */
  protected int m_MaxValue;

  /** the generator used internally. */
  protected RandomGenerator m_Generator;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generate-doubles", "generateDoubles",
	    false);

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
    
    m_Generator = null;
  }

  /**
   * Sets whether to return doubles or integers.
   *
   * @param value	true if to return doubles, false for integers
   */
  @Override
  public void setGenerateDoubles(boolean value) {
    m_GenerateDoubles = value;
    reset();
  }

  /**
   * Returns whether to return doubles or integers.
   *
   * @return		true if to return doubles, false for integers
   */
  @Override
  public boolean getGenerateDoubles() {
    return m_GenerateDoubles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String generateDoublesTipText() {
    return "If enabled, doubles instead of integers are returned.";
  }

  /**
   * Sets the minimum value of the integers to generate.
   *
   * @param value	the minimum
   */
  public void setMinValue(int value) {
    m_MinValue = value;
    reset();
  }

  /**
   * Returns the minimum value of the integers to generate.
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
    return "The smallest integer that could be generated.";
  }

  /**
   * Sets the maximum value of the integers to generate.
   *
   * @param value	the maximum
   */
  public void setMaxValue(int value) {
    m_MaxValue = value;
    reset();
  }

  /**
   * Returns the maximum value of the integers to generate.
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
    return "The largest integer that could be generated.";
  }

  /**
   * The underlying random number generator.
   * 
   * @return		the configured generator
   */
  @Override
  public abstract RandomGenerator getRandomGenerator();

  /**
   * Performs optional checks.
   * <p/>
   * Initializes the internal generator if necessary.
   */
  @Override
  protected void check() {
    super.check();

    if (getMinValue() >= getMaxValue())
      throw new IllegalStateException(
	  "MinValue must be smaller than MaxValue: "
	  + "MinValue=" + getMinValue() + ", MaxValue=" + getMaxValue());

    if (m_Generator == null)
      m_Generator = getRandomGenerator();
  }

  /**
   * Returns the next random number. Does the actual computation.
   *
   * @return		the next number
   */
  @Override
  protected Number doNext() {
    if (m_GenerateDoubles)
      return m_Generator.nextDouble();
    else
      return m_Generator.nextInt(m_MaxValue - m_MinValue + 1) + m_MinValue;
  }
}
