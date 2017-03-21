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
 * FileHandler.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.core.logging;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Sends all logging output to the specified log file.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7676 $
 */
public class FileHandler
  extends AbstractLogHandler {

  /** the log file to use. */
  protected File m_LogFile;

  /** whether the file points to a directory. */
  protected boolean m_LogIsDir;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    setLogFile(new PlaceholderFile("."));
  }

  /**
   * Sets the log file to use.
   *
   * @param value	the file
   */
  public void setLogFile(File value) {
    m_LogFile  = value;
    reset();
  }

  /**
   * Returns the log file in use.
   *
   * @return		the file
   */
  public File getLogFile() {
    return m_LogFile;
  }

  /**
   * Hook method for performing setup before processing first log record.
   */
  @Override
  protected void setUp() {
    super.setUp();
    m_LogIsDir = (m_LogFile == null) || m_LogFile.isDirectory();
  }

  /**
   * Publish a <tt>LogRecord</tt>.
   * <p>
   * The logging request was made initially to a <tt>Logger</tt> object,
   * which initialized the <tt>LogRecord</tt> and forwarded it here.
   * <p>
   * The <tt>Handler</tt>  is responsible for formatting the message, when and
   * if necessary.  The formatting should include localization.
   *
   * @param  record  description of the log event. A null record is
   *                 silently ignored and is not published
   */
  @Override
  protected void doPublish(LogRecord record) {
    if (!m_LogIsDir)
      FileUtils.writeToFile(m_LogFile.getAbsolutePath(), LoggingHelper.assembleMessage(record).toString(), true);
  }

  /**
   * Compares the handler with itself.
   *
   * @param o		the other handler
   * @return		less than 0, equal to 0, or greater than 0 if the
   * 			handler is less, equal to, or greater than this one
   */
  public int compareTo(Handler o) {
    int		result;
    FileHandler	other;

    result = super.compareTo(o);

    if (result == 0) {
      other  = (FileHandler) o;
      result = getLogFile().compareTo(other.getLogFile());
    }

    return result;
  }
}
