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
 * DirExists.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.condition.test;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderDirectory;

/**
 <!-- globalinfo-start -->
 * Checks whether a specified directory exists.
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
 * <pre>-dir &lt;adams.core.io.PlaceholderFile&gt; (property: directory)
 * &nbsp;&nbsp;&nbsp;The directory to look for.
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
public class DirExists
  extends AbstractTestCondition {

  /** for serialization. */
  private static final long serialVersionUID = -2604770228945146124L;

  /** the directory to look for. */
  protected PlaceholderDirectory m_Directory;

  /** whether to invert the matching sense. */
  protected boolean m_Invert;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether a specified directory exists.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "dir", "directory",
	    new PlaceholderDirectory("."));

    m_OptionManager.add(
	    "invert", "invert",
	    false);
  }

  /**
   * Sets the file to look for.
   *
   * @param value	the file
   */
  public void setDirectory(PlaceholderDirectory value) {
    m_Directory = value;
    reset();
  }

  /**
   * Returns the file currently to look for.
   *
   * @return		the file
   */
  public PlaceholderDirectory getDirectory() {
    return m_Directory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String directoryTipText() {
    return "The directory to look for.";
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
    return QuickInfoHelper.toString(this, "directory", m_Directory, QuickInfoHelper.toString(this, "invert", (m_Invert ? "! " : ""), "dir: "));
  }

  /**
   * Performs the actual testing of the condition.
   *
   * @return		the test result, null if everything OK, otherwise
   * 			the error message
   */
  @Override
  protected String performTest() {
    if (m_Invert) {
      if (m_Directory.exists() && m_Invert)
	return "Directory '" + m_Directory + "' already exists!";
    }
    else {
      if (!m_Directory.isDirectory())
	return "Target '" + m_Directory + "' is not a directory or does not exist!";
      if (!m_Directory.exists() && !m_Invert)
	return "Directory '" + m_Directory + "' does not exist!";
    }

    return null;
  }
}
