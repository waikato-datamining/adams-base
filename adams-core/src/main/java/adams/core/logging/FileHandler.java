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
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.core.logging;

import adams.core.Stoppable;
import adams.core.StoppableWithFeedback;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.management.EnvVar;
import adams.env.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
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
  extends AbstractLogHandler
  implements Stoppable {

  /** the environment variable to inject a prefix into the log file. */
  public final static String ADAMS_LOGFILE_PREFIX = "ADAMS_LOGFILE_PREFIX";

  /**
   * Runnable for doing the actual writing.
   */
  public static class DelayedPublishRunnable
    implements Runnable, StoppableWithFeedback {

    /** the file to write to. */
    protected File m_LogFile;

    /** whether the writing has been stopped. */
    protected boolean m_Stopped;

    /** the queue of log records to output. */
    protected BlockingQueue<LogRecord> m_Queue;

    /**
     * Initializes the runnable.
     *
     * @param logFile	the file to write to
     */
    public DelayedPublishRunnable(File logFile) {
      m_LogFile = logFile;
      m_Queue   = new ArrayBlockingQueue<>(65535);
      m_Stopped = false;
    }

    /**
     * Writes the record to disk.
     *
     * @param record	the record to write
     */
    protected void doPublish(LogRecord record) {
      String	msg;

      msg = LoggingHelper.assembleMessage(record).toString();
      if (!FileUtils.writeToFile(m_LogFile.getAbsolutePath(), msg, true)) {
	m_LogFile.getParentFile().mkdirs();
	FileUtils.writeToFile(m_LogFile.getAbsolutePath(), msg, true);
      }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
      LogRecord		record;

      m_Queue.clear();
      m_Stopped = false;
      while (!m_Stopped) {
	try {
	  record = m_Queue.poll(100, TimeUnit.MILLISECONDS);
	  if (record != null)
	    doPublish(record);
	}
	catch (Exception e) {
	  // pass
	}
      }
    }

    /**
     * Adds the record to write to disk.
     *
     * @param record	the record to write out
     */
    public void publish(LogRecord record) {
      m_Queue.add(record);
    }

    /**
     * Stops the execution.
     */
    @Override
    public void stopExecution() {
      m_Stopped = true;
    }

    /**
     * Whether the execution has been stopped.
     *
     * @return true if stopped
     */
    @Override
    public boolean isStopped() {
      return m_Stopped;
    }
  }

  /** whether the file has been configured. */
  protected final static Map<File,Boolean> m_Configured = new HashMap<>();

  /** the log file to use. */
  protected File m_LogFile;

  /** whether the file points to a directory. */
  protected boolean m_LogIsDir;

  /** the runnable for writing the log records. */
  protected DelayedPublishRunnable m_Runnable;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    PlaceholderDirectory	logDir;
    String			env;

    super.initialize();

    env    = EnvVar.get(ADAMS_LOGFILE_PREFIX, "");
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
   * Returns whether the handler has been initialized.
   *
   * @return		true if initialized
   */
  protected boolean isSetUp() {
    if (m_LogFile == null)
      return false;
    synchronized (m_Configured) {
      return m_Configured.getOrDefault(m_LogFile, false);
    }
  }

  /**
   * Hook method for performing setup before processing first log record.
   */
  @Override
  protected void setUp() {
    File	logDir;

    synchronized (m_Configured) {
      super.setUp();

      if (m_LogFile != null) {
	logDir = m_LogFile.getAbsoluteFile().getParentFile();
	if (!logDir.exists()) {
	  if (!logDir.mkdirs())
	    System.err.println(getClass().getName() + ": Failed to create log directory '" + logDir + "'?");
	}
      }

      m_LogIsDir = (m_LogFile == null) || m_LogFile.isDirectory();
      m_Configured.put(m_LogFile, true);
    }
  }

  /**
   * Hook method after the {@link #setUp()} method was called.
   */
  @Override
  protected void postSetUp() {
    super.postSetUp();
    m_Runnable = new DelayedPublishRunnable(m_LogFile);
    new Thread(m_Runnable).start();
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

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    if (m_Runnable != null) {
      m_Runnable.stopExecution();
      m_Runnable = null;
    }
    super.stopExecution();
  }
}
