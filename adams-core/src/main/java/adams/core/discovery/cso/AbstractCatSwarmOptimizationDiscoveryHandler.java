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
 * AbstractCatSwarmOptimizationDiscoveryHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery.cso;

import adams.core.Randomizable;
import adams.core.Utils;
import adams.core.discovery.AbstractDiscoveryHandler;
import adams.core.discovery.PropertyPath.PropertyContainer;
import adams.core.option.OptionUtils;
import adams.opt.cso.AbstractCatSwarmOptimization;

import java.util.Random;

/**
 * Ancestor for CSO algorithm related discovery handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCatSwarmOptimizationDiscoveryHandler
  extends AbstractDiscoveryHandler
  implements Randomizable {

  private static final long serialVersionUID = 9187636596983559404L;

  /** the seed value. */
  protected long m_Seed;

  /** the random number generator in use. */
  protected Random m_Random;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "seed", "seed",
      1L);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Random = new Random(m_Seed);
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value to use for initializing the random number generator";
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
  public void performInitialization(AbstractCatSwarmOptimization owner, PropertyContainer cont) {
  }

  /**
   * Returns the number of dimensions (= double values) this scheme requires.
   *
   * @return		the dimensions
   */
  public abstract int getDimensions();

  /**
   * Returns the random double values for the swarm to initialize with.
   *
   * @return		the random double values
   */
  public abstract double[] random();

  /**
   * Returns the double values for the swarm.
   *
   * @param cont	the container to obtain the value from to turn into a double
   * @return		the double values
   */
  protected abstract double[] doObtain(PropertyContainer cont);

  /**
   * Returns the double values for the swarm.
   *
   * @param cont	the container to obtain the value from to turn into a double
   * @return		the double values
   */
  public double[] obtain(PropertyContainer cont) {
    double[]	result;

    result = doObtain(cont);
    if (isLoggingEnabled())
      getLogger().info(OptionUtils.getCommandLine(cont.getObject()) + "\n--> " + Utils.arrayToString(result));

    return result;
  }

  /**
   * Applies the values from the swarm.
   *
   * @param cont	the container to set the value for
   * @param value	the double values
   */
  protected abstract void doApply(PropertyContainer cont, double[] value);

  /**
   * Applies the values from the swarm.
   *
   * @param cont	the container to set the value for
   * @param value	the double values
   */
  public void apply(PropertyContainer cont, double[] value) {
    doApply(cont, value);
    if (isLoggingEnabled())
      getLogger().info(Utils.arrayToString(value) + "\n--> " + OptionUtils.getCommandLine(cont.getObject()));
  }
}
