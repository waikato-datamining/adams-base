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
 * LWLDatasetBuilder.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.lazy;

import adams.core.logging.CustomLoggingLevelObject;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.LinearNNSearch;
import weka.core.neighboursearch.NearestNeighbourSearch;

import java.io.Serializable;

/**
 * Class for building LWL-style weighted datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LWLDatasetBuilder
  extends CustomLoggingLevelObject {

  private static final long serialVersionUID = 246129751885426502L;

  /** the container with the weighted dataset, distances, indices. */
  public static class LWLContainer
    implements Serializable {

    private static final long serialVersionUID = 5090533464519863032L;

    /** the weighted dataset. */
    public Instances dataset;

    /** the distances. */
    public double[] distances;

    /** the indices (from the training dataset). */
    public int[] originalIndices;
  }

  /** The training instances used for classification. */
  protected Instances m_Train = null;

  /** The number of neighbours used to select the kernel bandwidth. */
  protected int m_kNN = -1;

  /** The weighting kernel method currently selected. */
  protected int m_WeightKernel = LWL.LINEAR;

  /** True if m_kNN should be set to all instances. */
  protected boolean m_UseAllK = true;

  /** The nearest neighbour search algorithm to use. */
  protected NearestNeighbourSearch m_Search =  new LinearNNSearch();

  /** The actual nearest neighbour search algorithm to use. */
  protected NearestNeighbourSearch m_ActualSearch;

  /** whether to suppress the update of the nearest-neighbor search algorithm
   * when making predictions. */
  protected boolean m_NoUpdate = false;

  /**
   * Gets the number of neighbours used for kernel bandwidth setting.
   * The bandwidth is taken as the distance to the kth neighbour.
   *
   * @return the number of neighbours included inside the kernel
   * bandwidth, or 0 for all neighbours
   */
  public int getKNN() {
    return m_kNN;
  }

  /**
   * Sets the number of neighbours used for kernel bandwidth setting.
   * The bandwidth is taken as the distance to the kth neighbour.
   *
   * @param value the number of neighbours included inside the kernel
   * bandwidth, or 0 to specify using all neighbors.
   */
  public void setKNN(int value) {
    m_kNN = value;
    if (value <= 0) {
      m_kNN = 0;
      m_UseAllK = true;
    } else {
      m_UseAllK = false;
    }
  }

  /**
   * Sets the kernel weighting method to use. Must be one of LINEAR,
   * EPANECHNIKOV,  TRICUBE, INVERSE, GAUSS or CONSTANT, other values
   * are ignored.
   *
   * @param value the new kernel method to use. Must be one of LINEAR,
   * EPANECHNIKOV,  TRICUBE, INVERSE, GAUSS or CONSTANT.
   */
  public void setWeightingKernel(int value) {
    if ((value != LWL.LINEAR)
      && (value != LWL.EPANECHNIKOV)
      && (value != LWL.TRICUBE)
      && (value != LWL.INVERSE)
      && (value != LWL.GAUSS)
      && (value != LWL.CONSTANT)) {
      return;
    }
    m_WeightKernel = value;
  }

  /**
   * Gets the kernel weighting method to use.
   *
   * @return the new kernel method to use. Will be one of LINEAR,
   * EPANECHNIKOV,  TRICUBE, INVERSE, GAUSS or CONSTANT.
   */
  public int getWeightingKernel() {
    return m_WeightKernel;
  }

  /**
   * Sets the nearestNeighbourSearch algorithm to be used for finding nearest
   * neighbour(s).
   * @param value - The NearestNeighbourSearch class.
   */
  public void setSearchAlgorithm(NearestNeighbourSearch value) {
    m_Search = value;
  }

  /**
   * Returns the current nearestNeighbourSearch algorithm in use.
   * @return the NearestNeighbourSearch algorithm currently in use.
   */
  public NearestNeighbourSearch getSearchAlgorithm() {
    return m_Search;
  }

  /**
   * Sets whether to suppress updating the nearest-neighbor search algorithm
   * when making predictions.
   *
   * @param value	if true then no update happens.
   */
  public void setNoUpdate(boolean value) {
    m_NoUpdate = value;
  }

  /**
   * Returns whether to suppress the update of the nearest-neighbor search
   * algorithm when making predictions.
   *
   * @return 		true if the update is suppressed
   */
  public boolean getNoUpdate() {
    return m_NoUpdate;
  }

  /**
   * Sets the training data to use.
   *
   * @param value	the data
   */
  public void setTrain(Instances value) {
    m_Train = value;
  }

  /**
   * Returns the training data in use.
   *
   * @return		the data
   */
  public Instances getTrain() {
    return m_Train;
  }

  /**
   * Constructs the weighted dataset.
   *
   * @param instance	the instance to make prediction for
   * @return 		the container with the generated for the classifier to train with
   * @throws Exception	if build fails
   */
  public LWLContainer build(Instance instance) throws Exception {
    int 		k;
    Instances 		weighted;
    double 		distances[];
    int			i;
    double 		sumOfWeights;
    double 		newSumOfWeights;
    double 		weight;
    Instance 		inst;
    LWLContainer 	result;

    if (!m_NoUpdate)
      m_Search.addInstanceInfo(instance);

    k = m_Train.numInstances();
    if(!m_UseAllK && (m_kNN < k)) {
      k = m_kNN;
    }

    weighted = m_Search.kNearestNeighbours(instance, k);
    distances = m_Search.getDistances();

    if (isLoggingEnabled()) {
      getLogger().fine("Test Instance: "+instance);
      getLogger().fine("For "+k+" kept " + weighted.numInstances() + " out of " +
                         m_Train.numInstances() + " instances.");
    }

    // IF LinearNN has skipped so much that <k neighbours are remaining.
    if(k > distances.length)
      k = distances.length;

    if (isLoggingEnabled()) {
      getLogger().fine("Instance Distances");
      for (i = 0; i < distances.length; i++) {
	getLogger().fine((i+1) + ". " + distances[i]);
      }
    }

    // Determine the bandwidth
    double bandwidth = distances[k-1];

    // Check for bandwidth zero
    if (bandwidth <= 0) {
      //if the kth distance is zero than give all instances the same weight
      for(i = 0; i < distances.length; i++)
        distances[i] = 1;
    } else {
      // Rescale the distances by the bandwidth
      for (i = 0; i < distances.length; i++)
        distances[i] = distances[i] / bandwidth;
    }

    // Pass the distances through a weighting kernel
    for (i = 0; i < distances.length; i++) {
      switch (m_WeightKernel) {
        case LWL.LINEAR:
          distances[i] = 1.0001 - distances[i];
          break;
        case LWL.EPANECHNIKOV:
          distances[i] = 3/4D*(1.0001 - distances[i]*distances[i]);
          break;
        case LWL.TRICUBE:
          distances[i] = Math.pow( (1.0001 - Math.pow(distances[i], 3)), 3 );
          break;
        case LWL.CONSTANT:
          distances[i] = 1;
          break;
        case LWL.INVERSE:
          distances[i] = 1.0 / (1.0 + distances[i]);
          break;
        case LWL.GAUSS:
          distances[i] = Math.exp(-distances[i] * distances[i]);
          break;
      }
    }

    if (isLoggingEnabled()) {
      getLogger().fine("Instance Weights");
      for (i = 0; i < distances.length; i++) {
	getLogger().fine((i+1) + ". " + distances[i]);
      }
    }

    // Set the weights on the training data
    sumOfWeights    = 0;
    newSumOfWeights = 0;
    for (i = 0; i < distances.length; i++) {
      weight = distances[i];
      inst = weighted.instance(i);
      sumOfWeights += inst.weight();
      newSumOfWeights += inst.weight() * weight;
      inst.setWeight(inst.weight() * weight);
    }

    // Rescale weights
    for (i = 0; i < weighted.numInstances(); i++) {
      inst = weighted.instance(i);
      inst.setWeight(inst.weight() * sumOfWeights / newSumOfWeights);
    }

    result = new LWLContainer();
    result.dataset = weighted;
    result.distances = distances.clone();
    result.originalIndices = null;  // TODO

    return result;
  }
}
