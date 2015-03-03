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
 * AbstractSpreadSheetColumnConverter.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.Index;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

/**
 * Ancestor for column converter schemes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of class to convert the cell values to
 */
public abstract class AbstractSpreadSheetColumnConverter<T>
  extends AbstractInPlaceSpreadSheetConversion {

  /** for serialization. */
  private static final long serialVersionUID = 8681800940519018023L;

  /** the column to process. */
  protected SpreadSheetColumnIndex m_Column;

  /** whether to keep cell values of failed conversions rather than setting them to zero. */
  protected boolean m_KeepFailed;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "column", "column",
	    new SpreadSheetColumnIndex(Index.FIRST));

    m_OptionManager.add(
	    "keep-failed", "keepFailed",
	    false);
  }

  /**
   * Sets the column to convert.
   *
   * @param value	the index
   */
  public void setColumn(SpreadSheetColumnIndex value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column to convert.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String columnTipText();

  /**
   * Sets whether to keep cells with failed conversion rather than setting 
   * them to missing.
   *
   * @param value	if true failed cells are kept
   */
  public void setKeepFailed(boolean value) {
    m_KeepFailed = value;
    reset();
  }

  /**
   * Returns whether to keep cells with failed conversion rather than setting
   * them to missing.
   *
   * @return		true if to keep them
   */
  public boolean getKeepFailed() {
    return m_KeepFailed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keepFailedTipText() {
    return 
	"Whether to keep the original value of cells which conversion has "
	+ "failed rather than setting them to missing.";
  }

  /**
   * Prepares the conversion.
   * 
   * @param input	the spreadsheet to convert
   */
  protected void preConvert(SpreadSheet input) {
    m_Column.setSpreadSheet(input);
  }
  
  /**
   * Converts the cell's content to a new format.
   * 
   * @param cellOld	the current cell
   * @param cellNew	the new cell with the converted content
   * @throws Exception	if conversion fails
   */
  protected abstract void convert(Cell cellOld, Cell cellNew) throws Exception;
  
  /**
   * Generates the new spreadsheet from the input.
   * 
   * @param input	the incoming spreadsheet
   * @return		the generated spreadsheet
   * @throws Exception	if conversion fails for some reason
   */
  @Override
  protected SpreadSheet convert(SpreadSheet input) throws Exception {
    SpreadSheet	result;
    int		i;
    int		r;
    int		index;
    Row		newRow;
    Row		row;
    Cell	cell;
    Cell	newCell;
    
    preConvert(input);
    
    if (m_NoCopy)
      result = input;
    else
      result = input.getHeader();
    index = m_Column.getIntIndex();
    
    for (r = 0; r < input.getRowCount(); r++) {
      if (m_Stopped)
	break;
      
      row = input.getRow(r);
      if (m_NoCopy)
	newRow = row;
      else
	newRow = result.addRow();
      for (i = 0; i < input.getColumnCount(); i++) {
	cell = row.getCell(i);
	if (cell == null)
	  continue;
	newCell = newRow.addCell(i);
	if (cell.isMissing()) {
	  newCell.setMissing();
	  continue;
	}
	if (i == index) {
	  try {
	    convert(cell, newCell);
	  }
	  catch (Exception e) {
	    getLogger().severe("Failed to convert value '" + cell.getContent() + "' at " + SpreadSheet.getCellPosition(r, i));
	    if (m_KeepFailed)
	      newCell.assign(cell);
	    else
	      newCell.setMissing();
	  }
	}
	else {
	  newCell.assign(cell);
	}
      }
    }
    
    if (m_Stopped)
      result = null;
    
    return result;
  }
}
