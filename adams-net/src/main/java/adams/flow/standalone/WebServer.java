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
 * WebServer.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.flow.standalone.webserver.DefaultHandler;
import adams.flow.standalone.webserver.Handler;
import adams.flow.standalone.webserver.ResourceHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;

/**
 <!-- globalinfo-start -->
 * Provides a simple web server using an embedded jetty server.<br>
 * Depending on the used handlers, it can do more than just serve static files.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WebServer
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-port &lt;int&gt; (property: port)
 * &nbsp;&nbsp;&nbsp;The port to use.
 * &nbsp;&nbsp;&nbsp;default: 8080
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 65536
 * </pre>
 * 
 * <pre>-handler &lt;adams.flow.standalone.webserver.Handler&gt; [-handler ...] (property: handlers)
 * &nbsp;&nbsp;&nbsp;The handlers to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.webserver.ResourceHandler, adams.flow.standalone.webserver.DefaultHandler
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WebServer
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = 3822479609541053446L;

  /** the port the server is running on. */
  protected int m_Port;
  
  /** the handlers to use. */
  protected Handler[] m_Handlers;
  
  /** the jetty server. */
  protected Server m_Server;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Provides a simple web server using an embedded jetty server.\n"
	+ "Depending on the used handlers, it can do more than just serve static files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "port", "port",
	    8080, 1, 65536);

    m_OptionManager.add(
	    "handler", "handlers",
	    getDefaultHandlers());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "port", m_Port, "port: ");
  }

  /**
   * Sets the SMTP port to use.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    if ((value >= 1) && (value <= 65536)) {
      m_Port = value;
      reset();
    }
    else {
      getLogger().severe("Port has to satisfy 1<=x<=65536, provided: " + value);
    }
  }

  /**
   * Returns the SMTP port in use.
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
   * 			displaying in the GUI or for listing the options.
   */
  public String portTipText() {
    return "The port to use.";
  }

  /**
   * Returns the default handlers.
   * 
   * @return		the handlers
   */
  protected Handler[] getDefaultHandlers() {
    return new Handler[]{new ResourceHandler(), new DefaultHandler()};
  }
  
  /**
   * Sets the handlers to use.
   *
   * @param value	the handlers
   */
  public void setHandlers(Handler[] value) {
    m_Handlers = value;
    reset();
  }

  /**
   * Returns the handlers in use.
   *
   * @return		the handlers
   */
  public Handler[] getHandlers() {
    return m_Handlers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String handlersTipText() {
    return "The handlers to use.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    ServerConnector 			connector;
    HandlerList 			handlers;
    int					i;
    org.eclipse.jetty.server.Handler[]	list;
    
    result = null;

    m_Server  = new Server();
    connector = new ServerConnector(m_Server);
    connector.setPort(m_Port);
    m_Server.setConnectors(new Connector[]{connector});

    list = new org.eclipse.jetty.server.Handler[m_Handlers.length];
    for (i = 0; i < m_Handlers.length; i++)
      list[i] = m_Handlers[i].configureHandler();
    
    handlers = new HandlerList();
    handlers.setHandlers(list);
    m_Server.setHandler(handlers);

    try {
      m_Server.start();
    }
    catch (Exception e) {
      result = handleException("Failed to start jetty server", e);
      m_Server = null;
    }
    
    return result;
  }
  
  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Server != null) {
      try {
	m_Server.stop();
      }
      catch (Exception e) {
	// ignored
      }
      m_Server = null;
    }
    
    super.stopExecution();
  }
}
