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
 * FileHandler.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.core.logging;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.env.Environment;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Sends all logging output to the specified log file.
 * <br>
 * Makes use of the ADAMS_LOGFILE_PREFIX environment variable.
 * E.g., with "ADAMS_LOGFILE_PREFIX=testing-",
 * the default log file "$HOME/.adams/log/console.log"
 * will become "$HOME/.adams/log/testing-console.log".
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FileHandler
  extends AbstractLogHandler {

  /** the environment variable to inject a prefix into the log file. */
  public final static String ADAMS_LOGFILE_PREFIX = "ADAMS_LOGFILE_PREFIX";

  /** the log file to use. */
  protected File m_LogFile;

  /** whether the file points to a directory. */
  protected boolean m_LogIsDir;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    PlaceholderDirectory	logDir;
    String			env;

    super.initialize();

    env = System.getenv(ADAMS_LOGFILE_PREFIX);
    if (env == null)
      env = "";

    logDir = new PlaceholderDirectory(Environment.getInstance().getHome() + File.separator + "log");
    setLogFile(new PlaceholderFile(logDir.getAbsolutePath() + File.separator + env + "console.log"));
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
    File	logDir;

    super.setUp();

    if (m_LogFile != null) {
      logDir = m_LogFile.getAbsoluteFile().getParentFile();
      if (!logDir.exists()) {
	if (!logDir.mkdirs())
	  System.err.println(getClass().getName() + ": Failed to create log directory '" + logDir + "'?");
      }
    }

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
    String	msg;

    if (!m_LogIsDir) {
      msg = LoggingHelper.assembleMessage(record).toString();
      if (!FileUtils.writeToFile(m_LogFile.getAbsolutePath(), msg, true)) {
	m_LogFile.getParentFile().mkdirs();
	FileUtils.writeToFile(m_LogFile.getAbsolutePath(), msg, true);
      }
    }
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
