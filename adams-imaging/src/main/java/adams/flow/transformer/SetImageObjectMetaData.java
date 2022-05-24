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
 * SetImageObjectMetaData.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.Utils;
import adams.data.report.DataType;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;

/**
 <!-- globalinfo-start -->
 * Sets meta-data in the incoming adams.flow.transformer.locateobjects.LocatedObject object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.transformer.locateobjects.LocatedObject<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.transformer.locateobjects.LocatedObject<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SetImageObjectMetaData
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-key &lt;java.lang.String&gt; (property: key)
 * &nbsp;&nbsp;&nbsp;The meta-data key to store the value under.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The meta-data value to store.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-type &lt;STRING|NUMERIC|BOOLEAN|UNKNOWN&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;How to parse the value string.
 * &nbsp;&nbsp;&nbsp;default: STRING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SetImageObjectMetaData
    extends AbstractTransformer {

  private static final long serialVersionUID = -8300475715029765628L;

  /** the key to use. */
  protected String m_Key;

  /** the value to set. */
  protected String m_Value;

  /** the type of the value. */
  protected DataType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sets meta-data in the incoming " + Utils.classToString(LocatedObject.class) + " object.";
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
        "value", "value",
        "");

    m_OptionManager.add(
        "type", "type",
        DataType.STRING);
  }

  /**
   * Sets the key.
   *
   * @param value	the key
   */
  public void setKey(String value) {
    m_Key = value;
    reset();
  }

  /**
   * Returns the key.
   *
   * @return		the key
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
    return "The meta-data key to store the value under.";
  }

  /**
   * Sets the value.
   *
   * @param value	the value
   */
  public void setValue(String value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the value.
   *
   * @return		the value
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
    return "The meta-data value to store.";
  }

  /**
   * Sets how to parse the value string.
   *
   * @param value	the type
   */
  public void setType(DataType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns how to parse the value string.
   *
   * @return		the type
   */
  public DataType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "How to parse the value string.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{LocatedObject.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{LocatedObject.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    LocatedObject	object;

    result = null;
    object = m_InputToken.getPayload(LocatedObject.class);

    try {
      switch (m_Type) {
        case BOOLEAN:
          object.getMetaData().put(m_Key, Boolean.parseBoolean(m_Value));
          break;
        case NUMERIC:
          object.getMetaData().put(m_Key, Double.parseDouble(m_Value));
          break;
        default:
          object.getMetaData().put(m_Key, m_Value);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to set meta-data value (key=" + m_Key + ", type=" + m_Type + "): " + m_Value, e);
    }

    m_OutputToken = new Token(object);

    return result;
  }
}
