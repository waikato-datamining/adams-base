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
 * AbstractCallableActor.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import java.util.HashSet;
import java.util.Hashtable;

import adams.core.QuickInfoHelper;

/**
 * Abstract ancestor for all actors that access callable actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCallableActor
  extends AbstractActor
  implements CallableActorUser {

  /** for serialization. */
  private static final long serialVersionUID = -6652513967046303107L;

  /** the key for backing up the callable actor. */
  public final static String BACKUP_CALLABLEACTOR = "callable actor";

  /** the key for backing up the configured state. */
  public final static String BACKUP_CONFIGURED = "configured";

  /** the callable name. */
  protected CallableActorReference m_CallableName;

  /** the callable actor. */
  protected AbstractActor m_CallableActor;

  /** whether the callable actor has been configured. */
  protected boolean m_Configured;
  
  /** the helper class. */
  protected CallableActorHelper m_Helper;
  
  /** whether the callable actor is optional. */
  protected boolean m_Optional;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "callable", "callableName",
	    new CallableActorReference("unknown"));

    m_OptionManager.add(
	    "optional", "optional",
	    false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CallableActor = null;
    m_Configured    = false;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Sets the name of the callable actor to use.
   *
   * @param value 	the callable name
   */
  public void setCallableName(CallableActorReference value) {
    m_CallableName = value;
    reset();
  }

  /**
   * Returns the name of the callable actor in use.
   *
   * @return 		the callable name
   */
  public CallableActorReference getCallableName() {
    return m_CallableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String callableNameTipText() {
    return "The name of the callable actor to use.";
  }

  /**
   * Sets whether the callable actor is optional.
   *
   * @param value 	true if optional
   */
  public void setOptional(boolean value) {
    m_Optional = value;
    reset();
  }

  /**
   * Returns whether the callable actor is optional.
   *
   * @return 		true if optional
   */
  public boolean getOptional() {
    return m_Optional;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optionalTipText() {
    return 
	"If enabled, then the callable actor is optional, ie no error is "
	+ "raised if not found, merely ignored.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "callableName", m_CallableName);
    result += QuickInfoHelper.toString(this, "optional", m_Optional, "optional", ", ");
    
    return result;
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected AbstractActor findCallableActor() {
    return m_Helper.findCallableActorRecursive(this, getCallableName());
  }

  /**
   * Checks whether a reference to the callable actor is currently available.
   *
   * @return		true if a reference is available
   * @see		#getCallableActor()
   */
  public boolean hasCallableActor() {
    return (m_CallableActor != null);
  }

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  @Override
  public AbstractActor getCallableActor() {
    return m_CallableActor;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_CALLABLEACTOR);
    pruneBackup(BACKUP_CONFIGURED);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_CallableActor != null)
      result.put(BACKUP_CALLABLEACTOR, m_CallableActor);
    
    result.put(BACKUP_CONFIGURED, m_Configured);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    super.restoreState(state);

    if (state.containsKey(BACKUP_CALLABLEACTOR)) {
      m_CallableActor = (AbstractActor) state.get(BACKUP_CALLABLEACTOR);
      state.remove(BACKUP_CALLABLEACTOR);
    }

    if (state.containsKey(BACKUP_CONFIGURED)) {
      m_Configured = (Boolean) state.get(BACKUP_CONFIGURED);
      state.remove(BACKUP_CONFIGURED);
    }
  }

  /**
   * Configures the callable actor.
   *
   * @return		null if successful, otherwise error message
   */
  protected String setUpCallableActor() {
    String		result;
    HashSet<String>	variables;

    result = null;

    m_CallableActor = findCallableActor();
    m_Configured    = true;
    if (m_CallableActor == null) {
      if (!m_Optional)
	result = "Couldn't find callable actor '" + getCallableName() + "'!";
      else
	getLogger().info("Callable actor '" + getCallableName() + "' not found, ignoring.");
    }
    else {
      variables = findVariables(m_CallableActor);
      m_DetectedVariables.addAll(variables);
      if (m_DetectedVariables.size() > 0)
	getVariables().addVariableChangeListener(this);
      if (getErrorHandler() != this)
	ActorUtils.updateErrorHandler(m_CallableActor, getErrorHandler());
    }

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    String	variable;

    result = super.setUp();

    if (result == null) {
      // do we have to wait till execution time because of attached variable?
      variable = getOptionManager().getVariableForProperty("callableName");
      if (variable == null)
	result = setUpCallableActor();
    }

    return result;
  }

  /**
   * Executes the callable actor. Derived classes might need to override this
   * method to ensure atomicity.
   *
   * @return		null if no error, otherwise error message
   */
  protected abstract String executeCallableActor();

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    // is variable attached?
    if (!m_Configured)
      result = setUpCallableActor();

    if (result == null) {
      if (m_CallableActor != null) {
	if (!m_CallableActor.getSkip() && !m_CallableActor.isStopped()) {
	  synchronized(m_CallableActor) {
	    if (isLoggingEnabled())
	      getLogger().info("Executing callable actor - start: " + m_CallableActor);
	    result = executeCallableActor();
	    if (isLoggingEnabled())
	      getLogger().info("Executing callable actor - end: " + result);
	  }
	}
      }
    }

    return result;
  }

  /**
   * Returns whether the actor has finished.
   *
   * @return		true if finished
   */
  @Override
  public boolean isFinished() {
    if (m_CallableActor == null)
      return true;
    else
      return m_CallableActor.isFinished();
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    try {
      if (m_CallableActor != null) {
	m_CallableActor.notifyAll();
	m_CallableActor.stopExecution();
      }
    }
    catch (Exception e) {
      // ignored
    }

    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_CallableActor != null) {
      synchronized(m_CallableActor) {
	m_CallableActor.wrapUp();
      }
    }

    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    if (m_CallableActor != null) {
      m_CallableActor.cleanUp();
      m_CallableActor = null;
    }
  }
}