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
 * RemoteReceiveHandler.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.logging;

import adams.core.SerializationHelper;
import adams.flow.core.RunnableWithLogging;
import gnu.trove.list.array.TByteArrayList;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Waits for log records from a remote sender.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteReceiveHandler
  extends AbstractLogHandler {

  /** the port to listen to. */
  protected int m_Port;

  /** the timeout to use (msec). */
  protected int m_TimeOut;

  /** the socket in use. */
  protected ServerSocket m_Socket;

  /** the base handler. */
  protected Handler m_Handler;

  /** the runnable in use. */
  protected RunnableWithLogging m_Runnable;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Port     = 12345;
    m_TimeOut  = 10000;
    m_Socket   = null;
    m_Handler  = new SimpleConsoleHandler();
    m_Runnable = null;
  }

  /**
   * Sets the handler to use for outputting the log records.
   *
   * @param value	the handler
   */
  public void setHandler(Handler value) {
    m_Handler = value;
  }

  /**
   * Returns the handler to use for outputting the log records.
   *
   * @return		the handler
   */
  public Handler getHandler() {
    return m_Handler;
  }

  /**
   * Sets the port to listen for data.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    if ((value >= 1) && (value < 65536)) {
      m_Port = value;
      close();
    }
  }

  /**
   * Returns the port to listen for data.
   *
   * @return		the port
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Sets the timeout (in msec) for accepting connections.
   *
   * @param value	the timeout (in msec)
   */
  public void setTimeOut(int value) {
    if (value >= 1) {
      m_TimeOut = value;
      close();
    }
  }

  /**
   * Returns the timeout (in msec) for accepting connections.
   *
   * @return		the timeout (in msec)
   */
  public int getTimeOut() {
    return m_TimeOut;
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
    if (m_Runnable != null) {
      m_Runnable.stopExecution();
      m_Runnable = null;
    }

    if (m_Socket != null) {
      try {
	m_Socket.close();
      }
      catch (Exception e) {
	// ignored
      }
      m_Socket = null;
    }

    super.close();
  }

  /**
   * Tries to start listing to the port.
   *
   * @return		true if successfully started
   */
  public boolean startListening() {
    try {
      m_Socket = new ServerSocket(m_Port);
      m_Socket.setSoTimeout(m_TimeOut);
      m_Runnable = new RunnableWithLogging() {
	private static final long serialVersionUID = 6546465605007778490L;
	@Override
	protected void doRun() {
	  TByteArrayList list = new TByteArrayList();
	  while (!m_Socket.isClosed() && !m_Stopped) {
	    Socket client;
	    // read data
	    list.clear();
	    try {
	      client = m_Socket.accept();
	      int data;
	      while ((data = client.getInputStream().read()) != -1)
		list.add((byte) data);
	    }
	    catch (SocketTimeoutException e) {
	      // ignored
	      continue;
	    }
	    catch (Exception e) {
	      System.err.println(getClass().getName() + ": failed to accept client connection!");
	      e.printStackTrace();
	    }
	    // extract log record
	    if (list.size() > 0) {
	      try {
		LogRecord record = (LogRecord) SerializationHelper.fromByteArray(list.toArray())[0];
		publish(record);
	      }
	      catch (Exception e) {
		System.err.println(getClass().getName() + ": failed to extract LogRecord from binary data!");
		e.printStackTrace();
	      }
	    }
	  }
	}
      };
      new Thread(m_Runnable).start();
      return true;
    }
    catch (Exception e) {
      System.err.println(getClass().getName() + ": failed to listen to " + m_Port);
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Stops the listening.
   *
   * @see		#close()
   */
  public void stopListening() {
    close();
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
    m_Handler.publish(record);
  }

  /**
   * Just for testing.
   *
   * @param args	optionally supplying port and timeout
   */
  public static void main(String[] args) {
    int port = 12345;
    if (args.length > 0)
      port = Integer.parseInt(args[0]);
    int timeout = 10000;
    if (args.length > 1)
      timeout = Integer.parseInt(args[1]);
    RemoteReceiveHandler handler = new RemoteReceiveHandler();
    handler.setPort(port);
    handler.setTimeOut(timeout);
    handler.setHandler(new SimpleConsoleHandler());
    handler.startListening();
  }
}
