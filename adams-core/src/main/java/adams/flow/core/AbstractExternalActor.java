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
 * AbstractExternalActor.java
 * Copyright (C) 2009-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.core.io.PlaceholderFile;
import adams.core.io.filechanged.FileChangeMonitor;
import adams.core.io.filechanged.NoChange;
import adams.core.option.UserMode;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeEvent.Type;

/**
 * Ancestor of actors that load another actor from disk and execute it.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractExternalActor
  extends AbstractBaseExternalActor
  implements ExternalActorHandler {

  /** for serialization. */
  private static final long serialVersionUID = 1024129351334661368L;

  /** the file monitor. */
  protected FileChangeMonitor m_Monitor;

  /** whether the flow gets built on the fly and might not be present at the start. */
  protected boolean m_OnTheFly;

  /** the external actor itself. */
  protected Actor m_ExternalActor;

  /** indicates whether a variable is attached to the external file. */
  protected Boolean m_ActorFileIsVariable;

  /** the variable attached to the external file. */
  protected String m_ActorFileVariable;

  /** whether the external actor file has changed. */
  protected boolean m_ActorFileChanged;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "monitor", "monitor",
      new NoChange()).setMinUserMode(UserMode.EXPERT);

    m_OptionManager.add(
      "on-the-fly", "onTheFly",
      false);
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
    result += QuickInfoHelper.toString(this, "monitor", m_Monitor, ", monitor: ");
    result += QuickInfoHelper.toString(this, "onTheFly", m_OnTheFly, "on-the-fly", ", ");

    return result;
  }

  /**
   * Sets the file change monitor for the actor file.
   *
   * @param value	the monitor
   */
  public void setMonitor(FileChangeMonitor value) {
    m_Monitor = value;
    reset();
  }

  /**
   * Returns the file change monitor for the actor file.
   *
   * @return		the monitor
   */
  public FileChangeMonitor getMonitor() {
    return m_Monitor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String monitorTipText() {
    return "The scheme to use for monitoring the actor file for changes.";
  }

  /**
   * Sets whether the flow file gets built on the fly and might not be present
   * at start up time.
   *
   * @param value	if true then the flow does not have to be present at
   * 			start up time
   */
  public void setOnTheFly(boolean value) {
    m_OnTheFly = value;
    reset();
  }

  /**
   * Returns whether the flow file gets built on the fly and might not be present
   * at start up time.
   *
   * @return		true if the flow is not necessarily present at start
   * 			up time
   */
  public boolean getOnTheFly() {
    return m_OnTheFly;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onTheFlyTipText() {
    return
        "If enabled, the external flow is not required to be present at "
      + "set up time (eg if built on the fly), only at execution time.";
  }

  /**
   * Sets the parent of this actor, e.g., the group it belongs to.
   *
   * @param value	the new parent
   */
  @Override
  public void setParent(Actor value) {
    super.setParent(value);
    if (m_ExternalActor != null) {
      m_ExternalActor.setParent(null);
      m_ExternalActor.setParent(this);
    }
  }
  
  /**
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  @Override
  public void variableChanged(VariableChangeEvent e) {
    super.variableChanged(e);

    if (m_ActorFileIsVariable == null) {
      m_ActorFileVariable   = getOptionManager().getVariableForProperty("actorFile");
      m_ActorFileIsVariable = (m_ActorFileVariable != null);
      if (m_ActorFileIsVariable)
	m_ActorFileVariable = Variables.extractName(m_ActorFileVariable);
    }

    if ((m_ActorFileIsVariable) && (e.getName().equals(m_ActorFileVariable))) {
      m_ActorFileChanged = (e.getType() != Type.REMOVED);
      if (isLoggingEnabled())
	getLogger().fine("Actor file changed due to variable");
    }
  }

  /**
   * Returns the external actor.
   *
   * @return		the actor, can be null if not initialized yet or failed
   * 			to initialize
   */
  public Actor getExternalActor() {
    return m_ExternalActor;
  }

  /**
   * Cleans up the external actor.
   */
  public void cleanUpExternalActor() {
    if (m_ActorFileChanged && (m_ExternalActor != null)) {
      m_ExternalActor.wrapUp();
      m_ExternalActor.cleanUp();
      m_ExternalActor = null;
    }
  }

  /**
   * Sets up the external actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String setUpExternalActor() {
    String		result;
    MessageCollection 	errors;
    String		warning;
    PlaceholderFile 	file;

    result = null;

    file = getActualActorFile();
    if (!file.isFile()) {
      result = "'" + file.getAbsolutePath() + "' does not point to a file!";
    }
    else {
      errors = new MessageCollection();
      if (isLoggingEnabled())
	getLogger().fine("Attempting to load actor file: " + file);
      m_ExternalActor = ActorUtils.read(file.getAbsolutePath(), errors);
      if (!errors.isEmpty()) {
	result = "Error loading external actor '" + file.getAbsolutePath() + "':\n" + errors;
      }
      else if (m_ExternalActor == null) {
	result = "Error loading external actor '" + file.getAbsolutePath() + "'!";
      }
      else {
	m_ExternalActor.setParent(this);
	m_ExternalActor.setVariables(getVariables());
	result = m_ExternalActor.setUp();
	if (getErrorHandler() != this)
	  ActorUtils.updateErrorHandler(m_ExternalActor, getErrorHandler(), isLoggingEnabled());
	// make sure we've got the current state of the variables
	if (result == null) {
	  warning = m_ExternalActor.getOptionManager().updateVariableValues(true);
	  if (warning != null)
	    getLogger().severe(
		"Updating variables ('" + getFullName() + "'/'" + file + "') resulted in the following error output "
	    + "(which gets ignored since variables might get initialized later on):\n" + warning);
	}
      }
      if (isLoggingEnabled())
	getLogger().fine("Actor file load/setUp result: " + result);
    }

    m_ActorFileChanged = false;

    return result;
  }
  
  /**
   * Updates the Variables instance in use.
   * <br><br>
   * Use with caution!
   *
   * @param value	the instance to use
   */
  @Override
  protected void forceVariables(Variables value) {
    super.forceVariables(value);

    if (m_ExternalActor != null)
      m_ExternalActor.setVariables(value);
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;

    result = super.setUp();

    if (result == null) {
      // due to change in variable, we might need to clean up external actor
      cleanUpExternalActor();

      if ((getOptionManager().getVariableForProperty("actorFile") == null) && !m_OnTheFly) {
	if (m_ExternalActor == null)
	  result = setUpExternalActor();
      }
    }

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();

    if (m_ExternalActor != null)
      m_ExternalActor.stopExecution();
  }

  /**
   * Gets called in the doExceute() method, after an optional
   * setUpExternalActor() call (in case a variable is used for the actor file),
   * but before the external actor's execute() method is called.
   * <br><br>
   * Default implementation does nothing.
   *
   * @return		null if everything ok, otherwise error message
   * @see		#doExecute()
   * @see		#setUpExternalActor()
   */
  protected String preExecuteExternalActorHook() {
    return null;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    PlaceholderFile	file;

    result = null;

    // file changed?
    file = getActualActorFile();
    if (!m_Monitor.isInitialized(file)) {
      result = m_Monitor.initialize(file);
    }
    else if (m_Monitor.hasChanged(file)) {
      if (isLoggingEnabled())
        getLogger().info("Actor file has changed: " + file);
      m_ActorFileChanged = true;
      result = m_Monitor.update(file);
    }

    // not setup yet due to variable or on-the-fly?
    if (result == null) {
      cleanUpExternalActor();
      if (m_ExternalActor == null)
	result = setUpExternalActor();
    }

    if (result == null)
      result = preExecuteExternalActorHook();

    if (result == null)
      result = m_ExternalActor.execute();

    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_ExternalActor != null)
      m_ExternalActor.wrapUp();

    m_ActorFileIsVariable = null;
    m_ActorFileVariable   = null;
    m_ActorFileChanged    = false;

    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void cleanUp() {
    if (m_ExternalActor != null) {
      m_ExternalActor.destroy();
      m_ExternalActor.setParent(null);
      m_ExternalActor = null;
    }

    super.cleanUp();
  }
}
