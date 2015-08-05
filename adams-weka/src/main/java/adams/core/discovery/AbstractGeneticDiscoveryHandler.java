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
 * AbstractGeneticDiscoveryHandler.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

/**
 * Ancestor for genetic algorithm related discovery handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGeneticDiscoveryHandler
  extends AbstractDiscoveryHandler {

  private static final long serialVersionUID = 9187636596983559404L;

  /**
   * Returns the packed bits for the genetic algorithm.
   *
   * @return		the bits
   */
  public abstract String pack();

  /**
   * Unpacks and applies the bits from the genetic algorithm.
   *
   * @param bits	the bits to use
   */
  public abstract void unpack(String bits);
}
