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
 * FileSplitterWithBinarySupport.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.splitfile;

/**
 * Interface for file splitters that can process binary files as well.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface FileSplitterWithBinarySupport
  extends FileSplitter {

  /**
   * Sets how to process the files.
   *
   * @param value	the type of file
   */
  public void setFileType(FileType value);

  /**
   * Returns how to process the files.
   *
   * @return		the type of file
   */
  public FileType getFileType();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fileTypeTipText();

  /**
   * Sets the size of the buffer.
   *
   * @param value	the size
   */
  public void setBufferSize(int value);

  /**
   * Get output file.
   *
   * @return	file
   */
  public int getBufferSize();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bufferSizeTipText();
}
