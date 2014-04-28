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

/**
 * IDGeneratorPostProcessor.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.id;

import adams.core.Utils;
import adams.core.Placeholders;

/**
 <!-- globalinfo-start -->
 * A simple ID generator that can make use of data provided by classes implementing IDHandler and DatabaseIDHandler.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-generator &lt;adams.data.id.AbstractIDGenerator [options]&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to use for generating the IDs.
 * &nbsp;&nbsp;&nbsp;default: adams.data.id.SimpleIDGenerator
 * </pre>
 *
 * <pre>-find &lt;java.lang.String&gt; (property: find)
 * &nbsp;&nbsp;&nbsp;The string to find (a regular expression).
 * &nbsp;&nbsp;&nbsp;default: find
 * </pre>
 *
 * <pre>-replace &lt;java.lang.String&gt; (property: replace)
 * &nbsp;&nbsp;&nbsp;The string to replace the occurrences with.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-all (property: replaceAll)
 * &nbsp;&nbsp;&nbsp;If set to true, then all occurrences will be replaced; otherwise only the
 * &nbsp;&nbsp;&nbsp;first.
 * </pre>
 *
 * <pre>-placeholder (property: replaceContainsPlaceholder)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic placeholder expansion for the replacement
 * &nbsp;&nbsp;&nbsp;string.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IDGeneratorPostProcessor
  extends AbstractIDGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 3108348760412162025L;

  /** the generator for generating the ID. */
  protected AbstractIDGenerator m_Generator;

  /** the string to find (regexp). */
  protected String m_Find;

  /** the replacement string. */
  protected String m_Replace;

  /** whether to replace all or only the first occurrence. */
  protected boolean m_ReplaceAll;

  /** whether the replace string contains a placeholder, which needs to be
   * expanded first. */
  protected boolean m_ReplaceContainsPlaceholder;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "A simple ID generator that can make use of data provided by classes "
      + "implementing IDHandler and DatabaseIDHandler.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "generator",
	    new SimpleIDGenerator());

    m_OptionManager.add(
	    "find", "find",
	    "find");

    m_OptionManager.add(
	    "replace", "replace",
	    "");

    m_OptionManager.add(
	    "all", "replaceAll",
	    false);

    m_OptionManager.add(
	    "placeholder", "replaceContainsPlaceholder",
	    false);
  }

  /**
   * Sets the generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractIDGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator in use.
   *
   * @return		the generator
   */
  public AbstractIDGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The generator to use for generating the IDs.";
  }

  /**
   * Sets the string to find (regular expression).
   *
   * @param value	the string
   */
  public void setFind(String value) {
    m_Find = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the string to find (regular expression).
   *
   * @return		the string
   */
  public String getFind() {
    return Utils.backQuoteChars(m_Find);
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
   * Sets whether all occurrences are replaced or only the first.
   *
   * @param value	true if all are to be replaced, false if only the first
   */
  public void setReplaceAll(boolean value) {
    m_ReplaceAll = value;
    reset();
  }

  /**
   * Returns whether all occurrences are replaced or only the first one.
   *
   * @return		true if all are to be replaced, false if only the first
   */
  public boolean getReplaceAll() {
    return m_ReplaceAll;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceAllTipText() {
    return "If set to true, then all occurrences will be replaced; otherwise only the first.";
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
   * Generates the actual ID.
   *
   * @param o		the object to generate the ID for
   * @return		the generated ID
   */
  protected String assemble(Object o) {
    String	result;
    String	actReplace;

    result = m_Generator.generate(o);

    if (m_ReplaceContainsPlaceholder)
      actReplace = Placeholders.getSingleton().expand(m_Replace).replace("\\", "/");
    else
      actReplace = m_Replace;

    if (m_ReplaceAll)
      result = result.replaceAll(m_Find, actReplace);
    else
      result = result.replaceFirst(m_Find, actReplace);

    return result;
  }
}
