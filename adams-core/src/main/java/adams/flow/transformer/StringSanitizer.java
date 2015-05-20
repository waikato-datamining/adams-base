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
 * StringSanitizer.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;


/**
 <!-- globalinfo-start -->
 * Removes all characters that haven't been specified as 'acceptable' characters. The matching sense can also be inverted, i.e., 'acceptable' characters become 'outlawed' ones.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
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
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: StringSanitizer
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-acceptable &lt;java.lang.String&gt; (property: acceptableChars)
 * &nbsp;&nbsp;&nbsp;The allowed characters, all others will get removed (or replaced).
 * &nbsp;&nbsp;&nbsp;default: abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-
 * </pre>
 *
 * <pre>-replace &lt;java.lang.String&gt; (property: replacementChar)
 * &nbsp;&nbsp;&nbsp;The character to replace the 'unacceptable' characters with; use the empty
 * &nbsp;&nbsp;&nbsp;string if you want to remove 'unacceptable' characters.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-V (property: invertMatching)
 * &nbsp;&nbsp;&nbsp;If set to true, then 'acceptable' characters will become 'outlaws', ie,
 * &nbsp;&nbsp;&nbsp;replaced or removed.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringSanitizer
  extends AbstractStringOperation {

  /** for serialization. */
  private static final long serialVersionUID = 1359563281659205366L;

  /** the characters that are allowed. */
  protected String m_AcceptableChars;

  /** the character to replace the unacceptable ones. */
  protected String m_ReplacementChar;

  /** whether to invert the matching. */
  protected boolean m_InvertMatching;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Removes all characters that haven't been specified as 'acceptable' "
      + "characters. The matching sense can also be inverted, i.e., "
      + "'acceptable' characters become 'outlawed' ones.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "acceptable", "acceptableChars",
	    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-");

    m_OptionManager.add(
	    "replace", "replacementChar",
	    "");

    m_OptionManager.add(
	    "V", "invertMatching",
	    false);
  }

  /**
   * Sets the 'acceptable' characters.
   *
   * @param value	the characters
   */
  public void setAcceptableChars(String value) {
    m_AcceptableChars = value;
    reset();
  }

  /**
   * Returns the 'acceptable' characters.
   *
   * @return		the characters
   */
  public String getAcceptableChars() {
    return m_AcceptableChars;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String acceptableCharsTipText() {
    return "The allowed characters, all others will get removed (or replaced).";
  }

  /**
   * Sets the character to replace the unacceptable characters with. If
   * empty, then characters will be just removed.
   *
   * @param value	the character
   */
  public void setReplacementChar(String value) {
    if (value.length() <= 1) {
      m_ReplacementChar = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Either empty string or a single character are allowed, provided: " + value);
    }
  }

  /**
   * Returns the character to replace the unacceptable characters with. If
   * empty, then characters will be just removed.
   *
   * @return		the character
   */
  public String getReplacementChar() {
    return m_ReplacementChar;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replacementCharTipText() {
    return
        "The character to replace the 'unacceptable' characters with; "
      + "use the empty string if you want to remove 'unacceptable' characters.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	if true then 'acceptable' characters are treated as
   * 			'outlaws'
   */
  public void setInvertMatching(boolean value) {
    m_InvertMatching = value;
    reset();
  }

  /**
   * Returns whether all occurrences are replaced or only the first one.
   *
   * @return		true if all are to be replaced, false if only the first
   */
  public boolean getInvertMatching() {
    return m_InvertMatching;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertMatchingTipText() {
    return "If set to true, then 'acceptable' characters will become 'outlaws', ie, replaced or removed.";
  }

  /**
   * Processes the string.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  @Override
  protected String process(String s) {
    StringBuilder	result;
    char		ch;
    int			i;

    result = new StringBuilder();
    for (i = 0; i < s.length(); i++) {
      ch = s.charAt(i);

      if (m_InvertMatching) {
	if (m_AcceptableChars.indexOf(ch) > -1)
	  result.append(m_ReplacementChar);
	else
	  result.append(ch);
      }
      else {
	if (m_AcceptableChars.indexOf(ch) > -1)
	  result.append(ch);
	else
	  result.append(m_ReplacementChar);
      }
    }

    return result.toString();
  }
}
