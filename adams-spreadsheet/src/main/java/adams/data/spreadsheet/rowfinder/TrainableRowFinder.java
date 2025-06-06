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
 * TrainableRowFinder.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowfinder;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Interface for {@link RowFinder} algorithms that can be trained.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface TrainableRowFinder
  extends RowFinder {

  /**
   * Trains the row finder with the specified spreadsheet.
   * 
   * @param data	the training data
   * @return		true if successfully trained
   */
  public boolean trainRowFinder(SpreadSheet data);
  
  /**
   * Checks whether the row finder has been trained.
   * 
   * @return		true if the row finder has been trained already
   */
  public boolean isRowFinderTrained();
}
