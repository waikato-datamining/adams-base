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
 * AbstractResultsHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekamultiexperimenter.experiment;

import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for classes that store the results from an experiment run.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractResultsHandler
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -8012929412471749484L;

  /**
   * Loads the results (if possible).
   *
   * @return		the results, null if failed to obtain (or not available)
   */
  public abstract SpreadSheet read();

  /**
   * Stores the results.
   *
   * @param results	the results to store
   * @return		null if successful, otherwise error message
   */
  public abstract String write(SpreadSheet results);
}
