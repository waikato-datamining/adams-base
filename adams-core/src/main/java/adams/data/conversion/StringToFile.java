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
 * StringToFile.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.io.File;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;

/**
 <!-- globalinfo-start -->
 * Turns a String into a File object. Optionally, the file name part (excluding the parent path) can be fixed to make it file-system compliant. E.g., ':' is not allowed on MS Windows systems.
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
 * <pre>-placeholderfile (property: createPlaceholderFileObjects)
 * &nbsp;&nbsp;&nbsp;If enabled, PlaceholderFile objects instead of simple File objects are created.
 * </pre>
 *
 * <pre>-make-compliant (property: makeCompliant)
 * &nbsp;&nbsp;&nbsp;If enabled, the file name part of the file (excluding the parent path) is
 * &nbsp;&nbsp;&nbsp;made file-system compliant; compliant characters: abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789
 * &nbsp;&nbsp;&nbsp;_-.,()
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringToFile
  extends AbstractConversionFromString {

  /** for serialization. */
  private static final long serialVersionUID = 8828591710515484463L;

  /** whether to generate PlaceholderFile objects instead. */
  protected boolean m_CreatePlaceholderFileObjects;

  /** whether to fix the name part of the filename, in order to make it
   * file-system compliant. */
  protected boolean m_MakeCompliant;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Turns a String into a File object. Optionally, the file name part "
      + "(excluding the parent path) can be fixed to make it file-system "
      + "compliant. E.g., ':' is not allowed on MS Windows systems.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "placeholderfile", "createPlaceholderFileObjects",
	    false);

    m_OptionManager.add(
	    "make-compliant", "makeCompliant",
	    false);
  }

  /**
   * Sets whether to create PlaceholderFile objects instead.
   *
   * @param value	if true then PlaceholderFile objects are created
   */
  public void setCreatePlaceholderFileObjects(boolean value) {
    m_CreatePlaceholderFileObjects = value;
    reset();
  }

  /**
   * Returns whether to create PlaceholderFile objects instead.
   *
   * @return		true if PlaceholderFile objects are created
   */
  public boolean getCreatePlaceholderFileObjects() {
    return m_CreatePlaceholderFileObjects;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String createPlaceholderFileObjectsTipText() {
    return "If enabled, PlaceholderFile objects instead of simple File objects are created.";
  }

  /**
   * Sets whether to make the file name part file-system compliant.
   *
   * @param value	if true then file name part is made file-system compliant
   */
  public void setMakeCompliant(boolean value) {
    m_MakeCompliant = value;
    reset();
  }

  /**
   * Returns whether to make the file name part file-system compliant.
   *
   * @return		true if the file name part is made file-system compliant
   */
  public boolean getMakeCompliant() {
    return m_MakeCompliant;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String makeCompliantTipText() {
    return
        "If enabled, the file name part of the file (excluding the parent "
      + "path) is made file-system compliant; compliant characters: "
      + FileUtils.getFileNameChars() + " (see " + FileUtils.FILENAME + ")";
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return File.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    File	result;
    String	input;

    input = (String) m_Input;
    if (input.startsWith("\\\\"))
      input = "\\\\" + input.substring(2).replace("\\", "/");
    else
      input = input.replace("\\", "/");
    
    if (m_CreatePlaceholderFileObjects) {
      result = new PlaceholderFile(input);
      if (m_MakeCompliant)
	result = new PlaceholderFile(
	      result.getParent()
	    + File.separator
	    + FileUtils.createFilename(result.getName(), "_"));
    }
    else {
      result = new File(input);
      if (m_MakeCompliant)
	result = new File(
	      result.getParent()
	    + File.separator
	    + FileUtils.createFilename(result.getName(), "_"));
    }

    return result;
  }
}
