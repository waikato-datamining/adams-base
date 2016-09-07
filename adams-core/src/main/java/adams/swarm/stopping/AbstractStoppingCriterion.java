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
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.swarm.stopping;

import adams.core.option.AbstractOptionHandler;
import adams.swarm.AbstractCatSwarmOptimization;

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
   * Gets called when the swarm algorithm starts.
   */
  public abstract void start();

  /**
   * Hook method for checking the algorithm before attempting the actual stop check.
   * <br>
   * Default implementation only ensures that an algorithm was provided.
   *
   * @param swarm		the algorithm to check
   */
  protected void check(AbstractCatSwarmOptimization swarm) {
    if (swarm == null)
      throw new IllegalArgumentException("No swarm algorithm provided!");
  }

  /**
   * Performs the actual check of the stopping criterion.
   *
   * @param swarm	the algorithm
   * @return		true if to stop
   */
  protected abstract boolean doCheckStopping(AbstractCatSwarmOptimization swarm);

  /**
   * Performs the check of the stopping criterion.
   *
   * @param swarm	the algorithm
   * @return		true if to stop
   */
  public boolean checkStopping(AbstractCatSwarmOptimization swarm) {
    boolean	result;

    check(swarm);
    result = doCheckStopping(swarm);
    if (isLoggingEnabled())
      getLogger().info("checkStopping: " + result);

    return result;
  }
}
