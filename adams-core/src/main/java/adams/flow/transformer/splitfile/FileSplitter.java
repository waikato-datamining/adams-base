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
 * FileSplitter.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.splitfile;

import adams.core.Stoppable;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionHandler;

/**
 * Interface for file splitters.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface FileSplitter
  extends OptionHandler, Stoppable {

  /**
   * Sets the prefix for the generated files.
   *
   * @param value	the prefix
   */
  public void setPrefix(PlaceholderFile value);

  /**
   * Returns the prefix for the generated files.
   *
   * @return		the prefix
   */
  public PlaceholderFile getPrefix();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText();

  /**
   * Sets the extension for the generated files.
   *
   * @param value	the extension
   */
  public void setExtension(String value);

  /**
   * Returns the extension for the generated files.
   *
   * @return		the extension
   */
  public String getExtension();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String extensionTipText();

  /**
   * Sets the number of digits to use for the index of the generated files.
   *
   * @param value	the number of digits
   */
  public void setNumDigits(int value);

  /**
   * Returns the number of digits to use for the index of the generated files.
   *
   * @return		the number of digits
   */
  public int getNumDigits();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDigitsTipText();

  /**
   * Splits the file and returns the filenames of the generated files.
   * 
   * @param file	the file to split
   * @return		the filenames of the new files generated
   */
  public String[] split(PlaceholderFile file);
}
