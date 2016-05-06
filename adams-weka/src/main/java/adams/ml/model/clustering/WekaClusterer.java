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
 * WekaClusterer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.model.clustering;

import adams.core.option.OptionUtils;
import adams.ml.capabilities.Capabilities;
import adams.ml.data.Dataset;
import adams.ml.data.WekaConverter;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Wraps around a Weka clusterer.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-strict-capabilities &lt;boolean&gt; (property: strictCapabilities)
 * &nbsp;&nbsp;&nbsp;If enabled, a strict capabilities test is performed; otherwise, it is attempted 
 * &nbsp;&nbsp;&nbsp;to adjust the data to fit the algorithm's capabilities.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-clusterer &lt;weka.clusterers.Clusterer&gt; (property: clusterer)
 * &nbsp;&nbsp;&nbsp;The clusterer to use.
 * &nbsp;&nbsp;&nbsp;default: weka.clusterers.SimpleKMeans -init 0 -max-candidates 100 -periodic-pruning 10000 -min-density 2.0 -t1 -1.25 -t2 -1.0 -N 2 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -num-slots 1 -S 10
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClusterer
  extends AbstractClusterer {

  private static final long serialVersionUID = -4086036132431888958L;

  /** the weka classifier to use. */
  protected weka.clusterers.Clusterer m_Clusterer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Wraps around a Weka clusterer.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "clusterer", "clusterer",
	    new SimpleKMeans());
  }

  /**
   * Sets the clusterer to use.
   *
   * @param value	the clusterer
   */
  public void setClusterer(weka.clusterers.Clusterer value) {
    m_Clusterer = value;
    reset();
  }

  /**
   * Returns the clusterer to use.
   *
   * @return		the clusterer
   */
  public weka.clusterers.Clusterer getClusterer() {
    return m_Clusterer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String clustererTipText() {
    return "The clusterer to use.";
  }

  /**
   * Returns the algorithm's capabilities in terms of data.
   *
   * @return		the algorithm's capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = super.getCapabilities();
    result.assign(WekaConverter.convertCapabilities(m_Clusterer.getCapabilities()));

    return result;
  }

  /**
   * Builds a model from the data.
   *
   * @param data	the data to use for building the model
   * @return		the generated model
   * @throws Exception	if the build fails
   */
  @Override
  protected ClusteringModel doBuildModel(Dataset data) throws Exception {
    Instances			inst;
    weka.clusterers.Clusterer   clusterer;

    inst       = WekaConverter.toInstances(data);
    clusterer = (weka.clusterers.Clusterer) OptionUtils.shallowCopy(m_Clusterer);
    if (clusterer == null)
      throw new Exception("Failed to create shallow copy of classifier: " + OptionUtils.getCommandLine(m_Clusterer));

    clusterer.buildClusterer(inst);

    return new WekaClusteringModel(clusterer, data, inst);
  }
}
