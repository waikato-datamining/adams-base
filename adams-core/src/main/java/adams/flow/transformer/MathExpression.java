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
 * MathExpression.java
 * Copyright (C) 2009-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.RoundingType;
import adams.data.RoundingUtils;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Token;
import adams.parser.GrammarSupplier;
import adams.parser.MathematicalExpression;
import adams.parser.MathematicalExpressionText;

import java.util.HashMap;

/**
 <!-- globalinfo-start -->
 * Evaluates a mathematical expression.<br>
 * The input value (double or integer) can be accessed via 'X'.<br>
 * Variables are supported as well, e.g.: pow(X,&#64;{exp}) with '&#64;{exp}' being a variable available at execution time.<br>
 * <br>
 * The following grammar is used for the expressions:<br>
 * <br>
 * expr_list ::= '=' expr_list expr_part | expr_part ;<br>
 * expr_part ::=  expr ;<br>
 * <br>
 * expr      ::=   ( expr )<br>
 * <br>
 * # data types<br>
 *               | number<br>
 *               | string<br>
 *               | boolean<br>
 *               | date<br>
 * <br>
 * # constants<br>
 *               | true<br>
 *               | false<br>
 *               | pi<br>
 *               | e<br>
 *               | now()<br>
 *               | today()<br>
 * <br>
 * # negating numeric value<br>
 *               | -expr<br>
 * <br>
 * # comparisons<br>
 *               | expr &lt; expr<br>
 *               | expr &lt;= expr<br>
 *               | expr &gt; expr<br>
 *               | expr &gt;= expr<br>
 *               | expr = expr<br>
 *               | expr != expr (or: expr &lt;&gt; expr)<br>
 * <br>
 * # boolean operations<br>
 *               | ! expr (or: not expr)<br>
 *               | expr &amp; expr (or: expr and expr)<br>
 *               | expr | expr (or: expr or expr)<br>
 *               | if[else] ( expr , expr (if true) , expr (if false) )<br>
 *               | ifmissing ( variable , expr (default value if variable is missing) )<br>
 *               | isNaN ( expr )<br>
 * <br>
 * # arithmetics<br>
 *               | expr + expr<br>
 *               | expr - expr<br>
 *               | expr * expr<br>
 *               | expr &#47; expr<br>
 *               | expr ^ expr (power of)<br>
 *               | expr % expr (modulo)<br>
 *               ;<br>
 * <br>
 * # numeric functions<br>
 *               | abs ( expr )<br>
 *               | sqrt ( expr )<br>
 *               | cbrt ( expr )<br>
 *               | log ( expr )<br>
 *               | log10 ( expr )<br>
 *               | exp ( expr )<br>
 *               | sin ( expr )<br>
 *               | sinh ( expr )<br>
 *               | cos ( expr )<br>
 *               | cosh ( expr )<br>
 *               | tan ( expr )<br>
 *               | tanh ( expr )<br>
 *               | atan ( expr )<br>
 *               | atan2 ( exprY , exprX )<br>
 *               | hypot ( exprX , exprY )<br>
 *               | signum ( expr )<br>
 *               | rint ( expr )<br>
 *               | floor ( expr )<br>
 *               | pow[er] ( expr , expr )<br>
 *               | ceil ( expr )<br>
 *               | min ( expr1 , expr2 )<br>
 *               | max ( expr1 , expr2 )<br>
 *               | year ( expr )<br>
 *               | month ( expr )<br>
 *               | day ( expr )<br>
 *               | hour ( expr )<br>
 *               | minute ( expr )<br>
 *               | second ( expr )<br>
 *               | weekday ( expr )<br>
 *               | weeknum ( expr )<br>
 * <br>
 * # string functions<br>
 *               | substr ( expr , start [, end] )<br>
 *               | left ( expr , len )<br>
 *               | mid ( expr , start , len )<br>
 *               | right ( expr , len )<br>
 *               | rept ( expr , count )<br>
 *               | concatenate ( expr1 , expr2 [, expr3-5] )<br>
 *               | lower[case] ( expr )<br>
 *               | upper[case] ( expr )<br>
 *               | trim ( expr )<br>
 *               | matches ( expr , regexp )<br>
 *               | trim ( expr )<br>
 *               | len[gth] ( str )<br>
 *               | find ( search , expr [, pos] )<br>
 *               | replace ( str , pos , len , newstr )<br>
 *               | substitute ( str , find , replace [, occurrences] )<br>
 *               ;<br>
 * <br>
 * Notes:<br>
 * - Variables are either all upper case letters (e.g., "ABC") or any character   apart from "]" enclosed by "[" and "]" (e.g., "[Hello World]").<br>
 * - 'start' and 'end' for function 'substr' are indices that start at 1.<br>
 * - Index 'end' for function 'substr' is excluded (like Java's 'String.substring(int,int)' method)<br>
 * - Line comments start with '#'.<br>
 * - Semi-colons (';') or commas (',') can be used as separator in the formulas,<br>
 *   e.g., 'pow(2,2)' is equivalent to 'pow(2;2)'<br>
 * - dates have to be of format 'yyyy-MM-dd' or 'yyyy-MM-dd HH:mm:ss'<br>
 * - times have to be of format 'HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss'<br>
 * - the characters in square brackets in function names are optional:<br>
 *   e.g. 'len("abc")' is the same as 'length("abc")'<br>
 * <br>
 * A lot of the functions have been modeled after LibreOffice:<br>
 *   https:&#47;&#47;help.libreoffice.org&#47;Calc&#47;Functions_by_Category<br>
 * <br>
 * Additional functions:<br>
 * - env(String): String<br>
 * &nbsp;&nbsp;&nbsp;First argument is the name of the environment variable to retrieve.<br>
 * &nbsp;&nbsp;&nbsp;The result is the value of the environment variable.<br>
 * <br>
 * Additional procedures:<br>
 * - println(...)<br>
 * &nbsp;&nbsp;&nbsp;One or more arguments are printed as comma-separated list to stdout.<br>
 * &nbsp;&nbsp;&nbsp;If no argument is provided, a simple line feed is output.<br>
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Long<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: MathExpression
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
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-expression &lt;adams.parser.MathematicalExpressionText&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The mathematical expression to evaluate; the input value can be accessed 
 * &nbsp;&nbsp;&nbsp;via 'X'.
 * &nbsp;&nbsp;&nbsp;default: X
 * </pre>
 * 
 * <pre>-output-value-pair &lt;boolean&gt; (property: outputValuePair)
 * &nbsp;&nbsp;&nbsp;If enabled, a double array with X and Y is output and not just Y.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-round-output &lt;boolean&gt; (property: roundOutput)
 * &nbsp;&nbsp;&nbsp;If enabled, the output of the expression is rounding with the specified 
 * &nbsp;&nbsp;&nbsp;type of rounding.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-rounding-type &lt;ROUND|CEILING|FLOOR&gt; (property: roundingType)
 * &nbsp;&nbsp;&nbsp;The rounding type to perform on the doubles passing through.
 * &nbsp;&nbsp;&nbsp;default: ROUND
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see adams.parser.MathematicalExpression
 */
