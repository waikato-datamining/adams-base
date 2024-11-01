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
 * Invert.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.columnfinder;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.hash.TIntHashSet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Inverts the selected columns of the provided sub-column-filter.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Invert
  extends AbstractFilteredColumnFinder {

  /** for serialization. */
  private static final long serialVersionUID = -3635836960365586341L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Inverts the columns of the provided sub-column-filter.";
  }

  /**
   * Returns the columns of interest in the spreadsheet.
   * 
   * @param data	the spreadsheet to inspect
   * @return		the columns of interest
   */
  @Override
  protected int[] doFindColumns(SpreadSheet data) {
    TIntArrayList	result;
    TIntHashSet		set;
    int[]		indices;
    int			i;
    Row			header;
    
    result  = new TIntArrayList();
    indices = m_ColumnFinder.findColumns(data);
    set     = new TIntHashSet(indices);
    header  = data.getHeaderRow();
    for (i = 0; i < header.getCellCount(); i++) {
      if (!set.contains(i))
	result.add(i);
    }
    
    return result.toArray();
  }
}
