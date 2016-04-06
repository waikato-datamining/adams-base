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
 * AbstractHashableInstance.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package weka.core;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Enumeration;

/**
 * Ancestor for instance classes that wraps around any WEKA {@link Instance} 
 * and allow them to be used in data structures that make use of on object's 
 * hash, like maps or hashtables.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractHashableInstance
  implements Serializable, Instance {

  /** for serialization. */
  private static final long serialVersionUID = -7852430972984310506L;

  /** the current hashcode. */
  protected Integer m_HashCode;
  
  /** the wrapped instance. */
  protected Instance m_Data;

  /** whether to exclude the class from the hashcode. */
  protected boolean m_ExcludeClass;

  /** whether to exclude the weight from the hashcode. */
  protected boolean m_ExcludeWeight;
  
  /**
   * Initializes the wrapper. Class and weight are included in hashcode by default.
   * 
   * @param data	the instance to wrap
   */
  protected AbstractHashableInstance(Instance data) {
    super();
    
    m_HashCode      = null;
    m_Data          = data;
    m_ExcludeClass  = false;
    m_ExcludeWeight = false;
  }
  
  /**
   * This method produces a shallow copy of an object.
   * It does the same as the clone() method in Object, which also produces
   * a shallow copy.
   * 
   * @return		the copy
   */
  @Override
  public Object copy() {
    AbstractHashableInstance	result;
    Constructor			constr;
    
    try {
      constr = getClass().getConstructor(new Class[]{Instance.class});
      result = (AbstractHashableInstance) constr.newInstance(new Object[]{m_Data});
      result.assign(this);
    }
    catch (Exception e) {
      System.err.println("Failed to create copy of " + getClass().getName() + ":");
      e.printStackTrace();
      result = null;
    }
    
    return result;
  }

  /**
   * Assigns all the data, apart from wrapped instance, that the provided
   * hashable instance provides.
   * 
   * @param inst	the hashable instance to get the data from
   */
  protected void assign(AbstractHashableInstance inst) {
    setExcludeClass(inst.getExcludeClass());
    setExcludeWeight(inst.getExcludeWeight());
  }

  /**
   * Returns the attribute with the given index.
   * 
   * @param index the attribute's index
   * @return the attribute at the given position
   * @throws UnassignedDatasetException if instance doesn't have access to a
   *           dataset
   */
  @Override
  public Attribute attribute(int index) {
    return m_Data.attribute(index);
  }

  /**
   * Returns the attribute with the given index in the sparse representation.
   * Same as attribute(int) for a DenseInstance.
   * 
   * @param indexOfIndex the index of the attribute's index
   * @return the attribute at the given position
   * @throws UnassignedDatasetException if instance doesn't have access to a
   *           dataset
   */
  @Override
  public Attribute attributeSparse(int indexOfIndex) {
    return m_Data.attributeSparse(indexOfIndex);
  }

  /**
   * Returns class attribute.
   * 
   * @return the class attribute
   * @throws UnassignedDatasetException if the class is not set or the instance
   *           doesn't have access to a dataset
   */
  @Override
  public Attribute classAttribute() {
    return m_Data.classAttribute();
  }

  /**
   * Returns the class attribute's index.
   * 
   * @return the class index as an integer
   * @throws UnassignedDatasetException if instance doesn't have access to a
   *           dataset
   */
  @Override
  public int classIndex() {
    return m_Data.classIndex();
  }

  /**
   * Tests if an instance's class is missing.
   * 
   * @return true if the instance's class is missing
   * @throws UnassignedClassException if the class is not set or the instance
   *           doesn't have access to a dataset
   */
  @Override
  public boolean classIsMissing() {
    return m_Data.classIsMissing();
  }

  /**
   * Returns an instance's class value as a floating-point number.
   * 
   * @return the corresponding value as a double (If the corresponding attribute
   *         is nominal (or a string) then it returns the value's index as a
   *         double).
   * @throws UnassignedClassException if the class is not set or the instance
   *           doesn't have access to a dataset
   */
  @Override
  public double classValue() {
    return m_Data.classValue();
  }

  /**
   * Returns the dataset this instance has access to. (ie. obtains information
   * about attribute types from) Null if the instance doesn't have access to a
   * dataset.
   * 
   * @return the dataset the instance has accesss to
   */
  @Override
  public Instances dataset() {
    return m_Data.dataset();
  }

  /**
   * Deletes an attribute at the given position (0 to numAttributes() - 1). Only
   * succeeds if the instance does not have access to any dataset because
   * otherwise inconsistencies could be introduced.
   * 
   * @param position the attribute's position
   * @throws RuntimeException if the instance has access to a dataset
   */
  @Override
  public void deleteAttributeAt(int position) {
    invalidateHashCode();
    m_Data.deleteAttributeAt(position);
  }

  /**
   * Returns an enumeration of all the attributes.
   * 
   * @return enumeration of all the attributes
   * @throws UnassignedDatasetException if the instance doesn't have access to a
   *           dataset
   */
  @Override
  public Enumeration enumerateAttributes() {
    return m_Data.enumerateAttributes();
  }

  /**
   * Tests if the headers of two instances are equivalent.
   * 
   * @param inst another instance
   * @return true if the header of the given instance is equivalent to this
   *         instance's header
   * @throws UnassignedDatasetException if instance doesn't have access to any
   *           dataset
   */
  @Override
  public boolean equalHeaders(Instance inst) {
    return equalHeaders(inst);
  }

  /**
   * Checks if the headers of two instances are equivalent. If not, then returns
   * a message why they differ.
   * 
   * @param dataset another instance
   * @return null if the header of the given instance is equivalent to this
   *         instance's header, otherwise a message with details on why they
   *         differ
   */
  @Override
  public String equalHeadersMsg(Instance inst) {
    return m_Data.equalHeadersMsg(inst);
  }

  /**
   * Tests whether an instance has a missing value. Skips the class attribute if
   * set.
   * 
   * @return true if instance has a missing value.
   * @throws UnassignedDatasetException if instance doesn't have access to any
   *           dataset
   */
  @Override
  public boolean hasMissingValue() {
    return m_Data.hasMissingValue();
  }

  /**
   * Returns the index of the attribute stored at the given position in the
   * sparse representation. Identify function for an instance of type
   * DenseInstance.
   * 
   * @param position the position
   * @return the index of the attribute stored at the given position
   */
  @Override
  public int index(int position) {
    return index(position);
  }

  /**
   * Inserts an attribute at the given position (0 to numAttributes()). Only
   * succeeds if the instance does not have access to any dataset because
   * otherwise inconsistencies could be introduced.
   * 
   * @param position the attribute's position
   * @throws RuntimeException if the instance has accesss to a dataset
   * @throws IllegalArgumentException if the position is out of range
   */
  @Override
  public void insertAttributeAt(int position) {
    invalidateHashCode();
    m_Data.insertAttributeAt(position);
  }

  /**
   * Tests if a specific value is "missing".
   * 
   * @param attIndex the attribute's index
   * @return true if the value is "missing"
   */
  @Override
  public boolean isMissing(int attIndex) {
    return m_Data.isMissing(attIndex);
  }

  /**
   * Tests if a specific value is "missing". The given attribute has to belong
   * to a dataset.
   * 
   * @param att the attribute
   * @return true if the value is "missing"
   */
  @Override
  public boolean isMissing(Attribute att) {
    return m_Data.isMissing(att);
  }

  /**
   * Tests if a specific value is "missing" in the sparse representation. Samse
   * as isMissing(int) for a DenseInstance.
   * 
   * @param indexOfIndex the index of the attribute's index
   * @return true if the value is "missing"
   */
  @Override
  public boolean isMissingSparse(int indexOfIndex) {
    return m_Data.isMissingSparse(indexOfIndex);
  }

  /**
   * Merges this instance with the given instance and returns the result.
   * Dataset is set to null. The returned instance is of the same type as this
   * instance.
   * 
   * @param inst the instance to be merged with this one
   * @return the merged instances
   */
  @Override
  public Instance mergeInstance(Instance inst) {
    invalidateHashCode();
    return m_Data.mergeInstance(inst);
  }

  /**
   * Returns the number of attributes.
   * 
   * @return the number of attributes as an integer
   */
  @Override
  public int numAttributes() {
    return m_Data.numAttributes();
  }

  /**
   * Returns the number of class labels.
   * 
   * @return the number of class labels as an integer if the class attribute is
   *         nominal, 1 otherwise.
   * @throws UnassignedDatasetException if instance doesn't have access to any
   *           dataset
   */
  @Override
  public int numClasses() {
    return m_Data.numClasses();
  }

  /**
   * Returns the number of values present in a sparse representation.
   * 
   * @return the number of values
   */
  @Override
  public int numValues() {
    return m_Data.numValues();
  }

  /**
   * Returns the relational value of a relational attribute.
   * 
   * @param attIndex the attribute's index
   * @return the corresponding relation as an Instances object
   * @throws IllegalArgumentException if the attribute is not a relation-valued
   *           attribute
   * @throws UnassignedDatasetException if the instance doesn't belong to a
   *           dataset.
   */
  @Override
  public Instances relationalValue(int attIndex) {
    return m_Data.relationalValue(attIndex);
  }

  /**
   * Returns the relational value of a relational attribute.
   * 
   * @param att the attribute
   * @return the corresponding relation as an Instances object
   * @throws IllegalArgumentException if the attribute is not a relation-valued
   *           attribute
   * @throws UnassignedDatasetException if the instance doesn't belong to a
   *           dataset.
   */
  @Override
  public Instances relationalValue(Attribute att) {
    return m_Data.relationalValue(att);
  }

  /**
   * Replaces all missing values in the instance with the values contained in
   * the given array. A deep copy of the vector of attribute values is performed
   * before the values are replaced.
   * 
   * @param array containing the means and modes
   * @throws IllegalArgumentException if numbers of attributes are unequal
   */
  @Override
  public void replaceMissingValues(double[] array) {
    invalidateHashCode();
    m_Data.replaceMissingValues(array);
  }

  /**
   * Sets the class value of an instance to be "missing". A deep copy of the
   * vector of attribute values is performed before the value is set to be
   * missing.
   * 
   * @throws UnassignedClassException if the class is not set
   * @throws UnassignedDatasetException if the instance doesn't have access to a
   *           dataset
   */
  @Override
  public void setClassMissing() {
    if (!m_ExcludeClass)
      invalidateHashCode();
    m_Data.setClassMissing();
  }

  /**
   * Sets the class value of an instance to the given value (internal
   * floating-point format). A deep copy of the vector of attribute values is
   * performed before the value is set.
   * 
   * @param value the new attribute value (If the corresponding attribute is
   *          nominal (or a string) then this is the new value's index as a
   *          double).
   * @throws UnassignedClassException if the class is not set
   * @throws UnaddignedDatasetException if the instance doesn't have access to a
   *           dataset
   */
  @Override
  public void setClassValue(double value) {
    if (!m_ExcludeClass)
      invalidateHashCode();
    m_Data.setClassValue(value);
  }

  /**
   * Sets the class value of an instance to the given value. A deep copy of the
   * vector of attribute values is performed before the value is set.
   * 
   * @param value the new class value (If the class is a string attribute and
   *          the value can't be found, the value is added to the attribute).
   * @throws UnassignedClassException if the class is not set
   * @throws UnassignedDatasetException if the dataset is not set
   * @throws IllegalArgumentException if the attribute is not nominal or a
   *           string, or the value couldn't be found for a nominal attribute
   */
  @Override
  public void setClassValue(String value) {
    if (!m_ExcludeClass)
      invalidateHashCode();
    m_Data.setClassValue(value);
  }

  /**
   * Sets the reference to the dataset. Does not check if the instance is
   * compatible with the dataset. Note: the dataset does not know about this
   * instance. If the structure of the dataset's header gets changed, this
   * instance will not be adjusted automatically.
   * 
   * @param instances the reference to the dataset
   */
  @Override
  public void setDataset(Instances instances) {
    invalidateHashCode();
    m_Data.setDataset(instances);
  }

  /**
   * Sets a specific value to be "missing". Performs a deep copy of the vector
   * of attribute values before the value is set to be missing.
   * 
   * @param attIndex the attribute's index
   */
  @Override
  public void setMissing(int attIndex) {
    invalidateHashCode();
    m_Data.setMissing(attIndex);
  }

  /**
   * Sets a specific value to be "missing". Performs a deep copy of the vector
   * of attribute values before the value is set to be missing. The given
   * attribute has to belong to a dataset.
   * 
   * @param att the attribute
   */
  @Override
  public void setMissing(Attribute att) {
    invalidateHashCode();
    m_Data.setMissing(att);
  }

  /**
   * Sets a specific value in the instance to the given value (internal
   * floating-point format). Performs a deep copy of the vector of attribute
   * values before the value is set.
   * 
   * @param attIndex the attribute's index
   * @param value the new attribute value (If the corresponding attribute is
   *          nominal (or a string) then this is the new value's index as a
   *          double).
   */
  @Override
  public void setValue(int attIndex, double value) {
    invalidateHashCode();
    m_Data.setValue(attIndex, value);
  }

  /**
   * Sets a value of a nominal or string attribute to the given value. Performs
   * a deep copy of the vector of attribute values before the value is set.
   * 
   * @param attIndex the attribute's index
   * @param value the new attribute value (If the attribute is a string
   *          attribute and the value can't be found, the value is added to the
   *          attribute).
   * @throws UnassignedDatasetException if the dataset is not set
   * @throws IllegalArgumentException if the selected attribute is not nominal
   *           or a string, or the supplied value couldn't be found for a
   *           nominal attribute
   */
  @Override
  public void setValue(int attIndex, String value) {
    invalidateHashCode();
    m_Data.setValue(attIndex, value);
  }

  /**
   * Sets a specific value in the instance to the given value (internal
   * floating-point format). Performs a deep copy of the vector of attribute
   * values before the value is set, so if you are planning on calling setValue
   * many times it may be faster to create a new instance using toDoubleArray.
   * The given attribute has to belong to a dataset.
   * 
   * @param att the attribute
   * @param value the new attribute value (If the corresponding attribute is
   *          nominal (or a string) then this is the new value's index as a
   *          double).
   */
  @Override
  public void setValue(Attribute att, double value) {
    invalidateHashCode();
    m_Data.setValue(att, value);
  }

  /**
   * Sets a value of an nominal or string attribute to the given value. Performs
   * a deep copy of the vector of attribute values before the value is set, so
   * if you are planning on calling setValue many times it may be faster to
   * create a new instance using toDoubleArray. The given attribute has to
   * belong to a dataset.
   * 
   * @param att the attribute
   * @param value the new attribute value (If the attribute is a string
   *          attribute and the value can't be found, the value is added to the
   *          attribute).
   * @throws IllegalArgumentException if the the attribute is not nominal or a
   *           string, or the value couldn't be found for a nominal attribute
   */
  @Override
  public void setValue(Attribute att, String value) {
    invalidateHashCode();
    m_Data.setValue(att, value);
  }

  /**
   * Sets a specific value in the instance to the given value (internal
   * floating-point format), given an index into the sparse representation.
   * Performs a deep copy of the vector of attribute values before the value is
   * set. Same as setValue(int, double) for a DenseInstance.
   * 
   * @param indexOfIndex the index of the attribute's index
   * @param value the new attribute value (If the corresponding attribute is
   *          nominal (or a string) then this is the new value's index as a
   *          double).
   */
  @Override
  public void setValueSparse(int indexOfIndex, double value) {
    invalidateHashCode();
    m_Data.setValueSparse(indexOfIndex, value);
  }

  /**
   * Sets the weight of an instance.
   * 
   * @param weight the weight
   */
  @Override
  public void setWeight(double weight) {
    invalidateHashCode();
    m_Data.setWeight(weight);
  }

  /**
   * Returns the value of a nominal, string, date, or relational attribute for
   * the instance as a string.
   * 
   * @param attIndex the attribute's index
   * @return the value as a string
   * @throws IllegalArgumentException if the attribute is not a nominal, string,
   *           date, or relation-valued attribute.
   * @throws UnassignedDatasetException if the instance doesn't belong to a
   *           dataset.
   */
  @Override
  public String stringValue(int attIndex) {
    return m_Data.stringValue(attIndex);
  }

  /**
   * Returns the value of a nominal, string, date, or relational attribute for
   * the instance as a string.
   * 
   * @param att the attribute
   * @return the value as a string
   * @throws IllegalArgumentException if the attribute is not a nominal, string,
   *           date, or relation-valued attribute.
   * @throws UnassignedDatasetException if the instance doesn't belong to a
   *           dataset.
   */
  @Override
  public String stringValue(Attribute att) {
    return m_Data.stringValue(att);
  }

  /**
   * Returns the values of each attribute as an array of doubles.
   * 
   * @return an array containing all the instance attribute values
   */
  @Override
  public double[] toDoubleArray() {
    return m_Data.toDoubleArray();
  }

  /**
   * Returns the value of the {@link Instance}'s <code>toString()</code> method.
   * 
   * @return the instance as a string
   */
  @Override
  public String toString() {
    return m_Data.toString();
  }

  /**
   * Returns the description of one value of the instance as a string. If the
   * instance doesn't have access to a dataset, it returns the internal
   * floating-point value. Quotes string values that contain whitespace
   * characters, or if they are a question mark.
   * 
   * @param attIndex the attribute's index
   * @return the value's description as a string
   */
  @Override
  public String toString(int attIndex) {
    return m_Data.toString(attIndex);
  }

  /**
   * Returns the description of one value of the instance as a string. If the
   * instance doesn't have access to a dataset it returns the internal
   * floating-point value. Quotes string values that contain whitespace
   * characters, or if they are a question mark. The given attribute has to
   * belong to a dataset.
   * 
   * @param att the attribute
   * @return the value's description as a string
   */
  @Override
  public String toString(Attribute att) {
    return m_Data.toString(att);
  }

  /**
   * Returns the description of one value of the instance as a string. If the
   * instance doesn't have access to a dataset, it returns the internal
   * floating-point value. Quotes string values that contain whitespace
   * characters, or if they are a question mark.
   * 
   * @param attIndex the attribute's index
   * @param afterDecimalPoint the maximum number of digits permitted after the
   *          decimal point for numeric values
   * @return the value's description as a string
   */
  @Override
  public String toString(int attIndex, int afterDecimalPoint) {
    return m_Data.toString(attIndex, afterDecimalPoint);
  }

  /**
   * Returns the description of one value of the instance as a string. If the
   * instance doesn't have access to a dataset it returns the internal
   * floating-point value. Quotes string values that contain whitespace
   * characters, or if they are a question mark. The given attribute has to
   * belong to a dataset.
   * 
   * @param att the attribute
   * @param afterDecimalPoint the maximum number of decimal places to print
   * @return the value's description as a string
   */
  @Override
  public String toString(Attribute att, int afterDecimalPoint) {
    return m_Data.toString(att, afterDecimalPoint);
  }

  /**
   * Returns the description of one instance with any numeric values printed at
   * the supplied maximum number of decimal places. If the instance doesn't have
   * access to a dataset, it returns the internal floating-point values. Quotes
   * string values that contain whitespace characters.
   * 
   * @param afterDecimalPoint the maximum number of digits permitted after the
   *          decimal point for a numeric value
   * 
   * @return the instance's description as a string
   */
  @Override
  public String toStringMaxDecimalDigits(int afterDecimalPoint) {
    return m_Data.toStringMaxDecimalDigits(afterDecimalPoint);
  }

  /**
   * Returns the description of one instance (without weight appended). If the
   * instance doesn't have access to a dataset, it returns the internal
   * floating-point values. Quotes string values that contain whitespace
   * characters.
   * 
   * This method is used by getRandomNumberGenerator() in Instances.java in
   * order to maintain backwards compatibility with weka 3.4.
   * 
   * @return the instance's description as a string
   */
  @Override
  public String toStringNoWeight() {
    return m_Data.toStringNoWeight();
  }

  /**
   * Returns the description of one instance (without weight appended). If the
   * instance doesn't have access to a dataset, it returns the internal
   * floating-point values. Quotes string values that contain whitespace
   * characters.
   * 
   * This method is used by getRandomNumberGenerator() in Instances.java in
   * order to maintain backwards compatibility with weka 3.4.
   * 
   * @param afterDecimalPoint maximum number of digits after the decimal point
   *          for numeric values
   * 
   * @return the instance's description as a string
   */
  @Override
  public String toStringNoWeight(int afterDecimalPoint) {
    return m_Data.toStringMaxDecimalDigits(afterDecimalPoint);
  }

  /**
   * Returns an instance's attribute value in internal format.
   * 
   * @param attIndex the attribute's index
   * @return the specified value as a double (If the corresponding attribute is
   *         nominal (or a string) then it returns the value's index as a
   *         double).
   */
  @Override
  public double value(int attIndex) {
    return m_Data.value(attIndex);
  }

  /**
   * Returns an instance's attribute value in internal format. The given
   * attribute has to belong to a dataset.
   * 
   * @param att the attribute
   * @return the specified value as a double (If the corresponding attribute is
   *         nominal (or a string) then it returns the value's index as a
   *         double).
   */
  @Override
  public double value(Attribute att) {
    return m_Data.value(att);
  }

  /**
   * Returns an instance's attribute value in internal format, given an index in
   * the sparse representation. Same as value(int) for a DenseInstance.
   * 
   * @param indexOfIndex the index of the attribute's index
   * @return the specified value as a double (If the corresponding attribute is
   *         nominal (or a string) then it returns the value's index as a
   *         double).
   */
  @Override
  public double valueSparse(int indexOfIndex) {
    return m_Data.valueSparse(indexOfIndex);
  }

  /**
   * Returns the instance's weight.
   * 
   * @return the instance's weight as a double
   */
  @Override
  public double weight() {
    return m_Data.weight();
  }

  /**
   * Returns only true if the same class and the same hashcode.
   * 
   * @return		true if same class and hashcode
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj.getClass() != getClass())
      return false;
    return (obj.hashCode() == hashCode());
  }
  
  /**
   * Sets whether to exclude the class from the hashcode computation.
   * 
   * @param value	if true the class value gets excluded
   */
  public void setExcludeClass(boolean value) {
    invalidateHashCode();
    m_ExcludeClass = value;
  }
  
  /**
   * Returns whether the class is excluded from the hashcode computation.
   * 
   * @return		true if the class is excluded
   */
  public boolean getExcludeClass() {
    return m_ExcludeClass;
  }

  /**
   * Sets whether to exclude the weight from the hashcode computation.
   * 
   * @param value	if true the weight value gets excluded
   */
  public void setExcludeWeight(boolean value) {
    invalidateHashCode();
    m_ExcludeWeight = value;
  }
  
  /**
   * Returns whether the weight is excluded from the hashcode computation.
   * 
   * @return		true if the weight is excluded
   */
  public boolean getExcludeWeight() {
    return m_ExcludeWeight;
  }
  
  /**
   * Invalidates the hash code.
   */
  protected void invalidateHashCode() {
    m_HashCode = null;
  }

  /**
   * Computes the hashcode.
   * 
   * @return		the hash code
   * @see	`	#m_ExcludeClass
   * @see		#m_ExcludeWeight
   */
  protected abstract int computeHashCode();
  
  /**
   * Returns the hashcode of this {@link Instance}, computes it if neccessary.
   * 
   * @return		the hashcode
   * @see		#computeHashCode()
   */
  @Override
  public synchronized int hashCode() {
    if (m_HashCode == null)
      m_HashCode = computeHashCode();
    return m_HashCode;
  }
}
