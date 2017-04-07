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
 * EchoServer.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.websocket.server;

import adams.core.Utils;
import adams.core.logging.Logger;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;
import com.pusher.java_websocket.WebSocket;
import com.pusher.java_websocket.framing.CloseFrame;
import com.pusher.java_websocket.handshake.ClientHandshake;
import com.pusher.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.logging.Level;

/**
 * Just sends the messages back to the client.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EchoServer
  extends WebSocketServer
  implements FlowContextHandler  {

  /** for logging purposes. */
  protected Logger m_Logger;

  /** the flow context. */
  protected Actor m_FlowContext;

  /**
   * Instantiates the server with the given address.
   *
   * @param address	the address to bind to
   */
  public EchoServer(InetSocketAddress address, Logger logger) {
    super(address);
    m_Logger = logger;
  }

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns the logger instance to use.
   *
   * @return		the logger
   */
  protected synchronized Logger getLogger() {
    return m_Logger;
  }

  /**
   * Called after an opening handshake has been performed and the given
   * websocket is ready to be written on.
   */
  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    getLogger().info("onOpen: " + conn.getRemoteSocketAddress());
  }

  /**
   * Called after the websocket connection has been closed.
   *
   * @param code	The codes can be looked up here: {@link CloseFrame}
   * @param reason	Additional information string
   * @param remote	Returns whether or not the closing of the connection was initiated by the remote host.
   */
  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    getLogger().info("onClose: " + conn.getRemoteSocketAddress() + ", code=" + code + ", reason=" + reason + ", remote=" + remote);
  }

  /**
   * Callback for string messages received from the remote host
   */
  @Override
  public void onMessage(WebSocket conn, String message) {
    getLogger().info("onMessage: " + conn.getRemoteSocketAddress() + ", message=" + message);
    conn.send(message);
  }

  /**
   * Callback for binary messages received from the remote host
   */
  public void onMessage( WebSocket conn, ByteBuffer message ) {
    getLogger().info("onMessage: " + conn.getRemoteSocketAddress() + ", message=" + Utils.toHexArray(message.array()));
    conn.send(message);
  }

  /**
   * Called when errors occurs.
   *
   * @param conn	Can be null if there error does not belong to one
   *                    specific websocket. For example if the servers port could not be bound.
   **/
  @Override
  public void onError(WebSocket conn, Exception ex) {
    getLogger().log(Level.SEVERE, "Error occurred", ex);
  }
}
