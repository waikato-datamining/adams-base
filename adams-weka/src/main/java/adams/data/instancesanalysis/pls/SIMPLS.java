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
 * SIMPLS.java
 * Copyright (C) 2006-2018 University of Waikato, Hamilton, NZ
 */

package adams.data.instancesanalysis.pls;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.Utils;
import weka.core.Instances;
import weka.core.matrix.Matrix;
import weka.core.matrix.MatrixHelper;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Implementation of SIMPLS algorithm.<br>
 * <br>
 * Available matrices: W, B<br>
 * <br>
 * For more information see:<br>
 * <br>
 * Tormod Naes, Tomas Isaksson, Tom Fearn, Tony Davies (2002). A User Friendly Guide to Multivariate Calibration and Classification. NIR Publications.<br>
 * <br>
 * S. de Jong (1993). SIMPLS: an alternative approach to partial least squares regression. Chemometrics and Intelligent Laboratory Systems. 18:251-263.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;book{Naes2002,
 *    author = {Tormod Naes and Tomas Isaksson and Tom Fearn and Tony Davies},
 *    publisher = {NIR Publications},
 *    title = {A User Friendly Guide to Multivariate Calibration and Classification},
 *    year = {2002},
 *    ISBN = {0-9528666-2-5}
 * }
 *
 * &#64;article{Jong1993,
 *    author = {S. de Jong},
 *    journal = {Chemometrics and Intelligent Laboratory Systems},
 *    pages = {251-263},
 *    title = {SIMPLS: an alternative approach to partial least squares regression},
 *    volume = {18},
 *    year = {1993}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
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
 * <pre>-num-coefficients &lt;int&gt; (property: numCoefficients)
 * &nbsp;&nbsp;&nbsp;The number of coefficients of W matrix to keep (rest gets zeroed); use 0
 * &nbsp;&nbsp;&nbsp;to keep all.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SIMPLS
  extends AbstractSingleClassPLS {

  private static final long serialVersionUID = -2148100447010845646L;

  /** the number of coefficients in W to keep (0 keep all). */
  protected int m_NumCoefficients;

  /** the W matrix */
  protected Matrix m_W;

  /** the B matrix (used for prediction) */
  protected Matrix m_B;

  /**
   * Returns a string describing this class.
   *
   * @return 		a description of the class suitable for displaying in the
   *         		explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Implementation of SIMPLS algorithm.\n\n"
      + "Available matrices: " + Utils.flatten(getMatrixNames(), ", ") + "\n\n"
      + "For more information see:\n\n"
      + getTechnicalInformation().toString();
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
    TechnicalInformation additional;

    result = new TechnicalInformation(Type.BOOK);
    result.setValue(Field.AUTHOR, "Tormod Naes and Tomas Isaksson and Tom Fearn and Tony Davies");
    result.setValue(Field.YEAR, "2002");
    result.setValue(Field.TITLE, "A User Friendly Guide to Multivariate Calibration and Classification");
    result.setValue(Field.PUBLISHER, "NIR Publications");
    result.setValue(Field.ISBN, "0-9528666-2-5");

    additional = result.add(Type.ARTICLE);
    additional.setValue(Field.AUTHOR, "S. de Jong");
    additional.setValue(Field.YEAR, "1993");
    additional.setValue(Field.TITLE, "SIMPLS: an alternative approach to partial least squares regression");
    additional.setValue(Field.JOURNAL, "Chemometrics and Intelligent Laboratory Systems");
    additional.setValue(Field.VOLUME, "18");
    additional.setValue(Field.PAGES, "251-263");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-coefficients", "numCoefficients",
      0, 0, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  public void reset() {
    super.reset();

    m_B = null;
    m_W = null;
  }

  /**
   * Sets the number of coefficients of W matrix to keep (rest gets zeroed).
   *
   * @param value 	the number of coefficients, 0 to keep all
   */
  public void setNumCoefficients(int value) {
    m_NumCoefficients = value;
    reset();
  }

  /**
   * returns the number of coefficients of W matrix to keep (rest gets zeroed).
   *
   * @return 		the maximum number of attributes, 0 to keep all
   */
  public int getNumCoefficients() {
    return m_NumCoefficients;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String numCoefficientsTipText() {
    return "The number of coefficients of W matrix to keep (rest gets zeroed); use 0 to keep all.";
  }

  /**
   * Returns the all the available matrices.
   *
   * @return		the names of the matrices
   */
  @Override
  public String[] getMatrixNames() {
    return new String[]{
      "W",
      "B"
    };
  }

  /**
   * Returns the matrix with the specified name.
   *
   * @param name	the name of the matrix
   * @return		the matrix, null if not available
   */
  @Override
  public Matrix getMatrix(String name) {
    switch (name) {
      case "W":
	return m_W;
      case "B":
	return m_B;
      default:
	return null;
    }
  }

  /**
   * Whether the algorithm supports return of loadings.
   *
   * @return		true if supported
   * @see		#getLoadings()
   */
  public boolean hasLoadings() {
    return true;
  }

  /**
   * Returns the loadings, if available.
   *
   * @return		the loadings, null if not available
   */
  public Matrix getLoadings() {
    return getMatrix("W");
  }

  /**
   * Zeroes the coefficients of the W matrix beyond the specified number of
   * coefficients.
   *
   * @param in		the matrix to process in-place
   */
  protected void slim(Matrix in) {
    double[][] B = in.getArray();

    for (int i = 0; i < in.getColumnDimension(); i++) {
      Matrix l = in.getMatrix(0, in.getRowDimension() - 1, i, i);
      double[] ld = l.getRowPackedCopy();
      for (int t = 0; t < ld.length; t++) {
	ld[t] = Math.abs(ld[t]);
      }
      int[] srt = weka.core.Utils.sort(ld);
      //int index = srt.length - 1 - srt[Math.min(getNumCoefficients(),srt.length-1)]; //nonono
      int index = srt[Math.max(srt.length - 1 - getNumCoefficients(), 0)];

      double val = ld[index];
      for (int c = 0; c < in.getRowDimension(); c++) {
	if (Math.abs(B[c][i]) < val) {
	  B[c][i] = 0;
	}
      }
    }
  }

  /**
   * Transforms the data, initializes if necessary.
   *
   * @param data	the data to use
   */
  protected Instances doTransform(Instances data, Map<String,Object> params) throws Exception {
    Matrix A, A_trans;
    Matrix M;
    Matrix X, X_trans;
    Matrix X_new;
    Matrix Y, y;
    Matrix C, c;
    Matrix Q, q;
    Matrix W, w;
    Matrix P, p, p_trans;
    Matrix v, v_trans;
    Matrix T;
    Instances result;
    int h;

    if (!isInitialized()) {
      // init
      X = MatrixHelper.getX(data);
      X_trans = X.transpose();
      Y = MatrixHelper.getY(data);
      A = X_trans.times(Y);
      M = X_trans.times(X);
      C = Matrix.identity(data.numAttributes() - 1,
        data.numAttributes() - 1);
      W = new Matrix(data.numAttributes() - 1, getNumComponents());
      P = new Matrix(data.numAttributes() - 1, getNumComponents());
      Q = new Matrix(1, getNumComponents());

      for (h = 0; h < getNumComponents(); h++) {
        // 1. qh as dominant EigenVector of Ah'*Ah
        A_trans = A.transpose();
        q = MatrixHelper.getDominantEigenVector(A_trans.times(A));

        // 2. wh=Ah*qh, ch=wh'*Mh*wh, wh=wh/sqrt(ch), store wh in W as column
        w = A.times(q);
        c = w.transpose().times(M).times(w);
        w = w.times(1.0 / StrictMath.sqrt(c.get(0, 0)));
        MatrixHelper.setVector(w, W, h);

        // 3. ph=Mh*wh, store ph in P as column
        p = M.times(w);
        p_trans = p.transpose();
        MatrixHelper.setVector(p, P, h);

        // 4. qh=Ah'*wh, store qh in Q as column
        q = A_trans.times(w);
        MatrixHelper.setVector(q, Q, h);

        // 5. vh=Ch*ph, vh=vh/||vh||
        v = C.times(p);
        MatrixHelper.normalizeVector(v);
        v_trans = v.transpose();

        // 6. Ch+1=Ch-vh*vh', Mh+1=Mh-ph*ph'
        C = C.minus(v.times(v_trans));
        M = M.minus(p.times(p_trans));

        // 7. Ah+1=ChAh (actually Ch+1)
        A = C.times(A);
      }

      // finish
      if (getNumCoefficients() > 0)
        slim(W);
      m_W   = W;
      T     = X.times(m_W);
      X_new = T;
      m_B   = W.times(Q.transpose());

      switch (m_PredictionType) {
	case ALL:
          y = T.times(P.transpose()).times(m_B);
	  break;
	case NONE:
	case EXCEPT_CLASS:
          y = MatrixHelper.getY(data);
	  break;
	default:
	  throw new IllegalStateException("Unhandled prediction type: " + m_PredictionType);
      }

      result = MatrixHelper.toInstances(getOutputFormat(), X_new, y);
    }
    else {
      X     = MatrixHelper.getX(data);
      X_new = X.times(m_W);

      switch (m_PredictionType) {
        case ALL:
          y = X.times(m_B);
          break;
        case NONE:
        case EXCEPT_CLASS:
          y = MatrixHelper.getY(data);
          break;
        default:
          throw new IllegalStateException("Unhandled prediction type: " + m_PredictionType);
      }

      result = MatrixHelper.toInstances(getOutputFormat(), X_new, y);
    }

    return result;
  }
}
