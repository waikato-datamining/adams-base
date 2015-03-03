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
 * ByStringComparison.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowfinder;

import gnu.trove.list.array.TIntArrayList;
import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

/**
 * Returns indices of rows which string value match the min/max.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10109 $
 */
public class ByStringComparison
  extends AbstractRowFinder {

  /** for serialization. */
  private static final long serialVersionUID = 235661615457187608L;
  
  /** the attribute index to work on. */
  protected SpreadSheetColumnIndex m_AttributeIndex;
  
  /** the minimum value. */
  protected String m_Minimum;
  
  /** whether the minimum value is included. */
  protected boolean m_MinimumIncluded;
  
  /** the maximum value. */
  protected String m_Maximum;
  
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
	    new SpreadSheetColumnIndex(Index.LAST));

    m_OptionManager.add(
	    "minimum", "minimum",
	    "");

    m_OptionManager.add(
	    "minimum-included", "minimumIncluded",
	    false);

    m_OptionManager.add(
	    "maximum", "maximum",
	    "");

    m_OptionManager.add(
	    "maximum-included", "maximumIncluded",
	    false);
  }

  /**
   * Sets the index of the column to perform the matching on.
   *
   * @param value	the index
   */
  public void setAttributeIndex(SpreadSheetColumnIndex value) {
    m_AttributeIndex = value;
    reset();
  }

  /**
   * Returns the index of the column to perform the matching on.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getAttributeIndex() {
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
  public void setMinimum(String value) {
    m_Minimum = value;
    reset();
  }

  /**
   * Returns the minimum.
   *
   * @return		the minimum
   */
  public String getMinimum() {
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
	"The minimum string value that the values must satisfy; use empty "
	+ "string to ignore minimum.";
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
  public void setMaximum(String value) {
    m_Maximum = value;
    reset();
  }

  /**
   * Returns the maximum.
   *
   * @return		the maximum
   */
  public String getMaximum() {
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
	"The maximum string value that the values must satisfy; use empty "
	+ "string to ignore maximum.";
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
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "attributeIndex", m_AttributeIndex, "col: ");
    result += QuickInfoHelper.toString(this, "minimum", (m_Minimum.isEmpty() ? "-none-" : m_Minimum), ", min: ");
    result += " [" + QuickInfoHelper.toString(this, "minimumIncluded", (m_MinimumIncluded ? "incl" : "excl")) + "]";
    result += QuickInfoHelper.toString(this, "maximum", (m_Maximum.isEmpty() ? "-none-" : m_Maximum), ", max: ");
    result += " [" + QuickInfoHelper.toString(this, "maximumIncluded", (m_MaximumIncluded ? "incl" : "excl")) + "]";
    
    return result;
  }

  /**
   * 
   * Returns the rows of interest in the spreadsheet.
   * 
   * @param data	the spreadsheet to inspect
   * @return		the rows of interest
   */
  @Override
  protected int[] doFindRows(SpreadSheet data) {
    TIntArrayList	result;
    int			i;
    int			index;
    Row			row;
    Cell		cell;
    String		value;
    boolean		add;
    
    result = new TIntArrayList();
    
    m_AttributeIndex.setSpreadSheet(data);
    index = m_AttributeIndex.getIntIndex();
    if (index == -1)
      throw new IllegalStateException("Invalid index '" + m_AttributeIndex.getIndex() + "'?");
    
    for (i = 0; i < data.getRowCount(); i++) {
      row = data.getRow(i);
      if (!row.hasCell(index))
	continue;
      cell = row.getCell(index);
      if (cell.isMissing())
	continue;

      value = cell.getContent();

      add = true;
      if (!m_Minimum.isEmpty()) {
	if (m_MinimumIncluded) {
	  if (value.compareTo(m_Minimum) < 0)
	    add = false;
	}
	else {
	  if (value.compareTo(m_Minimum) <= 0)
	    add = false;
	}
      }
      if (!m_Maximum.isEmpty()) {
	if (m_MaximumIncluded) {
	  if (value.compareTo(m_Maximum) > 0)
	    add = false;
	}
	else {
	  if (value.compareTo(m_Maximum) >= 0)
	    add = false;
	}
      }
      if (add)
	result.add(i);
    }
    
    return result.toArray();
  }
}
