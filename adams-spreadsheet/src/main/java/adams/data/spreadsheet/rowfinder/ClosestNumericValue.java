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
 * ClosestNumericValue.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowfinder;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import gnu.trove.list.array.TIntArrayList;

/**
 * Returns the index of the row that comes closest to the provided numeric value.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClosestNumericValue
  extends AbstractRowFinder {

  /** for serialization. */
  private static final long serialVersionUID = 235661615457187608L;

  /**
   * Determines how the search is performed.
   */
  public enum SearchDirection {
    ANY,
    FROM_BELOW,
    FROM_ABOVE,
  }

  /** the attribute index to work on. */
  protected SpreadSheetColumnIndex m_AttributeIndex;
  
  /** the value to get to as close as possible. */
  protected double m_Value;

  /** the search direction. */
  protected SearchDirection m_SearchDirection;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the index of the row that comes closest to the provided numeric value.";
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
	    "value", "value",
	    0.0);

    m_OptionManager.add(
	    "search-direction", "searchDirection",
	    SearchDirection.ANY);
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
   * Sets the value to get closest to.
   *
   * @param value	the value
   */
  public void setValue(double value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the value to get closest to.
   *
   * @return		the value
   */
  public double getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String valueTipText() {
    return "The value to get to as close as possible.";
  }

  /**
   * Sets the search direction to use.
   *
   * @param value	the direction
   */
  public void setSearchDirection(SearchDirection value) {
    m_SearchDirection = value;
    reset();
  }

  /**
   * Returns the search direction to use.
   *
   * @return		the direction
   */
  public SearchDirection getSearchDirection() {
    return m_SearchDirection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String searchDirectionTipText() {
    return
      "Determines how the search is performed: whether the closest value has "
	+ "to be below or above the specified value or doesn't matter.";
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
    result += QuickInfoHelper.toString(this, "value", m_Value, ", value: ");

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
    int			rowIndex;
    Row			row;
    Cell		cell;
    double		value;
    double		diff;
    double		diffNew;
    
    result = new TIntArrayList();
    
    m_AttributeIndex.setSpreadSheet(data);
    index = m_AttributeIndex.getIntIndex();
    if (index == -1)
      throw new IllegalStateException("Invalid index '" + m_AttributeIndex.getIndex() + "'?");
    if (!data.isNumeric(index, true))
      throw new IllegalStateException("Column at index '" + m_AttributeIndex.getIndex() + "' is not numeric!");

    diff     = Double.POSITIVE_INFINITY;
    rowIndex = -1;
    for (i = 0; i < data.getRowCount(); i++) {
      row = data.getRow(i);
      if (!row.hasCell(index))
	continue;
      cell = row.getCell(index);
      if (cell.isMissing())
	continue;
      if (!cell.isNumeric())
	continue;

      value = cell.toDouble();

      // skip values that violate search direction
      switch (m_SearchDirection) {
	case FROM_ABOVE:
	  if (value < m_Value)
	    continue;
	  break;
	case FROM_BELOW:
	  if (value > m_Value)
	    continue;
	  break;
      }

      diffNew = Math.abs(value - m_Value);
      if (diffNew < diff) {
	rowIndex = i;
	diff = diffNew;
      }
    }
    if (rowIndex > -1) {
      result.add(rowIndex);
      if (isLoggingEnabled())
	getLogger().info("Closest row (0-based) to " + m_Value + ": " + rowIndex + " (diff: " + diff + ")");
    }
    else {
      getLogger().warning("No closest row found for value " + m_Value + "!");
    }
    
    return result.toArray();
  }
}
