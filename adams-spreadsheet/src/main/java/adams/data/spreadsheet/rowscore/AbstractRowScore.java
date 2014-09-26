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
 * AbstractRowScore.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet.rowscore;

import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for algorithms that calculate a score for a spreadsheet row.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9765 $
 */
public abstract class AbstractRowScore
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 3225015615927453223L;
  
  /** the last error that was generated. */
  protected String m_LastError;

  /**
   * Checks whether there was an error with the last stats generation.
   *
   * @return		true if there was an error
   * @see		#getLastError()
   */
  public boolean hasLastError() {
    return (m_LastError != null);
  }

  /**
   * Returns the last error that occurred.
   *
   * @return		the last error, null if none occurred
   */
  public String getLastError() {
    return m_LastError;
  }

  /**
   * Checks whether the spreadsheet can be handled.
   *
   * @param sheet	the spreadsheet to check
   * @param rowIndex	the row index
   * @return		null if everythin ok, otherwise error message
   */
  protected String check(SpreadSheet sheet, int rowIndex) {
    String	result;

    result = null;

    if (sheet == null)
      result = "No data provided!";

    if (result == null) {
      if (rowIndex >= sheet.getRowCount())
	result = "Row index out of bounds: " + rowIndex;
    }

    return result;
  }

  /**
   * Performs the actual calculation of the row score.
   *
   * @param sheet	the spreadsheet to generate the score for
   * @param rowIndex	the row index
   * @return		the generated score, null in case of an error
   */
  protected abstract Double doCalculateScore(SpreadSheet sheet, int rowIndex);

  /**
   * Performs the calculation of the row score.
   *
   * @param sheet	the spreadsheet to generate the score for
   * @param rowIndex	the row index
   * @return		the generated score, null in case of an error
   */
  public Double calculateScore(SpreadSheet sheet, int rowIndex) {
    Double 	result;

    result = null;

    m_LastError = check(sheet, rowIndex);
    if (m_LastError == null) {
      result = doCalculateScore(sheet, rowIndex);
      if (result == null) {
	if (m_LastError == null)
	  m_LastError = "Error occurred calculating score!";
      }
    }

    return result;
  }
}
