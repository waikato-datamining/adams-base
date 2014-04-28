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
 * FileToString.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Turns a String into a File object.
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
 * <pre>-placeholderfile (property: createPlaceholderFileObjects)
 * &nbsp;&nbsp;&nbsp;If enabled, PlaceholderFile objects instead of simple File objects are created.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileToString
  extends AbstractConversionToString {

  /** for serialization. */
  private static final long serialVersionUID = 8828591710515484463L;

  /** whether to output an absolute path. */
  protected boolean m_AbsolutePath;

  /** whether to output forward slashes. */
  protected boolean m_UseForwardSlashes;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a File object into a String.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "absolute", "absolutePath",
	    false);

    m_OptionManager.add(
	    "use-forward-slashes", "useForwardSlashes",
	    false);
  }

  /**
   * Sets whether to create absolute paths.
   *
   * @param value	if true then absolute paths are created
   */
  public void setAbsolutePath(boolean value) {
    m_AbsolutePath = value;
    reset();
  }

  /**
   * Returns whether to create absolute paths.
   *
   * @return		true if absolute paths are created
   */
  public boolean getAbsolutePath() {
    return m_AbsolutePath;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String absolutePathTipText() {
    return "If enabled, absolute paths are generated.";
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
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
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
    String	result;
    
    if (m_AbsolutePath)
      result = ((File) m_Input).getAbsolutePath();
    else
      result = ((File) m_Input).getPath();
    
    if (m_UseForwardSlashes) {
      if (result.startsWith("\\\\"))
	result = "\\\\" + result.substring(2).replace("\\", "/");
      else
	result = result.replace("\\", "/");
    }
    
    return result;
  }
}
