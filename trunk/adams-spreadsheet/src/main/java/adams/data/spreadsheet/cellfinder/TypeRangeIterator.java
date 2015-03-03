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
 * TypeRangeIterator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.cellfinder;

import java.util.Iterator;
import java.util.NoSuchElementException;

import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Iterates over a range of cells, looking for a type of cell.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TypeRangeIterator 
  implements Iterator<CellLocation> {

  /** the spreadsheet to process. */
  protected SpreadSheet m_Sheet;
  
  /** the cell type to look for. */
  protected ContentType m_Type;
  
  /** the row indices. */
  protected int[] m_Rows;
  
  /** the column indices. */
  protected int[] m_Columns;
  
  /** the current row (index). */
  protected int m_Row;
  
  /** the current column (index). */
  protected int m_Column;
  
  /** whether we finished searching. */
  protected boolean m_Finished;
  
  /** the next location. */
  protected int[] m_Next;
  
  /**
   * Initializes the iterator.
   * 
   * @param rows	the row indices of the range
   * @param cols	the column indices of the range
   */
  public TypeRangeIterator(SpreadSheet sheet, ContentType type, int[] rows, int[] cols) {
    m_Sheet    = sheet;
    m_Type     = type;
    m_Rows     = rows.clone();
    m_Columns  = cols.clone();
    m_Row      = -1;
    m_Column   = m_Columns.length - 1;
    m_Finished = false;
    m_Next     = null;
  }
  
  /**
   * Find the next location.
   */
  protected void findNext() {
    if (m_Finished)
      return;
    
    while (!m_Finished && (m_Next == null)) {
      // next cell
      m_Column++;
      if (m_Column == m_Columns.length) {
	m_Row++;
	m_Column = 0;
      }

      // correct cell type?
      if (m_Sheet.hasCell(m_Rows[m_Row], m_Columns[m_Column]) && (m_Sheet.getCell(m_Rows[m_Row], m_Columns[m_Column]).getContentType() == m_Type))
	m_Next = new int[]{m_Rows[m_Row], m_Columns[m_Column]};

      // reached last cell?
      m_Finished = ((m_Row == m_Rows.length - 1) && (m_Column == m_Columns.length - 1));
      if (m_Finished)
	m_Sheet = null;
    }
  }
  
  /**
   * Returns whether another cell location is available.
   * 
   * @return		true if another is available
   */
  @Override
  public boolean hasNext() {
    if (m_Next == null)
      findNext();
    return (m_Next != null);
  }

  /**
   * Returns the next cell location.
   * 
   * @return		the cell location
   */
  @Override
  public CellLocation next() {
    CellLocation	result;
    
    if (!hasNext())
      throw new NoSuchElementException();
    
    result = new CellLocation(m_Next[0], m_Next[1]);
    m_Next = null;
    
    return result;
  }

  /**
   * Not supported.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}