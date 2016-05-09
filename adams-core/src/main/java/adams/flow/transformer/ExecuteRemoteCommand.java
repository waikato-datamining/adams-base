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
 * ExecuteRemoteCommand.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Pausable;
import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.event.FlowPauseStateEvent;
import adams.event.FlowPauseStateEvent.Type;
import adams.event.FlowPauseStateListener;
import adams.flow.control.Flow;
import adams.flow.core.Token;
import adams.scripting.command.RemoteCommand;
import adams.scripting.engine.ManualFeedScriptingEngine;
import adams.scripting.permissionhandler.AllowAll;
import adams.scripting.permissionhandler.PermissionHandler;

/**
 <!-- globalinfo-start -->
 * Executes the incoming commands.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.scripting.command.RemoteCommand<br>
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
 * &nbsp;&nbsp;&nbsp;default: ExecuteRemoteCommand
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
 * <pre>-max-commands &lt;int&gt; (property: maxCommands)
 * &nbsp;&nbsp;&nbsp;The maximum number of commands allowed in the queue.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-permission-handler &lt;adams.scripting.permissionhandler.PermissionHandler&gt; (property: permissionHandler)
 * &nbsp;&nbsp;&nbsp;The handler that determines what request can be executed.
 * &nbsp;&nbsp;&nbsp;default: adams.scripting.permissionhandler.AllowAll
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExecuteRemoteCommand
  extends AbstractTransformer
  implements Pausable, FlowPauseStateListener {

  /** for serialization. */
  private static final long serialVersionUID = 7491100983182267771L;

  /** the maximum number of commands to allow in queue. */
  protected int m_MaxCommands;

  /** the permission handler to use. */
  protected PermissionHandler m_PermissionHandler;

  /** the scripting engine in use. */
  protected ManualFeedScriptingEngine m_ScriptingEngine;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the incoming commands.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "max-commands", "maxCommands",
      100, 1, null);

    m_OptionManager.add(
      "permission-handler", "permissionHandler",
      new AllowAll());
  }

  /**
   * Sets the maximum number of commands allowed in the queue.
   *
   * @param value	the maximum
   */
  public void setMaxCommands(int value) {
    if (getOptionManager().isValid("maxCommands", value)) {
      m_MaxCommands = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of commands allowed in the queue.
   *
   * @return		the maximum
   */
  public int getMaxCommands() {
    return m_MaxCommands;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maxCommandsTipText() {
    return "The maximum number of commands allowed in the queue.";
  }

  /**
   * Sets the permission handler to use.
   *
   * @param value	the permission handler
   */
  public void setPermissionHandler(PermissionHandler value) {
    m_PermissionHandler = value;
    reset();
  }

  /**
   * Returns the permission handler in use.
   *
   * @return		the permission handler
   */
  public PermissionHandler getPermissionHandler() {
    return m_PermissionHandler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String permissionHandlerTipText() {
    return "The handler that determines what request can be executed.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "permissionHandler", m_PermissionHandler, "permissions: ");
    result += QuickInfoHelper.toString(this, "maxCommands", m_MaxCommands, ", max: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.scripting.command.RemoteCommand.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{RemoteCommand.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.scripting.command.RemoteCommand.class<!-- flow-generates-end -->
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
    if (m_ScriptingEngine == null) {
      m_ScriptingEngine = new ManualFeedScriptingEngine();
      m_ScriptingEngine.setMaxCommands(m_MaxCommands);
      if (getRoot() instanceof Flow)
	m_ScriptingEngine.setApplicationContext(((Flow) getRoot()).getApplicationFrame());
      m_ScriptingEngine.setPermissionHandler((PermissionHandler) OptionUtils.shallowCopy(m_PermissionHandler));
      new Thread(() -> {
	String msg = m_ScriptingEngine.execute();
	if (msg != null) {
	  getLogger().severe(msg);
	  stopExecution(msg);
	}
      }).start();
    }
    
    m_ScriptingEngine.addCommand((RemoteCommand) m_InputToken.getPayload());
    m_OutputToken = new Token(m_InputToken.getPayload());

    return null;
  }

  /**
   * Gets called when the pause state of the flow changes.
   *
   * @param e		the event
   */
  public void flowPauseStateChanged(FlowPauseStateEvent e) {
    if (e.getType() == Type.PAUSED)
      pauseExecution();
    else
      resumeExecution();
  }

  /**
   * Pauses the execution.
   */
  public void pauseExecution() {
    if (m_ScriptingEngine != null)
      m_ScriptingEngine.pauseExecution();
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  public boolean isPaused() {
    if (m_ScriptingEngine != null)
      return m_ScriptingEngine.isPaused();
    else
      return false;
  }

  /**
   * Resumes the execution.
   */
  public void resumeExecution() {
    if (m_ScriptingEngine != null)
      m_ScriptingEngine.resumeExecution();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();

    if (m_ScriptingEngine != null)
      m_ScriptingEngine.stopExecution();
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    if (m_ScriptingEngine != null) {
      if (!m_ScriptingEngine.isStopped())
	m_ScriptingEngine.stopExecution();
      m_ScriptingEngine = null;
    }
  }
}
