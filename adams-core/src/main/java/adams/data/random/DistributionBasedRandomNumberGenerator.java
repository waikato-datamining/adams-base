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
 * DistributionBasedRandomNumberGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

/**
 * Interface for distribution-based random number generators.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7171 $
 * @param <T> the type of random number to return
 */
public interface DistributionBasedRandomNumberGenerator<T extends Number>
  extends SeededRandomNumberGenerator<T> {

  /**
   * Sets the mean to use.
   *
   * @param value	the mean
   */
  public void setMean(double value);

  /**
   * Returns the mean to use.
   *
   * @return  		the mean
   */
  public double getMean();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String meanTipText();

  /**
   * Sets the stdev to use.
   *
   * @param value	the stdev
   */
  public void setStdev(double value);

  /**
   * Returns the stdev to use.
   *
   * @return  		the stdev
   */
  public double getStdev();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stdevTipText();
}
