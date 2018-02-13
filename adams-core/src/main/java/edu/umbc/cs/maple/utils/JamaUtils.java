package edu.umbc.cs.maple.utils;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import optimization.Fmin;
import optimization.Fmin_methods;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/** Various utility functions for the Jama matrix toolkit.
 * <p>
 * Copyright (c) 2008 Eric Eaton
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * @author Eric Eaton (EricEaton@umbc.edu) <br>
 *         University of Maryland Baltimore County
 *
 * @version 0.1
 *
 */
public class JamaUtils {

  /** Gets the specified column of a matrix.
   * @param m the matrix.
   * @param col the column to get.
   * @return the specified column of m.
   */
  public static Matrix getcol(Matrix m, int col) {
    return m.getMatrix(0,m.getRowDimension()-1,col,col);
  }


  /** Gets the specified columns of a matrix.
   * @param m the matrix
   * @param columns the columns to get
   * @return the matrix of the specified columns of m.
   */
  public static Matrix getcolumns(Matrix m, int[] columns) {
    Matrix colMatrix = new Matrix(m.getRowDimension(),columns.length);
    for (int i=0; i<columns.length; i++) {
      setcol(colMatrix, i, getcol(m, columns[i]));
    }
    return colMatrix;
  }


  /** Gets the specified row of a matrix.
   * @param m the matrix.
   * @param row the row to get.
   * @return the specified row of m.
   */
  public static Matrix getrow(Matrix m, int row) {
    return m.getMatrix(row,row,0,m.getColumnDimension()-1);
  }


  /** Gets the specified rows of a matrix.
   * @param m the matrix
   * @param rows the rows to get
   * @return the matrix of the specified rows of m.
   */
  public static Matrix getrows(Matrix m, int[] rows) {
    Matrix rowMatrix = new Matrix(rows.length, m.getColumnDimension());
    for (int i=0; i<rows.length; i++) {
      setrow(rowMatrix, i, getrow(m, rows[i]));
    }
    return rowMatrix;
  }

  /** Sets the specified row of a matrix.  Modifies the passed matrix.
   * @param m the matrix.
   * @param row the row to modify.
   * @param values the new values of the row.
   */
  public static void setrow(Matrix m, int row, Matrix values) {
    if (!isRowVector(values))
      throw new IllegalArgumentException("values must be a row vector.");
    m.setMatrix(row,row,0,m.getColumnDimension()-1,values);
  }


  /** Sets the specified column of a matrix.  Modifies the passed matrix.
   * @param m the matrix.
   * @param col the column to modify.
   * @param values the new values of the column.
   */
  public static void setcol(Matrix m, int col, Matrix values) {
    if (!isColumnVector(values))
      throw new IllegalArgumentException("values must be a column vector.");
    m.setMatrix(0,m.getRowDimension()-1,col,col,values);
  }


  /** Sets the specified column of a matrix.  Modifies the passed matrix.
   * @param m the matrix.
   * @param col the column to modify.
   * @param values the new values of the column.
   */
  public static void setcol(Matrix m, int col, double[] values) {
    if (values.length != m.getRowDimension())
      throw new IllegalArgumentException("values must have the same number of rows as the matrix.");
    for (int i=0; i<values.length; i++) {
      m.set(i, col, values[i]);
    }
  }


  /** Appends additional rows to the first matrix.
   * @param m the first matrix.
   * @param n the matrix to append containing additional rows.
   * @return a matrix with all the rows of m then all the rows of n.
   */
  public static Matrix rowAppend(Matrix m, Matrix n) {
    int mNumRows = m.getRowDimension();
    int mNumCols = m.getColumnDimension();
    int nNumRows = n.getRowDimension();
    int nNumCols = n.getColumnDimension();

    if (mNumCols != nNumCols)
      throw new IllegalArgumentException("Number of columns must be identical to row-append.");

    Matrix x = new Matrix(mNumRows+nNumRows,mNumCols);
    x.setMatrix(0,mNumRows-1,0,mNumCols-1,m);
    x.setMatrix(mNumRows,mNumRows+nNumRows-1,0,mNumCols-1,n);

    return x;
  }


