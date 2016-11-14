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

/**
 * AbstractPLS.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package weka.filters.supervised.attribute.pls;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.CapabilitiesHandler;
import weka.core.GenericPLSMatrixAccess;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.WekaOptionUtils;
import weka.core.matrix.Matrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Ancestor for PLS implementations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPLS
  implements Serializable, OptionHandler, CapabilitiesHandler,
             TechnicalInformationHandler, GenericPLSMatrixAccess {

  private static final long serialVersionUID = -2619191840396410446L;

  public static final String OPTION_DEBUG = "debug";

  public static final String OPTION_PREPROCESSING = "preprocessing";

  public static final String OPTION_C = "C";

  public static final String OPTION_PREDICTION = "prediction";

  /**
   * The preprocessing type.
   */
  public enum PreprocessingType {
    NONE,
    CENTER,
    STANDARDIZE
  }

  /**
   * The type of prediction to perform.
   */
  public enum PredictionType {
    /** no prediction at all. */
    NONE,
    /** predict all Ys. */
    ALL,
    /** predict all Ys except class attribute. */
    EXCEPT_CLASS
  }

  /** whether the scheme has been initialized. */
  protected boolean m_Initialized;

  /** Whether the classifier is run in debug mode. */
  protected boolean m_Debug = getDefaultDebug();

  /** the preprocessing type to perform. */
  protected PreprocessingType m_PreprocessingType = getDefaultPreprocessingType();

  /** whether to replace missing values */
  protected boolean m_ReplaceMissing = getDefaultReplaceMissing();

  /** the maximum number of components to generate */
  protected int m_NumComponents = getDefaultNumComponents();

  /** the prediction type to perform. */
  protected PredictionType m_PredictionType = getDefaultPredictionType();

  /** the output format. */
  protected Instances m_OutputFormat;

  /**
   * Returns a string describing this class.
   *
   * @return 		a description of the class suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public abstract String globalInfo();

  /**
   * Returns an instance of a TechnicalInformation object, containing detailed
   * information about the technical background of this class, e.g., paper
   * reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  @Override
  public abstract TechnicalInformation getTechnicalInformation();

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<>();

    WekaOptionUtils.addOption(result, debugTipText(), "" + getDefaultDebug(), OPTION_DEBUG);
    WekaOptionUtils.addOption(result, preprocessingTypeTipText(), "" + getDefaultPreprocessingType(), OPTION_PREPROCESSING);
    WekaOptionUtils.addOption(result, numComponentsTipText(), "" + getDefaultNumComponents(), OPTION_C);
    WekaOptionUtils.addOption(result, predictionTypeTipText(), "" + getDefaultPredictionType(), OPTION_PREDICTION);

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setDebug(Utils.getFlag(OPTION_DEBUG, options));
    setPreprocessingType((PreprocessingType) WekaOptionUtils.parse(options, OPTION_PREPROCESSING, getDefaultPreprocessingType()));
    setNumComponents(WekaOptionUtils.parse(options, OPTION_C, getDefaultNumComponents()));
    setPredictionType((PredictionType) WekaOptionUtils.parse(options, OPTION_PREDICTION, getDefaultPredictionType()));
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();

    WekaOptionUtils.add(result, OPTION_DEBUG, getDebug());
    WekaOptionUtils.add(result, OPTION_PREPROCESSING, getPreprocessingType());
    WekaOptionUtils.add(result, OPTION_C, getNumComponents());
    WekaOptionUtils.add(result, OPTION_PREDICTION, getPredictionType());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns the default debug setting.
   *
   * @return		the default
   */
  protected boolean getDefaultDebug() {
    return false;
  }

  /**
   * Set debugging mode.
   *
   * @param debug 	true if debug output should be printed
   */
  public void setDebug(boolean debug) {
    m_Debug = debug;
    reset();
  }

  /**
   * Get whether debugging is turned on.
   *
   * @return 		true if debugging output is on
   */
  public boolean getDebug() {
    return m_Debug;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String debugTipText() {
    return "If enabled, additional info may be output to the console.";
  }

  /**
   * Returns the default preprocessing type.
   *
   * @return		the default
   */
  protected PreprocessingType getDefaultPreprocessingType() {
    return PreprocessingType.CENTER;
  }

  /**
   * Sets the type of preprocessing to perform.
   *
   * @param value 	the type
   */
  public void setPreprocessingType(PreprocessingType value) {
    m_PreprocessingType = value;
    reset();
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
   * Returns the default replace missing setting.
   *
   * @return		the default
   */
  protected boolean getDefaultReplaceMissing() {
    return false;
  }

  /**
   * Sets whether to replace missing values.
   *
   * @param value 	if true missing values are replaced with the
   *          		ReplaceMissingValues filter.
   */
  public void setReplaceMissing(boolean value) {
    m_ReplaceMissing = value;
    reset();
  }

  /**
   * Gets whether missing values are replace.
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
   * Returns the default number of components.
   *
   * @return		the default
   */
  protected int getDefaultNumComponents() {
    return 20;
  }

  /**
   * sets the maximum number of attributes to use.
   *
   * @param value 	the maximum number of attributes
   */
  public void setNumComponents(int value) {
    m_NumComponents = value;
    reset();
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
   * Returns the default prediction type.
   *
   * @return		the default
   */
  protected PredictionType getDefaultPredictionType() {
    return PredictionType.NONE;
  }

  /**
   * Sets the type of prediction to perform.
   *
   * @param value 	the type
   */
  public void setPredictionType(PredictionType value) {
    m_PredictionType = value;
    reset();
  }

  /**
   * Returns the type of prediction to perform.
   *
   * @return 		the type
   */
  public PredictionType getPredictionType() {
    return m_PredictionType;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String predictionTypeTipText() {
    return "The type of prediction to perform.";
  }

  /**
   * Resets the scheme.
   */
  public void reset() {
    m_Initialized  = false;
    m_OutputFormat = null;
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = new Capabilities(this);

    // attributes
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.DATE_ATTRIBUTES);
    result.enable(Capability.MISSING_VALUES);

    // class
    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.DATE_CLASS);

    return result;
  }

  /**
   * Returns the all the available matrices.
   *
   * @return		the names of the matrices
   */
  public abstract String[] getMatrixNames();

  /**
   * Returns the matrix with the specified name.
   *
   * @param name	the name of the matrix
   * @return		the matrix, null if not available
   */
  public abstract Matrix getMatrix(String name);

  /**
   * Whether the algorithm supports return of loadings.
   *
   * @return		true if supported
   * @see		#getLoadings()
   */
  public abstract boolean hasLoadings();

  /**
   * Returns the loadings, if available.
   *
   * @return		the loadings, null if not available
   */
  public abstract Matrix getLoadings();

  /**
   * Returns whether the scheme has been initialized.
   *
   * @return		true if initialized
   */
  public boolean isInitialized() {
    return m_Initialized;
  }

  /**
   * Determines the output format based on the input format and returns this.
   *
   * @param input 	the input format to base the output format on
   * @return 		the output format
   * @throws Exception 	in case the determination goes wrong
   */
  public abstract Instances determineOutputFormat(Instances input) throws Exception;

  /**
   * Returns the output format.
   *
   * @return		the output format
   */
  public Instances getOutputFormat() {
    return m_OutputFormat;
  }

  /**
   * Preprocesses the data.
   *
   * @param instances	the data to process
   * @param params 	additional parameters
   * @return		the preprocessed data
   */
  protected abstract Instances preTransform(Instances instances, Map<String,Object> params) throws Exception;

  /**
   * Transforms the data, initializes if necessary.
   *
   * @param data	the data to use
   * @param params 	additional parameters
   * @return		the transformed data
   */
  protected abstract Instances doTransform(Instances data, Map<String,Object> params) throws Exception;

  /**
   * Postprocesses the data.
   *
   * @param instances	the data to process
   * @param params 	additional parameters
   * @return		the postprocessed data
   */
  protected abstract Instances postTransform(Instances instances, Map<String,Object> params) throws Exception;

  /**
   * Transforms the data, initializes if necessary.
   *
   * @param data	the data to use
   * @return		the transformed data
   */
  public Instances transform(Instances data) throws Exception {
    Instances		result;
    Map<String,Object> 	params;

    params        = new HashMap<>();
    result        = preTransform(data, params);
    result        = doTransform(result, params);
    result        = postTransform(result, params);
    m_Initialized = true;

    return result;
  }
}
