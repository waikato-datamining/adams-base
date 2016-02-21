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
 * Dataset.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.ml.data;

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import gnu.trove.list.array.TIntArrayList;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Extended {@link SpreadSheet} class, providing additional machine
 * learning functionality.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Dataset
  extends DefaultSpreadSheet {

  /** for serialization. */
  private static final long serialVersionUID = -6517147329804452995L;
  
  /** the keys of the columns that act as class attribute. */
  protected HashSet<String> m_ClassAttributes;
  
  /**
   * Default constructor.
   */
  public Dataset() {
    super();
  }

  /**
   * Initializes the dataset with the data from the spreadsheet.
   * 
   * @param sheet	the data to use
   */
  public Dataset(SpreadSheet sheet) {
    this();
    assign(sheet);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_ClassAttributes = new HashSet<String>();
  }

  /**
   * Removes all cells, but leaves comments.
   */
  @Override
  public void clear() {
    super.clear();
    m_ClassAttributes.clear();
  }
  
  /**
   * Clears this spreadsheet and copies all the data from the given one.
   * 
   * @param sheet	the data to copy
   */
  @Override
  public void assign(SpreadSheet sheet) {
    super.assign(sheet);
    
    if (sheet instanceof Dataset)
      m_ClassAttributes.addAll(((Dataset) sheet).m_ClassAttributes);
  }

  /**
   * Returns a clone of itself.
   *
   * @return		the clone
   */
  @Override
  public SpreadSheet getClone() {
    SpreadSheet	result;
    
    result = super.getClone();
    
    if (result instanceof Dataset)
      ((Dataset) result).m_ClassAttributes.addAll(m_ClassAttributes);
    
    return result;
  }
  
  /**
   * Returns the a spreadsheet with the same header and comments.
   *
   * @return		the spreadsheet
   */
  @Override
  public SpreadSheet getHeader() {
    SpreadSheet	result;
    
    result = super.getHeader();

    if (result instanceof Dataset)
      ((Dataset) result).m_ClassAttributes.addAll(m_ClassAttributes);
    
    return result;
  }

  /**
   * Removes the specified column.
   *
   * @param columnKey	the column to remove
   * @return		true if removed
   */
  @Override
  public boolean removeColumn(String columnKey) {
    m_ClassAttributes.remove(columnKey);
    return super.removeColumn(columnKey);
  }

  /**
   * Removes all set class attributes.
   */
  public void removeClassAttributes() {
    m_ClassAttributes.clear();
  }

  /**
   * Returns whether the specified column is a class attribute.
   * 
   * @param colKey	they key of the column to query
   * @return		true if column a class attribute
   */
  public boolean isClassAttribute(String colKey) {
    if (colKey == null)
      return false;
    else
      return m_ClassAttributes.contains(colKey);
  }
  
  /**
   * Returns whether the specified column is a class attribute.
   * 
   * @param colIndex	they index of the column to query
   * @return		true if column a class attribute
   */
  public boolean isClassAttribute(int colIndex) {
    return isClassAttribute(m_HeaderRow.getCellKey(colIndex));
  }
  
  /**
   * Sets the class attribute status for a column.
   * 
   * @param colKey	the column to set the class attribute status for
   * @param isClass	if true then the column will be flagged as class 
   * 			attribute, otherwise the flag will get removed
   * @return		true if successfully updated
   */
  public boolean setClassAttribute(String colKey, boolean isClass) {
    if (colKey == null)
      return false;
    if (getHeaderRow().indexOf(colKey) == -1)
      return false;
    
    if (isClass)
      m_ClassAttributes.add(colKey);
    else
      m_ClassAttributes.remove(colKey);
    
    return true;
  }
  
  /**
   * Sets the class attribute status for a column.
   * 
   * @param colIndex	the column to set the class attribute status for
   * @param isClass	if true then the column will be flagged as class 
   * 			attribute, otherwise the flag will get removed
   * @return		true if successfully updated
   */
  public boolean setClassAttribute(int colIndex, boolean isClass) {
    return setClassAttribute(m_HeaderRow.getCellKey(colIndex), isClass);
  }
  
  /**
   * Returns all the class attributes that are currently set.
   * 
   * @return		the column keys of class attributes (not ordered)
   */
  public String[] getClassAttributeKeys() {
    return m_ClassAttributes.toArray(new String[m_ClassAttributes.size()]);
  }
  
  /**
   * Returns all the class attributes that are currently set.
   * 
   * @return		the indices of class attributes (sorted asc)
   */
  public int[] getClassAttributeIndices() {
    int[]	result;
    String[]	keys;
    int		i;
    
    keys   = getClassAttributeKeys();
    result = new int[keys.length];
    for (i = 0; i < keys.length; i++)
      result[i] = getHeaderRow().indexOf(keys[i]);
    Arrays.sort(result);
    
    return result;
  }

  /**
   * Compares the header of this spreadsheet with the other one.
   *
   * @param other	the other spreadsheet to compare with
   * @return		null if equal, otherwise details what differs
   */
  @Override
  public String equalsHeader(SpreadSheet other) {
    String	result;
    int[]	indices;
    int[]	otherIndices;
    int		i;
    
    result = super.equalsHeader(other);
    
    if (result == null) {
      if (other instanceof Dataset) {
	indices      = getClassAttributeIndices();
	otherIndices = ((Dataset) other).getClassAttributeIndices();
	if (indices.length != otherIndices.length)
	  result = "Number of class attributes differ: " + indices.length + " != " + otherIndices.length;
	if (result == null) {
	  for (i = 0; i < indices.length; i++) {
	    if (indices[i] != otherIndices[i]) {
	      result = "Class attribute index #" + (i+1) + " differs: " + (indices[i]+1) + " != " + (otherIndices[i]+1);
	      break;
	    }
	  }
	}
      }
    }
    
    return result;
  }

  /**
   * Puts the content of the provided spreadsheet on the right.
   * 
   * @param other		the spreadsheet to merge with
   */
  @Override
  public void mergeWith(SpreadSheet other) {
    int[]	indices;
    int		i;
    int		numCols;
    
    numCols = getColumnCount();
    
    super.mergeWith(other);
    
    if (other instanceof Dataset) {
      indices = ((Dataset) other).getClassAttributeIndices();
      for (i = 0; i < indices.length; i++)
	setClassAttribute(numCols + indices[i], true);
    }
  }
  
  /**
   * Returns a spreadsheet containing only the input columns, not class
   * columns.
   * 
   * @return		the input features, null if data conists only of class columns
   */
  public SpreadSheet getInputs() {
    SpreadSheet		result;
    TIntArrayList	indices;
    int			i;
    Row			newRow;
    
    if (m_ClassAttributes.size() == 0)
      return getClone();
    else if (m_ClassAttributes.size() == getColumnCount())
      return null;

    // determine indices
    indices = new TIntArrayList();
    for (i = 0; i < getColumnCount(); i++) {
      if (!isClassAttribute(i))
	indices.add(i);
    }
    
    result = newInstance();
    
    // header
    newRow = result.getHeaderRow();
    for (i = 0; i < indices.size(); i++)
      newRow.addCell("" + i).assign(getHeaderRow().getCell(indices.get(i)));
    
    // data
    for (Row row: rows()) {
      newRow = result.addRow();
      for (i = 0; i < indices.size(); i++) {
	if (row.hasCell(indices.get(i)))
	  newRow.addCell(i).assign(row.getCell(indices.get(i)));
      }
    }
    
    return result;
  }
  
  /**
   * Returns a spreadsheet containing only output columns, i.e., the class
   * columns.
   * 
   * @return		the output features, null if data has no class columns
   */
  public SpreadSheet getOutputs() {
    SpreadSheet		result;
    TIntArrayList	indices;
    int			i;
    Row			newRow;
    
    if (m_ClassAttributes.size() == 0)
      return null;
    else if (m_ClassAttributes.size() == getColumnCount())
      return getClone();

    // determine indices
    indices = new TIntArrayList();
    for (i = 0; i < getColumnCount(); i++) {
      if (isClassAttribute(i))
	indices.add(i);
    }
    
    result = newInstance();
    
    // header
    newRow = result.getHeaderRow();
    for (i = 0; i < indices.size(); i++)
      newRow.addCell("" + i).assign(getHeaderRow().getCell(indices.get(i)));
    
    // data
    for (Row row: rows()) {
      newRow = result.addRow();
      for (i = 0; i < indices.size(); i++) {
	if (row.hasCell(indices.get(i)))
	  newRow.addCell(i).assign(row.getCell(indices.get(i)));
      }
    }
    
    return result;
  }
}
