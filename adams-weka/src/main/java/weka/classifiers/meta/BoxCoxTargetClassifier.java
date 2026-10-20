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
 * BoxCoxTargetClassifier.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import weka.classifiers.SingleClassifierEnhancer;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author Dale
 */
public class BoxCoxTargetClassifier
  extends SingleClassifierEnhancer
  implements TechnicalInformationHandler {

  private static final long serialVersionUID = 1622445547504697157L;

  protected double m_lambda = 0.65;

  protected double m_sigma2_z = 0.0;

  protected boolean m_unbias = true;

  /**
   * Returns a string describing classifier
   * @return a description suitable for
   * displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Applies the Box-Cox transform to the target.\n"
	+ "For more information, see:\n\n" + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing detailed
   * information about the technical background of this class, e.g., paper
   * reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(TechnicalInformation.Field.AUTHOR, "Wikipedia");
    result.setValue(TechnicalInformation.Field.TITLE, "Box–Cox transformation");
    result.setValue(Field.HTTP, "https://en.wikipedia.org/wiki/Power_transform#Box%E2%80%93Cox_transformation");

    return result;
  }

  /**
   * Sets the lambda parameter.
   *
   * @param value	the lambda value
   */
  public void setLambda(double value) {
    m_lambda = value;
  }

  /**
   * Returns the lambda parameter.
   *
   * @return		the lambda value
   */
  public double getLambda() {
    return m_lambda;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String lambdaTipText() {
    return "The lambda parameter.";
  }

  /**
   * Sets whether to unbias the training residual variance in z-space.
   *
   * @param unbias	true if to remove bias
   */
  public void setUnbias(boolean unbias) {
    m_unbias = unbias;
  }

  /**
   * Returns whether to unbias the training residual variance in z-space.
   *
   * @return		true if to remove bias
   */
  public boolean getUnbias() {
    return m_unbias;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String unbiasTipText() {
    return "Whether to unbias the training residual variance in z-space.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> options = new Vector<>();

    options.addElement(new Option("\tBox-Cox lambda (default 0.65)", "L", 1, "-L <double>"));

    options.addElement(new Option("\tDisable Taylor variance unbiasing", "no-unbias", 0, "-no-unbias"));

    Enumeration<Option> superOptions = super.listOptions();
    while (superOptions.hasMoreElements())
      options.addElement(superOptions.nextElement());

    return options.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String lambdaStr = Utils.getOption('L', options);
    if (lambdaStr.length() != 0)
      setLambda(Double.parseDouble(lambdaStr));
    else
      setLambda(0.65);

    setUnbias(!Utils.getFlag("no-unbias", options));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the Classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String> options = new Vector<>();
    options.add("-L");
    options.add("" + getLambda());
    if (!getUnbias())
      options.add("-no-unbias");
    Collections.addAll(options, super.getOptions());
    return options.toArray(new String[0]);
  }

  /**
   * Returns default capabilities of the base classifier.
   *
   * @return      the capabilities of the base classifier
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();
    result.disableAll();
    result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capabilities.Capability.NUMERIC_CLASS);
    result.enable(Capabilities.Capability.MISSING_VALUES);
    return result;
  }

  /**
   * Generates a classifier.
   *
   * @param data set of instances serving as training data
   * @throws Exception if the classifier has not been generated successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    getCapabilities().testWithFail(data);

    // 1. Clone data structure and transform class values to z-space
    Instances zData = new Instances(data);

    for (int i = 0; i < zData.numInstances(); i++) {
      Instance inst = zData.instance(i);
      if (!inst.classIsMissing()) {
	double y = Math.max(inst.classValue(), 0.0);
	double z = (m_lambda == 0.0)
		     ? Math.log(y + 1e-9)
		     : (Math.pow(y, m_lambda) - 1.0) / m_lambda;
	inst.setClassValue(z);
      }
    }

    // 2. Train the base classifier on z
    m_Classifier.buildClassifier(zData);

    // 3. Compute sigma2_z (training residual variance in z-space)
    if (m_unbias) {
      double sumSq = 0.0;
      int count = 0;
      for (int i = 0; i < zData.numInstances(); i++) {
	Instance inst = zData.instance(i);
	if (!inst.classIsMissing()) {
	  double zPred = m_Classifier.classifyInstance(inst);
	  double diff = inst.classValue() - zPred;
	  sumSq += diff * diff;
	  count++;
	}
      }
      m_sigma2_z = (count > 1) ? (sumSq / (count - 1)) : 0.0;
    }
    else {
      m_sigma2_z = 0.0;
    }
  }

  /**
   * Classifies the given test instance.
   *
   * @param instance the instance to be classified
   * @return the predicted most likely class for the instance or Utils.missingValue() if no prediction is made
   * @throws Exception if an error occurred during the prediction
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    // 1. Predict in z-space
    double zHat = m_Classifier.classifyInstance(instance);

    // 2. De-transform back to original physical units
    if (m_lambda == 0.0)
      return Math.exp(zHat);

    double baseInv = Math.max(m_lambda * zHat + 1.0, 1e-6);
    double exactInv = Math.pow(baseInv, 1.0 / m_lambda);

    if (m_unbias && m_sigma2_z > 0.0) {
      double unbiasFactor = 1.0 + 0.5 * (1.0 - m_lambda) * m_sigma2_z / (baseInv * baseInv);
      return exactInv * unbiasFactor;
    }

    return exactInv;
  }

  /**
   * Main method for executing this classifier.
   *
   * @param args the arguments for execution
   */
  public static void main(String [] args) {
    runClassifier(new BoxCoxTargetClassifier(), args);
  }
}