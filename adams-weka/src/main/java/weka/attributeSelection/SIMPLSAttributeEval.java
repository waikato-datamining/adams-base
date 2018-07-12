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
 * SIMPLSAttributeEval.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package weka.attributeSelection;

import adams.data.instancesanalysis.pls.AbstractSingleClassPLS;
import adams.data.instancesanalysis.pls.PreprocessingType;
import adams.data.instancesanalysis.pls.SIMPLS;
import adams.env.Environment;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.matrix.Matrix;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Uses the first component of SIMPLS to determine the importance of the attributes
 * (defaults: no preprocessing, missing values not replaced, and 20 components)
 *
 * @author Hisham Abdel Qader (habdelqa at waikato dot ac dot nz)
 */

public class SIMPLSAttributeEval extends ASEvaluation
  implements AttributeEvaluator, OptionHandler {

  private static final long serialVersionUID = 3127432070356406114L;

  public enum LoadingsCalculations {
    USE_FIRST_COMPONENT,
  }

  /** the underlying model. */
  protected AbstractSingleClassPLS m_Model;

  /** the preprocessing type to perform. */
  protected PreprocessingType m_PreprocessingType = PreprocessingType.NONE;

  /** the replace missing values parameter. */
  protected boolean m_ReplaceMissing = false;

  /** the number of components parameter. */
  protected int m_NumComponents = 20;

  /** for how to use the loadings. */
  protected LoadingsCalculations m_LoadingsCalculations = LoadingsCalculations.USE_FIRST_COMPONENT;

  /** the determined attribute ranking. */
  protected double[] m_Ranking;

  /**
   * Returns a string describing this attribute evaluator
   *
   * @return a description of the evaluator suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() {
    return "Uses the first component of " + SIMPLS.class.getName() + " to determine the "
      + "importance of the attributes (no preprocessing, missing values not replaced, and 20 components):\n"
      + "- absolute value of coefficients\n"
      + "- all coefficients normalized (ie sum up to one)";
  }

  /**
   * Returns an enumeration of all the available options..
   *
   * @return an enumeration of all available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> newVector = new Vector<Option>();

    newVector.addElement(new Option(
      "\tSet preprocessing type (default: NONE).\n", "P", 1, "-P <String>"));

    newVector.addElement(new Option(
      "\tReplace missing values (default: false).", "R", 0, "-R"));

    newVector.addElement(new Option("\tSet the number of components (default: 20)",
      "N", 1, "-N <int>"));

    return newVector.elements();
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @exception Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String preProcessingString = Utils.getOption('P', options);
    if (preProcessingString.length() != 0) {
      setPreprocessingType(PreprocessingType.valueOf(preProcessingString));
    } else {
      setPreprocessingType(PreprocessingType.NONE);
    }

    setReplaceMissing(Utils.getFlag("R", options));

    String numComponentsString = Utils.getOption('N', options);
    if (numComponentsString.length() != 0) {
      setNumComponents(Integer.parseInt(numComponentsString));
    } else {
      setNumComponents(20);
    }
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    Vector<String> result = new Vector<String>();

    result.add("-P");
    result.add("" + getPreprocessingType());

    if (getReplaceMissing()) {
      result.add("-R");
    }

    result.add("-N");
    result.add("" + getNumComponents());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the type of preprocessing to perform.
   *
   * @param value 	the type
   */
  public void setPreprocessingType(PreprocessingType value) {
    m_PreprocessingType = value;
  }

  /**
   * Returns the type of preprocessing to perform.
   *
   * @return 		the type
   */
  public PreprocessingType getPreprocessingType() {
    return m_PreprocessingType;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String preprocessingTypeTipText() {
    return "The type of preprocessing to perform.";
  }

  /**
   * Sets whether to replace missing values.
   *
   * @param value 	if true missing values are replaced with the
   *          		ReplaceMissingValues filter.
   */
  public void setReplaceMissing(boolean value) {
    m_ReplaceMissing = value;
  }

  /**
   * Gets whether missing values are replaced.
   *
   * @return 		true if missing values are replaced with the ReplaceMissingValues
   *         		filter
   */
  public boolean getReplaceMissing() {
    return m_ReplaceMissing;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String replaceMissingTipText() {
    return "Whether to replace missing values.";
  }

  /**
   * sets the maximum number of attributes to use.
   *
   * @param value 	the maximum number of attributes
   */
  public void setNumComponents(int value) {
    m_NumComponents = value;
  }

  /**
   * returns the maximum number of attributes to use.
   *
   * @return 		the current maximum number of attributes
   */
  public int getNumComponents() {
    return m_NumComponents;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String numComponentsTipText() {
    return "The number of components to compute.";
  }

  /**
   * sets the maximum number of attributes to use.
   *
   * @param value 	the maximum number of attributes
   */
  public void setLoadingsCalculations(LoadingsCalculations value) {
    m_LoadingsCalculations = value;
  }

  /**
   * returns the maximum number of attributes to use.
   *
   * @return 		the current maximum number of attributes
   */
  public LoadingsCalculations getLoadingsCalculations() {
    return m_LoadingsCalculations;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String loadingsCalculationsTipText() {
    return "The number of components to compute.";
  }

  /**
   * Creates a new instance of a PLS algorrithm.
   *
   * @return		the instance
   */
  protected AbstractSingleClassPLS newModel() {
    return new SIMPLS();
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

    m_Model = newModel();

    // user supplied options
    m_Model.setPreprocessingType(m_PreprocessingType);
    m_Model.setReplaceMissing(m_ReplaceMissing);
    m_Model.setNumComponents(m_NumComponents);

    // build model
    m_Model.determineOutputFormat(instances);
    m_Model.transform(instances);

    Matrix coefficientsMatrix = m_Model.getLoadings();
    double[] coefficients = new double[coefficientsMatrix.getRowDimension()];

    switch (m_LoadingsCalculations) {
      case USE_FIRST_COMPONENT:
	for (int i = 0; i < coefficients.length; i++)
	  coefficients[i] = Math.abs(coefficientsMatrix.get(i, 0));
	Utils.normalize(coefficients);
	break;

      default:
	throw new IllegalStateException("Unhandled loadings calculations: " + m_LoadingsCalculations);
    }

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
    Environment.setEnvironmentClass(Environment.class);
    runEvaluator(new SIMPLSAttributeEval(), args);
  }
}
