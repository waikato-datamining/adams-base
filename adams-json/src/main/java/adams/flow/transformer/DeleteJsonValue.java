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
 * DeleteJsonValue.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.JsonPathExpression;
import adams.flow.core.Token;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONObject;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DeleteJsonValue
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -8757919765508522198L;

  /** the path of the value(s) to obtain. */
  protected JsonPathExpression m_Path;

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
      "Deletes the value associated with the specified key from the JSON object passing through.";
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
    return "The path (or key if not starting with '$') of the value(s) to retrieve.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "path", m_Path, "path: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{JSONObject.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{JSONObject.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    JSONObject	json;

    result = null;

    json = m_InputToken.getPayload(JSONObject.class);

    if (!m_PathCompiled) {
      m_ActualPath   = m_Path.toJsonPath();
      m_PathCompiled = true;
    }

    if (m_Path.isSimpleKey() || (m_ActualPath == null)) {
      if (json.containsKey(m_Path.getValue())) {
        json.remove(m_Path.getValue());
      }
      else {
        if (isLoggingEnabled())
          getLogger().info("No value found for '" + m_Path.getValue() + "'!");
      }
    }
    else {
      m_ActualPath.delete(json, Configuration.builder().build());
    }

    m_OutputToken = new Token(json);

    return result;
  }
}
