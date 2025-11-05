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
 * FindCellsIterator.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet;

import adams.core.StoppableWithFeedback;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Locates cells that match a certain string.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FindCellsIterator
  implements Iterator<Cell>, StoppableWithFeedback {

  /** the spreadsheet to search. */
  protected SpreadSheet m_SpreadSheet;

  /** the search parameters. */
  protected FindCellsParameters m_Parameters;

  /** the current row index. */
  protected int m_Row;

  /** the current column index. */
  protected int m_Column;

  /** the next cell. */
  protected Cell m_Next;

  /** whether the search was stopped. */
  protected boolean m_Stopped;

  /**
   * Initializes the iterator.
   *
   * @param spreadSheet 	the spreadsheet to search
   * @param parameters		the search parameters
   */
  public FindCellsIterator(SpreadSheet spreadSheet, FindCellsParameters parameters) {
    m_SpreadSheet = spreadSheet;
    m_Parameters  = parameters;
    m_Row         = -1;
    m_Column      = -1;
    m_Next         = null;
    m_Stopped      = false;
  }

  /**
   * Returns the spreadsheet that is being searched.
   *
   * @return		the spreadsheet
   */
  public SpreadSheet getSpreadSheet() {
    return m_SpreadSheet;
  }

  /**
   * Returns the search string.
   *
   * @return		the search string
   */
  public FindCellsParameters getParameters() {
    return m_Parameters;
  }

  /**
   * Moves to the next cell.
   *
   * @return		the next cell to inspect, null if no more cells
   */
  protected Cell nextCell() {
    Cell	cell;

    // no data?
    if ((m_SpreadSheet.getColumnCount() == 0) || (m_SpreadSheet.getRowCount() == 0))
      return null;

    // traversing first row?
    if (m_Row == -1)
      m_Column = m_SpreadSheet.getColumnCount() - 1;

    while (true) {
      // reached end of row?
      if (m_Column == m_SpreadSheet.getColumnCount() - 1) {
	m_Column = 0;
	m_Row++;
	// traversed all rows?
	if (m_Row >= m_SpreadSheet.getRowCount())
	  return null;
      }
      else {
	m_Column++;
      }
      cell = m_SpreadSheet.getCell(m_Row, m_Column);
      if ((cell != null) && !cell.isMissing()) {
	if (!m_Parameters.search.isEmpty() && cell.isEmpty())
	  continue;
	return cell;
      }
    }
  }

  /**
   * Checks whether the cell is matching the criteria.
   *
   * @param cell	the cell to check
   * @return		true if a match
   */
  protected boolean isMatch(Cell cell) {
    String	content;

    if ((cell == null) || cell.isMissing())
      return false;

    content = cell.getContent();
    if (m_Parameters.regExp) {
      return m_Parameters.pattern.matcher(content).matches();
    }
    else {
      if (!m_Parameters.caseSensitive)
	content = content.toLowerCase();
      return content.contains(m_Parameters.search);
    }
  }

  /**
   * Returns {@code true} if the iteration has more elements.
   * (In other words, returns {@code true} if {@link #next} would
   * return an element rather than throwing an exception.)
   *
   * @return {@code true} if the iteration has more elements
   */
  @Override
  public boolean hasNext() {
    Cell	cell;

    if (m_Next == null) {
      while (!m_Stopped) {
	cell = nextCell();
	if (cell != null) {
	  if (isMatch(cell)) {
	    m_Next = cell;
	    break;
	  }
	}
	else {
	  // traversed all cells
	  break;
	}
      }
    }

    return (m_Next != null);
  }

  /**
   * Returns the next element in the iteration.
   *
   * @return the next element in the iteration
   * @throws NoSuchElementException if the iteration has no more elements
   */
  @Override
  public Cell next() {
    Cell	result;

    if (!hasNext())
      throw new NoSuchElementException();

    result = m_Next;
    m_Next = null;

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return true if stopped
   */
  @Override
  public boolean isStopped() {
    return m_Stopped;
  }
}