  /** Appends additional columns to the first matrix.
   * @param m the first matrix.
   * @param n the matrix to append containing additional columns.
   * @return a matrix with all the columns of m then all the columns of n.
   */
  public static Matrix columnAppend(Matrix m, Matrix n) {
    int mNumRows = m.getRowDimension();
    int mNumCols = m.getColumnDimension();
    int nNumRows = n.getRowDimension();
    int nNumCols = n.getColumnDimension();

    if (mNumRows != nNumRows)
      throw new IllegalArgumentException("Number of rows must be identical to column-append.");

    Matrix x = new Matrix(mNumRows,mNumCols+nNumCols);
    x.setMatrix(0,mNumRows-1,0,mNumCols-1,m);
    x.setMatrix(0,mNumRows-1,mNumCols,mNumCols+nNumCols-1,n);

    return x;
  }


  /** Deletes a row from a matrix.  Does not change the passed matrix.
   * @param m the matrix.
   * @param row the row to delete.
   * @return m with the specified row deleted.
   */
  public static Matrix deleteRow(Matrix m, int row) {
    int numRows = m.getRowDimension();
    int numCols = m.getColumnDimension();
    Matrix m2 = new Matrix(numRows-1,numCols);
    for (int mi=0,m2i=0; mi < numRows; mi++) {
      if (mi == row)
	continue;  // skips incrementing m2i
      for (int j=0; j<numCols; j++) {
	m2.set(m2i,j,m.get(mi,j));
      }
      m2i++;
    }
    return m2;
  }


  /** Deletes a column from a matrix.  Does not change the passed matrix.
   * @param m the matrix.
   * @param col the column to delete.
   * @return m with the specified column deleted.
   */
  public static Matrix deleteCol(Matrix m, int col) {
    int numRows = m.getRowDimension();
    int numCols = m.getColumnDimension();
    Matrix m2 = new Matrix(numRows,numCols-1);
    for (int mj=0,m2j=0; mj < numCols; mj++) {
      if (mj == col)
	continue;  // skips incrementing m2j
      for (int i=0; i<numRows; i++) {
	m2.set(i,m2j,m.get(i,mj));
      }
      m2j++;
    }
    return m2;
  }


  /** Gets the sum of the specified row of the matrix.
   * @param m the matrix.
   * @param row the row.
   * @return the sum of m[row,*]
   */
  public static double rowsum(Matrix m, int row) {
    // error check the column index
    if (row < 0 || row >= m.getRowDimension()) {
      throw new IllegalArgumentException("row exceeds the row indices [0,"+(m.getRowDimension()-1)+"] for m.");
    }

    double rowsum = 0;

    // loop through the rows for this column and compute the sum
    int numCols = m.getColumnDimension();
    for (int j=0; j<numCols; j++) {
      rowsum += m.get(row,j);
    }

    return rowsum;
  }


  /** Gets the sum of the specified column of the matrix.
   * @param m the matrix.
   * @param col the column.
   * @return the sum of m[*,col]
   */
  public static double colsum(Matrix m, int col) {
    // error check the column index
    if (col < 0 || col >= m.getColumnDimension()) {
      throw new IllegalArgumentException("col exceeds the column indices [0,"+(m.getColumnDimension()-1)+"] for m.");
    }

    double colsum = 0;

    // loop through the rows for this column and compute the sum
    int numRows = m.getRowDimension();
    for (int i=0; i<numRows; i++) {
      colsum += m.get(i,col);
    }

    return colsum;
  }


  /** Computes the sum of each row of a matrix.
   * @param m the matrix.
   * @return a column vector of the sum of each row of m.
   */
  public static Matrix rowsum(Matrix m) {
    int numRows = m.getRowDimension();
    int numCols = m.getColumnDimension();
    Matrix sum = new Matrix(numRows,1);
    // loop through the rows and compute the sum
    for (int i=0; i<numRows; i++) {
      for (int j=0; j<numCols; j++) {
	sum.set(i,0,sum.get(i,0)+m.get(i,j));
      }
    }
    return sum;
  }


  /** Computes the sum of each column of a matrix.
   * @param m the matrix.
   * @return a row vector of the sum of each column of m.
   */
  public static Matrix colsum(Matrix m) {
    int numRows = m.getRowDimension();
    int numCols = m.getColumnDimension();
    Matrix sum = new Matrix(1,numCols);
    // loop through the rows and compute the sum
    for (int i=0; i<numRows; i++) {
      for (int j=0; j<numCols; j++) {
	sum.set(0,j,sum.get(0,j)+m.get(i,j));
      }
    }
    return sum;
  }

