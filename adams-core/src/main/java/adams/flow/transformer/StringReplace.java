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
 * StringReplace.java
 * Copyright (C) 2009-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.net.HtmlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 <!-- globalinfo-start -->
 * Performs a string replacement, using either String.replaceFirst(...) or String.replaceAll(...). Special characters like \n \r \t and \ need to be escaped properly. The input is expected to be escaped, i.e., the string "\t" will get turned into the character '\t'.<br>
 * If the 'replace' string contains both, variables and placeholders, then first all variables are expanded and then the placeholders. This ensures that variables containing placeholders expand their placeholders as well. Not expanding placeholders will cause 'Illegal group reference' error messages.<br>
 * By enabling 'useDotAll', matching across lines is possible as well.<br>
 * If no regular expression matching is required, you can also use the simple replacing, which uses String.replace(...).
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: StringReplace
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
 * <pre>-find &lt;adams.core.base.BaseRegExp&gt; (property: find)
 * &nbsp;&nbsp;&nbsp;The string to find (a regular expression).
 * &nbsp;&nbsp;&nbsp;default: find
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;en&#47;java&#47;javase&#47;11&#47;docs&#47;api&#47;java.base&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-replace &lt;java.lang.String&gt; (property: replace)
 * &nbsp;&nbsp;&nbsp;The string to replace the occurrences with.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-type &lt;FIRST|ALL|SIMPLE&gt; (property: replaceType)
 * &nbsp;&nbsp;&nbsp;Defines whether to use regular expression replacement (first&#47;all) or simple
 * &nbsp;&nbsp;&nbsp;string sequence replacement.
 * &nbsp;&nbsp;&nbsp;default: FIRST
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
 * <pre>-use-dot-all &lt;boolean&gt; (property: useDotAll)
 * &nbsp;&nbsp;&nbsp;If enabled, new-lines are treated as regular characters, enabling matching
 * &nbsp;&nbsp;&nbsp;across multiple lines, see: https:&#47;&#47;docs.oracle.com&#47;en&#47;java&#47;javase&#47;11&#47;docs
 * &nbsp;&nbsp;&nbsp;&#47;api&#47;java.base&#47;java&#47;util&#47;regex&#47;Pattern.html#DOTALL
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class StringReplace
  extends AbstractStringOperation {

  /** for serialization. */
  private static final long serialVersionUID = -1167336515862285272L;

  /**
   * The type of replace to use.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ReplaceType {
    /** String.replaceFirst(...). */
    FIRST,
    /** String.replaceAll(...). */
    ALL,
    /** String.replace(...). */
    SIMPLE
  }

  /** the string to find. */
  protected BaseRegExp m_Find;

  /** the replacement string. */
  protected String m_Replace;

  /** how to replace the occurrences. */
  protected ReplaceType m_ReplaceType;

  /** whether the replace string contains a placeholder, which needs to be
   * expanded first. */
  protected boolean m_ReplaceContainsPlaceholder;

  /** whether the replace string contains a variable, which needs to be
   * expanded first. */
  protected boolean m_ReplaceContainsVariable;

  /** whether to use dot all. */
  protected boolean m_UseDotAll;

  /** the pattern. */
  protected transient Pattern m_Pattern;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs a string replacement, using either String.replaceFirst(...) "
	+ "or String.replaceAll(...). Special characters like \\n \\r \\t and \\ "
	+ "need to be escaped properly. The input is expected to be escaped, "
	+ "i.e., the string \"\\t\" will get turned into the character '\\t'.\n"
	+ "If the 'replace' string contains both, variables and placeholders, "
	+ "then first all variables are expanded and then the placeholders. This "
	+ "ensures that variables containing placeholders expand their placeholders "
	+ "as well. Not expanding placeholders will cause 'Illegal group reference' "
	+ "error messages.\n"
	+ "By enabling 'useDotAll', matching across lines is possible as well.\n"
	+ "If no regular expression matching is required, you can also use the "
	+ "simple replacing, which uses String.replace(...).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "find", "find",
      new BaseRegExp("find"));

    m_OptionManager.add(
      "replace", "replace",
      "");

    m_OptionManager.add(
      "type", "replaceType",
      ReplaceType.FIRST);

    m_OptionManager.add(
      "placeholder", "replaceContainsPlaceholder",
      false);

    m_OptionManager.add(
      "variable", "replaceContainsVariable",
      false);

    m_OptionManager.add(
      "use-dot-all", "useDotAll",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Pattern = null;
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
    if (m_ReplaceType != ReplaceType.FIRST)
      result += "all ";
    result += "'" + find + "'";

    if (QuickInfoHelper.hasVariable(this, "replace") || (replace.length() > 0))
      result += " with '" + replace + "'";

    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "replaceContainsPlaceholder", m_ReplaceContainsPlaceholder, "PH"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "replaceContainsVariable", m_ReplaceContainsVariable, "Var"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "useDotAll", m_UseDotAll, "dot-all"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the string to find (regular expression).
   *
   * @param value	the string
   */
  public void setFind(BaseRegExp value) {
    m_Find = value;
    reset();
  }

  /**
   * Returns the string to find (regular expression).
   *
   * @return		the string
   */
  public BaseRegExp getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String findTipText() {
    return "The string to find (a regular expression).";
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
   * Sets how to replace the strings.
   *
   * @param value	the type
   */
  public void setReplaceType(ReplaceType value) {
    m_ReplaceType = value;
    reset();
  }

  /**
   * Returns the type of replacement.
   *
   * @return		the type
   */
  public ReplaceType getReplaceType() {
    return m_ReplaceType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceTypeTipText() {
    return
      "Defines whether to use regular expression replacement (first/all) "
	+ "or simple string sequence replacement.";
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
   * Sets whether to enabled matching across multiple lines.
   *
   * @param value	true if to use dot all
   */
  public void setUseDotAll(boolean value) {
    m_UseDotAll = value;
    reset();
  }

  /**
   * Returns whether to enabled matching across multiple lines.
   *
   * @return		true if to use dot all
   */
  public boolean getUseDotAll() {
    return m_UseDotAll;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useDotAllTipText() {
    return "If enabled, new-lines are treated as regular characters, enabling matching "
	     + "across multiple lines, see: " + HtmlUtils.toJavaApiURL(Pattern.class) + "#DOTALL";
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
      getLogger().info("pattern: " + m_Find);

    if (m_UseDotAll && (m_Pattern == null))
      m_Pattern = Pattern.compile(m_Find.getValue(), Pattern.DOTALL);

    // do we need to replace variables?
    replace = m_Replace;
    if (m_ReplaceContainsVariable)
      replace = getVariables().expand(replace);
    if (m_ReplaceContainsPlaceholder)
      replace = Placeholders.getSingleton().expand(replace);

    if (isLoggingEnabled())
      getLogger().info("replacement string: " + replace);

    switch (m_ReplaceType) {
      case FIRST:
	if (m_Pattern != null)
	  return m_Pattern.matcher(s).replaceFirst(replace);
	else
	  return s.replaceFirst(m_Find.getValue(), replace);
      case ALL:
	if (m_Pattern != null)
	  return m_Pattern.matcher(s).replaceAll(replace);
	else
	  return s.replaceAll(m_Find.getValue(), replace);
      case SIMPLE:
	return s.replace(m_Find.getValue(), replace);
      default:
	throw new IllegalStateException("Unhandled replace type: " + m_ReplaceType);
    }
  }
}
