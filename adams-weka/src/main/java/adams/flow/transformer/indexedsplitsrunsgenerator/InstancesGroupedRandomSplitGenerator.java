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
 * InstancesGroupedRandomSplitGenerator.java
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
import adams.data.binning.BinnableInstances.StringAttributeGroupExtractor;
import adams.data.binning.operation.Grouping;
import adams.data.binning.operation.Wrapping;
import adams.data.binning.operation.Wrapping.IndexedBinValueExtractor;
import adams.data.indexedsplits.IndexedSplit;
import adams.data.indexedsplits.IndexedSplits;
import adams.data.indexedsplits.IndexedSplitsRun;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.indexedsplits.SplitIndices;
import adams.data.splitgenerator.generic.core.Subset;
import adams.data.splitgenerator.generic.randomization.DefaultRandomization;
import adams.data.splitgenerator.generic.randomization.PassThrough;
import adams.data.splitgenerator.generic.randomsplit.RandomSplitGenerator;
import adams.data.splitgenerator.generic.randomsplit.SplitPair;
import adams.data.splitgenerator.generic.splitter.DefaultSplitter;
import adams.data.weka.WekaAttributeIndex;
import com.github.fracpete.javautils.struct.Struct2;
import gnu.trove.list.TIntList;
import weka.core.Instance;
import weka.core.Instances;

import java.util.List;

/**
 * Random split generator that works on Instances objects (groups instances).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InstancesGroupedRandomSplitGenerator
  extends AbstractInstancesIndexedSplitsRunsGenerator
  implements Randomizable {

  private static final long serialVersionUID = -845552507613381226L;

  /** the percentage. */
  protected double m_Percentage;

  /** the seed value. */
  protected long m_Seed;

  /** whether to preserve the order. */
  protected boolean m_PreserveOrder;

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
    return "Random split generator that works on Instances objects.\n"
      + "Ensures that groups of instances stay together, determined via a regular "
      + "expression (eg '^(.*)-([0-9]+)-(.*)$') and a group replacement string (eg '$2').";
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
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "preserve-order", "preserveOrder",
      false);

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
    return "The percentage to use for training (0-1).";
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
    result += QuickInfoHelper.toString(this, "percentage", m_Percentage, ", percentage: ");
    result += QuickInfoHelper.toString(this, "preserveOrder", (m_PreserveOrder ? "preserve order" : "randomize"), ", ");
    if (!m_PreserveOrder)
      result += QuickInfoHelper.toString(this, "seed", m_Seed, ", seed: ");
    result += QuickInfoHelper.toString(this, "index", m_Index, ", index: ");
    result += QuickInfoHelper.toString(this, "regExp", m_RegExp, ", regexp: ");
    result += QuickInfoHelper.toString(this, "group", m_Group, ", group: ");

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
    IndexedSplitsRuns				result;
    IndexedSplitsRun				indexedSplitsRun;
    IndexedSplits				indexedSplits;
    IndexedSplit				indexedSplit;
    Instances 					instances;
    RandomSplitGenerator 			generator;
    List<Binnable<Instance>> 			binnableInst;
    List<BinnableGroup<Instance>> 		groupedInst;
    SplitPair<Binnable<BinnableGroup<Instance>>> splitGroups;
    List<Binnable<BinnableGroup<Instance>>> 	binnableGroups;
    int[]					trainRows;
    int[]					testRows;
    Struct2<TIntList,List<Binnable<Instance>>> 	subsetTrain;
    Struct2<TIntList,List<Binnable<Instance>>>	subsetTest;

    instances = (Instances) data;

    generator = new RandomSplitGenerator();
    if (m_PreserveOrder) {
      PassThrough rand = new PassThrough();
      generator.setRandomization(rand);
    }
    else {
      DefaultRandomization rand = new DefaultRandomization();
      rand.setSeed(m_Seed);
      generator.setRandomization(rand);
    }

    DefaultSplitter splitter = new DefaultSplitter();
    splitter.setPercentage(m_Percentage);
    generator.setSplitter(splitter);

    try {
      m_Index.setData(instances);
      binnableInst   = BinnableInstances.toBinnableUsingIndex(instances);
      binnableInst   = Wrapping.addTmpIndex(binnableInst);  // adding the original index
      groupedInst    = Grouping.groupAsList(binnableInst, new StringAttributeGroupExtractor(m_Index.getIntIndex(), m_RegExp.getValue(), m_Group));
      binnableGroups = Wrapping.wrap(groupedInst, new IndexedBinValueExtractor<>());  // wrap for split generator
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to create binnable instances!", e);
    }
    splitGroups  = generator.generate(binnableGroups);

    subsetTrain  = Subset.extractIndicesAndBinnable(splitGroups.getTrain());
    subsetTest   = Subset.extractIndicesAndBinnable(splitGroups.getTest());
    trainRows    = subsetTrain.value1.toArray();
    testRows     = subsetTest.value1.toArray();

    indexedSplit = new IndexedSplit(0);
    indexedSplit.add(new SplitIndices("train", trainRows));
    indexedSplit.add(new SplitIndices("test", testRows));
    indexedSplits = new IndexedSplits();
    indexedSplits.add(indexedSplit);
    indexedSplitsRun = new IndexedSplitsRun(0, indexedSplits);
    result = new IndexedSplitsRuns();
    result.add(indexedSplitsRun);

    return result;
  }
}
