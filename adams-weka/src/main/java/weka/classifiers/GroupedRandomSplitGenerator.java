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
import adams.data.weka.WekaAttributeIndex;
import adams.flow.container.WekaTrainTestSetContainer;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.InstanceGrouping;
import weka.core.Instances;
import weka.core.InstancesView;

import java.util.Random;

/**
 * Generates random splits of datasets, making sure that groups of instances
 * stay together (identified via a regexp).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GroupedRandomSplitGenerator
  extends DefaultRandomSplitGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -4813006743965500489L;

  /** the index to use for grouping. */
  protected WekaAttributeIndex m_Index;

  /** the regular expression for the nominal/string attribute. */
  protected BaseRegExp m_RegExp;

  /** the group expression. */
  protected String m_Group;

  /** generates the groups. */
  protected InstanceGrouping m_Grouping;

  /** the collapsed dataset. */
  protected Instances m_Collapsed;

  /** the random number generator for the collapsed data. */
  protected Random m_RandomCollapsed;

  /**
   * Initializes the generator.
   */
  public GroupedRandomSplitGenerator() {
    super();
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
   * Generates the original indices.
   *
   * @return	the original indices
   */
  protected TIntList originalIndices() {
    TIntList 	result;
    int		i;

    result = new TIntArrayList();
    for (i = 0; i < m_Collapsed.numInstances(); i++)
      result.add(i);

    if (canRandomize())
      randomize(result, new Random(m_Seed));

    return result;
  }

  /**
   * Initializes the iterator, randomizes the data if required.
   */
  @Override
  protected void doInitializeIterator() {
    m_Grouping  = new InstanceGrouping(m_Data, m_Index, m_RegExp, m_Group);
    m_Collapsed = m_Grouping.collapse(m_Data);

    if (canRandomize()) {
      m_RandomCollapsed = new Random(m_Seed);
      if (!m_UseViews)
	m_Collapsed.randomize(m_RandomCollapsed);
    }

    super.doInitializeIterator();
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
    WekaTrainTestSetContainer	result;
    Instances			trainSet;
    Instances			testSet;
    Instances			trainSetExp;
    Instances			testSetExp;
    int 			trainSize;
    int 			testSize;
    TIntList			trainRows;
    TIntList			testRows;
    TIntList			trainRowsExp;
    TIntList			testRowsExp;

    m_Generated = true;

    // split collapsed dataset
    trainSize = (int) Math.round((double) m_OriginalIndices.size() * m_Percentage);
    testSize  = m_OriginalIndices.size() - trainSize;
    trainRows = m_OriginalIndices.subList(0, trainSize);
    testRows  = m_OriginalIndices.subList(trainSize, m_OriginalIndices.size());
    trainSet  = new Instances(m_Collapsed, 0, trainSize);
    testSet   = new Instances(m_Collapsed, trainSize, testSize);

    // expand
    trainRowsExp = m_Grouping.expand(m_Collapsed, trainRows);
    testRowsExp  = m_Grouping.expand(m_Collapsed, testRows);
    if (m_UseViews) {
      trainSetExp = new InstancesView(m_Data, trainRowsExp.toArray());
      testSetExp  = new InstancesView(m_Data, testRowsExp.toArray());
    }
    else {
      trainSetExp = m_Grouping.expand(trainSet, false);
      testSetExp  = m_Grouping.expand(testSet, false);
    }

    result = new WekaTrainTestSetContainer(
      trainSetExp, testSetExp, m_Seed, null, null, trainRowsExp.toArray(), testRowsExp.toArray());
    
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
