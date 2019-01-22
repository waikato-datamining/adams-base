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
 * AbstractSimpleClassifier.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.simple;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.AbstractSimpleOptionHandler;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.CapabilitiesHandler;
import weka.core.CommandlineRunnable;
import weka.core.Instance;
import weka.core.Utils;

/**
 * Ancestor for classifiers using ADAMS option handling.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSimpleClassifier
  extends AbstractSimpleOptionHandler
  implements Classifier, CapabilitiesHandler {

  private static final long serialVersionUID = 2170248971336058726L;

  /**
   * Classifies the given test instance. The instance has to belong to a dataset
   * when it's being classified. Note that a classifier MUST implement either
   * this or distributionForInstance().
   *
   * @param instance the instance to be classified
   * @return the predicted most likely class for the instance or
   *         Utils.missingValue() if no prediction is made
   * @exception Exception if an error occurred during the prediction
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    double[] dist = distributionForInstance(instance);
    if (dist == null)
      throw new Exception("Null distribution predicted");

    switch (instance.classAttribute().type()) {
    case Attribute.NOMINAL:
      double max = 0;
      int maxIndex = 0;

      for (int i = 0; i < dist.length; i++) {
        if (dist[i] > max) {
          maxIndex = i;
          max = dist[i];
        }
      }
      if (max > 0)
        return maxIndex;
      else
        return Utils.missingValue();
    case Attribute.NUMERIC:
    case Attribute.DATE:
      return dist[0];
    default:
      return Utils.missingValue();
    }
  }

  /**
   * Predicts the class memberships for a given instance. If an instance is
   * unclassified, the returned array elements must be all zero. If the class is
   * numeric, the array must consist of only one element, which contains the
   * predicted value. Note that a classifier MUST implement either this or
   * classifyInstance().
   *
   * @param instance the instance to be classified
   * @return an array containing the estimated membership probabilities of the
   *         test instance in each class or the numeric prediction
   * @exception Exception if distribution could not be computed successfully
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    double[] dist = new double[instance.numClasses()];
    switch (instance.classAttribute().type()) {
    case Attribute.NOMINAL:
      double classification = classifyInstance(instance);
      if (Utils.isMissingValue(classification))
        return dist;
      else
        dist[(int) classification] = 1.0;
      return dist;
    case Attribute.NUMERIC:
    case Attribute.DATE:
      dist[0] = classifyInstance(instance);
      return dist;
    default:
      return dist;
    }
  }

  /**
   * runs the classifier instance with the given options.
   *
   * @param classifier the classifier to run
   * @param options the commandline options
   */
  public static void runClassifier(Classifier classifier, String[] options) {
    try {
      if (classifier instanceof CommandlineRunnable) {
	((CommandlineRunnable)classifier).preExecution();
      }
      System.out.println(Evaluation.evaluateModel(classifier, options));
    }
    catch (Exception e) {
      if (((e.getMessage() != null)
	&& e.getMessage().contains("General options"))
	|| (e.getMessage() == null)) {
	e.printStackTrace();
      }
      else {
	System.err.println(e.getMessage());
      }
    }
    if (classifier instanceof CommandlineRunnable) {
      try {
	((CommandlineRunnable) classifier).postExecution();
      }
      catch (Exception ex) {
	ex.printStackTrace();
      }
    }
  }

  /**
   * Returns the Capabilities of this classifier. Maximally permissive
   * capabilities are allowed by default. Derived classifiers should override
   * this method and first disable all capabilities and then enable just those
   * capabilities that make sense for the scheme.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = new Capabilities(this);
    result.enableAll();
    return result;
  }
}
