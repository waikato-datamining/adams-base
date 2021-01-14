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
 * LineByLineTextReader.java
 * Copyright (C) 2014-2021 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;

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
 * <pre>-max-lines &lt;int&gt; (property: maxLines)
 * &nbsp;&nbsp;&nbsp;The maximum number of lines to read; using -1 will read all.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class LineByLineTextReader
  extends AbstractTextReaderWithEncoding<String> {

  /** for serialization. */
  private static final long serialVersionUID = -2921085514028198744L;

  /** the maximum number of lines to read. */
  protected int m_MaxLines;

  /** lines read so far. */
  protected int m_LineCount;

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
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "max-lines", "maxLines",
      -1, -1, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  public void reset() {
    super.reset();

    m_LineCount = 0;
  }

  /**
   * Sets the maximum lines to read.
   *
   * @param value	the maximum, &lt; 1 denotes infinity
   */
  public void setMaxLines(int value) {
    if (value < 1)
      value = -1;
    m_MaxLines = value;
    reset();
  }

  /**
   * Returns the maximum lines to read.
   *
   * @return		the maximum, &lt; 1 denotes infinity
   */
  public int getMaxLines() {
    return m_MaxLines;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maxLinesTipText() {
    return "The maximum number of lines to read; using -1 will read all.";
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "maxLines", m_MaxLines, ", max: ");

    return result;
  }

  /**
   * Initializes the input stream to read the content from.
   *
   * @param stream	the input stream to use
   */
  public void initialize(InputStream stream) {
    super.initialize(stream);
    m_Scanner   = new Scanner(m_Stream, m_Encoding.charsetValue().name());
    m_LineCount = 0;
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

    if ((m_MaxLines > -1) && (m_LineCount >= m_MaxLines)) {
      if (isLoggingEnabled())
        getLogger().info("Reached maximum number of lines: " + m_MaxLines);
      if (m_Scanner != null) {
	m_Scanner.close();
	m_Scanner = null;
	try {
	  m_Stream.close();
	}
	catch (Exception e) {
	  // ignored
	}
	m_Stream = null;
      }
      return null;
    }

    try {
      result = FileUtils.removeAllByteOrderMarks(m_Scanner.nextLine());
      m_LineCount++;
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
