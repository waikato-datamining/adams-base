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
 * NIPALS.java
 * Copyright (C) 2018-2025 University of Waikato, Hamilton, NZ
 */

package adams.data.instancesanalysis.pls;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import com.github.waikatodatamining.matrix.algorithm.pls.NIPALS.DeflationMode;
import weka.core.Instances;
import weka.core.matrix.Matrix;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Nonlinear Iterative Partial Least Squares (NIPALS).<br>
 * <br>
 * For more information see:<br>
 * scikit-learn. Nonlinear Iterative Partial Least Squares (NIPALS). URL https:&#47;&#47;github.com&#47;scikit-learn&#47;scikit-learn&#47;blob&#47;ed5e127b&#47;sklearn&#47;cross_decomposition&#47;pls_.py#L455
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;misc{missing_id,
 *    author = {scikit-learn},
 *    title = {Nonlinear Iterative Partial Least Squares (NIPALS)},
 *    URL = {https:&#47;&#47;github.com&#47;scikit-learn&#47;scikit-learn&#47;blob&#47;ed5e127b&#47;sklearn&#47;cross_decomposition&#47;pls_.py#L455}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-preprocessing-type &lt;NONE|CENTER|STANDARDIZE&gt; (property: preprocessingType)
 * &nbsp;&nbsp;&nbsp;The type of preprocessing to perform.
 * &nbsp;&nbsp;&nbsp;default: CENTER
 * </pre>
 *
 * <pre>-replace-missing &lt;boolean&gt; (property: replaceMissing)
 * &nbsp;&nbsp;&nbsp;Whether to replace missing values.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-num-components &lt;int&gt; (property: numComponents)
 * &nbsp;&nbsp;&nbsp;The number of components to compute.
 * &nbsp;&nbsp;&nbsp;default: 20
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-prediction-type &lt;NONE|ALL|EXCEPT_CLASS&gt; (property: predictionType)
 * &nbsp;&nbsp;&nbsp;The type of prediction to perform.
 * &nbsp;&nbsp;&nbsp;default: NONE
 * </pre>
 *
 * <pre>-tol &lt;double&gt; (property: tol)
 * &nbsp;&nbsp;&nbsp;The inner NIPALS loop improvement tolerance.
 * &nbsp;&nbsp;&nbsp;default: 1.0E-6
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-max-iter &lt;int&gt; (property: maxIter)
 * &nbsp;&nbsp;&nbsp;The inner NIPALS loop maximum number of iterations.
 * &nbsp;&nbsp;&nbsp;default: 500
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-norm-y-weights &lt;boolean&gt; (property: normYWeights)
 * &nbsp;&nbsp;&nbsp;Whether to normalize Y weights.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-deflation-mode &lt;CANONICAL|REGRESSION&gt; (property: deflationMode)
 * &nbsp;&nbsp;&nbsp;The deflation mode to use.
 * &nbsp;&nbsp;&nbsp;default: REGRESSION
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NIPALS
  extends AbstractSingleClassPLS {

  private static final long serialVersionUID = -1605633160253194760L;

  /** the actual algorithm. */
  protected com.github.waikatodatamining.matrix.algorithm.pls.NIPALS m_NIPALS;

  /** Inner NIPALS loop improvement tolerance */
  protected double m_Tol;

  /** Inner NIPALS loop maximum number of iterations */
  protected int m_MaxIter;

  /** Flag to normalize Y weights */
  protected boolean m_NormYWeights;

  /** X and Y deflation Mode */
  protected DeflationMode m_DeflationMode;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Nonlinear Iterative Partial Least Squares (NIPALS).\n\n"
	     + "For more information see:\n"
	     + getTechnicalInformation();
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
    result.setValue(Field.AUTHOR, "scikit-learn");
    result.setValue(Field.TITLE, "Nonlinear Iterative Partial Least Squares (NIPALS)");
    result.setValue(Field.URL, "https://github.com/scikit-learn/scikit-learn/blob/ed5e127b/sklearn/cross_decomposition/pls_.py#L455");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "tol", "tol",
      1e-6, 0.0, null);

    m_OptionManager.add(
      "max-iter", "maxIter",
      500, 1, null);

    m_OptionManager.add(
      "norm-y-weights", "normYWeights",
      false);

    m_OptionManager.add(
      "deflation-mode", "deflationMode",
      DeflationMode.REGRESSION);
  }

  /**
   * Sets the inner NIPALS loop improvement tolerance.
   *
   * @param value	the tolerance
   */
  public void setTol(double value) {
    if (getOptionManager().isValid("tol", value)) {
      m_Tol = value;
      reset();
    }
  }

  /**
   * Returns the inner NIPALS loop improvement tolerance.
   *
   * @return		the tolerance
   */
  public double getTol() {
    return m_Tol;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String tolTipText() {
    return "The inner NIPALS loop improvement tolerance.";
  }

  /**
   * Sets the inner NIPALS loop maximum number of iterations.
   *
   * @param value	the maximum
   */
  public void setMaxIter(int value) {
    if (getOptionManager().isValid("maxIter", value)) {
      m_MaxIter = value;
      reset();
    }
  }

  /**
   * Returns the NIPALS loop maximum number of iterations.
   *
   * @return		the maximum
   */
  public int getMaxIter() {
    return m_MaxIter;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String maxIterTipText() {
    return "The inner NIPALS loop maximum number of iterations.";
  }

  /**
   * Sets whether to normalize Y weights.
   *
   * @param value	true if to normalize
   */
  public void setNormYWeights(boolean value) {
    m_NormYWeights = value;
    reset();
  }

  /**
   * Returns whether to normalize Y weights.
   *
   * @return		true if to normalized
   */
  public boolean getNormYWeights() {
    return m_NormYWeights;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String normYWeightsTipText() {
    return "Whether to normalize Y weights.";
  }

  /**
   * Sets the deflation mode to use.
   *
   * @param value	the mode
   */
  public void setDeflationMode(DeflationMode value) {
    m_DeflationMode = value;
    reset();
  }

  /**
   * Returns the deflation mode to use.
   *
   * @return		the model
   */
  public DeflationMode getDeflationMode() {
    return m_DeflationMode;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String deflationModeTipText() {
    return "The deflation mode to use.";
  }

  /**
   * Returns the all the available matrices.
   *
   * @return		the names of the matrices
   */
  @Override
  public String[] getMatrixNames() {
    return m_NIPALS.getMatrixNames();
  }

  /**
   * Returns the matrix with the specified name.
   *
   * @param name	the name of the matrix
   * @return		the matrix, null if not available
   */
  @Override
  public Matrix getMatrix(String name) {
    return MatrixHelper.matrixAlgoToWeka(m_NIPALS.getMatrix(name));
  }

  /**
   * Whether the algorithm supports return of loadings.
   *
   * @return		true if supported
   * @see		#getLoadings()
   */
  @Override
  public boolean hasLoadings() {
    return m_NIPALS.hasLoadings();
  }

  /**
   * Returns the loadings, if available.
   *
   * @return		the loadings, null if not available
   */
  @Override
  public Matrix getLoadings() {
    return MatrixHelper.matrixAlgoToWeka(m_NIPALS.getLoadings());
  }

  /**
   * Transforms the data, initializes if necessary.
   *
   * @param data	the data to use
   * @param params 	additional parameters
   * @return		the transformed data
   */
  @Override
  protected Instances doTransform(Instances data, Map<String, Object> params) throws Exception {
    com.github.waikatodatamining.matrix.core.Matrix	X;
    com.github.waikatodatamining.matrix.core.Matrix	y;
    com.github.waikatodatamining.matrix.core.Matrix	X_new;
    com.github.waikatodatamining.matrix.core.Matrix	y_new;
    String 						error;

    X = MatrixHelper.wekaToMatrixAlgo(MatrixHelper.getX(data));
    y = MatrixHelper.wekaToMatrixAlgo(MatrixHelper.getY(data));
    if (!isInitialized()) {
      m_NIPALS = new com.github.waikatodatamining.matrix.algorithm.pls.NIPALS();
      m_NIPALS.setNumComponents(m_NumComponents);
      m_NIPALS.setPreprocessingType(com.github.waikatodatamining.matrix.core.PreprocessingType.NONE);
      m_NIPALS.setTol(m_Tol);
      m_NIPALS.setMaxIter(m_MaxIter);
      m_NIPALS.setNormYWeights(m_NormYWeights);
      m_NIPALS.setDeflationMode(m_DeflationMode);
      error = m_NIPALS.initialize(X, y);
      if (error != null)
	throw new Exception(error);
    }
    X_new = m_NIPALS.transform(X);

    if (m_PredictionType == PredictionType.ALL)
      y_new = m_NIPALS.predict(X);
    else
      y_new = y;

    return MatrixHelper.toInstances(getOutputFormat(), MatrixHelper.matrixAlgoToWeka(X_new), MatrixHelper.matrixAlgoToWeka(y_new));
  }
}