  /** Computes the sum the elements of a matrix.
   * @param m the matrix.
   * @return the sum of the elements of the matrix
   */
  public static double sum(Matrix m) {
    int numRows = m.getRowDimension();
    int numCols = m.getColumnDimension();
    double sum = 0;
    // loop through the rows and compute the sum
    for (int i=0; i<numRows; i++) {
      for (int j=0; j<numCols; j++) {
	sum += m.get(i,j);
      }
    }
    return sum;
  }


  /** Determines if a given matrix is a row vector, that is, it has only one row.
   * @param m the matrix.
   * @return whether the given matrix is a row vector (whether it has only one row).
   */
  public static boolean isRowVector(Matrix m) {
    return m.getRowDimension()==1;
  }


  /** Determines if a given matrix is a column vector, that is, it has only one column.
   * @param m the matrix.
   * @return whether the given matrix is a column vector (whether it has only one column).
   */
  public static boolean isColumnVector(Matrix m) {
    return m.getColumnDimension()==1;
  }


  /** Transforms the given matrix into a column vector, that is, a matrix with one column.
   * The matrix must be a vector (row or column) to begin with.
   * @param m
   * @return <code>m.transpose()</code> if m is a row vector,
   *         <code>m</code> if m is a column vector.
   * @throws IllegalArgumentException if m is not a row vector or a column vector.
   */
  public static Matrix makeColumnVector(Matrix m) {
    if (isColumnVector(m))
      return m;
    else if (isRowVector(m))
      return m.transpose();
    else
      throw new IllegalArgumentException("m is not a vector.");
  }


  /** Transforms the given matrix into a row vector, that is, a matrix with one row.
   * The matrix must be a vector (row or column) to begin with.
   * @param m
   * @return <code>m.transpose()</code> if m is a column vector,
   *         <code>m</code> if m is a row vector.
   * @throws IllegalArgumentException if m is not a row vector or a column vector.
   */
  public static Matrix makeRowVector(Matrix m) {
    if (isRowVector(m))
      return m;
    else if (isColumnVector(m))
      return m.transpose();
    else
      throw new IllegalArgumentException("m is not a vector.");
  }


  /** Computes the dot product of two vectors.  Both must be either row or column vectors.
   * @param m1
   * @param m2
   * @return the dot product of the two vectors.
   */
  public static double dotproduct(Matrix m1, Matrix m2) {

    Matrix m1colVector = makeColumnVector(m1);
    Matrix m2colVector = makeColumnVector(m2);

    int n = m1colVector.getRowDimension();
    if (n != m2colVector.getRowDimension()) {
      throw new IllegalArgumentException("m1 and m2 must have the same number of elements.");
    }

    double scalarProduct = 0;
    for (int row=0; row<n; row++) {
      scalarProduct += m1colVector.get(row,0) * m2colVector.get(row,0);
    }

    return scalarProduct;

  }

  /** Determines whether a matrix is symmetric.
   * @param m the matrix.
   * @return <code>true</code> if a is symmetric, <code>false</code> otherwise
   */
  public static boolean isSymmetric(Matrix m) {
    int numRows = m.getRowDimension();
    int numCols = m.getColumnDimension();

    for (int i=0; i<numRows; i++) {
      for (int j=i+1; j<numCols; j++) {
	if (m.get(i,j) != m.get(j,i))
	  return false;
      }
    }
    return true;
  }


  /** Specifies a simple mathematical function. */
  public enum Function {MAX, MIN, MEAN;

    /** Apply the function to the two values.
     */
    public double applyFunction(int v1, int v2) {
      switch (this) {
	case MAX: 	return Math.max(v1,v2);
	case MIN: 	return Math.min(v1,v2);
	case MEAN:  return (v1+v2)/2.0;
      }
      throw new IllegalStateException("Unknown Function.");
    }

    /** Apply the function to the two values.
     */
    public double applyFunction(double v1, double v2) {
      switch (this) {
	case MAX: 	return Math.max(v1,v2);
	case MIN: 	return Math.min(v1,v2);
	case MEAN:  return (v1+v2)/2.0;
      }
      throw new IllegalStateException("Unknown Function.");
    }
  }