public class MathExpression
  extends AbstractTransformer
  implements GrammarSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -8477454145267616359L;

  /** the mathematical expression to evaluate. */
  protected MathematicalExpressionText m_Expression;

  /** whether to output X and Y as double array or just Y. */
  protected boolean m_OutputValuePair;

  /** whether to round the output of the expression. */
  protected boolean m_RoundOutput;

  /** the rounding type to perform. */
  protected RoundingType m_RoundingType;

  /** the number of decimals. */
  protected int m_NumDecimals;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates a mathematical expression.\n"
      + "The input value (double or integer) can be accessed via '" + MathematicalExpression.PLACEHOLDER_OBJECT + "'.\n"
      + "Variables are supported as well, e.g.: pow(X,@{exp}) with '@{exp}' "
      + "being a variable available at execution time.\n\n"
      + "The following grammar is used for the expressions:\n\n"
      + getGrammar();
  }

  /**
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return new MathematicalExpression().getGrammar();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "expression", "expression",
      new MathematicalExpressionText(MathematicalExpression.PLACEHOLDER_OBJECT));

    m_OptionManager.add(
      "output-value-pair", "outputValuePair",
      false);

    m_OptionManager.add(
      "round-output", "roundOutput",
      false);

    m_OptionManager.add(
      "rounding-type", "roundingType",
      RoundingType.ROUND);

    m_OptionManager.add(
      "num-decimals", "numDecimals",
      0, 0, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "expression", m_Expression);
    result += QuickInfoHelper.toString(this, "roundingType", (m_RoundOutput ? "" + m_RoundingType : "no rounding"), ", ");
    result += QuickInfoHelper.toString(this, "numDecimals", m_NumDecimals, ", decimals: ");

    return result;
  }

  /**
   * Sets the mathematical expression to evaluate.
   *
   * @param value	the expression
   */
  public void setExpression(MathematicalExpressionText value) {
    m_Expression = value;
    reset();
  }

  /**
   * Returns the mathematical expression to evaluate.
   *
   * @return		the expression
   */
  public MathematicalExpressionText getExpression() {
    return m_Expression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expressionTipText() {
    return
        "The mathematical expression to evaluate; the input value can be "
      + "accessed via '" + MathematicalExpression.PLACEHOLDER_OBJECT + "'.";
  }

  /**
   * Sets whether to output a double array with X and Y or just Y.
   *
   * @param value	if true then X and Y are output
   */
  public void setOutputValuePair(boolean value) {
    m_OutputValuePair = value;
    reset();
  }

  /**
   * Returns whether to output a double array with X and Y or just Y.
   *
   * @return		true if X and Y are ouput
   */
  public boolean getOutputValuePair() {
    return m_OutputValuePair;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputValuePairTipText() {
    return "If enabled, a double array with X and Y is output and not just Y.";
  }

  /**
   * Sets whether to round the output of the expression.
   *
   * @param value	if true then round output
   */
  public void setRoundOutput(boolean value) {
    m_RoundOutput = value;
    reset();
  }

  /**
   * Returns whether to round the output of the expression.
   *
   * @return		true if to round output
   */
  public boolean getRoundOutput() {
    return m_RoundOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String roundOutputTipText() {
    return "If enabled, the output of the expression is rounding with the specified type of rounding.";
  }

  /**
   * Sets the roundingType to perform on the doubles.
   *
   * @param value	the roundingType
   */
  public void setRoundingType(RoundingType value) {
    m_RoundingType = value;
    reset();
  }

  /**
   * Returns the roundingType to perform on the doubles.
   *
   * @return		the roundingType
   */
  public RoundingType getRoundingType() {
    return m_RoundingType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String roundingTypeTipText() {
    return "The rounding type to perform on the doubles passing through.";
  }

  /**
   * Sets the number of decimals after the decimal point to use.
   *
   * @param value	the number of decimals
   */
  public void setNumDecimals(int value) {
    if (getOptionManager().isValid("numDecimals", value)) {
      m_NumDecimals = value;
      reset();
    }
  }

  /**
   * Returns the number of decimals after the decimal point to use.
   *
   * @return		the number of decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDecimalsTipText() {
    return "The number of decimals after the decimal point to use.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted classes
   */
  public Class[] accepts() {
    return new Class[]{Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Number.class, Report.class, ReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the generated classes
   */
  public Class[] generates() {
    if (m_OutputValuePair)
      return new Class[]{Double[].class};
    else if (m_RoundOutput && (m_NumDecimals == 0))
      return new Class[]{Integer.class};
    else
      return new Class[]{Double.class};
  }

  /**
   * Initializes the sub-actors for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if ((m_Expression == null) || (m_Expression.getValue().length() == 0))
	result = "No expression provided!";
    }

    return result;
  }

  /**
   * Applies rounding if necessary.
   *
   * @param value	the value to round
   * @return		the potentially modified value
   * @see		#getRoundOutput()
   */
  protected Double applyRounding(double value) {
    if (!getRoundOutput())
      return value;
    else
      return RoundingUtils.apply(m_RoundingType, value, m_NumDecimals);
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
    Double	x;
    Double	y;
    String	exp;

    result = null;

    exp = m_Expression.getValue();
    try {
      // replace variables with their actual values
      if (isLoggingEnabled())
	getLogger().info("Expression: " + exp);
      exp = getVariables().expand(exp);
      if (isLoggingEnabled())
	getLogger().info("--> expanded: " + exp);
      
      // get input
      symbols = MathematicalExpression.objectToSymbols(m_InputToken.getPayload());
      x       = (m_InputToken.getPayload() instanceof Number ? ((Number) m_InputToken.getPayload()).doubleValue() : null);
      y       = MathematicalExpression.evaluate(exp, symbols);

      if (!Double.isNaN(y)) {
	y = applyRounding(y);
	if (m_OutputValuePair)
	  m_OutputToken = new Token(new Double[]{x, y});
	else if (getRoundOutput() && (m_NumDecimals == 0))
	  m_OutputToken = new Token(y.intValue());
	else
	  m_OutputToken = new Token(y);
	if (isLoggingEnabled())
	  getLogger().info("--> y" + (getRoundOutput() ? " (" + m_RoundingType + ")" : "") + ": " + y);
      }
      else {
	result = "Failed to generate output?";
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Error evaluating: " + exp, e);
    }

    return result;
  }
}
