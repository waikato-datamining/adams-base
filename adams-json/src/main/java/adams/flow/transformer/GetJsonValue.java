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
 * GetJsonValue.java
 * Copyright (C) 2013-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.JsonDataType;
import adams.core.QuickInfoHelper;
import adams.core.base.JsonPathExpression;
import adams.data.json.JsonHelper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Obtains the value associated with the specified key from the JSON object passing through.<br>
 * Null values are only forwarded if requested.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;net.minidev.json.JSONAware<br>
 * &nbsp;&nbsp;&nbsp;net.minidev.json.JSONObject<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;net.minidev.json.JSONObject<br>
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
 * &nbsp;&nbsp;&nbsp;default: GetJsonValue
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
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the JSON values as array or one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-path &lt;adams.core.base.JsonPathExpression&gt; (property: path)
 * &nbsp;&nbsp;&nbsp;The path (or key if not starting with '$') of the value(s) to retrieve.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;github.com&#47;json-path&#47;JsonPath
 * </pre>
 * 
 * <pre>-data-type &lt;BOOLEAN|NUMBER|STRING|OBJECT|ARRAY&gt; (property: dataType)
 * &nbsp;&nbsp;&nbsp;The type of the data that is output.
 * &nbsp;&nbsp;&nbsp;default: OBJECT
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
 */
public class GetJsonValue
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -8757919765508522198L;
  
  /** the path of the value(s) to obtain. */
  protected JsonPathExpression m_Path;
  
  /** the output data type. */
  protected JsonDataType m_DataType;
  
  /** whether to forward null values. */
  protected boolean m_ForwardNull;

  /** the compiled path. */
  protected transient JsonPath m_ActualPath;

  /** whether path has been compiled. */
  protected transient boolean m_PathCompiled;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Obtains the value associated with the specified key from the JSON "
	+ "object passing through.\n"
	+ "Null values are only forwarded if requested.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "path", "path",
	    new JsonPathExpression());

    m_OptionManager.add(
	    "data-type", "dataType",
	    JsonDataType.OBJECT);

    m_OptionManager.add(
	    "forward-null", "forwardNull",
	    false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualPath   = null;
    m_PathCompiled = false;
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    switch (m_DataType) {
      case BOOLEAN:
	return Boolean.class;
      case NUMBER:
	return Double.class;
      case STRING:
	return String.class;
      case OBJECT:
	return JSONObject.class;
      case ARRAY:
	return JSONArray.class;
      default:
	throw new IllegalStateException("Unhandled JSON data type: " + m_DataType);
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
    return "Whether to output the JSON values as array or one-by-one.";
  }

  /**
   * Sets the path (or simple key if not starting with $) of the value(s).
   *
   * @param value 	the path or key
   */
  public void setPath(JsonPathExpression value) {
    m_Path = value;
    reset();
  }

  /**
   * Returns the path (or simple key if not starting with $) of the value(s).
   *
   * @return 		the path or key
   */
  public JsonPathExpression getPath() {
    return m_Path;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pathTipText() {
    return "The path (or key if not starting with '$') of the value(s) to retrieve.";
  }

  /**
   * Sets the type of the value(s).
   *
   * @param value 	the type
   */
  public void setDataType(JsonDataType value) {
    m_DataType = value;
    reset();
  }

  /**
   * Returns the type of the value(s).
   *
   * @return 		the type
   */
  public JsonDataType getDataType() {
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
    String		result;
    List<String>	options;

    result  = QuickInfoHelper.toString(this, "path", m_Path, "path: ");
    if (result != null)
      result += ", ";
    else
      result = "";
    result += QuickInfoHelper.toString(this, "dataType", m_DataType, "data-type: ");
    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "outputArray", m_OutputArray, "output array"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "forwardNull", m_ForwardNull, "forward null"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{JSONAware.class, JSONObject.class, String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Object	obj;
    JSONAware	json;
    Object	val;

    result = null;
    
    json = null;
    if (m_InputToken.hasPayload(String.class)) {
      obj = JsonHelper.parse(m_InputToken.getPayload(String.class), this);
      if (obj == null)
        result = "Failed to parse JSON string: " + m_InputToken.getPayload(String.class);
    }
    else {
      obj = m_InputToken.getPayload();
    }
    if (!(obj instanceof JSONAware))
      result = "Input is not of type " + JSONAware.class.getName() + "!";
    else
      json = (JSONAware) obj;
    
    if (result == null) {
      if (!m_PathCompiled) {
        m_ActualPath = m_Path.toJsonPath();
        m_PathCompiled = true;
      }
      if (m_Path.isSimpleKey() || (m_ActualPath == null)) {
        if (!(obj instanceof JSONObject))
          result = "Input is not of type " + JSONObject.class.getName() + "!";
      }
    }

    if (result == null) {
      if (m_Path.isSimpleKey() || (m_ActualPath == null)) {
	if (((JSONObject) json).containsKey(m_Path.getValue())) {
	  val = ((JSONObject) json).get(m_Path.getValue());
	  if (isLoggingEnabled())
	    getLogger().info("Found value for '" + m_Path.getValue() + "': " + val);
	  if (val instanceof Number)
	    val = ((Number) val).doubleValue();
	  if ((val != null) || m_ForwardNull)
	    m_Queue.add(val);
	}
	else {
	  if (isLoggingEnabled())
	    getLogger().info("No value found for '" + m_Path.getValue() + "'!");
	}
      }
      else {
	val  = m_ActualPath.read(json);
	if (val instanceof JSONArray)
	  m_Queue.add(val);
	else if (val instanceof List)
	  m_Queue.addAll((List) val);
	else
	  m_Queue.add(val);
      }
    }
    
    return result;
  }
}
