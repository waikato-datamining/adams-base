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
 * NewRemoteCommand.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.flow.core.Token;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.basic.SystemInfo;

/**
 <!-- globalinfo-start -->
 * Configures and forwards a remote command.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.scripting.command.RemoteCommand<br>
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
 * &nbsp;&nbsp;&nbsp;default: NewRemoteCommand
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
 * <pre>-command &lt;adams.scripting.command.RemoteCommand&gt; (property: command)
 * &nbsp;&nbsp;&nbsp;The command to create.
 * &nbsp;&nbsp;&nbsp;default: adams.scripting.command.basic.SystemInfo -response-connection adams.scripting.connection.DefaultConnection
 * </pre>
 * 
 * <pre>-request &lt;boolean&gt; (property: request)
 * &nbsp;&nbsp;&nbsp;If enabled, a request instead of a response is created.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NewRemoteCommand
  extends AbstractSimpleSource {

  private static final long serialVersionUID = -8413782615159219659L;

  /** the command to create. */
  protected RemoteCommand m_Command;

  /** whether to create a request or response. */
  protected boolean m_Request;

  @Override
  public String globalInfo() {
    return "Configures and forwards a remote command.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "command", "command",
      new SystemInfo());

    m_OptionManager.add(
      "request", "request",
      true);
  }

  /**
   * Sets the command to create.
   *
   * @param value 	the command
   */
  public void setCommand(RemoteCommand value) {
    m_Command = value;
    reset();
  }

  /**
   * Returns the command to create.
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
    return "The command to create.";
  }

  /**
   * Sets whether to create a request or response.
   *
   * @param value 	true if to create a request
   */
  public void setRequest(boolean value) {
    m_Request = value;
    reset();
  }

  /**
   * Returns whether to create a request or response.
   *
   * @return 		true if to create a request
   */
  public boolean getRequest() {
    return m_Request;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String requestTipText() {
    return "If enabled, a request instead of a response is created.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "command", m_Command, "cmd: ");
    result += QuickInfoHelper.toString(this, "request", (m_Request ? "request" : "response"), ", type: ");

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{RemoteCommand.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    RemoteCommand   cmd;

    cmd = (RemoteCommand) OptionUtils.shallowCopy(m_Command);
    cmd.setRequest(m_Request);
    m_OutputToken = new Token(cmd);

    return null;
  }
}
