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
 * DataRow.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import adams.event.SpreadSheetColumnInsertionEvent;
import adams.event.SpreadSheetColumnInsertionListener;

/**
 * The interface for data rows.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface DataRow
  extends Row, SpreadSheetColumnInsertionListener {
  
  /**
   * Returns a clone of itself.
   *
   * @param owner	the new owner
   * @return		the clone
   */
  @Override
  public DataRow getClone(SpreadSheet owner);

  /**
   * A column got inserted.
   * 
   * @param e		the insertion event
   */
  @Override
  public void spreadSheetColumnInserted(SpreadSheetColumnInsertionEvent e);
}
