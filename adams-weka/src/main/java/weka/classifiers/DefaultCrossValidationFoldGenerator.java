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
package weka.classifiers;

import adams.data.binning.Binnable;
import adams.data.binning.BinnableInstances;
import adams.data.splitgenerator.generic.crossvalidation.CrossValidationGenerator;
import adams.data.splitgenerator.generic.crossvalidation.FoldPair;
import adams.data.splitgenerator.generic.randomization.DefaultRandomization;
import adams.data.splitgenerator.generic.stratification.DefaultStratification;
import adams.flow.container.WekaTrainTestSetContainer;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.InstancesView;

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
  implements CrossValidationFoldGenerator {

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

  /** the template for the relation name. */
  protected String m_RelationName;

  /** whether to randomize the data. */
  protected boolean m_Randomize;

  /** the underlying scheme for generating the folds. */
  protected transient CrossValidationGenerator m_Generator;

  /** the temporary pairs. */
  protected transient List<FoldPair<Binnable<Instance>>> m_FoldPairs;

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
  public DefaultCrossValidationFoldGenerator(Instances data, int numFolds, long seed, boolean stratify) {
    this(data, numFolds, seed, true, stratify, null);
  }

  /**
   * Initializes the generator.
   *
   * @param data	the full dataset
   * @param numFolds	the number of folds, leave-one-out if less than 2
   * @param seed	the seed value
   * @param randomize 	whether to randomize the data
   * @param stratify	whether to perform stratified CV
   * @param relName	the relation name template, use null to ignore
   */
  public DefaultCrossValidationFoldGenerator(Instances data, int numFolds, long seed, boolean randomize, boolean stratify, String relName) {
    super();
    setData(data);
    setSeed(seed);
    setNumFolds(numFolds);
    setRelationName(relName);
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
      "relation-name", "relationName",
      CrossValidationHelper.PLACEHOLDER_ORIGINAL);

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
  public void setData(Instances value) {
    super.setData(value);
    if (m_Data != null) {
      if (getStratify() && (m_Data.classIndex() == -1))
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
   * Sets the template for the relation name.
   *
   * @param value	the template
   */
  public void setRelationName(String value) {
    m_RelationName = value;
    reset();
  }

  /**
   * Returns the relation name template.
   *
   * @return		the template
   */
  public String getRelationName() {
    return m_RelationName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String relationNameTipText() {
    return CrossValidationHelper.relationNameTemplateTipText();
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
    if (m_Data == null)
      throw new IllegalStateException("No data provided!");

    if (m_NumFolds < 2)
      m_ActualNumFolds = m_Data.numInstances();
    else
      m_ActualNumFolds = m_NumFolds;

    if (m_Data.numInstances() < m_ActualNumFolds)
      throw new IllegalArgumentException(
	  "Cannot have less data than folds: "
	      + "required=" + m_ActualNumFolds + ", provided=" + m_Data.numInstances());

    m_Generator = new CrossValidationGenerator();
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
    if (m_Stratify && m_Data.classAttribute().isNominal() && (m_ActualNumFolds < m_Data.numInstances())) {
      DefaultStratification strat = new DefaultStratification();
      strat.setLoggingLevel(m_LoggingLevel);
      m_Generator.setStratification(strat);
    }
    else {
      adams.data.splitgenerator.generic.stratification.PassThrough strat = new adams.data.splitgenerator.generic.stratification.PassThrough();
      strat.setLoggingLevel(m_LoggingLevel);
      m_Generator.setStratification(strat);
    }

    if ((m_RelationName == null) || m_RelationName.isEmpty())
      m_RelationName = CrossValidationHelper.PLACEHOLDER_ORIGINAL;
  }

  /**
   * Returns the next element in the iteration.
   *
   * @return 				the next element in the iteration.
   * @throws NoSuchElementException 	iteration has no more elements.
   */
  @Override
  protected WekaTrainTestSetContainer createNext() {
    WekaTrainTestSetContainer		result;
    List<Binnable<Instance>>  		binnedData;
    FoldPair<Binnable<Instance>> 	foldPair;
    Instances 				train;
    Instances 				test;
    int[]				trainRows;
    int[]				testRows;


    if (m_CurrentFold > m_ActualNumFolds)
      throw new NoSuchElementException("No more folds available!");

    // generate pairs
    if (m_FoldPairs == null) {
      try {
	binnedData = BinnableInstances.toBinnableUsingClass(m_Data);
      }
      catch (Exception e) {
	throw new IllegalStateException("Failed to create binnable Instances!", e);
      }

      m_FoldPairs = m_Generator.generate(binnedData);

      m_OriginalIndices = new TIntArrayList();
      for (FoldPair<Binnable<Instance>> pair : m_FoldPairs)
	m_OriginalIndices.addAll(pair.getTest().getOriginalIndices());
    }

    foldPair = m_FoldPairs.get(m_CurrentFold - 1);

    trainRows = foldPair.getTrain().getOriginalIndices().toArray();
    testRows  = foldPair.getTest().getOriginalIndices().toArray();

    // generate fold pair
    if (m_UseViews) {
      train = new InstancesView(m_Data, trainRows);
      test = new InstancesView(m_Data, testRows);
    }
    else {
      train = BinnableInstances.toInstances(foldPair.getTrain().getData());
      test  = BinnableInstances.toInstances(foldPair.getTest().getData());
    }

    // rename datasets
    train.setRelationName(CrossValidationHelper.createRelationName(m_Data.relationName(), m_RelationName, m_CurrentFold, true));
    test.setRelationName(CrossValidationHelper.createRelationName(m_Data.relationName(), m_RelationName, m_CurrentFold, false));

    result = new WekaTrainTestSetContainer(
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
    return super.toString() + ", numFolds=" + m_NumFolds + ", stratify=" + m_Stratify + ", relName=" + m_RelationName;
  }
}
