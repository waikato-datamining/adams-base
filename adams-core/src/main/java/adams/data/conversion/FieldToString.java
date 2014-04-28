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
 * FieldToString.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.report.AbstractField;

/**
 <!-- globalinfo-start -->
 * Turns a Field object into a String.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-append-data-type (property: appendDataType)
 * &nbsp;&nbsp;&nbsp;If enabled, the data type is appended as well.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FieldToString
  extends AbstractConversionToString {

  /** for serialization. */
  private static final long serialVersionUID = 8828591710515484463L;

  /** whether to output the data type as well. */
  protected boolean m_AppendDataType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Turns a Field object into a String.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "append-data-type", "appendDataType",
	    false);
  }

  /**
   * Sets whether to append the data type.
   *
   * @param value	if true then the data type is appended
   */
  public void setAppendDataType(boolean value) {
    m_AppendDataType = value;
    reset();
  }

  /**
   * Returns whether to append the data type.
   *
   * @return		true if the data type is appended
   */
  public boolean getAppendDataType() {
    return m_AppendDataType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String appendDataTypeTipText() {
    return "If enabled, the data type is appended as well.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  public Class accepts() {
    return AbstractField.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    if (m_AppendDataType)
      return ((AbstractField) m_Input).toParseableString();
    else
      return ((AbstractField) m_Input).toString();
  }
}
