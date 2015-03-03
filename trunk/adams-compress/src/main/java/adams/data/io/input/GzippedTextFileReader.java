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
 * GzippedTextFileReader.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

/**
 <!-- globalinfo-start -->
 * Reads content from gzipped text files.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-text-reader &lt;adams.data.io.input.AbstractTextReader&gt; (property: textReader)
 * &nbsp;&nbsp;&nbsp;The reader to use for reading the decompressed content.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.LineArrayTextReader
 * </pre>
 * 
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when reading the file, use empty string for 
 * &nbsp;&nbsp;&nbsp;default.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GzippedTextFileReader
  extends AbstractCompressedTextReader {

  /** for serialization. */
  private static final long serialVersionUID = 35626483638973054L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads content from gzipped text files.";
  }

  /**
   * Whether to use a {@link Reader} or an {@link InputStream}.
   * 
   * @return		true if using reader
   */
  @Override
  public boolean useReader() {
    return false;
  }
  
  /**
   * Initializes the input stream to read the content from.
   * 
   * @param stream	the input stream to use
   */
  @Override
  public void initialize(InputStream stream) {
    GZIPInputStream	gis;
    
    try {
      gis = new GZIPInputStream(stream);
      super.initialize(gis);
      m_TextReader.initialize(new BufferedReader(new InputStreamReader(gis, m_Encoding.charsetValue())));
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to initialize gzip stream!", e);
    }
  }
}
