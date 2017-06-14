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
 * AbstractSpreadSheetFilter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet.filter;

import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for spreadsheet filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSpreadSheetFilter
  extends AbstractOptionHandler
  implements SpreadSheetFilter {

  private static final long serialVersionUID = 7811561046971207234L;

  /**
   * Hook method for checks.
   *
   * @param data	the spreadsheet to filter
   * @return		the filtered spreadsheet
   */
  protected String check(SpreadSheet data) {
    if (data == null)
      return "No spreadsheet provided!";
    return null;
  }

  /**
   * Performs the actual filtering of the spreadsheet.
   *
   * @param data	the spreadsheet to filter
   * @return		the filtered spreadsheet
   * @throws Exception	if filtering fails
   */
  protected abstract SpreadSheet doFilter(SpreadSheet data) throws Exception;

  /**
   * Filters the spreadsheet.
   *
   * @param data	the spreadsheet to filter
   * @return		the filtered spreadsheet
   * @throws Exception	if filtering fails
   */
  public SpreadSheet filter(SpreadSheet data) throws Exception {
    String	msg;

    msg = check(data);
    if (msg != null)
      throw new IllegalArgumentException(msg);

    return doFilter(data);
  }
}
