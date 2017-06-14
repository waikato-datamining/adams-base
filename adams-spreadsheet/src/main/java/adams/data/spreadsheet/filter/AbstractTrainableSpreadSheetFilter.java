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
 * AbstractTrainableSpreadSheetFilter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet.filter;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for trainable spreadsheet filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTrainableSpreadSheetFilter
  extends AbstractSpreadSheetFilter
  implements TrainableSpreadSheetFilter {

  private static final long serialVersionUID = 3614769696656146304L;

  /** whether the filter has been trained. */
  protected boolean m_Trained;

  /**
   * Invalidates the training.
   */
  public void resetFilter() {
    m_Trained = false;
  }

  /**
   * Returns whether the filter has been trained.
   *
   * @return		true if trained
   */
  @Override
  public boolean isTrained() {
    return m_Trained;
  }

  /**
   * Hook method for checks (training data).
   *
   * @param data	the spreadsheet to filter
   * @return		the filtered spreadsheet
   */
  protected String checkTrain(SpreadSheet data) {
    if (data == null)
      return "No training spreadsheet provided!";
    return null;
  }

  /**
   * Performs the actual retraining on the spreadsheet.
   *
   * @param data	the spreadsheet to train with and filter
   * @return		the filtered spreadsheet
   * @throws Exception	if filtering fails
   */
  protected abstract SpreadSheet doTrain(SpreadSheet data) throws Exception;

  /**
   * (Re-)Trains on the spreadsheet.
   *
   * @param data	the spreadsheet to train with and filter
   * @return		the filtered spreadsheet
   * @throws Exception	if filtering fails
   */
  public SpreadSheet train(SpreadSheet data) throws Exception {
    SpreadSheet	result;
    String	msg;

    msg = checkTrain(data);
    if (msg != null)
      throw new IllegalArgumentException(msg);

    result    = doTrain(data);
    m_Trained = true;

    return result;
  }

  /**
   * Filters the spreadsheet.
   *
   * @param data	the spreadsheet to filter
   * @return		the filtered spreadsheet
   * @throws Exception	if filtering fails
   */
  public SpreadSheet filter(SpreadSheet data) throws Exception {
    if (!isTrained())
      return train(data);
    else
      return super.filter(data);
  }
}
