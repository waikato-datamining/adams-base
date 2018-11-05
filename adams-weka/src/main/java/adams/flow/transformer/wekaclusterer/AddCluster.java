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
 * AddCluster.java
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
 * Just adds the predicted cluster (or distribution) to the original dataset.<br>
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
 * <pre>-output-distribution &lt;boolean&gt; (property: outputDistribution)
 * &nbsp;&nbsp;&nbsp;If enabled, the cluster distribution is output instead of the cluster index.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class AddCluster
  extends AbstractClusterMembershipPostProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 5983792992620091051L;

  public static final String PREDICTED_CLUSTER = "Predicted cluster";

  public static final String PREDICTED_DISTRIBUTION= "Predicted distribution";

  /** output distribution instead of cluster index. */
  protected boolean m_OutputDistribution;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Just adds the predicted cluster (or distribution) to the original dataset.\n"
	+ "Stored in container under: " + VALUE_CLUSTERED_DATASET;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-distribution", "outputDistribution",
      false);
  }

  /**
   * Sets whether to output the cluster distribution instead of the cluster index.
   *
   * @param value	true if to output distribution
   */
  public void setOutputDistribution(boolean value){
    m_OutputDistribution = value;
    reset();
  }

  /**
   * Returns whether to output the cluster distribution instead of the cluster index.
   *
   * @return		true if to output distribution
   */
  public boolean getOutputDistribution(){
    return m_OutputDistribution;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDistributionTipText() {
    return "If enabled, the cluster distribution is output instead of the cluster index.";
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
    double[]			dist;
    int				n;
    int				numClusters;

    if (m_OutputDistribution) {
      try {
        numClusters = clusterer.numberOfClusters();
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to query number of clusters!", e);
        numClusters = 0;
      }

      result = data;
      for (n = 0; n < numClusters; n++) {
	add = new Add();
	add.setAttributeType(new SelectedTag(Attribute.NUMERIC, Add.TAGS_TYPE));
	add.setAttributeName(PREDICTED_DISTRIBUTION + " " + n);
	add.setAttributeIndex("" + (result.numAttributes() + 1));
	try {
	  add.setInputFormat(result);
	  result = Filter.useFilter(result, add);
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to add attribute '" + PREDICTED_DISTRIBUTION + " " + n + "' to the dataset!", e);
	  result = null;
	}
	if (result == null)
	  break;
      }

      if (result != null) {
	for (i = 0; i < data.numInstances(); i++) {
	  try {
	    dist = clusterer.distributionForInstance(data.instance(i));
	    for (n = 0; n < numClusters; n++)
	      result.instance(i).setValue(result.numAttributes() - numClusters + n, dist[n]);
	  }
	  catch (Exception e) {
	    getLogger().log(Level.SEVERE, "Failed to cluster instance #" + (i + 1) + "!", e);
	  }
	}
      }
    }
    else {
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
	    getLogger().log(Level.SEVERE, "Failed to cluster instance #" + (i + 1) + "!", e);
	  }
	}
      }
    }

    return result;
  }
}
