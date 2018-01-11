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
 * LogTargetRegressor.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import weka.classifiers.SingleClassifierEnhancer;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;

/**
 <!-- globalinfo-start -->
 * Takes logs of all numeric attributes in the data.
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
 * @author Eibe Frank
 * @version $Revision$
 */
public class LogTargetRegressor
  extends SingleClassifierEnhancer {

  /** suid. */
  private static final long serialVersionUID = -6941274159321491218L;

  /** Constant to add to attributes before logarithm is taken. */
  public final static double ATT_CONSTANT = 1.0;

  /** Constant to add to class before logarithm is taken. */
  public final static double CLASS_CONSTANT = 1.0;

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Takes logs of all numeric attributes in the data.";
  }

  /**
   * Transform instance.
   *
   * @param inst	the instance to transform
   * @return		the transformed instance
   */
  public Instance transform(Instance inst) {
    double[] vals = inst.toDoubleArray();
    for (int i = 0; i < inst.numAttributes(); i++) {
      if (!(inst.isMissing(i))) {
	if (inst.attribute(i).isNumeric()) {
	  if (i != inst.classIndex()) {
	    vals[i] = Math.log(ATT_CONSTANT + vals[i]);
	  } else {
	    vals[i] = Math.log(CLASS_CONSTANT + vals[i]);
	  }
	}
      }
    }
    Instance newInst = new DenseInstance(inst.weight(), vals);
    newInst.setDataset(inst.dataset());
    return newInst;
  }

  /**
   * Builds the classifier.
   *
   * @param data	the training data
   * @throws Exception	if something goes wrong
   */
  public void buildClassifier(Instances data) throws Exception {
    getCapabilities().testWithFail(data);

    Instances newData = new Instances(data, data.numInstances());

    for (int i = 0; i < data.numInstances(); i++) {
      newData.add(transform(data.instance(i)));
    }

    if (getDebug())
      System.out.println(newData);

    m_Classifier.buildClassifier(newData);
  }

  /**
   * Returns the prediction.
   *
   * @param inst	the instance to predict
   * @return		the prediction
   * @throws Exception	if prediction fails
   */
  public double classifyInstance(Instance inst) throws Exception {
    double result = m_Classifier.classifyInstance(transform(inst));
    return Math.exp(result) - CLASS_CONSTANT;
  }

  /**
   * Returns description of classifier.
   *
   * @return		the description
   */
  public String toString() {
    return m_Classifier.toString();
  }

  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for running this class.
   *
   * @param argv the options
   */
  public static void main(String[] argv) {
    runClassifier(new LogTargetRegressor(), argv);
  }
}
