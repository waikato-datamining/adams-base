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
 * DefaultRandomization.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.splitgenerator.generic.randomization;

import adams.core.Randomizable;
import adams.data.binning.Binnable;
import adams.data.binning.operation.Randomize;

import java.util.List;
import java.util.Random;

/**
 * Randomizes the data using a random number generator initialized with the seed value.
 * Calling reset() resets the number generator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see Randomize#randomizeData(List, long)
 */
public class DefaultRandomization
  extends AbstractRandomization
  implements Randomizable {

  private static final long serialVersionUID = -7659572567934958935L;

  /** the seed value. */
  protected long m_Seed;

  /** the random number generator used. */
  protected transient Random m_Random;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Randomizes the data using a random number generator initialized with the seed value.\n"
      + "Calling reset() resets the number generator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "seed", "seed",
      1L);
  }

  /**
   * Resets the scheme.
   */
  @Override
  public void reset() {
    super.reset();
    m_Random = null;
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value to use for the random number generator.";
  }

  /**
   * Randomizes the data.
   *
   * @param data	the data to randomize
   * @param <T>		the payload type
   * @return		the randomized data
   */
  @Override
  protected <T> List<Binnable<T>> doRandomize(List<Binnable<T>> data) {
    if (m_Random == null)
      m_Random = new Random(m_Seed);
    return Randomize.randomizeData(data, m_Random);
  }
}
