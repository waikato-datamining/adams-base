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
 * AbstractSplitGenerator.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;
import adams.flow.container.WekaTrainTestSetContainer;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Instances;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Ancestor for helper classes that generates dataset splits.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSplitGenerator
  implements Serializable, Iterator<WekaTrainTestSetContainer> {

  /** for serialization. */
  private static final long serialVersionUID = -8387205583429213079L;

  /** the seed value. */
  protected long m_Seed;

  /** whether to create views. */
  protected boolean m_CreateView;

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
   *
   * @param data	the full dataset
   */
  public AbstractSplitGenerator(Instances data, long seed) {
    if (data == null)
      throw new IllegalArgumentException("No data provided!");

    m_Data            = new Instances(data);
    m_Seed            = seed;
    m_Initialized     = false;
    m_OriginalIndices = new TIntArrayList();
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
   * Returns the seed value.
   *
   * @return		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Sets whether to create views only.
   *
   * @param value	true if to create views
   */
  public void setCreateView(boolean value) {
    m_CreateView  = value;
    m_Initialized = false;
  }

  /**
   * Returns whether to create views.
   *
   * @return		true if to create views
   */
  public boolean getCreateView() {
    return m_CreateView;
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
   * Generates the original indices.
   *
   * @return	the original indices
   */
  protected TIntList originalIndices() {
    TIntList 	result;
    int		i;

    result = new TIntArrayList();
    for (i = 0; i < m_Data.numInstances(); i++)
      result.add(i);

    if (canRandomize())
      randomize(result, new Random(m_Seed));

    return result;
  }

  /**
   * Initializes the iterator, randomizes the data if required.
   *
   * @see		#canRandomize()
   */
  protected void initialize() {
    int		i;

    m_Initialized     = true;
    m_OriginalIndices = originalIndices();

    if (canRandomize()) {
      m_Random = new Random(m_Seed);
      if (!m_CreateView)
	m_Data.randomize(m_Random);
    }
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
      initialize();
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
    return "data=" + m_Data.relationName() + (canRandomize() ? ", seed=" + m_Seed : "");
  }
}
