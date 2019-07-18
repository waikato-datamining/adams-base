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
 * BinnedNumericClassRandomSplitGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;

import adams.core.Utils;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;
import adams.data.binning.BinnableInstances;
import adams.data.binning.algorithm.AbstractBinningAlgorithm;
import adams.data.binning.algorithm.ManualBinning;
import adams.data.binning.operation.Bins;
import adams.data.binning.operation.Randomize;
import adams.data.binning.operation.Wrapping;
import adams.data.binning.postprocessing.MinBinSize;
import adams.flow.container.WekaTrainTestSetContainer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.InstancesView;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates random splits of datasets with numeric classes using a binning algorithm.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BinnedNumericClassRandomSplitGenerator
  extends AbstractSplitGenerator
  implements weka.classifiers.RandomSplitGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -4813006743965500489L;

  /** the percentage. */
  protected double m_Percentage;

  /** whether to preserve the order. */
  protected boolean m_PreserveOrder;

  /** the binning algorithm. */
  protected AbstractBinningAlgorithm m_Algorithm;

  /** whether the split was generated. */
  protected boolean m_Generated;

  /**
   * Initializes the generator.
   */
  public BinnedNumericClassRandomSplitGenerator() {
    super();
  }

  /**
   * Initializes the generator. Does not preserve the order.
   *
   * @param data	the dataset to split
   * @param seed	the seed value to use for randomization
   * @param percentage	the percentage of the training set (0-1)
   */
  public BinnedNumericClassRandomSplitGenerator(Instances data, long seed, double percentage) {
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
  public BinnedNumericClassRandomSplitGenerator(Instances data, double percentage) {
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
  public BinnedNumericClassRandomSplitGenerator(Instances data, long seed, double percentage, boolean preserveOrder) {
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
    return "Generates random splits of datasets with numeric classes. Uses a binning algorithm to obtain similar distribution in splits. Order can be preserved.";
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

    m_OptionManager.add(
      "algorithm", "algorithm",
      new ManualBinning());
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
   * Sets the binning algorithm.
   *
   * @param value 	the algorithm
   */
  public void setAlgorithm(AbstractBinningAlgorithm value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the binning algorithm.
   *
   * @return 		the algorithm
   */
  public AbstractBinningAlgorithm getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The binning algorithm to apply to the data.";
  }

  /**
   * Sets the original data.
   *
   * @param value	the data
   */
  public void setData(Instances value) {
    super.setData(value);
    if (m_Data != null) {
      if (m_Data.classIndex() == -1)
        throw new IllegalArgumentException("No class attribute set!");
      if (!m_Data.classAttribute().isNumeric())
        throw new IllegalArgumentException("Class attribute is not numeric!");
    }
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
   * Initializes the iterator.
   */
  protected void doInitializeIterator() {
    if (m_Data == null)
      throw new IllegalStateException("No data available!");

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
    List<Binnable<Instance>> 		binnableInst;
    List<Bin<Instance>> 		binInst;
    MinBinSize				minBinSize;
    Instances				trainSet;
    Instances				testSet;
    int[] 				trainRows;
    int[]				testRows;
    List<Binnable<Instance>> 		binnableTrain;
    List<Binnable<Instance>> 		binnableTest;
    int[] 				maxPerBin;
    int[] 				trainPerBin;
    int					i;
    int					n;
    int					maxTotal;
    int 				trainTotal;
    boolean				added;

    m_Generated = true;

    try {
      binnableInst = BinnableInstances.toBinnableUsingIndex(m_Data);
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to create binnable Instances!", e);
    }

    Wrapping.addTmpIndex(binnableInst);

    if (canRandomize())
      Randomize.randomizeData(binnableInst, m_Seed);

    binInst = m_Algorithm.generateBins(binnableInst);
    if (isLoggingEnabled())
      getLogger().info("Bins: " + Utils.arrayToString(Bins.binSizes(binInst)));

    // at least two instances in each bin
    minBinSize = new MinBinSize();
    minBinSize.setMinSize(2);
    binInst = minBinSize.postProcessBins(binInst);
    if (isLoggingEnabled())
      getLogger().info("Bins after postprocessing: " + Utils.arrayToString(Bins.binSizes(binInst)));

    // calculate max instances to retrieve from each bin (for training data)
    maxTotal  = (int) Math.round(m_Data.size() * m_Percentage);
    maxPerBin = new int[binInst.size()];
    for (i = 0; i < binInst.size(); i++)
      maxPerBin[i] = Math.min(binInst.get(i).size() - 1, (int) Math.round(binInst.get(i).size() * m_Percentage));
    if (isLoggingEnabled()) {
      getLogger().info("max total: " + maxTotal);
      getLogger().info("max per bin: " + Utils.arrayToString(maxPerBin));
    }

    // train
    binnableTrain = new ArrayList<>();
    trainPerBin   = new int[maxPerBin.length];
    trainTotal    = 0;
    while (trainTotal < maxTotal) {
      added = false;
      for (i = 0; i < trainPerBin.length; i++) {
        if ((trainPerBin[i] < maxPerBin[i]) && (trainTotal < maxTotal)) {
          binnableTrain.add(binInst.get(i).get().get(trainPerBin[i]));
          trainTotal++;
          trainPerBin[i]++;
          added = true;
	}
      }
      if (!added)
        break;
    }
    if (isLoggingEnabled()) {
      getLogger().info("train total: " + trainTotal);
      getLogger().info("train per bin: " + Utils.arrayToString(trainPerBin));
    }

    // test
    binnableTest = new ArrayList<>();
    for (i = 0; i < trainPerBin.length; i++) {
      for (n = trainPerBin[i]; n < binInst.get(i).size(); n++) {
	binnableTest.add(binInst.get(i).get().get(n));
      }
    }

    trainRows = Wrapping.getTmpIndices(binnableTrain).toArray();
    testRows  = Wrapping.getTmpIndices(binnableTest).toArray();
    if (m_UseViews) {
      trainSet = new InstancesView(m_Data, trainRows);
      testSet  = new InstancesView(m_Data, testRows);
    }
    else {
      trainSet = BinnableInstances.toInstances(binnableTrain);
      testSet  = BinnableInstances.toInstances(binnableTest);
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
