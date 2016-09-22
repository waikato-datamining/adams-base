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
 * RandomSplitGenerator.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;

import weka.core.Instances;
import adams.flow.container.WekaTrainTestSetContainer;

/**
 * Generates random splits of datasets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RandomSplitGenerator
  extends AbstractSplitGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -4813006743965500489L;

  /** the percentage. */
  protected double m_Percentage;
  
  /** whether to preserve the order. */
  protected boolean m_PreserveOrder;
  
  /** whether the split was generated. */
  protected boolean m_Generated;
  
  /**
   * Initializes the generator. Does not preserve the order.
   * 
   * @param data	the dataset to split
   * @param seed	the seed value to use for randomization
   * @param percentage	the percentage of the training set
   */
  public RandomSplitGenerator(Instances data, long seed, double percentage) {
    super(data, seed);
    
    m_Percentage    = percentage;
    m_PreserveOrder = false;
  }
  
  /**
   * Initializes the generator. Preserves the order.
   * 
   * @param data	the dataset to split
   * @param percentage	the percentage of the training set
   */
  public RandomSplitGenerator(Instances data, double percentage) {
    super(data, -1L);
    
    m_Percentage    = percentage;
    m_PreserveOrder = true;
  }

  /**
   * Returns whether randomization is enabled.
   * 
   * @return		true if to randomize
   */
  @Override
  protected boolean canRandomize() {
    return !m_PreserveOrder;
  }

  /**
   * Initializes the iterator, randomizes the data if required.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Generated = false;
  }
  
  /**
   * Returns <tt>true</tt> if the iteration has more elements. (In other
   * words, returns <tt>true</tt> if <tt>next</tt> would return an element
   * rather than throwing an exception.)
   *
   * @return 		<tt>true</tt> if the iterator has more elements.
   */
  @Override
  protected boolean checkNext() {
    return !m_Generated;
  }

  /**
   * Creates the next result.
   * 
   * @return		the next result
   */
  @Override
  protected WekaTrainTestSetContainer createNext() {
    WekaTrainTestSetContainer	result;
    Instances			trainSet;
    Instances			testSet;
    int 			trainSize;
    int 			testSize;
    
    m_Generated = true;

    trainSize = (int) Math.round((double) m_Data.numInstances() * m_Percentage);
    testSize  = m_Data.numInstances() - trainSize;
    trainSet  = new Instances(m_Data, 0, trainSize);
    testSet   = new Instances(m_Data, trainSize, testSize);

    result = new WekaTrainTestSetContainer(
      trainSet, testSet, m_Seed,
      null, null,
      m_OriginalIndices.subList(0, trainSize).toArray(),
      m_OriginalIndices.subList(trainSize, m_OriginalIndices.size()).toArray());
    
    return result;
  }
  
  /**
   * Returns a short description of the generator.
   * 
   * @return		a short description
   */
  @Override
  public String toString() {
    return super.toString() + ", percentage=" + m_Percentage;
  }
}
