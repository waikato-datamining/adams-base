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
import adams.genetic.AbstractGeneticAlgorithm;

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
   * Gets called when the genetic algorithm starts.
   */
  public abstract void start();

  /**
   * Hook method for checking the algorithm before attempting the actual stop check.
   * <br>
   * Default implementation only ensures that an algorithm was provided.
   *
   * @param genetic		the algorithm to check
   */
  protected void check(AbstractGeneticAlgorithm genetic) {
    if (genetic == null)
      throw new IllegalArgumentException("No genetic algorithm provided!");
  }

  /**
   * Performs the actual check of the stopping criterion.
   *
   * @param genetic	the algorithm
   * @return		true if to stop
   */
  protected abstract boolean doCheckStopping(AbstractGeneticAlgorithm genetic);

  /**
   * Performs the check of the stopping criterion.
   *
   * @param genetic	the algorithm
   * @return		true if to stop
   */
  public boolean checkStopping(AbstractGeneticAlgorithm genetic) {
    boolean	result;

    check(genetic);
    result = doCheckStopping(genetic);
    if (isLoggingEnabled())
      getLogger().info("checkStopping: " + result);

    return result;
  }
}
