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
 * AbstractLogHandler.java
 * Copyright (C) 2013-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.core.logging;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Ancestor for log handlers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractLogHandler
  extends Handler
  implements Comparable<Handler> {

  /** the log listeners. */
  protected HashSet<LoggingListener> m_LoggingListeners;

  /** whether the log has been used. */
  protected boolean m_LogUsed;

  /**
   * Initializes the log handler.
   */
  protected AbstractLogHandler() {
    super();
    reset();
    initialize();
  }

  /**
   * Resets the handler.
   */
  protected void reset() {
    m_LogUsed = false;
    flush();
    close();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_LoggingListeners = new HashSet<>();
  }

  /**
   * Hook method for performing setup before processing first log record.
   */
  protected void setUp() {
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
  protected abstract void doPublish(LogRecord record);

  /**
   * Hook method after logging occurred.
   *
   * @param  record  description of the log event. A null record is
   *                 silently ignored and is not published
   */
  protected void postPublish(LogRecord record) {
    m_LogUsed = true;
    notifyLoggingListeners(record);
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
  public void publish(LogRecord record) {
    if (!m_LogUsed)
      setUp();
    doPublish(record);
    postPublish(record);
  }

  /**
   * Flush any buffered output.
   */
  @Override
  public void flush() {
  }

  /**
   * Close the <tt>Handler</tt> and free all associated resources.
   * <p>
   * The close method will perform a <tt>flush</tt> and then close the
   * <tt>Handler</tt>.   After close has been called this <tt>Handler</tt>
   * should no longer be used.  Method calls may either be silently
   * ignored or may throw runtime exceptions.
   *
   * @exception  SecurityException  if a security manager exists and if
   *             the caller does not have <tt>LoggingPermission("control")</tt>.
   */
  @Override
  public void close() throws SecurityException {
  }

  /**
   * Adds the specified logging listener.
   * 
   * @param l		the listener to add
   */
  public void addLoggingListener(LoggingListener l) {
    m_LoggingListeners.add(l);
  }

  /**
   * Removes the specified logging listener.
   * 
   * @param l		the listener to remove
   */
  public void removeLoggingListener(LoggingListener l) {
    m_LoggingListeners.remove(l);
  }

  /**
   * Removes all logging listener.
   */
  public void removeLoggingListeners() {
    m_LoggingListeners.clear();
  }

  /**
   * Returns an iterator over all current logging listeners.
   * 
   * @return		the iterator
   */
  public Iterator<LoggingListener> iterator() {
    return m_LoggingListeners.iterator();
  }

  /**
   * Returns an iterator over all current logging listeners.
   * 
   * @return		the iterator
   */
  public Set<LoggingListener> loggingListeners() {
    return new HashSet<>(m_LoggingListeners);
  }
  
  /**
   * Sends out the log record to its listeners.
   * 
   * @param record	the record to broadcast
   */
  protected void notifyLoggingListeners(LogRecord record) {
    LoggingListener[]	listeners;
    
    listeners = m_LoggingListeners.toArray(new LoggingListener[0]);
    for (LoggingListener l: listeners)
      l.logEventOccurred(this, record);
  }

  /**
   * Compares the handler with itself.
   *
   * @param o		the other handler
   * @return		less than 0, equal to 0, or greater than 0 if the
   * 			handler is less, equal to, or greater than this one
   */
  public int compareTo(Handler o) {
    return getClass().getName().compareTo(o.getClass().getName());
  }

  /**
   * Checks whether the provided object is the same as this handler.
   *
   * @param obj		the object to check
   * @return		true if the same
   * @see		#compareTo(Handler)
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Handler) && (compareTo((Handler) obj) == 0);
  }
}
