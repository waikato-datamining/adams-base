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
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.Variables;
import adams.core.io.FlowFile;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeEvent.Type;
import adams.flow.control.FlowStructureModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor of actors that load another actor from disk and execute it.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractExternalActor
  extends AbstractActor
  implements ExternalActorHandler, FlowStructureModifier {

  /** for serialization. */
  private static final long serialVersionUID = 1024129351334661368L;

  /** the file the external actor is stored in. */
  protected FlowFile m_ActorFile;

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
	    "file", "actorFile",
	    new FlowFile("."));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "actorFile", m_ActorFile);
  }

  /**
   * Sets the file containing the external actor.
   *
   * @param value	the actor file
   */
  public void setActorFile(FlowFile value) {
    m_ActorFile = value;
    reset();
  }

  /**
   * Returns the file containing the external actor.
   *
   * @return		the actor file
   */
  public FlowFile getActorFile() {
    return m_ActorFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorFileTipText() {
    return "The file containing the external actor.";
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
    List<String>	errors;
    String		warning;

    result = null;

    if (!m_ActorFile.isFile()) {
      result = "'" + m_ActorFile.getAbsolutePath() + "' does not point to a file!";
    }
    else {
      errors = new ArrayList<>();
      if (isLoggingEnabled())
	getLogger().fine("Attempting to load actor file: " + m_ActorFile);
      m_ExternalActor = ActorUtils.read(m_ActorFile.getAbsolutePath(), errors);
      if (!errors.isEmpty()) {
	result = "Error loading external actor '" + m_ActorFile.getAbsolutePath() + "':\n" + Utils.flatten(errors, "\n");
      }
      else if (m_ExternalActor == null) {
	result = "Error loading external actor '" + m_ActorFile.getAbsolutePath() + "'!";
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
		"Updating variables ('" + getFullName() + "'/'" + m_ActorFile + "') resulted in the following error output "
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

      if (getOptionManager().getVariableForProperty("actorFile") == null) {
	if (m_ExternalActor == null)
	  result = setUpExternalActor();
      }
    }

    return result;
  }

  /**
   * Returns whether the actor is modifying the structure.
   *
   * @return		true if the actor is modifying the structure
   */
  public boolean isModifyingStructure() {
    return !getSkip();
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

    result = null;

    // not setup yet due to variable?
    cleanUpExternalActor();
    if (m_ExternalActor == null)
      result = setUpExternalActor();

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
