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
 * AbstractMetaIterator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.cellfinder;

import java.util.Iterator;

/**
 * A meta-iterator that wraps around a base iterator.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMetaIterator
  implements Iterator<CellLocation> {

  /** the base iterator to use. */
  protected Iterator<CellLocation> m_Base;
  
  /**
   * Initializes the iterator.
   * 
   * @param base	the base iterator to use
   */
  public AbstractMetaIterator(Iterator<CellLocation> base) {
    m_Base = base;
  }
  
  /**
   * Returns whether another cell location is available.
   * 
   * @return		true if another is available
   */
  @Override
  public boolean hasNext() {
    return m_Base.hasNext();
  }

  /**
   * Processes the cell location.
   * 
   * @param location	the location to process
   * @return		the processed location
   */
  protected abstract CellLocation process(CellLocation location);
  
  /**
   * Returns the next cell location.
   * 
   * @return		the cell location
   */
  @Override
  public CellLocation next() {
    return process(m_Base.next());
  }

  /**
   * Removes from the underlying collection the last element returned by the
   * iterator (optional operation), if the base iterator supports this.
   */
  @Override
  public void remove() {
    m_Base.remove();
  }
}
