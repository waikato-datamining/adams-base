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
 * WebSocketClient.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.flow.core.FlowContextHandler;
import adams.flow.websocket.client.SimpleSendGenerator;
import adams.flow.websocket.client.WebSocketClientGenerator;

import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Sends the incoming data to a websocket server and forwards the received data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;byte[]<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WebSocketClient
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-generator &lt;adams.flow.websocket.client.AbstractWebSocketClientGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The client generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.websocket.client.SimpleSendGenerator
 * </pre>
 * 
 * <pre>-disconnect &lt;boolean&gt; (property: disconnect)
 * &nbsp;&nbsp;&nbsp;If enabled, the connection will get closed immediately after sending the 
 * &nbsp;&nbsp;&nbsp;data.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WebSocketClient
  extends AbstractSink {

  private static final long serialVersionUID = -8723835373836608550L;

  /** the client generator. */
  protected WebSocketClientGenerator m_Generator;

  /** the client instance. */
  protected transient com.pusher.java_websocket.client.WebSocketClient m_Client;

  /** whether to disconnect immediately. */
  protected boolean m_Disconnect;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sends the incoming data to a websocket server and forwards the received data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "generator", "generator",
      new SimpleSendGenerator());

    m_OptionManager.add(
      "disconnect", "disconnect",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Client = null;
  }

  /**
   * Sets the client generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(WebSocketClientGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the client generator to use
   *
   * @return 		the generator
   */
  public WebSocketClientGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The client generator to use.";
  }

  /**
   * Sets whether to immediately disconnect after sending the data.
   *
   * @param value	true if to disconnect immediately
   */
  public void setDisconnect(boolean value) {
    m_Disconnect = value;
    reset();
  }

  /**
   * Returns whether to immediately disconnect after sending the data.
   *
   * @return 		true if to disconnect immediately
   */
  public boolean getDisconnect() {
    return m_Disconnect;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String disconnectTipText() {
    return "If enabled, the connection will get closed immediately after sending the data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "generator", m_Generator);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, byte[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    if (m_Client == null) {
      try {
	m_Client = m_Generator.generateClient();
	if (m_Client instanceof FlowContextHandler)
	  ((FlowContextHandler) m_Client).setFlowContext(this);
      }
      catch (Exception e) {
	result = handleException("Failed to generate websocket client!", e);
      }
    }

    if (result == null) {
      try {
	if (!m_Client.connectBlocking()) {
	  result = "Failed to establish connection!";
	}
	else {
	  if (m_InputToken.getPayload() instanceof String)
	    m_Client.send((String) m_InputToken.getPayload());
	  else
	    m_Client.send((byte[]) m_InputToken.getPayload());

	  // close immediately?
	  if (m_Disconnect) {
	    try {
	      m_Client.closeBlocking();
	    }
	    catch (Exception e) {
	      getLogger().log(Level.SEVERE, "Error closing websocket client!", e);
	    }
	  }
	}
      }
      catch (InterruptedException e) {
	// ignored
      }
      catch (Exception e) {
	result = handleException("Failed to connect/send data!", e);
      }
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    if (m_Client != null) {
      if (m_Client.isOpen()) {
	try {
	  m_Client.closeBlocking();
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Error closing websocket client!", e);
	}
	m_Client = null;
      }
    }
    super.wrapUp();
  }
}
