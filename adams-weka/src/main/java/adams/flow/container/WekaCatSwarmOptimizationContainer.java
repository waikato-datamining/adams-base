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

/*
 * WekaCatSwarmOptimizationContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import adams.opt.cso.Measure;
import weka.classifiers.Classifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for Cat Swarm Optimization (CSO) algorithms output (setup, measure, fitness).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaCatSwarmOptimizationContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 5581530171877321061L;

  /** the identifier for the setup. */
  public final static String VALUE_SETUP = "Setup";

  /** the identifier for the measure. */
  public final static String VALUE_MEASURE = "Measure";

  /** the identifier for the fitness. */
  public final static String VALUE_FITNESS = "Fitness";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public WekaCatSwarmOptimizationContainer() {
    this(null);
  }

  /**
   * Initializes the container the setup.
   *
   * @param cls	  	the setup to use
   */
  public WekaCatSwarmOptimizationContainer(Classifier cls) {
    this(cls, null, null);
  }

  /**
   * Initializes the container.
   *
   * @param cls	  	the setup to use
   * @param measure	the measure to use, can be null
   * @param fitness	the fitness to use, can be null
   */
  public WekaCatSwarmOptimizationContainer(Classifier cls, Measure measure, Double fitness) {
    super();

    store(VALUE_SETUP, cls);
    store(VALUE_MEASURE, measure);
    store(VALUE_FITNESS, fitness);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_SETUP, "classifier object; " + Classifier.class.getName());
    addHelp(VALUE_MEASURE, "measure used for fitness; " + Measure.class.getName());
    addHelp(VALUE_FITNESS, "fitness score; " + Double.class.getName());
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String>	result;

    result = new ArrayList<>();

    result.add(VALUE_SETUP);
    result.add(VALUE_MEASURE);
    result.add(VALUE_FITNESS);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_SETUP);
  }
}
