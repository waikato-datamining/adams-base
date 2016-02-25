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
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.TempUtils;
import adams.scripting.command.CommandUtils;
import adams.scripting.command.FlowAwareRemoteCommand;
import adams.scripting.command.RemoteCommand;
import adams.scripting.connection.Connection;
import adams.scripting.connection.DefaultConnection;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Sends a command to the remote host defined by the connection settings.<br>
 * Unsuccessful commands can be store on disk to retry later.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
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
 * <pre>-connection &lt;adams.scripting.connection.Connection&gt; (property: connection)
 * &nbsp;&nbsp;&nbsp;The connection to send the command to.
 * &nbsp;&nbsp;&nbsp;default: adams.scripting.connection.DefaultConnection
 * </pre>
 * 
 * <pre>-store-unsuccessful &lt;boolean&gt; (property: storeUnsuccessful)
 * &nbsp;&nbsp;&nbsp;If enabled, unsuccessful remote commands get stored in the specified directory.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-unsuccessful-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: unsuccessfulDir)
 * &nbsp;&nbsp;&nbsp;The directory to store the unsuccessful commands in (if enabled).
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
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

  /** whether to save unsuccessful remote commands to disk. */
  protected boolean m_StoreUnsuccessful;

  /** the directory for the unsuccessful remote commands. */
  protected PlaceholderDirectory m_UnsuccessfulDir;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Sends a command to the remote host defined by the connection settings.\n"
      + "Unsuccessful commands can be store on disk to retry later.";
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
      "store-unsuccessful", "storeUnsuccessful",
      false);

    m_OptionManager.add(
      "unsuccessful-dir", "unsuccessfulDir",
      new PlaceholderDirectory());
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
   * Sets whether to store unsuccessful commands on disk.
   *
   * @param value 	true if to store unsuccessful commands
   */
  public void setStoreUnsuccessful(boolean value) {
    m_StoreUnsuccessful = value;
    reset();
  }

  /**
   * Returns whether to store unsuccessful commands on disk.
   *
   * @return 		true if to store unsuccessful commands
   */
  public boolean getStoreUnsuccessful() {
    return m_StoreUnsuccessful;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storeUnsuccessfulTipText() {
    return "If enabled, unsuccessful remote commands get stored in the specified directory.";
  }

  /**
   * Sets the directory to store the unsuccessful commands ins.
   *
   * @param value 	the directory
   */
  public void setUnsuccessfulDir(PlaceholderDirectory value) {
    m_UnsuccessfulDir = value;
    reset();
  }

  /**
   * Returns the directory to store the unsuccessful commands in.
   *
   * @return 		the directory
   */
  public PlaceholderDirectory getUnsuccessfulDir() {
    return m_UnsuccessfulDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String unsuccessfulDirTipText() {
    return "The directory to store the unsuccessful commands in (if enabled).";
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
    result += QuickInfoHelper.toString(this, "storeUnsuccessful", (m_StoreUnsuccessful ? "store" : "don't store"), ", ");
    result += QuickInfoHelper.toString(this, "unsuccessfulDir", m_UnsuccessfulDir, ", dir: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.scripting.command.RemoteCommand.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{RemoteCommand.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    File		tmp;
    RemoteCommand	cmd;

    cmd = (RemoteCommand) m_InputToken.getPayload();
    if (cmd instanceof FlowAwareRemoteCommand)
      ((FlowAwareRemoteCommand) cmd).setFlowContext(this);

    if (cmd.isRequest())
      result = m_Connection.sendRequest(cmd);
    else
      result = m_Connection.sendResponse(cmd);

    if ((result != null) && m_StoreUnsuccessful) {
      if (!m_UnsuccessfulDir.exists()) {
	result = "Directory for storing unsuccessful commands does not exist: " + m_UnsuccessfulDir;
      }
      else if (!m_UnsuccessfulDir.isDirectory()) {
	result = "Directory supplied for storing unsuccessful commands is not a directory: " + m_UnsuccessfulDir;
      }
      else {
	tmp    = TempUtils.createTempFile(m_UnsuccessfulDir, "remote", ".rc");
	result = FileUtils.writeToFileMsg(tmp.getAbsolutePath(), cmd.assembleRequest(), false, CommandUtils.MESSAGE_CHARSET);
      }
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();
    m_Connection.cleanUp();
  }
}
