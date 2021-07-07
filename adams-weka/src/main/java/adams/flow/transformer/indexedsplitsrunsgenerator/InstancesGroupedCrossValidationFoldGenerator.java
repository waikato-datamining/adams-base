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
import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.core.base.BaseRegExp;
import adams.data.binning.Binnable;
import adams.data.binning.BinnableGroup;
import adams.data.binning.BinnableInstances;
import adams.data.binning.BinnableInstances.GroupedClassValueBinValueExtractor;
import adams.data.binning.BinnableInstances.StringAttributeGroupExtractor;
import adams.data.binning.operation.Grouping;
import adams.data.binning.operation.Wrapping;
import adams.data.indexedsplits.IndexedSplit;
import adams.data.indexedsplits.IndexedSplits;
import adams.data.indexedsplits.IndexedSplitsRun;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.indexedsplits.SplitIndices;
import adams.data.splitgenerator.generic.core.Subset;
import adams.data.splitgenerator.generic.crossvalidation.CrossValidationGenerator;
import adams.data.splitgenerator.generic.crossvalidation.FoldPair;
import adams.data.splitgenerator.generic.randomization.DefaultRandomization;
import adams.data.splitgenerator.generic.stratification.DefaultStratification;
import adams.data.weka.WekaAttributeIndex;
import com.github.fracpete.javautils.struct.Struct2;
import gnu.trove.list.TIntList;
import weka.core.Instance;
import weka.core.Instances;

import java.util.List;

/**
 * Split generator that generates folds for cross-validation for Instances objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InstancesGroupedCrossValidationFoldGenerator
  extends AbstractInstancesIndexedSplitsRunsGenerator
  implements Randomizable {

  private static final long serialVersionUID = -845552507613381226L;

  /** the number of folds. */
  protected int m_NumFolds;

  /** whether to randomize the data. */
  protected boolean m_Randomize;

  /** the seed value. */
  protected long m_Seed;

  /** whether to stratify the data (in case of nominal class). */
  protected boolean m_Stratify;

  /** the index to use for grouping. */
  protected WekaAttributeIndex m_Index;

  /** the regular expression for the nominal/string attribute. */
  protected BaseRegExp m_RegExp;

  /** the group expression. */
  protected String m_Group;

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

    m_OptionManager.add(
      "index", "index",
      new WekaAttributeIndex(WekaAttributeIndex.FIRST));

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "group", "group",
      "$0");
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
   * Sets the attribute index to use for grouping.
   *
   * @param value	the index
   */
  public void setIndex(WekaAttributeIndex value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the attribute index to use for grouping.
   *
   * @return		the index
   */
  public WekaAttributeIndex getIndex() {
    return m_Index;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indexTipText() {
    return "The index of the attribute to determine the group from.";
  }

  /**
   * Sets the regular expression for identifying the group (eg '^(.*)-([0-9]+)-(.*)$').
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression for identifying the group (eg '^(.*)-([0-9]+)-(.*)$').
   *
   * @return		the expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression for identifying the group (eg '^(.*)-([0-9]+)-(.*)$').";
  }

  /**
   * Sets the replacement string to use as group (eg '$2').
   *
   * @param value	the group
   */
  public void setGroup(String value) {
    m_Group = value;
    reset();
  }

  /**
   * Returns the replacement string to use as group (eg '$2').
   *
   * @return		the group
   */
  public String getGroup() {
    return m_Group;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String groupTipText() {
    return "The replacement string to use as group (eg '$2').";
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
    result += QuickInfoHelper.toString(this, "index", m_Index, ", index: ");
    result += QuickInfoHelper.toString(this, "regExp", m_RegExp, ", regexp: ");
    result += QuickInfoHelper.toString(this, "group", m_Group, ", group: ");

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
    IndexedSplitsRuns					result;
    IndexedSplitsRun					indexedSplitsRun;
    IndexedSplits					indexedSplits;
    IndexedSplit					indexedSplit;
    Instances 						instances;
    CrossValidationGenerator 				generator;
    int 						actualNumFolds;
    int[]						trainRows;
    int[]						testRows;
    List<Binnable<Instance>> 				binnableInst;
    List<BinnableGroup<Instance>> 			groupedInst;
    List<Binnable<BinnableGroup<Instance>>> 		binnableGroups;
    List<FoldPair<Binnable<BinnableGroup<Instance>>>> 	foldPairs;
    FoldPair<Binnable<BinnableGroup<Instance>>> 	foldPair;
    int							fold;
    Struct2<TIntList,List<Binnable<Instance>>> 		subsetTrain;
    Struct2<TIntList,List<Binnable<Instance>>>		subsetTest;

    instances = (Instances) data;
    m_Index.setData(instances);

    try {
      binnableInst   = BinnableInstances.toBinnableUsingIndex(instances);
      binnableInst   = Wrapping.addTmpIndex(binnableInst);  // adding the original index
      groupedInst    = Grouping.groupAsList(binnableInst, new StringAttributeGroupExtractor(m_Index.getIntIndex(), m_RegExp.getValue(), m_Group));
      binnableGroups = Wrapping.wrap(groupedInst, new GroupedClassValueBinValueExtractor());  // wrap for CV generator
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to create binnable Instances!", e);
    }

    if (m_NumFolds < 2)
      actualNumFolds = binnableGroups.size();
    else
      actualNumFolds = m_NumFolds;

    if (binnableGroups.size() < actualNumFolds) {
      errors.add(
	"Cannot have less data than (grouped) folds: "
	  + "required=" + actualNumFolds + ", provided=" + binnableGroups.size());
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

    foldPairs        = generator.generate(binnableGroups);
    result           = new IndexedSplitsRuns();
    indexedSplits    = new IndexedSplits();
    indexedSplitsRun = new IndexedSplitsRun(0, indexedSplits);
    result.add(indexedSplitsRun);
    for (fold = 0; fold < actualNumFolds; fold++) {
      foldPair     = foldPairs.get(fold);
      subsetTrain  = Subset.extractIndicesAndBinnable(foldPair.getTrain());
      subsetTest   = Subset.extractIndicesAndBinnable(foldPair.getTest());
      trainRows    = subsetTrain.value1.toArray();
      testRows     = subsetTest.value1.toArray();
      indexedSplit = new IndexedSplit(fold);
      indexedSplit.add(new SplitIndices("train", trainRows));
      indexedSplit.add(new SplitIndices("test", testRows));
      indexedSplits.add(indexedSplit);
    }

    return result;
  }
}
