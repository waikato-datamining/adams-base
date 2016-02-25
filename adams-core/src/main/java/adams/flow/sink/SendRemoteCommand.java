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
 * SendRemoteCommand.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.flow.core.Unknown;
import adams.scripting.command.FlowAwareRemoteCommand;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.basic.SystemInfo;
import adams.scripting.connection.Connection;
import adams.scripting.connection.DefaultConnection;

/**
 <!-- globalinfo-start -->
 * Executes a remote command.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SendRemoteCommand
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-host &lt;java.lang.String&gt; (property: host)
 * &nbsp;&nbsp;&nbsp;The host to send the command to.
 * &nbsp;&nbsp;&nbsp;default: 127.0.0.1
 * </pre>
 * 
 * <pre>-port &lt;int&gt; (property: port)
 * &nbsp;&nbsp;&nbsp;The port to send the command to.
 * &nbsp;&nbsp;&nbsp;default: 12345
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 65535
 * </pre>
 * 
 * <pre>-command &lt;adams.scripting.command.RemoteCommand&gt; (property: command)
 * &nbsp;&nbsp;&nbsp;The command to send to the remote host.
 * &nbsp;&nbsp;&nbsp;default: adams.scripting.command.basic.SystemInfo
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SendRemoteCommand
  extends AbstractSink {

  /** for serialization. */
  private static final long serialVersionUID = -4210882711380055794L;

  /** the connection. */
  protected Connection m_Connection;

  /** the command to executre. */
  protected RemoteCommand m_Command;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Executes a remote command.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "connection", "connection",
      new DefaultConnection());

    m_OptionManager.add(
      "command", "command",
      new SystemInfo());
  }

  /**
   * Sets the connection to send the command to.
   *
   * @param value	the connection
   */
  public void setConnection(Connection value) {
    m_Connection = value;
    reset();
  }

  /**
   * Returns the connection to send the command to.
   *
   * @return		the connection
   */
  public Connection getConnection() {
    return m_Connection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String connectionTipText() {
    return "The connection to send the command to.";
  }

  /**
   * Sets the command to execute.
   *
   * @param value 	the command
   */
  public void setCommand(RemoteCommand value) {
    m_Command = value;
    reset();
  }

  /**
   * Returns the command to execute.
   *
   * @return 		the command
   */
  public RemoteCommand getCommand() {
    return m_Command;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commandTipText() {
    return "The command to send to the remote host.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "connection", m_Connection, "connection: ");
    result += QuickInfoHelper.toString(this, "command", m_Command, ", command: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    if (m_Command instanceof FlowAwareRemoteCommand)
      ((FlowAwareRemoteCommand) m_Command).setFlowContext(this);
    result = m_Connection.sendRequest(m_Command);

    return result;
  }
}
