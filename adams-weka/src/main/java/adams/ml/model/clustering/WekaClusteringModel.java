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
 * WekaClusteringModel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.model.clustering;

import adams.core.logging.LoggingObject;
import adams.data.spreadsheet.Row;
import adams.ml.data.Dataset;
import adams.ml.data.DatasetInfo;
import adams.ml.data.WekaConverter;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Clustering model for Weka classifiers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClusteringModel
  extends LoggingObject
  implements ClusteringModel {

  private static final long serialVersionUID = 4557749254683230577L;

  /** the underlying model. */
  protected weka.clusterers.Clusterer m_Model;

  /** the dataset info. */
  protected DatasetInfo m_DatasetInfo;

  /** the instances used. */
  protected Instances m_InstancesHeader;

  /**
   * Initializes the model.
   *
   * @param model	the built Weka clusterer
   * @param data	the training data
   * @param inst	the Weka training data
   */
  public WekaClusteringModel(weka.clusterers.Clusterer model, Dataset data, Instances inst) {
    m_Model           = model;
    m_DatasetInfo     = new DatasetInfo(data);
    m_InstancesHeader = new Instances(inst, 0);
  }

  /**
   * Returns the cluster for the given row.
   *
   * @param row		the row to make predictions for
   * @return		the prediction
   * @throws Exception	if prediction fails
   */
  @Override
  public int cluster(Row row) throws Exception{
    Instance 	inst;

    inst = WekaConverter.toInstance(m_InstancesHeader, row);
    if (inst == null)
      throw new Exception("Failed to convert data row into Weka instance: " + row);

    return m_Model.clusterInstance(inst);
  }

  /**
   * Returns the cluster distribution for the given row.
   *
   * @param row		the row to generate the cluster distribution for
   * @return		the cluster distribution
   * @throws Exception	if prediction fails
   */
  @Override
  public double[] distribution(Row row) throws Exception {
    Instance 	inst;

    inst = WekaConverter.toInstance(m_InstancesHeader, row);
    if (inst == null)
      throw new Exception("Failed to convert data row into Weka instance: " + row);

    return m_Model.distributionForInstance(inst);
  }

  /**
   * Returns information about the dataset used for building the model.
   *
   * @return		the information
   */
  @Override
  public DatasetInfo getDatasetInfo() {
    return m_DatasetInfo;
  }

  /**
   * Returns the Instances header used for building the model.
   *
   * @return		the header
   */
  public Instances getInstancesHeader() {
    return m_InstancesHeader;
  }

  /**
   * Gets a short string description of the model.
   *
   * @return		the description, null if none available
   */
  @Override
  public String getModelDescription() {
    return m_Model.toString();
  }
}
