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
 * AbstractMultiMatrixOperation.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.matlab.multimatrixoperation;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.data.matlab.MatlabUtils;
import us.hebi.matlab.mat.types.Matrix;

/**
 * Abstract base class for operations that require multiple matrices.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMultiMatrixOperation
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 1185449853784824033L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br><br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the minimum number of matrices that are required for the operation.
   *
   * @return		the number of matrices that are required, <= 0 means no lower limit
   */
  public abstract int minNumMatricesRequired();

  /**
   * Returns the maximum number of matrices that are required for the operation.
   *
   * @return		the number of matrices that are required, <= 0 means no upper limit
   */
  public abstract int maxNumMatricesRequired();

  /**
   * Checks whether the two matrices have the same dimensions.
   *
   * @param array1	the first array
   * @param array2	the second array
   * @return		true if the same dimensions
   */
  protected boolean checkSameDimensions(Matrix array1, Matrix array2) {
    return MatlabUtils.arrayDimensionsToString(array1).equals(MatlabUtils.arrayDimensionsToString(array2));
  }

  /**
   * Checks whether the matrices have the same dimensions.
   *
   * @param matrices	the matrices
   * @return		null if the same dimensions, other error message
   */
  protected String checkSameDimensions(Matrix[] matrices) {
    int		i;

    for (i = 1; i < matrices.length; i++) {
      if (!checkSameDimensions(matrices[0], matrices[i]))
	return
	  "All matrices need to have the same dimensions: "
	    + MatlabUtils.arrayDimensionsToString(matrices[0]) + " (#1)"
	    + " != "
	    + MatlabUtils.arrayDimensionsToString(matrices[i]) + "(#" + (i+1) +")";
    }

    return null;
  }

  /**
   * Checks the matrices.
   * <br><br>
   * Default implementation only ensures that matrices are present.
   *
   * @param matrices	the matrices to check
   */
  protected void check(Matrix[] matrices) {
    if ((matrices == null) || (matrices.length == 0))
      throw new IllegalStateException("No matrices provided!");

    if (minNumMatricesRequired() > 0) {
      if (matrices.length < minNumMatricesRequired())
	throw new IllegalStateException(
	  "Not enough matrices supplied (min > supplied): " + minNumMatricesRequired() + " > " + matrices.length);
    }

    if (maxNumMatricesRequired() > 0) {
      if (matrices.length > maxNumMatricesRequired())
	throw new IllegalStateException(
	  "Too many matrices supplied (max < supplied): " + maxNumMatricesRequired() + " < " + matrices.length);
    }
  }

  /**
   * Performs the actual processing of the matrices.
   *
   * @param matrices	the matrices to process
   * @return		the generated array(s)
   */
  protected abstract Matrix[] doProcess(Matrix[] matrices);

  /**
   * Processes the matrices.
   *
   * @param matrices	the matrices to process
   * @return		the generated array(s)
   */
  public Matrix[] process(Matrix[] matrices) {
    check(matrices);
    return doProcess(matrices);
  }
}
