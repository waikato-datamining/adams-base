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
 * RotatingFileOutput.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.simplelogging.output;

import adams.core.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Appends the log messages to the specified file, but also allows for rotating them.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RotatingFileOutput
  extends FileOutput {

  private static final long serialVersionUID = -6106247481018229336L;

  /** the number of log files to use. */
  protected int m_NumLogFiles;

  /** whether the logs have been rotated. */
  protected boolean m_Rotated;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Appends the log messages to the specified file, but also allows for rotating them.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-log-files", "numLogFiles",
      5, 1, null);
  }

  /**
   * Sets the number of logs to use.
   *
   * @param value	the number of log files
   */
  public void setNumLogFiles(int value) {
    m_NumLogFiles = value;
    reset();
  }

  /**
   * Returns the number of logs to use.
   *
   * @return		the number of log files
   */
  public int getNumLogFiles() {
    return m_NumLogFiles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String numLogFilesTipText() {
    return "The number of log files to use for rotating.";
  }

  /**
   * Logs the (formatted) logging message.
   *
   * @param msg the message to log
   * @return null if successful, otherwise error message
   */
  @Override
  protected String doLogMessage(String msg) {
    int		i;
    List<File> 	logs;
    File 	logNew;
    File 	logOld;

    if (!m_Rotated && (m_NumLogFiles > 1)) {
      m_Rotated = true;
      if (isLoggingEnabled())
        getLogger().info("Rotating logs...");
      logs = new ArrayList<>();
      logs.add(m_OutputFile);
      for (i = 0; i < m_NumLogFiles; i++)
	logs.add(FileUtils.replaceExtension(m_OutputFile, "." + FileUtils.getExtension(m_OutputFile) + "." + (i+1)));
      for (i = logs.size() - 1; i >= 1; i--) {
	logOld = logs.get(i);
	logNew = logs.get(i - 1);
	if (logOld.exists())
	  logOld.delete();
	if (logNew.exists()) {
	  try {
	    FileUtils.move(logNew, logOld);
	  }
	  catch (Exception e) {
	    getLogger().log(Level.SEVERE, ": Failed to move log files " + logNew + " -> " + logOld, e);
	  }
	}
      }
    }

    return super.doLogMessage(msg);
  }
}
