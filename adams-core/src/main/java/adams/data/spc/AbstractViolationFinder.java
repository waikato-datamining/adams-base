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
 * AbstractViolationFinder.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.spc;

import adams.core.option.AbstractOptionHandler;
import adams.flow.container.ControlChartContainer;

/**
 * Ancestor for algorithms that check for violations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractViolationFinder
  extends AbstractOptionHandler
  implements ViolationFinder {

  private static final long serialVersionUID = 4850346604018127672L;

  /**
   * Check method before locating violations.
   *
   * @param cont	the container to check
   */
  protected void check(ControlChartContainer cont) {
    if (!cont.isValid())
      throw new IllegalStateException("Container is not valid: " + cont);
  }

  /**
   * Performs the actual finding.
   *
   * @param cont	the container to check for violations
   * @return		the new and updated container
   */
  protected abstract ControlChartContainer doFind(ControlChartContainer cont);

  /**
   * Performs the finding.
   *
   * @param cont	the container to check for violations
   * @return		the new and updated container
   */
  public ControlChartContainer find(ControlChartContainer cont) {
    check(cont);
    return doFind(cont);
  }
}
