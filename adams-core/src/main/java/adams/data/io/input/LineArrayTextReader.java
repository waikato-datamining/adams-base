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
 * LineArrayTextReader.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Reads the text in as array of strings (each line is an array element).<br>
 * For large files, the data can be 'chunked' (ie outputting it in blocks).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when reading the file, use empty string for 
 * &nbsp;&nbsp;&nbsp;default.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 * <pre>-chunk-size &lt;int&gt; (property: chunkSize)
 * &nbsp;&nbsp;&nbsp;The maximum number of lines per chunk; using -1 will read put all data into 
 * &nbsp;&nbsp;&nbsp;a single array.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LineArrayTextReader
  extends AbstractTextReaderWithEncoding<String[]> {

  /** for serialization. */
  private static final long serialVersionUID = -4772416995579481937L;

  /** the chunk size to use. */
  protected int m_ChunkSize;

  /** the scanner in use. */
  protected transient Scanner m_Scanner;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Reads the text in as array of strings (each line is an array element).\n"
	+ "For large files, the data can be 'chunked' (ie outputting it in blocks).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "chunk-size", "chunkSize",
	    -1, -1, null);
  }

  /**
   * Sets the maximum chunk size.
   *
   * @param value	the size of the chunks, &lt; 1 denotes infinity
   */
  public void setChunkSize(int value) {
    if (value < 1)
      value = -1;
    m_ChunkSize = value;
    reset();
  }

  /**
   * Returns the current chunk size.
   *
   * @return	the size of the chunks, &lt; 1 denotes infinity
   */
  public int getChunkSize() {
    return m_ChunkSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String chunkSizeTipText() {
    return "The maximum number of lines per chunk; using -1 will read put all data into a single array.";
  }
  
  /**
   * Returns the class of the data that it returns.
   * 
   * @return		the generated data type
   */
  @Override
  public Class generates() {
    return String[].class;
  }

  /**
   * Initializes the input stream to read the content from.
   *
   * @param stream	the input stream to use
   */
  public void initialize(InputStream stream) {
    super.initialize(stream);
    m_Scanner = new Scanner(m_Stream, m_Encoding.charsetValue().name());
  }

  /**
   * Returns the next lot of data.
   * 
   * @return		the next amount of data, null if failed to read
   */
  @Override
  public String[] doNext() {
    List<String>	result;
    String		line;
    
    result = new ArrayList<String>();
    
    try {
      while ((line = m_Scanner.nextLine()) != null) {
	result.add(line);
	if (m_ChunkSize > -1) {
	  if (result.size() >= m_ChunkSize)
	    break;
	}
      }
      if (m_ChunkSize == -1) {
	m_Scanner.close();
	m_Scanner = null;
      }
    }
    catch (NoSuchElementException e) {
      m_Scanner.close();
      m_Scanner = null;
      // nothing left in stream
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read from scanner!", e);
      m_Scanner.close();
      m_Scanner = null;
      m_Stream  = null;
      return null;
    }

    if (m_Scanner == null)
      m_Stream = null;
    
    return result.toArray(new String[result.size()]);
  }
}
