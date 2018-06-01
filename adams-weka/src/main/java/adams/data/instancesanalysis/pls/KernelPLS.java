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
 * KernelPLS.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.instancesanalysis.pls;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.option.OptionUtils;
import com.github.waikatodatamining.matrix.algorithm.PreprocessingType;
import com.github.waikatodatamining.matrix.transformation.kernel.AbstractKernel;
import com.github.waikatodatamining.matrix.transformation.kernel.RBFKernel;
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
public class KernelPLS
  extends AbstractMultiClassPLS {

  private static final long serialVersionUID = 3943381091993382352L;

  /** the kernel to use. */
  protected AbstractKernel m_Kernel;

  /** Inner NIPALS loop improvement tolerance */
  protected double m_Tol;

  /** Inner NIPALS loop maximum number of iterations */
  protected int m_MaxIter;

  /** the actual algorithm. */
  protected com.github.waikatodatamining.matrix.algorithm.KernelPLS m_KernelPLS;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Kernel Partial Least Squares Regression in Reproducing Kernel Hilbert Space\n\n"
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
    result.setValue(Field.AUTHOR, "Roman Rosipal and Leonard J. Trejo");
    result.setValue(Field.YEAR, "2001");
    result.setValue(Field.TITLE, "Kernel Partial Least Squares Regression in Reproducing Kernel Hilbert Space");
    result.setValue(Field.JOURNAL, "Journal of Machine Learning Research");
    result.setValue(Field.VOLUME, "2");
    result.setValue(Field.PAGES, "97-123");
    result.setValue(Field.URL, "http://www.jmlr.org/papers/volume2/rosipal01a/rosipal01a.pdf");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "kernel", "kernel",
      new RBFKernel());

    m_OptionManager.add(
      "tol", "tol",
      1e-6, 0.0, null);

    m_OptionManager.add(
      "max-iter", "maxIter",
      500, 1, null);
  }

  /**
   * Sets the kernel to use.
   *
   * @param value	the kernel
   */
  public void setKernel(AbstractKernel value) {
    m_Kernel = value;
    reset();
  }

  /**
   * Sets the kernel to use.
   *
   * @return		the kernel
   */
  public AbstractKernel getKernel() {
    return m_Kernel;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String kernelipText() {
    return "The kernel to use";
  }

  /**
   * Sets the inner NIPALS loop maximum number of iterations.
   *
   * @param value	the iterations
   */
  public void setTol(double value) {
    m_Tol = value;
    reset();
  }

  /**
   * Sets the inner NIPALS loop maximum number of iterations.
   *
   * @return		the iterations
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
  public String tolipText() {
    return "The inner NIPALS loop maximum number of iterations.";
  }

  /**
   * Sets the inner NIPALS loop improvement tolerance.
   *
   * @param value	the tolerance
   */
  public void setMaxIter(int value) {
    m_MaxIter = value;
    reset();
  }

  /**
   * Sets the inner NIPALS loop improvement tolerance.
   *
   * @return		the tolerance
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
  public String maxIteripText() {
    return "The inner NIPALS loop improvement tolerance.";
  }

  /**
   * Returns the all the available matrices.
   *
   * @return		the names of the matrices
   */
  @Override
  public String[] getMatrixNames() {
    return m_KernelPLS.getMatrixNames();
  }

  /**
   * Returns the matrix with the specified name.
   *
   * @param name	the name of the matrix
   * @return		the matrix, null if not available
   */
  @Override
  public Matrix getMatrix(String name) {
    return MatrixHelper.jamaToWeka(m_KernelPLS.getMatrix(name));
  }

  /**
   * Whether the algorithm supports return of loadings.
   *
   * @return		true if supported
   * @see		#getLoadings()
   */
  @Override
  public boolean hasLoadings() {
    return m_KernelPLS.hasLoadings();
  }

  /**
   * Returns the loadings, if available.
   *
   * @return		the loadings, null if not available
   */
  @Override
  public Matrix getLoadings() {
    return MatrixHelper.jamaToWeka(m_KernelPLS.getLoadings());
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
    Jama.Matrix		X;
    Jama.Matrix		Y;
    Jama.Matrix		X_new;
    int[]		cols;
    String 		error;

    cols = m_ClassAttributeIndices.toArray();
    X    = MatrixHelper.wekaToJama(MatrixHelper.getX(data));
    Y    = MatrixHelper.wekaToJama(MatrixHelper.getY(data, cols));
    if (!isInitialized()) {
      m_KernelPLS = new com.github.waikatodatamining.matrix.algorithm.KernelPLS();
      m_KernelPLS.setKernel((AbstractKernel) OptionUtils.shallowCopy(m_Kernel));
      m_KernelPLS.setNumComponents(m_NumComponents);
      m_KernelPLS.setPreprocessingType(PreprocessingType.NONE);
      m_KernelPLS.setTol(m_Tol);
      m_KernelPLS.setMaxIter(m_MaxIter);
      error = m_KernelPLS.initialize(X, Y);
      if (error != null)
	throw new Exception(error);
    }
    X_new = m_KernelPLS.transform(X);

    return MatrixHelper.toInstances(getOutputFormat(), MatrixHelper.jamaToWeka(X_new), MatrixHelper.jamaToWeka(Y));
  }
}
