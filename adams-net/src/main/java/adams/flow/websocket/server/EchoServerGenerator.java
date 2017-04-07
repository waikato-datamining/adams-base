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
 * EchoServerGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.websocket.server;

import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import com.pusher.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * Generates a simple echo server.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EchoServerGenerator
  extends AbstractWebSocketServerGenerator {

  private static final long serialVersionUID = -3871912816817034765L;

  /** the port to bind to. */
  protected int m_Port;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a simple echo server which just sends the messages back to the client.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "port", "port",
      8000, 1, 65535);
  }

  /**
   * Sets the port to connect to.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    if (getOptionManager().isValid("port", value)) {
      m_Port = value;
      reset();
    }
  }

  /**
   * Returns the port to connect to.
   *
   * @return 		the port
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String portTipText() {
    return "The port to connect to.";
  }

  /**
   * Generates the server.
   *
   * @return		the server
   */
  @Override
  protected WebSocketServer doGenerate() {
    Logger 	logger;

    logger = LoggingHelper.getLogger(EchoServer.class);
    logger.setLevel(getLoggingLevel().getLevel());

    return new EchoServer(new InetSocketAddress(m_Port), logger);
  }
}
