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
 * PLSFilterWithLoadings.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.filters.supervised.attribute;


  import java.util.ArrayList;

  import weka.core.Attribute;
  import weka.core.DenseInstance;
  import weka.core.Instances;
  import weka.core.matrix.Matrix;
  import weka.filters.Filter;
  import weka.filters.supervised.attribute.PLSFilter;
  import weka.filters.unsupervised.attribute.Center;
  import weka.filters.unsupervised.attribute.Standardize;

/***
 * Class contains changes to the Weka's PLSFilter in order to
 * have simpls work with multiple y attributes.
 *
 * * @author Cor Lieftink (c.lieftink at nki dot nl)
 */

public class PLSFilterExtended extends PLSFilter {

  private static final long serialVersionUID = 3908648580492805218L;

  protected double[] m_Means = {};
  protected double[] m_StdDevs = {};

  public Matrix getxWeights() {
    return (this.m_PLS1_W);
  }

  /***
   * Override superclass method in order to deal with multiple y
   *
   * @param instances
   *            the data to work on
   * @return the x attributes
   */
  public Matrix getX(Instances instances) {
    double[][] x;
    Matrix result;

    // Classindex is interpreted as the first of the Y variables, which is
    // equal to the number of x-variables.
    int numberXattributes = instances.classIndex();
    x = new double[instances.numInstances()][numberXattributes];
    for (int i = 0; i < instances.numInstances(); i++)
      for (int j = 0; j < numberXattributes; j++)
        x[i][j] = instances.instance(i).value(j);

    result = new Matrix(x);

    return result;
  }

  /***
   * Override superclass method in order to deal with multiple y
   *
   * @param instances
   *            the data to work on
   * @return the y attributes
   */
  public Matrix getY(Instances instances) {
    double[][] y;
    Matrix result;

    // classindex is used to indicate the first of the Y variables
    int numberYattributes = instances.numAttributes()
      - instances.classIndex();
    y = new double[instances.numInstances()][numberYattributes];
    for (int i = 0; i < instances.numInstances(); i++)
      for (int j = 0; j < numberYattributes; j++)
        y[i][j] = instances.instance(i).value(
          instances.classIndex() + j);

    result = new Matrix(y);

    return result;
  }

  public Matrix getbHat() {
    return (this.m_PLS1_b_hat);
  }

  public Matrix getRegVector() {
    return (this.m_PLS1_RegVector);
  }

  /** the mean of all the attributes */
  public double[] means() {
    return (this.m_Means);
  }

  /** the standard deviation of the class */
  public double[] stdDevs() {
    return (this.m_StdDevs);
  }

  /**
   * Override original, in order to put more than one class attribute to the
   * output format.
   *
   * Determines the output format based on the input format and returns this.
   * In case the output format cannot be returned immediately, i.e.,
   * immediateOutputFormat() returns false, then this method will be called
   * from batchFinished().
   *
   * @param inputFormat
   *            the input format to base the output format on
   * @return the output format
   * @throws Exception
   *             in case the determination goes wrong
   * @see #hasImmediateOutputFormat()
   * @see #batchFinished()
   */
  protected Instances determineOutputFormat(Instances inputFormat)
    throws Exception {

    // generate header: number of components + number of predicted y
    // attributes
    int numberPredictedY = (inputFormat.numAttributes() - inputFormat
      .classIndex());
    int numberAttributes = getNumComponents() + numberPredictedY;

    ArrayList<Attribute> atts = new ArrayList<Attribute>(numberAttributes);
    String prefix = getAlgorithm().getSelectedTag().getReadable();
    for (int i = 0; i < getNumComponents(); i++)
      atts.add(new Attribute(prefix + "_" + (i + 1)));

    for (int i = 0; i < numberPredictedY; i++)
      atts.add(new Attribute("Class" + (i + 1)));
    Instances result = new Instances(prefix, atts, 0);
    result.setClassIndex(getNumComponents());

    return result;
  }

  /**
   * Override superclass method, as this cannot deal with multiple y
   * attributes
   *
   * Returns the X and Y matrix again as Instances object, based on the given
   * header (must have a class attribute set).
   *
   * @param header
   *            the format of the instance object
   * @param x
   *            the X matrix (data)
   * @param y
   *            the Y matrix (class)
   * @return the assembled data
   */
  protected Instances toInstances(Instances header, Matrix x, Matrix y) {
    double[] values;
    int i;
    int n;
    Instances result;
    int rows;
    int colsX;
    int colsY;

    result = new Instances(header, 0);

    rows = x.getRowDimension();
    colsX = x.getColumnDimension();
    colsY = y.getColumnDimension();

    for (i = 0; i < rows; i++) {
      int k = -1;
      int l = -1;
      values = new double[colsX + colsY];
      for (n = 0; n < values.length; n++) {
        if (n < colsX) {
          k++;
          values[n] = x.get(i, k);
        } else {
          l++;
          values[n] = y.get(i, l);
        }
      }

      result.add(new DenseInstance(1.0, values));
    }

    return result;
  }

