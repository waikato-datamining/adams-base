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

package adams.data.instancesanalysis.pls;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.core.option.AbstractOptionHandler;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.CapabilitiesHandler;
import weka.core.GenericPLSMatrixAccess;
import weka.core.Instances;
import weka.core.matrix.Matrix;

import java.util.HashMap;
import java.util.Map;

/**
 * Ancestor for PLS implementations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPLS
  extends AbstractOptionHandler
  implements CapabilitiesHandler, TechnicalInformationHandler, GenericPLSMatrixAccess {

  private static final long serialVersionUID = -2619191840396410446L;

  /** whether the scheme has been initialized. */
  protected boolean m_Initialized;

  /** the preprocessing type to perform. */
  protected PreprocessingType m_PreprocessingType;

  /** whether to replace missing values */
  protected boolean m_ReplaceMissing;

  /** the maximum number of components to generate */
  protected int m_NumComponents;

  /** the prediction type to perform. */
  protected PredictionType m_PredictionType;

  /** the output format. */
  protected Instances m_OutputFormat;

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
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "preprocessing-type", "preprocessingType",
      PreprocessingType.CENTER);

    m_OptionManager.add(
      "replace-missing", "replaceMissing",
      false);

    m_OptionManager.add(
      "num-components", "numComponents",
      20, 1, null);

    m_OptionManager.add(
      "prediction-type", "predictionType",
      PredictionType.NONE);
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
   * Sets the type of prediction to perform.
   * Calling this method does not result in a {@link #reset()} call.
   *
   * @param value 	the type
   */
  public void setPredictionType(PredictionType value) {
    m_PredictionType = value;
    //reset();
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
    super.reset();

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
