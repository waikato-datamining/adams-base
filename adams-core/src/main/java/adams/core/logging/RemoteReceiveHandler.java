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
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Waits for log records from a remote sender.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteReceiveHandler
  extends AbstractEnhancingSingleHandler {

  /**
   * Ancestor for remote listeners.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static abstract class AbstractRemoteListenerRunnable
    extends RunnableWithLogging {

    private static final long serialVersionUID = 2095617474011098979L;

    /** the default port. */
    public final static int DEFAULT_PORT = 23456;

    /** the default timeout. */
    public final static int DEFAULT_TIMEOUT = 10000;

    /** the port to use. */
    protected int m_Port;

    /** the timeout in msec. */
    protected int m_TimeOut;

    /** the socket in use. */
    protected ServerSocket m_Socket;

    /**
     * Initializes the runnable.
     *
     * @param port	the port to listen on
     * @param timeout	the timeout
     */
    protected AbstractRemoteListenerRunnable(int port, int timeout) {
      super();

      m_Port    = port;
      m_TimeOut = timeout;
    }

    /**
     * Publishes the record.
     *
     * @param record	the record
     */
    protected abstract void publish(LogRecord record);

    /**
     * Hook method before the run is started.
     */
    @Override
    protected void preRun() {
      super.preRun();

      try {
	m_Socket = new ServerSocket(m_Port);
	m_Socket.setSoTimeout(m_TimeOut);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to create server socket on port " + m_Port, e);
	m_Stopped = true;
      }
    }

    /**
     * Listens for records till stopped.
     */
    @Override
    protected void doRun() {
      TByteArrayList 	list;
      Socket 		client;
      int 		data;
      LogRecord 	record;

      list = new TByteArrayList();
      while (!m_Socket.isClosed() && !m_Stopped) {
	// read data
	list.clear();
	try {
	  client = m_Socket.accept();
	  while ((data = client.getInputStream().read()) != -1)
	    list.add((byte) data);
	}
	catch (SocketTimeoutException e) {
	  // ignored
	  continue;
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to accept client connection!", e);
	}
	// extract log record
	if (list.size() > 0) {
	  try {
	    record = (LogRecord) SerializationHelper.fromByteArray(list.toArray())[0];
	    publish(record);
	  }
	  catch (Exception e) {
	    getLogger().log(Level.SEVERE, "Failed to extract LogRecord from binary data!", e);
	  }
	}
      }
    }

    /**
     * Stops the execution.
     */
    @Override
    public void stopExecution() {
      super.stopExecution();
      if ((m_Socket != null) && !m_Socket.isClosed()) {
	try {
	  m_Socket.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }
  }

  /**
   * Publishes the logging records using the supplied handler.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class RemoteListenerRunnableUsingHandler
    extends AbstractRemoteListenerRunnable {

    private static final long serialVersionUID = -3339471481050533817L;

    /** the handler to use for publishing. */
    protected Handler m_Handler;

    /**
     * Initializes the runnable.
     *
     * @param port    	the port to listen on
     * @param timeout 	the timeout
     * @param handler	the handler
     */
    protected RemoteListenerRunnableUsingHandler(int port, int timeout, Handler handler) {
      super(port, timeout);
      m_Handler = handler;
    }

    /**
     * Publishes the record.
     *
     * @param record	the record
     */
    @Override
    protected void publish(LogRecord record) {
      if (m_Handler != null)
	m_Handler.publish(record);
    }
  }

  /** the port to listen to. */
  protected int m_Port;

  /** the timeout to use (msec). */
  protected int m_TimeOut;

  /** the runnable in use. */
  protected AbstractRemoteListenerRunnable m_Runnable;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Runnable = null;

    setPort(AbstractRemoteListenerRunnable.DEFAULT_PORT);
    setTimeOut(AbstractRemoteListenerRunnable.DEFAULT_TIMEOUT);
  }

  /**
   * Returns the default handler.
   *
   * @return		the default
   */
  protected Handler getDefaultHandler() {
    return new SimpleConsoleHandler();
  }

  /**
   * Sets the port to listen for data.
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
      reset();
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

    super.close();
  }

  /**
   * Tries to start listing to the port.
   *
   * @return		true if successfully started
   */
  public boolean startListening() {
    m_Runnable = new RemoteListenerRunnableUsingHandler(m_Port, m_TimeOut, this);
    new Thread(m_Runnable).start();
    return true;
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
   * Compares the handler with itself.
   *
   * @param o		the other handler
   * @return		less than 0, equal to 0, or greater than 0 if the
   * 			handler is less, equal to, or greater than this one
   */
  public int compareTo(Handler o) {
    int				result;
    RemoteReceiveHandler	other;

    result = super.compareTo(o);

    if (result == 0) {
      other  = (RemoteReceiveHandler) o;
      result = new Integer(getPort()).compareTo(other.getPort());
    }

    return result;
  }

  /**
   * Just for testing.
   *
   * @param args	optionally supplying port and timeout
   */
  public static void main(String[] args) {
    int port = AbstractRemoteListenerRunnable.DEFAULT_PORT;
    if (args.length > 0)
      port = Integer.parseInt(args[0]);
    int timeout = AbstractRemoteListenerRunnable.DEFAULT_TIMEOUT;
    if (args.length > 1)
      timeout = Integer.parseInt(args[1]);
    RemoteReceiveHandler handler = new RemoteReceiveHandler();
    handler.setPort(port);
    handler.setTimeOut(timeout);
    handler.setHandler(new SimpleConsoleHandler());
    handler.startListening();
  }
}
