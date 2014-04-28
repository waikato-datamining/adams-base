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
 * BaseTimeExpression.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.parser;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.logging.Level;

import java_cup.runtime.DefaultSymbolFactory;
import java_cup.runtime.SymbolFactory;
import adams.core.base.BaseTime;
import adams.parser.basetime.Parser;
import adams.parser.basetime.Scanner;

/**
 <!-- globalinfo-start -->
 * Evaluates time expressions.<br/>
 * <br/>
 * Format:<br/>
 * (&lt;date&gt;|NOW|-INF|+INF|START|END) [expr (SECOND|MINUTE|HOUR)]*<br/>
 * expr ::=   ( expr )<br/>
 *          | - expr<br/>
 *          | + expr<br/>
 *          | expr + expr<br/>
 *          | expr - expr<br/>
 *          | expr * expr<br/>
 *          | expr &#47; expr<br/>
 *          | expr % expr<br/>
 *          | expr ^ expr<br/>
 *          | abs ( expr )<br/>
 *          | sqrt ( expr )<br/>
 *          | log ( expr )<br/>
 *          | exp ( expr )<br/>
 *          | rint ( expr )<br/>
 *          | floor ( expr )<br/>
 *          | pow[er] ( expr , expr )<br/>
 *          | ceil ( expr )<br/>
 *          | NUMBER<br/>
 * <br/>
 * <br/>
 * Examples:<br/>
 * 01:02:03<br/>
 * 01:02:03 +1 MINUTE<br/>
 * NOW<br/>
 * +INF<br/>
 * NOW +1 HOUR<br/>
 * NOW +14 MINUTE<br/>
 * NOW +(2*3) MINUTE<br/>
 * <br/>
 * Amounts can be chained as well:<br/>
 * NOW -1 MINUTE +1 HOUR<br/>
 * <br/>
 * START and END can only be set programmatically; by default they are equal to -INF and +INF.
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
 * <pre>-env &lt;java.lang.String&gt; (property: environment)
 * &nbsp;&nbsp;&nbsp;The class to use for determining the environment.
 * &nbsp;&nbsp;&nbsp;default: adams.env.Environment
 * </pre>
 * 
 * <pre>-expression &lt;java.lang.String&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The boolean expression to evaluate (must evaluate to a boolean).
 * &nbsp;&nbsp;&nbsp;default: NOW
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseTimeExpression
  extends AbstractExpressionEvaluator<Date> {

  /** for serialization. */
  private static final long serialVersionUID = -5923987640355752595L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates time expressions.\n"
      + "\n"
      + "Format:\n"
      + getGrammar() + "\n"
      + "\n"
      + "Examples:\n"
      + "01:02:03\n"
      + "01:02:03 +1 MINUTE\n"
      + "NOW\n"
      + "+INF\n"
      + "NOW +1 HOUR\n"
      + "NOW +14 MINUTE\n"
      + "NOW +(2*3) MINUTE\n"
      + "\n"
      + "Amounts can be chained as well:\n"
      + "NOW -1 MINUTE +1 HOUR\n"
      + "\n"
      + "START and END can only be set programmatically; by default they are equal to -INF and +INF.";
  }

  /**
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return 
	"(<date>|NOW|-INF|+INF|START|END) [expr (SECOND|MINUTE|HOUR)]*"
      + "\n"
      + "expr ::=   ( expr )\n"
      + "         | - expr\n"
      + "         | + expr\n"
      + "         | expr + expr\n"
      + "         | expr - expr\n"
      + "         | expr * expr\n"
      + "         | expr / expr\n"
      + "         | expr % expr\n"
      + "         | expr ^ expr\n"
      + "         | abs ( expr )\n"
      + "         | sqrt ( expr )\n"
      + "         | log ( expr )\n"
      + "         | exp ( expr )\n"
      + "         | rint ( expr )\n"
      + "         | floor ( expr )\n"
      + "         | pow[er] ( expr , expr )\n"
      + "         | ceil ( expr )\n"
      + "         | NUMBER\n"
      ;
  }

  /**
   * Returns the default expression to use.
   *
   * @return		the default expression
   */
  @Override
  protected String getDefaultExpression() {
    return "NOW";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String expressionTipText() {
    return "The boolean expression to evaluate (must evaluate to a boolean).";
  }

  /**
   * Initializes the symbol.
   *
   * @param name	the name of the symbol
   * @param value	the string representation of the symbol
   * @return		the object representation of the symbol
   */
  protected Object initializeSymbol(String name, String value) {
    Double	result;

    try {
      result = new Double(value);
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to parse the value of symbol '" + name + "': " + value, e);
    }

    return result;
  }

  /**
   * Performs the evaluation.
   *
   * @return		the evaluation, or null in case of error
   * @throws Exception	if evaluation fails
   */
  @Override
  public Date evaluate() throws Exception {
    return evaluate(m_Expression);
  }

  /**
   * Performs the evaluation.
   *
   * @param start	the start time, can be null
   * @param end		the end time, can be null
   * @return		the evaluation, or null in case of error
   * @throws Exception	if evaluation fails
   */
  public Date evaluate(Date start, Date end) throws Exception {
    return evaluate(m_Expression, start, end);
  }

  /**
   * Parses and evaluates the given expression.
   * Returns the result of the boolean expression, based on the given
   * values of the symbols.
   *
   * @param expr	the expression to evaluate
   * @return		the evaluated result
   * @throws Exception	if something goes wrong
   */
  public static Date evaluate(String expr) throws Exception {
    return evaluate(expr, null, null);
  }

  /**
   * Parses and evaluates the given expression.
   * Returns the result of the boolean expression, based on the given
   * values of the symbols.
   *
   * @param expr	the expression to evaluate
   * @param start	the start time, can be null
   * @param end		the end time, can be null
   * @return		the evaluated result
   * @throws Exception	if something goes wrong
   * @see		BaseTime#START
   * @see		BaseTime#END
   */
  public static Date evaluate(String expr, Date start, Date end) throws Exception {
    SymbolFactory 		sf;
    ByteArrayInputStream 	parserInput;
    Parser 			parser;

    sf          = new DefaultSymbolFactory();
    parserInput = new ByteArrayInputStream(expr.getBytes());
    parser      = new Parser(new Scanner(parserInput, sf), sf);
    parser.setStart(start);
    parser.setEnd(end);
    parser.parse();

    return parser.getResult();
  }

  /**
   * Runs the evaluator from command-line.
   *
   * @param args	the command-line options, use "-help" to list them
   */
  public static void main(String[] args) {
    runEvaluator(BaseTimeExpression.class, args);
  }
}
