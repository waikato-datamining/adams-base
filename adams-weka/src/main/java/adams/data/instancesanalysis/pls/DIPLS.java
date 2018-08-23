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
 * DIPLS.java
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
public class DIPLS
  extends AbstractSingleClassPLS {

  private static final long serialVersionUID = -1605633160253194760L;

  /** the actual algorithm. */
  protected com.github.waikatodatamining.matrix.algorithm.pls.DIPLS m_DIPLS;

  /** lambda */
  protected double m_Lambda;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Domain Invariant Partial Least Squares (DIPLS).\n\n"
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
    result.setValue(Field.AUTHOR, "Ramin Nikzad-Langerodi, Werner Zellinger, Edwin Lughofer, and Susanne Saminger-Platz");
    result.setValue(Field.TITLE, "Domain-Invariant Partial-Least-Squares Regression");
    result.setValue(Field.JOURNAL, "Analytical Chemistry");
    result.setValue(Field.VOLUME, "90");
    result.setValue(Field.NUMBER, "11");
    result.setValue(Field.PAGES, "6693-6701");
    result.setValue(Field.URL, "https://pubs.acs.org/doi/10.1021/acs.analchem.8b00498");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "lambda", "lambda",
      1.0, 1e-8, null);
  }

  /**
   * Sets the lambda.
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
   * Returns the lambda.
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
    return "The lambda (> 0).";
  }

  /**
   * Returns the all the available matrices.
   *
   * @return		the names of the matrices
   */
  @Override
  public String[] getMatrixNames() {
    return m_DIPLS.getMatrixNames();
  }

  /**
   * Returns the matrix with the specified name.
   *
   * @param name	the name of the matrix
   * @return		the matrix, null if not available
   */
  @Override
  public Matrix getMatrix(String name) {
    return MatrixHelper.matrixAlgoToWeka(m_DIPLS.getMatrix(name));
  }

  /**
   * Whether the algorithm supports return of loadings.
   *
   * @return		true if supported
   * @see		#getLoadings()
   */
  @Override
  public boolean hasLoadings() {
    return m_DIPLS.hasLoadings();
  }

  /**
   * Returns the loadings, if available.
   *
   * @return		the loadings, null if not available
   */
  @Override
  public Matrix getLoadings() {
    return MatrixHelper.matrixAlgoToWeka(m_DIPLS.getLoadings());
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
      m_DIPLS = new com.github.waikatodatamining.matrix.algorithm.pls.DIPLS();
      m_DIPLS.setNumComponents(m_NumComponents);
      m_DIPLS.setPreprocessingType(com.github.waikatodatamining.matrix.core.PreprocessingType.NONE);
      m_DIPLS.setLambda(m_Lambda);
      error = m_DIPLS.initialize(X, y);
      if (error != null)
	throw new Exception(error);
    }
    X_new = m_DIPLS.transform(X);

    return MatrixHelper.toInstances(getOutputFormat(), MatrixHelper.matrixAlgoToWeka(X_new), MatrixHelper.matrixAlgoToWeka(y));
  }
}
