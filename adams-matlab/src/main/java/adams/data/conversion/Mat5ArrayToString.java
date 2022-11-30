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
 * Mat5ArrayToString.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.matlab.ArrayElementType;
import adams.data.matlab.MatlabUtils;
import us.hebi.matlab.mat.types.Array;
import us.hebi.matlab.mat.types.Matrix;

/**
 <!-- globalinfo-start -->
 * Converts matrices into a textual representation, otherwise just outputs the dimensions.
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
 * <pre>-compact &lt;boolean&gt; (property: compact)
 * &nbsp;&nbsp;&nbsp;If enabled, the output omits spaces.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-num-decimals &lt;int&gt; (property: numDecimals)
 * &nbsp;&nbsp;&nbsp;The number of decimals after the decimal point to use; -1 means automatic.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Mat5ArrayToString
  extends AbstractConversionToString {

  private static final long serialVersionUID = -5951006048504569301L;

  /** the element type. */
  protected ArrayElementType m_ElementType;

  /** whether to output compact string (ie no blanks). */
  protected boolean m_Compact;

  /** the number of decimals. */
  protected int m_NumDecimals;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts matrices into a textual representation, otherwise just outputs the dimensions.";
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

    m_OptionManager.add(
      "compact", "compact",
      false);

    m_OptionManager.add(
      "num-decimals", "numDecimals",
      -1, -1, null);
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
   * Sets whether to output compact format (ie no spaces).
   *
   * @param value	true if compact
   */
  public void setCompact(boolean value) {
    m_Compact = value;
    reset();
  }

  /**
   * Returns whether to output compact format (ie no spaces).
   *
   * @return		true if compact
   */
  public boolean getCompact() {
    return m_Compact;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String compactTipText() {
    return "If enabled, the output omits spaces.";
  }

  /**
   * Sets the number of decimals after the decimal point to use.
   *
   * @param value	the number of decimals, -1 is automatic
   */
  public void setNumDecimals(int value) {
    if (getOptionManager().isValid("numDecimals", value)) {
      m_NumDecimals = value;
      reset();
    }
  }

  /**
   * Returns the number of decimals after the decimal point to use.
   *
   * @return		the number of decimals, -1 is automatic
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDecimalsTipText() {
    return "The number of decimals after the decimal point to use; -1 means automatic.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "elementType", m_ElementType, "element: ");
    result +=  QuickInfoHelper.toString(this, "compact", m_Compact, "compact", ", ");
    result += QuickInfoHelper.toString(this, "numDecimals", (m_NumDecimals == -1 ? "auto" : "" + m_NumDecimals), ", #decimals: ");

    return result;
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
   * Checks whether the data can be processed.
   *
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String checkData() {
    String 	result;

    result = super.checkData();

    if (result == null) {
      if (!(m_Input instanceof Matrix))
	result = "Expected " + Utils.classToString(Matrix.class) + " but got " + Utils.classToString(m_Input);
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
    StringBuilder	result;
    Matrix		input;
    int			numCols;
    int			numRows;
    int			x;
    int			y;
    int[]		index;
    Object		value;

    result = new StringBuilder();
    input  = (Matrix) m_Input;

    if (input.getNumDimensions() == 2) {
      numRows = input.getNumRows();
      numCols = input.getNumCols();
      index = new int[2];
      result.append("[");
      for (y = 0; y < numRows; y++) {
	if (y > 0) {
	  result.append(";");
	  if (!m_Compact)
	    result.append(" ");
	}
	for (x = 0; x < numCols; x++) {
	  if (x > 0) {
	    result.append(",");
	    if (!m_Compact)
	      result.append(" ");
	  }
	  index[0] = y;
	  index[1] = x;
	  value    = MatlabUtils.getElement(input, index, m_ElementType);
	  if ((m_NumDecimals > -1) && (value instanceof Number))
	    value = Utils.doubleToString(((Number) value).doubleValue(), m_NumDecimals);
	  result.append(value);
	}
      }
      result.append("]");
    }
    else {
      result.append(MatlabUtils.arrayDimensionsToString(input));
    }

    return result.toString();
  }
}
