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
 * PRM.java
 * Copyright (C) 2018-2025 University of Waikato, Hamilton, NZ
 */

package adams.data.instancesanalysis.pls;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import weka.core.Instances;
import weka.core.matrix.Matrix;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Partial robust M-regression (PRM).<br>
 * For more information see:<br>
 * Sven Serneels, Christophe Croux, Peter Filzmoser, Pierre J.Van Espen (2005). Partial robust M-regression. Chemometrics and Intelligent Laboratory Systems. 79:55-64. URL https:&#47;&#47;www.sciencedirect.com&#47;science&#47;article&#47;pii&#47;S0169743905000638
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;article{SvenSerneels2005,
 *    author = {Sven Serneels, Christophe Croux, Peter Filzmoser, Pierre J.Van Espen},
 *    journal = {Chemometrics and Intelligent Laboratory Systems},
 *    pages = {55-64},
 *    title = {Partial robust M-regression},
 *    volume = {79},
 *    year = {2005},
 *    URL = {https:&#47;&#47;www.sciencedirect.com&#47;science&#47;article&#47;pii&#47;S0169743905000638}
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
 * &nbsp;&nbsp;&nbsp;default: NONE
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
 * <pre>-num-simpls-coefficients &lt;int&gt; (property: numSimplsCoefficients)
 * &nbsp;&nbsp;&nbsp;The number of SIMPLS coefficients to keep; &lt;= 0 to keep all.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-c &lt;double&gt; (property: c)
 * &nbsp;&nbsp;&nbsp;The tuning parameter, &gt;0.
 * &nbsp;&nbsp;&nbsp;default: 4.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0E-10
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PRM
  extends AbstractSingleClassPLS {

  private static final long serialVersionUID = -1605633160253194760L;

  /** the actual algorithm. */
  protected com.github.waikatodatamining.matrix.algorithm.pls.PRM m_PRM;

  /** Inner loop improvement tolerance */
  protected double m_Tol;

  /** Inner loop maximum number of iterations */
  protected int m_MaxIter;

  /** the number of SIMPLS coefficients. */
  protected int m_NumSimplsCoefficients;

  /** Tuning parameter. */
  protected double m_C;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Partial robust M-regression (PRM).\n"
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

    result = new TechnicalInformation(Type.ARTICLE);
    result.setValue(Field.AUTHOR, "Sven Serneels, Christophe Croux, Peter Filzmoser, Pierre J.Van Espen");
    result.setValue(Field.YEAR, "2005");
    result.setValue(Field.TITLE, "Partial robust M-regression");
    result.setValue(Field.JOURNAL, "Chemometrics and Intelligent Laboratory Systems");
    result.setValue(Field.VOLUME, "79");
    result.setValue(Field.PAGES, "55-64");
    result.setValue(Field.URL, "https://www.sciencedirect.com/science/article/pii/S0169743905000638");

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
      "num-simpls-coefficients", "numSimplsCoefficients",
      -1, -1, null);

    m_OptionManager.add(
      "c", "c",
      4.0, 1e-10, null);
  }

  /**
   * Returns the default preprocessing type.
   *
   * @return		the default
   */
  protected PreprocessingType getDefaultPreprocessingType() {
    return PreprocessingType.NONE;
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
   * Sets the number of SIMPLS coefficients to keep. <= to keep all.
   *
   * @param value	the coefficients
   */
  public void setNumSimplsCoefficients(int value) {
    if (getOptionManager().isValid("numSimplsCoefficients", value)) {
      m_NumSimplsCoefficients = value;
      reset();
    }
  }

  /**
   * Returns the number of SIMPLS coefficients to keep. <= to keep all.
   *
   * @return		the coefficients
   */
  public int getNumSimplsCoefficients() {
    return m_NumSimplsCoefficients;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String numSimplsCoefficientsTipText() {
    return "The number of SIMPLS coefficients to keep; <= 0 to keep all.";
  }

  /**
   * Tuning parameter.
   *
   * @param value	the C, > 0
   */
  public void setC(double value) {
    if (getOptionManager().isValid("c", value)) {
      m_C = value;
      reset();
    }
  }

  /**
   * Returns the tuning parameter.
   *
   * @return		the C, > 0
   */
  public double getC() {
    return m_C;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String cTipText() {
    return "The tuning parameter, >0.";
  }

  /**
   * Returns the all the available matrices.
   *
   * @return		the names of the matrices
   */
  @Override
  public String[] getMatrixNames() {
    return m_PRM.getMatrixNames();
  }

  /**
   * Returns the matrix with the specified name.
   *
   * @param name	the name of the matrix
   * @return		the matrix, null if not available
   */
  @Override
  public Matrix getMatrix(String name) {
    return MatrixHelper.matrixAlgoToWeka(m_PRM.getMatrix(name));
  }

  /**
   * Whether the algorithm supports return of loadings.
   *
   * @return		true if supported
   * @see		#getLoadings()
   */
  @Override
  public boolean hasLoadings() {
    return m_PRM.hasLoadings();
  }

  /**
   * Returns the loadings, if available.
   *
   * @return		the loadings, null if not available
   */
  @Override
  public Matrix getLoadings() {
    return MatrixHelper.matrixAlgoToWeka(m_PRM.getLoadings());
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
      m_PRM = new com.github.waikatodatamining.matrix.algorithm.pls.PRM();
      m_PRM.setNumComponents(m_NumComponents);
      m_PRM.setPreprocessingType(com.github.waikatodatamining.matrix.core.PreprocessingType.NONE);
      m_PRM.setTol(m_Tol);
      m_PRM.setMaxIter(m_MaxIter);
      m_PRM.setNumSimplsCoefficients(m_NumSimplsCoefficients);
      m_PRM.setC(m_C);
      error = m_PRM.initialize(X, y);
      if (error != null)
	throw new Exception(error);
    }
    X_new = m_PRM.transform(X);

    if (m_PredictionType == PredictionType.ALL)
      y_new = m_PRM.predict(X);
    else
      y_new = y;

    return MatrixHelper.toInstances(getOutputFormat(), MatrixHelper.matrixAlgoToWeka(X_new), MatrixHelper.matrixAlgoToWeka(y_new));
  }
}
