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
 * ViolationFinder.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.spc;

import adams.core.option.OptionHandler;

/**
 * Interface for algorithms that check for violations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ViolationFinder
  extends OptionHandler {

  /**
   * Performs the finding.
   *
   * @param data	the data to check
   * @param limits	the limits for the data
   * @return		the indices of the violations
   */
  public int[] find(double[] data, Limits[] limits);
}
