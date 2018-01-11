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
 * WekaGeneticAlgorithmInitializationContainer.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import adams.opt.genetic.AbstractClassifierBasedGeneticAlgorithm;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for initializing genetic algorithms.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaGeneticAlgorithmInitializationContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 5581530171877321061L;

  /** the identifier for the algorithm instance. */
  public final static String VALUE_ALGORITHM = "Algorithm";

  /** the identifier for the data. */
  public final static String VALUE_DATA = "Data";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public WekaGeneticAlgorithmInitializationContainer() {
    this(null, null);
  }

  /**
   * Initializes the container.
   *
   * @param algorithm	the algorithm to use
   * @param data	the training data
   */
  public WekaGeneticAlgorithmInitializationContainer(AbstractClassifierBasedGeneticAlgorithm algorithm, Instances data) {
    super();

    store(VALUE_ALGORITHM, algorithm);
    store(VALUE_DATA, data);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_ALGORITHM, "genetic algorithm object", AbstractClassifierBasedGeneticAlgorithm.class);
    addHelp(VALUE_DATA, "training data", Instances.class);
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

    result.add(VALUE_ALGORITHM);
    result.add(VALUE_DATA);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_ALGORITHM) && hasValue(VALUE_DATA);
  }
}
