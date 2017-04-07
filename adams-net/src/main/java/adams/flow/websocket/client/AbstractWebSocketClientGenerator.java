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
 * AbstractWebSocketClientGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.websocket.client;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.base.BaseURI;
import adams.core.option.AbstractOptionHandler;
import com.pusher.java_websocket.client.WebSocketClient;

/**
 * Ancestor for client generators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWebSocketClientGenerator
  extends AbstractOptionHandler
  implements WebSocketClientGenerator, QuickInfoSupporter {

  private static final long serialVersionUID = -4615906058085465471L;

  /** the URL to connect to. */
  protected BaseURI m_URL;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "url", "URL",
      new BaseURI("http://localhost:8000"));
  }

  /**
   * Sets the URL to connect to.
   *
   * @param value	the URL
   */
  public void setURL(BaseURI value) {
    m_URL = value;
    reset();
  }

  /**
   * Returns the URL to connect to.
   *
   * @return 		the URL
   */
  public BaseURI getURL() {
    return m_URL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String URLTipText() {
    return "The URL to connect to.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "URL", m_URL, "URL: ");
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
   * Generates the client.
   *
   * @return		the client
   */
  protected abstract WebSocketClient doGenerate();

  /**
   * Generates the client.
   *
   * @return				the generated client instance
   * @throws IllegalStateException	if checks failed
   * @see				#check()
   */
  @Override
  public WebSocketClient generateClient() {
    String	msg;

    msg = check();
    if (msg != null)
      throw new IllegalStateException(msg);

    return doGenerate();
  }
}
