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
 * ClusterStatistics.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
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
 * Computes cluster statistics (min&#47;max&#47;mean&#47;stdev) for the provided dataset. An additional attribute is added to the dataset structure that indicates the cluster index and the statistic.<br/>
 * Only numeric attributes are considered when computing the statistics.<br/>
 * Stored in container under: Clustered dataset
 * <p/>
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
 * @version $Revision: 7171 $
 */
public class ClusterStatistics
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
	"Computes cluster statistics (min/max/mean/stdev) for the provided "
	+ "dataset. An additional attribute is added to the dataset structure "
	+ "that indicates the cluster index and the statistic.\n"
	+ "Only numeric attributes are considered when computing the statistics.\n"
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
      add.setAttributeName("Statistic");
      add.setAttributeType(new SelectedTag(Attribute.STRING, Add.TAGS_TYPE));
      add.setInputFormat(result);
      result = Filter.useFilter(result, add);
    }
    catch (Exception e) {
      throw new IllegalStateException(e);
    }
    
    return result;
  }
  
  /**
   * Creates a new stats row.
   * 
   * @param index	the cluster index
   * @param statistic	the statistic's name
   * @param data	the data format
   * @return		the generated row
   */
  protected DenseInstance createRow(int index, String statistic, Instances data) {
    DenseInstance	result;
    
    result = new DenseInstance(data.numAttributes());
    result.setDataset(data);
    result.setValue(0, result.attribute(0).addStringValue(index + "-" + statistic));
    
    return result;
  }
  
  /**
   * Calculates the statistics.
   * 
   *  @param data		the input data used for training the clusterer
   *  @param clusterer		the built clusterer
   *  @param outputFormat	the format to use for the output
   *  @return			the generated output
   */
  protected Instances calculateStatistics(Instances data, Clusterer clusterer, Instances outputFormat) {
    Instances				result;
    Hashtable<Integer,Instances>	clusters;
    List<Integer>			indices;
    int					i;
    int					cluster;
    boolean				error;
    String				errorMsg;
    Instances				subset;
    double[]				values;
    Instance				min;
    Instance				max;
    Instance				median;
    Instance				mean;
    Instance				stdev;
    
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
	min    = createRow(cl, "Min", result);
	max    = createRow(cl, "Max", result);
	median = createRow(cl, "Median", result);
	mean   = createRow(cl, "Mean", result);
	stdev  = createRow(cl, "StdDev", result);
	for (i = 0; i < subset.numAttributes(); i++) {
	  if (!subset.attribute(i).isNumeric())
	    continue;
	  values = subset.attributeToDoubleArray(i);
	  min.setValue(i + 1, StatUtils.min(values));
	  max.setValue(i + 1, StatUtils.max(values));
	  median.setValue(i + 1, StatUtils.mean(values));
	  mean.setValue(i + 1, StatUtils.mean(values));
	  stdev.setValue(i + 1, StatUtils.stddev(values, true));
	}
	result.add(min);
	result.add(max);
	result.add(median);
	result.add(mean);
	result.add(stdev);
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
    result = calculateStatistics(data, clusterer, result);
    
    return result;
  }
}
