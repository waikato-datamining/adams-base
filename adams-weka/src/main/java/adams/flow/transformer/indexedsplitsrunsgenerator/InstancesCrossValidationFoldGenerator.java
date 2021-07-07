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
 * InstancesCrossValidationFoldGenerator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsgenerator;

import adams.core.MessageCollection;
import adams.core.OptionalRandomizable;
import adams.core.QuickInfoHelper;
import adams.data.binning.Binnable;
import adams.data.binning.BinnableInstances;
import adams.data.indexedsplits.IndexedSplit;
import adams.data.indexedsplits.IndexedSplits;
import adams.data.indexedsplits.IndexedSplitsRun;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.indexedsplits.SplitIndices;
import adams.data.splitgenerator.generic.crossvalidation.CrossValidationGenerator;
import adams.data.splitgenerator.generic.crossvalidation.FoldPair;
import adams.data.splitgenerator.generic.randomization.DefaultRandomization;
import adams.data.splitgenerator.generic.stratification.DefaultStratification;
import weka.core.Instance;
import weka.core.Instances;

import java.util.List;

/**
 * Split generator that generates folds for cross-validation for Instances objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InstancesCrossValidationFoldGenerator
  extends AbstractInstancesIndexedSplitsRunsGenerator
  implements OptionalRandomizable {

  private static final long serialVersionUID = -845552507613381226L;

  /** the number of folds. */
  protected int m_NumFolds;

  /** whether to randomize the data. */
  protected boolean m_Randomize;

  /** the seed value. */
  protected long m_Seed;

  /** whether to stratify the data (in case of nominal class). */
  protected boolean m_Stratify;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Split generator that generates folds for cross-validation for Instances objects.";
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
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "randomize", "randomize",
      true);

    m_OptionManager.add(
      "stratify", "stratify",
      true);
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
   * Sets whether to randomize the data.
   *
   * @param value	true if to randomize the data
   */
  @Override
  public void setRandomize(boolean value) {
    m_Randomize = value;
    reset();
  }

  /**
   * Returns whether to randomize the data.
   *
   * @return		true if to randomize the data
   */
  @Override
  public boolean getRandomize() {
    return m_Randomize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String randomizeTipText() {
    return "If enabled, the data is randomized first.";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  @Override
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return		the seed
   */
  @Override
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String seedTipText() {
    return "The seed value for the random number generator.";
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
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "numFolds", m_NumFolds, ", folds: ");
    if (m_Randomize)
      result += QuickInfoHelper.toString(this, "seed", m_Seed, ", seed: ");
    result += QuickInfoHelper.toString(this, "stratify", (m_Stratify ? "stratified" : "not stratified"), ", ");

    return result;
  }

  /**
   * Checks whether the data can be processed.
   *
   * @param data	the data to check
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String check(Object data) {
    String	result;
    Instances	inst;

    result = super.check(data);

    if (result == null) {
      inst = (Instances) data;
      if (inst.classIndex() == -1)
        result = "No class attribute defined!";
    }

    return result;
  }

  /**
   * Generates the indexed splits.
   *
   * @param data   the data to use for generating the splits
   * @param errors for storing any errors occurring during processing
   * @return the splits or null in case of error
   */
  @Override
  protected IndexedSplitsRuns doGenerate(Object data, MessageCollection errors) {
    IndexedSplitsRuns			result;
    IndexedSplitsRun			indexedSplitsRun;
    IndexedSplits			indexedSplits;
    IndexedSplit			indexedSplit;
    Instances 				instances;
    CrossValidationGenerator 		generator;
    int 				actualNumFolds;
    int[]				trainRows;
    int[]				testRows;
    List<Binnable<Instance>> 		binnableInst;
    FoldPair<Binnable<Instance>> 	foldPair;
    List<FoldPair<Binnable<Instance>>> 	foldPairs;
    int					fold;

    instances = (Instances) data;

    if (m_NumFolds < 2)
      actualNumFolds = instances.numInstances();
    else
      actualNumFolds = m_NumFolds;

    if (instances.numInstances() < actualNumFolds) {
      errors.add(
	"Cannot have less data than folds: "
	  + "required=" + actualNumFolds + ", provided=" + instances.numInstances());
      return null;
    }

    generator = new CrossValidationGenerator();
    generator.setNumFolds(m_NumFolds);
    if (m_Randomize) {
      DefaultRandomization rand = new DefaultRandomization();
      rand.setSeed(m_Seed);
      rand.setLoggingLevel(m_LoggingLevel);
      generator.setRandomization(rand);
    }
    else {
      adams.data.splitgenerator.generic.randomization.PassThrough rand = new adams.data.splitgenerator.generic.randomization.PassThrough();
      rand.setLoggingLevel(m_LoggingLevel);
      generator.setRandomization(rand);
    }
    if (m_Stratify && instances.classAttribute().isNominal() && (actualNumFolds < instances.numInstances())) {
      DefaultStratification strat = new DefaultStratification();
      strat.setLoggingLevel(m_LoggingLevel);
      generator.setStratification(strat);
    }
    else {
      adams.data.splitgenerator.generic.stratification.PassThrough strat = new adams.data.splitgenerator.generic.stratification.PassThrough();
      strat.setLoggingLevel(m_LoggingLevel);
      generator.setStratification(strat);
    }

    try {
      binnableInst = BinnableInstances.toBinnableUsingClass(instances);
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to create binnable Instances!", e);
    }

    foldPairs        = generator.generate(binnableInst);
    result           = new IndexedSplitsRuns();
    indexedSplits    = new IndexedSplits();
    indexedSplitsRun = new IndexedSplitsRun(0, indexedSplits);
    result.add(indexedSplitsRun);
    for (fold = 0; fold < actualNumFolds; fold++) {
      foldPair = foldPairs.get(fold);
      trainRows = foldPair.getTrain().getOriginalIndices().toArray();
      testRows  = foldPair.getTest().getOriginalIndices().toArray();

      indexedSplit = new IndexedSplit(fold);
      indexedSplit.add(new SplitIndices("train", trainRows));
      indexedSplit.add(new SplitIndices("test", testRows));
      indexedSplits.add(indexedSplit);
    }

    return result;
  }
}
