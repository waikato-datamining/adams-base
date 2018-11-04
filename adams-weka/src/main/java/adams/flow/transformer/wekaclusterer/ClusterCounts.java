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
 * ClusterCounts.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.wekaclusterer;

import weka.clusterers.Clusterer;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Creates an overview of how many instances get clustered into each cluster.<br>
 * Stored in container under: Clustered dataset
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ClusterCounts
  extends AbstractClusterMembershipPostProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 5983792992620091051L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Creates an overview of how many instances get clustered into each cluster.\n"
	+ "Stored in container under: " + VALUE_CLUSTERED_DATASET;
  }
  
  /**
   * Generates the output format (additional attribute for cluster index).
   * 
   * @param data	the original input data
   * @return		the header of the output format
   */
  protected Instances createOutputFormat(Instances data) {
    Instances			result;
    ArrayList<Attribute>	atts;

    atts = new ArrayList<>();
    atts.add(new Attribute("Cluster"));
    atts.add(new Attribute("Count"));
    result = new Instances(data.relationName(), atts, 0);

    return result;
  }

  /**
   * Performs some form of processing on the full dataset.
   */
  @Override
  protected Instances processDatasetWithClusterer(Instances data, Clusterer clusterer) {
    Instances			result;
    Map<Integer,Integer> 	counts;
    int				i;
    int				cluster;
    List<Integer>		clusters;
    DenseInstance		inst;

    result = createOutputFormat(data);
    counts = new HashMap<>();

    // cluster instances
    for (i = 0; i < data.numInstances(); i++) {
      try {
        cluster = clusterer.clusterInstance(data.instance(i));
        if (!counts.containsKey(cluster))
          counts.put(cluster, 0);
        counts.put(cluster, counts.get(cluster) + 1);
      }
      catch (Exception e) {
        getLogger().log(Level.SEVERE, "Failed to cluster instance #" + (i+1) + "!", e);
      }
    }

    // generate overview
    clusters = new ArrayList<>(counts.keySet());
    Collections.sort(clusters);
    for (i = 0; i < clusters.size(); i++) {
      cluster = clusters.get(i);
      inst = new DenseInstance(1.0, new double[]{cluster, counts.get(cluster)});
      result.add(inst);
    }

    return result;
  }
}
