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
 * FileOutput.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.simplelogging.output;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;

/**
 * Appends the log messages to the specified file.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FileOutput
  extends AbstractSimpleOutput
  implements FileWriter {

  private static final long serialVersionUID = 1185731738994830870L;

  /** the file to write to. */
  protected PlaceholderFile m_OutputFile;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Appends the log messages to the specified file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-file", "outputFile",
      new PlaceholderFile());
  }

  /**
   * Sets the output file.
   *
   * @param value the file
   */
  @Override
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output file.
   *
   * @return the file
   */
  @Override
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The file to append the log messages to.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "outputFile", m_OutputFile, "output: ");
  }

  @Override
  protected String check(String msg) {
    String	result;

    result = super.check(msg);

    if (result == null) {
      if (m_OutputFile.isDirectory())
        result = "Log file points to a directory: " + m_OutputFile;
    }

    return result;
  }

  /**
   * Logs the (formatted) logging message.
   *
   * @param msg the message to log
   * @return null if successful, otherwise error message
   */
  @Override
  protected String doLogMessage(String msg) {
    return FileUtils.writeToFileMsg(m_OutputFile.getAbsolutePath(), msg, true, null);
  }
}
