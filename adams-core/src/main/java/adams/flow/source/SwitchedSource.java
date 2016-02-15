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
 * SwitchedSource.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.IndexedBooleanCondition;
import adams.flow.condition.bool.IndexedBooleanConditionSupporter;
import adams.flow.condition.bool.True;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SwitchedSource
  extends AbstractSource
  implements MutableActorHandler, IndexedBooleanConditionSupporter {

  /** the "conditions" for the various switch cases. */
  protected BooleanCondition[] m_Conditions;

  /** the "cases" to execute if the corresponding expression matches. */
  protected List<Actor> m_Cases;

  /** the active case. */
  protected Actor m_ActiveCase;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Emulates a Switch control statement. The first 'condition' that evaluates to 'true' "
      + "selects the corresponding 'case' actor and stops evaluation of conditions.\n"
      + "A catch-all or default can be set up as well by having one more 'case' than "
      + "'conditions' (the last case acts as default).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "condition", "conditions",
	    new BooleanCondition[]{new True()});

    m_OptionManager.add(
	    "case", "cases",
	    new Actor[]{new Start()});
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Cases = new ArrayList<>();
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActiveCase = null;
  }

  /**
   * Sets the conditions to evaluate.
   *
   * @param value	the conditions
   */
  public void setConditions(BooleanCondition[] value) {
    int		i;

    // check for IndexedBooleanCondition
    if (value.length > 1) {
      for (i = 0; i < value.length; i++) {
	if (value[i] instanceof IndexedBooleanCondition) {
	  getLogger().severe("When using " + IndexedBooleanCondition.class.getName() + " conditions, only a single one is allowed!");
	  return;
	}
      }
    }

    m_Conditions = value;
    reset();
  }

  /**
   * Returns the conditions to evaluate.
   *
   * @return		the conditions
   */
  public BooleanCondition[] getConditions() {
    return m_Conditions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conditionsTipText() {
    return
        "The switch conditions to evaluate - the first condition that "
      + "evaluates to 'true' triggers the execution of the corresponding "
      + "'case' actor.";
  }

  /**
   * Sets the cases.
   *
   * @param value 	the cases
   */
  public void setCases(Actor[] value) {
    int		i;

    ActorUtils.uniqueNames(value);

    m_Cases.clear();
    for (i = 0; i < value.length; i++)
      m_Cases.add(value[i]);

    updateParent();
    reset();
  }

  /**
   * Returns the cases.
   *
   * @return 		the cases
   */
  public Actor[] getCases() {
    return m_Cases.toArray(new Actor[m_Cases.size()]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String casesTipText() {
    return
        "The 'cases' - one of them gets executed if the corresponding "
      + "'condition' evaluates to 'true'.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = m_Conditions.length + " condition";
    if (m_Conditions.length != 1)
	result += "s";
    result = QuickInfoHelper.toString(this, "conditions", result);

    if (super.getQuickInfo() != null)
      result += ", " + super.getQuickInfo();

    return result;
  }

  /**
   * Returns the size of the group.
   *
   * @return		the number of cases
   */
  @Override
  public int size() {
    return m_Cases.size();
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  @Override
  public Actor get(int index) {
    return m_Cases.get(index);
  }

  /**
   * Updates the parent of all actors in this group.
   */
  protected void updateParent() {
    int		i;

    for (i = 0; i < size(); i++) {
      get(i).setParent(null);
      get(i).setParent(this);
    }
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  @Override
  public void set(int index, Actor actor) {
    if ((index > -1) && (index < m_Cases.size())) {
      ActorUtils.uniqueName(actor, this, index);
      m_Cases.set(index, actor);
      reset();
      updateParent();
    }
    else {
      getLogger().severe("Index out of range (0-" + (m_Cases.size() - 1) + "): " + index);
    }
  }

  /**
   * Inserts the actor at the end.
   *
   * @param actor	the actor to insert
   */
  public void add(Actor actor) {
    add(size(), actor);
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   */
  public void add(int index, Actor actor) {
    m_Cases.add(index, actor);
    reset();
    updateParent();
  }

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  public Actor remove(int index) {
    Actor	result;

    result = m_Cases.remove(index);
    reset();

    return result;
  }

  /**
   * Removes all actors.
   */
  public void removeAll() {
    m_Cases.clear();
    reset();
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  @Override
  public int indexOf(String actor) {
    int		result;
    int		i;

    result = -1;

    for (i = 0; i < m_Cases.size(); i++) {
      if (m_Cases.get(i).getName().equals(actor)) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Returns the first non-skipped actor.
   *
   * @return		the first 'active' actor, null if none available
   */
  @Override
  public Actor firstActive() {
    Actor	result;
    int		i;

    result = null;
    for (i = 0; i < size(); i++) {
      if (!get(i).getSkip()) {
	result = get(i);
	break;
      }
    }

    return result;
  }

  /**
   * Returns the last non-skipped actor.
   *
   * @return		the last 'active' actor, null if none available
   */
  @Override
  public Actor lastActive() {
    Actor	result;
    int		i;

    result = null;
    for (i = size() - 1; i >= 0; i--) {
      if (!get(i).getSkip()) {
	result = get(i);
	break;
      }
    }

    return result;
  }

  /**
   * Returns whether the condition is supported.
   *
   * @return		true if supported, false otherwise
   */
  @Override
  public boolean supports(BooleanCondition condition) {
    return (condition instanceof IndexedBooleanCondition);
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(false, ActorExecution.SEQUENTIAL, false);
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
    int		i;

    super.forceVariables(value);

    for (i = 0; i < size(); i++)
      get(i).setVariables(value);
  }

  /**
   * Performs checks on the "sub-actors". Default implementation does nothing.
   *
   * @return		null
   */
  @Override
  public String check() {
    String	result;
    int		i;

    result = null;

    for (i = 0; i < size(); i++) {
      if (get(i).getSkip()) {
	result = "Actor #" + (i+1) + " gets skipped!";
	break;
      }
      else {
	if (!ActorUtils.isSource(get(i))) {
	  result = "Actor #" + (i+1) + " is not a source!";
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Initializes the sub-actors for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    int		i;

    result = super.setUp();

    if (result == null) {
      if ((m_Conditions == null) || (m_Conditions.length == 0)) {
	result = "No condition provided!";
      }
      else if (m_Cases.size() > m_Conditions.length + 1) {
	if ((m_Conditions.length == 1) && (m_Conditions[0] instanceof IndexedBooleanCondition)) {
	  // we presume it's fine
	}
	else {
	  result =   "Only 1 default case can be defined: "
	      + m_Conditions.length + " condition(s) but " + m_Cases.size() + " case(s)";
	}
      }
      else if (m_Cases.size() < m_Conditions.length) {
	result = "Not enough cases defined: " + m_Conditions.length + " required";
      }
    }

    if (result == null) {
      for (i = 0; i < m_Conditions.length; i++) {
	result = m_Conditions[i].setUp(this);
	if (result != null) {
	  result = "Condition #" + (i+1) + ": " + result;
	  break;
	}
      }
    }

    if (result == null)
      result = check();

    if (result == null) {
      for (i = 0; i < size(); i++) {
	result = get(i).setUp();
	if (result != null) {
	  result = "Actor #" + (i+1) + ": " + result;
	  break;
	}
      }
    }

    return result;
  }

    /**
     * Determines which case to execute.
     *
     * @return		the index of the case to execute
     */
    protected int whichCase() {
      int		result;
      int		i;
      boolean		indexed;

      result = -1;

      indexed = (getConditions().length == 1) && (getConditions()[0] instanceof IndexedBooleanCondition);

      if (indexed) {
	result = ((IndexedBooleanCondition) getConditions()[0]).getCaseIndex(this, null);
      }
      else {
	for (i = 0; i < getConditions().length; i++) {
	  try {
	    if (getConditions()[i].evaluate(this, null)) {
	      result = i;
	      break;
	    }
	  }
	  catch (Throwable t) {
	    handleException("Error evaluating boolean condition: " + getConditions()[i], t);
	  }
	}
      }

      // default case?
      if (result == -1) {
	if (indexed) {
	  result = ((IndexedBooleanCondition) getConditions()[0]).getDefaultCaseIndex(this, null);
	  if (result >= getCases().length)
	    result = -1;
	}
	else if (getCases().length > getConditions().length) {
	  result = getCases().length - 1;
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
    int		index;

    result = null;

    // locate active case
    m_ActiveCase = null;
    index        = whichCase();
    if (index == -1)
      result = "No case matched?";
    else
      m_ActiveCase = m_Cases.get(index);

    if (result == null)
      result = m_ActiveCase.execute();

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_ActiveCase != null) && ((OutputProducer) m_ActiveCase).hasPendingOutput();
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    if (m_ActiveCase != null)
      return ((OutputProducer) m_ActiveCase).output();
    else
      return null;
  }

  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    if ((m_ActiveCase != null) && (m_ActiveCase instanceof ActorHandler))
      ((ActorHandler) m_ActiveCase).flushExecution();
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    for (BooleanCondition cond: m_Conditions)
      cond.stopExecution();
    if (m_ActiveCase != null)
      m_ActiveCase.stopExecution();
    super.stopExecution();
  }
}
