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
 * LastLineMatches.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io.fileuse;

import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.base.BaseRegExp;
import adams.core.io.EncodingSupporter;
import adams.core.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Reads the file as text file and makes sure that the last line matches the regular expression.<br>
 * If the last line doesn't match the expression, failing to read or reading no lines at all are all interpreted as 'in use'.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when creating the string.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression the last line must match.
 * &nbsp;&nbsp;&nbsp;default: .*
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-discard-empty-lines &lt;boolean&gt; (property: discardEmptyLines)
 * &nbsp;&nbsp;&nbsp;If enabled, empty lines get discarded first before checking the last line.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-trim &lt;boolean&gt; (property: trim)
 * &nbsp;&nbsp;&nbsp;If enabled, lines get trimmed before checking for emptiness.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class LastLineMatches
  extends AbstractFileUseCheck
  implements EncodingSupporter {

  private static final long serialVersionUID = 9186395782852398722L;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** the regular expression the last line must match. */
  protected BaseRegExp m_RegExp;

  /** whether to discard empty lines. */
  protected boolean m_DiscardEmptyLines;

  /** whether to timr lines before checking emptiness. */
  protected boolean m_Trim;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads the file as text file and makes sure that the last line matches the regular expression.\n"
      + "If the last line doesn't match the expression, failing to read or reading no lines at all are all "
      + "interpreted as 'in use'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp());

    m_OptionManager.add(
      "discard-empty-lines", "discardEmptyLines",
      false);

    m_OptionManager.add(
      "trim", "trim",
      false);
  }

  /**
   * Sets the encoding to use.
   *
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }

  /**
   * Returns the encoding to use.
   *
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText() {
    return "The type of encoding to use when creating the string.";
  }

  /**
   * Sets the regular expression the last line must match.
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression the last line must match.
   *
   * @return		the expression
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
    return "The regular expression the last line must match.";
  }

  /**
   * Sets whether to discard empty lines.
   *
   * @param value	true if to discard
   */
  public void setDiscardEmptyLines(boolean value) {
    m_DiscardEmptyLines = value;
    reset();
  }

  /**
   * Returns whether to discard empty lines.
   *
   * @return		true if to discard
   */
  public boolean getDiscardEmptyLines() {
    return m_DiscardEmptyLines;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String discardEmptyLinesTipText() {
    return "If enabled, empty lines get discarded first before checking the last line.";
  }

  /**
   * Sets whether to trim the lines first before checking emptiness.
   *
   * @param value	true if to trim
   */
  public void setTrim(boolean value) {
    m_Trim = value;
    reset();
  }

  /**
   * Returns whether to trim the lines first before checking emptiness.
   *
   * @return		true if to trim
   */
  public boolean getTrim() {
    return m_Trim;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String trimTipText() {
    return "If enabled, lines get trimmed before checking for emptiness.";
  }

  /**
   * Checks whether the file is in use.
   *
   * @param file the file to check
   * @return true if in use
   */
  @Override
  public boolean isInUse(File file) {
    boolean		result;
    List<String>	lines;

    lines = FileUtils.loadFromFile(file);
    if ((lines == null) || (lines.size() == 0))
      return true;

    if (m_DiscardEmptyLines)
      Utils.removeEmptyLines(lines, m_Trim);

    result = !m_RegExp.isMatch(lines.get(lines.size() - 1));

    return result;
  }
}
