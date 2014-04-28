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
 * AppendableSpreadSheetWriter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Interface for spreadsheet writers that can append data to existing files.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface AppendableSpreadSheetWriter {

  /**
   * Checks whether we can append the specified spreadsheet to the existing
   * file.
   * 
   * @param sheet	the spreadsheet to append to the existing one
   * @return		true if appending is possible
   */
  public boolean canAppend(SpreadSheet sheet);
  
  /**
   * Sets whether the output file already exists.
   * 
   * @param value	true if the output file already exists
   */
  public void setFileExists(boolean value);
  
  /**
   * Returns whether the output file already exists.
   * 
   * @return		true if the output file already exists
   */
  public boolean getFileExists();
  
  /**
   * Sets whether the next write call is to append the data to the existing
   * file.
   * 
   * @param value	true if to append
   */
  public void setAppending(boolean value);
  
  /**
   * Returns whether the next spreadsheet will get appended.
   * 
   * @return		true if append is active
   */
  public boolean isAppending();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String appendingTipText();

  /**
   * Sets whether to keep any existing file on first execution.
   *
   * @param value	if true then existing file is kept
   */
  public void setKeepExisting(boolean value);

  /**
   * Returns whether any existing file is kept on first execution.
   *
   * @return		true if existing file is kept
   */
  public boolean getKeepExisting();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keepExistingTipText();
}
