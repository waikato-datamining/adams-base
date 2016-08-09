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

/**
 <!-- globalinfo-start -->
 * Interprets the string as file name and replaces its extension with the provided one. If no extension is given, it simply removes it.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-extension &lt;java.lang.String&gt; (property: extension)
 * &nbsp;&nbsp;&nbsp;The new extension to replace the old one with (incl dot); use empty string 
 * &nbsp;&nbsp;&nbsp;to remove the extension.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReplaceFileExtension
  extends AbstractStringConversion {

  /** for serialization. */
  private static final long serialVersionUID = 6141301014328628058L;
  
  /** the new file extension to use. */
  protected String m_Extension;
  
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
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    return FileUtils.replaceExtension((String) m_Input, m_Extension);
  }
}
