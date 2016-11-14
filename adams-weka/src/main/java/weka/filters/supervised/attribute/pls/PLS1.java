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
 * PLS1.java
 * Copyright (C) 2006-2016 University of Waikato, Hamilton, NZ
 */

package weka.filters.supervised.attribute.pls;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.matrix.Matrix;
import weka.core.matrix.MatrixHelper;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Implementation of PLS1 algorithm.<br>
 * <br>
 * For more information see:<br>
 * <br>
 * Tormod Naes, Tomas Isaksson, Tom Fearn, Tony Davies (2002). A User Friendly Guide to Multivariate Calibration and Classification. NIR Publications.<br>
 * <br>
 * StatSoft, Inc.. Partial Least Squares (PLS).<br>
 * <br>
 * Bent Jorgensen, Yuri Goegebeur. Module 7: Partial least squares regression I.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;book{Naes2002,
 *    author = {Tormod Naes and Tomas Isaksson and Tom Fearn and Tony Davies},
 *    publisher = {NIR Publications},
 *    title = {A User Friendly Guide to Multivariate Calibration and Classification},
 *    year = {2002},
 *    ISBN = {0-9528666-2-5}
 * }
 * 
 * &#64;misc{missing_id,
 *    author = {StatSoft, Inc.},
 *    booktitle = {Electronic Textbook StatSoft},
 *    title = {Partial Least Squares (PLS)},
 *    HTTP = {http://www.statsoft.com/textbook/stpls.html}
 * }
 * 
 * &#64;misc{missing_id,
 *    author = {Bent Jorgensen and Yuri Goegebeur},
 *    booktitle = {ST02: Multivariate Data Analysis and Chemometrics},
 *    title = {Module 7: Partial least squares regression I},
 *    HTTP = {http://statmaster.sdu.dk/courses/ST02/module07/}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 * 
 * <pre> -debug &lt;value&gt;
 *  If enabled, additional info may be output to the console.
 *  (default: false)</pre>
 * 
 * <pre> -preprocessing &lt;value&gt;
 *  The type of preprocessing to perform.
 *  (default: CENTER)</pre>
 * 
 * <pre> -C &lt;value&gt;
 *  The number of components to compute.
 *  (default: 20)</pre>
 * 
 * <pre> -prediction &lt;value&gt;
 *  The type of prediction to perform.
 *  (default: NONE)</pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PLS1
  extends AbstractSingleClassPLS {

  private static final long serialVersionUID = -2148100447010845646L;

  /** the regression vector "r-hat" */
  protected Matrix m_r_hat;

  /** the P matrix */
  protected Matrix m_P;

  /** the W matrix */
  protected Matrix m_W;

  /** the b-hat vector */
  protected Matrix m_b_hat;

  /**
   * Returns a string describing this class.
   *
   * @return 		a description of the class suitable for displaying in the
   *         		explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Implementation of PLS1 algorithm.\n\n"
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

    additional = result.add(Type.MISC);
    additional.setValue(Field.AUTHOR, "StatSoft, Inc.");
    additional.setValue(Field.TITLE, "Partial Least Squares (PLS)");
    additional.setValue(Field.BOOKTITLE, "Electronic Textbook StatSoft");
    additional.setValue(Field.HTTP, "http://www.statsoft.com/textbook/stpls.html");

    additional = result.add(Type.MISC);
    additional.setValue(Field.AUTHOR, "Bent Jorgensen and Yuri Goegebeur");
    additional.setValue(Field.TITLE, "Module 7: Partial least squares regression I");
    additional.setValue(Field.BOOKTITLE, "ST02: Multivariate Data Analysis and Chemometrics");
    additional.setValue(Field.HTTP, "http://statmaster.sdu.dk/courses/ST02/module07/");

    return result;
  }

  /**
   * Resets the scheme.
   */
  @Override
  public void reset() {
    super.reset();

    m_r_hat = null;
    m_P     = null;
    m_W     = null;
    m_b_hat = null;
  }

  /**
   * Returns the all the available matrices.
   *
   * @return		the names of the matrices
   */
  @Override
  public String[] getMatrixNames() {
    return new String[]{
      "r_hat",
      "P",
      "W",
      "b_hat"
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
      case "RegVector":
	return m_r_hat;
      case "P":
	return m_P;
      case "W":
	return m_W;
      case "b_hat":
	return m_b_hat;
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
    return getMatrix("P");
  }

  /**
   * Transforms the data, initializes if necessary.
   *
   * @param data	the data to use
   */
  protected Instances doTransform(Instances data, Map<String,Object> params) throws Exception {
    Matrix X, X_trans, x;
    Matrix y;
    Matrix W, w;
    Matrix T, t, t_trans;
    Matrix P, p, p_trans;
    double b;
    Matrix b_hat;
    int i;
    int j;
    Matrix X_new;
    Matrix tmp;
    Instances result;
    Instances tmpInst;

    // initialization
    if (!isInitialized()) {
      // split up data
      X = MatrixHelper.getX(data);
      y = MatrixHelper.getY(data);
      X_trans = X.transpose();

      // init
      W = new Matrix(data.numAttributes() - 1, getNumComponents());
      P = new Matrix(data.numAttributes() - 1, getNumComponents());
      T = new Matrix(data.numInstances(), getNumComponents());
      b_hat = new Matrix(getNumComponents(), 1);

      for (j = 0; j < getNumComponents(); j++) {
	// 1. step: wj
	w = X_trans.times(y);
	MatrixHelper.normalizeVector(w);
	MatrixHelper.setVector(w, W, j);

	// 2. step: tj
	t = X.times(w);
	t_trans = t.transpose();
	MatrixHelper.setVector(t, T, j);

	// 3. step: ^bj
	b = t_trans.times(y).get(0, 0) / t_trans.times(t).get(0, 0);
	b_hat.set(j, 0, b);

	// 4. step: pj
	p = X_trans.times(t).times(1 / t_trans.times(t).get(0, 0));
	p_trans = p.transpose();
	MatrixHelper.setVector(p, P, j);

	// 5. step: Xj+1
	X = X.minus(t.times(p_trans));
	y = y.minus(t.times(b));
      }

      // W*(P^T*W)^-1
      tmp = W.times(((P.transpose()).times(W)).inverse());

      // X_new = X*W*(P^T*W)^-1
      X_new = MatrixHelper.getX(data).times(tmp);

      // factor = W*(P^T*W)^-1 * b_hat
      m_r_hat = tmp.times(b_hat);

      // save matrices
      m_P = P;
      m_W = W;
      m_b_hat = b_hat;

      switch (m_PredictionType) {
	case ALL:
	  result = MatrixHelper.toInstances(getOutputFormat(), X_new, y);
	  break;
	case NONE:
	case EXCEPT_CLASS:
	  result = MatrixHelper.toInstances(getOutputFormat(), X_new, MatrixHelper.getY(data));
	  break;
	default:
	  throw new IllegalStateException("Unhandled prediction type: " + m_PredictionType);
      }
    }
    // prediction
    else {
      result = new Instances(getOutputFormat());

      for (i = 0; i < data.numInstances(); i++) {
	// work on each instance
	tmpInst = new Instances(data, 0);
	tmpInst.add((Instance) data.instance(i).copy());
	x = MatrixHelper.getX(tmpInst);
	X = new Matrix(1, getNumComponents());
	T = new Matrix(1, getNumComponents());

	for (j = 0; j < getNumComponents(); j++) {
	  MatrixHelper.setVector(x, X, j);
	  // 1. step: tj = xj * wj
	  t = x.times(MatrixHelper.getVector(m_W, j));
	  MatrixHelper.setVector(t, T, j);
	  // 2. step: xj+1 = xj - tj*pj^T (tj is 1x1 matrix!)
	  x = x.minus(MatrixHelper.getVector(m_P, j).transpose().times(t.get(0, 0)));
	}

	switch (m_PredictionType) {
	  case ALL:
	    tmpInst = MatrixHelper.toInstances(getOutputFormat(), T, T.times(m_b_hat));
	    break;
	  case NONE:
	  case EXCEPT_CLASS:
	    tmpInst = MatrixHelper.toInstances(getOutputFormat(), T, MatrixHelper.getY(tmpInst));
	    break;
	  default:
	    throw new IllegalStateException("Unhandled prediction type: " + m_PredictionType);
	}

	result.add(tmpInst.instance(0));
      }
    }

    return result;
  }
}