  /** Makes a matrix symmetric by applying a function to symmetric elements.
   * @param m the matrix to make symmetric
   * @param f the function to apply
   */
  public static void makeMatrixSymmetric(Matrix m, Function f) {
    int numRows = m.getRowDimension();
    int numCols = m.getColumnDimension();
    for (int i = 0; i < numRows; i++) {
      for (int j = i; j < numCols; j++) {
	double value1 = m.get(i,j);
	double value2 = m.get(j,i);
	double similarity = f.applyFunction(value1,value2);
	m.set(i, j, similarity);
	m.set(j, i, similarity); // similarity is symmetric
      }
    }

  }


  /** Normalizes a matrix to make the elements sum to 1.
   * @param m the matrix
   * @return the normalized form of m with all elements summing to 1.
   */
  public static Matrix normalize(Matrix m) {
    int numRows = m.getRowDimension();
    int numCols = m.getColumnDimension();

    // compute the sum of the matrix
    double sum = 0;
    for (int i=0; i<numRows; i++) {
      for (int j=0; j<numCols; j++) {
	sum += m.get(i,j);
      }
    }

    // normalize the matrix
    Matrix normalizedM = new Matrix(numRows, numCols);
    for (int i=0; i<numRows; i++) {
      for (int j=0; j<numCols; j++) {
	normalizedM.set(i, j, m.get(i,j)/sum);
      }
    }

    return normalizedM;
  }

  /** Gets the maximum value in a matrix.
   * @param m the matrix
   * @return the maximum value in m.
   */
  public static double getMax(Matrix m) {
    int numRows = m.getRowDimension();
    int numCols = m.getColumnDimension();

    // compute the max of the matrix
    double maxValue = Double.MIN_VALUE;
    for (int i=0; i<numRows; i++) {
      for (int j=0; j<numCols; j++) {
	maxValue = Math.max(maxValue,m.get(i,j));
      }
    }
    return maxValue;
  }

  /** Gets the minimum value in a matrix.
   * @param m the matrix
   * @return the minimum value in m.
   */
  public static double getMin(Matrix m) {
    int numRows = m.getRowDimension();
    int numCols = m.getColumnDimension();

    // compute the min of the matrix
    double minValue = Double.MAX_VALUE;
    for (int i=0; i<numRows; i++) {
      for (int j=0; j<numCols; j++) {
	minValue = Math.min(minValue,m.get(i,j));
      }
    }
    return minValue;
  }


  /** Make a matrix of ones.
   * @param numRows the number of rows.
   * @param numCols the number of columns.
   * @return the numRows x numCols matrix of ones.
   */
  public static Matrix ones(int numRows, int numCols) {
    return new Matrix(numRows, numCols, 1);
  }



  /** Performs least squares regression using Tikhonov regularization.
   * Solves the problem Ax = b for x using regularization:
   *   min || Ax - b ||^2 - lambda^2 || x ||^2 ,
   * which can be solved by
   *   x = inv(A' * A + lambda^2 * I) * A' * b;
   *
   * Uses the identity matrix as the regop, and estimates lambda using
   * generalized cross-validation.
   *
   * @param A  the data matrix (n x m).
   * @param b the target function values (n x 1).
   */
  public static Matrix regLeastSquares(Matrix A, Matrix b) {

    //int m = A.getColumnDimension();
    int n = b.getRowDimension();

    // error check A and b
    if (A.getRowDimension() != n) {
      throw new IllegalArgumentException("A and b are incompatible sizes.");
    }

    // compute the optimal lambda using generalized cross-validation
    double lambda = gcv(A, b);

    return regLeastSquares(A,b,lambda);
  }

  /** Performs least squares regression using Tikhonov regularization.
   * Solves the problem Ax = b for x using regularization:
   *   min || Ax - b ||^2 - lambda^2 || x ||^2 ,
   * which can be solved by
   *   x = inv(A' * A + lambda^2 * I) * A' * b;
   *
   * Uses the identity matrix as the regularization operator.
   *
   * @param A  the data matrix (n x m).
   * @param b the target function values (n x 1).
   * @param lambda the lambda values.  If less than zero, it is estimated
   *           using generalized cross-validation.
   */
  public static Matrix regLeastSquares(Matrix A, Matrix b, double lambda) {
    int m = A.getColumnDimension();
    Matrix regop = Matrix.identity(m,m).times(Math.pow(lambda,2));
    return regLeastSquares(A,b,regop);
  }

