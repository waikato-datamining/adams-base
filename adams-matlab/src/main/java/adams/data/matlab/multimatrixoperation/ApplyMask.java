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
 * ApplyMask.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.matlab.multimatrixoperation;

import adams.data.matlab.MatlabUtils;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.types.Matrix;

/**
 * Performs element-wise subtraction and outputs the result as single matrix.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ApplyMask
  extends AbstractMultiMatrixOperation {

  private static final long serialVersionUID = 8924493141315007273L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the second matrix as mask that gets applied to the first matrix.\n"
      + "Elements from the first matrix are only kept if the corresponding "
      + "ones in the mask are non-zero.";
  }

  /**
   * Returns the minimum number of matrices that are required for the operation.
   *
   * @return the number of matrices that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumMatricesRequired() {
    return 2;
  }

  /**
   * Returns the maximum number of matrices that are required for the operation.
   *
   * @return the number of matrices that are required, <= 0 means no upper limit
   */
  @Override
  public int maxNumMatricesRequired() {
    return 2;
  }

  /**
   * Performs the actual processing of the matrices.
   *
   * @param matrices the matrices to process
   * @return the generated array(s)
   */
  @Override
  protected Matrix[] doProcess(Matrix[] matrices) {
    Matrix[]	result;
    int		i;
    double 	value;
    int[]	dims;
    int[]	index;
    boolean	finished;

    dims      = matrices[0].getDimensions();
    index     = new int[dims.length];
    result    = new Matrix[1];
    result[0] = Mat5.newMatrix(dims);
    finished  = false;
    while (!finished) {
      if (matrices[1].getDouble(index) == 0)
        result[0].setDouble(index, 0);
      else
        result[0].setDouble(index, matrices[0].getDouble(index));
      finished = MatlabUtils.increment(index, dims);
    }

    return result;
  }
}
