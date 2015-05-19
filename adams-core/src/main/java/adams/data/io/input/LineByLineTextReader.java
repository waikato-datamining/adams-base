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
 * LineByLineTextReader.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Reads the text data, line by line. Allows the reading of very large files.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LineByLineTextReader
  extends AbstractTextReaderWithEncoding<String> {

  /** for serialization. */
  private static final long serialVersionUID = -2921085514028198744L;

  /** the scanner in use. */
  protected transient Scanner m_Scanner;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads the text data, line by line. Allows the reading of very large files.";
  }

  /**
   * Returns the class of the data that it returns.
   * 
   * @return		the generated data type
   */
  @Override
  public Class generates() {
    return String.class;
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
  protected String doNext() {
    String		result;
    
    result = null;
    
    try {
      result = m_Scanner.nextLine();
    }
    catch (NoSuchElementException e) {
      // nothing left in stream
      m_Scanner.close();
      m_Scanner = null;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read from scanner!", e);
      m_Scanner.close();
      m_Scanner = null;
      result    = null;
    }

    if (m_Scanner == null)
      m_Stream = null;

    return result;
  }
}