  /** Performs least squares regression using Tikhonov regularization.
   * Solves the problem Ax = b for x using regularization:
   *   min || Ax - b ||^2 - || \sqrt(regop) x ||^2 ,
   * which can be solved by
   *   x = inv(A' * A + regop) * A' * b;
   *
   * @param A  the data matrix (n x m).
   * @param b the target function values (n x 1).
   * @param regop the regularization operator (m x m). The default is to use the identity matrix
   *              as the regularization operator, so you probably don't want to use this
   *              verion of regLeastSquares() without a really good reason.  Use
   *              regLeastSquares(Matrix A, Matrix b, double lambda) instead.
   */
  public static Matrix regLeastSquares(Matrix A, Matrix b, Matrix regop) {

    int m = A.getColumnDimension();
    int n = b.getRowDimension();

    // error check A and b
    if (A.getRowDimension() != n) {
      throw new IllegalArgumentException("A and b are incompatible sizes.");
    }

    // error check A and regop
    if (regop.getRowDimension() != m || regop.getColumnDimension() != m) {
      throw new IllegalArgumentException("A and regop are incompatible sizes.");
    }

    // solve the equation
    // x = inv(A' * A + regop) * A' * b;
    Matrix x = (A.transpose().times(A).plus(regop)).inverse().times(A.transpose()).times(b);
    return x;
  }

  /** Selects the regularization parameter by generalized cross-validation.
   *
   *    Given a matrix A and a data vector b, it
   *    returns a regularization parameter rpar chosen by generalized
   *    cross-validation using ridge regression.
   *
   *                 RSS
   *            G =  ---
   *                 T^2
   *
   *    where T = n - sum(f_i) is an effective number of degrees of
   *    freedom and f_i are the filter factors of the regularization
   *    method.
   *
   *    The returned regularization parameter rpar is the ridge parameter of
   *    ridge regression.
   *
   * References:
   *     Hansen, P. C., 1998: Rank-Deficient and Discrete Ill-Posed
   *        Problems. SIAM Monogr. on Mathematical Modeling and
   *        Computation, SIAM.
   *
   *     Wahba, G., 1990: Spline Models for Observational Data. CBMS-NSF
   *        Regional Conference Series in Applied Mathematics, Vol. 59,
   *        SIAM.
   *
   *     Adapted from various routines in Per-Christian Hansen's
   *     regularization toolbox for matlab, then converted to java.
   */
  protected static double gcv(Matrix A, Matrix b) {

    int AnumRows = A.getRowDimension();
    int AnumCols = A.getColumnDimension();

    // SVD of A
    Matrix U = null;
    double[] s = null;
    if (AnumRows >= AnumCols) {  // AnumRows >= AnumCols so Jama SVD will work
      SingularValueDecomposition Asvd = A.svd();  // Jama SVD computes the economy SVD
      U = Asvd.getU();
      s = Asvd.getSingularValues();
    }
    // AnumRows < AnumCols, so Jama SVD won't work without adjustment
    // Using [U,S,V] = svd(A)  <===>  [V,S,U] = svd(A')
    else {
      SingularValueDecomposition Asvd = A.transpose().svd();
      U = Asvd.getV();
      s = Asvd.getSingularValues();
    }

    int n = U.getRowDimension();
    int q = U.getColumnDimension();

    U = new Matrix(U.getArray());

    // Coefficients in expansion of solution in terms of right singular vectors
    // fc = U(:, 1:q)'*g;
    Matrix fc = U.transpose().times(b);
    // s2 = s.^2;
    double[] s2 = new double[q];
    for (int i=0; i<q; i++) {
      s2[i] = Math.pow(s[i],2);
    }

    // Least squares residual
    double rss0 = 0;
    if (n > q) {
      // rss0 = sum((g - U(:, 1:q)*fc).^2);
      double[] tempValues = b.minus(U.times(fc)).getColumnPackedCopy();
      for (double d : tempValues) {
	rss0 += Math.pow(d,2);
      }
    }

    // Accuracy of regularization parameter
    // h_tol = ((q^2 + q + 1)*eps)^(1/2);
    double eps = Math.pow(2,-52);
    double h_tol = Math.sqrt((Math.pow(q,2) + q + 1)*eps);

    // Heuristic upper bound on regularization parameter
    // h_max = max(s);
    double h_max = MathUtils.maxValue(s);

    // Heuristic lower bound
    // h_min = min(s) * h_tol;
    double h_min = MathUtils.minValue(s) * h_tol;

    // Find minimizer of GCV function
    // minopt = optimset('TolX', h_tol, 'Display', 'off');
    // [rpar, G] = fminbnd('gcvfctn', h_min, h_max, minopt, s2, fc, rss0, n-q);
    GCVFn gcvfn = new GCVFn();
    gcvfn.setS2(s2);
    gcvfn.setFC(fc);
    gcvfn.setRSS0(rss0);
    gcvfn.setDOF0(n-q);
		
		/*
		System.out.println("Doing Fmin with:");
		System.out.print("  s2 = [ "); for (double d:s2) System.out.print(d + " "); System.out.println("]");
		System.out.print("  fc = "); fc.print(6,4);
		System.out.println("  rss0 = " + rss0);
		System.out.println("  dof0 = " + (n-q));
		*/

    double rpar = Fmin.fmin(h_min, h_max, gcvfn, h_tol);

    return rpar;

  }

