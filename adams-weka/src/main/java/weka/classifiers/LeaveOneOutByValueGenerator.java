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
 * LeaveOneOutByValueGenerator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package weka.classifiers;

import adams.data.weka.WekaAttributeIndex;
import adams.flow.container.WekaTrainTestSetContainer;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Generates train/test split pairs using the unique values from the specified attribute.
 * All values apart from one will be part of the training data and the remainder the test set.
 * This is repeated for each unique value.
 * <br><br>
 * The template for the relation name accepts the following placeholders:
 * @ = original relation name, $T = type (train/test), $N = current fold number
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LeaveOneOutByValueGenerator
    extends AbstractSplitGenerator {

  private static final long serialVersionUID = -6949071991599401776L;

  /** the placeholder for the (original) relation name. */
  public final static String PLACEHOLDER_ORIGINAL = "@";

  /** the placeholder for "train" or "test" type. */
  public final static String PLACEHOLDER_TYPE = "$T";

  /** the placeholder for the current value in the test set. */
  public final static String PLACEHOLDER_CURRENTVALUE = "$V";

  /** the template for the relation name. */
  protected String m_RelationName;

  /** whether to randomize the data. */
  protected boolean m_Randomize;

  /** the index to get the unique values from. */
  protected WekaAttributeIndex m_Index;

  /** the unique values. */
  protected transient List<String> m_UniqueValues;

  /** the current train/test pair to generate. */
  protected transient int m_CurrentPair;

  /**
   * Initializes the generator.
   */
  public LeaveOneOutByValueGenerator() {
    super();
  }

  /**
   * Initializes the generator.
   *
   * @param data	the full dataset
   * @param seed	the seed for randomization
   * @param randomize   whether to randomize the data
   * @param index 	the attribute index
   */
  public LeaveOneOutByValueGenerator(Instances data, long seed, boolean randomize, WekaAttributeIndex index) {
    super();
    setData(data);
    setSeed(seed);
    setRandomize(randomize);
    setIndex(index);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates train/test split pairs using the unique values from the specified attribute. "
	+ "All values apart from one will be part of the training data and the remainder the test set. "
	+ "This is repeated for each unique value.";
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
	"randomize", "randomize",
	true);

    m_OptionManager.add(
	"relation-name", "relationName",
	PLACEHOLDER_ORIGINAL);
  }

  /**
   * Resets the generator.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CurrentPair  = 1;
    m_UniqueValues = null;
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
   * Returns the tiptext for the relation name template.
   *
   * @return		the tiptext
   */
  public static String relationNameTipText() {
    return "The template for the relation name; available placeholders: "
	+ PLACEHOLDER_ORIGINAL + " for original, "
	+ PLACEHOLDER_TYPE + " for type (train/test), "
	+ PLACEHOLDER_CURRENTVALUE + " for current value (string)";
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
    return (m_UniqueValues != null) && (m_CurrentPair <= m_UniqueValues.size());
  }

  /**
   * Initializes the iterator, randomizes the data if required.
   */
  @Override
  protected void doInitializeIterator() {
    int   	col;
    int		i;

    if (m_Data == null)
      throw new IllegalStateException("No data provided!");

    if (canRandomize()) {
      m_Data = new Instances(m_Data);
      m_Data.randomize(new Random(getSeed()));
    }

    m_UniqueValues = new ArrayList<>();
    m_Index.setData(m_Data);
    col = m_Index.getIntIndex();
    if (col == -1)
      throw new IllegalStateException("Attribute not found: " + m_Index.getIndex());

    if (m_Data.attribute(col).isNumeric())
      throw new IllegalStateException("Attribute is numeric: " + m_Index.getIndex());

    for (i = 0; i < m_Data.attribute(col).numValues(); i++)
      m_UniqueValues.add(m_Data.attribute(col).value(i));

    if ((m_RelationName == null) || m_RelationName.isEmpty())
      m_RelationName = PLACEHOLDER_ORIGINAL;
  }

  /**
   * Generates a relation name for the current value.
   *
   * @param relation 	the original relation name
   * @param template 	the template for the relation name
   * @param current 	the current value
   * @param train	whether train or test set
   * @return		the relation name
   */
  protected String createRelationName(String relation, String template, String current, boolean train) {
    StringBuilder	result;
    String		name;
    int			len;

    result = new StringBuilder();
    name   = template;

    while (name.length() > 0) {
      if (name.startsWith(PLACEHOLDER_ORIGINAL)) {
	len = 1;
	result.append(relation);
      }
      else if (name.startsWith(PLACEHOLDER_TYPE)) {
	len = 2;
	if (train)
	  result.append("train");
	else
	  result.append("test");
      }
      else if (name.startsWith(PLACEHOLDER_CURRENTVALUE)) {
	len = 2;
	result.append(current);
      }
      else {
	len = 1;
	result.append(name.charAt(0));
      }

      name = name.substring(len);
    }

    return result.toString();
  }

  /**
   * Returns the next element in the iteration.
   *
   * @return 				the next element in the iteration.
   * @throws NoSuchElementException 	iteration has no more elements.
   */
  @Override
  protected WekaTrainTestSetContainer createNext() {
    WekaTrainTestSetContainer	result;
    Instances 			trainSet;
    Instances 			testSet;
    Instance			inst;
    String			current;
    int				col;
    int				i;

    if (m_CurrentPair > m_UniqueValues.size())
      throw new NoSuchElementException("No more pairs available!");

    m_Index.setData(m_Data);
    col      = m_Index.getIntIndex();
    current  = m_UniqueValues.get(m_CurrentPair - 1);
    trainSet = new Instances(m_Data, m_Data.numInstances());
    testSet  = new Instances(m_Data, m_Data.numInstances());

    for (i = 0; i < m_Data.numInstances(); i++) {
      inst = m_Data.instance(i);
      if (inst.stringValue(col).equals(current))
        testSet.add((Instance) inst.copy());
      else
	trainSet.add((Instance) inst.copy());
    }

    trainSet.compactify();
    testSet.compactify();
    trainSet.setRelationName(createRelationName(m_Data.relationName(), m_RelationName, current, true));
    testSet.setRelationName(createRelationName(m_Data.relationName(), m_RelationName, current, false));

    result = new WekaTrainTestSetContainer(trainSet, testSet);

    m_CurrentPair++;

    return result;
  }

  /**
   * Returns a short description of the generator.
   *
   * @return		a short description
   */
  @Override
  public String toString() {
    return super.toString() + ", relName=" + m_RelationName + ", index=" + m_Index;
  }
}
