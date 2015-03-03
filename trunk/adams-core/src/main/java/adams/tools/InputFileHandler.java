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
 * InputFileHandler.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.tools;

import adams.core.io.PlaceholderFile;

/**
 * For tools that handle an input file.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface InputFileHandler {

  /**
   * Set input file.
   * 
   * @param value	file
   */
  public void setInputFile(PlaceholderFile value);
  
  /**
   * Get input file.
   * 
   * @return	file
   */
  public PlaceholderFile getInputFile();
  
  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputFileTipText();
}
