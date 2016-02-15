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
 * AbstractConditionalActor.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.flow.condition.test.TestCondition;
import adams.flow.condition.test.TestConditionSupporter;
import adams.flow.condition.test.True;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;

/**
 * Abstract superclass for actors that need to fullfil a test condition before
 * they can be executed.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractConditionalActor
  extends AbstractActor
  implements ActorHandler, TestConditionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -7877511203822332577L;

  /** the condition to check in the setUp() method of the actor. */
  protected TestCondition m_Condition;

  /** the base actor to run if condition is met. */
  protected Actor m_BaseActor;

  /** whether to test condition during setup or whenever executed. */
  protected boolean m_CheckAtExecutionTime;

  /** whether to execute the actor if the condition fails. */
  protected boolean m_ExecuteOnFail;

  /** whether the base actor has been setup. */
  protected boolean m_BaseActorInitialized;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "condition", "condition",
	    new True());

    m_OptionManager.add(
	    "execution-time", "checkAtExecutionTime",
	    false);

    m_OptionManager.add(
	    "execute-on-fail", "executeOnFail",
	    false);

    m_OptionManager.add(
	    "actor", "actor",
	    getDefaultActor());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "condition", m_Condition);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void reset() {
    super.reset();

    m_BaseActorInitialized = false;
  }

  /**
   * Sets the condition to use.
   *
   * @param value	the condition
   */
  public void setCondition(TestCondition value) {
    m_Condition = value;
    reset();
  }

  /**
   * Returns the currently set condition.
   *
   * @return		the condition
   */
  public TestCondition getCondition() {
    return m_Condition;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conditionTipText() {
    return "The condition that has to be met before the actor can be executed.";
  }

  /**
   * Sets whether to check the condition at execution time or during setup.
   *
   * @param value	true if to check at execution time
   */
  public void setCheckAtExecutionTime(boolean value) {
    m_CheckAtExecutionTime = value;
    reset();
  }

  /**
   * Returns whether to check the condition at execution time or during setup.
   *
   * @return		true if the check happens at execution time
   */
  public boolean getCheckAtExecutionTime() {
    return m_CheckAtExecutionTime;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String checkAtExecutionTimeTipText() {
    return
        "If set to true, then the condition is checked at execution time "
      + "(whenever the actor gets executed) and not during setup.";
  }

  /**
   * Sets whether to execute the actor when the condition fails instead of
   * succeeds.
   *
   * @param value	if true then the actor gets executed when the condition
   * 			fails
   */
  public void setExecuteOnFail(boolean value) {
    m_ExecuteOnFail = value;
    reset();
  }

  /**
   * Returns whether to execute the actor when the condition fails instead of
   * succeeds.
   *
   * @return		true if the check happens at execution time
   */
  public boolean getExecuteOnFail() {
    return m_ExecuteOnFail;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String executeOnFailTipText() {
    return "If set to true, then the actor is only executed if the condition fails.";
  }

  /**
   * Returns the default actor to use.
   *
   * @return		the default actor
   */
  protected abstract Actor getDefaultActor();

  /**
   * Sets the base actor.
   *
   * @param value 	the actor
   */
  public void setActor(Actor value) {
    m_BaseActor = value;
    updateParent();

    reset();
  }

  /**
   * Returns the base actor.
   *
   * @return 		the actor
   */
  public Actor getActor() {
    return m_BaseActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String actorTipText();

  /**
   * Performs checks on the "sub-actors".
   *
   * @return		null if everything is fine, otherwise the error
   */
  public String check() {
    return m_BaseActor.setUp();
  }

  /**
   * Updates the parent of all actors in this group.
   */
  protected void updateParent() {
    if (m_BaseActor != null)
      m_BaseActor.setParent(this);
  }

  /**
   * Returns the size of the group.
   *
   * @return		always 1
   */
  public int size() {
    return 1;
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  public Actor get(int index) {
    if (index == 0)
      return m_BaseActor;
    else
      throw new IllegalArgumentException("Illegal index: " + index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  public void set(int index, Actor actor) {
    if (index == 0) {
      m_BaseActor = actor;
      updateParent();
    }
    else {
      throw new IllegalArgumentException("Illegal index: " + index);
    }
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  public int indexOf(String actor) {
    if (actor.equals(m_BaseActor.getName()))
      return 0;
    else
      return -1;
  }

  /**
   * Returns the first non-skipped actor.
   *
   * @return		the first 'active' actor, null if none available
   */
  public Actor firstActive() {
    if (m_BaseActor.getSkip())
      return null;
    else
      return m_BaseActor;
  }

  /**
   * Returns the last non-skipped actor.
   *
   * @return		the last 'active' actor, null if none available
   */
  public Actor lastActive() {
    if (m_BaseActor.getSkip())
      return null;
    else
      return m_BaseActor;
  }

  /**
   * Checks whether the class' options can be inspected. By default, arrays
   * of actors (i.e., the control actor's sub-actors) won't be inspected, as
   * they do it themselves.
   *
   * @param cls		the class to check
   * @return		true if it can be inspected, false otherwise
   */
  @Override
  public boolean canInspectOptions(Class cls) {
    // we don't inspect sub-actors!
    if (cls == Actor[].class)
      return false;
    else if (cls == Actor.class)
      return false;
    else
      return super.canInspectOptions(cls);
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
    m_BaseActor.setVariables(value);
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

    if (result == null) {
      if (!m_CheckAtExecutionTime) {
	result = m_Condition.getTestResult();
	// we can ignore a failure of the condition if we want to execute
	// the actor only if the condition fails
	if (m_ExecuteOnFail)
	  result = null;
      }
      m_BaseActorInitialized = false;
    }

    return result;
  }

  /**
   * Pre-execute hook.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String preExecute() {
    String	result;

    result = super.preExecute();

    if (result == null) {
      if (m_CheckAtExecutionTime)
	result = m_Condition.getTestResult();
      // we can ignore a failure of the condition if we want to execute
      // the actor only if the condition fails
      if (m_ExecuteOnFail)
	result = null;
      if ((result == null) && !m_BaseActorInitialized) {
	result = m_BaseActor.setUp();
	m_BaseActorInitialized = (result == null);
      }
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    boolean 	execute;

    result  = null;

    execute = true;
    if (m_ExecuteOnFail)
      execute = (m_Condition.getTestResult() != null);

    if (execute)
      result = m_BaseActor.execute();

    return result;
  }
  
  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    if (m_BaseActor instanceof ActorHandler)
      ((ActorHandler) m_BaseActor).flushExecution();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    m_BaseActor.cleanUp();
  }
}
