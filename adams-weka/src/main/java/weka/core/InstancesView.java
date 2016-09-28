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

/**
 * InstancesView.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package weka.core;

import adams.core.exception.NotImplementedException;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Presents a view of an Instances object. Rows can be limited.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstancesView
  extends Instances {

  private static final long serialVersionUID = 6849032134927533467L;

  /** the underlying dataset. */
  protected Instances m_Dataset;

  /** the rows to use. */
  protected TIntList m_Rows;

  /**
   * Initializes the dataset.
   *
   * @param dataset	the underlying dataset
   * @param rows	the rows to use, null for all
   */
  public InstancesView(Instances dataset, int[] rows) {
    super(new Instances(dataset, 0));
    m_Dataset = dataset;
    m_Rows    = (rows == null) ? null : new TIntArrayList(rows);
  }

  /**
   * Create a copy of the structure. If the data has string or relational
   * attributes, theses are replaced by empty copies. Other attributes are left
   * unmodified, but the underlying list structure holding references to the attributes
   * is shallow-copied, so that other Instances objects with a reference to this list are not affected.
   *
   * @return a copy of the instance structure.
   */
  public Instances stringFreeStructure() {
    return new InstancesView(
      m_Dataset.stringFreeStructure(),
      (m_Rows == null) ? null : m_Rows.toArray());
  }

  /**
   * Adds one instance to the end of the set. Shallow copies instance before it
   * is added. Increases the size of the dataset if it is not large enough. Does
   * not check if the instance is compatible with the dataset. Note: String or
   * relational values are not transferred.
   *
   * @param instance the instance to be added
   */
  @Override
  public boolean add(Instance instance) {
    m_Dataset.add(instance);
    if (m_Rows != null)
      m_Rows.add(m_Dataset.numInstances() - 1);
    return true;
  }

  /**
   * Adds one instance at the given position in the list. Shallow
   * copies instance before it is added. Increases the size of the
   * dataset if it is not large enough. Does not check if the instance
   * is compatible with the dataset. Note: String or relational values
   * are not transferred.
   *
   * @param index position where instance is to be inserted
   * @param instance the instance to be added
   */
  @Override
  public void add(int index, Instance instance) {
    throw new NotImplementedException();
  }

  /**
   * Does nothing.
   */
  public void compactify() {
  }

  /**
   * Removes all instances from the set.
   */
  public void delete() {
    m_Rows = null;
    m_Dataset.delete();
  }

  /**
   * Removes an instance at the given position from the set.
   *
   * @param index the instance's position (index starts with 0)
   */
  public void delete(int index) {
    if (m_Rows == null)
      m_Dataset.delete(index);
    else
      m_Rows.removeAt(index);
  }

  /**
   * Deletes an attribute at the given position (0 to numAttributes()
   * - 1). Attribute objects after the deletion point are copied so
   * that their indices can be decremented. Creates a fresh list to
   * hold the old and new attribute objects.
   * @param position the attribute's position (position starts with 0)
   * @throws IllegalArgumentException if the given index is out of range or the
   *           class attribute is being deleted
   */
  public void deleteAttributeAt(int position) {
    super.deleteAttributeAt(position);
    m_Dataset.deleteAttributeAt(position);
  }

  /**
   * Deletes all attributes of the given type in the dataset. A deep copy of the
   * attribute information is performed before an attribute is deleted.
   *
   * @param attType the attribute type to delete
   * @throws IllegalArgumentException if attribute couldn't be successfully
   *           deleted (probably because it is the class attribute).
   */
  public void deleteAttributeType(int attType) {
    super.deleteAttributeAt(attType);
    m_Dataset.deleteAttributeType(attType);
  }

  /**
   * Removes all instances with missing values for a particular attribute from
   * the dataset.
   *
   * @param attIndex the attribute's index (index starts with 0)
   */
  public void deleteWithMissing(int attIndex) {
    TIntArrayList	newRows;
    int			i;

    if (m_Rows == null) {
      m_Dataset.deleteWithMissing(attIndex);
      return;
    }

    newRows = new TIntArrayList();
    for (i = 0; i < m_Rows.size(); i++) {
      if (!instance(m_Rows.get(i)).isMissing(attIndex))
	newRows.add(m_Rows.get(i));
    }
    m_Rows = newRows;
  }

  /**
   * Returns an enumeration of all instances in the dataset.
   *
   * @return enumeration of all instances in the dataset
   */
  public Enumeration<Instance> enumerateInstances() {
    List<Instance>	insts;
    int			i;

    if (m_Rows == null)
      return m_Dataset.enumerateInstances();

    insts = new ArrayList<>();
    for (i = 0; i < numInstances(); i++)
      insts.add(instance(i));

    return new WekaEnumeration<>(insts);
  }

  /**
   * Returns the first instance in the set.
   *
   * @return the first instance in the set
   */
  public Instance firstInstance() {
    return instance(0);
  }

  /**
   * Inserts an attribute at the given position (0 to numAttributes())
   * and sets all values to be missing. Shallow copies the attribute
   * before it is inserted. Existing attribute objects at and after
   * the insertion point are also copied so that their indices can be
   * incremented. Creates a fresh list to hold the old and new
   * attribute objects.
   *
   * @param att the attribute to be inserted
   * @param position the attribute's position (position starts with 0)
   * @throws IllegalArgumentException if the given index is out of range
   */
  public void insertAttributeAt(Attribute att, int position) {
    super.insertAttributeAt(att, position);
    m_Dataset.insertAttributeAt(att, position);
  }

  /**
   * Returns the instance at the given position.
   *
   * @param index the instance's index (index starts with 0)
   * @return the instance at the given position
   */
  public Instance instance(int index) {
    if (m_Rows == null)
      return m_Dataset.instance(index);
    else
      return m_Dataset.instance(m_Rows.get(index));
  }

  /**
   * Returns the instance at the given position.
   *
   * @param index the instance's index (index starts with 0)
   * @return the instance at the given position
   */
  @Override
  public Instance get(int index) {
    return instance(index);
  }

  /**
   * Returns the last instance in the set.
   *
   * @return the last instance in the set
   */
  public Instance lastInstance() {
    return instance(numInstances() - 1);
  }

  /**
   * Returns the number of class labels.
   *
   * @return the number of class labels as an integer if the class attribute is
   *         nominal, 1 otherwise.
   * @throws UnassignedClassException if the class is not set
   */
  public int numClasses() {
    if (!classAttribute().isNominal())
      return 1;
    else
      return classAttribute().numValues();
  }

  /**
   * Returns the number of instances in the dataset.
   *
   * @return the number of instances in the dataset as an integer
   */
  @Override
  public int size() {
    return numInstances();
  }

  /**
   * Returns the number of instances in the dataset.
   *
   * @return the number of instances in the dataset as an integer
   */
  public int numInstances() {
    return (m_Rows != null) ? m_Rows.size() : m_Dataset.numInstances();
  }

  /**
   * Swaps two instances in the set.
   *
   * @param i the first instance's index (index starts with 0)
   * @param j the second instance's index (index starts with 0)
   */
  public void swap(int i, int j) {
    int		old;

    if (m_Rows == null) {
      m_Dataset.swap(i, j);
    }
    else {
      old = m_Rows.get(i);
      m_Rows.set(i, m_Rows.get(j));
      m_Rows.set(j, old);
    }
  }

  /**
   * Replaces the attribute at the given position (0 to
   * numAttributes()) with the given attribute and sets all its values to
   * be missing. Shallow copies the given attribute before it is
   * inserted. Creates a fresh list to hold the old and new
   * attribute objects.
   *
   * @param att the attribute to be inserted
   * @param position the attribute's position (position starts with 0)
   * @throws IllegalArgumentException if the given index is out of range
   */
  public void replaceAttributeAt(Attribute att, int position) {
    super.replaceAttributeAt(att, position);
    m_Dataset.replaceAttributeAt(att, position);
  }

  /**
   * Removes the instance at the given position.
   *
   * @param index the instance's index (index starts with 0)
   * @return the instance at the given position
   */
  @Override
  public Instance remove(int index) {
    Instance	result;

    result = instance(index);
    delete(index);

    return result;
  }

  /**
   * Renames an attribute. This change only affects this dataset.
   *
   * @param att the attribute's index (index starts with 0)
   * @param name the new name
   */
  public void renameAttribute(int att, String name) {
    super.renameAttribute(att, name);
    m_Dataset.renameAttribute(att, name);
  }

  /**
   * Renames the value of a nominal (or string) attribute value. This change
   * only affects this dataset.
   *
   * @param att the attribute's index (index starts with 0)
   * @param val the value's index (index starts with 0)
   * @param name the new name
   */
  public void renameAttributeValue(int att, int val, String name) {
    super.renameAttributeValue(att, val, name);
    m_Dataset.renameAttributeValue(att, val, name);
  }

  /**
   * Replaces the instance at the given position. Shallow copies instance before
   * it is added. Does not check if the instance is compatible with the dataset.
   * Note: String or relational values are not transferred.
   *
   * @param index position where instance is to be inserted
   * @param instance the instance to be inserted
   * @return the instance previously at that position
   */
  @Override
  public Instance set(int index, Instance instance) {
    if (m_Rows == null)
      return m_Dataset.set(index, instance);
    else
      return m_Dataset.set(m_Rows.get(index), instance);
  }

  /**
   * Sorts a nominal attribute (stable, linear-time sort). Instances
   * are sorted based on the attribute label ordering specified in the header.
   *
   * @param attIndex the attribute's index (index starts with 0)
   */
  protected void sortBasedOnNominalAttribute(int attIndex) {
    throw new NotImplementedException();
  }

  /**
   * Sorts the instances based on an attribute. For numeric attributes,
   * instances are sorted in ascending order. For nominal attributes, instances
   * are sorted based on the attribute label ordering specified in the header.
   * Instances with missing values for the attribute are placed at the end of
   * the dataset.
   *
   * @param attIndex the attribute's index (index starts with 0)
   */
  public void sort(int attIndex) {
    throw new NotImplementedException();
  }

  /**
   * Sorts the instances based on an attribute, using a stable sort. For numeric attributes,
   * instances are sorted in ascending order. For nominal attributes, instances
   * are sorted based on the attribute label ordering specified in the header.
   * Instances with missing values for the attribute are placed at the end of
   * the dataset.
   *
   * @param attIndex the attribute's index (index starts with 0)
   */
  public void stableSort(int attIndex) {
    throw new NotImplementedException();
  }

  /**
   * Calculates summary statistics on the values that appear in this set of
   * instances for a specified attribute.
   *
   * @param index the index of the attribute to summarize (index starts with 0)
   * @return an AttributeStats object with it's fields calculated.
   */
  public AttributeStats attributeStats(int index) {
    AttributeStats result = new AttributeStats();
    if (attribute(index).isNominal()) {
      result.nominalCounts = new int[attribute(index).numValues()];
      result.nominalWeights = new double[attribute(index).numValues()];
    }
    if (attribute(index).isNumeric())
      result.numericStats = new weka.experiment.Stats();
    result.totalCount = numInstances();

    HashMap<Double,double[]> map = new HashMap<>(2 * result.totalCount);
    for (int i = 0; i < numInstances(); i++) {
      Instance current = instance(i);
      double key = current.value(index);
      if (Utils.isMissingValue(key)) {
        result.missingCount++;
      } else {
        double[] values = map.get(key);
        if (values == null) {
          values = new double[2];
          values[0] = 1.0;
          values[1] = current.weight();
          map.put(key, values);
        } else {
          values[0]++;
          values[1] += current.weight();
        }
      }
    }

    for (Entry<Double, double[]> entry : map.entrySet()) {
      result.addDistinct(entry.getKey(), (int)entry.getValue()[0], entry.getValue()[1]);
    }
    return result;
  }

  /**
   * Help function needed for stratification of set.
   *
   * @param numFolds the number of folds for the stratification
   */
  protected void stratStep(int numFolds) {
    throw new NotImplementedException();
  }

  /**
   * Sets the class index of the set. If the class index is negative there is
   * assumed to be no class. (ie. it is undefined)
   *
   * @param classIndex the new class index (index starts with 0)
   * @throws IllegalArgumentException if the class index is too big or < 0
   */
  @Override
  public void setClassIndex(int classIndex) {
    super.setClassIndex(classIndex);
    m_Dataset.setClassIndex(classIndex);
  }
}
