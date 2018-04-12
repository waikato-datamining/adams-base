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
 * AbstractSummaryStatistic.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.summarystatistics;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for summary statistics.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSummaryStatistic
  extends AbstractOptionHandler
  implements SummaryStatistic {

  private static final long serialVersionUID = 7599874755655659367L;

  /**
   * Clears all input.
   * <br>
   * Default implementation does nothing.
   */
  public void clear() {
  }

  /**
   * Hook method for performing checks before calculating statistic.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if successful, otherwise error message
   */
  protected String check() {
    return null;
  }

  /**
   * Calculates the summary statistics.
   *
   * @return		the statistics
   */
  protected abstract double[] doCalculate();

  /**
   * Calculates the summary statistics.
   *
   * @return		the statistics
   */
  public double[] calculate() {
    String	msg;

    msg = check();
    if (msg != null)
      throw new IllegalStateException(msg);
    return doCalculate();
  }
}
