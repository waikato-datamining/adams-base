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
 * SimpleStringReplace.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Performs a string replacement, using String.replace(.., ...).<br>
 *  If the 'replace' string contains both, variables and placeholders, then first all variables are expanded and then the placeholders.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: SimpleStringReplace
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
 * <pre>-find &lt;java.lang.String&gt; (property: find)
 * &nbsp;&nbsp;&nbsp;The sub-string to find.
 * &nbsp;&nbsp;&nbsp;default: find
 * </pre>
 *
 * <pre>-replace &lt;java.lang.String&gt; (property: replace)
 * &nbsp;&nbsp;&nbsp;The string to replace the occurrences with.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-placeholder &lt;boolean&gt; (property: replaceContainsPlaceholder)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic placeholder expansion for the replacement
 * &nbsp;&nbsp;&nbsp;string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-variable &lt;boolean&gt; (property: replaceContainsVariable)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic variable expansion for the replacement
 * &nbsp;&nbsp;&nbsp;string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SimpleStringReplace
  extends AbstractStringOperation {

  /** for serialization. */
  private static final long serialVersionUID = -1167336515862285272L;

  /** the string to find. */
  protected String m_Find;

  /** the replacement string. */
  protected String m_Replace;

  /** whether the replace string contains a placeholder, which needs to be
   * expanded first. */
  protected boolean m_ReplaceContainsPlaceholder;

  /** whether the replace string contains a variable, which needs to be
   * expanded first. */
  protected boolean m_ReplaceContainsVariable;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Performs a string replacement, using String.replace(.., ...).\n "
      + "If the 'replace' string contains both, variables and placeholders, "
      + "then first all variables are expanded and then the placeholders.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "find", "find",
	    "find");

    m_OptionManager.add(
	    "replace", "replace",
	    "");

    m_OptionManager.add(
	    "placeholder", "replaceContainsPlaceholder",
	    false);

    m_OptionManager.add(
	    "variable", "replaceContainsVariable",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    String		replace;
    String		find;
    List<String>	options;

    replace = QuickInfoHelper.toString(this, "replace", m_Replace);
    if (replace == null)
      replace = "";
    find    = QuickInfoHelper.toString(this, "find", m_Find);
    if (find == null)
      find = "";

    if (QuickInfoHelper.hasVariable(this, "replace") || (replace.length() > 0))
      result = "replace ";
    else
      result = "remove ";
    result += "'" + find + "'";

    if (QuickInfoHelper.hasVariable(this, "replace") || (replace.length() > 0))
      result += " with '" + replace + "'";

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "replaceContainsPlaceholder", m_ReplaceContainsPlaceholder, "PH"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "replaceContainsVariable", m_ReplaceContainsVariable, "Var"));
    result += QuickInfoHelper.flatten(options);
    
    return result;
  }

  /**
   * Sets the string to find.
   *
   * @param value	the string
   */
  public void setFind(String value) {
    m_Find = value;
    reset();
  }

  /**
   * Returns the string to find.
   *
   * @return		the string
   */
  public String getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String findTipText() {
    return "The sub-string to find.";
  }

  /**
   * Sets the string to replace the occurrences with.
   *
   * @param value	the string
   */
  public void setReplace(String value) {
    m_Replace = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the string to replace the occurences with.
   *
   * @return		the string
   */
  public String getReplace() {
    return Utils.backQuoteChars(m_Replace);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceTipText() {
    return "The string to replace the occurrences with.";
  }

  /**
   * Sets whether the replace string contains a placeholder which needs to be
   * expanded first.
   *
   * @param value	true if replace string contains a placeholder
   */
  public void setReplaceContainsPlaceholder(boolean value) {
    m_ReplaceContainsPlaceholder = value;
    reset();
  }

  /**
   * Returns whether the replace string contains a placeholder which needs to be
   * expanded first.
   *
   * @return		true if replace string contains a placeholder
   */
  public boolean getReplaceContainsPlaceholder() {
    return m_ReplaceContainsPlaceholder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceContainsPlaceholderTipText() {
    return "Set this to true to enable automatic placeholder expansion for the replacement string.";
  }

  /**
   * Sets whether the replace string contains a variable which needs to be
   * expanded first.
   *
   * @param value	true if replace string contains a variable
   */
  public void setReplaceContainsVariable(boolean value) {
    m_ReplaceContainsVariable = value;
    reset();
  }

  /**
   * Returns whether the replace string contains a variable which needs to be
   * expanded first.
   *
   * @return		true if replace string contains a variable
   */
  public boolean getReplaceContainsVariable() {
    return m_ReplaceContainsVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceContainsVariableTipText() {
    return "Set this to true to enable automatic variable expansion for the replacement string.";
  }

  /**
   * Processes the string.
   *
   * @param s		the string to process
   * @param index	the 0-based index of the string currently being processed
   * @return		the processed string or null if nothing produced
   */
  @Override
  protected String process(String s, int index) {
    String	replace;

    if (isLoggingEnabled())
      getLogger().info("search string: " + m_Find);

    // do we need to replace variables?
    replace = m_Replace;
    if (m_ReplaceContainsVariable)
      replace = getVariables().expand(replace);
    if (m_ReplaceContainsPlaceholder)
      replace = Placeholders.getSingleton().expand(replace).replace("\\", "/");

    if (isLoggingEnabled())
      getLogger().info("replacement string: " + replace);

    return s.replace(m_Find, replace);
  }
}
