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
 * AbstractSeededRandomNumberGenerator.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

import adams.core.Randomizable;

/**
 * Ancestor for seeded random number generators.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of random number to return
 */
public abstract class AbstractDistributionBasedRandomNumberGenerator<T extends Number>
  extends AbstractSeededRandomNumberGenerator<T>
  implements Randomizable {

  /** for serialization. */
  private static final long serialVersionUID = -4193009658719437993L;

  /** the mean. */
  protected double m_Mean;

  /** the standard deviation. */
  protected double m_Stdev;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "mean", "mean",
	    getDefaultMean());

    m_OptionManager.add(
	    "stdev", "stdev",
	    getDefaultStdev(), 0.000001, null);
  }

  /**
   * Returns the default mean to use.
   *
   * @return		the mean
   */
  protected double getDefaultMean() {
    return 0.0;
  }

  /**
   * Returns the default standard deviation to use.
   *
   * @return		the stdev
   */
  protected double getDefaultStdev() {
    return 1.0;
  }

  /**
   * Sets the mean to use.
   *
   * @param value	the mean
   */
  public void setMean(double value) {
    m_Mean = value;
    reset();
  }

  /**
   * Returns the mean to use.
   *
   * @return  		the mean
   */
  public double getMean() {
    return m_Mean;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String meanTipText() {
    return "The mean to use for the distribution.";
  }

  /**
   * Sets the stdev to use.
   *
   * @param value	the stdev
   */
  public void setStdev(double value) {
    if (value > 0) {
      m_Stdev = value;
      reset();
    }
    else {
      getLogger().severe("Standard deviation must be >0, provided: " + value);
    }
  }

  /**
   * Returns the stdev to use.
   *
   * @return  		the stdev
   */
  public double getStdev() {
    return m_Stdev;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stdevTipText() {
    return "The standard deviation to use for the distribution.";
  }
}
