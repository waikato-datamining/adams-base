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
 * ClusterCenters.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.wekaclusterer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import weka.clusterers.Clusterer;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Computes the cluster centers for the provided dataset. An additional attribute is added to the dataset structure that contains the cluster index.<br>
 * Only numeric attributes are considered when computing the centers.<br>
 * Stored in container under: Clustered dataset
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClusterCenters
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
	"Computes the cluster centers for the provided dataset. An additional "
	+ "attribute is added to the dataset structure that contains the "
	+ "cluster index.\n"
	+ "Only numeric attributes are considered when computing the centers.\n"
	+ "Stored in container under: " + VALUE_CLUSTERED_DATASET;
  }
  
  /**
   * Generates the output format (additional attribute for cluster index).
   * 
   * @param data	the original input data
   * @return		the header of the output format
   */
  protected Instances createOutputFormat(Instances data) {
    Instances	result;
    Add		add;
    
    result = new Instances(data, 0);
    try {
      add = new Add();
      add.setAttributeIndex("1");
      add.setAttributeName("Cluster index");
      add.setAttributeType(new SelectedTag(Attribute.NUMERIC, Add.TAGS_TYPE));
      add.setInputFormat(result);
      result = Filter.useFilter(result, add);
    }
    catch (Exception e) {
      throw new IllegalStateException(e);
    }
    
    return result;
  }
  
  /**
   * Calculates the centers.
   * 
   *  @param data		the input data used for training the clusterer
   *  @param clusterer		the built clusterer
   *  @param outputFormat	the format to use for the output
   *  @return			the generated output
   */
  protected Instances calculateCenters(Instances data, Clusterer clusterer, Instances outputFormat) {
    Instances				result;
    Hashtable<Integer,Instances>	clusters;
    List<Integer>			indices;
    int					i;
    int					cluster;
    boolean				error;
    String				errorMsg;
    Instances				subset;
    double[]				values;
    Instance				inst;
    
    result   = new Instances(outputFormat, 0);
    clusters = new Hashtable<Integer,Instances>();
    
    // cluster the data
    error    = false;
    errorMsg = null;
    for (i = 0; i < data.numInstances(); i++) {
      try {
	cluster = clusterer.clusterInstance(data.instance(i));
	if (!clusters.containsKey(cluster))
	  clusters.put(cluster, new Instances(data, 0));
	clusters.get(cluster).add(data.instance(i));
      }
      catch (Exception e) {
	// ignored
	if (!error) {
	  error    = true;
	  errorMsg = e.toString();
	}
      }
    }
    
    // process clusters
    if (!error) {
      indices = new ArrayList<Integer>(clusters.keySet());
      Collections.sort(indices);
      for (Integer cl: indices) {
	subset = clusters.get(cl);
	inst   = new DenseInstance(result.numAttributes());
	inst.setValue(0, cl);
	for (i = 0; i < subset.numAttributes(); i++) {
	  if (!subset.attribute(i).isNumeric())
	    continue;
	  values = subset.attributeToDoubleArray(i);
	  inst.setValue(i + 1, StatUtils.mean(values));
	}
	result.add(inst);
      }
    }
    else {
      getLogger().severe("At least one error occurred: " + errorMsg);
    }
    
    return result;
  }
  
  /**
   * Performs some form of processing on the full dataset.
   */
  @Override
  protected Instances processDatasetWithClusterer(Instances data, Clusterer clusterer) {
    Instances	result;
    
    result = createOutputFormat(data);
    result = calculateCenters(data, clusterer, result);
    
    return result;
  }
}
