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
 * EmailFileWriter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;

/**
 * Interface for email writers that write to files.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface EmailFileWriter
  extends EmailWriter, FileFormatHandler {

  /**
   * Sets the file to write the email to.
   *
   * @param value	the file
   */
  public void setOutput(PlaceholderFile value);

  /**
   * Returns the file to write the email to.
   *
   * @return 		the file
   */
  public PlaceholderFile getOutput();

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *			displaying in the GUI or for listing the options.
   */
  public String outputTipText();
  
  /**
   * Returns the description of the file format.
   * 
   * @return		the description
   */
  public String getFormatDescription();
  
  /**
   * Returns the extension(s) of the file format (without dot).
   * 
   * @return		the extensions (no dot!)
   */
  public String[] getFormatExtensions();
}
