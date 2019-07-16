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
 * GroupedRandomSplitGenerator.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;

import adams.core.base.BaseRegExp;
import adams.data.binning.Binnable;
import adams.data.binning.BinnableGroup;
import adams.data.binning.BinnableInstances;
import adams.data.binning.BinnableInstances.StringAttributeGroupExtractor;
import adams.data.binning.operation.Grouping;
import adams.data.binning.operation.Wrapping;
import adams.data.binning.operation.Wrapping.IndexedBinValueExtractor;
import adams.data.splitgenerator.generic.core.Subset;
import adams.data.splitgenerator.generic.randomization.DefaultRandomization;
import adams.data.splitgenerator.generic.randomization.PassThrough;
import adams.data.splitgenerator.generic.randomsplit.RandomSplitGenerator;
import adams.data.splitgenerator.generic.randomsplit.SplitPair;
import adams.data.splitgenerator.generic.splitter.DefaultSplitter;
import adams.data.weka.WekaAttributeIndex;
import adams.flow.container.WekaTrainTestSetContainer;
import com.github.fracpete.javautils.struct.Struct2;
import gnu.trove.list.TIntList;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.InstancesView;

import java.util.List;

/**
 * Generates random splits of datasets, making sure that groups of instances
 * stay together (identified via a regexp).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GroupedRandomSplitGenerator
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

  /** the index to use for grouping. */
  protected WekaAttributeIndex m_Index;

  /** the regular expression for the nominal/string attribute. */
  protected BaseRegExp m_RegExp;

  /** the group expression. */
  protected String m_Group;

  /** the underlying scheme for generating the split. */
  protected RandomSplitGenerator m_Generator;

  /**
   * Initializes the generator.
   */
  public GroupedRandomSplitGenerator() {
    super();
  }

  /**
   * Initializes the generator. Does not preserve the order.
   *
   * @param data	the dataset to split
   * @param seed	the seed value to use for randomization
   * @param percentage	the percentage of the training set (0-1)
   * @param preserveOrder 	whether to preserve the order
   * @param index 	the attribute index
   * @param regExp 	the regular expression to apply to the attribute values
   * @param group 	the regexp group to use as group
   */
  public GroupedRandomSplitGenerator(Instances data, long seed, double percentage, boolean preserveOrder, WekaAttributeIndex index, BaseRegExp regExp, String group) {
    super();
    setData(data);
    setSeed(seed);
    setPercentage(percentage);
    setPreserveOrder(preserveOrder);
    setIndex(index);
    setRegExp(regExp);
    setGroup(group);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs a percentage split, either randomized or with the order preserved.\n"
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
    WekaTrainTestSetContainer			result;
    List<Binnable<Instance>> 			binnableInst;
    List<BinnableGroup<Instance>> 		groupedInst;
    SplitPair<Binnable<BinnableGroup<Instance>>> splitGroups;
    List<Binnable<BinnableGroup<Instance>>> 	binnableGroups;
    Instances					trainSet;
    Instances					testSet;
    int[]					trainRows;
    int[]					testRows;
    Struct2<TIntList,List<Binnable<Instance>>> 	subsetTrain;
    Struct2<TIntList,List<Binnable<Instance>>>	subsetTest;

    m_Generated = true;

    try {
      m_Index.setData(m_Data);
      binnableInst   = BinnableInstances.toBinnableUsingIndex(m_Data);
      binnableInst   = Wrapping.addTmpIndex(binnableInst);  // adding the original index
      groupedInst    = Grouping.groupAsList(binnableInst, new StringAttributeGroupExtractor(m_Index.getIntIndex(), m_RegExp.getValue(), m_Group));
      binnableGroups = Wrapping.wrap(groupedInst, new IndexedBinValueExtractor<>());  // wrap for split generator
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to create binnable Instances!", e);
    }
    splitGroups = m_Generator.generate(binnableGroups);
    subsetTrain = Subset.extractIndicesAndBinnable(splitGroups.getTrain());
    subsetTest  = Subset.extractIndicesAndBinnable(splitGroups.getTest());
    trainRows   = subsetTrain.value1.toArray();
    testRows    = subsetTest.value1.toArray();

    if (m_UseViews) {
      trainSet = new InstancesView(m_Data, trainRows);
      testSet  = new InstancesView(m_Data, testRows);
    }
    else {
      trainSet = BinnableInstances.toInstances(subsetTrain.value2);
      testSet  = BinnableInstances.toInstances(subsetTest.value2);
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
    return super.toString() + ", index=" + m_Index + ", regexp=" + m_RegExp + ", group=" + m_Group;
  }
}
