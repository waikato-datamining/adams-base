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
 * DefaultCrossValidationFoldGenerator.java
 * Copyright (C) 2012-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.ml.evaluation;

import adams.data.binning.Binnable;
import adams.data.binning.BinnableDataset;
import adams.data.splitgenerator.CrossValidationFoldGenerator;
import adams.data.splitgenerator.generic.crossvalidation.FoldPair;
import adams.data.splitgenerator.generic.randomization.DefaultRandomization;
import adams.data.splitgenerator.generic.stratification.DefaultStratification;
import adams.data.spreadsheet.DataRow;
import adams.flow.container.TrainTestSetContainer;
import adams.ml.data.Dataset;
import adams.ml.data.DatasetView;
import gnu.trove.list.array.TIntArrayList;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Helper class for generating cross-validation folds.
 * <br><br>
 * The template for the relation name accepts the following placeholders:
 * @ = original relation name, $T = type (train/test), $N = current fold number
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DefaultCrossValidationFoldGenerator
  extends AbstractSplitGenerator
  implements CrossValidationFoldGenerator<Dataset,TrainTestSetContainer> {

  /** for serialization. */
  private static final long serialVersionUID = -8387205583429213079L;

  /** the number of folds. */
  protected int m_NumFolds;

  /** the actual number of folds. */
  protected int m_ActualNumFolds;

  /** whether to stratify the data (in case of nominal class). */
  protected boolean m_Stratify;

  /** the current fold. */
  protected transient int m_CurrentFold;

  /** whether to randomize the data. */
  protected boolean m_Randomize;

  /** the underlying scheme for generating the folds. */
  protected transient adams.data.splitgenerator.generic.crossvalidation.CrossValidationGenerator m_Generator;

  /** the temporary pairs. */
  protected transient List<FoldPair<Binnable<DataRow>>> m_FoldPairs;

  /**
   * Initializes the generator.
   */
  public DefaultCrossValidationFoldGenerator() {
    super();
  }

  /**
   * Initializes the generator.
   *
   * @param data	the full dataset
   * @param numFolds	the number of folds, leave-one-out if less than 2
   * @param seed	the seed for randomization
   * @param stratify	whether to perform stratified CV
   */
  public DefaultCrossValidationFoldGenerator(Dataset data, int numFolds, long seed, boolean stratify) {
    this(data, numFolds, seed, true, stratify);
  }

  /**
   * Initializes the generator.
   *
   * @param data	the full dataset
   * @param numFolds	the number of folds, leave-one-out if less than 2
   * @param seed	the seed value
   * @param randomize 	whether to randomize the data
   * @param stratify	whether to perform stratified CV
   */
  public DefaultCrossValidationFoldGenerator(Dataset data, int numFolds, long seed, boolean randomize, boolean stratify) {
    super();
    setData(data);
    setSeed(seed);
    setNumFolds(numFolds);
    setStratify(stratify);
    setRandomize(randomize);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates cross-validation fold pairs. Leave-one-out is performed when specified folds <2.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-folds", "numFolds",
      10);

    m_OptionManager.add(
      "randomize", "randomize",
      true);

    m_OptionManager.add(
      "stratify", "stratify",
      true);
  }

  /**
   * Resets the generator.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CurrentFold    = 1;
    m_ActualNumFolds = -1;
    m_FoldPairs      = null;
  }

  /**
   * Sets the original data.
   *
   * @param value	the data
   */
  public void setData(Dataset value) {
    super.setData(value);
    if (m_Data != null) {
      if (getStratify() && (m_Data.getClassAttributeIndices().length == 0))
        throw new IllegalArgumentException("No class attribute set!");
    }
  }

  /**
   * Sets the number of folds to use.
   *
   * @param value	the number of folds, less than 2 for LOO
   */
  public void setNumFolds(int value) {
    m_NumFolds = value;
    reset();
  }

  /**
   * Returns the number of folds.
   *
   * @return		the number of folds
   */
  public int getNumFolds() {
    return m_NumFolds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numFoldsTipText() {
    return "The number of folds; use <2 for leave one out (LOO).";
  }

  /**
   * Returns the actual number of folds used (eg when using LOO).
   *
   * @return		the actual number of folds, -1 if not yet calculated
   * @see		#initializeIterator()
   */
  public int getActualNumFolds() {
    return m_ActualNumFolds;
  }

  /**
   * Sets whether to randomize the data.
   *
   * @param value	true if to randomize the data
   */
  public void setRandomize(boolean value) {
    m_Randomize = value;
    reset();
  }

  /**
   * Returns whether to randomize the data.
   *
   * @return		true if to randomize the data
   */
  public boolean getRandomize() {
    return m_Randomize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String randomizeTipText() {
    return "If enabled, the data is randomized first.";
  }

  /**
   * Sets whether to stratify the data (nominal class).
   *
   * @param value	whether to stratify the data (nominal class)
   */
  public void setStratify(boolean value) {
    m_Stratify = value;
    reset();
  }

  /**
   * Returns whether to stratify the data (in case of nominal class).
   *
   * @return		true if to stratify
   */
  public boolean getStratify() {
    return m_Stratify;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stratifyTipText() {
    return "If enabled, the folds get stratified in case of a nominal class attribute.";
  }

  /**
   * Returns whether randomization is enabled.
   *
   * @return		true if to randomize
   */
  @Override
  protected boolean canRandomize() {
    return m_Randomize;
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
    return (m_CurrentFold <= m_ActualNumFolds);
  }

  /**
   * Initializes the iterator, randomizes the data if required.
   */
  @Override
  protected void doInitializeIterator() {
    boolean	classIsNominal;

    if (m_Data == null)
      throw new IllegalStateException("No data provided!");

    if (m_NumFolds < 2)
      m_ActualNumFolds = m_Data.getRowCount();
    else
      m_ActualNumFolds = m_NumFolds;

    if (m_Data.getRowCount() < m_ActualNumFolds)
      throw new IllegalArgumentException(
	  "Cannot have less data than folds: "
	      + "required=" + m_ActualNumFolds + ", provided=" + m_Data.getRowCount());

    m_Generator = new adams.data.splitgenerator.generic.crossvalidation.CrossValidationGenerator();
    m_Generator.setNumFolds(m_NumFolds);
    if (canRandomize()) {
      DefaultRandomization rand = new DefaultRandomization();
      rand.setSeed(m_Seed);
      rand.setLoggingLevel(m_LoggingLevel);
      m_Generator.setRandomization(rand);
    }
    else {
      adams.data.splitgenerator.generic.randomization.PassThrough rand = new adams.data.splitgenerator.generic.randomization.PassThrough();
      rand.setLoggingLevel(m_LoggingLevel);
      m_Generator.setRandomization(rand);
    }
    classIsNominal = !m_Data.isNumeric(m_Data.getClassAttributeIndices()[0]);
    if (m_Stratify && classIsNominal && (m_ActualNumFolds < m_Data.getRowCount())) {
      DefaultStratification strat = new DefaultStratification();
      strat.setLoggingLevel(m_LoggingLevel);
      m_Generator.setStratification(strat);
    }
    else {
      adams.data.splitgenerator.generic.stratification.PassThrough strat = new adams.data.splitgenerator.generic.stratification.PassThrough();
      strat.setLoggingLevel(m_LoggingLevel);
      m_Generator.setStratification(strat);
    }
  }

  /**
   * Returns the next element in the iteration.
   *
   * @return 				the next element in the iteration.
   * @throws NoSuchElementException 	iteration has no more elements.
   */
  @Override
  protected TrainTestSetContainer createNext() {
    TrainTestSetContainer		result;
    List<Binnable<DataRow>>  		binnedData;
    FoldPair<Binnable<DataRow>> 	foldPair;
    Dataset 				train;
    Dataset 				test;
    int[]				trainRows;
    int[]				testRows;


    if (m_CurrentFold > m_ActualNumFolds)
      throw new NoSuchElementException("No more folds available!");

    // generate pairs
    if (m_FoldPairs == null) {
      try {
	binnedData = BinnableDataset.toBinnableUsingClass(m_Data, m_Data.getClassAttributeIndices()[0]);
      }
      catch (Exception e) {
	throw new IllegalStateException("Failed to create binnable Dataset!", e);
      }

      m_FoldPairs = m_Generator.generate(binnedData);

      m_OriginalIndices = new TIntArrayList();
      for (FoldPair<Binnable<DataRow>> pair : m_FoldPairs)
	m_OriginalIndices.addAll(pair.getTest().getOriginalIndices());
    }

    foldPair = m_FoldPairs.get(m_CurrentFold - 1);

    trainRows = foldPair.getTrain().getOriginalIndices().toArray();
    testRows  = foldPair.getTest().getOriginalIndices().toArray();

    // generate fold pair
    if (m_UseViews) {
      train = new DatasetView(m_Data, trainRows, null);
      test = new DatasetView(m_Data, testRows, null);
    }
    else {
      train = BinnableDataset.toDataset(foldPair.getTrain().getData());
      test  = BinnableDataset.toDataset(foldPair.getTest().getData());
    }

    result = new TrainTestSetContainer(
      train, test, m_Seed, m_CurrentFold, m_ActualNumFolds, trainRows, testRows);
    m_CurrentFold++;

    if (m_CurrentFold > m_ActualNumFolds)
      m_FoldPairs = null;

    return result;
  }

  /**
   * Returns the cross-validation indices.
   *
   * @return		the indices
   */
  public int[] crossValidationIndices() {
    return m_OriginalIndices.toArray();
  }

  /**
   * Returns a short description of the generator.
   *
   * @return		a short description
   */
  @Override
  public String toString() {
    return super.toString() + ", numFolds=" + m_NumFolds + ", stratify=" + m_Stratify;
  }
}
