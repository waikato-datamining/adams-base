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
 * ZipArchiveManager.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io;

import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.annotation.MixedCopyright;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Manages zip files.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
@MixedCopyright(
  copyright = "Apache compress commons",
  license = License.APACHE2,
  url = "http://commons.apache.org/compress/examples.html"
)
public class ZipArchiveManager
  extends AbstractOptionHandler
  implements ArchiveManager {

  private static final long serialVersionUID = 903710989406686406L;

  /** the buffer size to use. */
  protected int m_BufferSize;

  /** the zip archive stream. */
  protected transient ZipArchiveOutputStream m_ZipOut;

  /** the file output stream for the zip archive. */
  protected transient FileOutputStream m_FileOut;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Manages zip files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "buffer", "bufferSize",
      1024, 1, null);
  }

  /**
   * Sets the buffer size for the stream.
   *
   * @param value	the size in bytes
   */
  public void setBufferSize(int value) {
    if (getOptionManager().isValid("bufferSize", value)) {
      m_BufferSize = value;
      reset();
    }
  }

  /**
   * Returns the buffer size for the stream.
   *
   * @return 		the size in bytes
   */
  public int getBufferSize() {
    return m_BufferSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String bufferSizeTipText() {
    return "The size of the buffer in bytes for the data stream.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "buffer", m_BufferSize, "buffer: ");
  }

  /**
   * Initializes the archive.
   *
   * @param output the file name for the archive
   * @return null if successful, otherwise error message
   */
  @Override
  public String initialize(PlaceholderFile output) {
    if (output.exists())
      getLogger().warning("overwriting '" + output + "'!");

    m_FileOut = null;
    m_ZipOut  = null;

    try {
      m_FileOut = new FileOutputStream(output.getAbsolutePath());
      m_ZipOut  = new ZipArchiveOutputStream(new BufferedOutputStream(m_FileOut));
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to open zip file: " + output, e);
    }

    return null;
  }

  /**
   * Adds the file to the archive.
   *
   * @param data the file to add
   * @param name the name for the file in the archive
   * @return null if successful, otherwise error message
   */
  @Override
  public String add(PlaceholderFile data, String name) {
    String			result;
    BufferedInputStream in;
    FileInputStream 	fis;
    byte[] 		buf;
    int 		len;
    ZipArchiveEntry 	entry;

    result = null;
    fis    = null;
    in     = null;
    try {
      buf = new byte[m_BufferSize];
      fis = new FileInputStream(data.getAbsolutePath());
      in  = new BufferedInputStream(fis);

      // Add ZIP entry to output stream.
      entry = new ZipArchiveEntry(name);
      entry.setSize(data.length());
      m_ZipOut.putArchiveEntry(entry);

      // Transfer bytes from the file to the ZIP file
      while ((len = in.read(buf)) > 0)
	m_ZipOut.write(buf, 0, len);

      // Complete the entry
      m_ZipOut.closeArchiveEntry();
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to add data from file: " + data, e);
    }
    finally {
      FileUtils.closeQuietly(in);
      FileUtils.closeQuietly(fis);
    }

    return result;
  }

  /**
   * Adds the data from the input stream to the archive.
   * Caller needs to close input stream.
   *
   * @param data the data to add
   * @param name the name for the data in the archive
   * @return null if successful, otherwise error message
   */
  @Override
  public String add(InputStream data, String name) {
    String			result;
    BufferedInputStream 	in;
    byte[] 			buf;
    int 			len;
    ZipArchiveEntry 		entry;

    result = null;
    in     = null;
    try {
      buf = new byte[m_BufferSize];
      in  = new BufferedInputStream(data);

      // Add ZIP entry to output stream.
      entry = new ZipArchiveEntry(name);
      m_ZipOut.putArchiveEntry(entry);

      // Transfer bytes from the file to the ZIP file
      while ((len = in.read(buf)) > 0)
	m_ZipOut.write(buf, 0, len);

      // Complete the entry
      m_ZipOut.closeArchiveEntry();
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to add data from stream!", e);
    }
    finally {
      FileUtils.closeQuietly(in);
    }

    return result;
  }

  /**
   * Adds the data to the archive.
   *
   * @param data the data to add
   * @param name the name for the data in the archive
   * @return null if successful, otherwise error message
   */
  @Override
  public String add(byte[] data, String name) {
    String			result;
    BufferedInputStream 	in;
    ByteArrayInputStream	bis;
    byte[] 			buf;
    int 			len;
    ZipArchiveEntry 		entry;

    result = null;
    bis    = null;
    in     = null;
    try {
      buf = new byte[m_BufferSize];
      bis = new ByteArrayInputStream(data);
      in  = new BufferedInputStream(bis);

      // Add ZIP entry to output stream.
      entry = new ZipArchiveEntry(name);
      entry.setSize(data.length);
      m_ZipOut.putArchiveEntry(entry);

      // Transfer bytes from the file to the ZIP file
      while ((len = in.read(buf)) > 0)
	m_ZipOut.write(buf, 0, len);

      // Complete the entry
      m_ZipOut.closeArchiveEntry();
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to add data from bytes!", e);
    }
    finally {
      FileUtils.closeQuietly(in);
      FileUtils.closeQuietly(bis);
    }

    return result;
  }

  /**
   * Finalizes the archive.
   *
   * @return null if successful, otherwise error message
   */
  @Override
  public String close() {
    FileUtils.closeQuietly(m_ZipOut);
    FileUtils.closeQuietly(m_FileOut);
    m_ZipOut  = null;
    m_FileOut = null;
    return null;
  }
}
