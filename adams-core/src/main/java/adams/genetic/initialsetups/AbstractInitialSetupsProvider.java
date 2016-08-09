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
 * AbstractInitialSetupsProvider.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.genetic.initialsetups;

import adams.core.option.AbstractOptionHandler;
import adams.genetic.AbstractGeneticAlgorithm;

import java.util.List;

/**
 * Ancestor for providers for initial gene setups.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInitialSetupsProvider<T extends AbstractGeneticAlgorithm>
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -5674507092025702755L;

  /**
   * Provides the initial gene setup.
   *
   * @param owner	the owning the algorithm
   * @return		the genes (0s and 1s)
   */
  public abstract List<int[]> getInitialSetups(T owner);
}
