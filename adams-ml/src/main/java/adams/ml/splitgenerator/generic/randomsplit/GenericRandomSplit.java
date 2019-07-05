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
 * GenericRandomSplit.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.ml.splitgenerator.generic.randomsplit;

import adams.core.logging.CustomLoggingLevelObject;
import adams.data.binning.Binnable;
import adams.ml.splitgenerator.generic.core.Subset;
import adams.ml.splitgenerator.generic.randomization.DefaultRandomization;
import adams.ml.splitgenerator.generic.randomization.Randomization;
import adams.ml.splitgenerator.generic.splitter.DefaultSplitter;
import adams.ml.splitgenerator.generic.splitter.Splitter;
import com.github.fracpete.javautils.enumerate.Enumerated;
import com.github.fracpete.javautils.struct.Struct2;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.List;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Generates generic random splits.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GenericRandomSplit
  extends CustomLoggingLevelObject {

  private static final long serialVersionUID = 1816472926230469853L;

  /** the temporary index stored in the binnable meta-data. */
  public static final String TMP_INDEX = "$$$tmpindex$$$";

  /** the randomization scheme. */
  protected Randomization m_Randomization;

  /** the splitter scheme. */
  protected Splitter m_Splitter;

  /**
   * Initializes the cross-validation.
   */
  public GenericRandomSplit() {
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
   * Temporarily adds the original index to the Binnable meta-data, using {@link #TMP_INDEX} as key.
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
    for (Enumerated<Binnable<T>> d: enumerate(data))
      d.value.addMetaData(TMP_INDEX, d.index);

    // randomize/stratify
    data = m_Randomization.randomize(data);

    // split
    split = m_Splitter.split(data);
    
    // train
    trainIndices = new TIntArrayList();
    for (Binnable<T> item: split.value1)
      trainIndices.add((Integer) item.getMetaData(TMP_INDEX));
    train = new Subset<>(split.value1, trainIndices);
    
    // test
    testIndices = new TIntArrayList();
    for (Binnable<T> item: split.value2)
      testIndices.add((Integer) item.getMetaData(TMP_INDEX));
    test = new Subset<>(split.value2, testIndices);

    // remove tmp index
    for (Binnable<T> item: data)
      item.removeMetaData(TMP_INDEX);

    result = new SplitPair<>(train, test);

    return result;
  }
}
