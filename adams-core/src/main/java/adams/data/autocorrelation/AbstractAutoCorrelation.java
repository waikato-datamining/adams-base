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
 * AbstractAutoCorrelation.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.autocorrelation;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for autocorrelatin algorithms..
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAutoCorrelation
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 1369408539876533245L;

  /**
   * Hook method for checks.
   * <br>
   * Default implementation does nothing.
   *
   * @param data	the data to check
   */
  protected void check(double[] data) {
  }

  /**
   * Performs the actual autocorrelation on the data.
   *
   * @param data	the data to process
   * @return		the processed data
   */
  protected abstract double[] doCorrelate(double[] data);

  /**
   * Performs autocorrelation on the data.
   *
   * @param data	the data to process
   * @return		the processed data
   */
  public double[] correlate(double[] data) {
    check(data);
    return doCorrelate(data);
  }
}
