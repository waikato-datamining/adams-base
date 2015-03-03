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
 * PlainTextEmailFileWriter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.io.FileUtils;
import adams.core.net.Email;

/**
 <!-- globalinfo-start -->
 * Writes emails to text files.<br/>
 * Handles only ASCII content.
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
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The file to write the email to.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlainTextEmailFileWriter
  extends AbstractEmailFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 8613890718581689507L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	  "Writes emails to text files.\n"
	+ "Handles only ASCII content.";
  }
  
  /**
   * Returns the description of the file format.
   * 
   * @return		the description
   */
  public String getFormatDescription() {
    return "Email text files";
  }
  
  /**
   * Returns the extension(s) of the file format (without dot).
   * 
   * @return		the extensions (no dot!)
   */
  public String[] getFormatExtensions() {
    return new String[]{"eml"};
  }
  
  /**
   * Performs the actual writing.
   * 
   * @param email	the email to write
   * @return		the error message, null if everything OK
   */
  @Override
  protected String doWrite(Email email) {
    String		result;
    
    result = null;

    if (!FileUtils.writeToFile(m_Output.getAbsolutePath(), email.toPlainText(), false))
      result = "Failed to write email content to " + m_Output + ", check console!";
    
    return result;
  }
}
