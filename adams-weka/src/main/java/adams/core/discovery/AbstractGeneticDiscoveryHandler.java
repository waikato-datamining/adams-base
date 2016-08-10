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

import adams.core.discovery.PropertyPath.PropertyContainer;
import adams.core.option.OptionUtils;
import adams.genetic.AbstractGeneticAlgorithm;

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
   * Returns the number of required bits.
   *
   * @return		the number of bits
   */
  public abstract int getNumBits();

  /**
   * Returns the packed bits for the genetic algorithm.
   *
   * @param cont	the container to obtain the value from to turn into a string
   * @return		the bits
   */
  protected abstract String doPack(PropertyContainer cont);

  /**
   * Returns the packed bits for the genetic algorithm.
   *
   * @param cont	the container to obtain the value from to turn into a string
   * @return		the bits
   */
  public String pack(PropertyContainer cont) {
    String	result;

    result = doPack(cont);
    if (isLoggingEnabled())
      getLogger().info(OptionUtils.getCommandLine(cont.getObject()) + "\n--> " + result);

    return result;
  }

  /**
   * Unpacks and applies the bits from the genetic algorithm.
   *
   * @param cont	the container to set the value for created from the string
   * @param bits	the bits to use
   */
  protected abstract void doUnpack(PropertyContainer cont, String bits);

  /**
   * Unpacks and applies the bits from the genetic algorithm.
   *
   * @param cont	the container to set the value for created from the string
   * @param bits	the bits to use
   */
  public void unpack(PropertyContainer cont, String bits) {
    doUnpack(cont, bits);
    if (isLoggingEnabled())
      getLogger().info(bits + "\n--> " + OptionUtils.getCommandLine(cont.getObject()));
  }

  /**
   * Returns whether the handler requires an initialization.
   * <br>
   * Default implementation returns false.
   *
   * @return		true if necessary
   */
  public boolean requiresInitialization() {
    return false;
  }

  /**
   * Gets called for performing the initialization.
   * <br>
   * Default implementation does nothing.
   *
   * @param owner	the owning algorithm
   * @param cont	the property container to update
   */
  public void performInitialization(AbstractGeneticAlgorithm owner, PropertyContainer cont) {
  }
}
