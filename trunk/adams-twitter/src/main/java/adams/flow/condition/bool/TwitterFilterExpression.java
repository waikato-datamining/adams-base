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
 * TwitterExpression.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import java.util.HashMap;
import java.util.logging.Level;

import twitter4j.Status;
import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.core.net.TwitterHelper;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.parser.GrammarSupplier;
import adams.parser.TwitterFilter;

/**
 <!-- globalinfo-start -->
 * Evaluates to 'true' if the twitter expression evaluates to 'true'.<br/>
 * The following grammar is used for evaluating the expressions:<br/>
 * <br/>
 * expr_list ::= expr_list expr | expr;<br/>
 * expr      ::=   ( expr )<br/>
 *               | boolexpr<br/>
 *               ;<br/>
 * <br/>
 * boolexpr ::=    BOOLEAN<br/>
 *               | ( boolexpr )<br/>
 *               | not boolexpr<br/>
 *               | boolexpr and boolexpr<br/>
 *               | boolexpr or boolexpr<br/>
 *               | boolexpr xor boolexpr<br/>
 *               | numexpr &lt; numexpr<br/>
 *               | numexpr &lt;= numexpr<br/>
 *               | numexpr = numexpr<br/>
 *               | numexpr &gt; numexpr<br/>
 *               | numexpr &gt;= numexpr<br/>
 *               | numexpr &lt;&gt; numexpr<br/>
 * <br/>
 *               | retweet<br/>
 *               | isretweeted<br/>
 * <br/>
 *               | langcode &lt;match&gt; pattern<br/>
 *               | country &lt;match&gt; pattern<br/>
 *               | countrycode &lt;match&gt; pattern<br/>
 *               | place &lt;match&gt; pattern<br/>
 *               | source &lt;match&gt; pattern<br/>
 *               | text &lt;match&gt; pattern<br/>
 *               | user &lt;match&gt; pattern<br/>
 *               | hashtag &lt;match&gt; pattern<br/>
 *               | usermention &lt;match&gt; pattern<br/>
 *               | statuslang &lt;match&gt; pattern<br/>
 * <br/>
 *               | if[else] ( boolexpr:test , boolexpr:test_true , boolexpr:test_false )<br/>
 *               | has ( parameter )<br/>
 *               ;<br/>
 * <br/>
 * numexpr  ::=    num<br/>
 *               | longitude<br/>
 *               | latitude<br/>
 *               | favcount<br/>
 *               ;<br/>
 * <br/>
 * parameter ::=   langcode<br/>
 *               | country<br/>
 *               | countrycode<br/>
 *               | place<br/>
 *               | source<br/>
 *               | text<br/>
 *               | user<br/>
 *               | longitude<br/>
 *               | latitude<br/>
 *               ;<br/>
 * <br/>
 * The '&lt;match&gt;' operator can be one of the following:<br/>
 * 1. '=' - exact match (the twitter field must be the exact 'pattern' string)<br/>
 * 2. ':' - substring match (the 'pattern' can occur anywhere in the twitter field)<br/>
 * 3. '~' - regular expression match (the 'pattern' is a regular expression that the twitter field must match)<br/>
 * <br/>
 * Please note, all strings are converted to lower case before the filter is applied.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-expression &lt;adams.core.base.TwitterFilterExpression&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The filter expression to evaluate.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TwitterFilterExpression
  extends AbstractBooleanCondition
  implements GrammarSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -9169161144858552052L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates to 'true' if the twitter expression evaluates to 'true'.\n"
      + "The following grammar is used for evaluating the expressions:\n\n"
      + getGrammar();
  }
  
  /** the filter expression to evaluate. */
  protected adams.core.base.TwitterFilterExpression m_Expression;

  /**
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return new adams.core.base.TwitterFilterExpression().getGrammar();
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
  protected adams.core.base.TwitterFilterExpression getDefaultExpression() {
    return new adams.core.base.TwitterFilterExpression("");
  }
  
  /**
   * Sets the expression to evaluate. Automatically wraps expressions in
   * parentheses that consists only of a variable. Otherwise, the expresssion
   * would get interpreted as attached variable for the expression option.
   *
   * @param value	the expression
   */
  public void setExpression(adams.core.base.TwitterFilterExpression value) {
    if (Variables.isPlaceholder(value.getValue()))
      value = new adams.core.base.TwitterFilterExpression("(" + value.getValue() + ")");
    m_Expression = value;
    reset();
  }

  /**
   * Returns the expression to evaluate.
   *
   * @return		the expression
   */
  public adams.core.base.TwitterFilterExpression getExpression() {
    return m_Expression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expressionTipText() {
    return "The filter expression to evaluate.";
  }

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
    return new Class[]{Status.class};
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
      result = TwitterFilter.evaluate(exp, symbols);
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

  /**
   * Evaluates the expression.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		true if the expression evaluates to 'true'
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    String	exp;
    HashMap	symbols;

    exp     = owner.getVariables().expand(getExpression().getValue());
    symbols = new HashMap();
    if ((token != null) && (token.getPayload() != null)) {
      if (token.getPayload() instanceof Status)
	symbols = TwitterHelper.statusToSymbols((Status) token.getPayload(), true);
    }

    try {
      return doEvaluate(exp, symbols);
    }
    catch (Throwable t) {
      throw new RuntimeException("Failed to evaluate '" + exp + "' with symbols: " + symbols, t);
    }
  }
}
