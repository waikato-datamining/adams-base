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

/**
 * Class for building LWL-style weighted datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LWLDatasetBuilder
  extends CustomLoggingLevelObject {

  private static final long serialVersionUID = 246129751885426502L;

  /** The training instances used for classification. */
  protected Instances m_Train = null;

  /** The number of neighbours used to select the kernel bandwidth. */
  protected int m_kNN = -1;

  /** The weighting kernel method currently selected. */
  protected int m_WeightKernel = LWL.LINEAR;

  /** True if m_kNN should be set to all instances. */
  protected boolean m_UseAllK = true;

  /** The nearest neighbour search algorithm to use.
   * (Default: weka.core.neighboursearch.LinearNNSearch)
   */
  protected NearestNeighbourSearch m_NNSearch =  new LinearNNSearch();

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
   * @param knn the number of neighbours included inside the kernel
   * bandwidth, or 0 to specify using all neighbors.
   */
  public void setKNN(int knn) {
    m_kNN = knn;
    if (knn <= 0) {
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
   * @param kernel the new kernel method to use. Must be one of LINEAR,
   * EPANECHNIKOV,  TRICUBE, INVERSE, GAUSS or CONSTANT.
   */
  public void setWeightingKernel(int kernel) {
    if ((kernel != LWL.LINEAR)
      && (kernel != LWL.EPANECHNIKOV)
      && (kernel != LWL.TRICUBE)
      && (kernel != LWL.INVERSE)
      && (kernel != LWL.GAUSS)
      && (kernel != LWL.CONSTANT)) {
      return;
    }
    m_WeightKernel = kernel;
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
   * @param nearestNeighbourSearchAlgorithm - The NearestNeighbourSearch class.
   */
  public void setNearestNeighbourSearchAlgorithm(NearestNeighbourSearch nearestNeighbourSearchAlgorithm) {
    m_NNSearch = nearestNeighbourSearchAlgorithm;
  }

  /**
   * Returns the current nearestNeighbourSearch algorithm in use.
   * @return the NearestNeighbourSearch algorithm currently in use.
   */
  public NearestNeighbourSearch getNearestNeighbourSearchAlgorithm() {
    return m_NNSearch;
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
   * @return 		the weighted dataset for the classifier to train with
   * @throws Exception	if build fails
   */
  public Instances build(Instance instance) throws Exception {
    if (!m_NoUpdate)
      m_NNSearch.addInstanceInfo(instance);

    int k = m_Train.numInstances();
    if( (!m_UseAllK && (m_kNN < k)) /*&&
       !(m_WeightKernel==INVERSE ||
         m_WeightKernel==GAUSS)*/ ) {
      k = m_kNN;
    }

    Instances result = m_NNSearch.kNearestNeighbours(instance, k);
    double distances[] = m_NNSearch.getDistances();

    if (isLoggingEnabled()) {
      getLogger().fine("Test Instance: "+instance);
      getLogger().fine("For "+k+" kept " + result.numInstances() + " out of " +
                         m_Train.numInstances() + " instances.");
    }

    //IF LinearNN has skipped so much that <k neighbours are remaining.
    if(k>distances.length)
      k = distances.length;

    if (isLoggingEnabled()) {
      getLogger().fine("Instance Distances");
      for (int i = 0; i < distances.length; i++) {
	getLogger().fine((i+1) + ". " + distances[i]);
      }
    }

    // Determine the bandwidth
    double bandwidth = distances[k-1];

    // Check for bandwidth zero
    if (bandwidth <= 0) {
      //if the kth distance is zero than give all instances the same weight
      for(int i=0; i < distances.length; i++)
        distances[i] = 1;
    } else {
      // Rescale the distances by the bandwidth
      for (int i = 0; i < distances.length; i++)
        distances[i] = distances[i] / bandwidth;
    }

    // Pass the distances through a weighting kernel
    for (int i = 0; i < distances.length; i++) {
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
      for (int i = 0; i < distances.length; i++) {
	getLogger().fine((i+1) + ". " + distances[i]);
      }
    }

    // Set the weights on the training data
    double sumOfWeights = 0, newSumOfWeights = 0;
    for (int i = 0; i < distances.length; i++) {
      double weight = distances[i];
      Instance inst = result.instance(i);
      sumOfWeights += inst.weight();
      newSumOfWeights += inst.weight() * weight;
      inst.setWeight(inst.weight() * weight);
      //weightedTrain.add(newInst);
    }

    // Rescale weights
    for (int i = 0; i < result.numInstances(); i++) {
      Instance inst = result.instance(i);
      inst.setWeight(inst.weight() * sumOfWeights / newSumOfWeights);
    }

    return result;
  }
}
