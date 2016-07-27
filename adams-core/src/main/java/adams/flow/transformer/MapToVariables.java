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
 * MapToVariables.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.Variables;
import adams.core.base.BaseRegExp;
import adams.flow.core.Token;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Turns the map content into variables.<br>
 * Only the map keys that match the regular expression are turned into variables.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Map<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Map<br>
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
 * &nbsp;&nbsp;&nbsp;default: MapToVariables
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
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression to match the map keys against.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 * <pre>-variable-prefix &lt;java.lang.String&gt; (property: variablePrefix)
 * &nbsp;&nbsp;&nbsp;The prefix to prepend the map keys with to make up the variable name.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MapToVariables
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -184602726110144511L;

  /** the regular expression that the map keys must match. */
  protected BaseRegExp m_RegExp;

  /** the prefix for the variables. */
  protected String m_VariablePrefix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Turns the map content into variables.\n"
      + "Only the map keys that match the regular expression are turned "
      + "into variables.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "variable-prefix", "variablePrefix",
      "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = QuickInfoHelper.toString(this, "regExp", m_RegExp, "regexp: ");
    value = QuickInfoHelper.toString(this, "variablePrefix", (m_VariablePrefix.length() > 0 ? m_VariablePrefix : null), ", prefix: ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Sets the regular expressions to use.
   *
   * @param value	the regular expressions
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expressions in use.
   *
   * @return 		the regular expressions
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to match the map keys against.";
  }

  /**
   * Sets the prefix for the variables (prefix + map key).
   *
   * @param value	the prefix
   */
  public void setVariablePrefix(String value) {
    m_VariablePrefix = value;
    reset();
  }

  /**
   * Returns the prefix for the variables (prefix + map key).
   *
   * @return		the prefix
   */
  public String getVariablePrefix() {
    return m_VariablePrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variablePrefixTipText() {
    return "The prefix to prepend the map keys with to make up the variable name.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.util.Map.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Map.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.util.Map.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{Map.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Map 	map;
    String	key;
    String	name;
    boolean	exists;

    result = null;

    map = null;
    if (m_InputToken.getPayload() instanceof Map)
      map = (Map) m_InputToken.getPayload();
    else
      result = "Unhandled input type: " + Utils.classToString(m_InputToken.getPayload().getClass());

    if (result == null) {
      for (Object obj : map.keySet()) {
	key = "" + obj;
	if (m_RegExp.isMatch(key)) {
	  name   = Variables.toValidName(m_VariablePrefix + key);
	  exists = getVariables().has(name);
	  getVariables().set(name, "" + map.get(key));
	  if (isLoggingEnabled())
	    getLogger().info((exists ? "Overwriting" : "Setting") + " variable '" + name + "' to '" + map.get(key) + "'");
	}
      }
    }

    m_OutputToken = new Token(m_InputToken.getPayload());

    return result;
  }
}
