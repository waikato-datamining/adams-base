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
 * Logger.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.logging;

import adams.core.CleanUpHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Simple logger class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Logger
  implements CleanUpHandler {

  /** the name of the logger. */
  protected String m_Name;

  /** the handlers. */
  protected Set<Handler> m_Handlers;

  /** the level. */
  protected Level m_Level;

  /**
   * Initializes the logger with the specified name.
   *
   * @param name	the name
   */
  public Logger(String name) {
    super();

    m_Name     = name;
    m_Handlers = new HashSet<>();
    m_Level    = Level.WARNING;
  }

  /**
   * Returns the name of the logger.
   *
   * @return		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Adds the handler.
   *
   * @param h		the handler
   */
  public synchronized void addHandler(Handler h) {
    m_Handlers.add(h);
  }

  /**
   * Removes the handler.
   *
   * @param h		the handler
   */
  public synchronized void removeHandler(Handler h) {
    m_Handlers.remove(h);
  }

  /**
   * Returns all current handlers.
   *
   * @return		the handlers
   */
  public synchronized Handler[] getHandlers() {
    return m_Handlers.toArray(new Handler[m_Handlers.size()]);
  }

  /**
   * Sets the logging level.
   *
   * @param value	the level
   */
  public void setLevel(Level value) {
    m_Level = value;
  }

  /**
   * Returns the logging level.
   *
   * @return		the level
   */
  public Level getLevel() {
    return m_Level;
  }

  /**
   * Ignored.
   *
   * @param value	ignored
   */
  public void setUseParentHandlers(boolean value) {
    // ignored
  }

  /**
   * Always false.
   *
   * @return		false
   */
  public boolean getUseParentHandlers() {
    return false;
  }

  /**
   * Logs the given record.
   *
   * @param record	the record
   */
  public synchronized void log(LogRecord record) {
    if (m_Level == Level.OFF)
      return;

    if (LoggingHelper.isAtLeast(m_Level, record.getLevel())) {
      record.setLoggerName(getName());
      for (Handler handler : m_Handlers) {
	if (handler.isLoggable(record))
	  handler.publish(record);
      }
    }
  }

  /**
   * Logs a {@link Level#SEVERE} message.
   *
   * @param msg		the message to log
   */
  public void severe(String msg) {
    log(new LogRecord(Level.SEVERE, msg));
  }

  /**
   * Logs a {@link Level#WARNING} message.
   *
   * @param msg		the message to log
   */
  public void warning(String msg) {
    log(new LogRecord(Level.WARNING, msg));
  }

  /**
   * Logs a {@link Level#CONFIG} message.
   *
   * @param msg		the message to log
   */
  public void config(String msg) {
    log(new LogRecord(Level.CONFIG, msg));
  }

  /**
   * Logs a {@link Level#INFO} message.
   *
   * @param msg		the message to log
   */
  public void info(String msg) {
    log(new LogRecord(Level.INFO, msg));
  }

  /**
   * Logs a {@link Level#FINE} message.
   *
   * @param msg		the message to log
   */
  public void fine(String msg) {
    log(new LogRecord(Level.FINE, msg));
  }

  /**
   * Logs a {@link Level#FINER} message.
   *
   * @param msg		the message to log
   */
  public void finer(String msg) {
    log(new LogRecord(Level.FINER, msg));
  }

  /**
   * Logs a {@link Level#FINEST} message.
   *
   * @param msg		the message to log
   */
  public void finest(String msg) {
    log(new LogRecord(Level.FINEST, msg));
  }

  /**
   * Logs the message with the specified level.
   *
   * @param level	the level of the message
   * @param msg		the message to log
   */
  public void log(Level level, String msg) {
    log(new LogRecord(level, msg));
  }

  /**
   * Logs the message with the specified level.
   *
   * @param level	the level of the message
   * @param msg		the message to log
   * @param t 		the exception to log
   */
  public void log(Level level, String msg, Throwable t) {
    LogRecord	record;

    record = new LogRecord(level, msg);
    record.setThrown(t);
    log(record);
  }

  /**
   * Logs the message with the specified level.
   *
   * @param level	the level of the message
   * @param msg		the message to log
   * @param param 	the parameter to log
   */
  public void log(Level level, String msg, Object param) {
    log(level, msg, new Object[]{param});
  }

  /**
   * Logs the message with the specified level.
   *
   * @param level	the level of the message
   * @param msg		the message to log
   * @param params 	the parameters to log
   */
  public void log(Level level, String msg, Object[] params) {
    LogRecord	record;

    record = new LogRecord(level, msg);
    record.setParameters(params);
    log(record);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_Handlers.clear();
  }

  /**
   * Returns a logger for the name.
   *
   * @param name	the name of the logger
   * @return		the logger
   */
  public static synchronized Logger getLogger(String name) {
    return LogManager.getLogManager().getLogger(name);
  }
}
