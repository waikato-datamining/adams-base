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
 * GetPropertyValue.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.awt.Color;
import java.awt.Font;
import java.util.Date;
import java.util.Set;

import adams.core.DateTime;
import adams.core.Properties;
import adams.core.PropertiesDataType;
import adams.core.QuickInfoHelper;
import adams.core.Time;
import adams.core.base.BasePassword;
import adams.core.base.BaseRegExp;

/**
 <!-- globalinfo-start -->
 * Obtains the value(s) associated with the keys that match the specified regular expression from the Properties object passing through.<br>
 * Null values are only forwarded if requested.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Properties<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: GetPropertyValue
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the values as array or one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-key &lt;adams.core.base.BaseRegExp&gt; (property: key)
 * &nbsp;&nbsp;&nbsp;The regular expression to match the keys against.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 * <pre>-data-type &lt;PROPERTY|PATH|BOOLEAN|INTEGER|LONG|DOUBLE|COLOR|FONT|TIME|DATE|DATETIME|PASSWORD&gt; (property: dataType)
 * &nbsp;&nbsp;&nbsp;The type of the data that is output.
 * &nbsp;&nbsp;&nbsp;default: PROPERTY
 * </pre>
 * 
 * <pre>-forward-null &lt;boolean&gt; (property: forwardNull)
 * &nbsp;&nbsp;&nbsp;If enabled, null values are forward as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GetPropertyValue
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -8757919765508522198L;
  
  /** the regular expression for the keys to obtain the values for. */
  protected BaseRegExp m_Key;
  
  /** the output data type. */
  protected PropertiesDataType m_DataType;
  
  /** whether to forward null values. */
  protected boolean m_ForwardNull;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Obtains the value(s) associated with the keys that match the specified "
	+ "regular expression from the Properties object passing through.\n"
	+ "Null values are only forwarded if requested.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "key", "key",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "data-type", "dataType",
	    PropertiesDataType.PROPERTY);

    m_OptionManager.add(
	    "forward-null", "forwardNull",
	    false);
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    switch (m_DataType) {
      case PROPERTY:
      case PATH:
	return String.class;
      case BOOLEAN:
	return Boolean.class;
      case INTEGER:
	return Integer.class;
      case LONG:
	return Integer.class;
      case DOUBLE:
	return Double.class;
      case COLOR:
	return Color.class;
      case FONT:
	return Font.class;
      case TIME:
	return Time.class;
      case DATE:
	return Date.class;
      case DATETIME:
	return DateTime.class;
      case PASSWORD:
	return BasePassword.class;
      default:
	throw new IllegalStateException("Unhandled properties data type: " + m_DataType);
    }
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to output the values as array or one-by-one.";
  }

  /**
   * Sets the regular expression to match the keys against.
   *
   * @param value 	the expression
   */
  public void setKey(BaseRegExp value) {
    m_Key = value;
    reset();
  }

  /**
   * Returns the regular expression to match the keys against.
   *
   * @return 		the expression
   */
  public BaseRegExp getKey() {
    return m_Key;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyTipText() {
    return "The regular expression to match the keys against.";
  }

  /**
   * Sets the type of the value(s).
   *
   * @param value 	the type
   */
  public void setDataType(PropertiesDataType value) {
    m_DataType = value;
    reset();
  }

  /**
   * Returns the type of the value(s).
   *
   * @return 		the type
   */
  public PropertiesDataType getDataType() {
    return m_DataType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataTypeTipText() {
    return "The type of the data that is output.";
  }

  /**
   * Sets whether to forward null values.
   *
   * @param value 	true if to forward
   */
  public void setForwardNull(boolean value) {
    m_ForwardNull = value;
    reset();
  }

  /**
   * Returns whether to forward null values.
   *
   * @return 		true if to forward
   */
  public boolean getForwardNull() {
    return m_ForwardNull;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String forwardNullTipText() {
    return "If enabled, null values are forward as well.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "key", m_Key, "key: ");
    if (result != null)
      result += ", ";
    else
      result = "";
    result += QuickInfoHelper.toString(this, "dataType", m_DataType, "data-type: ");
    result += QuickInfoHelper.toString(this, "forwardNull", m_ForwardNull, "forward null", ", ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{java.util.Properties.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Properties	props;
    Set<String>	keys;
    Object	val;
    
    result = null;
    
    if (m_InputToken.getPayload() instanceof Properties)
      props = (Properties) m_InputToken.getPayload();
    else
      props = new Properties((java.util.Properties) m_InputToken.getPayload());

    keys = props.keySetAll(m_Key);
    if ((keys.size() == 0) && m_ForwardNull) {
      m_Queue.add(null);
    }
    else {
      for (String key: keys) {
	switch (m_DataType) {
	  case PROPERTY:
	    val = props.getProperty(key);
	    break;
	  case PATH:
	    val = props.getPath(key);
	    break;
	  case BOOLEAN:
	    val = props.getBoolean(key);
	    break;
	  case INTEGER:
	    val = props.getInteger(key);
	    break;
	  case LONG:
	    val = props.getLong(key);
	    break;
	  case DOUBLE:
	    val = props.getDouble(key);
	    break;
	  case COLOR:
	    val = props.getColor(key);
	    break;
	  case FONT:
	    val = props.getFont(key);
	    break;
	  case TIME:
	    val = props.getTime(key);
	    break;
	  case DATE:
	    val = props.getDate(key);
	    break;
	  case DATETIME:
	    val = props.getDateTime(key);
	    break;
	  case PASSWORD:
	    val = props.getPassword(key);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled properties data type: " + m_DataType);
	}
	if ((val != null) || m_ForwardNull)
	  m_Queue.add(val);
      }
    }
    
    return result;
  }
}
