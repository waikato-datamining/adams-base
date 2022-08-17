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
 * MapVariableIterator.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.VariableName;
import adams.core.base.BaseRegExp;
import adams.flow.core.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Iterates over the keys of the incoming map and sets variables for current key and variable. The incoming map is forwarded each time.
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
 * &nbsp;&nbsp;&nbsp;default: MapIterator
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
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-skip-non-primitive &lt;boolean&gt; (property: skipNonPrimitive)
 * &nbsp;&nbsp;&nbsp;If enabled, all values get skipped that are not primitive objects.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-var-key &lt;adams.core.VariableName&gt; (property: varKey)
 * &nbsp;&nbsp;&nbsp;The variable to store the map key in.
 * &nbsp;&nbsp;&nbsp;default: key
 * </pre>
 *
 * <pre>-var-value &lt;adams.core.VariableName&gt; (property: varValue)
 * &nbsp;&nbsp;&nbsp;The variable to store the map value in.
 * &nbsp;&nbsp;&nbsp;default: value
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MapVariableIterator
    extends AbstractTransformer {

  private static final long serialVersionUID = -6262313011503736212L;

  /** the regular expression that the map keys must match. */
  protected BaseRegExp m_RegExp;

  /** whether to skip non-primitive values. */
  protected boolean m_SkipNonPrimitive;

  /** the variable for the keys. */
  protected VariableName m_VarKey;

  /** the variable for the values. */
  protected VariableName m_VarValue;

  /** the current keys to broadcast. */
  protected transient List<Object> m_CurrentKeys;

  /** the current map. */
  protected transient Map m_CurrentMap;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Iterates over the keys of the incoming map and sets variables for current key and variable. "
	+ "The incoming map is forwarded each time.";
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
	"skip-non-primitive", "skipNonPrimitive",
	false);

    m_OptionManager.add(
	"var-key", "varKey",
	new VariableName("key"));

    m_OptionManager.add(
	"var-value", "varValue",
	new VariableName("value"));
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CurrentKeys = null;
    m_CurrentMap  = null;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Map.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Map.class};
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
    result += QuickInfoHelper.toString(this, "skipNonPrimitive", m_SkipNonPrimitive, "skip non-primitives");
    result += QuickInfoHelper.toString(this, "varKey", m_VarKey, ", key: ");
    result += QuickInfoHelper.toString(this, "varValue", m_VarValue, ", value: ");

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
   * Sets whether to skip non-primitive values.
   *
   * @param value	true if to skip
   */
  public void setSkipNonPrimitive(boolean value) {
    m_SkipNonPrimitive = value;
    reset();
  }

  /**
   * Returns whether to skip non-primitive values.
   *
   * @return		true if to skip
   */
  public boolean getSkipNonPrimitive() {
    return m_SkipNonPrimitive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipNonPrimitiveTipText() {
    return "If enabled, all values get skipped that are not primitive objects.";
  }

  /**
   * Sets the variable to store the key in.
   *
   * @param value	the variable
   */
  public void setVarKey(VariableName value) {
    m_VarKey = value;
    reset();
  }

  /**
   * Returns the variable to store the key in.
   *
   * @return		the variable
   */
  public VariableName getVarKey() {
    return m_VarKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String varKeyTipText() {
    return "The variable to store the map key in.";
  }

  /**
   * Sets the variable to store the value in.
   *
   * @param value	the variable
   */
  public void setVarValue(VariableName value) {
    m_VarValue = value;
    reset();
  }

  /**
   * Returns the variable to store the value in.
   *
   * @return		the variable
   */
  public VariableName getVarValue() {
    return m_VarValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String varValueTipText() {
    return "The variable to store the map value in.";
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Map		map;
    String	key;

    result = null;
    map    = m_InputToken.getPayload(Map.class);

    m_CurrentMap  = map;
    m_CurrentKeys = new ArrayList<>();
    for (Object objkey : map.keySet()) {
      if (!m_RegExp.isMatchAll()) {
        key = "" + objkey;
        if (!m_RegExp.isMatch(key))
          continue;
      }
      if (m_SkipNonPrimitive && !Utils.isPrimitive(map.get(objkey)))
	continue;
      m_CurrentKeys.add(objkey);
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_CurrentMap != null) && (m_CurrentKeys != null) && (m_CurrentKeys.size() > 0);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;
    String	key;
    String	value;

    result = null;

    if ((m_CurrentMap != null) && (m_CurrentKeys.size() > 0)) {
      key    = "" + m_CurrentKeys.remove(0);
      value  = "" + m_CurrentMap.get(key);
      result = new Token(m_CurrentMap);
      getVariables().set(m_VarKey.getValue(),   key);
      getVariables().set(m_VarValue.getValue(), value);
      // finished?
      if (m_CurrentKeys.size() == 0) {
	m_CurrentKeys = null;
	m_CurrentMap  = null;
      }
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_CurrentKeys = null;
    m_CurrentMap  = null;

    super.wrapUp();
  }
}
