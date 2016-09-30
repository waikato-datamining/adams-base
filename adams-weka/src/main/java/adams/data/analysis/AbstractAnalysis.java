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
 * AbstractAnalysis.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.analysis;

import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import weka.core.Instances;

/**
 * Ancestor for data analysis classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAnalysis
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -5262468901500523353L;

  /**
   * Hook method for checks.
   *
   * @param data	the data to check
   */
  protected void check(Instances data) {
    if (data == null)
      throw new IllegalStateException("No data provided!");
  }

  /**
   * Performs the actual analysis.
   *
   * @param data	the data to analyze
   * @return		null if successful, otherwise error message
   * @throws Exception	if analysis fails
   */
  protected abstract String doAnalyze(Instances data) throws Exception;

  /**
   * Performs the data analysis.
   *
   * @param data	the data to analyze
   * @return		null if successful, otherwise error message
   */
  public String analyze(Instances data) {
    check(data);
    try {
      return doAnalyze(data);
    }
    catch (Exception e) {
      return Utils.handleException(this, "Failed to perform analysis!", e);
    }
  }
}
