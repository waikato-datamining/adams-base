package edu.umbc.cs.maple.utils;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/** This class implements utility functions for Spectral Graph Theory methods.
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
public class SGTUtils {

  /** Defines the type of the graph Laplacian */
  public enum LaplacianType { COMBINATORIAL, NORMALIZED }



  /** Computes the Laplacian matrix for a weighted adjacency matrix A.
   * Automatically detects whether the adjacency matrix is directed or undirected.
   *
   * @param adjacencyMatrix the adjacency matrix
   * @param laplacianType whether to use the normalized or combinatorial form of the Laplacian
   */
  public static Matrix weightedAdjacencyToLaplacian(Matrix adjacencyMatrix, LaplacianType laplacianType) {
    // an undirected graph must have a symmetric adjacency matrix
    if (JamaUtils.isSymmetric(adjacencyMatrix))
      return undirectedWeightedAdjacencyToLaplacian(adjacencyMatrix, laplacianType);
    else
      return directedWeightedAdjacencyToLaplacian(adjacencyMatrix, laplacianType);
  }




  /** Computes the Laplacian matrix for an undirected weighted adjacency matrix A.
   * Coded from Section 1.4 of Chung's Spectral Graph Theory
   *
   * @param adjacencyMatrix the adjacency matrix
   * @param laplacianType whether to use the normalized or combinatorial form of the Laplacian
   */
  public static Matrix undirectedWeightedAdjacencyToLaplacian(Matrix adjacencyMatrix, LaplacianType laplacianType) {

    int numRows = adjacencyMatrix.getRowDimension();
    int numCols = adjacencyMatrix.getColumnDimension();

    if (numRows != numCols) {
      throw new IllegalArgumentException("adjacencyMatrix must be square.");
    }

    // precompute the column sums
    double[] colsums = new double[numCols];
    for (int i=0; i<numCols; i++) {
      colsums[i] = JamaUtils.colsum(adjacencyMatrix, i);
    }


    Matrix Laplacian = new Matrix(numRows, numCols);

    switch (laplacianType) {

      case NORMALIZED:
	for (int u=0; u<numRows; u++) {
	  double du = colsums[u];
	  for (int v=0; v<numRows; v++) {
	    double dv = colsums[v];

	    // conditional determination of Laplacian[u][v]
	    double lapUV;
	    if (u==v && dv != 0)						// if u == v and dv != 0
	      lapUV = 1 - adjacencyMatrix.get(v,v)/dv;

	    else if (adjacencyMatrix.get(u,v) > 0)		// if u and v are adjacent
	      lapUV = -adjacencyMatrix.get(u,v) / Math.sqrt(du*dv);

	    else										// otherwise
	      lapUV = 0;

	    Laplacian.set(u,v,lapUV);
	  }
	}
	break;

      case COMBINATORIAL:
	for (int u=0; u<numRows; u++) {
	  for (int v=0; v<numRows; v++) {
	    double dv = colsums[v];

	    // conditional determination of Laplacian[u][v]
	    double lapUV;
	    if (u==v)									// if u == v
	      lapUV = dv - adjacencyMatrix.get(v,v);

	    else if (adjacencyMatrix.get(u,v) > 0)		// if u and v are adjacent
	      lapUV = -adjacencyMatrix.get(u,v);

	    else										// otherwise
	      lapUV = 0;

	    Laplacian.set(u,v,lapUV);
	  }
	}
	break;
    }

    return Laplacian;
  }



  /** Computes the Laplacian matrix for a directed weighted adjacency matrix A.
   *
   * @param adjacencyMatrix the adjacency matrix
   * @param laplacianType whether to use the normalized or combinatorial form of the Laplacian
   */
  public static Matrix directedWeightedAdjacencyToLaplacian(Matrix adjacencyMatrix, LaplacianType laplacianType) {

    int numRows = adjacencyMatrix.getRowDimension();
    int numCols = adjacencyMatrix.getColumnDimension();


    if (numRows != numCols) {
      throw new IllegalArgumentException("adjacencyMatrix must be square.");
    }

    // precompute the row sums
    double[] rowsums = new double[numRows];
    for (int i=0; i<numRows; i++) {
      rowsums[i] = JamaUtils.rowsum(adjacencyMatrix, i);
    }

    // compute the transition probability matrix and insure that the
    // out-transition probabilities (rows) sum to 1
    // (automatically add in uniform out edges to all other nodes from a sink)
    Matrix P = new Matrix(numRows, numCols);
    double defaultTransitionProbability = 1.0 / (numRows-1);
    for (int i=0; i<numRows; i++) {
      for (int j=0; j<numCols; j++) {
	double value;
	if (rowsums[i] == 0) {
	  value = (i==j) ? 0 : defaultTransitionProbability; // no self-loops
	} else {
	  value = adjacencyMatrix.get(i,j) / rowsums[i];
	}
	P.set(i,j,value);
      }
    }



    // compute the Perron vector of P

    // compute the teleport matrix
    // TODO: according to Jeff Johns and Mahadevan (ICML 2007), we don't need to explicitly
    // compute the Pteleport matrix and can do it in O(n) instead
    double eta = 0.99;
    Matrix Pteleport = P.times(eta).plus(JamaUtils.ones(numRows,numRows).times((1-eta)/numRows));

    Matrix psi = Matrix.random(numRows,1);
    psi = JamaUtils.normalize(psi); //normalize to sum to 1

    // loop until convergence
    boolean converged = false;
    int numIterations = 0;
    while (!converged) {

      Matrix psi_new = (psi.transpose().times(Pteleport)).transpose();
      psi_new = JamaUtils.normalize(psi_new);

      // test for convergence
      double norm = psi.minus(psi_new).normF();
      if (norm < 1e-8) {
	converged = true;
      }
      //  System.out.println(norm);
      psi = psi_new;

      // test for max number of iterations
      numIterations++;
      if (numIterations > 500) {
	converged = true;
	System.err.println("Power method exceeded maximum number of iterations.  Results may be inaccurate.  Current norm:  " + norm);
      }
    }

    // form the matrix phi with the Perron vector as the diagonal
    Matrix phi = new Matrix(numRows, numCols);
    for (int i=0; i<numRows; i++) {
      phi.set(i,i,psi.get(i,0));
    }


    // precompute the conjugate transpose of P, which is just P.transpose() in our case
    Matrix PconjugateTranspose = P.transpose();


    // compute the Laplacian
    Matrix Laplacian = null;

    switch (laplacianType) {

      case NORMALIZED:
	// compute the square root and negative square root of phi;
	// since phi is diagonal, this is simply the powers of the diagonals
	Matrix phiSquareRoot = new Matrix(numRows, numCols);
	Matrix phiNegSquareRoot = new Matrix(numRows, numCols);
	for (int i=0; i<numRows; i++) {
	  double value = phi.get(i,i);
	  phiSquareRoot.set(i,i, Math.sqrt(value));
	  phiNegSquareRoot.set(i,i,Math.pow(value,-0.5));
	}

	// \Phi^{1/2} P \Phi^{-1/2}
	Matrix temp1 = phiSquareRoot.times(P.times(phiNegSquareRoot));
	// \Phi^{-1/2} P^* \Phi^{1/2}
	Matrix temp2 = phiNegSquareRoot.times(PconjugateTranspose.times(phiSquareRoot));
	// \frac{\Phi^{1/2} P \Phi^{-1/2} + \Phi^{-1/2} P^* \Phi^{1/2}}{2}
	Matrix temp3 = (temp1.plus(temp2)).times(0.5);
	// I - \frac{\Phi^{1/2} P \Phi^{-1/2} + \Phi^{-1/2} P^* \Phi^{1/2}}{2}
	Laplacian = Matrix.identity(numRows,numCols).minus(temp3);
	break;

      case COMBINATORIAL:
	// \Phi P
	Matrix tempA = phi.times(P);
	// P^* \Phi
	Matrix tempB = PconjugateTranspose.times(phi);
	// \frac{\Phi P +  P^* \Phi}{2}
	Matrix tempC = (tempA.plus(tempB)).times(0.5);
	// \Phi - \frac{\Phi P +  P^* \Phi}{2}
	Laplacian = phi.minus(tempC);
	break;
    }

    return Laplacian;
  }



  /** Defines whether the largest or the smallest eigenvalues are the most
   * important (i.e. corresponding to the lowest-order components).
   *
   */
  public enum KeyEigenvalues {LARGEST, SMALLEST}

  /** Computes the specified resolution of matrix A.
   * @param A the matrix
   * @param resolution the resolution
   * @param keyEigenvalues specifies whether the top LARGEST or SMALLEST
   * 	eigenvalues should be taken.  LARGEST should be the choice for most
   *  applications; SMALLEST should be the choice for eigenvectors of the
   *  graph Laplacian.
   * @return three matrices M[3]
   *   M[0]:  Ak, A at the specified resolution
   *   M[1]:  Qk, the eigenvectors used to compose Ak
   *   M[2]:  Lk, the eigenvalues used to compose Ak
   */
  public static Matrix[] resolution(Matrix A, int resolution, KeyEigenvalues keyEigenvalues) {


    // perform the eigendecomposition
    EigenvalueDecomposition eig = A.eig();
    Matrix Q = eig.getV();
    Matrix L = eig.getD();

    int n = Q.getColumnDimension();
    Matrix Lk=null, Qk=null;

    if (resolution > n) {
      throw new IllegalArgumentException("Max resolution available is: "+n+".");
    }


    switch (keyEigenvalues) {

      case SMALLEST:
	// keep only the smallest eigenvalues (zero the rest)
	// Matlab code:
	// L((resolution+1):end, (resolution+1):end) = 0;
	// Lk = L(1:resolution, 1:resolution);
	// Qk = Q(:, 1:resolution);
	for (int i=resolution; i<n; i++)
	  for (int j=resolution; j<n; j++)
	    L.set(i,j,0);
	Lk = L.getMatrix(0,resolution-1,0,resolution-1);
	Qk = Q.getMatrix(0,n-1,0,resolution-1);
	break;

      case LARGEST:
	// keep only the largest eigenvalues (zero the rest)
	// Matlab code:
	// L(1:(n-resolution), 1:(n-resolution)) = 0;
	// Lk = L((n-resolution+1):end, (n-resolution+1):end);
	// Qk = Q(:, (n-resolution+1):end);
	int stop = n-resolution;
	for (int i=0; i<stop; i++)
	  for (int j=0; j<stop; j++)
	    L.set(i,j,0);
	Lk = L.getMatrix(stop,n-1,stop,n-1);
	Qk = Q.getMatrix(0,n-1,stop,n-1);
	break;
    }

    // recompose the matrix  (Q * L * Q')
    Matrix Ak = Q.times(L).times(Q.transpose());

    Matrix[] struct = new Matrix[3];
    struct[0] = Ak;
    struct[1] = Qk;
    struct[2] = Lk;
    return struct;

  }



  /** Computes the specified resolution of a function on a graph.
   * @param graphLaplacian the graph Laplacian
   * @param f the function values on the vertices of the graph
   * @param resolution the resolution
   * @return three matrices M[3]
   *   M[0]:  fk, f at the specified resolution on the graph
   *   M[1]:  Qk, the eigenfunctions of the graph Laplacian used in the computation
   *   M[2]:  Lk, the eigenvalues of the graph Laplacian used in the computation
   */
  public static Matrix[] resolutionGraphFunction(Matrix graphLaplacian, Matrix f, int resolution) {

    // perform the eigendecomposition of the Laplacian
    Matrix[] eigLaplacian = resolution(graphLaplacian, resolution, KeyEigenvalues.SMALLEST);
    Matrix Qk = eigLaplacian[1];
    Matrix Lk = eigLaplacian[2];
    Matrix fk = projectFunctionToBasis(Qk,f);

    Matrix[] struct = new Matrix[3];
    struct[0] = fk;
    struct[1] = Qk;
    struct[2] = Lk;
    return struct;
  }

  /** Computes the projection of a function onto another basis.
   * @param basisVectors the basis vectors
   * @param f the function values on the vertices of the graph
   * @return one matrix f on the basis vectors
   */
  public static Matrix projectFunctionToBasis(Matrix basisVectors, Matrix f) {

    Matrix Qk = basisVectors;

    int numrowsf = f.getRowDimension();
    int numcolsf = f.getColumnDimension();
    int numcolsQk = Qk.getColumnDimension();

    // reconstruct the function at that resolution
    Matrix fk = new Matrix(numrowsf, numcolsf);
    for (int col=0; col<numcolsf; col++) {

      // get the col^th column of f
      Matrix f_col = JamaUtils.getcol(f,col);

      for (int i=0; i<numcolsQk; i++) {

	// get the ith column of Qk
	Matrix qi = JamaUtils.getcol(Qk,i);

	// add the projection of fi onto qi to fk
	// Matlab code:  fk(:,col) = fk(:,col) + dot(f(:,col),qi)*qi;
	Matrix temp = JamaUtils.getcol(fk,col).plus(
	  qi.times(JamaUtils.dotproduct(f_col,qi)));
	JamaUtils.setcol(fk,col,temp);
      }
    }

    return fk;
  }

}
