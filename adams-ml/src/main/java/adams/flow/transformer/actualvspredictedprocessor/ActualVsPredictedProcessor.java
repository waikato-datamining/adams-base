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
 * ActualVsPredictedProcessor.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.actualvspredictedprocessor;

import adams.core.MessageCollection;
import adams.core.option.OptionHandler;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Interface for processors that generate output from a spreadsheet with
 * actual vs predicted data.
 * The 1st column in the spreadsheet contains the actual values and the 2nd the predicted ones.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of output that is generated
 */
public interface ActualVsPredictedProcessor<T>
  extends OptionHandler {

  /**
   * Returns the type of output the processor generates.
   *
   * @return		the type of output
   */
  public Class generates();

  /**
   * Processes the actual vs predicted data and returns
   * the output generated.
   *
   * @param sheet	the data to process
   * @param errors 	for collecting errors
   * @return		the output, null in case of error
   */
  public T process(SpreadSheet sheet, MessageCollection errors);
}
