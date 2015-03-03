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
 * RandomDoubleRangeGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

/**
 * Interface for random number generators that support returning random
 * doubles within a certain range.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of random number to return
 */
public interface RandomDoubleRangeGenerator<T extends Number>
  extends RandomNumberGenerator<T> {

  /**
   * Sets the minimum value of the numbers to generate.
   *
   * @param value	the minimum
   */
  public void setMinValue(double value);

  /**
   * Returns the minimum value of the numbers to generate.
   *
   * @return		the minimum
   */
  public double getMinValue();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minValueTipText();

  /**
   * Sets the maximum value of the numbers to generate.
   *
   * @param value	the maximum
   */
  public void setMaxValue(double value);

  /**
   * Returns the maximum value of the numbers to generate.
   *
   * @return		the maximum
   */
  public double getMaxValue();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxValueTipText();
}
