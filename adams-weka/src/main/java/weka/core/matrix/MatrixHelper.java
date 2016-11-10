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
 * MatrixHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package weka.core.matrix;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * Some matrix operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MatrixHelper {

  /**
   * returns the data minus the class column as matrix
   *
   * @param instances the data to work on
   * @return the data without class attribute
   */
  public static Matrix getX(Instances instances) {
    double[][] x;
    double[] values;
    Matrix result;
    int i;
    int n;
    int j;
    int clsIndex;

    clsIndex = instances.classIndex();
    x = new double[instances.numInstances()][];

    for (i = 0; i < instances.numInstances(); i++) {
      values = instances.instance(i).toDoubleArray();
      x[i] = new double[values.length - 1];

      j = 0;
      for (n = 0; n < values.length; n++) {
        if (n != clsIndex) {
          x[i][j] = values[n];
          j++;
        }
      }
    }

    result = new Matrix(x);

    return result;
  }

  /**
   * returns the data minus the class column as matrix
   *
   * @param instance the instance to work on
   * @return the data without the class attribute
   */
  public static Matrix getX(Instance instance) {
    double[][] x;
    double[] values;
    Matrix result;

    x = new double[1][];
    values = instance.toDoubleArray();
    x[0] = new double[values.length - 1];
    System.arraycopy(values, 0, x[0], 0, values.length - 1);

    result = new Matrix(x);

    return result;
  }

  /**
   * returns the data class column as matrix
   *
   * @param instances the data to work on
   * @return the class attribute
   */
  public static Matrix getY(Instances instances) {
    double[][] y;
    Matrix result;
    int i;

    y = new double[instances.numInstances()][1];
    for (i = 0; i < instances.numInstances(); i++) {
      y[i][0] = instances.instance(i).classValue();
    }

    result = new Matrix(y);

    return result;
  }

  /**
   * returns the data class column as matrix
   *
   * @param instance the instance to work on
   * @return the class attribute
   */
  public static Matrix getY(Instance instance) {
    double[][] y;
    Matrix result;

    y = new double[1][1];
    y[0][0] = instance.classValue();

    result = new Matrix(y);

    return result;
  }

  /**
   * returns the X and Y matrix again as Instances object, based on the given
   * header (must have a class attribute set).
   *
   * @param header the format of the instance object
   * @param x the X matrix (data)
   * @param y the Y matrix (class)
   * @return the assembled data
   */
  public static Instances toInstances(Instances header, Matrix x, Matrix y) {
    double[] values;
    int i;
    int n;
    Instances result;
    int rows;
    int cols;
    int offset;
    int clsIdx;

    result = new Instances(header, 0);

    rows = x.getRowDimension();
    cols = x.getColumnDimension();
    clsIdx = header.classIndex();

    for (i = 0; i < rows; i++) {
      values = new double[cols + 1];
      offset = 0;

      for (n = 0; n < values.length; n++) {
        if (n == clsIdx) {
          offset--;
          values[n] = y.get(i, 0);
        } else {
          values[n] = x.get(i, n + offset);
        }
      }

      result.add(new DenseInstance(1.0, values));
    }

    return result;
  }

  /**
   * returns the given column as a vector (actually a n x 1 matrix)
   *
   * @param m the matrix to work on
   * @param columnIndex the column to return
   * @return the column as n x 1 matrix
   */
  public static Matrix columnAsVector(Matrix m, int columnIndex) {
    Matrix result;
    int i;

    result = new Matrix(m.getRowDimension(), 1);

    for (i = 0; i < m.getRowDimension(); i++) {
      result.set(i, 0, m.get(i, columnIndex));
    }

    return result;
  }

  /**
   * stores the data from the (column) vector in the matrix at the specified
   * index
   *
   * @param v the vector to store in the matrix
   * @param m the receiving matrix
   * @param columnIndex the column to store the values in
   */
  public static void setVector(Matrix v, Matrix m, int columnIndex) {
    m.setMatrix(0, m.getRowDimension() - 1, columnIndex, columnIndex, v);
  }

  /**
   * returns the (column) vector of the matrix at the specified index
   *
   * @param m the matrix to work on
   * @param columnIndex the column to get the values from
   * @return the column vector
   */
  public static Matrix getVector(Matrix m, int columnIndex) {
    return m.getMatrix(0, m.getRowDimension() - 1, columnIndex, columnIndex);
  }

  /**
   * determines the dominant eigenvector for the given matrix and returns it
   *
   * @param m the matrix to determine the dominant eigenvector for
   * @return the dominant eigenvector
   */
  public static Matrix getDominantEigenVector(Matrix m) {
    EigenvalueDecomposition eigendecomp;
    double[] eigenvalues;
    int index;
    Matrix result;

    eigendecomp = m.eig();
    eigenvalues = eigendecomp.getRealEigenvalues();
    index = Utils.maxIndex(eigenvalues);
    result = columnAsVector(eigendecomp.getV(), index);

    return result;
  }

  /**
   * normalizes the given vector (inplace)
   *
   * @param v the vector to normalize
   */
  public static void normalizeVector(Matrix v) {
    double sum;
    int i;

    // determine length
    sum = 0;
    for (i = 0; i < v.getRowDimension(); i++) {
      sum += v.get(i, 0) * v.get(i, 0);
    }
    sum = StrictMath.sqrt(sum);

    // normalize content
    for (i = 0; i < v.getRowDimension(); i++) {
      v.set(i, 0, v.get(i, 0) / sum);
    }
  }
}
