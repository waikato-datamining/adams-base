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
 * TrainableSpreadSheetFilter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet.filter;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Interface for filters that can be trained.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface TrainableSpreadSheetFilter
  extends SpreadSheetFilter {

  /**
   * Invalidates the training.
   */
  public void resetFilter();

  /**
   * Whether the filter has already been trained.
   *
   * @return		true if trained
   */
  public boolean isTrained();

  /**
   * (Re-)Trains on the spreadsheet.
   *
   * @param data	the spreadsheet to train with and filter
   * @return		the filtered spreadsheet
   * @throws Exception	if training fails
   */
  public SpreadSheet train(SpreadSheet data) throws Exception;
}
