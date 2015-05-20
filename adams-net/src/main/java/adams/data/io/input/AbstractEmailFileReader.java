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
 * AbstractEmailFileReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.io.PlaceholderFile;

/**
 * Ancestor of email readers that read emails from files.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractEmailFileReader
  extends AbstractEmailReader 
  implements EmailFileReader {

  /** for serialization. */
  private static final long serialVersionUID = -815445698354646307L;
  
  /** the file to read the email from. */
  protected PlaceholderFile m_Input;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "input", "input",
	    new PlaceholderFile("."));
  }

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  public String getDefaultFormatExtension() {
    return getFormatExtensions()[0];
  }

  /**
   * Sets the file to read.
   *
   * @param value	the file
   */
  public void setInput(PlaceholderFile value) {
    m_Input = value;
    reset();
  }

  /**
   * Returns the file to read.
   *
   * @return 		the object
   */
  public PlaceholderFile getInput() {
    return m_Input;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *			displaying in the GUI or for listing the options.
   */
  public String inputTipText() {
    return "The file to read the email from.";
  }

  /**
   * Hook method for performing checks before reading the emails. Throws
   * {@link IllegalStateException} in case something is wrong.
   * <br><br>
   * Ensures that input is a file and present.
   */
  @Override
  protected void check() {
    super.check();
    if (!m_Input.exists())
      throw new IllegalStateException("Input file does not exist: " + m_Input);
    if (!m_Input.isFile())
      throw new IllegalStateException("Input is not a file: " + m_Input);
  }
}
