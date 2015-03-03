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
 * PathBreakpoint.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.BooleanConditionSupporter;
import adams.flow.condition.bool.Expression;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Triggers when the specified actor path (full path) is encountered during listening.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-disabled (property: disabled)
 * &nbsp;&nbsp;&nbsp;If set to true, the breakpoint is completely disabled.
 * </pre>
 * 
 * <pre>-on-pre-input (property: onPreInput)
 * &nbsp;&nbsp;&nbsp;If set to true, the breakpoint gets evaluated at pre-input (of token) time.
 * </pre>
 * 
 * <pre>-on-post-input (property: onPostInput)
 * &nbsp;&nbsp;&nbsp;If set to true, the breakpoint gets evaluated at post-input (of token) time.
 * </pre>
 * 
 * <pre>-on-pre-execute (property: onPreExecute)
 * &nbsp;&nbsp;&nbsp;If set to true, the breakpoint gets evaluated at pre-execute time.
 * </pre>
 * 
 * <pre>-on-post-execute (property: onPostExecute)
 * &nbsp;&nbsp;&nbsp;If set to true, the breakpoint gets evaluated at post-execute time.
 * </pre>
 * 
 * <pre>-on-pre-output (property: onPreOutput)
 * &nbsp;&nbsp;&nbsp;If set to true, the breakpoint gets evaluated at pre-output (of token) time.
 * </pre>
 * 
 * <pre>-on-post-output (property: onPostOutput)
 * &nbsp;&nbsp;&nbsp;If set to true, the breakpoint gets evaluated at post-output (of token) 
 * &nbsp;&nbsp;&nbsp;time.
 * </pre>
 * 
 * <pre>-path &lt;java.lang.String&gt; (property: path)
 * &nbsp;&nbsp;&nbsp;The full actor path to listen for.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-condition &lt;adams.flow.condition.bool.BooleanCondition&gt; (property: condition)
 * &nbsp;&nbsp;&nbsp;The condition to evaluate; if the condition evaluates to 'true', the execution 
 * &nbsp;&nbsp;&nbsp;of the flow is interrupted and the control panel can be used.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.condition.bool.Expression
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PathBreakpoint
  extends AbstractBreakpoint
  implements BooleanConditionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 3782327753485131754L;
  
  /** the path to listen for. */
  protected String m_Path;

  /** the condition to evaluate. */
  protected BooleanCondition m_Condition;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Triggers when the specified actor path (full path) is encountered during listening.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    
    m_OptionManager.add(
	    "path", "path",
	    "");

    m_OptionManager.add(
	    "condition", "condition",
	    new Expression());
  }
  
  /**
   * Sets the full actor path to listen for.
   *
   * @param value	the path
   */
  public void setPath(String value) {
    m_Path = value;
    reset();
  }

  /**
   * Returns the full actor path to listen for.
   *
   * @return		the path
   */
  public String getPath() {
    return m_Path;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pathTipText() {
    return "The full actor path to listen for.";
  }

  /**
   * Sets the break condition to evaluate.
   *
   * @param value	the expression
   */
  public void setCondition(BooleanCondition value) {
    m_Condition = value;
    reset();
  }

  /**
   * Returns the break condition to evaluate.
   *
   * @return		the expression
   */
  public BooleanCondition getCondition() {
    return m_Condition;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conditionTipText() {
    return
        "The condition to evaluate; if the condition evaluates to 'true', "
      + "the execution of the flow is interrupted and the control panel can be used.";
  }

  /**
   * Evaluates the breakpoint at pre-input.
   * 
   * @param actor	the current actor
   * @param token	the token available for input
   * @return		true if breakpoint triggers
   */
  @Override
  protected boolean evaluatePreInput(Actor actor, Token token) {
    return actor.getFullName().equals(m_Path) && m_Condition.evaluate(actor, token);
  }
  
  /**
   * Evaluates the breakpoint at post-input.
   * 
   * @param actor	the current actor
   * @return		true if breakpoint triggers
   */
  @Override
  protected boolean evaluatePostInput(Actor actor) {
    return actor.getFullName().equals(m_Path) && m_Condition.evaluate(actor, null);
  }
  
  /**
   * Evaluates the breakpoint at pre-execute.
   * 
   * @param actor	the current actor
   * @return		true if breakpoint triggers
   */
  @Override
  protected boolean evaluatePreExecute(Actor actor) {
    return actor.getFullName().equals(m_Path) && m_Condition.evaluate(actor, null);
  }
  
  /**
   * Evaluates the breakpoint at post-execute.
   * 
   * @param actor	the current actor
   * @return		true if breakpoint triggers
   */
  @Override
  protected boolean evaluatePostExecute(Actor actor) {
    return actor.getFullName().equals(m_Path) && m_Condition.evaluate(actor, null);
  }
  
  /**
   * Evaluates the breakpoint at pre-output.
   * 
   * @param actor	the current actor
   * @return		true if breakpoint triggers
   */
  @Override
  protected boolean evaluatePreOutput(Actor actor) {
    return actor.getFullName().equals(m_Path) && m_Condition.evaluate(actor, null);
  }
  
  /**
   * Evaluates the breakpoint at post-output.
   * 
   * @param actor	the current actor
   * @param token	the token available for output
   * @return		true if breakpoint triggers
   */
  @Override
  protected boolean evaluatePostOutput(Actor actor, Token token) {
    return actor.getFullName().equals(m_Path) && m_Condition.evaluate(actor, token);
  }
}