  /**
   * Override in order to have the destandardized predictions for multiple y
   * added.
   *
   * Processes the given data (may change the provided dataset) and returns
   * the modified version. This method is called in batchFinished().
   *
   * @param instances
   *            the data to process
   * @return the modified data
   * @throws Exception
   *             in case the processing goes wrong
   * @see #batchFinished()
   */
  protected Instances process(Instances instances) throws Exception {
    Instances result;
    Instances instancesInput;

    result = null;

    // save original class values if no prediction is performed
    if (!getPerformPrediction())
      instancesInput = instances;
    else
      instancesInput = null;

    if (!isFirstBatchDone()) {
      // init filters
      if (m_ReplaceMissing)
        m_Missing.setInputFormat(instances);

      int numberYattributes = instances.numAttributes()
        - instances.classIndex();
      this.m_Means = new double[numberYattributes];
      this.m_StdDevs = new double[numberYattributes];
      switch (m_Preprocessing) {
        case PREPROCESSING_CENTER:
          for (int i = 0; i < numberYattributes; i++) {
            this.m_Means[i] = instances.meanOrMode(instances
              .classIndex()
              + i);
            this.m_StdDevs[i] = 1;
          }
          m_Filter = new Center();
          ((Center) m_Filter).setIgnoreClass(true);
          break;
        case PREPROCESSING_STANDARDIZE:
          for (int i = 0; i < numberYattributes; i++) {
            this.m_Means[i] = instances.meanOrMode(instances
              .classIndex()
              + i);
            this.m_StdDevs[i] = StrictMath.sqrt(instances
              .variance(instances.classIndex() + i));
          }
          m_Filter = new Standardize();
          ((Standardize) m_Filter).setIgnoreClass(true);
          break;
        default:
          for (int i = 0; i < numberYattributes; i++) {
            m_Means[i] = 0;
            m_StdDevs[i] = 1;
          }
          m_Filter = null;
      }
      if (m_Filter != null)
        m_Filter.setInputFormat(instances);
    }

    // filter data
    if (m_ReplaceMissing)
      instances = Filter.useFilter(instances, m_Missing);
    if (m_Filter != null)
      instances = Filter.useFilter(instances, m_Filter);

    switch (m_Algorithm) {
      case ALGORITHM_SIMPLS:
        result = processSIMPLS(instances);
        break;
      case ALGORITHM_PLS1:
        result = processPLS1(instances);
        break;
      default:
        throw new IllegalStateException("Algorithm type '" + m_Algorithm
          + "' is not recognized!");
    }

    // add the mean to the class again if predictions are to be performed,
    // otherwise restore original class values
    for (int i = 0; i < result.numInstances(); i++) {
      int numberYattributes = instances.numAttributes()
        - instances.classIndex();
      for (int j = 0; j < numberYattributes; j++) {
        if (!getPerformPrediction()) {
          double value = instancesInput.instance(i).value(
            instances.classIndex() + j);
          result.instance(i).setValue(getNumComponents() + j, value);
        } else {
          double value = result.instance(i).value(
            getNumComponents() + j);
          result.instance(i).setValue(getNumComponents() + j,
            value * m_StdDevs[j] + m_Means[j]);
        }

      }
    }

    return result;
  }

  /**
   * Extended superclass method for increasing dimensions and/of changing
   * handling of the Matrices C,W,P,Q to deal with multiple Y variables.
   *
   * processes the instances using the SIMPLS algorithm
   *
   * @param instances
   *            the data to process
   * @return the modified data
   * @throws Exception
   *             in case the processing goes wrong
   */
  protected Instances processSIMPLS(Instances instances) throws Exception {
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

    if (!isFirstBatchDone()) {
      // init
      X = getX(instances);
      X_trans = X.transpose();
      Y = getY(instances);
      A = X_trans.times(Y);
      M = X_trans.times(X);
      C = Matrix.identity(X.getColumnDimension(), X.getColumnDimension());
      W = new Matrix(X.getColumnDimension(), getNumComponents());
      P = new Matrix(X.getColumnDimension(), getNumComponents());
      Q = new Matrix(Y.getColumnDimension(), getNumComponents());

      for (h = 0; h < getNumComponents(); h++) {
        // 1. qh as dominant EigenVector of Ah'*Ah
        A_trans = A.transpose();
        q = getDominantEigenVector(A_trans.times(A));

        // 2. wh=Ah*qh, ch=wh'*Mh*wh, wh=wh/sqrt(ch), store wh in W as
        // column
        w = A.times(q);
        c = w.transpose().times(M).times(w);
        w = w.times(1.0 / StrictMath.sqrt(c.get(0, 0)));
        setVector(w, W, h);

        // 3. ph=Mh*wh, store ph in P as column
        p = M.times(w);
        p_trans = p.transpose();
        setVector(p, P, h);

        // 4. qh=Ah'*wh, store qh in Q as column
        q = A_trans.times(w);
        setVector(q, Q, h);

        // 5. vh=Ch*ph, vh=vh/||vh||
        v = C.times(p);
        normalizeVector(v);
        v_trans = v.transpose();

        // 6. Ch+1=Ch-vh*vh', Mh+1=Mh-ph*ph'
        C = C.minus(v.times(v_trans));
        M = M.minus(p.times(p_trans));

        // 7. Ah+1=ChAh (actually Ch+1)
        A = C.times(A);
      }

      // finish
      m_SIMPLS_W = W;
      T = X.times(m_SIMPLS_W);
      X_new = T;
      m_SIMPLS_B = W.times(Q.transpose());

      if (getPerformPrediction())
        y = T.times(P.transpose()).times(m_SIMPLS_B);
      else
        y = getY(instances);

      result = toInstances(getOutputFormat(), X_new, y);
    } else {
      result = new Instances(getOutputFormat());

      X = getX(instances);
      X_new = X.times(m_SIMPLS_W);

      if (getPerformPrediction())
        y = X.times(m_SIMPLS_B);
      else
        y = getY(instances);

      result = toInstances(getOutputFormat(), X_new, y);
    }

    return result;
  }

}
