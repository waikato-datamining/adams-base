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
 * AttachOverlappingMetaData.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfilter;

import adams.core.QuickInfoHelper;
import adams.data.report.DataType;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 <!-- globalinfo-start -->
 * Attaches the specified meta-data value.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-key &lt;java.lang.String&gt; (property: key)
 * &nbsp;&nbsp;&nbsp;The key of the meta-data value to add.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-data-type &lt;STRING|NUMERIC|BOOLEAN|UNKNOWN&gt; (property: dataType)
 * &nbsp;&nbsp;&nbsp;The data type to use for the meta-data.
 * &nbsp;&nbsp;&nbsp;default: STRING
 * </pre>
 *
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The meta-data value to add.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AttachMetaData
  extends AbstractObjectFilter {

  private static final long serialVersionUID = 5647107073729835067L;

  /** the key name. */
  protected String m_Key;

  /** the data type. */
  protected DataType m_DataType;

  /** the value (gets parsed according to data type). */
  protected String m_Value;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Attaches the specified meta-data value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key", "key",
      "");

    m_OptionManager.add(
      "data-type", "dataType",
      DataType.STRING);

    m_OptionManager.add(
      "value", "value",
      "");
  }

  /**
   * Sets the key of the meta-data value to add.
   *
   * @param value	the key
   */
  public void setKey(String value) {
    m_Key = value;
    reset();
  }

  /**
   * Returns the key of the meta-data value to add.
   *
   * @return		the name
   */
  public String getKey() {
    return m_Key;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyTipText() {
    return "The key of the meta-data value to add.";
  }

  /**
   * Sets the data type of the value.
   *
   * @param value 	the type
   */
  public void setDataType(DataType value) {
    m_DataType = value;
    reset();
  }

  /**
   * Returns the data type of the value.
   *
   * @return 		the type
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
    return "The data type to use for the meta-data.";
  }

  /**
   * Sets the meta-data value to add.
   *
   * @param value 	the value
   */
  public void setValue(String value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the meta-data value to add.
   *
   * @return 		the value
   */
  public String getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The meta-data value to add.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "key", m_Key, "key: ");
    result += QuickInfoHelper.toString(this, "dataType", m_DataType, ", type: ");
    result += QuickInfoHelper.toString(this, "value", m_Value, ", value: ");

    return result;
  }

  /**
   * Filters the image objects.
   *
   * @param objects	the objects to filter
   * @return		the updated object list
   */
  @Override
  protected LocatedObjects doFilter(LocatedObjects objects) {
    LocatedObjects	result;
    Object  		val;

    result = new LocatedObjects();
    switch (m_DataType) {
      case BOOLEAN:
        val = Boolean.parseBoolean(m_Value);
        break;
      case NUMERIC:
        val = Double.parseDouble(m_Value);
        break;
      default:
        val = m_Value;
    }

    for (LocatedObject object: objects) {
      object = object.getClone();
      object.getMetaData().put(m_Key, val);
      result.add(object);
    }

    return result;
  }
}
