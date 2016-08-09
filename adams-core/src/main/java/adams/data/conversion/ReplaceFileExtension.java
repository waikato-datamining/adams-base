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
 * ReplaceFileExtension.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.io.FileUtils;
import adams.core.io.ForwardSlashSupporter;

/**
 <!-- globalinfo-start -->
 * Interprets the string as file name and replaces its extension with the provided one. If no extension is given, it simply removes it.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-extension &lt;java.lang.String&gt; (property: extension)
 * &nbsp;&nbsp;&nbsp;The new extension to replace the old one with (incl dot); use empty string 
 * &nbsp;&nbsp;&nbsp;to remove the extension.
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
public class ReplaceFileExtension
  extends AbstractStringConversion
  implements ForwardSlashSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 6141301014328628058L;
  
  /** the new file extension to use. */
  protected String m_Extension;

  /** whether to output forward slashes. */
  protected boolean m_UseForwardSlashes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Interprets the string as file name and replaces its extension with "
	+ "the provided one. If no extension is given, it simply removes it.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "extension", "extension",
	    "");

    m_OptionManager.add(
	    "use-forward-slashes", "useForwardSlashes",
	    false);
  }

  /**
   * Sets the extension to use.
   *
   * @param value	the extension (incl dot!), empty string for removing the extension
   */
  public void setExtension(String value) {
    m_Extension = value;
    reset();
  }

  /**
   * Returns the extension to use.
   *
   * @return 		the extension
   */
  public String getExtension() {
    return m_Extension;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String extensionTipText() {
    return "The new extension to replace the old one with (incl dot); use empty string to remove the extension.";
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
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    String	result;

    result = FileUtils.replaceExtension((String) m_Input, m_Extension);
    if (m_UseForwardSlashes)
      result = FileUtils.useForwardSlashes(result);

    return result;
  }
}