  private static class GCVFn implements Fmin_methods {

    double[] s2;
    Matrix fc;
    double rss0;
    double dof0;

    public void setS2(double[] s2)   { this.s2 = s2; }
    public void setFC(Matrix fc)     { this.fc = fc; }
    public void setRSS0(double rss0) { this.rss0 = rss0; }
    public void setDOF0(double dof0) { this.dof0 = dof0; }

    public double f_to_minimize(double lambda) {
      try {
	if (fc.getRowDimension() > s2.length) {
	  throw new IllegalStateException("Not enough s2's:  fc.numRows() > s2.length");
	}
	// f = lambda^2 ./ (s2 + lambda^2);
	double lambda2 = Math.pow(lambda, 2);
	int rows = fc.getRowDimension();
	Matrix f = new Matrix(rows,1);
	for (int i=0; i<rows; i++) {
	  double val = lambda2 / (s2[i] + lambda2);
	  f.set(i,0,val);
	}

	// G = (norm(f.*fc)^2 + rss0) / (dof0 + sum(f))^2;
	double denominator = Math.pow(dof0 + MathUtils.sum(f.getColumnPackedCopy()),2);
	for (int i=0; i<fc.getRowDimension(); i++) {
	  f.set(i,0,f.get(i,0) * fc.get(i,0));
	}
	double numerator = Math.pow(f.normF(),2) + rss0;

	return numerator / denominator;
      } catch(Exception e) {
	System.exit(1);
      }
      return 0;
    }

  }

  /** Computes the root mean squared error of two matrices
   * @param a
   * @param b
   * @return the RMSE of a and b
   */
  public static double rmse(Matrix a, Matrix b) {
    Matrix difference = a.minus(b);
    double rmse = Math.sqrt(JamaUtils.sum(difference.transpose().times(difference)));
    return rmse;
  }



  public static Matrix loadSparseMatrix(File file) {
    FileReader fileReader = null;
    try {

      fileReader = new FileReader(file);
      BufferedReader reader = new BufferedReader(fileReader);
      int lineNumber = 0;
      String line = null;
      String[] split = null;
      int rows=-1, cols=-1;

      // read the matrix size
      while ((line = reader.readLine()) != null) {
	lineNumber++;

	// skip lines that don't start with a number
	if (!line.matches("^\\d+?.*"))
	  continue;

	split = line.split("[\\s,;]");

	if (split.length != 2) {
	  throw new IllegalArgumentException("Invalid matrix file format:  file must start with the size of the matrix.  Error on line number "+lineNumber+".");
	}

	rows = Integer.parseInt(split[0]);
	cols = Integer.parseInt(split[1]);
	break;
      }

      Matrix matrix = new Matrix(rows,cols);
      int row = 0;

      // read each line of the matrix, skipping non-matrix rows
      while ((line = reader.readLine()) != null) {
	lineNumber++;

	// skip lines that don't start with a number
	if (!line.matches("^\\d+?.*"))
	  continue;

	split = line.split("[\\s,;]");
	// detect a full matrix specification
	if (split.length == cols) {
	  for (int col=0; col<cols; col++) {
	    matrix.set(row, col, Double.parseDouble(split[col]));
	  }
	} else if (split.length == 3) {
	  matrix.set(Integer.parseInt(split[0]),
	    Integer.parseInt(split[1]),
	    Double.parseDouble(split[2]));
	} else {
	  throw new IllegalArgumentException("Invalid matrix file format:  must be either a full or sparse specification.  Error on line number "+lineNumber+".");
	}
	row++;
      }

      return matrix;

    } catch (IOException e) {
      System.err.println("Invalid file:  "+file.getAbsolutePath());
    } finally {
      try {
	fileReader.close();
      } catch (Exception e) {}
    }

    return null;
  }


