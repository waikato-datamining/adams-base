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
 * LinearRegressionAttributeEval.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package weka.attributeSelection;

import weka.classifiers.functions.LinearRegressionJ;
import weka.core.*;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Uses the coefficients of LinearRegressionJ to determine the importance of the attributes
 * (attribute selection turned off, no elimination of collinear attributes).
 *
 * @author Hisham Abdel Qader (habdelqa at waikato dot ac dot nz)
 */
public class LinearRegressionAttributeEval
  extends ASEvaluation
  implements AttributeEvaluator, OptionHandler {

  /** the degtermined attribute ranking. */
  protected double[] m_Ranking;

  /** the underlying model. */
  protected LinearRegressionJ m_Model;

  /** The ridge parameter */
  protected double m_Ridge = 1.0e-8;

  /** Conserve memory? */
  protected boolean m_Minimal = false;

  /**
   * Whether to output additional statistics such as std. dev. of coefficients
   * and t-stats
   */
  protected boolean m_outputAdditionalStats;

  /**
   * Returns a string describing this attribute evaluator
   *
   * @return a description of the evaluator suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() {
    return "Uses the coefficients of " + LinearRegressionJ.class.getName() + " to determine the "
      + "importance of the attributes (attribute selection turned off, no elimination of collinear attributes):\n"
      + "- absolute value of coefficients\n"
      + "- intercept gets set to zero\n"
      + "- all coefficients normalized (ie sum up to one)";
  }

  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> newVector = new Vector<Option>();

    newVector.addElement(new Option(
      "\tSet ridge parameter (default 1.0e-8).\n", "R", 1, "-R <double>"));

    newVector.addElement(new Option(
      "\tConserve memory, don't keep dataset header and means/stdevs.\n"
        + "\tModel cannot be printed out if this option is enabled."
        + "\t(default: keep data)", "minimal", 0, "-minimal"));

    newVector.addElement(new Option("\tOutput additional statistics.",
      "additional-stats", 0, "-additional-stats"));

    return newVector.elements();
  }

  @Override
  public void setOptions(String[] options) throws Exception {
    String ridgeString = Utils.getOption('R', options);
    if (ridgeString.length() != 0) {
      setRidge(new Double(ridgeString).doubleValue());
    } else {
      setRidge(1.0e-8);
    }
    setMinimal(Utils.getFlag("minimal", options));

    setOutputAdditionalStats(Utils.getFlag("additional-stats", options));
  }

  /**
   * Set whether to output additional statistics (such as std. deviation of
   * coefficients and t-statistics
   *
   * @param additional true if additional stats are to be output
   */
  public void setOutputAdditionalStats(boolean additional) {
    m_outputAdditionalStats = additional;
  }

  /**
   * Sets whether to be more memory conservative or being able to output the
   * model as string.
   *
   * @param value if true memory will be conserved
   */
  public void setMinimal(boolean value) {
    m_Minimal = value;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String ridgeTipText() {
    return "The value of the Ridge parameter.";
  }

  /**
   * Get the value of Ridge.
   *
   * @return Value of Ridge.
   */
  public double getRidge() {

    return m_Ridge;
  }

  /**
   * Set the value of Ridge.
   *
   * @param newRidge Value to assign to Ridge.
   */
  public void setRidge(double newRidge) {

    m_Ridge = newRidge;
  }

  @Override
  public String[] getOptions() {
    Vector<String> result = new Vector<String>();

    result.add("-R");
    result.add("" + getRidge());

    if (getMinimal()) {
      result.add("-minimal");
    }

    if (getOutputAdditionalStats()) {
      result.add("-additional-stats");
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns whether to be more memory conservative or being able to output the
   * model as string.
   *
   * @return true if memory conservation is preferred over outputting model
   *         description
   */
  public boolean getMinimal() {
    return m_Minimal;
  }

  /**
   * Get whether to output additional statistics (such as std. deviation of
   * coefficients and t-statistics
   *
   * @return true if additional stats are to be output
   */
  public boolean getOutputAdditionalStats() {
    return m_outputAdditionalStats;
  }

  /**
   * Returns the capabilities of this evaluator.
   *
   * @return the capabilities of this evaluator
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = new LinearRegressionJ().getCapabilities();
    result.setOwner(this);

    return result;
  }

  /**
   * Generates a attribute evaluator. Has to initialize all fields of the
   * evaluator that are not being set via options.
   *
   * @param instances set of instances serving as training data
   * @exception Exception if the evaluator has not been generated successfully
   */
  @Override
  public void buildEvaluator(Instances instances) throws Exception {
    getCapabilities().testWithFail(instances);

    m_Model = new LinearRegressionJ();
    // disable eliminating colinear attributes
    m_Model.setEliminateColinearAttributes(false);
    // turn off attribute selection
    m_Model.setAttributeSelectionMethod(new SelectedTag(LinearRegressionJ.SELECTION_NONE, LinearRegressionJ.TAGS_SELECTION));
    // user supplied options
    m_Model.setRidge(m_Ridge);
    m_Model.setMinimal(m_Minimal);
    m_Model.setOutputAdditionalStats(m_outputAdditionalStats);

    // build model
    m_Model.buildClassifier(instances);

    double[] coefficients = m_Model.coefficients();
    for (int i = 0; i < coefficients.length; i++)
      coefficients[i] = Math.abs(coefficients[i]);
    coefficients[coefficients.length - 1] = 0;  // clear intercept
    Utils.normalize(coefficients);

    m_Ranking = coefficients;
  }

  /**
   * evaluates an individual attribute
   *
   * @param i the index of the attribute to be evaluated
   * @return the "merit" of the attribute
   * @exception Exception if the attribute could not be evaluated
   */
  @Override
  public double evaluateAttribute(int i) throws Exception {
    return m_Ranking[i];
  }

  /**
   * Outputs the underlying linear regression model.
   *
   * @return the model output
   */
  @Override
  public String toString() {
    return m_Model.toString();
  }

  /**
   * Main method for running this class from commandline.
   *
   * @param args the options
   */
  public static void main(String[] args) {
    runEvaluator(new LinearRegressionAttributeEval(), args);
  }
}
