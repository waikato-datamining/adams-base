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
 * Combine.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.matlab.multimatrixoperation;

import adams.core.QuickInfoHelper;
import adams.data.matlab.ArrayElementType;
import adams.data.matlab.MatlabUtils;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.types.Matrix;

/**
 * Combines the matrices into single one with additional dimension.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Combine
  extends AbstractMultiMatrixOperation {

  private static final long serialVersionUID = 8924493141315007273L;

  /** the element type. */
  protected ArrayElementType m_ElementType;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines the matrices into single one with additional dimension.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "element-type", "elementType",
      ArrayElementType.DOUBLE);
  }

  /**
   * Sets the type of the element in the array.
   *
   * @param value	the type
   */
  public void setElementType(ArrayElementType value) {
    m_ElementType = value;
    reset();
  }

  /**
   * Returns the type of the element in the array.
   *
   * @return		the type
   */
  public ArrayElementType getElementType() {
    return m_ElementType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String elementTypeTipText() {
    return "Specifies the type of the values in the array.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "elementType", m_ElementType, "element: ");
  }

  /**
   * Returns the minimum number of matrices that are required for the operation.
   *
   * @return the number of matrices that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumMatricesRequired() {
    return 1;
  }

  /**
   * Returns the maximum number of matrices that are required for the operation.
   *
   * @return the number of matrices that are required, <= 0 means no upper limit
   */
  @Override
  public int maxNumMatricesRequired() {
    return 0;
  }

  /**
   * Performs the actual processing of the matrices.
   *
   * @param matrices the matrices to process
   * @return the generated array(s)
   */
  @Override
  protected Matrix[] doProcess(Matrix[] matrices) {
    Matrix[]		result;
    int[]	inputIndex;
    int[]	inputDims;
    int[] 	inputOpen;
    int[]	outputIndex;
    int[]	outputDims;
    int[] 	outputOpen;
    int		i;

    inputDims   = matrices[0].getDimensions();
    outputDims  = new int[inputDims.length + 1];
    System.arraycopy(inputDims, 0, outputDims, 0, inputDims.length);
    outputDims[outputDims.length - 1] = matrices.length;
    inputOpen   = new int[inputDims.length];
    outputOpen  = new int[inputDims.length];
    inputIndex  = new int[inputDims.length];
    outputIndex = new int[outputDims.length];
    for (i = 0; i < inputDims.length; i++) {
      inputOpen[i]  = i;
      outputOpen[i] = i;
    }
    result    = new Matrix[1];
    result[0] = Mat5.newMatrix(outputDims);
    for (i = 0; i < matrices.length; i++) {
      outputIndex[outputIndex.length - 1] = i;
      MatlabUtils.transfer(
        matrices[i], inputIndex.clone(), inputDims, inputOpen,
        result[0], outputIndex.clone(), outputDims, outputOpen, m_ElementType);
    }

    return result;
  }
}
