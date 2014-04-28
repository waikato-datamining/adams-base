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
 * AbstractCopyCallableActor.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import java.util.HashSet;

import adams.core.QuickInfoHelper;
import adams.core.Variables;

/**
 * Ancestor for actors that use a copy of a callable actor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCopyCallableActor
  extends AbstractActor 
  implements CallableActorUser {

  /** for serialization. */
  private static final long serialVersionUID = -7860206690560690212L;

  /** the callable name. */
  protected CallableActorReference m_CallableName;

  /** the callable actor. */
  protected AbstractActor m_CallableActor;

  /** the helper class. */
  protected CallabledActorHelper m_Helper;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "callable", "callableName",
	    new CallableActorReference("unknown"));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallabledActorHelper();
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "callableName", m_CallableName);
  }

  /**
   * Tries to find the callable actor referenced by its name.
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
   * Updates the Variables instance in use.
   * <p/>
   * Use with caution!
   *
   * @param value	the instance to use
   */
  @Override
  protected void forceVariables(Variables value) {
    super.forceVariables(value);

    if (m_CallableActor != null)
      m_CallableActor.setVariables(value);
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    HashSet<String>	variables;

    result = super.setUp();

    if (result == null) {
      m_CallableActor = findCallableActor();
      if (m_CallableActor == null) {
        result = "Couldn't find callable actor '" + getCallableName() + "'!";
      }
      else {
	m_CallableActor = m_CallableActor.shallowCopy();
	m_CallableActor.setParent(getParent());
	m_CallableActor.setVariables(getVariables());
	result        = m_CallableActor.setUp();
	if (result == null) {
	  variables = findVariables(m_CallableActor);
	  m_DetectedVariables.addAll(variables);
	  if (m_DetectedVariables.size() > 0)
	    getVariables().addVariableChangeListener(this);
	}
      }
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

    if (!m_CallableActor.getSkip() && !m_CallableActor.isStopped())
      result = executeCallableActor();

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
    if (m_CallableActor != null)
      m_CallableActor.stopExecution();

    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_CallableActor != null)
      m_CallableActor.wrapUp();

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
