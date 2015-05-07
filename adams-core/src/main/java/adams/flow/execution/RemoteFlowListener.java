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
 * RemoteFlowListener.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import adams.core.option.NestedProducer;
import adams.flow.core.RunnableWithLogging;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

/**
 * Listens on the specified port, returns the currently running flow setup.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteFlowListener
  extends AbstractFlowExecutionListener {

  /** for serialization. */
  private static final long serialVersionUID = -4076279129101243861L;

  /** the default port. */
  public final static int DEFAULT_PORT = 12345;

  /**
   * The runnable that listens to the
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ListenerRunnable
    extends RunnableWithLogging {

    private static final long serialVersionUID = -4092874820780532222L;

    /** the owner. */
    protected RemoteFlowListener m_Owner;

    /** the server socket in use. */
    protected ServerSocket m_ServerSocket;

    /**
     * Initializes the runnable.
     *
     * @param owner   the owning listener
     */
    public ListenerRunnable(RemoteFlowListener owner) {
      super();
      m_Owner = owner;
      setLoggingLevel(m_Owner.getLoggingLevel());
    }

    /**
     * Performs the actual execution.
     */
    @Override
    protected void doRun() {
      Socket          socket;
      PrintWriter     out;
      NestedProducer  producer;

      m_ServerSocket = null;
      try {
        if (isLoggingEnabled())
          getLogger().info("Creating server socket on port " + m_Owner.getPort());
        m_ServerSocket = new ServerSocket(m_Owner.getPort());
        while (m_Running) {
          if (isLoggingEnabled())
            getLogger().info("Waiting for connection...");
          socket   = m_ServerSocket.accept();
          if (isLoggingEnabled())
            getLogger().info("Accepted connection from: " + socket.getInetAddress() + ":" + socket.getPort());
          out      = new PrintWriter(socket.getOutputStream(), true);
          producer = new NestedProducer();
          producer.setOutputClasspath(false);
          producer.produce(m_Owner.getOwner());
          out.write(producer.toString());
          out.close();
          if (isLoggingEnabled())
            getLogger().info("Sent flow to: " + socket.getInetAddress() + ":" + socket.getPort());
          socket.close();
          if (isLoggingEnabled())
            getLogger().info("Closed socket");
        }
      }
      catch (Exception e) {
        getLogger().log(Level.SEVERE, "Error while listening!", e);
      }
      finally {
        stopExecution();
      }
    }

    /**
     * Stops the execution.
     */
    @Override
    public void stopExecution() {
      if (m_ServerSocket != null) {
        try {
          m_ServerSocket.close();
        }
        catch (Exception e) {
          // ignored
        }
      }
      super.stopExecution();
    }
  }

  /** the port to listen on. */
  protected int m_Port;

  /** the runnable in use. */
  protected transient ListenerRunnable m_Runnable;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Listens on the specified port, returns the currently running flow setup.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "port", "port",
	    DEFAULT_PORT, 1, 65535);
  }

  /**
   * Sets the port to listen on.
   *
   * @param value	the port to listen on
   */
  public void setPort(int value) {
    if ((value >= 1) && (value <= 65535)) {
      m_Port = value;
      reset();
    }
    else {
      getLogger().severe("Port must be 1 <= x <= 65535, provided: " + value);
    }
  }

  /**
   * Returns the port to listen on.
   *
   * @return		the port
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String portTipText() {
    return "The port to listen on.";
  }

  /**
   * Gets called when the flow execution starts.
   */
  public void startListening() {
    m_Runnable = new ListenerRunnable(this);
    new Thread(m_Runnable).start();
  }

  /**
   * Gets called when the flow execution ends.
   */
  public void finishListening() {
    m_Runnable.stopExecution();
    m_Runnable = null;
  }
}
