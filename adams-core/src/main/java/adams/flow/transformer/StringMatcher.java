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
 * StringMatcher.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;

/**
 <!-- globalinfo-start -->
 * Lets string tokens only pass if they match the regular expression. Matching sense can be inverted as well.<br>
 * Special characters like \n \r \t and \ need to be escaped properly. The input is expected to be escaped, i.e., the string "\t" will get turned into the character '\t'.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * <pre>   java.lang.String</pre>
 * <pre>   java.lang.String[]</pre>
 * - generates:<br>
 * <pre>   java.lang.String</pre>
 * <pre>   java.lang.String[]</pre>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 *         The name of the actor.
 *         default: StringMatcher
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 *         The annotations to attach to this actor.
 *         default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 *         If set to true, transformation is skipped and the input token is just forwarded
 *          as it is.
 * </pre>
 *
 * <pre>-regexp &lt;java.lang.String&gt; (property: regExp)
 *         The regular expression used for matching the strings.
 *         default: .*
 * </pre>
 *
 * <pre>-invert (property: invert)
 *         If set to true, then the matching sense is inverted.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringMatcher
  extends AbstractStringOperation {

  /** for serialization. */
  private static final long serialVersionUID = 9030574317512531337L;

  /** the regular expression to match. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching sense. */
  protected boolean m_Invert;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Lets string tokens only pass if they match the regular expression. "
      + "Matching sense can be inverted as well.\n"
      + "Special characters like \\n \\r \\t and \\ need to be escaped "
      + "properly. The input is expected to be escaped, i.e., the string "
      + "\"\\t\" will get turned into the character '\\t'.";
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
	    "invert", "invert",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	value;
    
    value = QuickInfoHelper.toString(this, "invert", m_Invert, "! ");
    
    return QuickInfoHelper.toString(this, "regExp", m_RegExp, value);
  }

  /**
   * Sets the regular expression to match the strings against.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the strings against.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression used for matching the strings.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	true if inverting matching sense
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if matching sense is inverted
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If set to true, then the matching sense is inverted.";
  }

  /**
   * Processes the string. If null is returned, this output will be ignored.
   *
   * @param s		the string to process
   * @return		the processed string or null if nothing produced
   */
  @Override
  protected String process(String s) {
    boolean	pass;

    if (m_Invert)
      pass = !m_RegExp.isMatch(s);
    else
      pass = m_RegExp.isMatch(s);

    if (isLoggingEnabled())
      getLogger().info("'" + s + "' " + (m_Invert ? "doesn't match" : "matches") +  " '" + m_RegExp + "': " + pass);

    if (pass)
      return s;
    else
      return null;
  }
}
