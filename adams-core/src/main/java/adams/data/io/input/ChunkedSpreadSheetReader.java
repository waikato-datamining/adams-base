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
 * ChunkedSpreadSheetReader.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Interface for spreadsheet readers that can read data in chunks rather
 * than only all at once.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ChunkedSpreadSheetReader {

  /**
   * Sets the maximum chunk size.
   * 
   * @param value	the size of the chunks, &lt; 1 denotes infinity
   */
  public void setChunkSize(int value);
  
  /**
   * Returns the current chunk size.
   * 
   * @return	the size of the chunks, &lt; 1 denotes infinity
   */
  public int getChunkSize();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String chunkSizeTipText();
  
  /**
   * Checks whether there is more data to read.
   * 
   * @return		true if there is more data available
   */
  public boolean hasMoreChunks();
  
  /**
   * Returns the next chunk.
   * 
   * @return		the next chunk
   */
  public SpreadSheet nextChunk();
}
