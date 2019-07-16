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
 * RandomSplitGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.splitgenerator.generic.randomsplit;

import adams.core.logging.CustomLoggingLevelObject;
import adams.data.binning.Binnable;
import adams.data.binning.operation.Wrapping;
import adams.data.splitgenerator.generic.core.Subset;
import adams.data.splitgenerator.generic.randomization.DefaultRandomization;
import adams.data.splitgenerator.generic.randomization.Randomization;
import adams.data.splitgenerator.generic.splitter.DefaultSplitter;
import adams.data.splitgenerator.generic.splitter.Splitter;
import com.github.fracpete.javautils.struct.Struct2;
import gnu.trove.list.TIntList;

import java.util.List;

/**
 * Generates generic random splits.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RandomSplitGenerator
  extends CustomLoggingLevelObject {

  private static final long serialVersionUID = 1816472926230469853L;

  /** the randomization scheme. */
  protected Randomization m_Randomization;

  /** the splitter scheme. */
  protected Splitter m_Splitter;

  /**
   * Initializes the cross-validation.
   */
  public RandomSplitGenerator() {
    m_Randomization = new DefaultRandomization();
    m_Splitter      = new DefaultSplitter();
    reset();
  }

  /**
   * Resets the scheme.
   */
  public void reset() {
    m_Randomization.reset();
    m_Splitter.reset();
  }

  /**
   * Sets the randomization scheme to use.
   *
   * @param value	the scheme
   */
  public void setRandomization(Randomization value) {
    m_Randomization = value;
    reset();
  }

  /**
   * Returns the randomization scheme in use.
   *
   * @return		the scheme
   */
  public Randomization getRandomization() {
    return m_Randomization;
  }

  /**
   * Sets the splitter scheme to use.
   *
   * @param value	the scheme
   */
  public void setSplitter(Splitter value) {
    m_Splitter = value;
    reset();
  }

  /**
   * Returns the splitter scheme in use.
   *
   * @return		the scheme
   */
  public Splitter getSplitter() {
    return m_Splitter;
  }

  /**
   * Generates the split.
   * Temporarily adds the original index to the Binnable meta-data, using {@link Wrapping#TMP_INDEX} as key.
   *
   * @param data	the data to generate the pairs from
   * @param <T>		the payload type
   * @return		the fold pairs
   */
  public <T> SplitPair<Binnable<T>> generate(List<Binnable<T>> data) {
    SplitPair<Binnable<T>>				result;
    Subset<Binnable<T>>					train;
    TIntList						trainIndices;
    Subset<Binnable<T>>					test;
    TIntList						testIndices;
    Struct2<List<Binnable<T>>,List<Binnable<T>>>	split;

    // add tmp index
    data = Wrapping.addTmpIndex(data);

    // randomize/stratify
    data = m_Randomization.randomize(data);

    // split
    split = m_Splitter.split(data);
    
    // train
    trainIndices = Wrapping.getTmpIndices(split.value1);
    train        = new Subset<>(split.value1, trainIndices);
    
    // test
    testIndices = Wrapping.getTmpIndices(split.value2);
    test        = new Subset<>(split.value2, testIndices);

    // remove tmp index
    Wrapping.removeTmpIndex(data);

    result = new SplitPair<>(train, test);

    return result;
  }
}
