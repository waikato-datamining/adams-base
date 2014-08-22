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
 * Empirical.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.distribution;

import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.random.EmpiricalDistribution;

/**
 * Empirical distribution.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see EmpiricalDistribution
 */
public class Empirical
  extends AbstractRealDistribution {

  /** for serialization. */
  private static final long serialVersionUID = -1708992443868275973L;

  /** the bin count. */
  protected int m_BinCount;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The Empirical distribution.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "bin-count", "binCount",
	    EmpiricalDistribution.DEFAULT_BIN_COUNT, 1, null);
  }

  /**
   * Sets the bin count.
   *
   * @param value	the count
   */
  public void setBinCount(int value) {
    m_BinCount = value;
    reset();
  }

  /**
   * Returns the bin count.
   *
   * @return		the count
   */
  public double getBinCount() {
    return m_BinCount;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String binCountTipText() {
    return "The bin count.";
  }

  /**
   * Returns the configured distribution.
   *
   * @return		the distribution
   */
  @Override
  public RealDistribution getRealDistribution() {
    return new EmpiricalDistribution(m_BinCount);
  }
}
