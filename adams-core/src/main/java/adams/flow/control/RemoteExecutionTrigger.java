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
 * RemoteExecutionTrigger.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.VariableName;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.core.GUIHelper;
import adams.scripting.command.flow.RemoteFlowExecution;
import adams.scripting.connection.Connection;
import adams.scripting.connection.DefaultConnection;

import java.awt.Container;

/**
 <!-- globalinfo-start -->
 * Transfers the actors below itself, the specified storage items and variables using the specified connection for remote execution.<br>
 * Uses the adams.scripting.command.flow.RemoteFlowExecution remote command behind the scenes.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
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
 * &nbsp;&nbsp;&nbsp;default: TriggerRemoteExecution
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
 * <pre>-finish-before-stopping &lt;boolean&gt; (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-asynchronous &lt;boolean&gt; (property: asynchronous)
 * &nbsp;&nbsp;&nbsp;If enabled, the sub-actors get executed asynchronously rather than the flow 
 * &nbsp;&nbsp;&nbsp;waiting for them to finish before proceeding with execution.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-tee &lt;adams.flow.core.Actor&gt; [-tee ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to siphon-off the tokens to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; [-storage-name ...] (property: storageNames)
 * &nbsp;&nbsp;&nbsp;The (optional) storage items to transfer.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-variable-name &lt;adams.core.VariableName&gt; [-variable-name ...] (property: variableNames)
 * &nbsp;&nbsp;&nbsp;The (optional) variables to transfer.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-connection &lt;adams.scripting.connection.Connection&gt; (property: connection)
 * &nbsp;&nbsp;&nbsp;Defines how to send the flow for remote execution.
 * &nbsp;&nbsp;&nbsp;default: adams.scripting.connection.DefaultConnection
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteExecutionTrigger
  extends AbstractTee {

  private static final long serialVersionUID = 3640543579873695646L;

  /** the storage items to transmit. */
  protected StorageName[] m_StorageNames;

  /** the variables to transmit. */
  protected VariableName[] m_VariableNames;

  /** where to send the flow to. */
  protected Connection m_Connection;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Transfers the actors below itself, the specified storage items and variables "
	+ "using the specified connection for remote execution.\n"
	+ "Uses the " + RemoteFlowExecution.class.getName() + " remote command behind the scenes.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageNames",
      new StorageName[0]);

    m_OptionManager.add(
      "variable-name", "variableNames",
      new VariableName[0]);

    m_OptionManager.add(
      "connection", "connection",
      new DefaultConnection());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Actors.setAllowStandalones(true);
    m_Actors.setAllowSource(true);
  }

  /**
   * Sets the names of the storage items to transfer.
   *
   * @param value	the storage names
   */
  public void setStorageNames(StorageName[] value) {
    m_StorageNames = value;
    reset();
  }

  /**
   * Returns the names of the storage items to transfer.
   *
   * @return		the storage names
   */
  public StorageName[] getStorageNames() {
    return m_StorageNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String storageNamesTipText() {
    return "The (optional) storage items to transfer.";
  }

  /**
   * Sets the names of the variables to transfer.
   *
   * @param value	the variable names
   */
  public void setVariableNames(VariableName[] value) {
    m_VariableNames = value;
    reset();
  }

  /**
   * Returns the names of the variables to transfer.
   *
   * @return		the variable names
   */
  public VariableName[] getVariableNames() {
    return m_VariableNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String variableNamesTipText() {
    return "The (optional) variables to transfer.";
  }

  /**
   * Sets the connection used for sending the flow.
   *
   * @param value	the connection
   */
  public void setConnection(Connection value) {
    m_Connection = value;
    reset();
  }

  /**
   * Returns the connection used for sending the flow.
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
    return "Defines how to send the flow for remote execution.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    if (result == null)
      result = "";
    else
      result += ", ";
    result += QuickInfoHelper.toString(this, "connection", m_Connection, "connection: ");
    result += QuickInfoHelper.toString(this, "storageNames", (m_StorageNames.length == 0 ? "-none-" : Utils.arrayToString(m_StorageNames)), ", storage: ");
    result += QuickInfoHelper.toString(this, "variableNames", (m_VariableNames.length == 0 ? "-none-" : Utils.arrayToString(m_VariableNames)), ", var: ");

    return result;
  }

  /**
   * Gets called in the setUp() method. Returns null if tee-actors are fine,
   * otherwise error message.
   *
   * @return		always null
   */
  @Override
  protected String setUpTeeActors() {
    return ActorUtils.checkForSource(getActors());
  }

  /**
   * Returns whether singletons are allowed in this group or not.
   *
   * @return		true if singletons are allowed
   */
  public boolean canContainStandalones() {
    return true;
  }

  /**
   * Checks the tee actor before it is set.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param index	the index the actor gets set
   * @param actor	the actor to check
   * @return		always null
   */
  @Override
  protected String checkTeeActor(int index, Actor actor) {
    return null;
  }

  /**
   * Checks the tee actors before they are set via the setTeeActors method.
   * Returns an error message if the actors are not acceptable, null otherwise.
   *
   * @param actors	the actors to check
   * @return		null if checks passed or null in case of an error
   */
  @Override
  protected String checkTeeActors(Actor[] actors) {
    return ActorUtils.checkForSource(actors);
  }

  /**
   * Processes the token.
   *
   * @param token	ignored
   * @return		an optional error message, null if everything OK
   */
  @Override
  protected String processInput(Token token) {
    String		result;
    Flow		flow;
    int			i;
    RemoteFlowExecution	remote;

    try {
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().preExecute(this);

      // assemble remote flow
      flow = new Flow();
      for (i = 0; i < size(); i++)
	flow.add(get(i).shallowCopy());

      // assemble command
      remote = new RemoteFlowExecution();
      if (getParentComponent() instanceof Container)
	remote.setApplicationContext((AbstractApplicationFrame) GUIHelper.getParent((Container) getParentComponent(), AbstractApplicationFrame.class));
      remote.setFlowContext(this);
      remote.setStorageNames(m_StorageNames);
      remote.setVariableNames(m_VariableNames);
      remote.setActor(flow);
      result = m_Connection.sendRequest(remote);

      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().postExecute(this);
    }
    catch (Exception e) {
      result = handleException("Failed to trigger: ", e);
    }

    return result;
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(true, ActorExecution.SEQUENTIAL, false);
  }
}
