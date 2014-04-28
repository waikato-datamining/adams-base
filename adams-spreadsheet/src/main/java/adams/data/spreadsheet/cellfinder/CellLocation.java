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
 * CellLocation.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.cellfinder;

import java.io.Serializable;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Container object for a cell location (row and column).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CellLocation
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 4387727127432776366L;

  /** the 0-based row. */
  protected int m_Row;

  /** the 0-based column. */
  protected int m_Column;
  
  /**
   * Initializes the position.
   * 
   * @param row		the 0-based row
   * @param col		the 0-based column
   */
  public CellLocation(int row, int col) {
    m_Row    = row;
    m_Column = col;
  }
  
  /**
   * Initializes the position with a cell string, like "A1".
   * 
   * @param position	the cell position
   * @throws Exception	if position is invalid
   */
  public CellLocation(String position) throws Exception {
    int[]	pos;
    
    pos      = SpreadSheet.getCellLocation(position);
    m_Row    = pos[0];
    m_Column = pos[1];
  }
  
  /**
   * Returns the stored row.
   * 
   * @return		the 0-based row
   */
  public int getRow() {
    return m_Row;
  }
  
  /**
   * Returns the stored column.
   * 
   * @return		the 0-based column
   */
  public int getColumn() {
    return m_Column;
  }
  
  /**
   * Returns the cell location as position string, like "A1".
   * 
   * @return		the position
   */
  public String toPosition() {
    return SpreadSheet.getCellPosition(m_Row, m_Column);
  }
  
  /**
   * Returns a short description of the location.
   * 
   * @return		the location as string
   */
  @Override
  public String toString() {
    return m_Row + "," + m_Column;
  }
}
