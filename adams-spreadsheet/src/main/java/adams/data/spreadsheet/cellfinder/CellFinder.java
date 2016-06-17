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
 * CellFinder.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.cellfinder;

import adams.core.QuickInfoSupporter;
import adams.data.spreadsheet.SpreadSheet;

import java.util.Iterator;

/**
 * Interface for classes that locate cells of interest in a spreadsheet.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface CellFinder
  extends QuickInfoSupporter {

  /**
   * Locates the cells in the spreadsheet.
   * 
   * @param sheet	the sheet to locate the cells in
   * @return		the iterator over the locations
   */
  public Iterator<CellLocation> findCells(SpreadSheet sheet);
}
