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

/*
 * Rserve.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import java.util.logging.Level;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import adams.core.QuickInfoHelper;
import adams.core.RProjectHelper;
import adams.core.Utils;
import adams.core.management.LoggingObjectOutputPrinter;
import adams.core.management.OS;
import adams.core.management.OutputProcessStream;

/**
 <!-- globalinfo-start -->
 * Establishes a connection to the Rserve server.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Rserve
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-host &lt;java.lang.String&gt; (property: host)
 * &nbsp;&nbsp;&nbsp;The host the Rserve server is running on.
 * &nbsp;&nbsp;&nbsp;default: localhost
 * </pre>
 * 
 * <pre>-port &lt;int&gt; (property: port)
 * &nbsp;&nbsp;&nbsp;The port the Rserve server is listening on.
 * &nbsp;&nbsp;&nbsp;default: 6311
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 65535
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Rserve
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = 3708758302541394633L;

  /** the Rserve host. */
  protected String m_Host;
  
  /** the Rserve port. */
  protected int m_Port;

  /** the connection in use. */
  protected RConnection m_Connection;

  /** whether we need to shutdown the server. */
  protected boolean m_Shutdown;
  
  /**
   * Overall description of this flow.
   */
  @Override
  public String globalInfo() {
    return "Establishes a connection to the Rserve server.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"host", "host", 
	RProjectHelper.getSingleton().getRserveHost());

    m_OptionManager.add(
	"port", "port", 
	RProjectHelper.getSingleton().getRservePort(), 0, 65535);
  }

  /**
   * Sets the Rserve host.
   * 
   * @param value	the host
   */
  public void setHost(String value) {
    m_Host = value;
    reset();
  }

  /**
   * Returns the Rserve host.
   * 
   * @return 		the host
   */
  public String getHost() {
    return m_Host;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hostTipText() {
    return "The host the Rserve server is running on.";
  }

  /**
   * Sets the Rserve port.
   * 
   * @param value	the port
   */
  public void setPort(int value) {
    m_Port = value;
    reset();
  }

  /**
   * Returns the Rserve port.
   * 
   * @return 		the port
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
    return "The port the Rserve server is listening on.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String 	result;

    result  = QuickInfoHelper.toString(this, "host", m_Host, "host: ");
    result += QuickInfoHelper.toString(this, "port", m_Port, ", port: ");

    return result;
  }

  /**
   * Returns a new connection to the Rserve server.
   * 
   * @return		the connection, null if failed to create connection
   */
  public RConnection newConnection() {
    try {
      return new RConnection(m_Host, m_Port);
    }
    catch (Exception e) {
      handleException("Failed to create new connection (" + m_Host + ":" + m_Port + ")", e);
      return null;
    }
  }
  
  /**
   * Closes the connection to Rserve properly.
   * 
   * @param conn	the connection to close
   */
  public void closeConnection(RConnection conn) {
    if (conn != null) {
      if (m_Shutdown) {
	try {
	  getLogger().info("Shutting down Rserve");
	  conn.shutdown();
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Error shutting down Rserve server", e);
	}
      }
      conn.close();
    }
  }
  
  /**
   * Connects to Rserve and feeds it the script.
   */
  @Override
  protected String doExecute() {
    String 		result;
    String[] 		cmd;
    int 		exitValue;
    int			i;
    Process		process;
    OutputProcessStream	stdout;
    OutputProcessStream	stderr;
    boolean		connected;

    result = super.setUp();

    if (result == null) {
      m_Shutdown   = false;
      m_Connection = null;
      connected    = false;
      
      try {
	getLogger().info("Trying to connect to existing Rserve (" + m_Host + ":" + m_Port + ")");
	m_Connection = new RConnection(m_Host, m_Port);
	if (!m_Connection.isConnected())
	  throw new RserveException(m_Connection, "is not connected (" + m_Host + ":" + m_Port + ")");
	connected = m_Connection.isConnected();
	getLogger().info("Running Rserve found (" + m_Host + ":" + m_Port + ")");
      }
      catch (RserveException e) {
	getLogger().info("No running Rserve found (" + m_Host + ":" + m_Port + ")");
	cmd = new String[]{
	    RProjectHelper.getSingleton().getRExecutable().getAbsolutePath(),
	    "-e",
	    "library(Rserve);Rserve(FALSE,args='--no-save --slave --RS-port " + m_Port + "')",
	    "--no-save",
	    "--slave"
	};
	
	try {
	  getLogger().info("Starting Rserve on port " + m_Port + ": " + Utils.flatten(cmd, " "));
	  process = Runtime.getRuntime().exec(cmd);
	  stdout  = new OutputProcessStream(process, LoggingObjectOutputPrinter.class, true);
	  stderr  = new OutputProcessStream(process, LoggingObjectOutputPrinter.class, false);
	  ((LoggingObjectOutputPrinter) stdout.getPrinter()).setOwner(this);
	  ((LoggingObjectOutputPrinter) stderr.getPrinter()).setOwner(this);
	  new Thread(stdout).start();
	  new Thread(stderr).start();
	  
	  if (!OS.isWindows()) {
	    getLogger().info("Not Windows");
	    exitValue = process.waitFor();
	    getLogger().info("Exit code: " + exitValue);
	    if (exitValue == 0) {
	      m_Connection = new RConnection(m_Host, m_Port);
	      connected    = m_Connection.isConnected();
	      m_Shutdown   = true;
	    }
	  }
	  else {
	    getLogger().info("Windows");
	    // waitFor never stops!
	    connected = false;
	    for (i = 0; i < 5; i++) {
	      getLogger().info("Waiting for Rserve to become available #" + (i+1));
	      if (m_Stopped)
		break;
	      try {
		synchronized(this) {
		  wait(500);
		  m_Connection = new RConnection(m_Host, m_Port);
		  connected    = m_Connection.isConnected();
		  m_Shutdown   = true;
		}
		if (connected)
		  break;
	      }
	      catch (Exception ex) {
		connected = false;
		getLogger().log(Level.SEVERE, "Attempt #" + (i+1) + " for Rserve to become available:", ex);
	      }
	    }
	  }

	  if (!connected)
	    result = "Rserve may not be installed properly!\n" 
		+ "Command-line used: " + Utils.flatten(cmd, " ");
	}
	catch (Exception e2) {
	  result = handleException("Failed to start Rserve server!", e2);
	}
      }
    }
    
    // clean up connection
    if (m_Connection != null) {
      m_Connection.close();
      m_Connection = null;
    }

    return result;
  }
}
