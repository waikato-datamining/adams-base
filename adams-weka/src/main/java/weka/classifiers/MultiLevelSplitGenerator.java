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
 * MultiLevelSplitGenerator.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;

import adams.core.StoppableWithFeedback;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.data.binning.Binnable;
import adams.data.binning.BinnableGroup;
import adams.data.binning.BinnableInstances;
import adams.data.binning.BinnableInstances.StringAttributeGroupExtractor;
import adams.data.binning.operation.Grouping;
import adams.data.binning.operation.Wrapping;
import adams.data.weka.WekaAttributeIndex;
import adams.flow.container.WekaTrainTestSetContainer;
import com.github.fracpete.javautils.struct.Struct2;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates splits based on groups extracted via regular expressions.
 * Each attribute index/regular expression/group represents a level.
 * At each level, the data gets split into groups according to the level's regexp/group, making up train and test sets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiLevelSplitGenerator
  extends AbstractSplitGenerator
  implements SplitGenerator, StoppableWithFeedback {

  /** for serialization. */
  private static final long serialVersionUID = -4813006743965500489L;

  /** the attribute indices. */
  protected WekaAttributeIndex[] m_Indices;

  /** the regular expressions to apply to determine the grouping. */
  protected BaseRegExp[] m_RegExps;

  /** the groups to generate. */
  protected BaseString[] m_Groups;

  /** whether to suppress error output. */
  protected boolean m_Silent;

  /** the list of generated containers. */
  protected List<WekaTrainTestSetContainer> m_Containers;

  /** whether the generation got stopped. */
  protected boolean m_Stopped;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates splits based on groups extracted via regular expressions.\n"
      + "Each attribute index/regexp/group represents a level.\n"
      + "At each level, the data gets split into groups according to the level's regexp/group, making up train and test sets.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.removeByProperty("seed");
    m_OptionManager.removeByProperty("useViews");

    m_OptionManager.add(
      "index", "indices",
      new WekaAttributeIndex[]{new WekaAttributeIndex(WekaAttributeIndex.FIRST)});

    m_OptionManager.add(
      "regexp", "regExps",
      new BaseRegExp[]{new BaseRegExp(BaseRegExp.MATCH_ALL)});

    m_OptionManager.add(
      "group", "groups",
      new BaseString[]{new BaseString("$0")});

    m_OptionManager.add(
      "silent", "silent",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Containers = new ArrayList<>();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Containers.clear();
  }

  /**
   * Sets the attribute indices.
   *
   * @param value	the indices
   */
  public void setIndices(WekaAttributeIndex[] value) {
    m_Indices = value;
    m_RegExps = (BaseRegExp[]) Utils.adjustArray(m_RegExps, m_Indices.length, new BaseRegExp(BaseRegExp.MATCH_ALL));
    m_Groups  = (BaseString[]) Utils.adjustArray(m_Groups, m_Indices.length, new BaseString("$0"));
    reset();
  }

  /**
   * Returns the attribute indices.
   *
   * @return		the indices
   */
  public WekaAttributeIndex[] getIndices() {
    return m_Indices;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indicesTipText() {
    return "The attribute indices to work on.";
  }

  /**
   * Sets the regular expressions to use for extracting the groups.
   *
   * @param value	the expressions
   */
  public void setRegExps(BaseRegExp[] value) {
    m_RegExps = value;
    m_Indices = (WekaAttributeIndex[]) Utils.adjustArray(m_Indices, m_RegExps.length, new WekaAttributeIndex(WekaAttributeIndex.FIRST));
    m_Groups  = (BaseString[]) Utils.adjustArray(m_Groups,  m_RegExps.length, new BaseString("$0"));
    reset();
  }

  /**
   * Returns the regular expressions to use for extracting the groups.
   *
   * @return		the expressions
   */
  public BaseRegExp[] getRegExps() {
    return m_RegExps;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpsTipText() {
    return "The regular expressions to use for extracting the groups.";
  }

  /**
   * Sets the groups to generate.
   *
   * @param value	the groups
   */
  public void setGroups(BaseString[] value) {
    m_Groups  = value;
    m_Indices = (WekaAttributeIndex[]) Utils.adjustArray(m_Indices, m_Groups.length, new WekaAttributeIndex(WekaAttributeIndex.FIRST));
    m_RegExps = (BaseRegExp[]) Utils.adjustArray(m_RegExps, m_Groups.length, new BaseRegExp(BaseRegExp.MATCH_ALL));
    reset();
  }

  /**
   * Returns the groups to generate.
   *
   * @return		the groups
   */
  public BaseString[] getGroups() {
    return m_Groups;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String groupsTipText() {
    return "The groups to generate.";
  }

  /**
   * Sets whether to suppress error messages.
   *
   * @param value	true if to suppress
   */
  public void setSilent(boolean value) {
    m_Silent  = value;
    reset();
  }

  /**
   * Returns whether to suppress error messages.
   *
   * @return		true if to suppress
   */
  public boolean getSilent() {
    return m_Silent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String silentTipText() {
    return "If enabled, error messages are suppressed.";
  }

  /**
   * Returns whether randomization is enabled.
   *
   * @return		true if to randomize
   */
  @Override
  protected boolean canRandomize() {
    return false;
  }

  /**
   * Generates the groups from the data by applying the regexp/group.
   *
   * @param data	the data to split into groups
   * @param index	the attribute index
   * @param regexp	the regexp to apply to the values of the attribute
   * @param group	the group ID to generate
   * @return		the generated groups
   */
  protected List<Instances> generateGroups(Instances data, int index, String regexp, String group) {
    List<Instances> 			result;
    List<Binnable<Instance>> 		binnableInst;
    List<BinnableGroup<Instance>> 	groupedInst;
    Instances 				groupData;

    result = new ArrayList<>();
    try {
      binnableInst = BinnableInstances.toBinnableUsingIndex(data);
      binnableInst = Wrapping.addTmpIndex(binnableInst);  // adding the original index
      groupedInst = Grouping.groupAsList(binnableInst, new StringAttributeGroupExtractor(index, regexp, group));
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to create binnable Instances!", e);
    }

    for (BinnableGroup<Instance> item: groupedInst) {
      binnableInst = Grouping.ungroup(item);
      groupData    = BinnableInstances.toInstances(binnableInst);
      result.add(groupData);
    }

    return result;
  }

  /**
   * Generates the subset: either the specified index of the rest.
   *
   * @param groups	the groups to use
   * @param index	the current index
   * @param invert	whether to invert
   * @return		the generated instances
   */
  protected Instances subset(List<Instances> groups, int index, boolean invert) {
    Instances	result;
    int 	capacity;
    int		i;
    int		n;

    if (!invert) {
      result = new Instances(groups.get(index));
    }
    else {
      capacity = 0;
      for (i = 0; i < groups.size(); i++) {
        if (i == index)
          continue;
        capacity += groups.get(i).numInstances();
      }
      result = new Instances(groups.get(index), capacity);
      for (i = 0; i < groups.size(); i++) {
        if (i == index)
          continue;
        for (n = 0; n < groups.get(i).numInstances(); n++)
          result.add((Instance) groups.get(i).instance(n).copy());
      }
    }

    return result;
  }

  /**
   * Generates the train/test splits.
   *
   * @param data	the data to generate the splits for
   * @param index	the attribute index
   * @param regexp	the regexp to apply to the values of the attribute
   * @param group	the group ID to generate
   * @return		the generated splits
   */
  protected List<Struct2<Instances,Instances>> generateSplits(Instances data, int index, String regexp, String group) {
    List<Struct2<Instances,Instances>>	result;
    List<Instances>			grouped;
    int					i;

    result  = new ArrayList<>();
    grouped = generateGroups(data, index, regexp, group);
    for (i = 0; i < grouped.size(); i++) {
      if (isStopped())
        return new ArrayList<>();

      result.add(new Struct2<>(
        subset(grouped, i, true),
        subset(grouped, i, false)
      ));
    }

    return result;
  }

  /**
   * Combines train and test splits as long as there are matches. Others get dropped.
   *
   * @param trainSplits	the training splits
   * @param testSplits	the test splits
   * @return		the combined splits
   */
  protected List<Struct2<Instances,Instances>> match(List<Struct2<Instances,Instances>> trainSplits, List<Struct2<Instances,Instances>> testSplits, int index) {
    List<Struct2<Instances,Instances>>	result;
    int 				i;
    List<String>			trainIDs;
    List<String>			testIDs;
    int					trainIndex;
    int					testIndex;

    result = new ArrayList<>();
    
    trainIDs = new ArrayList<>();
    for (i = 0; i < trainSplits.size(); i++)
      trainIDs.add(trainSplits.get(i).value2.instance(0).stringValue(index));

    testIDs = new ArrayList<>();
    for (i = 0; i < testSplits.size(); i++)
      testIDs.add(testSplits.get(i).value2.instance(0).stringValue(index));
    
    for (trainIndex = 0; trainIndex < trainIDs.size(); trainIndex++) {
      testIndex = testIDs.indexOf(trainIDs.get(trainIndex));
      if (testIndex > -1)
	result.add(new Struct2<>(testSplits.get(testIndex).value1, trainSplits.get(trainIndex).value2));
      else if (!m_Silent)
        getLogger().warning("No matching test data found for '" + trainIDs.get(trainIndex) + "' (att index #" + (index+1) + ")!");

    }

    return result;
  }

  /**
   * Generates the containers.
   */
  protected void generateContainers() {
    int					i;
    List<Struct2<Instances,Instances>>	splits;
    List<Struct2<Instances,Instances>>	collected;
    List<Struct2<Instances,Instances>>	trainSplits;
    List<Struct2<Instances,Instances>>	testSplits;

    m_Containers.clear();

    // initial split
    // A,B,C -> A+B,C; B+A,C; C+A,B
    collected = new ArrayList<>();
    splits    = generateSplits(m_Data, m_Indices[0].getIntIndex(), m_RegExps[0].getValue(), m_Groups[0].getValue());
    for (i = 1; i < m_Indices.length; i++) {
      if (isStopped())
        break;

      collected.clear();
      for (Struct2<Instances, Instances> split : splits) {
        // subsequent splits
	// a,b,c -> a+b,c; b+a,c; c+a,b
        trainSplits = generateSplits(split.value1, m_Indices[i].getIntIndex(), m_RegExps[i].getValue(), m_Groups[i].getValue());
        testSplits  = generateSplits(split.value2, m_Indices[i].getIntIndex(), m_RegExps[i].getValue(), m_Groups[i].getValue());
        collected.addAll(match(trainSplits, testSplits, m_Indices[i].getIntIndex()));
      }
      splits = collected;
    }

    if (!isStopped()) {
      for (Struct2<Instances, Instances> split : splits)
	m_Containers.add(new WekaTrainTestSetContainer(split.value1, split.value2));
    }
  }

  /**
   * Initializes the iterator.
   */
  protected void doInitializeIterator() {
    m_Stopped = false;

    if (m_Data == null)
      throw new IllegalStateException("No data available!");

    if (m_Indices.length == 0)
      throw new IllegalStateException("At least one level of index/regexp/group required!");

    for (WekaAttributeIndex index: m_Indices)
      index.setData(m_Data);

    generateContainers();
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
    return !isStopped() && (m_Containers.size() > 0);
  }

  /**
   * Creates the next result.
   *
   * @return		the next result
   */
  @Override
  protected WekaTrainTestSetContainer createNext() {
    return m_Containers.remove(0);
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  @Override
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Returns a short description of the generator.
   *
   * @return		a short description
   */
  @Override
  public String toString() {
    return super.toString()
      + ", indices=" + Utils.arrayToString(m_Indices)
      + ", regexps=" + Utils.arrayToString(m_RegExps)
      + ", groups=" + Utils.arrayToString(m_Groups);
  }
}
