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
 * AbstractWebSocketServerGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.websocket.server;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import com.pusher.java_websocket.server.WebSocketServer;

/**
 * Ancestor for server generators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWebSocketServerGenerator
  extends AbstractOptionHandler
  implements WebSocketServerGenerator, QuickInfoSupporter {

  private static final long serialVersionUID = -4615906058085465471L;

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Hook method for performing checks.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if successful, otherwise error message
   */
  protected String check() {
    return null;
  }

  /**
   * Generates the server.
   *
   * @return		the server
   */
  protected abstract WebSocketServer doGenerate();

  /**
   * Generates the server.
   *
   * @return				the generated server instance
   * @throws IllegalStateException	if checks failed
   * @see				#check()
   */
  @Override
  public WebSocketServer generateServer() {
    String	msg;

    msg = check();
    if (msg != null)
      throw new IllegalStateException(msg);

    return doGenerate();
  }
}
