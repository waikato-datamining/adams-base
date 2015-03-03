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
 * CommonsRandomNumberGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

import org.apache.commons.math3.random.RandomGenerator;

import adams.core.option.OptionHandler;

/**
 * Interface for random number generators that return the underlying 
 * Apache commons number generator.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of random number to return
 */
public interface CommonsRandomNumberGenerator<T extends Number>
  extends RandomNumberGenerator<T>, OptionHandler {

  /**
   * Sets whether to return doubles or integers.
   *
   * @param value	true if to return doubles, false for integers
   */
  public void setGenerateDoubles(boolean value);
  
  /**
   * Returns whether to return doubles or integers.
   *
   * @return		true if to return doubles, false for integers
   */
  public boolean getGenerateDoubles();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generateDoublesTipText();

  /**
   * Sets the minimum value of the integers to generate.
   *
   * @param value	the minimum
   */
  public void setMinValue(int value);
  
  /**
   * Returns the minimum value of the integers to generate.
   *
   * @return		the minimum
   */
  public int getMinValue();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minValueTipText();
  
  /**
   * Sets the maximum value of the integers to generate.
   *
   * @param value	the maximum
   */
  public void setMaxValue(int value);
  
  /**
   * Returns the maximum value of the integers to generate.
   *
   * @return		the maximum
   */
  public int getMaxValue();
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxValueTipText();
  
  /**
   * The underlying random number generator.
   * 
   * @return		the configured generator
   */
  public RandomGenerator getRandomGenerator();
}
