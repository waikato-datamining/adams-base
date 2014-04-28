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
 * RangeIterator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.cellfinder;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates over a range of cells.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RangeIterator 
  implements Iterator<CellLocation> {

  /** the row indices. */
  protected int[] m_Rows;
  
  /** the column indices. */
  protected int[] m_Columns;
  
  /** the current row (index). */
  protected int m_Row;
  
  /** the current column (index). */
  protected int m_Column;
  
  /**
   * Initializes the iterator.
   * 
   * @param rows	the row indices of the range
   * @param cols	the column indices of the range
   */
  public RangeIterator(int[] rows, int[] cols) {
    m_Rows    = rows.clone();
    m_Columns = cols.clone();
    m_Row     = -1;
    m_Column  = m_Columns.length - 1;
  }
  
  /**
   * Returns whether another cell location is available.
   * 
   * @return		true if another is available
   */
  @Override
  public boolean hasNext() {
    return !((m_Row == m_Rows.length - 1) && (m_Column == m_Columns.length - 1));
  }

  /**
   * Returns the next cell location.
   * 
   * @return		the cell location
   */
  @Override
  public CellLocation next() {
    if (!hasNext())
      throw new NoSuchElementException();
    
    m_Column++;
    if (m_Column == m_Columns.length) {
      m_Row++;
      m_Column = 0;
    }
    
    return new CellLocation(m_Rows[m_Row], m_Columns[m_Column]);
  }

  /**
   * Not supported.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}