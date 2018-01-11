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
 * GenericPLSMatrixAccess.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package weka.core;

import weka.core.matrix.Matrix;

/**
 * For classes that allow access to PLS matrices.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface GenericPLSMatrixAccess {

  /**
   * Returns the all the available matrices.
   *
   * @return		the names of the matrices
   */
  public abstract String[] getMatrixNames();

  /**
   * Returns the matrix with the specified name.
   *
   * @param name	the name of the matrix
   * @return		the matrix, null if not available
   */
  public abstract Matrix getMatrix(String name);

  /**
   * Whether the algorithm supports return of loadings.
   *
   * @return		true if supported
   * @see		#getLoadings()
   */
  public abstract boolean hasLoadings();

  /**
   * Returns the loadings, if available.
   *
   * @return		the loadings, null if not available
   */
  public abstract Matrix getLoadings();
}
