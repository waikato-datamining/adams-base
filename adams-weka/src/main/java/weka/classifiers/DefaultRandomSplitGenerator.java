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
 * DefaultRandomSplitGenerator.java
 * Copyright (C) 2012-2019 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;

import adams.data.binning.Binnable;
import adams.data.binning.BinnableInstances;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.ml.splitgenerator.generic.randomization.DefaultRandomization;
import adams.ml.splitgenerator.generic.randomization.PassThrough;
import adams.ml.splitgenerator.generic.randomsplit.RandomSplitGenerator;
import adams.ml.splitgenerator.generic.randomsplit.SplitPair;
import adams.ml.splitgenerator.generic.splitter.DefaultSplitter;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.InstancesView;

import java.util.List;

/**
 * Generates random splits of datasets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DefaultRandomSplitGenerator
  extends AbstractSplitGenerator
  implements weka.classifiers.RandomSplitGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -4813006743965500489L;

  /** the percentage. */
  protected double m_Percentage;

  /** whether to preserve the order. */
  protected boolean m_PreserveOrder;

  /** whether the split was generated. */
  protected boolean m_Generated;

  /** the underlying scheme for generating the split. */
  protected RandomSplitGenerator m_Generator;

  /**
   * Initializes the generator.
   */
  public DefaultRandomSplitGenerator() {
    super();
  }

  /**
   * Initializes the generator. Does not preserve the order.
   *
   * @param data	the dataset to split
   * @param seed	the seed value to use for randomization
   * @param percentage	the percentage of the training set (0-1)
   */
  public DefaultRandomSplitGenerator(Instances data, long seed, double percentage) {
    super();
    setData(data);
    setSeed(seed);
    setPercentage(percentage);
    setPreserveOrder(false);
  }

  /**
   * Initializes the generator. Preserves the order.
   *
   * @param data	the dataset to split
   * @param percentage	the percentage of the training set (0-1)
   */
  public DefaultRandomSplitGenerator(Instances data, double percentage) {
    super();
    setData(data);
    setSeed(-1L);
    setPercentage(percentage);
    setPreserveOrder(true);
  }

  /**
   * Initializes the generator. Does not preserve the order.
   *
   * @param data	the dataset to split
   * @param seed	the seed value to use for randomization
   * @param percentage	the percentage of the training set (0-1)
   * @param preserveOrder 	whether to preserve the order
   */
  public DefaultRandomSplitGenerator(Instances data, long seed, double percentage, boolean preserveOrder) {
    super();
    setData(data);
    setSeed(seed);
    setPercentage(percentage);
    setPreserveOrder(preserveOrder);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs a percentage split, either randomized or with the order preserved.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "percentage", "percentage",
      0.66, 0.0, 1.0);

    m_OptionManager.add(
      "preserve-order", "preserveOrder",
      false);
  }

  /**
   * Sets the split percentage.
   *
   * @param value	the percentage (0-1)
   */
  public void setPercentage(double value) {
    if (getOptionManager().isValid("percentage", value)) {
      m_Percentage = value;
      reset();
    }
  }

  /**
   * Returns the split percentage.
   *
   * @return		the percentage (0-1)
   */
  public double getPercentage() {
    return m_Percentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String percentageTipText() {
    return "The percentage to use for training (0-1).";
  }

  /**
   * Sets whether to preserve the order.
   *
   * @param value	true if to preserve order
   */
  public void setPreserveOrder(boolean value) {
    m_PreserveOrder = value;
    reset();
  }

  /**
   * Returns whether to preserve the order.
   *
   * @return		true if to preserve order
   */
  public boolean getPreserveOrder() {
    return m_PreserveOrder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String preserveOrderTipText() {
    return "If enabled, the order in the data is preserved in the split.";
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
   *
   * @see		#canRandomize()
   */
  protected void doInitializeIterator() {
    if (m_Data == null)
      throw new IllegalStateException("No data available!");

    m_Generator = new RandomSplitGenerator();
    if (canRandomize()) {
      DefaultRandomization rand = new DefaultRandomization();
      rand.setSeed(m_Seed);
      m_Generator.setRandomization(rand);
    }
    else {
      PassThrough rand = new PassThrough();
      m_Generator.setRandomization(rand);
    }
    DefaultSplitter splitter = new DefaultSplitter();
    splitter.setPercentage(m_Percentage);
    m_Generator.setSplitter(splitter);

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
    WekaTrainTestSetContainer		result;
    List<Binnable<Instance>> 		binnedData;
    SplitPair<Binnable<Instance>> 	splitPair;
    Instances				trainSet;
    Instances				testSet;
    int[]				trainRows;
    int[]				testRows;

    m_Generated = true;

    try {
      binnedData = BinnableInstances.toBinnableUsingIndex(m_Data);
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to create binnable Instances!", e);
    }
    splitPair = m_Generator.generate(binnedData);

    trainRows = splitPair.getTrain().getOriginalIndices().toArray();
    testRows  = splitPair.getTest().getOriginalIndices().toArray();
    if (m_UseViews) {
      trainSet = new InstancesView(m_Data, trainRows);
      testSet  = new InstancesView(m_Data, testRows);
    }
    else {
      trainSet = BinnableInstances.toInstances(splitPair.getTrain().getData());
      testSet  = BinnableInstances.toInstances(splitPair.getTest().getData());
    }

    result = new WekaTrainTestSetContainer(
      trainSet, testSet, m_Seed, null, null, trainRows, testRows);

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
