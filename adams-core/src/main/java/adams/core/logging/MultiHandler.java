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
 * MultiHandler.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Combines multiple handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiHandler
  extends AbstractLogHandler {

  /** the logging handlers to use. */
  protected Handler[] m_Handlers;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    setHandlers(new Handler[0]);
  }

  /**
   * Sets the handlers to use.
   *
   * @param value	the handlers
   */
  public void setHandlers(Handler[] value) {
    m_Handlers = value;
    reset();
  }

  /**
   * Returns the current handlers.
   *
   * @return		the handlers
   */
  public Handler[] getHandlers() {
    return m_Handlers;
  }

  /**
   * Adds the specified handler.
   *
   * @param value	the handler
   */
  public void addHandler(Handler value) {
    Handler[]		handlers;
    int			i;

    handlers = new Handler[m_Handlers.length + 1];
    for (i = 0; i < m_Handlers.length; i++)
      handlers[i] = m_Handlers[i];
    handlers[handlers.length - 1] = value;

    m_Handlers = handlers;
  }

  /**
   * Removes the specified handler.
   *
   * @param index	the handler index
   */
  public void removeHandler(int index) {
    Handler[]		handlers;
    int			i;
    int			n;

    handlers = new Handler[m_Handlers.length - 1];
    n        = 0;
    for (i = 0; i < m_Handlers.length; i++) {
      if (i == index)
	continue;
      handlers[n] = m_Handlers[i];
      n++;
    }

    m_Handlers = handlers;
  }

  /**
   * Flush any buffered output.
   */
  @Override
  public void flush() {
    super.flush();
    if (m_Handlers != null) {
      for (Handler h : m_Handlers)
	h.flush();
    }
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
    if (m_Handlers != null) {
      for (Handler h : m_Handlers)
	h.close();
    }
    super.close();
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
    for (Handler h: m_Handlers)
      h.publish(record);
  }

  /**
   * Compares the handler with itself.
   *
   * @param o		the other handler
   * @return		less than 0, equal to 0, or greater than 0 if the
   * 			handler is less, equal to, or greater than this one
   */
  public int compareTo(Handler o) {
    int			result;
    MultiHandler	other;
    int			i;

    result = super.compareTo(o);

    if (result == 0) {
      other  = (MultiHandler) o;
      result = new Integer(getHandlers().length).compareTo(other.getHandlers().length);
      if (result == 0) {
	for (i = 0; i < getHandlers().length; i++) {
	  if ((getHandlers()[i] instanceof AbstractLogHandler) && (other.getHandlers()[i] instanceof AbstractLogHandler))
	    result = ((AbstractLogHandler) getHandlers()[i]).compareTo(other.getHandlers()[i]);
	  else
	    result = new Integer(getHandlers()[i].hashCode()).compareTo(other.getHandlers()[i].hashCode());
	  if (result != 0)
	    break;
	}
      }
    }

    return result;
  }
}
