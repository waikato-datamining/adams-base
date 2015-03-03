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

package adams.parser;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import java_cup.runtime.DefaultSymbolFactory;
import java_cup.runtime.SymbolFactory;
import adams.parser.twitterfilter.Parser;
import adams.parser.twitterfilter.Scanner;

/**
 <!-- globalinfo-start -->
 * Evaluates Twitter filter expressions.<br/>
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
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-env &lt;java.lang.String&gt; (property: environment)
 * &nbsp;&nbsp;&nbsp;The class to use for determining the environment.
 * &nbsp;&nbsp;&nbsp;default: adams.env.Environment
 * </pre>
 * 
 * <pre>-expression &lt;java.lang.String&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The filter expression to evaluate.
 * &nbsp;&nbsp;&nbsp;default: text~\".*\"
 * </pre>
 * 
 * <pre>-symbol &lt;adams.core.base.BaseString&gt; [-symbol ...] (property: symbols)
 * &nbsp;&nbsp;&nbsp;The symbols to initialize the parser with, key-value pairs: name=value.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TwitterFilter
  extends AbstractSymbolEvaluator<Boolean> {

  /** for serialization. */
  private static final long serialVersionUID = -1217454324054448107L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates Twitter filter expressions.\n"
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
    return
        "expr_list ::= expr_list expr | expr;\n"
      + "expr      ::=   ( expr )\n"
      + "              | boolexpr\n"
      + "              ;\n"
      + "\n"
      + "boolexpr ::=    BOOLEAN\n"
      + "              | ( boolexpr )\n"
      + "              | not boolexpr\n"
      + "              | boolexpr and boolexpr\n"
      + "              | boolexpr or boolexpr\n"
      + "              | boolexpr xor boolexpr\n"
      + "              | numexpr < numexpr\n"
      + "              | numexpr <= numexpr\n"
      + "              | numexpr = numexpr\n"
      + "              | numexpr > numexpr\n"
      + "              | numexpr >= numexpr\n"
      + "              | numexpr <> numexpr\n"
      + "\n"
      + "              | retweet\n"
      + "              | isretweeted\n"
      + "\n"
      + "              | langcode <match> pattern\n"
      + "              | country <match> pattern\n"
      + "              | countrycode <match> pattern\n"
      + "              | place <match> pattern\n"
      + "              | source <match> pattern\n"
      + "              | text <match> pattern\n"
      + "              | user <match> pattern\n"
      + "              | hashtag <match> pattern\n"
      + "              | usermention <match> pattern\n"
      + "              | statuslang <match> pattern\n"
      + "\n"
      + "              | if[else] ( boolexpr:test , boolexpr:test_true , boolexpr:test_false )\n"
      + "              | has ( parameter )\n"
      + "              ;\n"
      + "\n"
      + "numexpr  ::=    num\n"
      + "              | longitude\n"
      + "              | latitude\n"
      + "              | favcount\n"
      + "              ;\n"
      + "\n"
      + "parameter ::=   langcode\n"
      + "              | country\n"
      + "              | countrycode\n"
      + "              | place\n"
      + "              | source\n"
      + "              | text\n"
      + "              | user\n"
      + "              | longitude\n"
      + "              | latitude\n"
      + "              ;\n"
      + "\n"
      + "The '<match>' operator can be one of the following:\n"
      + "1. '=' - exact match (the twitter field must be the exact 'pattern' string)\n"
      + "2. ':' - substring match (the 'pattern' can occur anywhere in the twitter field)\n"
      + "3. '~' - regular expression match (the 'pattern' is a regular expression that the twitter field must match)\n"
      + "\n"
      + "Please note, all strings are converted to lower case before the filter is applied.";
  }

  /**
   * Returns the default expression to use.
   *
   * @return		the default expression
   */
  @Override
  protected String getDefaultExpression() {
    return "text~\".*\"";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String expressionTipText() {
    return "The filter expression to evaluate.";
  }

  /**
   * Initializes the symbol.
   *
   * @param name	the name of the symbol
   * @param value	the string representation of the symbol
   * @return		the object representation of the symbol
   */
  @Override
  protected Object initializeSymbol(String name, String value) {
    return value;
  }

  /**
   * Performs the actual evaluation.
   *
   * @param symbols	the symbols to use
   * @return		the evaluation, or null in case of error
   * @throws Exception	if evaluation fails
   */
  @Override
  protected Boolean doEvaluate(HashMap symbols) throws Exception {
    return evaluate(m_Expression, symbols);
  }

  /**
   * Parses and evaluates the given expression.
   * Returns the result of the boolean expression, based on the given
   * values of the symbols.
   *
   * @param expr	the expression to evaluate
   * @param symbols	the symbol/value mapping
   * @return		the evaluated result
   * @throws Exception	if something goes wrong
   */
  public static boolean evaluate(String expr, HashMap symbols) throws Exception {
    SymbolFactory 		sf;
    ByteArrayInputStream 	parserInput;
    Parser 			parser;

    sf          = new DefaultSymbolFactory();
    parserInput = new ByteArrayInputStream(expr.getBytes());
    parser      = new Parser(new Scanner(parserInput, sf), sf);
    parser.setSymbols(symbols);
    parser.parse();

    return parser.getResult();
  }
  
  /**
   * Runs the evaluator from command-line.
   *
   * @param args	the command-line options, use "-help" to list them
   */
  public static void main(String[] args) {
    runEvaluator(TwitterFilter.class, args);
  }
}
