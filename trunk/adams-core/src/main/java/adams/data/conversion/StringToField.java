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
 * StringToField.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Field;

/**
 <!-- globalinfo-start -->
 * Turns a String into a Field object.
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
 * <pre>-default-data-type &lt;S|N|B|U&gt; (property: defaultDataType)
 * &nbsp;&nbsp;&nbsp;The default data type to use for overrding U if the string does not have
 * &nbsp;&nbsp;&nbsp;a data type definition appended.
 * &nbsp;&nbsp;&nbsp;default: UNKNOWN
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringToField
  extends AbstractConversionFromString {

  /** for serialization. */
  private static final long serialVersionUID = 8828591710515484463L;

  /** the default data type, for overriding UNKNOWN. */
  protected DataType m_DefaultDataType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Turns a String into a Field object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "default-data-type", "defaultDataType",
	    DataType.UNKNOWN);
  }

  /**
   * Sets the default data type.
   *
   * @param value	the data type
   */
  public void setDefaultDataType(DataType value) {
    m_DefaultDataType = value;
    reset();
  }

  /**
   * Returns the default data type.
   *
   * @return 		the data type
   */
  public DataType getDefaultDataType() {
    return m_DefaultDataType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String defaultDataTypeTipText() {
    return
        "The default data type to use for overrding " + DataType.UNKNOWN
      + " if the string does not have a data type definition appended.";
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  public Class generates() {
    return Field.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    AbstractField	result;

    result = Field.parseField((String) m_Input);

    if ((m_DefaultDataType != DataType.UNKNOWN) && (result.getDataType() == DataType.UNKNOWN))
      result = new Field(result.getName(), m_DefaultDataType);

    return result;
  }
}
