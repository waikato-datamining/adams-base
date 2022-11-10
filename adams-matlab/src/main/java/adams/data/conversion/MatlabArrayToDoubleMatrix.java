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
 * MatlabArrayToDoubleMatrix.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.Utils;
import us.hebi.matlab.mat.types.Array;
import us.hebi.matlab.mat.types.Matrix;

/**
 <!-- globalinfo-start -->
 * Converts a 2-dimensional Matlab array into a double matrix.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MatlabArrayToDoubleMatrix
  extends AbstractConversion {

  private static final long serialVersionUID = 1324403475035054937L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a 2-dimensional Matlab array into a double matrix.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return Array.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return Double[][].class;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    Double[][]	result;
    Array	array;
    Matrix	matrix;
    int		i;
    int		n;

    array = (Array) m_Input;
    if (array.getNumDimensions() > 2)
      throw new IllegalStateException("Cannot handle arrays with more than two dimensions, received: " + array.getNumDimensions());

    if (!(array instanceof Matrix))
      throw new IllegalStateException("Array is not of type " + Utils.classToString(Matrix.class) + "!");
    matrix = (Matrix) array;

    // transfer data
    result = new Double[matrix.getNumRows()][matrix.getNumCols()];
    for (n = 0; n < array.getNumRows(); n++) {
      for (i = 0; i < array.getNumCols(); i++)
	result[n][i] = matrix.getDouble(n, i);
    }

    return result;
  }
}
