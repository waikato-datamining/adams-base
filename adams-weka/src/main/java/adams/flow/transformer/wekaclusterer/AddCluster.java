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
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;

import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class AddCluster
  extends AbstractClusterMembershipPostProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 5983792992620091051L;

  public static final String PREDICTED_CLUSTER = "Predicted cluster";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Just adds the predicted cluster to the original dataset.\n"
	+ "Stored in container under: " + VALUE_CLUSTERED_DATASET;
  }

  /**
   * Performs some form of processing on the full dataset.
   */
  @Override
  protected Instances processDatasetWithClusterer(Instances data, Clusterer clusterer) {
    Instances			result;
    Add				add;
    int				i;
    int				cluster;

    add = new Add();
    add.setAttributeType(new SelectedTag(Attribute.NUMERIC, Add.TAGS_TYPE));
    add.setAttributeName(PREDICTED_CLUSTER);
    add.setAttributeIndex("" + (data.numAttributes() + 1));
    try {
      add.setInputFormat(data);
      result = Filter.useFilter(data, add);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to add attribute '" + PREDICTED_CLUSTER + "' to the dataset!", e);
      result = null;
    }

    if (result != null) {
    for (i = 0; i < data.numInstances(); i++) {
      try {
        cluster = clusterer.clusterInstance(data.instance(i));
        result.instance(i).setValue(result.numAttributes() - 1, cluster);
      }
      catch (Exception e) {
        getLogger().log(Level.SEVERE, "Failed to cluster instance #" + (i+1) + "!", e);
      }
    }
    }

    return result;
  }
}
