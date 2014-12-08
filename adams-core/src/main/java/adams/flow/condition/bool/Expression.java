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
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import java.util.HashMap;

import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.parser.BooleanExpression;
import adams.parser.BooleanExpressionText;

/**
 <!-- globalinfo-start -->
 * Evaluates to 'true' if the expression evaluates to 'true'.<br/>
 * In case of integer or double tokens that arrive at the input, these can be accessed in the expression via 'X'; string tokens can be accessed via expression '"X"' (surrounding double quotes are required).<br/>
 * If the incoming token is either a Report or a ReportHandler, the contents of the report get added as values as well (boolean, numeric or string) and you can access them via their name instead of 'X'.<br/>
 * <br/>
 * The following grammar is used for evaluating the boolean expressions:<br/>
 * <br/>
 * expr_list ::= '=' expr_list expr_part | expr_part ;<br/>
 * expr_part ::=  expr ;<br/>
 * <br/>
 * expr      ::=   ( expr )<br/>
 * <br/>
 * # data types<br/>
 *               | number<br/>
 *               | string<br/>
 *               | boolean<br/>
 *               | date<br/>
 * <br/>
 * # constants<br/>
 *               | true<br/>
 *               | false<br/>
 *               | pi<br/>
 *               | e<br/>
 *               | now()<br/>
 *               | today()<br/>
 * <br/>
 * # negating numeric value<br/>
 *               | -expr<br/>
 * <br/>
 * # comparisons<br/>
 *               | expr &lt; expr<br/>
 *               | expr &lt;= expr<br/>
 *               | expr &gt; expr<br/>
 *               | expr &gt;= expr<br/>
 *               | expr = expr<br/>
 *               | expr != expr (or: expr &lt;&gt; expr)<br/>
 * <br/>
 * # boolean operations<br/>
 *               | ! expr (or: not expr)<br/>
 *               | expr &amp; expr (or: expr and expr)<br/>
 *               | expr | expr (or: expr or expr)<br/>
 *               | if[else] ( expr , expr (if true) , expr (if false) )<br/>
 *               | ifmissing ( variable , expr (default value if variable is missing) )<br/>
 *               | isNaN ( expr )<br/>
 * <br/>
 * # arithmetics<br/>
 *               | expr + expr<br/>
 *               | expr - expr<br/>
 *               | expr * expr<br/>
 *               | expr &#47; expr<br/>
 *               | expr ^ expr (power of)<br/>
 *               | expr % expr (modulo)<br/>
 *               ;<br/>
 * <br/>
 * # numeric functions<br/>
 *               | abs ( expr )<br/>
 *               | sqrt ( expr )<br/>
 *               | log ( expr )<br/>
 *               | exp ( expr )<br/>
 *               | sin ( expr )<br/>
 *               | cos ( expr )<br/>
 *               | tan ( expr )<br/>
 *               | rint ( expr )<br/>
 *               | floor ( expr )<br/>
 *               | pow[er] ( expr , expr )<br/>
 *               | ceil ( expr )<br/>
 *               | year ( expr )<br/>
 *               | month ( expr )<br/>
 *               | day ( expr )<br/>
 *               | hour ( expr )<br/>
 *               | minute ( expr )<br/>
 *               | second ( expr )<br/>
 *               | weekday ( expr )<br/>
 *               | weeknum ( expr )<br/>
 * <br/>
 * # string functions<br/>
 *               | substr ( expr , start [, end] )<br/>
 *               | left ( expr , len )<br/>
 *               | mid ( expr , start , len )<br/>
 *               | right ( expr , len )<br/>
 *               | rept ( expr , count )<br/>
 *               | concatenate ( expr1 , expr2 [, expr3-5] )<br/>
 *               | lower[case] ( expr )<br/>
 *               | upper[case] ( expr )<br/>
 *               | trim ( expr )<br/>
 *               | matches ( expr , regexp )<br/>
 *               | trim ( expr )<br/>
 *               | len[gth] ( str )<br/>
 *               | find ( search , expr [, pos] )<br/>
 *               | replace ( str , pos , len , newstr )<br/>
 *               | substitute ( str , find , replace [, occurrences] )<br/>
 * <br/>
 * # array functions<br/>
 *               | len[gth] ( array )<br/>
 *               | get ( array , index )<br/>
 *               ;<br/>
 * <br/>
 * Notes:<br/>
 * - Variables are either all upper case letters (e.g., "ABC") or any character   apart from "]" enclosed by "[" and "]" (e.g., "[Hello World]").<br/>
 * - 'start' and 'end' for function 'substr' are indices that start at 1.<br/>
 * - 'index' for function 'get' starts at 1.<br/>
 * - Index 'end' for function 'substr' is excluded (like Java's 'String.substring(int,int)' method)<br/>
 * - Line comments start with '#'<br/>
 * - Semi-colons (';') or commas (',') can be used as separator in the formulas,<br/>
 *   e.g., 'pow(2,2)' is equivalent to 'pow(2;2)'<br/>
 * - dates have to be of format 'yyyy-MM-dd' or 'yyyy-MM-dd HH:mm:ss'<br/>
 * - times have to be of format 'HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss'<br/>
 * - the characters in square brackets in function names are optional:<br/>
 *   e.g. 'len("abc")' is the same as 'length("abc")'<br/>
 * <br/>
 * A lot of the functions have been modeled after LibreOffice:<br/>
 *   https:&#47;&#47;help.libreoffice.org&#47;Calc&#47;Functions_by_Category<br/>
 * <br/>
 * Additional functions:<br/>
 * - env(String): String<br/>
 * &nbsp;&nbsp;&nbsp;First argument is the name of the environment variable to retrieve.<br/>
 * &nbsp;&nbsp;&nbsp;The result is the value of the environment variable.<br/>
 * <br/>
 * Additional procedures:<br/>
 * - println(...)<br/>
 * &nbsp;&nbsp;&nbsp;One or more arguments are printed as comma-separated list to stdout.<br/>
 * &nbsp;&nbsp;&nbsp;If no argument is provided, a simple line feed is output.<br/>
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-expression &lt;adams.parser.BooleanExpressionText&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The expression to evaluate; expressions that consists solely of a variable 
 * &nbsp;&nbsp;&nbsp;(eg '&#64;{blah}') get automatically wrapped in parentheses, since the expression 
 * &nbsp;&nbsp;&nbsp;string gets interpreted as attached variable instead.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Expression
  extends AbstractExpression {

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
        "Evaluates to 'true' if the expression evaluates to 'true'.\n"
      + "In case of integer or double tokens that arrive at the input, these "
      + "can be accessed in the expression via 'X'; string tokens can be accessed "
      + "via expression '\"X\"' (surrounding double quotes are required).\n"
      + "If the incoming token is either a Report or a ReportHandler, the contents "
      + "of the report get added as values as well (boolean, numeric or string) "
      + "and you can access them via their name instead of 'X'.\n\n"
      + "The following grammar is used for evaluating the boolean expressions:\n\n"
      + getGrammar();
  }

  /**
   * Returns the default expression to use.
   * 
   * @return		the default
   */
  @Override
  protected BooleanExpressionText getDefaultExpression() {
    return new BooleanExpressionText("true");
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String expressionTipText() {
    return
        "The expression to evaluate; expressions that consists solely of a "
      + "variable (eg '@{blah}') get automatically wrapped in parentheses, "
      + "since the expression string gets interpreted as attached variable "
      + "instead.";
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
    boolean	hasString;

    exp       = owner.getVariables().expand(getExpression().getValue());
    hasString = (exp.indexOf("\"X\"") > -1);
    symbols   = new HashMap();
    if ((token != null) && (token.getPayload() != null)) {
      if (token.getPayload() instanceof Integer)
	symbols.put("X", ((Integer) token.getPayload()).doubleValue());
      else if (token.getPayload() instanceof Double)
	symbols.put("X", ((Double) token.getPayload()).doubleValue());
      else if (token.getPayload().getClass().isArray())
	symbols.put("X", token.getPayload());
      else if ((token.getPayload() instanceof String) && hasString)
	exp = exp.replace("\"X\"", "\"" + token.getPayload() + "\"");
      else if (token.getPayload() instanceof Report)
	symbols = BooleanExpression.reportToSymbols((Report) token.getPayload());
      else if ((token.getPayload() instanceof ReportHandler) && (((ReportHandler) token.getPayload()).hasReport()))
	symbols = BooleanExpression.reportToSymbols(((ReportHandler) token.getPayload()).getReport());
    }

    try {
      return doEvaluate(exp, symbols);
    }
    catch (Throwable t) {
      throw new RuntimeException("Failed to evaluate '" + exp + "' with symbols: " + symbols, t);
    }
  }
}
