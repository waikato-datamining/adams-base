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
 * TwitterFilter.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.HashMap;

import twitter4j.Status;
import adams.core.QuickInfoHelper;
import adams.core.base.TwitterFilterExpression;
import adams.core.net.TwitterHelper;
import adams.flow.core.Token;
import adams.parser.GrammarSupplier;

/**
 <!-- globalinfo-start -->
 * Filters Twitter objects (tweet&#47;status) according to the provided filter expression. Only objects that match the filter expression are passed on.<br/>
 * <br/>
 * The expressions use the following grammar:<br/>
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
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;twitter4j.Status<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;twitter4j.Status<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: TwitterFilter
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-expression &lt;adams.core.base.TwitterFilterExpression&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The filter expression to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TwitterFilter
  extends AbstractTransformer
  implements GrammarSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -449062766931736640L;

  /** the filter expression. */
  protected TwitterFilterExpression m_Expression;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Filters Twitter objects (tweet/status) according to the provided "
      + "filter expression. Only objects that match the filter expression "
      + "are passed on.\n"
      + "\n"
      + "The expressions use the following grammar:\n\n"
      + getGrammar();
  }

  /**
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return new adams.parser.TwitterFilter().getGrammar();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "expression", "expression",
	    new TwitterFilterExpression());
  }

  /**
   * Sets the separator to use. \t, \n, \r, \\ must be quoted.
   *
   * @param value	the separator
   */
  public void setExpression(TwitterFilterExpression value) {
    m_Expression = value;
    reset();
  }

  /**
   * Returns the separator in use. \t, \r, \n, \\ get returned quoted.
   *
   * @return		the separator
   */
  public TwitterFilterExpression getExpression() {
    return m_Expression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expressionTipText() {
    return "The filter expression to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "expression", m_Expression);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->twitter4j.Status.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Status.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->twitter4j.Status.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Status.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    HashMap	symbols;
    boolean	match;
    String	exp;

    result = null;

    exp = m_Expression.getValue();
    try {
      // get input
      symbols = TwitterHelper.statusToSymbols((Status) m_InputToken.getPayload(), true);

      // evaluate the expression
      match = adams.parser.TwitterFilter.evaluate(exp, symbols);
      if (match)
	m_OutputToken = new Token(m_InputToken.getPayload());
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Error evaluating: " + exp, e);
    }

    return result;
  }
}
