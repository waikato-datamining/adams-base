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
 * AbstractSplitGenerator.java
 * Copyright (C) 2012-2018 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;

import adams.core.option.AbstractOptionHandler;
import adams.flow.container.WekaTrainTestSetContainer;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Instances;

import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Ancestor for helper classes that generates dataset splits.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSplitGenerator
  extends AbstractOptionHandler
  implements SplitGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8387205583429213079L;

  /** the seed value. */
  protected long m_Seed;

  /** whether to use views. */
  protected boolean m_UseViews;

  /** the random number generator. */
  protected Random m_Random;

  /** the original dataset. */
  protected Instances m_Data;

  /** whether the iterator has been initialized. */
  protected boolean m_Initialized;

  /** the original indicies. */
  protected TIntList m_OriginalIndices;

  /**
   * Initializes the generator.
   */
  protected AbstractSplitGenerator() {
    super();
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
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    m_OriginalIndices = new TIntArrayList();
  }

  /**
   * Resets the generator.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Initialized = false;
    m_OriginalIndices.clear();
  }

  /**
   * Sets the original data.
   *
   * @param value	the data
   */
  public void setData(Instances value) {
    m_Data = (value != null) ? new Instances(value) : null;
    reset();
  }

  /**
   * Returns the original data.
   *
   * @return		the data
   */
  public Instances getData() {
    return m_Data;
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
   * @return		the seed
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
    return "The seed value for the random number generator.";
  }

  /**
   * Sets whether to uses views only.
   *
   * @param value	true if to uses views
   */
  public void setUseViews(boolean value) {
    m_UseViews = value;
    reset();
  }

  /**
   * Returns whether to use views.
   *
   * @return		true if to uses views
   */
  public boolean getUseViews() {
    return m_UseViews;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useViewsTipText() {
    return "If enabled, views are used instead of copies of the data.";
  }

  /**
   * Returns whether randomization is enabled.
   *
   * @return		true if to randomize
   */
  protected abstract boolean canRandomize();

  /**
   * Returns <tt>true</tt> if the iteration has more elements. (In other
   * words, returns <tt>true</tt> if <tt>next</tt> would return an element
   * rather than throwing an exception.)
   *
   * @return 		<tt>true</tt> if the iterator has more elements.
   */
  protected abstract boolean checkNext();

  /**
   * Returns <tt>true</tt> if the iteration has more elements. (In other
   * words, returns <tt>true</tt> if <tt>next</tt> would return an element
   * rather than throwing an exception.)
   *
   * @return 		<tt>true</tt> if the iterator has more elements.
   */
  @Override
  public synchronized boolean hasNext() {
    if (!m_Initialized)
      initializeIterator();
    return checkNext();
  }

  /**
   * Randomizes the indices using the given random number generator.
   *
   * @param indices	the indices to randomize inplace
   * @param random	the random number generator to use
   */
  protected void randomize(TIntList indices, Random random) {
    int		i;
    int		j;
    int		val;

    for (j = indices.size() - 1; j > 0; j--) {
      i = random.nextInt(j + 1);
      val = indices.get(j);
      indices.set(j, indices.get(i));
      indices.set(i, val);
    }
  }

  /**
   * Initializes the iterator, randomizes the data if required.
   *
   * @see		#canRandomize()
   */
  protected abstract void doInitializeIterator();

  /**
   * Initializes the iterator, randomizes the data if required.
   *
   * @see		#canRandomize()
   */
  public void initializeIterator() {
    doInitializeIterator();
    m_Initialized = true;
  }

  /**
   * Creates the next result.
   *
   * @return		the next result
   */
  protected abstract WekaTrainTestSetContainer createNext();

  /**
   * Returns the next element in the iteration.
   *
   * @return 				the next element in the iteration.
   * @throws NoSuchElementException 	iteration has no more elements.
   */
  @Override
  public synchronized WekaTrainTestSetContainer next() {
    if (!m_Initialized)
      initializeIterator();
    return createNext();
  }

  /**
   * Unsupported.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException("Remove not implemented!");
  }

  /**
   * Returns a short description of the generator.
   *
   * @return		a short description
   */
  @Override
  public String toString() {
    return (canRandomize() ? ", seed=" + m_Seed : "");
  }
}
