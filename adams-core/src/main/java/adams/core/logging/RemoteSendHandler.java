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
  public final static int DEFAULT_PORT = RemoteReceiveHandler.DEFAULT_PORT;

  /** the remote host. */
  protected String m_Hostname;

  /** the remote port. */
  protected int m_Port;

  /** the socket in use. */
  protected Socket m_Socket;

  /** whether the connection has failed. */
  protected boolean m_ConnectionFailed;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Socket           = null;
    m_ConnectionFailed = false;

    setHostname(DEFAULT_HOSTNAME);
    setPort(DEFAULT_PORT);
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
    m_ConnectionFailed = false;
    super.close();
  }

  /**
   * Tries to open the socket.
   */
  protected void open() {
    try {
      m_Socket           = new Socket(m_Hostname, m_Port);
      m_ConnectionFailed = false;
    }
    catch (Exception e) {
      System.err.println(getClass().getName() + ": failed to connect to " + m_Hostname + "/" + m_Port);
      e.printStackTrace();
      m_ConnectionFailed = true;
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
    if ((m_Socket == null) && !m_ConnectionFailed)
      open();
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
