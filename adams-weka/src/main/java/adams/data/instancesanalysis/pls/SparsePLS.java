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
 * SparsePLS.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
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
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SparsePLS
  extends AbstractSingleClassPLS {

  private static final long serialVersionUID = -1605633160253194760L;

  /** the actual algorithm. */
  protected com.github.waikatodatamining.matrix.algorithm.pls.SparsePLS m_SparsePLS;

  /** Inner NIPALS loop improvement tolerance */
  protected double m_Tol;

  /** Inner NIPALS loop maximum number of iterations */
  protected int m_MaxIter;

  /** Sparsity parameter. Determines sparseness. */
  protected double m_Lambda;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Nonlinear Iterative Partial Least Squares (SparsePLS).\n"
      + "Automatically standardizes X and Y internally.\n\n"
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
    result.setValue(Field.AUTHOR, "Chun H, Keles S.");
    result.setValue(Field.YEAR, "2010");
    result.setValue(Field.TITLE, "Sparse partial least squares regression for simultaneous dimension reduction and variable selection");
    result.setValue(Field.JOURNAL, "Royal Statistical Society Series B, Statistical Methodology");
    result.setValue(Field.VOLUME, "1");
    result.setValue(Field.PAGES, "3-25");
    result.setValue(Field.URL, "https://www.ncbi.nlm.nih.gov/pmc/articles/PMC2810828/");

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
      1e-7, 0.0, null);

    m_OptionManager.add(
      "max-iter", "maxIter",
      500, 1, null);

    m_OptionManager.add(
      "lambda", "lambda",
      0.5, 0.0, null);
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
   * Sets sparsity parameter; determines sparseness.
   *
   * @param value	the lambda
   */
  public void setLambda(double value) {
    if (getOptionManager().isValid("lambda", value)) {
      m_Lambda = value;
      reset();
    }
  }

  /**
   * Returns the sparsity parameter; determines sparseness.
   *
   * @return		the lambda
   */
  public double getLambda() {
    return m_Lambda;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String lambdaTipText() {
    return "The sparsity parameter; determines sparseness.";
  }

  /**
   * Returns the all the available matrices.
   *
   * @return		the names of the matrices
   */
  @Override
  public String[] getMatrixNames() {
    return m_SparsePLS.getMatrixNames();
  }

  /**
   * Returns the matrix with the specified name.
   *
   * @param name	the name of the matrix
   * @return		the matrix, null if not available
   */
  @Override
  public Matrix getMatrix(String name) {
    return MatrixHelper.matrixAlgoToWeka(m_SparsePLS.getMatrix(name));
  }

  /**
   * Whether the algorithm supports return of loadings.
   *
   * @return		true if supported
   * @see		#getLoadings()
   */
  @Override
  public boolean hasLoadings() {
    return m_SparsePLS.hasLoadings();
  }

  /**
   * Returns the loadings, if available.
   *
   * @return		the loadings, null if not available
   */
  @Override
  public Matrix getLoadings() {
    return MatrixHelper.matrixAlgoToWeka(m_SparsePLS.getLoadings());
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
    String 						error;

    X = MatrixHelper.wekaToMatrixAlgo(MatrixHelper.getX(data));
    y = MatrixHelper.wekaToMatrixAlgo(MatrixHelper.getY(data));
    if (!isInitialized()) {
      m_SparsePLS = new com.github.waikatodatamining.matrix.algorithm.pls.SparsePLS();
      m_SparsePLS.setNumComponents(m_NumComponents);
      m_SparsePLS.setPreprocessingType(com.github.waikatodatamining.matrix.core.PreprocessingType.NONE);
      m_SparsePLS.setTol(m_Tol);
      m_SparsePLS.setMaxIter(m_MaxIter);
      m_SparsePLS.setLambda(m_Lambda);
      error = m_SparsePLS.initialize(X, y);
      if (error != null)
	throw new Exception(error);
    }
    X_new = m_SparsePLS.transform(X);

    return MatrixHelper.toInstances(getOutputFormat(), MatrixHelper.matrixAlgoToWeka(X_new), MatrixHelper.matrixAlgoToWeka(y));
  }
}
