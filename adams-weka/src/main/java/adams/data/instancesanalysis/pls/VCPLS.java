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
 * VCPLS.java
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
 * Variance constrained partial least squares.<br>
 * <br>
 * For more information see:<br>
 * Xiubao Jiang, Xinge You, Shujian Yu, Dacheng Tao, C.L. Philip Chen, Yiu-ming Cheung (2015). Variance constrained partial least squares. Chemometrics and Intelligent Laboratory Systems. 145:60-71. URL http:&#47;&#47;dx.doi.org&#47;10.1016&#47;j.chemolab.2015.04.014
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;article{XiubaoJiang2015,
 *    author = {Xiubao Jiang, Xinge You, Shujian Yu, Dacheng Tao, C.L. Philip Chen, Yiu-ming Cheung},
 *    journal = {Chemometrics and Intelligent Laboratory Systems},
 *    pages = {60-71},
 *    title = {Variance constrained partial least squares},
 *    volume = {145},
 *    year = {2015},
 *    URL = {http:&#47;&#47;dx.doi.org&#47;10.1016&#47;j.chemolab.2015.04.014},
 *    HTTP = {http:&#47;&#47;or.nsfc.gov.cn&#47;bitstream&#47;00001903-5&#47;485833&#47;1&#47;1000013952154.pdf}
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
 * <pre>-lambda &lt;double&gt; (property: lambda)
 * &nbsp;&nbsp;&nbsp;The lambda parameter.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class VCPLS
  extends AbstractSingleClassPLS {

  private static final long serialVersionUID = -1605633160253194760L;

  /** the actual algorithm. */
  protected com.github.waikatodatamining.matrix.algorithm.pls.VCPLS m_VCPLS;

  /** the lambda value. */
  protected double m_Lambda;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Variance constrained partial least squares.\n\n"
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
    result.setValue(Field.AUTHOR, "Xiubao Jiang, Xinge You, Shujian Yu, Dacheng Tao, C.L. Philip Chen, Yiu-ming Cheung");
    result.setValue(Field.YEAR, "2015");
    result.setValue(Field.TITLE, "Variance constrained partial least squares");
    result.setValue(Field.JOURNAL, "Chemometrics and Intelligent Laboratory Systems");
    result.setValue(Field.VOLUME, "145");
    result.setValue(Field.PAGES, "60-71");
    result.setValue(Field.URL, "http://dx.doi.org/10.1016/j.chemolab.2015.04.014");
    result.setValue(Field.HTTP, "http://or.nsfc.gov.cn/bitstream/00001903-5/485833/1/1000013952154.pdf");

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
      1.0);
  }

  /**
   * Sets the lambda parameter.
   *
   * @param value	the lambda
   */
  public void setLambda(double value) {
    m_Lambda = value;
    reset();
  }

  /**
   * Returns the lambda parameter.
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
    return "The lambda parameter.";
  }

  /**
   * Returns the all the available matrices.
   *
   * @return		the names of the matrices
   */
  @Override
  public String[] getMatrixNames() {
    return m_VCPLS.getMatrixNames();
  }

  /**
   * Returns the matrix with the specified name.
   *
   * @param name	the name of the matrix
   * @return		the matrix, null if not available
   */
  @Override
  public Matrix getMatrix(String name) {
    return MatrixHelper.matrixAlgoToWeka(m_VCPLS.getMatrix(name));
  }

  /**
   * Whether the algorithm supports return of loadings.
   *
   * @return		true if supported
   * @see		#getLoadings()
   */
  @Override
  public boolean hasLoadings() {
    return m_VCPLS.hasLoadings();
  }

  /**
   * Returns the loadings, if available.
   *
   * @return		the loadings, null if not available
   */
  @Override
  public Matrix getLoadings() {
    return MatrixHelper.matrixAlgoToWeka(m_VCPLS.getLoadings());
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
      m_VCPLS = new com.github.waikatodatamining.matrix.algorithm.pls.VCPLS();
      m_VCPLS.setNumComponents(m_NumComponents);
      m_VCPLS.setPreprocessingType(com.github.waikatodatamining.matrix.core.PreprocessingType.NONE);
      m_VCPLS.setLambda(m_Lambda);
      error = m_VCPLS.initialize(X, y);
      if (error != null)
	throw new Exception(error);
    }
    X_new = m_VCPLS.transform(X);

    if (m_PredictionType == PredictionType.ALL)
      y_new = m_VCPLS.predict(X);
    else
      y_new = y;

    return MatrixHelper.toInstances(getOutputFormat(), MatrixHelper.matrixAlgoToWeka(X_new), MatrixHelper.matrixAlgoToWeka(y_new));
  }
}
