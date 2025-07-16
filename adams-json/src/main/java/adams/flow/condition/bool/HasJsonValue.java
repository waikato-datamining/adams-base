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
 * HasJsonValue.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.JsonPathExpression;
import adams.data.json.JsonHelper;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

/**
 <!-- globalinfo-start -->
 * Checks whether the specified key is present in the JSON object.<br>
 * Handles tokens that contain either a JSON string or net.minidev.json.JSONAware objects.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-path &lt;adams.core.base.JsonPathExpression&gt; (property: path)
 * &nbsp;&nbsp;&nbsp;The path (or key if not starting with '$') of the value to look for.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;github.com&#47;json-path&#47;JsonPath
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class HasJsonValue
  extends AbstractBooleanCondition {

  private static final long serialVersionUID = 7492120193675649750L;

  /** the path of the value(s) to obtain. */
  protected JsonPathExpression m_Path;

  /** the compiled path. */
  protected transient JsonPath m_ActualPath;

  /** whether path has been compiled. */
  protected transient boolean m_PathCompiled;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the specified key is present in the JSON object.\n"
	     + "Handles tokens that contain either a JSON string or " + Utils.classToString(JSONAware.class) + " objects.";
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
    return "The path (or key if not starting with '$') of the value to look for.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "path", m_Path, "path: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return adams.flow.core.Unknown.class
   */
  @Override
  public Class[] accepts() {
    return new Class[0];
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner the owning actor
   * @param token the current token passing through
   * @return the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    boolean	result;
    Object	obj;
    JSONAware 	json;

    result = false;

    if (token.hasPayload(String.class)) {
      obj = JsonHelper.parse(token.getPayload(String.class), this);
      if (obj == null)
	throw new IllegalStateException("Failed to parse JSON string: " + token.getPayload(String.class));
    }
    else {
      obj = token.getPayload();
    }
    if (!(obj instanceof JSONAware))
      throw new IllegalStateException("Input is not of type " + Utils.classToString(JSONAware.class) + "!");
    else
      json = (JSONAware) obj;

    if (!m_PathCompiled) {
      m_ActualPath = m_Path.toJsonPath();
      m_PathCompiled = true;
    }
    if (m_Path.isSimpleKey() || (m_ActualPath == null)) {
      if (!(obj instanceof JSONObject))
	throw new IllegalStateException("Input is not of type " + JSONObject.class.getName() + "!");
    }

    if (m_Path.isSimpleKey() || (m_ActualPath == null)) {
      result = ((JSONObject) json).containsKey(m_Path.getValue());
    }
    else {
      try {
	m_ActualPath.read(json);
	result = true;
      }
      catch (Exception e) {
	// ignored
      }
    }

    return result;
  }
}
