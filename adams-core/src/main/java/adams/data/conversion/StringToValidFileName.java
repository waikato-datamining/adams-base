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
 * StringToValidFileName.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.io.FileUtils;
import adams.core.io.ForwardSlashSupporter;

/**
 <!-- globalinfo-start -->
 * Turns any string into a valid file name.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-replace &lt;java.lang.String&gt; (property: replace)
 * &nbsp;&nbsp;&nbsp;The replacement string for invalid characters in strings; use empty string 
 * &nbsp;&nbsp;&nbsp;for removing the invalid characters instead of replacing them.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-use-forward-slashes &lt;boolean&gt; (property: useForwardSlashes)
 * &nbsp;&nbsp;&nbsp;If enabled, forward slashes are used in the output (but the '\\' prefix 
 * &nbsp;&nbsp;&nbsp;of UNC paths is not converted).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringToValidFileName
  extends AbstractValidateString
  implements ForwardSlashSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 5528425779551772381L;

  /** whether to output forward slashes. */
  protected boolean m_UseForwardSlashes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns any string into a valid file name.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "use-forward-slashes", "useForwardSlashes",
	    false);
  }

  /**
   * Sets whether to use forward slashes in the output.
   *
   * @param value	if true then use forward slashes
   */
  public void setUseForwardSlashes(boolean value) {
    m_UseForwardSlashes = value;
    reset();
  }

  /**
   * Returns whether to use forward slashes in the output.
   *
   * @return		true if forward slashes are used
   */
  public boolean getUseForwardSlashes() {
    return m_UseForwardSlashes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useForwardSlashesTipText() {
    return
	"If enabled, forward slashes are used in the output (but "
	+ "the '\\\\' prefix of UNC paths is not converted).";
  }

  /**
   * Performs the actual validation.
   * 
   * @param input	the input string
   * @param replace	the replacement string for invalide characters
   */
  @Override
  protected String process(String input, String replace) throws Exception {
    String	result;

    result = FileUtils.createFilename(input, replace);

    if (m_UseForwardSlashes)
      result = FileUtils.useForwardSlashes(result);

    return result;
  }
}
