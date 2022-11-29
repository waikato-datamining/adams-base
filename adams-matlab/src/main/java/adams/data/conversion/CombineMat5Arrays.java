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
 * CombineMat5Arrays.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.matlab.ArrayElementType;
import adams.data.matlab.MatlabUtils;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.types.Array;
import us.hebi.matlab.mat.types.Matrix;

/**
 <!-- globalinfo-start -->
 * Combines multiple arrays (with the same dimensions) into a single one with an additional dimension.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-element-type &lt;BOOLEAN|BYTE|SHORT|INTEGER|LONG|FLOAT|DOUBLE&gt; (property: elementType)
 * &nbsp;&nbsp;&nbsp;Specifies the type of the values in the array.
 * &nbsp;&nbsp;&nbsp;default: DOUBLE
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class CombineMat5Arrays
  extends AbstractConversion {

  private static final long serialVersionUID = 829261648285123129L;

  /** the element type. */
  protected ArrayElementType m_ElementType;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines multiple arrays (with the same dimensions) into a single one with an additional dimension.";
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
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return Array[].class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return Array.class;
  }

  /**
   * Checks whether the data can be processed.
   *
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String checkData() {
    String	result;
    Array[]	input;
    String	dims;
    String	dimsOther;
    int		i;

    result = super.checkData();

    if (result == null) {
      input = (Array[]) m_Input;

      // length
      if (input.length == 0)
	result = "Require at least one array, but received 0-length array!";

      // array type
      if (result == null) {
	for (i = 0; i < input.length; i++) {
	  if (!(input[i] instanceof Matrix)) {
	    result = "Array #" + (i+1) + " is not a " + Utils.classToString(Matrix.class) + " but " + Utils.classToString(input[i]) + ".";
	    break;
	  }
	}
      }

      // dimensions
      if (result == null) {
	dims = MatlabUtils.arrayDimensionsToString(input[0]);
	for (i = 1; i < input.length; i++) {
	  dimsOther = MatlabUtils.arrayDimensionsToString(input[i]);
	  if (!dims.equals(dimsOther)) {
	    result = "Expected dimensions " + dims + " at #" + (i+1) + ", but found " + dimsOther;
	    break;
	  }
	}
      }
    }

    return result;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    Matrix	result;
    Array[]	input;
    int[]	inputIndex;
    int[]	inputDims;
    int[] 	inputOpen;
    int[]	outputIndex;
    int[]	outputDims;
    int[] 	outputOpen;
    int		i;

    input      = (Array[]) m_Input;
    inputDims  = input[0].getDimensions();
    outputDims = new int[inputDims.length + 1];
    System.arraycopy(inputDims, 0, outputDims, 0, inputDims.length);
    outputDims[outputDims.length - 1] = input.length;
    inputOpen = new int[inputDims.length];
    outputOpen = new int[inputDims.length];
    inputIndex = new int[inputDims.length];
    outputIndex = new int[outputDims.length];
    for (i = 0; i < inputDims.length; i++) {
      inputOpen[i] = i;
      outputOpen[i] = i;
    }
    result = Mat5.newMatrix(outputDims);
    for (i = 0; i < input.length; i++) {
      outputIndex[outputIndex.length - 1] = i;
      MatlabUtils.transfer(
        (Matrix) input[i], inputIndex.clone(), inputDims, inputOpen,
	result, outputIndex.clone(), outputDims, outputOpen, m_ElementType);
    }

    return result;
  }

  public static void main(String[] args) throws Exception {
    Matrix m1 = Mat5.newMatrix(new int[]{2, 2});
    m1.setDouble(0, 0, 1.0);
    m1.setDouble(1, 1, 1.0);
    Matrix m2 = Mat5.newMatrix(new int[]{2, 2});
    m2.setDouble(0, 1, 2.0);
    m2.setDouble(1, 0, 2.0);
    CombineMat5Arrays conv = new CombineMat5Arrays();
    conv.setInput(new Array[]{m1, m2});
    String msg = conv.convert();
    if (msg != null)
      System.err.println(msg);
    else
      System.out.println(conv.getOutput());
  }
}
