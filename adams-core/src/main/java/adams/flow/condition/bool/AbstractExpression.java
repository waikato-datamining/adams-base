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
 * Expression.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import java.util.HashMap;
import java.util.logging.Level;

import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.flow.core.Actor;
import adams.flow.core.Unknown;
import adams.parser.BooleanExpression;
import adams.parser.BooleanExpressionText;
import adams.parser.GrammarSupplier;

/**
 * Ancestor for conditions that use a boolean expression.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractExpression
  extends AbstractBooleanCondition
  implements GrammarSupplier {

  /** for serialization. */
  private static final long serialVersionUID = 4442436759501167843L;
  
  /** the "If" expression to evaluate. */
  protected BooleanExpressionText m_Expression;

  /**
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return new BooleanExpression().getGrammar();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "expression", "expression",
	    getDefaultExpression());
  }

  /**
   * Returns the default expression to use.
   * 
   * @return		the default
   */
  protected abstract BooleanExpressionText getDefaultExpression();
  
  /**
   * Sets the expression to evaluate. Automatically wraps expressions in
   * parentheses that consists only of a variable. Otherwise, the expresssion
   * would get interpreted as attached variable for the expression option.
   *
   * @param value	the expression
   */
  public void setExpression(BooleanExpressionText value) {
    if (Variables.isPlaceholder(value.getValue()))
      value = new BooleanExpressionText("(" + value.getValue() + ")");
    m_Expression = value;
    reset();
  }

  /**
   * Returns the expression to evaluate.
   *
   * @return		the expression
   */
  public BooleanExpressionText getExpression() {
    return m_Expression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String expressionTipText();

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "expression", m_Expression);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.flow.core.Unknown.class
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Configures the condition.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp(Actor owner) {
    String	result;

    result = super.setUp(owner);

    if (result == null) {
      if ((m_Expression == null) || (m_Expression.getValue().length() == 0))
	result = "No expression provided!";
    }

    return result;
  }

  /**
   * Evaluates the condition.
   *
   * @param exp		the expression to evaluate
   * @param symbols	the symbols to use in the evaluation
   * @return		the result of the boolean condition
   */
  protected boolean doEvaluate(String exp, HashMap symbols) {
    boolean	result;

    try {
      result = BooleanExpression.evaluate(exp, symbols);
      if (isLoggingEnabled())
	getLogger().fine(
	    "exp: " + getExpression() + "\n"
	    + "  --> " + exp + "\n"
	    + "  = " + result);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Error evaluating boolean expression: " + exp, e);
      result = false;
    }

    return result;
  }
}
