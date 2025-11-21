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
 * AbstractFileSplitterWithBinarySupport.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.splitfile;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;

/**
 * Ancestor for file splitters that support binary files as well.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFileSplitterWithBinarySupport
  extends AbstractFileSplitter
  implements FileSplitterWithBinarySupport {

  private static final long serialVersionUID = 1711653254314409962L;

  /** the file type. */
  protected FileType m_FileType;

  /** the buffer size. */
  protected int m_BufferSize;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "file-type", "fileType",
      FileType.TEXT);

    m_OptionManager.add(
      "buffer-size", "bufferSize",
      1024, 1, null);
  }

  /**
   * Sets how to process the files.
   *
   * @param value	the type of file
   */
  public void setFileType(FileType value) {
    m_FileType = value;
    reset();
  }

  /**
   * Returns how to process the files.
   *
   * @return		the type of file
   */
  public FileType getFileType() {
    return m_FileType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fileTypeTipText() {
    return "Defines how to treat the file(s).";
  }

  /**
   * Sets the size of the buffer.
   *
   * @param value	the size
   */
  public void setBufferSize(int value) {
    if (getOptionManager().isValid("bufferSize", value)) {
      m_BufferSize = value;
      reset();
    }
  }

  /**
   * Get output file.
   *
   * @return	file
   */
  public int getBufferSize() {
    return m_BufferSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bufferSizeTipText() {
    return "The size of byte-buffer used for reading the content.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String 	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "fileType", m_FileType, ", type: ");
    result += QuickInfoHelper.toString(this, "buffer", m_BufferSize, ", buffer: ");

    return result;
  }

  /**
   * Performs the actual splitting of the text file.
   *
   * @param file	the file to split
   */
  protected abstract void doSplitText(PlaceholderFile file);

  /**
   * Performs the actual splitting of the binary file.
   *
   * @param file	the file to split
   */
  protected abstract void doSplitBinary(PlaceholderFile file);

  /**
   * Performs the actual splitting of the file.
   *
   * @param file	the file to split
   */
  @Override
  protected void doSplit(PlaceholderFile file) {
    switch (m_FileType) {
      case BINARY:
	doSplitBinary(file);
	break;
      case TEXT:
	doSplitText(file);
	break;
      default:
	throw new IllegalStateException("Unhandled file type: " + m_FileType);
    }
  }
}
