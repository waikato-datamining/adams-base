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
 * MissingValue.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowfinder;

import gnu.trove.list.array.TIntArrayList;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;

/**
 * Returns indices of rows which label match the regular expression.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MissingValue
  extends AbstractRowFinder {

  /** for serialization. */
  private static final long serialVersionUID = 2163229604979216233L;
  
  /** the attribute index to work on. */
  protected SpreadSheetColumnRange m_AttributeRange;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the indices of rows of columns which have missing values.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "att-range", "attributeRange",
	    new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL));
  }

  /**
   * Sets the range of the columns to inspect.
   *
   * @param value	the range
   */
  public void setAttributeRange(SpreadSheetColumnRange value) {
    m_AttributeRange = value;
    reset();
  }

  /**
   * Returns the range of the columns to inspect.
   *
   * @return		the range
   */
  public SpreadSheetColumnRange getAttributeRange() {
    return m_AttributeRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String attributeRangeTipText() {
    return "The range of the columns to inspect; " + m_AttributeRange.getExample();
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "attributeRange", m_AttributeRange, "cols: ");
  }

  /**
   * Returns the rows of interest in the spreadsheet.
   * 
   * @param data	the spreadsheet to inspect
   * @return		the rows of interest
   */
  @Override
  protected int[] doFindRows(SpreadSheet data) {
    TIntArrayList	result;
    int			i;
    int[]		indices;
    Row			row;
    
    result = new TIntArrayList();
    
    m_AttributeRange.setSpreadSheet(data);
    indices = m_AttributeRange.getIntIndices();
    if (indices.length == 0)
      throw new IllegalStateException("Invalid range '" + m_AttributeRange.getRange() + "'?");
    
    for (i = 0; i < data.getRowCount(); i++) {
      row = data.getRow(i);
      for (int index: indices) {
	if (!row.hasCell(index) || row.getCell(index).isMissing()) {
	  result.add(i);
	  break;
	}
      }
    }
    
    return result.toArray();
  }
}
