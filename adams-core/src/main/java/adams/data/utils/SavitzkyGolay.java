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
 * SavitzkyGolay.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.utils;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.LUDecomposition;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;

import adams.core.Utils;

/**
 * A helper class for Savitzky-Golay.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SavitzkyGolay {

  /**
   * Determines the coefficients for the smoothing.
   *
   * @param numLeft	the number of points to the left
   * @param numRight	the number of points to the right
   * @param polyOrder	the polynomial order
   * @param derOrder	the derivative order
   * @return		the coefficients
   */
  public static double[] determineCoefficients(int numLeft, int numRight, int polyOrder, int derOrder) {
    return determineCoefficients(numLeft, numRight, polyOrder, derOrder, false);
  }

  /**
   * Determines the coefficients for the smoothing, with optional debugging
   * output.
   *
   * @param numLeft	the number of points to the left
   * @param numRight	the number of points to the right
   * @param polyOrder	the polynomial order
   * @param derOrder	the derivative order
   * @param debug	whether to output debugging information
   * @return		the coefficients
   */
  public static double[] determineCoefficients(int numLeft, int numRight, int polyOrder, int derOrder, boolean debug) {
    double[]		result;
    RealMatrix		A;
    int			i;
    int			j;
    int			k;
    float		sum;
    RealMatrix		b;
    LUDecomposition	lu;
    RealMatrix		solution;

    result = new double[numLeft + numRight + 1];

    // no window?
    if (result.length == 1) {
      result[0] = 1.0;
      return result;
    }

    // Note: "^" = superscript, "." = subscript

    // {A^T*A}.ij = Sum[k:-nl..nr](k^(i+j))
    A = new Array2DRowRealMatrix(polyOrder + 1, polyOrder + 1);
    for (i = 0; i < A.getRowDimension(); i++) {
      for (j = 0; j < A.getColumnDimension(); j++) {
	sum = 0;
	for (k = -numLeft; k <= numRight; k++)
	  sum += Math.pow(k, i + j);
	A.setEntry(i, j, sum);
      }
    }
    if (debug)
      System.out.println("A:\n" + A);

    // LU decomp for inverse matrix
    b = new Array2DRowRealMatrix(polyOrder + 1, 1);
    b.setEntry(derOrder, 0, 1.0);
    if (debug)
      System.out.println("b:\n" + b);

    lu       = new LUDecompositionImpl(A);
    solution = lu.getSolver().solve(b);
    if (debug)
      System.out.println("LU decomp. - solution:\n" + solution);

    // coefficients: c.n = Sum[m:0..M]((A^T*A)^-1).0m * n^m with n=-nl..nr
    for (i = -numLeft; i <= numRight; i++) {
      sum = 0;
      for (j = 0; j <= polyOrder; j++)
	sum += solution.getEntry(j, 0) * Math.pow(i, j);
      result[i + numLeft] = sum;
    }
    if (debug)
      System.out.println("Coefficients:\n" + Utils.arrayToString(result));

    return result;
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public static TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;
    TechnicalInformation 	additional;

    result = new TechnicalInformation(Type.ARTICLE);
    result.setValue(Field.AUTHOR, "A. Savitzky and Marcel J.E. Golay");
    result.setValue(Field.TITLE, "Smoothing and Differentiation of Data by Simplified Least Squares Procedures");
    result.setValue(Field.JOURNAL, "Analytical Chemistry");
    result.setValue(Field.VOLUME, "36");
    result.setValue(Field.PAGES, "1627-1639");
    result.setValue(Field.YEAR, "1964");
    result.setValue(Field.HTTP, "http://dx.doi.org/10.1021/ac60214a047");

    additional = result.add(Type.INBOOK);
    additional.setValue(Field.AUTHOR, "William H. Press and Saul A. Teukolsky and William T. Vetterling and Brian P. Flannery");
    additional.setValue(Field.SERIES, "Numerical Recipes in C");
    additional.setValue(Field.EDITION, "Second");
    additional.setValue(Field.TITLE, "Savitzky-Golay Smoothing Filters");
    additional.setValue(Field.CHAPTER, "14.8");
    additional.setValue(Field.PAGES, "650-655");
    additional.setValue(Field.YEAR, "1992");
    additional.setValue(Field.PUBLISHER, "Cambridge University Press");
    additional.setValue(Field.PDF, "http://www.nrbook.com/a/bookcpdf/c14-8.pdf");

    return result;
  }
}
