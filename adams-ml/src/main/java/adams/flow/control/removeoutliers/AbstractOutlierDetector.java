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
 * AbstractOutlierDetector.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.removeoutliers;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

import java.util.Set;

/**
 * Ancestor for outlier detectors of actual vs predicted.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOutlierDetector
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = -2791096934947903275L;

  /** the last error that occurred. */
  protected String m_LastError;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns whether an occurred during the last detection.
   *
   * @return		true if an error occurred
   */
  public boolean hasLastError() {
    return (m_LastError != null);
  }

  /**
   * Returns any error that occurred during the last detection.
   *
   * @return		the error, null if none occurred
   */
  public String getLastError() {
    return m_LastError;
  }

  /**
   * Check method before detection.
   *
   * @param sheet	the spreadsheet to analyze
   * @param actual	the column with the actual values
   * @param predicted	the column with the predicted values
   * @return		null if check passed, otherwise error message
   */
  public String check(SpreadSheet sheet, SpreadSheetColumnIndex actual, SpreadSheetColumnIndex predicted) {
    if (sheet == null)
      return "No spreadsheet provided!";

    actual.setData(sheet);
    if (actual.getIntIndex() == -1)
      return "'Actual' column not found in spreadsheet: " + actual;

    predicted.setData(sheet);
    if (predicted.getIntIndex() == -1)
      return "'Predicted' column not found in spreadsheet: " + predicted;

    return null;
  }

  /**
   * Performs the actual detection of the outliers.
   *
   * @param sheet	the spreadsheet to analyze
   * @param actual	the column with the actual values
   * @param predicted	the column with the predicted values
   * @return		the row indices of the outliers
   */
  protected abstract Set<Integer> doDetect(SpreadSheet sheet, SpreadSheetColumnIndex actual, SpreadSheetColumnIndex predicted);

  /**
   * For detecting outliers.
   *
   * @param sheet	the spreadsheet to analyze
   * @param actual	the column with the actual values
   * @param predicted	the column with the predicted values
   * @return		the row indices of the outliers, null in case of an error
   */
  public Set<Integer> detect(SpreadSheet sheet, SpreadSheetColumnIndex actual, SpreadSheetColumnIndex predicted) {
    m_LastError = check(sheet, actual, predicted);
    if (m_LastError == null) {
      return doDetect(sheet, actual, predicted);
    }
    else {
      getLogger().severe(m_LastError);
      return null;
    }
  }
}
