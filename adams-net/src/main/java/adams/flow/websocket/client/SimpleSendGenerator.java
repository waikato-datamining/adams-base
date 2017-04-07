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
 * SimpleSendGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.websocket.client;

import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.flow.websocket.server.EchoServer;
import com.pusher.java_websocket.client.WebSocketClient;

/**
 * Generates the SimpleSend client.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleSendGenerator
  extends AbstractWebSocketClientGenerator {

  private static final long serialVersionUID = -1431160448984414483L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates the " + SimpleSend.class.getName() + " client.";
  }

  /**
   * Generates the client.
   *
   * @return		the client
   */
  @Override
  protected WebSocketClient doGenerate() {
    Logger logger;

    logger = LoggingHelper.getLogger(EchoServer.class);
    logger.setLevel(getLoggingLevel().getLevel());

    return new SimpleSend(m_URL.uriValue(), logger);
  }
}
