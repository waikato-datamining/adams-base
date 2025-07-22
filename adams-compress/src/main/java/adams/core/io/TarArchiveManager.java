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
 * TarArchiveManager.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io;

import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.annotation.MixedCopyright;
import adams.core.io.TarUtils.Compression;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Level;

/**
 * Manages tar files.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
@MixedCopyright(
  author = "Jeremy Whitlock (jcscoobyrs)",
  copyright = "2010 Jeremy Whitlock",
  license = License.APACHE2,
  url = "http://www.thoughtspark.org/node/53"
)
public class TarArchiveManager
  extends AbstractOptionHandler
  implements ArchiveManager {

  private static final long serialVersionUID = 903710989406686406L;

  /** the compression to use. */
  protected Compression m_Compression;

  /** the buffer size to use. */
  protected int m_BufferSize;
  
  /** the tar archive stream. */
  protected transient TarArchiveOutputStream m_TarOut;

  /** the file output stream for the tar archive. */
  protected transient FileOutputStream m_FileOut;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Manages tar files. Uses GNU long filename support.";
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

    m_OptionManager.add(
      "compression", "compression",
      Compression.AUTO);
  }

  /**
   * Sets the compression to use. If {@link Compression#AUTO} then compression gets determined based on file extension.
   *
   * @param value	the compression to use
   */
  public void setCompression(Compression value) {
    m_Compression = value;
    reset();
  }

  /**
   * Returns the compression to use. If {@link Compression#AUTO} then compression gets determined based on file extension.
   *
   * @return 		the compression to use
   */
  public Compression getCompression() {
    return m_Compression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String compressionTipText() {
    return "The compression to use; if " + Compression.AUTO + " then compression gets determined based on file extension.";
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
    String	result;

    result  = QuickInfoHelper.toString(this, "compression", m_Compression, "compression: ");
    result += QuickInfoHelper.toString(this, "buffer", m_BufferSize, ", buffer: ");

    return result;
  }

  /**
   * Initializes the archive.
   *
   * @param output the file name for the archive
   * @return null if successful, otherwise error message
   */
  @Override
  public String initialize(PlaceholderFile output) {
    Compression		compression;

    if (output.exists())
      getLogger().warning("overwriting '" + output + "'!");

    m_FileOut = null;
    m_TarOut  = null;

    if (m_Compression == Compression.AUTO) {
      try {
	compression = TarUtils.determineCompression(output);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to determine compression for '" + output + "', using no compression.", e);
	compression = Compression.NONE;
      }
    }
    else {
      compression = m_Compression;
    }

    try {
      m_FileOut = new FileOutputStream(output.getAbsolutePath());
      m_TarOut  = TarUtils.openArchiveForWriting(m_FileOut, compression);
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to open tar file for writing: " + output, e);
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
    BufferedInputStream 	in;
    FileInputStream 		fis;
    byte[] 			buf;
    int 			len;
    TarArchiveEntry 		entry;

    result = null;
    fis    = null;
    in     = null;
    try {
      buf = new byte[m_BufferSize];
      fis = new FileInputStream(data.getAbsolutePath());
      in  = new BufferedInputStream(fis);

      // Add tar entry to output stream.
      entry = new TarArchiveEntry(name);
      entry.setSize(data.length());
      m_TarOut.putArchiveEntry(entry);

      // Transfer bytes from the file to the tar file
      while ((len = in.read(buf)) > 0)
	m_TarOut.write(buf, 0, len);

      // Complete the entry
      m_TarOut.closeArchiveEntry();
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
    TarArchiveEntry 		entry;

    result = null;
    in     = null;
    try {
      buf = new byte[m_BufferSize];
      in  = new BufferedInputStream(data);

      // Add tar entry to output stream.
      entry = new TarArchiveEntry(name);
      m_TarOut.putArchiveEntry(entry);

      // Transfer bytes from the file to the tar file
      while ((len = in.read(buf)) > 0)
	m_TarOut.write(buf, 0, len);

      // Complete the entry
      m_TarOut.closeArchiveEntry();
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
    TarArchiveEntry 		entry;

    result = null;
    bis    = null;
    in     = null;
    try {
      buf = new byte[m_BufferSize];
      bis = new ByteArrayInputStream(data);
      in  = new BufferedInputStream(bis);

      // Add tar entry to output stream.
      entry = new TarArchiveEntry(name);
      entry.setSize(data.length);
      m_TarOut.putArchiveEntry(entry);

      // Transfer bytes from the file to the tar file
      while ((len = in.read(buf)) > 0)
	m_TarOut.write(buf, 0, len);

      // Complete the entry
      m_TarOut.closeArchiveEntry();
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
    FileUtils.closeQuietly(m_TarOut);
    FileUtils.closeQuietly(m_FileOut);
    m_TarOut = null;
    m_FileOut = null;
    return null;
  }
}
