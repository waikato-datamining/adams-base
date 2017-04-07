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
 * SimpleSendWithQueueGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.websocket.client;

import adams.core.QuickInfoHelper;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.flow.control.StorageName;
import adams.flow.websocket.server.EchoServer;
import com.pusher.java_websocket.client.WebSocketClient;

/**
 * Generates the SimpleSendWithQueue client.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleSendWithQueueGenerator
  extends AbstractWebSocketClientGenerator {

  private static final long serialVersionUID = -1431160448984414483L;

  /** the name of the queue in the internal storage. */
  protected StorageName m_StorageName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates the " + SimpleSendWithQueue.class.getName() + " client.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "storage-name", "storageName",
	    new StorageName("queue"));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "storageName", m_StorageName, ", storage: ");

    return result;
  }

  /**
   * Sets the name for the queue in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the queue in the internal storage.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the queue in the internal storage.";
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

    return new SimpleSendWithQueue(m_URL.uriValue(), logger, m_StorageName);
  }
}
