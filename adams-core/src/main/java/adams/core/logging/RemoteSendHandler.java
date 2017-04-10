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
 * RemoteSendHandler.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.logging;

import adams.core.SerializationHelper;
import adams.core.logging.RemoteReceiveHandler.AbstractRemoteListenerRunnable;

import java.net.Socket;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Sends the log records to the specified host/port.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteSendHandler
  extends AbstractLogHandler {

  /** the default host. */
  public final static String DEFAULT_HOSTNAME = "127.0.0.1";

  /** the default port. */
  public final static int DEFAULT_PORT = AbstractRemoteListenerRunnable.DEFAULT_PORT;

  /** the default maximum number of send failures to accept. */
  public final static int DEFAULT_MAXFAILURES = -1;

  /** the maximum number of times doubling up the attempt interval (7: 2^7 = 64s). */
  public final static int MAX_DOUBLE_ATTEMPT_INTERVAL = 7;

  /** the remote host. */
  protected String m_Hostname;

  /** the remote port. */
  protected int m_Port;

  /** the maximum number of failures to accept before removing itself (-1 for infinite). */
  protected int m_MaxFailures;

  /** the socket in use. */
  protected Socket m_Socket;

  /** the number of times the connection has failed. */
  protected int m_ConnectionFailed;

  /** the last connection failure timestamp (msec). */
  protected long m_LastFailureTimestamp;

  /** the timestamp when next to attempt a connect. */
  protected long m_NextAttemptTimestamp;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Socket               = null;
    m_ConnectionFailed     = 0;
    m_LastFailureTimestamp = 0;
    m_NextAttemptTimestamp = 0;

    setHostname(DEFAULT_HOSTNAME);
    setPort(DEFAULT_PORT);
    setMaxFailures(DEFAULT_MAXFAILURES);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    m_ConnectionFailed     = 0;
    m_LastFailureTimestamp = 0;
    m_NextAttemptTimestamp = 0;
  }

  /**
   * Sets the host to send the logging data to.
   *
   * @param value	the host
   */
  public void setHostname(String value) {
    m_Hostname = value;
    reset();
  }

  /**
   * Returns the host to send the logging data to.
   *
   * @return		the host
   */
  public String getHostname() {
    return m_Hostname;
  }

  /**
   * Sets the port to send the logging data to.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    if ((value >= 1) && (value < 65536)) {
      m_Port = value;
      reset();
    }
  }

  /**
   * Returns the port to send the logging data to.
   *
   * @return		the port
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Sets the maximum number of failures to accept.
   *
   * @param value	the maximum, -1 for infinite tries
   */
  public void setMaxFailures(int value) {
    if (value >= -1) {
      m_MaxFailures = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of failures to accept.
   *
   * @return		the maximum, -1 for infinite tries
   */
  public int getMaxFailures() {
    return m_MaxFailures;
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
    if (m_Socket != null) {
      try {
	m_Socket.close();
      }
      catch (Exception e) {
	// ignored
      }
      m_Socket = null;
    }
    m_ConnectionFailed     = 0;
    m_LastFailureTimestamp = 0;
    m_NextAttemptTimestamp = 0;
    super.close();
  }

  /**
   * Tries to open the socket.
   */
  protected void open() {
    int		diff;

    try {
      m_Socket               = new Socket(m_Hostname, m_Port);
      m_ConnectionFailed     = 0;
      m_LastFailureTimestamp = 0;
      m_NextAttemptTimestamp = 0;
    }
    catch (Exception e) {
      System.err.println(getClass().getName() + ": failed to connect to " + m_Hostname + "/" + m_Port);
      e.printStackTrace();
      m_ConnectionFailed++;
      if ((m_MaxFailures > 0) && (m_ConnectionFailed >= m_MaxFailures)) {
	System.err.println("Too many failed attempts (" + m_ConnectionFailed + "), removing handler for sending log data!");
	LoggingHelper.removeFromDefaultHandler(this);
      }
      else{
	diff = 1000 * (int) Math.pow(2, Math.min(MAX_DOUBLE_ATTEMPT_INTERVAL, m_ConnectionFailed - 1));
	System.err.println("Attempting again in " + diff + "msec");
	m_LastFailureTimestamp = System.currentTimeMillis();
	m_NextAttemptTimestamp = m_LastFailureTimestamp + diff;
      }
    }
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
    if ((m_ConnectionFailed == 0) || (System.currentTimeMillis() >= m_NextAttemptTimestamp))
      open();
    else
      return;

    if (m_Socket == null)
      return;

    try {
      m_Socket.getOutputStream().write(SerializationHelper.toByteArray(record));
      m_Socket.getOutputStream().flush();
      m_Socket.close();
      m_Socket = null;
    }
    catch (Exception e) {
      System.err.println(getClass().getName() + ": failed to send log record to " + m_Hostname + "/" + m_Port);
      e.printStackTrace();
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
    int			result;
    RemoteSendHandler	other;

    result = super.compareTo(o);

    if (result == 0) {
      other  = (RemoteSendHandler) o;
      result = getHostname().compareTo(other.getHostname());
      if (result == 0)
	result = new Integer(getPort()).compareTo(other.getPort());
    }

    return result;
  }

  /**
   * Just for testing.
   *
   * @param args	optionally supply host and port
   */
  public static void main(String[] args) {
    String host = DEFAULT_HOSTNAME;
    if (args.length > 0)
      host = args[0];
    int port = DEFAULT_PORT;
    if (args.length > 1)
      port = Integer.parseInt(args[1]);
    RemoteSendHandler handler = new RemoteSendHandler();
    handler.setHostname(host);
    handler.setPort(port);
    LogRecord record;
    record = new LogRecord(Level.INFO, "Just a simple info message");
    handler.publish(record);
    record = new LogRecord(Level.SEVERE, "A severe error");
    handler.publish(record);
  }
}
