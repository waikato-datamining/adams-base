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
 * WekaNearestNeighborSearchContainer.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for nearest neighbor search (instance and neighborhood).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaNearestNeighborSearchContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 5581530171877321061L;

  /** the identifier for the Instance. */
  public final static String VALUE_INSTANCE = "Instance";

  /** the identifier for the neighborhood. */
  public final static String VALUE_NEIGHBORHOOD = "Neighborhood";

  /** the identifier for the distances. */
  public final static String VALUE_DISTANCES = "Distances";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public WekaNearestNeighborSearchContainer() {
    this(null, null);
  }

  /**
   * Initializes the container with the filter and the associated data.
   *
   * @param inst	the instance that triggered the nearest neighbor search
   * @param hood	the neighborhood
   */
  public WekaNearestNeighborSearchContainer(Instance inst, Instances hood) {
    this(inst, hood, null);
  }

  /**
   * Initializes the container with the filter and the associated data.
   *
   * @param inst	the instance that triggered the nearest neighbor search
   * @param hood	the neighborhood
   * @param distances	the distances, can be null
   */
  public WekaNearestNeighborSearchContainer(Instance inst, Instances hood, double[] distances) {
    super();

    store(VALUE_INSTANCE, inst);
    store(VALUE_NEIGHBORHOOD, hood);
    store(VALUE_DISTANCES, distances);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_INSTANCE, "Instance; " + Instance.class.getName());
    addHelp(VALUE_NEIGHBORHOOD, "Neighborhood; " + Instances.class.getName());
    addHelp(VALUE_DISTANCES, "Distances; " + double[].class.getName());
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

    result.add(VALUE_INSTANCE);
    result.add(VALUE_NEIGHBORHOOD);
    result.add(VALUE_DISTANCES);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return (hasValue(VALUE_INSTANCE) && hasValue(VALUE_NEIGHBORHOOD));
  }
}
