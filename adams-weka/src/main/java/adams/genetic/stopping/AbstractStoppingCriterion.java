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
 * AbstractStoppingCriterion.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.genetic.stopping;

import adams.core.option.AbstractOptionHandler;
import adams.event.FitnessChangeEvent;

/**
 * Ancestor for stopping criteria.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractStoppingCriterion
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -3789025381945184863L;

  /**
   * Hook method for checking the event before attempting the actual stop check.
   * <br>
   * Default implementation only ensures that event was provided.
   *
   * @param e		the event to check
   */
  protected void check(FitnessChangeEvent e) {
    if (e == null)
      throw new IllegalArgumentException("No fitness event provided!");
  }

  /**
   * Performs the actual check of the stopping criterion.
   *
   * @param e		the event
   * @return		true if to stop
   */
  protected abstract boolean doCheckStopping(FitnessChangeEvent e);

  /**
   * Performs the check of the stopping criterion.
   *
   * @param e		the event
   * @return		true if to stop
   */
  public boolean checkStopping(FitnessChangeEvent e) {
    check(e);
    return doCheckStopping(e);
  }
}
