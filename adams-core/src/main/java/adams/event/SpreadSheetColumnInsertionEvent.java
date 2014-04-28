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
 * SpreadSheetColumnInsertionEvent.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.event;

import java.util.EventObject;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Event that gets sent in case of insertion of a column.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetColumnInsertionEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -7245821885375091558L;
  
  /** the index of the column that got inserted. */
  protected int m_ColumnIndex;
  
  /**
   * Initializes the event.
   * 
   * @param source	the spreadsheet that triggered the event
   * @param columnIndex	te index of the column that got inserted
   */
  public SpreadSheetColumnInsertionEvent(SpreadSheet source, int columnIndex) {
    super(source);
    m_ColumnIndex = columnIndex;
  }

  /**
   * Returns the spreadsheet that triggered this event.
   * 
   * @return		the spreadsheet
   */
  public SpreadSheet getSpreadSheet() {
    return (SpreadSheet) getSource();
  }
  
  /**
   * Returns the index of the column that got inserted.
   * 
   * @return		the index
   */
  public int getColumnIndex() {
    return m_ColumnIndex;
  }
  
  /**
   * Returns a short description of the event.
   * 
   * @return		the description
   */
  @Override
  public String toString() {
    return "spreadsheet=" + getSpreadSheet().hashCode() + ", columnIndex=" + getColumnIndex();
  }
}
