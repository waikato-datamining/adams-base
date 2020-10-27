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
 * ArrayToReport.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.Utils;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

import java.lang.reflect.Array;

/**
 <!-- globalinfo-start -->
 * Turns an array into a report.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use in front of the index.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-suffix &lt;java.lang.String&gt; (property: suffix)
 * &nbsp;&nbsp;&nbsp;The suffix to use after the index.
 * &nbsp;&nbsp;&nbsp;default: .value
 * </pre>
 *
 * <pre>-start-index &lt;int&gt; (property: startIndex)
 * &nbsp;&nbsp;&nbsp;The start index to use.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-data-type &lt;STRING|NUMERIC|BOOLEAN|UNKNOWN&gt; (property: dataType)
 * &nbsp;&nbsp;&nbsp;The data type to use.
 * &nbsp;&nbsp;&nbsp;default: STRING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ArrayToReport
  extends AbstractConversion {

  private static final long serialVersionUID = 8892033674356714601L;

  /** the prefix to use. */
  protected String m_Prefix;

  /** the suffix to use. */
  protected String m_Suffix;

  /** the start for the index. */
  protected int m_StartIndex;

  /** the data type to use. */
  protected DataType m_DataType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns an array into a report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      "Object.");

    m_OptionManager.add(
      "suffix", "suffix",
      ".value");

    m_OptionManager.add(
      "start-index", "startIndex",
      1);

    m_OptionManager.add(
      "data-type", "dataType",
      DataType.STRING);
  }

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix to use in front of the index.";
  }

  /**
   * Sets the field suffix used in the report.
   *
   * @param value 	the field suffix
   */
  public void setSuffix(String value) {
    m_Suffix = value;
    reset();
  }

  /**
   * Returns the field suffix used in the report.
   *
   * @return 		the field suffix
   */
  public String getSuffix() {
    return m_Suffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suffixTipText() {
    return "The suffix to use after the index.";
  }

  /**
   * Sets the start index.
   *
   * @param value 	the start index
   */
  public void setStartIndex(int value) {
    m_StartIndex = value;
    reset();
  }

  /**
   * Returns the start index.
   *
   * @return 		the start index
   */
  public int getStartIndex() {
    return m_StartIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startIndexTipText() {
    return "The start index to use.";
  }

  /**
   * Sets the data type to use.
   *
   * @param value 	the data type
   */
  public void setDataType(DataType value) {
    m_DataType = value;
    reset();
  }

  /**
   * Returns the data type in use.
   *
   * @return 		the data type
   */
  public DataType getDataType() {
    return m_DataType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataTypeTipText() {
    return "The data type to use.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Object[].class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Report.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Report	result;
    int		i;
    int		len;
    Field	field;

    if (!m_Input.getClass().isArray())
      throw new IllegalArgumentException("Object is not an array: " + Utils.classToString(m_Input));

    result = new Report();
    len = Array.getLength(m_Input);
    for (i = 0; i < len; i++) {
      field = new Field(m_Prefix + (m_StartIndex + i) + m_Suffix, m_DataType);
      result.addField(field);
      result.setValue(field, Array.get(m_Input, i));
    }

    return result;
  }
}
