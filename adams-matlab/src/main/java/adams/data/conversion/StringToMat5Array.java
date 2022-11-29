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
 * StringToMat5Array.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.matlab.ArrayElementType;
import adams.data.matlab.MatlabUtils;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.types.Array;
import us.hebi.matlab.mat.types.Matrix;

/**
 <!-- globalinfo-start -->
 * Converts a string like '[1, 2; 3, 4]' into a Matlab matrix.
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
public class StringToMat5Array
  extends AbstractConversionFromString {

  private static final long serialVersionUID = 328317728225004681L;

  /** the element type. */
  protected ArrayElementType m_ElementType;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a string like '[1, 2; 3, 4]' into a Matlab matrix.";
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
    String	input;

    result = super.checkData();

    if (result == null) {
      input = (String) m_Input;
      input = input.trim();
      if (!(input.startsWith("[") && input.endsWith("]")))
	result = "Expected input to start with '[' and end with ']', but received: " + input;
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
    String	input;
    String[]	lines;
    String[]	cells;
    int		y;
    int		x;
    Object	value;
    int[]	index;

    result = null;
    input  = (String) m_Input;
    input  = input.substring(1, input.length() - 1);
    lines  = input.replace(" ", "").split(";");
    index  = new int[2];
    for (y = 0; y < lines.length; y++) {
      cells = lines[y].split(",");
      if (result == null)
	result = Mat5.newMatrix(new int[]{lines.length, cells.length});
      for (x = 0; x < cells.length; x++) {
	switch (m_ElementType) {
	  case BOOLEAN:
	    value = Boolean.parseBoolean(cells[x]);
	    break;
	  case BYTE:
	    value = Byte.parseByte(cells[x]);
	    break;
	  case SHORT:
	    value = Short.parseShort(cells[x]);
	    break;
	  case INTEGER:
	    value = Integer.parseInt(cells[x]);
	    break;
	  case LONG:
	    value = Long.parseLong(cells[x]);
	    break;
	  case FLOAT:
	    value = Float.parseFloat(cells[x]);
	    break;
	  case DOUBLE:
	    value = Double.parseDouble(cells[x]);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled element type: " + m_ElementType);
	}
	index[0] = y;
	index[1] = x;
	MatlabUtils.setElement(result, index, m_ElementType, value);
      }
    }

    return result;
  }
}
