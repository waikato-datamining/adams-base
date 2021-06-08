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
 * AverageSilhouetteCoefficient.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaclusterer;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.flow.container.WekaModelContainer;
import weka.clusterers.Clusterer;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instances;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Computes the average Silhouette coefficient for the clusters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @author Eibe Frank (original Groovy code on Weka mailing list)
 */
public class AverageSilhouetteCoefficient
  extends AbstractClustererPostProcessor
  implements TechnicalInformationHandler {

  private static final long serialVersionUID = 1326504894062853934L;

  /** the key in the container. */
  public final static String VALUE_AVERAGE_SILHOUETTE_COEFFICIENT = "Average Silhouette Coefficient";

  /** the distance function to use. */
  protected DistanceFunction m_DistanceFunction;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Computes the average Silhouette coefficient for the clusters.\n\n"
      + "For more information see:\n\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "WikiPedia");
    result.setValue(Field.TITLE, "Silhouette (clustering)");
    result.setValue(Field.URL, "https://en.wikipedia.org/wiki/Silhouette_%28clustering%29");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "distance-function", "distanceFunction",
      new EuclideanDistance());
  }

  /**
   * Sets the distance function to use.
   *
   * @param value	the function
   */
  public void setDistanceFunction(DistanceFunction value){
    m_DistanceFunction = value;
    reset();
  }

  /**
   * Returns the distance function to use.
   *
   * @return		the function
   */
  public DistanceFunction getDistanceFunction(){
    return m_DistanceFunction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String distanceFunctionTipText() {
    return "The distance function to use.";
  }

  /**
   * Returns the keys that the processor adds/modifies.
   *
   * @return the keys, null of zero-length array for none
   */
  @Override
  protected String[] getContainerKeys() {
    return new String[]{VALUE_AVERAGE_SILHOUETTE_COEFFICIENT};
  }

  /**
   * Performs the actual post-processing.
   *
   * @param cont the container to post-process
   * @return the post-processed container
   */
  @Override
  protected WekaModelContainer doPostProcess(WekaModelContainer cont) {
    Clusterer	clusterer;
    Instances	data;
    int[] 	clusterIndexOfInstance;
    int 	i;
    int 	j;
    int 	k;
    double 	sumSilhouetteCoefficients;
    double[] 	averageDistancePerCluster;
    int[] 	numberOfInstancesPerCluster;
    double 	avgDistance;
    double 	closestDistance;
    double 	average;

    clusterer = (Clusterer) cont.getValue(WekaModelContainer.VALUE_MODEL);
    data      = (Instances) cont.getValue(WekaModelContainer.VALUE_DATASET);

    try {
      m_DistanceFunction.setInstances(data);

      clusterIndexOfInstance = new int[data.numInstances()];
      for (i = 0; i < data.numInstances(); i++)
	clusterIndexOfInstance[i] = clusterer.clusterInstance(data.instance(i));

      sumSilhouetteCoefficients = 0;
      for (i = 0; i < data.numInstances(); i++) {
	// Compute average distance of current instance to each cluster, including its own cluster
	averageDistancePerCluster = new double[clusterer.numberOfClusters()];
	numberOfInstancesPerCluster = new int[clusterer.numberOfClusters()];
	for (j = 0; j < data.numInstances(); j++) {
	  averageDistancePerCluster[clusterIndexOfInstance[j]] += m_DistanceFunction.distance(data.instance(i), data.instance(j));
	  numberOfInstancesPerCluster[clusterIndexOfInstance[j]]++; // Should the current instance be skipped though?
	}
	for (k = 0; k < averageDistancePerCluster.length; k++)
	  averageDistancePerCluster[k] /= numberOfInstancesPerCluster[k];

	// Average distance to instance's own cluster
	avgDistance = averageDistancePerCluster[clusterIndexOfInstance[i]];

	// Find the distance of the "closest" other cluster
	averageDistancePerCluster[clusterIndexOfInstance[i]] = Double.MAX_VALUE;
	closestDistance = Arrays.stream(averageDistancePerCluster).min().getAsDouble();

	// Compute silhouette coefficient for current instance
	sumSilhouetteCoefficients += clusterer.numberOfClusters() > 1 ? (closestDistance - avgDistance) / Math.max(avgDistance, closestDistance) : 0;
      }

      average = (sumSilhouetteCoefficients / data.numInstances());
      if (isLoggingEnabled())
        getLogger().info("Average silhouette coefficient: " + average);

      cont.addAdditionalName(VALUE_AVERAGE_SILHOUETTE_COEFFICIENT);
      cont.setValue(VALUE_AVERAGE_SILHOUETTE_COEFFICIENT, average);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to compute average silhouette coefficient!", e);
    }

    return cont;
  }
}
