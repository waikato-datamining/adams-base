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
 * MakeFilename.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.io.FileUtils;

/**
 <!-- globalinfo-start -->
 * DEPRECATED -- use adams.data.conversion.StringToValidFileName instead.<br/>
 * <br/>
 * Creates a filesystem compliant filename out of any string.<br/>
 * Note: do not include the path, as the slashes (forward or backward) will get removed&#47;replaced.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-filename-replace &lt;java.lang.String&gt; (property: filenameReplaceChar)
 * &nbsp;&nbsp;&nbsp;The character for replacing invalid characters in strings; use empty string 
 * &nbsp;&nbsp;&nbsp;for removing the invalid characters instead of replacing them.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@Deprecated
public class MakeFilename
  extends AbstractStringConversion {

  /** for serialization. */
  private static final long serialVersionUID = -4017583319699378889L;

  /** the filename replacement character. */
  protected String m_FilenameReplaceChar;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"DEPRECATED -- use " + StringToValidFileName.class.getName() + " instead.\n\n"
	+ "Creates a filesystem compliant filename out of any string.\n"
	+ "Note: do not include the path, as the slashes (forward or backward) "
	+ "will get removed/replaced.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filename-replace", "filenameReplaceChar",
	    "");
  }

  /**
   * Sets the replacement character for filenames.
   *
   * @param value 	the character or empty string
   */
  public void setFilenameReplaceChar(String value) {
    m_FilenameReplaceChar = value;
    reset();
  }

  /**
   * Returns replacement character for filenames.
   *
   * @return 		the character or empty string
   */
  public String getFilenameReplaceChar() {
    return m_FilenameReplaceChar;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filenameReplaceCharTipText() {
    return
        "The character for replacing invalid characters in strings; use empty "
	+ "string for removing the invalid characters instead of replacing them.";
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
    String	input;
    
    input  = (String) m_Input;
    result = FileUtils.createFilename(input, m_FilenameReplaceChar);

    
    return result;
  }
}
