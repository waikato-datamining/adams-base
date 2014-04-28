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
 * ByValue.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowfinder;

import gnu.trove.list.array.TIntArrayList;
import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

/**
 * Returns indices of rows which label match the regular expression.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByValue
  extends AbstractRowFinder {

  /** for serialization. */
  private static final long serialVersionUID = 2989233908194930918L;
  
  /** the attribute index to work on. */
  protected SpreadSheetColumnIndex m_AttributeIndex;
  
  /** the regular expression to match the labels against. */
  protected BaseRegExp m_RegExp;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the indices of rows of columns which values match the provided regular expression.";
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
	    "reg-exp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));
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
   * Sets the regular expression to use.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression in use.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String regExpTipText() {
    return "The regular expression to match the column's values against.";
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
    result += QuickInfoHelper.toString(this, "regExp", m_RegExp, ", regexp: ");
    
    return result;
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
    int			index;
    Row			row;
    Cell		cell;
    
    result = new TIntArrayList();
    
    m_AttributeIndex.setSpreadSheet(data);
    index = m_AttributeIndex.getIntIndex();
    if (index == -1)
      throw new IllegalStateException("Invalid index '" + m_AttributeIndex.getIndex() + "'?");
    if (data.isNumeric(index))
      throw new IllegalStateException("Column at index '" + m_AttributeIndex.getIndex() + "' is numeric!");
    
    for (i = 0; i < data.getRowCount(); i++) {
      row = data.getRow(i);
      if (!row.hasCell(index))
	continue;
      cell = row.getCell(index);
      if (cell.isMissing())
	continue;
      if (cell.isNumeric())
	continue;
      if (m_RegExp.isMatch(cell.getContent()))
	result.add(i);
    }
    
    return result.toArray();
  }
}