  /** Test function for regularized least squares. */
  public static void main(String[] args) {

    Matrix A = new Matrix(new double[][]{{1,2,3},{4,5,6},{7,8,10}});
    Matrix b = new Matrix(new double[][]{{0.5},{0.7},{0.8}});
    System.out.print("A = ");
    A.print(4,2);
    System.out.print("b = ");
    b.print(4,2);

    System.out.println("Solving Ax = b for x:");
    System.out.println("  Should be [-0.2405; 0.1480; 0.1368] with lambda = 0.41424");
    JamaUtils.regLeastSquares(A,b).print(6,4);

    System.out.println("Solving Ax = b for x with lambda = 0.1:");
    System.out.println("  Should be [-0.4192; 0.5114; -0.0342]");
    JamaUtils.regLeastSquares(A,b,0.1).print(6,4);

    System.out.println("Solving Ax = b for x with lambda = 0.5:");
    System.out.println("  Should be [-0.2120; 0.1180; 0.1416]");
    JamaUtils.regLeastSquares(A,b,0.5).print(6,4);

    System.out.println("Solving Ax = b for x with lambda = 1:");
    System.out.println("  Should be [-0.1007; 0.0593; 0.1136]");
    JamaUtils.regLeastSquares(A,b,1).print(6,4);


    A = new Matrix(new double[][]{{1,.2,.36},{1,.55,.65},{1,.8,.41}});
    b = new Matrix(new double[][]{{0.25},{0.17},{0.98}});
    System.out.print("A = ");
    A.print(4,2);
    System.out.print("b = ");
    b.print(4,2);

    System.out.println("Solving Ax = b for x:");
    System.out.println("  Should be [0.2180; 0.2234; 0.0565] with lambda = 1.2105");
    JamaUtils.regLeastSquares(A,b).print(6,4);

    System.out.println("Solving Ax = b for x with lambda = 0.1:");
    System.out.println("  Should be [0.5278; 1.2681; -1.5171]");
    JamaUtils.regLeastSquares(A,b,0.1).print(6,4);

    System.out.println("Solving Ax = b for x with lambda = 0.5:");
    System.out.println("  Should be [0.2347; 0.5517; -0.1535]");
    JamaUtils.regLeastSquares(A,b,0.5).print(6,4);

    System.out.println("Solving Ax = b for x with lambda = 1:");
    System.out.println("  Should be [0.2301; 0.2726; 0.0401]");
    JamaUtils.regLeastSquares(A,b,1).print(6,4);



    A = new Matrix(new double[][]{{1,2,3},{4,5,6},{7,8,9},{10,11,12},{13,14,15}});
    b = new Matrix(new double[][]{{0.5},{0.7},{0.8},{0.3},{0.4}});
    System.out.print("A = ");
    A.print(4,2);
    System.out.print("b = ");
    b.print(4,2);

    System.out.println("Solving Ax = b for x:");
    System.out.println("  Should be [-0.3250; -0.0047; 0.3156] with lambda = 0.45081");
    JamaUtils.regLeastSquares(A,b).print(6,4);

    System.out.println("Solving Ax = b for x with lambda = 0.1:");
    System.out.println("  Should be [-0.3550; -0.0066; 0.3418]");
    JamaUtils.regLeastSquares(A,b,0.1).print(6,4);

    System.out.println("Solving Ax = b for x with lambda = 0.5:");
    System.out.println("  Should be [-0.3185; -0.0043; 0.3098]");
    JamaUtils.regLeastSquares(A,b,0.5).print(6,4);

    System.out.println("Solving Ax = b for x with lambda = 1:");
    System.out.println("  Should be [-0.2400; 0.0005; 0.2409]");
    JamaUtils.regLeastSquares(A,b,1).print(6,4);


    A = new Matrix(new double[][]{{1,2,3,4,5},{6,7,8,9,10},{11,12,13,14,15}});
    b = new Matrix(new double[][]{{0.5},{0.7},{0.8}});
    System.out.print("A = ");
    A.print(4,2);
    System.out.print("b = ");
    b.print(4,2);

    System.out.println("Solving Ax = b for x:");
    System.out.println("  Should be [-0.0779; -0.0359; 0.0061; 0.0481; 0.0902] with lambda = 0.31315");
    JamaUtils.regLeastSquares(A,b).print(6,4);

    System.out.println("Solving Ax = b for x with lambda = 0.1:");
    System.out.println("  Should be [-0.0792; -0.0366; 0.0060; 0.0486; 0.0912]");
    JamaUtils.regLeastSquares(A,b,0.1).print(6,4);

    System.out.println("Solving Ax = b for x with lambda = 0.5:");
    System.out.println("  Should be [-0.0758; -0.0347; 0.0063; 0.0474; 0.0884]");
    JamaUtils.regLeastSquares(A,b,0.5).print(6,4);

    System.out.println("Solving Ax = b for x with lambda = 1:");
    System.out.println("  Should be [-0.0665; -0.0297; 0.0072; 0.0440; 0.0808]");
    JamaUtils.regLeastSquares(A,b,1).print(6,4);

    System.out.println();
    System.out.println("===============================================");
    System.out.println();

    A = new Matrix(new double[][]{{2.2,-4.3,5.4,1.2,4.8},{2.2,2.3,7.8,3.4,5.3}});
    b = new Matrix(new double[][]{{0.2},{0.4}});
    System.out.print("A = ");
    A.print(4,2);
    System.out.print("b = ");
    b.print(4,2);
    System.out.println("Solving Ax = b for x:");
    System.out.println("  Should be [0.0055; 0.0145; 0.0266; 0.0094; 0.0172] with lambda = 2.1381e-007");
    JamaUtils.regLeastSquares(A,b).print(6,4);

    A = new Matrix(new double[][]{{1,2,3,4,5,6,7},{1,7,6,5,4,3,2},{1,2,4,6,8,10,3}});
    b = new Matrix(new double[][]{{1},{2},{7}});
    System.out.print("A = ");
    A.print(4,2);
    System.out.print("b = ");
    b.print(4,2);
    System.out.println("Solving Ax = b for x:");
    System.out.println("  Should be [0.0130; 0.0235; 0.0602; 0.0969; 0.1336; 0.1703; 0.0032] with lambda = 12.1354");
    JamaUtils.regLeastSquares(A,b).print(6,4);

    A = new Matrix(new double[][]{{.211,.485,.5893,-.1,.1,-.342,.8,.19,-.20,-.91},{.211,-.2,.2,.48,.9,.94,-.28,.7,.39,.1},{.211,-.38,.85,.2,-.38,.19,-.1,0,-.48,.23}});
    b = new Matrix(new double[][]{{0.98},{0.47},{0.86}});
    System.out.print("A = ");
    A.print(4,2);
    System.out.print("b = ");
    b.print(4,2);
    System.out.println("Solving Ax = b for x:");
    System.out.println("  Should be [0.0989; 0.0017; 0.2891; 0.0632; 0.0311; 0.0583; 0.1062; 0.1008; -0.0915; -0.1193] with lambda = 1.7534");
    JamaUtils.regLeastSquares(A,b).print(6,4);

    A = new Matrix(new double[][]{{.211,.485,.5893},{-.1,.1,-.342},{.8,.19,-.20},{.211,-.2,.2},{.48,.9,.94},{-.28,.7,.39},{.211,-.38,.85},{.2,-.38,.19},{-.1,0,-.48}});
    b = new Matrix(new double[][]{{0.15},{0.7},{0.8},{0.5},{-0.7},{0.1},{0.45},{-0.7},{-0.8}});
    System.out.print("A = ");
    A.print(4,2);
    System.out.print("b = ");
    b.print(4,2);
    System.out.println("Solving Ax = b for x:");
    System.out.println("  Should be [0.0910; -0.0500; -0.0350] with lambda = 1.8448");
    JamaUtils.regLeastSquares(A,b).print(6,4);

    A = new Matrix(new double[][]{{1,-2,3},{-4,5,6},{7,8,-9},{-10,11,12},{13,-14,-15}});
    b = new Matrix(new double[][]{{0.5},{-0.7},{0.8},{0.3},{-0.4}});
    System.out.print("A = ");
    A.print(4,2);
    System.out.print("b = ");
    b.print(4,2);
    System.out.println("Solving Ax = b for x:");
    System.out.println("  Should be [0.0016;0.0079;-0.0013] with lambda = 32.5251");
    JamaUtils.regLeastSquares(A,b).print(6,4);
  }
}
