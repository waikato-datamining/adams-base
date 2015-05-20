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
 * WeightedInstancesHandlerWrapper.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import weka.classifiers.SingleClassifierEnhancer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.WeightedInstancesHandler;

/**
 <!-- globalinfo-start -->
 * A meta-classifier that implements the weka.core.WeightedInstancesHandler interface in order to enable all classifiers to be used in other meta-classifiers that require the base classifier to implem
ent the WeightedInstancesHandler interface. This meta-classifier does nothing with the weights, it is just a dumb (but useful) wrapper.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.rules.ZeroR)</pre>
 *
 * <pre>
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WeightedInstancesHandlerWrapper
  extends SingleClassifierEnhancer
  implements WeightedInstancesHandler {

  /** for serialization. */
  private static final long serialVersionUID = -2789375910646576521L;

  /**
   * Returns a string describing the classifier.
   *
   * @return 		a description suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String globalInfo() {
    return
        "A meta-classifier that implements the weka.core.WeightedInstancesHandler "
      + "interface in order to enable all classifiers to be used in other "
      + "meta-classifiers that require the base classifier to implement the "
      + "WeightedInstancesHandler interface. This meta-classifier does nothing "
      + "with the weights, it is just a dumb (but useful) wrapper.";
  }

  /**
   * Generates a classifier. Must initialize all fields of the classifier
   * that are not being set via options (ie. multiple calls of buildClassifier
   * must always lead to the same result). Must not change the dataset
   * in any way.
   *
   * @param data 	set of instances serving as training data
   * @throws Exception 	if the classifier has not been
   * 			generated successfully
   */
  public void buildClassifier(Instances data) throws Exception {
    m_Classifier.buildClassifier(data);
  }

  /**
   * Classifies the given test instance. The instance has to belong to a
   * dataset when it's being classified.
   *
   * @param instance 	the instance to be classified
   * @return 		the predicted most likely class for the instance or
   * 			Instance.missingValue() if no prediction is made
   * @throws Exception 	if an error occurred during the prediction
   */
  public double classifyInstance(Instance instance) throws Exception {
    return m_Classifier.classifyInstance(instance);
  }

  /**
   * Predicts the class memberships for a given instance. If
   * an instance is unclassified, the returned array elements
   * must be all zero. If the class is numeric, the array
   * must consist of only one element, which contains the
   * predicted value.
   *
   * @param instance 	the instance to be classified
   * @return 		an array containing the estimated membership
   * 			probabilities of the test instance in each class
   * 			or the numeric prediction
   * @throws Exception 	if distribution could not be
   * 			computed successfully
   */
  public double[] distributionForInstance(Instance instance) throws Exception {
    return m_Classifier.distributionForInstance(instance);
  }

  /**
   * Returns a string representation of the base classifier.
   *
   * @return		the base classifier's model
   */
  public String toString() {
    return m_Classifier.toString();
  }

  /**
   * Main method for running this class.
   *
   * @param args 	the options
   */
  public static void main(String[] args) {
    runClassifier(new WeightedInstancesHandlerWrapper(), args);
  }
}
