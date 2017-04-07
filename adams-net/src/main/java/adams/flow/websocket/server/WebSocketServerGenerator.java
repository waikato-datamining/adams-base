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
 * WebSocketServerGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.websocket.server;

import adams.core.option.OptionHandler;
import com.pusher.java_websocket.server.WebSocketServer;

/**
 * Interface for server generators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface WebSocketServerGenerator
  extends OptionHandler {

  /**
   * Generates the server.
   *
   * @return				the generated server instance
   * @throws IllegalStateException	if checks failed
   */
  public WebSocketServer generateServer();
}
