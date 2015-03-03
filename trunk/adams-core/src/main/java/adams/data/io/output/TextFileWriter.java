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
 * TextFileWriter.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.io.FileUtils;


/**
 <!-- globalinfo-start -->
 * Writes the content to a text file.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TextFileWriter
  extends AbstractTextWriterWithFilenameGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 2481561390856324348L;

  /** whether to append the file. */
  protected boolean m_Append;

  /**
   * Returns a short description of the writer.
   *
   * @return		a description of the writer
   */
  @Override
  public String globalInfo() {
    return "Writes the content to a text file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "append", "append",
	    false);
  }

  /**
   * Sets whether to append the file rather than rewriting it.
   *
   * @param value 	if true then append the content
   */
  public void setAppend(boolean value) {
    m_Append = value;
    reset();
  }

  /**
   * Returns whether to append the file rather than rewriting it.
   *
   * @return 		true if to append the content
   */
  public boolean getAppend() {
    return m_Append;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String appendTipText() {
    return "If enabled, the content gets appended rather than rewriting the file.";
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
    boolean	ok;
    String	filename;

    filename = createFilename(name);
    if (isLoggingEnabled())
      getLogger().info("Filename: " + filename);

    ok = FileUtils.writeToFile(filename, content, m_Append, m_Encoding.stringValue());
    if (isLoggingEnabled())
      getLogger().info("Result (append=" + m_Append + "): " + ok);

    if (ok)
      return filename;
    else
      return null;
  }
}
