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
 * OPLS.java
 * Copyright (C) 2018-2025 University of Waikato, Hamilton, NZ
 */

package adams.data.instancesanalysis.pls;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.option.OptionUtils;
import weka.core.Instances;
import weka.core.matrix.Matrix;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Orthogonal Projections to latent structures (O-PLS).<br>
 * <br>
 * For more informatio see:<br>
 * Johan Trygg, Svante Wold (2001). Orthogonal projections to latent structures (O-PLS). JOURNAL OF CHEMOMETRICS. 16:119-128. URL https:&#47;&#47;onlinelibrary.wiley.com&#47;doi&#47;pdf&#47;10.1002&#47;cem.695
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;article{Trygg2001,
 *    author = {Johan Trygg and Svante Wold},
 *    journal = {JOURNAL OF CHEMOMETRICS},
 *    pages = {119-128},
 *    title = {Orthogonal projections to latent structures (O-PLS)},
 *    volume = {16},
 *    year = {2001},
 *    URL = {https:&#47;&#47;onlinelibrary.wiley.com&#47;doi&#47;pdf&#47;10.1002&#47;cem.695}
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
 * <pre>-base &lt;com.github.waikatodatamining.matrix.algorithm.pls.AbstractPLS&gt; (property: base)
 * &nbsp;&nbsp;&nbsp;The base PLS algorithm to use.
 * &nbsp;&nbsp;&nbsp;default: com.github.waikatodatamining.matrix.algorithm.pls.PLS1 -debug false -num-components 5 -preprocessing-type NONE
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class OPLS
  extends AbstractSingleClassPLS {

  private static final long serialVersionUID = -1605633160253194760L;

  /** the base PLS algorithm. */
  protected com.github.waikatodatamining.matrix.algorithm.pls.AbstractPLS m_Base;

  /** the actual algorithm. */
  protected com.github.waikatodatamining.matrix.algorithm.pls.OPLS m_OPLS;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Orthogonal Projections to latent structures (O-PLS).\n\n"
	     + "For more informatio see:\n"
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
    result.setValue(Field.AUTHOR, "Johan Trygg and Svante Wold");
    result.setValue(Field.YEAR, "2001");
    result.setValue(Field.TITLE, "Orthogonal projections to latent structures (O-PLS)");
    result.setValue(Field.JOURNAL, "JOURNAL OF CHEMOMETRICS");
    result.setValue(Field.VOLUME, "16");
    result.setValue(Field.PAGES, "119-128");
    result.setValue(Field.URL, "https://onlinelibrary.wiley.com/doi/pdf/10.1002/cem.695");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "base", "base",
      new com.github.waikatodatamining.matrix.algorithm.pls.PLS1());
  }

  /**
   * Sets the base PLS algorithm to use.
   *
   * @param value	the base algorithm
   */
  public void setBase(com.github.waikatodatamining.matrix.algorithm.pls.AbstractPLS value) {
    m_Base = value;
    reset();
  }

  /**
   * Sets the base PLS algorithm to use.
   *
   * @return		the base algorithm
   */
  public com.github.waikatodatamining.matrix.algorithm.pls.AbstractPLS getBase() {
    return m_Base;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String baseTipText() {
    return "The base PLS algorithm to use.";
  }

  /**
   * Returns the all the available matrices.
   *
   * @return		the names of the matrices
   */
  @Override
  public String[] getMatrixNames() {
    return m_OPLS.getMatrixNames();
  }

  /**
   * Returns the matrix with the specified name.
   *
   * @param name	the name of the matrix
   * @return		the matrix, null if not available
   */
  @Override
  public Matrix getMatrix(String name) {
    return MatrixHelper.matrixAlgoToWeka(m_OPLS.getMatrix(name));
  }

  /**
   * Whether the algorithm supports return of loadings.
   *
   * @return		true if supported
   * @see		#getLoadings()
   */
  @Override
  public boolean hasLoadings() {
    return m_OPLS.hasLoadings();
  }

  /**
   * Returns the loadings, if available.
   *
   * @return		the loadings, null if not available
   */
  @Override
  public Matrix getLoadings() {
    return MatrixHelper.matrixAlgoToWeka(m_OPLS.getLoadings());
  }

  /**
   * Determines the output format based on the input format and returns this.
   *
   * @param input 	the input format to base the output format on
   * @return 		the output format
   * @throws Exception 	in case the determination goes wrong
   */
  @Override
  public Instances determineOutputFormat(Instances input) throws Exception {
    m_OutputFormat = new Instances(input, 0);
    return m_OutputFormat;
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
      m_OPLS = new com.github.waikatodatamining.matrix.algorithm.pls.OPLS();
      m_OPLS.setNumComponents(m_NumComponents);
      m_OPLS.setPreprocessingType(com.github.waikatodatamining.matrix.core.PreprocessingType.NONE);
      m_OPLS.setBasePLS((com.github.waikatodatamining.matrix.algorithm.pls.AbstractPLS) OptionUtils.shallowCopy(m_Base));
      error = m_OPLS.initialize(X, y);
      if (error != null)
	throw new Exception(error);
    }
    X_new = m_OPLS.transform(X);

    if (m_PredictionType == PredictionType.ALL)
      y_new = m_OPLS.predict(X);
    else
      y_new = y;

    return MatrixHelper.toInstances(getOutputFormat(), MatrixHelper.matrixAlgoToWeka(X_new), MatrixHelper.matrixAlgoToWeka(y_new));
  }
}
