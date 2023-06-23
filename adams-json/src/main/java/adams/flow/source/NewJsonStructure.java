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
 * NewJsonStructure.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 <!-- globalinfo-start -->
 * Generates an empty JSON data structure of the specified type.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;net.minidev.json.JSONObject<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: NewJsonStructure
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-data-structure &lt;OBJECT|ARRAY&gt; (property: dataStructure)
 * &nbsp;&nbsp;&nbsp;The type of data structure to create.
 * &nbsp;&nbsp;&nbsp;default: OBJECT
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class NewJsonStructure
  extends AbstractSimpleSource {

  private static final long serialVersionUID = -5652163566235489447L;

  /**
   * The data structure types for JSON.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum JsonDataStructure {
    /** a JSON object. */
    OBJECT,
    /** a JSON array. */
    ARRAY
  }

  /** the data structure to create. */
  protected JsonDataStructure m_DataStructure;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates an empty JSON data structure of the specified type.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "data-structure", "dataStructure",
      JsonDataStructure.OBJECT);
  }

  /**
   * Sets the type of data structure to create.
   *
   * @param value 	the type
   */
  public void setDataStructure(JsonDataStructure value) {
    m_DataStructure = value;
    reset();
  }

  /**
   * Returns the type of data structure to create.
   *
   * @return 		the type
   */
  public JsonDataStructure getDataStructure() {
    return m_DataStructure;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataStructureTipText() {
    return "The type of data structure to create.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "dataStructure", m_DataStructure);
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    switch (m_DataStructure) {
      case OBJECT:
	return new Class[]{JSONObject.class};
      case ARRAY:
	return new Class[]{JSONArray.class};
      default:
	throw new IllegalStateException("Unhandled JSON data structure: " + m_DataStructure);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    switch (m_DataStructure) {
      case OBJECT:
	m_OutputToken = new Token(new JSONObject());
	break;
      case ARRAY:
	m_OutputToken = new Token(new JSONArray());
	break;
      default:
	throw new IllegalStateException("Unhandled JSON data structure: " + m_DataStructure);
    }

    return null;
  }
}
