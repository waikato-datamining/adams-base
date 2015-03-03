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
 * FileExists.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.condition.test;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;

/**
 <!-- globalinfo-start -->
 * Checks whether a specified file exists.
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
 * <pre>-file &lt;adams.core.io.PlaceholderFile&gt; (property: file)
 * &nbsp;&nbsp;&nbsp;The file to look for.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-invert (property: invert)
 * &nbsp;&nbsp;&nbsp;If set to true, then the matching sense is inverted.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileExists
  extends AbstractTestCondition {

  /** for serialization. */
  private static final long serialVersionUID = 3690024487474608636L;

  /** the file to look for. */
  protected PlaceholderFile m_File;

  /** whether to invert the matching sense. */
  protected boolean m_Invert;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether a specified file exists.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "file", "file",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "invert", "invert",
	    false);
  }

  /**
   * Sets the file to look for.
   *
   * @param value	the file
   */
  public void setFile(PlaceholderFile value) {
    m_File = value;
    reset();
  }

  /**
   * Returns the file currently to look for.
   *
   * @return		the file
   */
  public PlaceholderFile getFile() {
    return m_File;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String fileTipText() {
    return "The file to look for.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	true if inverting matching sense
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if matching sense is inverted
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If set to true, then the matching sense is inverted.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "file", m_File, QuickInfoHelper.toString(this, "invert", (m_Invert ? "! " : ""), "file: "));
  }

  /**
   * Performs the actual testing of the condition.
   *
   * @return		the test result, null if everything OK, otherwise
   * 			the error message
   */
  @Override
  protected String performTest() {
    if (m_File.isDirectory())
      return "Target '" + m_File + "' is a directory!";
    if (m_File.exists() && m_Invert)
      return "File '" + m_File + "' already exists!";
    if (!m_File.exists() && !m_Invert)
      return "File '" + m_File + "' does not exist!";

    return null;
  }
}
