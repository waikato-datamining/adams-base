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
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.QuickInfoHelper;
import adams.flow.control.FlowStructureModifier;

/**
 * Ancestor for actors that use a copy of a callable actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCopyCallableActor
  extends AbstractActor
  implements FlowStructureModifier {

  /** for serialization. */
  private static final long serialVersionUID = -7860206690560690212L;

  /** the callable name. */
  protected CallableActorReference m_CallableName;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

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
  protected Actor findCallableActor() {
    return m_Helper.findCallableActorRecursive(this, getCallableName());
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
   * Performs checks on the callable actor.
   *
   * @param actor	the actor to check
   * @return		null if OK, otherwise error message
   */
  protected abstract String checkCallableActor(Actor actor);

  /**
   * Configures the callable actor.
   *
   * @return		null if OK, otherwise error message
   */
  protected String setUpCallableActor() {
    String		result;
    Actor 		actor;

    actor = findCallableActor();
    if (actor == null) {
      result = "Couldn't find callable actor '" + getCallableName() + "'!";
    }
    else {
      result = checkCallableActor(actor);
      if (result == null) {
      }
      actor = actor.shallowCopy();
      actor.setParent(getParent());
      actor.setVariables(getVariables());
      result = actor.setUp();
      if (result == null) {
	if (actor.getName().equals(actor.getDefaultName()))
	  actor.setName(getName());
	actor.setVariables(getVariables());
	((ActorHandler) getParent()).set(index(), actor);
	result = actor.setUp();
	if (getErrorHandler() != this)
	  ActorUtils.updateErrorHandler(actor, getErrorHandler(), isLoggingEnabled());
	// make sure we've got the current state of the variables
	if (result == null)
	  actor.getOptionManager().updateVariableValues(true);
	setParent(null);
	cleanUp();
      }
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

    result = super.setUp();

    if (result == null)
      result = setUpCallableActor();

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    return null;
  }
}
