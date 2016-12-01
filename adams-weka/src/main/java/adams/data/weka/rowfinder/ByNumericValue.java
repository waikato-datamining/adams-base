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
 * ByNumericValue.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.rowfinder;

import adams.core.Index;
import adams.data.weka.WekaAttributeIndex;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Returns indices of rows which numeric value match the min/max.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByNumericValue
  extends AbstractRowFinder {

  /** for serialization. */
  private static final long serialVersionUID = 235661615457187608L;

  /** the placeholder for NaN. */
  public final static String NAN = "NaN";
  
  /** the attribute index to work on. */
  protected WekaAttributeIndex m_AttributeIndex;
  
  /** the minimum value. */
  protected double m_Minimum;
  
  /** whether the minimum value is included. */
  protected boolean m_MinimumIncluded;
  
  /** the maximum value. */
  protected double m_Maximum;
  
  /** whether the maximum value is included. */
  protected boolean m_MaximumIncluded;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the indices of rows of columns which values fall inside the minimum and maximum.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "att-index", "attributeIndex",
	    new WekaAttributeIndex(Index.LAST));

    m_OptionManager.add(
	    "minimum", "minimum",
	    Double.NaN);

    m_OptionManager.add(
	    "minimum-included", "minimumIncluded",
	    false);

    m_OptionManager.add(
	    "maximum", "maximum",
	    Double.NaN);

    m_OptionManager.add(
	    "maximum-included", "maximumIncluded",
	    false);
  }

  /**
   * Sets the index of the column to perform the matching on.
   *
   * @param value	the index
   */
  public void setAttributeIndex(WekaAttributeIndex value) {
    m_AttributeIndex = value;
    reset();
  }

  /**
   * Returns the index of the column to perform the matching on.
   *
   * @return		the index
   */
  public WekaAttributeIndex getAttributeIndex() {
    return m_AttributeIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String attributeIndexTipText() {
    return "The index of the column to use for matching; " + m_AttributeIndex.getExample();
  }

  /**
   * Sets the minimum.
   *
   * @param value	the minimum
   */
  public void setMinimum(double value) {
    m_Minimum = value;
    reset();
  }

  /**
   * Returns the minimum.
   *
   * @return		the minimum
   */
  public double getMinimum() {
    return m_Minimum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minimumTipText() {
    return 
	"The minimum value that the values must satisfy; use " + NAN + " (not a "
	+ "number) to ignore minimum.";
  }

  /**
   * Sets whether to exclude the minimum.
   *
   * @param value	true to exclude minimum
   */
  public void setMinimumIncluded(boolean value) {
    m_MinimumIncluded = value;
    reset();
  }

  /**
   * Returns whether the minimum is included.
   *
   * @return		true if minimum included
   */
  public boolean getMinimumIncluded() {
    return m_MinimumIncluded;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minimumIncludedTipText() {
    return "If enabled, then the minimum value gets included (testing '<=' rather than '<').";
  }
  
  /**
   * Sets the maximum.
   *
   * @param value	the maximum
   */
  public void setMaximum(double value) {
    m_Maximum = value;
    reset();
  }

  /**
   * Returns the maximum.
   *
   * @return		the maximum
   */
  public double getMaximum() {
    return m_Maximum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maximumTipText() {
    return 
	"The maximum value that the values must satisfy; use " + NAN + " (not a "
	+ "number) to ignore maximum.";
  }

  /**
   * Sets whether to exclude the maximum.
   *
   * @param value	true to exclude maximum
   */
  public void setMaximumIncluded(boolean value) {
    m_MaximumIncluded = value;
    reset();
  }

  /**
   * Returns whether the maximum is included.
   *
   * @return		true if maximum included
   */
  public boolean getMaximumIncluded() {
    return m_MaximumIncluded;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maximumIncludedTipText() {
    return "If enabled, then the maximum value gets included (testing '>=' rather than '>').";
  }

  /**
   * 
   * Returns the rows of interest in the spreadsheet.
   * 
   * @param data	the spreadsheet to inspect
   * @return		the rows of interest
   */
  @Override
  protected int[] doFindRows(Instances data) {
    TIntArrayList	result;
    int			i;
    int			index;
    Instance 		row;
    double		value;
    boolean		add;
    
    result = new TIntArrayList();
    
    m_AttributeIndex.setData(data);
    index = m_AttributeIndex.getIntIndex();
    if (index == -1)
      throw new IllegalStateException("Invalid index '" + m_AttributeIndex.getIndex() + "'?");
    if (!data.attribute(index).isNumeric())
      throw new IllegalStateException("Column at index '" + m_AttributeIndex.getIndex() + "' is not numeric!");
    
    for (i = 0; i < data.numInstances(); i++) {
      row = data.instance(i);
      if (!row.isMissing(index))
	continue;

      value = row.value(index);

      add = true;
      if (!Double.isNaN(m_Minimum)) {
	if (m_MinimumIncluded) {
	  if (value < m_Minimum)
	    add = false;
	}
	else {
	  if (value <= m_Minimum)
	    add = false;
	}
      }
      if (!Double.isNaN(m_Maximum)) {
	if (m_MaximumIncluded) {
	  if (value > m_Maximum)
	    add = false;
	}
	else {
	  if (value >= m_Maximum)
	    add = false;
	}
      }
      if (add)
	result.add(i);
    }
    
    return result.toArray();
  }
}
