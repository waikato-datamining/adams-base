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
 * MathExprClassRegressor.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import adams.parser.MathematicalExpression;
import adams.parser.MathematicalExpressionText;
import weka.classifiers.SingleClassifierEnhancer;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Applies the 'transform' expression to the class attribute in the data for training the based classifier and the 'inverse' expression at prediction time to convert the predicted class value back into input space.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -transform &lt;expression&gt;
 *  The expression to transform the class value.
 *  (default: log(1+X))</pre>
 *
 * <pre> -inverse &lt;expression&gt;
 *  The expression to inverse transform the predicted class value.
 *  (default: exp(X)-1)</pre>
 *
 * <pre> -W &lt;classifier name&gt;
 *  Full name of base classifier.
 *  (default: weka.classifiers.rules.ZeroR)</pre>
 *
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 *
 * <pre> -num-decimal-places
 *  The number of decimal places for the output of numbers in the model (default 2).</pre>
 *
 * <pre> -batch-size
 *  The desired batch size for batch prediction  (default 100).</pre>
 *
 * <pre>
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
 *
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 *
 * <pre> -num-decimal-places
 *  The number of decimal places for the output of numbers in the model (default 2).</pre>
 *
 * <pre> -batch-size
 *  The desired batch size for batch prediction  (default 100).</pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MathExprClassRegressor
  extends SingleClassifierEnhancer {

  /** suid. */
  private static final long serialVersionUID = -6941274159321491218L;

  public final static String TRANSFORM = "log(1+X)";

  public final static String INVERSE = "exp(X)-1";

  /** the expression to transform the class value. */
  protected MathematicalExpressionText m_Transform = new MathematicalExpressionText(TRANSFORM);

  /** the expression to inverse transform the class value. */
  protected MathematicalExpressionText m_Inverse = new MathematicalExpressionText(INVERSE);

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Applies the 'transform' expression to the class attribute in the data for training "
	     + "the based classifier and the 'inverse' expression at prediction time to convert "
	     + "the predicted class value back into input space.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector 	result;
    Enumeration enm;

    result = new Vector();

    result.addElement(new Option(
      "\tThe expression to transform the class value.\n"
	+ "\t(default: " + TRANSFORM + ")",
      "transform", 1, "-transform <expression>"));

    result.addElement(new Option(
      "\tThe expression to inverse transform the predicted class value.\n"
	+ "\t(default: " + INVERSE + ")",
      "inverse", 1, "-inverse <expression>"));

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String 	tmpStr;

    tmpStr = Utils.getOption("transform", options);
    if (!tmpStr.isEmpty())
      setTransform(new MathematicalExpressionText(tmpStr));
    else
      setTransform(new MathematicalExpressionText(TRANSFORM));

    tmpStr = Utils.getOption("inverse", options);
    if (!tmpStr.isEmpty())
      setInverse(new MathematicalExpressionText(tmpStr));
    else
      setInverse(new MathematicalExpressionText(INVERSE));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    Vector<String>	result;

    result = new Vector<>();

    result.add("-transform");
    result.add(getTransform().stringValue());

    result.add("-inverse");
    result.add(getInverse().stringValue());

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[0]);
  }

  /**
   * Set the mathematical expression for transforming the class value for the base classifier.
   *
   * @param value 	the expression
   */
  public void setTransform(MathematicalExpressionText value) {
    m_Transform = value;
  }

  /**
   * Get the mathematical expression for transforming the class value for the base classifier.
   *
   * @return 		the expression
   */
  public MathematicalExpressionText getTransform() {
    return m_Transform;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String transformTipText() {
    return "The mathematical expression for transforming the class value for the base classifier.";
  }

  /**
   * Set the mathematical expression for inverse transforming the class value for the base classifier.
   *
   * @param value 	the expression
   */
  public void setInverse(MathematicalExpressionText value) {
    m_Inverse = value;
  }

  /**
   * Get the mathematical expression for inverse transforming the class value for the base classifier.
   *
   * @return 		the expression
   */
  public MathematicalExpressionText getInverse() {
    return m_Inverse;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String inverseTipText() {
    return "The mathematical expression for inverse transforming the predicted class value from the base classifier back into input space.";
  }

  /**
   * Transforms the instance.
   *
   * @param inst	the instance to transform
   * @return		the transformed instance
   * @throws Exception	if evaluation of formula fails
   */
  protected Instance transform(Instance inst) throws Exception {
    double[] vals = inst.toDoubleArray();
    vals[inst.classIndex()] = transform(vals[inst.classIndex()]);
    Instance newInst = new DenseInstance(inst.weight(), vals);
    newInst.setDataset(inst.dataset());
    return newInst;
  }

  /**
   * Transforms the value.
   *
   * @param x		the value to transform
   * @return		the computed value
   * @throws Exception	if evaluation of formula fails
   */
  protected double transform(double x) throws Exception {
    HashMap 	symbols;

    symbols = new HashMap();
    symbols.put("X", x);
    return MathematicalExpression.evaluate(m_Transform.getValue(), symbols);
  }

  /**
   * Inverse transforms the value.
   *
   * @param x		the value to inverse transform
   * @return		the computed value
   * @throws Exception	if evaluation of formula fails
   */
  protected double inverse(double x) throws Exception {
    HashMap 	symbols;

    symbols = new HashMap();
    symbols.put("X", x);
    return MathematicalExpression.evaluate(m_Inverse.getValue(), symbols);
  }

  /**
   * Builds the classifier.
   *
   * @param data	the training data
   * @throws Exception	if something goes wrong
   */
  public void buildClassifier(Instances data) throws Exception {
    Instances 	transformed;
    int		i;

    getCapabilities().testWithFail(data);

    transformed = new Instances(data, data.numInstances());
    for (i = 0; i < data.numInstances(); i++)
      transformed.add(transform(data.instance(i)));

    if (getDebug())
      System.out.println(transformed);

    m_Classifier.buildClassifier(transformed);
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
    return inverse(result);
  }

  /**
   * Returns description of classifier.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    return m_Classifier.toString();
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for running this class.
   *
   * @param args the options
   */
  public static void main(String[] args) {
    runClassifier(new MathExprClassRegressor(), args);
  }
}
