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
 * WekaClusteringContainer.java
 * Copyright (C) 2009-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import weka.core.Instance;

/**
 * A container for clusterings made by a clusterer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClusteringContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = -4345755816230522577L;

  /** the identifier for the Instance. */
  public final static String VALUE_INSTANCE = "Instance";

  /** the identifier for the Cluster. */
  public final static String VALUE_CLUSTER = "Cluster";

  /** the identifier for the Distribution. */
  public final static String VALUE_DISTRIBUTION = "Distribution";

  /** the identifier for the LogDensity. */
  public final static String VALUE_LOGDENSITY = "LogDensity";

  /** the identifier for the LogDensityPerCluster. */
  public final static String VALUE_LOGDENSITYPERCLUSTER = "LogDensityPerCluster";

  /** the identifier for the LogJointDensities. */
  public final static String VALUE_LOGJOINTDENSITIES = "LogJointDensities";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public WekaClusteringContainer() {
    this(null, -1, new double[0]);
  }

  /**
   * Initializes the container.
   *
   * @param inst	the instance that was used for prediction
   * @param dist	the cluster distribution
   * @param cluster	the chosen cluster
   */
  public WekaClusteringContainer(Instance inst, int cluster, double[] dist) {
    this(inst, cluster, dist, 0, new double[0], new double[0]);
  }

  /**
   * Initializes the container.
   *
   * @param inst			the instance that was used for prediction
   * @param cluster			the chosen cluster
   * @param dist			the cluster distribution
   * @param logDensity			the log density
   * @param logDensityPerCluster	the log density per cluster
   * @param logJointDensities		the log joint densities
   */
  public WekaClusteringContainer(Instance inst, int cluster, double[] dist, double logDensity, double[] logDensityPerCluster, double[] logJointDensities) {
    super();

    if (inst != null)
      store(VALUE_INSTANCE, (Instance) inst.copy());
    store(VALUE_CLUSTER, cluster);
    store(VALUE_DISTRIBUTION, dist.clone());
    if (logDensityPerCluster.length > 0) {
      store(VALUE_LOGDENSITY, logDensity);
      store(VALUE_LOGDENSITYPERCLUSTER, logDensityPerCluster.clone());
      store(VALUE_LOGJOINTDENSITIES, logJointDensities.clone());
    }
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String>	result;

    result = new ArrayList<String>();

    result.add(VALUE_INSTANCE);
    result.add(VALUE_CLUSTER);
    result.add(VALUE_DISTRIBUTION);
    result.add(VALUE_LOGDENSITY);
    result.add(VALUE_LOGDENSITYPERCLUSTER);
    result.add(VALUE_LOGJOINTDENSITIES);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return   (hasValue(VALUE_INSTANCE) && hasValue(VALUE_CLUSTER) && hasValue(VALUE_DISTRIBUTION) && !hasValue(VALUE_LOGDENSITY) && !hasValue(VALUE_LOGDENSITYPERCLUSTER) && !hasValue(VALUE_LOGJOINTDENSITIES))
           | (hasValue(VALUE_INSTANCE) && hasValue(VALUE_CLUSTER) && hasValue(VALUE_DISTRIBUTION) &&  hasValue(VALUE_LOGDENSITY) &&  hasValue(VALUE_LOGDENSITYPERCLUSTER) &&  hasValue(VALUE_LOGJOINTDENSITIES));
  }
}
