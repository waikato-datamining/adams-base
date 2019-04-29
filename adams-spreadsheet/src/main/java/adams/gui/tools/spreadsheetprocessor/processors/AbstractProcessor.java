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
 * AbstractProcessor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor.processors;

import adams.core.MessageCollection;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.tools.spreadsheetprocessor.AbstractWidget;

/**
 * Ancestor for processors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractProcessor
  extends AbstractWidget {

  private static final long serialVersionUID = -6213610147417060823L;

  /**
   * Hook method for performing checks.
   *
   * @param data	the data to check
   * @param errors	for storing errors
   */
  protected void check(SpreadSheet data, MessageCollection errors) {
    if (data == null)
      errors.add("No data provided!");
  }

  /**
   * Processes the data.
   *
   * @param data	the input data
   * @param errors	for storing errors
   * @return		the generated data, null in case of an error
   */
  protected abstract SpreadSheet doProcess(SpreadSheet data, MessageCollection errors);

  /**
   * Processes the data.
   *
   * @param data	the input data
   * @param errors	for storing errors
   * @return		the generated data, null in case of an error
   */
  public SpreadSheet process(SpreadSheet data, MessageCollection errors) {
    SpreadSheet		result;

    result = null;

    check(data, errors);
    if (errors.isEmpty())
      result = doProcess(data, errors);

    if (errors.isEmpty())
      return result;
    else
      return null;
  }
}
