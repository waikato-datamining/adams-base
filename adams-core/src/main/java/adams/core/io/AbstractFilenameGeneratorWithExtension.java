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
 * AbstractFilenameGeneratorWithExtension.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

/**
 * Ancestor for filename generators that require a provided extension.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFilenameGeneratorWithExtension
  extends AbstractFilenameGeneratorWithDirectory {

  /** for serialization. */
  private static final long serialVersionUID = 170012014097820281L;

  /** the extension to use. */
  protected String m_Extension;

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
   * @param value	the extension (incl dot)
   */
  public void setExtension(String value) {
    if ((value.length() > 0) && !value.startsWith("."))
      value = "." + value;
    m_Extension = value;
    reset();
  }

  /**
   * Returns the extension in use.
   *
   * @return		the extension (incl dot)
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
    return "The extension to use (including the dot).";
  }
}
