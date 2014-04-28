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
 * TextFileWithLineNumbersWriter.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.Utils;

/**
 <!-- globalinfo-start -->
 * Writes the content to a text file, preceding each line with its line number.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-enabled &lt;boolean&gt; (property: enabled)
 * &nbsp;&nbsp;&nbsp;Whether the writer is enabled.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 * <pre>-filename-generator &lt;adams.core.io.AbstractFilenameGenerator&gt; (property: filenameGenerator)
 * &nbsp;&nbsp;&nbsp;The filename generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.core.io.SimpleFilenameGenerator
 * </pre>
 * 
 * <pre>-ignore-name &lt;boolean&gt; (property: ignoreName)
 * &nbsp;&nbsp;&nbsp;If set to true, then the name of the content is ignored for generating the 
 * &nbsp;&nbsp;&nbsp;filename (useful when prefix or suffix is based on variables).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-append &lt;boolean&gt; (property: append)
 * &nbsp;&nbsp;&nbsp;If enabled, the content gets appended rather than rewriting the file.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-separator &lt;java.lang.String&gt; (property: separator)
 * &nbsp;&nbsp;&nbsp;The separator between line number and line content.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TextFileWithLineNumbersWriter
  extends TextFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 4958356636746933734L;

  /** the separator between the line number and the actual line. */
  protected String m_Separator;

  /** the line count. */
  protected int m_Count;

  /**
   * Returns a short description of the writer.
   *
   * @return		a description of the writer
   */
  @Override
  public String globalInfo() {
    return
        "Writes the content to a text file, preceding each line with its line number.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "separator", "separator",
	    "");
  }

  /**
   * Resets the count.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Count = 0;
  }
  
  /**
   * Sets the separator between line number and line content.
   *
   * @param value 	the separator
   */
  public void setSeparator(String value) {
    m_Separator = value;
    reset();
  }

  /**
   * Returns the separator between line number and line content.
   *
   * @return 		the separator
   */
  public String getSeparator() {
    return m_Separator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String separatorTipText() {
    return "The separator between line number and line content.";
  }

  /**
   * Writes the given content under the specified name.
   *
   * @param content	the content to write
   * @param name	the name under which to save the content
   * @return		if a file was generated, the filename the content was written
   * 			as, otherwise null
   */
  @Override
  protected String doWrite(String content, String name) {
    String[]	lines;
    int		i;

    if (!m_Append)
      m_Count = 0;
    
    // add line numbers
    lines = content.split("\n");
    for (i = 0; i < lines.length; i++) {
      m_Count++;
      lines[i] = (m_Count) + m_Separator + lines[i];
    }

    return super.doWrite(Utils.flatten(lines, "\n"), name);
  }
}
