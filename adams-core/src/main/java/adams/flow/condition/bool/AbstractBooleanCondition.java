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
 * AbstractBooleanCondition.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.Stoppable;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 * Ancestor for conditions that get evaluated in, e.g., the IfThenElse control 
 * actor, deciding which branch to take.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBooleanCondition
  extends AbstractOptionHandler
  implements BooleanCondition, Stoppable {

  /** for serialization. */
  private static final long serialVersionUID = 1816980432972492738L;

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  public abstract String getQuickInfo();

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.flow.core.Unknown.class
   */
  public abstract Class[] accepts();

  /**
   * Configures the condition.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  public String setUp(Actor owner) {
    String	result;
    
    result = null;
    
    if (this instanceof IndexedBooleanCondition) {
      if (!(owner instanceof IndexedBooleanConditionSupporter))
	result = "Actor " + owner.getFullName() + " does not support " + IndexedBooleanCondition.class.getName() + "!";
      else if (!((IndexedBooleanConditionSupporter) owner).supports(this))
	result = "Actor " + owner.getFullName() + " does not support " + getClass().getName() + "!";
    }
    
    return result;
  }

  /**
   * Uses the token to determine the evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		null if OK, otherwise error message
   */
  protected String preEvaluate(Actor owner, Token token) {
    return null;
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  protected abstract boolean doEvaluate(Actor owner, Token token);

  /**
   * Uses the token to determine the evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through the actor
   * @return		the result of the evaluation
   */
  public boolean evaluate(Actor owner, Token token) {
    String	msg;
    
    msg = preEvaluate(owner, token);
    if (msg != null)
      throw new IllegalStateException(msg);
    
    return doEvaluate(owner, token);
  }

  /**
   * Stops the execution.
   * <br>
   * Default implementation does nothing.
   */
  public void stopExecution() {
    // nothing
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractBooleanCondition shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractBooleanCondition shallowCopy(boolean expand) {
    return (AbstractBooleanCondition) OptionUtils.shallowCopy(this, expand);
  }
}
