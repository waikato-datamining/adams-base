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
 * H2RemoteAccess.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import org.h2.tools.Server;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Instantiates a TCP server allowing other processes to connect to the H2 database of this process using:<br>
 * jdbc:h2:tcp:&#47;&#47;&lt;host&gt;:&lt;port&gt;&#47;&lt;database&gt;<br>
 * E.g., if the main process is using this JDBC URL:<br>
 * jdbc:h2:mem:1;DB_CLOSE_DELAY=-1;NON_KEYWORDS=VALUE<br>
 * Then it can be accessed remotely with this JDBC URL:<br>
 * jdbc:h2:tcp:&#47;&#47;&lt;host&gt;:&lt;port&gt;&#47;mem:1;NON_KEYWORDS=VALUE
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: H2RemoteAccess
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-port &lt;int&gt; (property: port)
 * &nbsp;&nbsp;&nbsp;The port to listen on.
 * &nbsp;&nbsp;&nbsp;default: 9092
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 65535
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class H2RemoteAccess
  extends AbstractStandalone {

  private static final long serialVersionUID = -6393974246995458857L;

  /** the port to listen on. */
  protected int m_Port;

  /** the server instance. */
  protected transient Server m_Server;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Instantiates a TCP server allowing other processes to connect to the H2 database of this process using:\n"
	     + "jdbc:h2:tcp://<host>:<port>/<database>\n"
	     + "E.g., if the main process is using this JDBC URL:\n"
	     + "jdbc:h2:mem:1;DB_CLOSE_DELAY=-1;NON_KEYWORDS=VALUE\n"
	     + "Then it can be accessed remotely with this JDBC URL:\n"
	     + "jdbc:h2:tcp://<host>:<port>/mem:1;NON_KEYWORDS=VALUE";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "port", "port",
      9092, 1, 65535);
  }

  /**
   * Sets the port to listen on.
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
   * Returns the port to listen on.
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
    return "The port to listen on.";
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
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    List<String> 	args;

    result = null;

    args = new ArrayList<>();
    args.add("-tcpPort");
    args.add("" + m_Port);
    args.add("-tcpAllowOthers");
    args.add("-tcpDaemon");

    try {
      m_Server = Server.createTcpServer(args.toArray(new String[0]));
      m_Server.start();
      System.out.println(m_Server.getURL());
      getLogger().info("Allowing access to H2 databases via: jdbc:h2:tcp://<host>:" + m_Port + "/<database>");
    }
    catch (Exception e) {
      result = handleException("Failed to start H2 TCP server!", e);
    }

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Server != null) {
      m_Server.stop();
      m_Server = null;
    }
    super.stopExecution();
  }
}
