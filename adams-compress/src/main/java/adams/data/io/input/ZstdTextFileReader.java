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
 * ZstdTextFileReader.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import com.github.luben.zstd.ZstdInputStream;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 <!-- globalinfo-start -->
 * Reads content from ZStd compressed text files.
 * <br><br>
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ZstdTextFileReader
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
    return "Reads content from Zstd compressed text files.";
  }

  /**
   * Initializes the input stream to read the content from.
   * 
   * @param stream	the input stream to use
   */
  @Override
  public void initialize(InputStream stream) {
    ZstdInputStream zis;
    
    try {
      zis = new ZstdInputStream(new BufferedInputStream(stream));
      super.initialize(zis);
      m_TextReader.initialize(zis);
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to initialize zstd stream!", e);
    }
  }
}
