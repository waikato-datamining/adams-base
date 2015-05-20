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
 * DefaultFilenameGenerator.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import adams.data.DataUtils;

/**
 <!-- globalinfo-start -->
 * The default generator for database ID and ID handlers.
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
 * <pre>-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: directory)
 * &nbsp;&nbsp;&nbsp;The parent directory of the generated filename.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-extension &lt;java.lang.String&gt; (property: extension)
 * &nbsp;&nbsp;&nbsp;The extension to use (including the dot).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-suffix &lt;java.lang.String&gt; (property: suffix)
 * &nbsp;&nbsp;&nbsp;The suffix to use (after the DB-ID or ID).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultFilenameGenerator
  extends AbstractFilenameGeneratorWithExtension {

  /** for serialization. */
  private static final long serialVersionUID = 6313170021657883586L;

  /** the suffix to use. */
  protected String m_Suffix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The default generator for database ID and ID handlers.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "suffix", "suffix",
	    "");
  }

  /**
   * Sets the suffix to use.
   *
   * @param value	the suffix
   */
  public void setSuffix(String value) {
    m_Suffix = value;
    reset();
  }

  /**
   * Returns the suffix in use.
   *
   * @return		the suffix
   */
  public String getSuffix() {
    return m_Suffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suffixTipText() {
    return "The suffix to use (after the DB-ID or ID).";
  }
  
  /**
   * Returns whether we actually need an object to generate the filename.
   * 
   * @return		true if object required
   */
  @Override
  public boolean canHandleNullObject() {
    return false;
  }

  /**
   * Performs the actual generation of the filename.
   *
   * @param obj		the object to generate the filename for
   * @return		the generated filename
   */
  @Override
  protected String doGenerate(Object obj) {
    return DataUtils.createFilename(m_Directory, obj, m_Suffix, m_Extension);
  }
}
