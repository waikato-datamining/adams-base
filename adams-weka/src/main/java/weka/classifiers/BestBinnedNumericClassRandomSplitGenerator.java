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
 * BestBinnedNumericClassRandomSplitGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package weka.classifiers;

import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;
import adams.data.binning.BinnableInstances;
import adams.data.binning.algorithm.BinningAlgorithm;
import adams.data.binning.algorithm.ManualBinning;
import adams.data.binning.operation.Bins;
import adams.data.binning.operation.Bins.SummaryType;
import adams.data.statistics.StatUtils;
import adams.flow.container.WekaTrainTestSetContainer;
import com.github.fracpete.javautils.enumerate.Enumerated;
import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Picks the best binning algorithm from the provided ones.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BestBinnedNumericClassRandomSplitGenerator
  extends AbstractSplitGenerator
  implements weka.classifiers.RandomSplitGenerator {

  private static final long serialVersionUID = -3836027382933579890L;

  /** the percentage. */
  protected double m_Percentage;

  /** whether to preserve the order. */
  protected boolean m_PreserveOrder;

  /** the algorithms to evaluate. */
  protected BinningAlgorithm[] m_Algorithms;

  /** the number of evaluation bins. */
  protected int m_NumEvaluationBins;

  /** whether the split was generated. */
  protected boolean m_Generated;

  /** for generating class distributions. */
  protected ManualBinning m_Manual;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Picks the best binning algorithm from the provided ones.\n"
      + "In order to do this, the class distributions from generated train and test splits "
      + "are compared against the overall class distribution. For comparison, the class "
      + "values are binned using the specified number of bins, using a fixed max/min. "
      + "How well the class distributions align is determined by computing the correlation coefficient (CC). "
      + "The binning algorithm with the highest sum of CCs for train and test is then picked.";
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
      "algorithm", "algorithms",
      new BinningAlgorithm[0]);

    m_OptionManager.add(
      "num-evaluation-bins", "numEvaluationBins",
      20, 1, null);
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
   * Sets the binning algorithms to choose from.
   *
   * @param value 	the algorithms
   */
  public void setAlgorithms(BinningAlgorithm[] value) {
    m_Algorithms = value;
    reset();
  }

  /**
   * Returns the binning algorithms to choose from.
   *
   * @return 		the algorithms
   */
  public BinningAlgorithm[] getAlgorithms() {
    return m_Algorithms;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmsTipText() {
    return "The binning algorithms to pick the best one from.";
  }

  /**
   * Sets the number of bints to use during evaluation.
   *
   * @param value	the number of bins
   */
  public void setNumEvaluationBins(int value) {
    if (getOptionManager().isValid("numEvaluationBins", value)) {
      m_NumEvaluationBins = value;
      reset();
    }
  }

  /**
   * Returns the number of bins to use during evaluation.
   *
   * @return		the number of bins
   */
  public int getNumEvaluationBins() {
    return m_NumEvaluationBins;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numEvaluationBinsTipText() {
    return "The number of bins for determining the class distribution during evaluation.";
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
    if (m_Algorithms.length == 0)
      throw new IllegalStateException("No binning algorithms specified!");

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
   * Calculates the class distribution.
   *
   * @param binnable	the instances to calculate the distribution for
   * @param emptyBinValue 	the value to use for empty bins
   * @return		the distribution
   */
  protected double[] calcDistribution(List<Binnable<Instance>> binnable, double emptyBinValue) {
    double[] 			result;
    List<Bin<Instance>> 	bins;

    bins   = m_Manual.generateBins(binnable);
    result = Bins.summarizeBinnableValues(bins, SummaryType.MEAN, emptyBinValue);
    result = StatUtils.normalizeRange(result, 0.0, 1.0);

    return result;
  }

  /**
   * Creates the next result.
   *
   * @return		the next result
   */
  @Override
  protected WekaTrainTestSetContainer createNext() {
    List<Binnable<Instance>> 	binnableInst;
    AttributeStats 		stats;
    double			min;
    double			max;
    double[] 			distOverall;
    BinnedNumericClassRandomSplitGenerator	generator;
    List<double[]>		distTrain;
    List<double[]>		distTest;
    double[]			dist;
    WekaTrainTestSetContainer	cont;
    double[]			ccTrain;
    double[]			ccTest;
    double[]			ccSum;
    int				i;
    int				best;

    m_Generated = true;

    // configure binning for class distributions
    stats  = m_Data.attributeStats(m_Data.classIndex());
    min    = stats.numericStats.min;
    max    = stats.numericStats.max;
    m_Manual = new ManualBinning();
    m_Manual.setNumBins(m_NumEvaluationBins);
    m_Manual.setUseFixedMinMax(true);
    m_Manual.setManualMin(min);
    m_Manual.setManualMax(max);

    // calculate overall distribution
    try {
      binnableInst = BinnableInstances.toBinnableUsingClass(m_Data);
      distOverall  = calcDistribution(binnableInst, min);
      if (isLoggingEnabled())
        getLogger().info("Total distribution: " + Utils.arrayToString(distOverall));
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to create binnable Instances!", e);
    }

    // apply algorithms
    distTrain = new ArrayList<>();
    distTest  = new ArrayList<>();
    for (Enumerated<BinningAlgorithm> algorithm: enumerate(m_Algorithms)) {
      // generate split
      generator = new BinnedNumericClassRandomSplitGenerator();
      generator.setAlgorithm(ObjectCopyHelper.copyObject(algorithm.value));
      generator.setPercentage(m_Percentage);
      generator.setPreserveOrder(m_PreserveOrder);
      generator.setData(m_Data);
      cont = generator.next();

      // train
      try {
	binnableInst = BinnableInstances.toBinnableUsingClass(cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN, Instances.class));
	dist         = calcDistribution(binnableInst, min);
	distTrain.add(dist);
	if (isLoggingEnabled())
	  getLogger().info("train distribution #" + algorithm.index + ": " + Utils.arrayToString(dist));
      }
      catch (Exception e) {
	throw new IllegalStateException("Failed to create binnable Instances (train #" + algorithm.index + ")!", e);
      }

      // test
      try {
	binnableInst = BinnableInstances.toBinnableUsingClass(cont.getValue(WekaTrainTestSetContainer.VALUE_TEST, Instances.class));
	dist         = calcDistribution(binnableInst, min);
	distTest.add(dist);
	if (isLoggingEnabled())
	  getLogger().info("test distribution #" + algorithm.index + ": " + Utils.arrayToString(dist));
      }
      catch (Exception e) {
	throw new IllegalStateException("Failed to create binnable Instances (test #" + algorithm.index + ")!", e);
      }
    }

    // evaluate distributions
    ccTrain = new double[distTrain.size()];
    ccTest  = new double[distTest.size()];
    ccSum   = new double[distTrain.size()];
    for (i = 0; i < ccTrain.length; i++) {
      ccTrain[i] = StatUtils.correlationCoefficient(distOverall, distTrain.get(i));
      ccTest[i]  = StatUtils.correlationCoefficient(distOverall, distTest.get(i));
      ccSum[i]   = ccTrain[i] + ccTest[i];
    }
    if (isLoggingEnabled()) {
      getLogger().info("CC train: " + Utils.arrayToString(ccTrain));
      getLogger().info("CC test: " + Utils.arrayToString(ccTest));
      getLogger().info("CC sum: " + Utils.arrayToString(ccSum));
    }

    // generate split with best
    best = StatUtils.maxIndex(ccSum);
    if (isLoggingEnabled())
      getLogger().info("Best: #" + best + ", " + OptionUtils.getCommandLine(m_Algorithms[best]));
    generator = new BinnedNumericClassRandomSplitGenerator();
    generator.setAlgorithm(ObjectCopyHelper.copyObject(m_Algorithms[best]));
    generator.setPercentage(m_Percentage);
    generator.setPreserveOrder(m_PreserveOrder);
    generator.setData(m_Data);

    return generator.next();
  }
}
