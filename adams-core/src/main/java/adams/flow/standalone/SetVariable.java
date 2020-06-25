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
 * SetVariable.java
 * Copyright (C) 2009-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.VariableName;
import adams.core.VariableUpdater;
import adams.core.base.BaseText;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.VariableValueType;
import adams.parser.BooleanExpression;
import adams.parser.MathematicalExpression;
import adams.parser.StringExpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Sets the value of a variable.<br>
 * Optionally, the specified value can be expanded, in case it is made up of variables itself.<br>
 * It is also possible to override the variable value with the value obtained from an environment variable.<br>
 * <br>
 * Grammar for mathematical expressions (value type 'MATH_EXPRESSION, MATH_EXPRESSION_ROUND'):<br>
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
 *               | has ( variable )<br>
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
 *               | find ( search , expr [, pos] ) (find 'search' in 'expr', return 1-based position)<br>
 *               | replace ( str , pos , len , newstr )<br>
 *               | substitute ( str , find , replace [, occurrences] )<br>
 *               | str ( expr )<br>
 *               | str ( expr  , numdecimals )<br>
 *               | str ( expr  , decimalformat )<br>
 *               | ext ( file_str )  (extracts extension from file)<br>
 *               | replaceext ( file_str, ext_str )  (replaces the extension of the file with the new one)<br>
 *               ;<br>
 * <br>
 * Notes:<br>
 * - Variables are either all alphanumeric and _, starting with uppercase letter (e.g., "ABc_12"),<br>
 *   any character apart from "]" enclosed by "[" and "]" (e.g., "[Hello World]") or<br>
 *   enclosed by single quotes (e.g., "'Hello World'").<br>
 * - 'start' and 'end' for function 'substr' are indices that start at 1.<br>
 * - Index 'end' for function 'substr' is excluded (like Java's 'String.substring(int,int)' method)<br>
 * - Line comments start with '#'.<br>
 * - Semi-colons (';') or commas (',') can be used as separator in the formulas,<br>
 *   e.g., 'pow(2,2)' is equivalent to 'pow(2;2)'<br>
 * - dates have to be of format 'yyyy-MM-dd' or 'yyyy-MM-dd HH:mm:ss'<br>
 * - times have to be of format 'HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss'<br>
 * - the characters in square brackets in function names are optional:<br>
 *   e.g. 'len("abc")' is the same as 'length("abc")'<br>
 * - 'str' uses java.text.DecimalFormat when supplying a format string<br>
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
 * <br>
 * <br>
 * Grammar for boolean expressions (value type 'BOOL_EXPRESSION'):<br>
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
 *               | has ( variable )<br>
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
 *               | find ( search , expr [, pos] ) (find 'search' in 'expr', return 1-based position)<br>
 *               | replace ( str , pos , len , newstr )<br>
 *               | substitute ( str , find , replace [, occurrences] )<br>
 *               | str ( expr )<br>
 *               | str ( expr  , numdecimals )<br>
 *               | str ( expr  , decimalformat )<br>
 *               | ext ( file_str )  (extracts extension from file)<br>
 *               | replaceext ( file_str, ext_str )  (replaces the extension of the file with the new one)<br>
 * <br>
 * # array functions<br>
 *               | len[gth] ( array )<br>
 *               | get ( array , index )<br>
 *               ;<br>
 * <br>
 * Notes:<br>
 * - Variables are either all alphanumeric and _, starting with uppercase letter (e.g., "ABc_12"),<br>
 *   any character apart from "]" enclosed by "[" and "]" (e.g., "[Hello World]") or<br>
 *   enclosed by single quotes (e.g., "'Hello World'").<br>
 * - 'start' and 'end' for function 'substr' are indices that start at 1.<br>
 * - 'index' for function 'get' starts at 1.<br>
 * - Index 'end' for function 'substr' is excluded (like Java's 'String.substring(int,int)' method)<br>
 * - Line comments start with '#'<br>
 * - Semi-colons (';') or commas (',') can be used as separator in the formulas,<br>
 *   e.g., 'pow(2,2)' is equivalent to 'pow(2;2)'<br>
 * - dates have to be of format 'yyyy-MM-dd' or 'yyyy-MM-dd HH:mm:ss'<br>
 * - times have to be of format 'HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss'<br>
 * - the characters in square brackets in function names are optional:<br>
 *   e.g. 'len("abc")' is the same as 'length("abc")'<br>
 * - 'str' uses java.text.DecimalFormat when supplying a format string<br>
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
 * <br>
 * <br>
 * Grammar for string expressions (value type 'STRING_EXPRESSION'):<br>
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
 *               | has ( variable )<br>
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
 *               | find ( search , expr [, pos] ) (find 'search' in 'expr', return 1-based position)<br>
 *               | replace ( str , pos , len , newstr )<br>
 *               | substitute ( str , find , replace [, occurrences] )<br>
 *               | str ( expr )<br>
 *               | str ( expr  , numdecimals )<br>
 *               | str ( expr  , decimalformat )<br>
 *               | ext ( file_str )  (extracts extension from file)<br>
 *               | replaceext ( file_str, ext_str )  (replaces the extension of the file with the new one)<br>
 * <br>
 * # array functions<br>
 *               | len[gth] ( array )<br>
 *               | get ( array , index )<br>
 *               ;<br>
 * <br>
 * Notes:<br>
 * - Variables are either all alphanumeric and _, starting with uppercase letter (e.g., "ABc_12"),<br>
 *   any character apart from "]" enclosed by "[" and "]" (e.g., "[Hello World]") or<br>
 *   enclosed by single quotes (e.g., "'Hello World'").<br>
 * - 'start' and 'end' for function 'substr' are indices that start at 1.<br>
 * - 'index' for function 'get' starts at 1.<br>
 * - Index 'end' for function 'substr' is excluded (like Java's 'String.substring(int,int)' method)<br>
 * - Line comments start with '#'<br>
 * - Semi-colons (';') or commas (',') can be used as separator in the formulas,<br>
 *   e.g., 'pow(2,2)' is equivalent to 'pow(2;2)'<br>
 * - dates have to be of format 'yyyy-MM-dd' or 'yyyy-MM-dd HH:mm:ss'<br>
 * - times have to be of format 'HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss'<br>
 * - the characters in square brackets in function names are optional:<br>
 *   e.g. 'len("abc")' is the same as 'length("abc")'<br>
 * - 'str' uses java.text.DecimalFormat when supplying a format string<br>
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
 * &nbsp;&nbsp;&nbsp;default: SetVariable
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-var-name &lt;adams.core.VariableName&gt; (property: variableName)
 * &nbsp;&nbsp;&nbsp;The name of the variable to update.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 * <pre>-var-value &lt;adams.core.base.BaseText&gt; (property: variableValue)
 * &nbsp;&nbsp;&nbsp;The value for the variable to use.
 * &nbsp;&nbsp;&nbsp;default: value
 * </pre>
 *
 * <pre>-value-type &lt;STRING|MATH_EXPRESSION|MATH_EXPRESSION_ROUND|BOOL_EXPRESSION|STRING_EXPRESSION|FILE_FORWARD_SLASHES&gt; (property: valueType)
 * &nbsp;&nbsp;&nbsp;How to interpret the 'value' string.
 * &nbsp;&nbsp;&nbsp;default: STRING
 * </pre>
 *
 * <pre>-expand-value &lt;boolean&gt; (property: expandValue)
 * &nbsp;&nbsp;&nbsp;If enabled, the value gets expanded first in case it is made up of variables
 * &nbsp;&nbsp;&nbsp;itself.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-override-with-env-var &lt;boolean&gt; (property: overrideWithEnvVar)
 * &nbsp;&nbsp;&nbsp;If enabled, the value gets overridden by the value obtained from the specified
 * &nbsp;&nbsp;&nbsp;environment variable.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-env-variable &lt;java.lang.String&gt; (property: envVariable)
 * &nbsp;&nbsp;&nbsp;The name of the environment variable to use for overriding the variable
 * &nbsp;&nbsp;&nbsp;value.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-env-var-optional &lt;boolean&gt; (property: envVarOptional)
 * &nbsp;&nbsp;&nbsp;If enabled, the environment does not have to exist and the currently set
 * &nbsp;&nbsp;&nbsp;value is used instead; if not optional, a missing environment variable will
 * &nbsp;&nbsp;&nbsp;generate an error.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SetVariable
  extends AbstractStandalone
  implements VariableUpdater {

  /** for serialization. */
  private static final long serialVersionUID = -3383735680425581504L;

  /** the name of the variable. */
  protected VariableName m_VariableName;

  /** the value of the variable. */
  protected BaseText m_VariableValue;

  /** how to interpret the value. */
  protected VariableValueType m_ValueType;

  /** whether to expand the value. */
  protected boolean m_ExpandValue;

  /** whether to override using an environment variable. */
  protected boolean m_OverrideWithEnvVar;

  /** the environment variable to use. */
  protected String m_EnvVariable;

  /** whether to override using an environment variable. */
  protected boolean m_EnvVarOptional;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Sets the value of a variable.\n"
	+ "Optionally, the specified value can be expanded, "
	+ "in case it is made up of variables itself.\n"
	+ "It is also possible to override the variable value with the value "
	+ "obtained from an environment variable.\n"
	+ "\n"
	+ "Grammar for mathematical expressions (value type '" + VariableValueType.MATH_EXPRESSION + ", " + VariableValueType.MATH_EXPRESSION_ROUND + "'):\n\n"
	+ new MathematicalExpression().getGrammar()
	+ "\n\n"
	+ "Grammar for boolean expressions (value type '" + VariableValueType.BOOL_EXPRESSION + "'):\n\n"
	+ new BooleanExpression().getGrammar()
	+ "\n\n"
	+ "Grammar for string expressions (value type '" + VariableValueType.STRING_EXPRESSION + "'):\n\n"
	+ new adams.parser.StringExpression().getGrammar();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "var-name", "variableName",
      new VariableName());

    m_OptionManager.add(
      "var-value", "variableValue",
      new BaseText("value"));

    m_OptionManager.add(
      "value-type", "valueType",
      VariableValueType.STRING);

    m_OptionManager.add(
      "expand-value", "expandValue",
      false);

    m_OptionManager.add(
      "override-with-env-var", "overrideWithEnvVar",
      false);

    m_OptionManager.add(
      "env-variable", "envVariable",
      "");

    m_OptionManager.add(
      "env-var-optional", "envVarOptional",
      true);
  }

  /**
   * Sets the name of the variable to update.
   *
   * @param value	the name
   */
  public void setVariableName(String value) {
    setVariableName(new VariableName(value));
  }

  /**
   * Sets the name of the variable to update.
   *
   * @param value	the name
   */
  public void setVariableName(VariableName value) {
    m_VariableName = value;
    reset();
  }

  /**
   * Returns the name of the variable to update.
   *
   * @return		the name
   */
  public VariableName getVariableName() {
    return m_VariableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNameTipText() {
    return "The name of the variable to update.";
  }

  /**
   * Sets the value of the variable to update.
   *
   * @param value	the value
   */
  public void setVariableValue(String value) {
    setVariableValue(new BaseText(value));
  }

  /**
   * Sets the value of the variable to update.
   *
   * @param value	the value
   */
  public void setVariableValue(BaseText value) {
    m_VariableValue = value;
    reset();
  }

  /**
   * Returns the value of the variable to update.
   *
   * @return		the value
   */
  public BaseText getVariableValue() {
    return m_VariableValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableValueTipText() {
    return "The value for the variable to use.";
  }

  /**
   * Sets how to interpret the value string.
   *
   * @param value	the type
   */
  public void setValueType(VariableValueType value) {
    m_ValueType = value;
    reset();
  }

  /**
   * Returns how to interpret the value string.
   *
   * @return		the type
   */
  public VariableValueType getValueType() {
    return m_ValueType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTypeTipText() {
    return "How to interpret the 'value' string.";
  }

  /**
   * Sets whether to expand the value before settting it
   * (eg if it is made up of variables itself).
   *
   * @param value	true if to expand
   */
  public void setExpandValue(boolean value) {
    m_ExpandValue = value;
    reset();
  }

  /**
   * Returns whether the value gets expanded before setting it
   * (eg if it is made up of variables itself).
   *
   * @return		true if expanded
   */
  public boolean getExpandValue() {
    return m_ExpandValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expandValueTipText() {
    return "If enabled, the value gets expanded first in case it is made up of variables itself.";
  }

  /**
   * Sets whether to override the value with the one obtained from the
   * specified environment variable.
   *
   * @param value	true if to override
   */
  public void setOverrideWithEnvVar(boolean value) {
    m_OverrideWithEnvVar = value;
    reset();
  }

  /**
   * Returns whether to override the value with the one obtained from the
   * specified environment variable.
   *
   * @return		true if to override
   */
  public boolean getOverrideWithEnvVar() {
    return m_OverrideWithEnvVar;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overrideWithEnvVarTipText() {
    return "If enabled, the value gets overridden by the value obtained from the specified environment variable.";
  }

  /**
   * Sets the name of the environment variable to use.
   *
   * @param value	the name
   */
  public void setEnvVariable(String value) {
    m_EnvVariable = value;
    reset();
  }

  /**
   * Returns the name of the environment variable to use.
   *
   * @return		the name
   */
  public String getEnvVariable() {
    return m_EnvVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String envVariableTipText() {
    return "The name of the environment variable to use for overriding the variable value.";
  }

  /**
   * Sets whether to the environment variable is optional.
   * If not optional, a missing variable will generate an error.
   *
   * @param value	true if optional
   */
  public void setEnvVarOptional(boolean value) {
    m_EnvVarOptional = value;
    reset();
  }

  /**
   * Returns whether to the environment variable is optional.
   * If not optional, a missing variable will generate an error.
   *
   * @return		true if optional
   */
  public boolean getEnvVarOptional() {
    return m_EnvVarOptional;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String envVarOptionalTipText() {
    return "If enabled, the environment does not have to exist and the "
      + "currently set value is used instead; if not optional, a missing "
      + "environment variable will generate an error.";
  }

  /**
   * Returns whether variables are being updated.
   *
   * @return		true if variables are updated
   */
  public boolean isUpdatingVariables() {
    return !getSkip();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		variable;
    String		result;
    String		value;
    List<String>	options;

    variable = QuickInfoHelper.getVariable(this, "variableName");
    if (variable != null)
      result = variable;
    else
      result = m_VariableName.paddedValue();

    if (m_OverrideWithEnvVar && !m_EnvVariable.isEmpty()) {
      result += QuickInfoHelper.toString(this, "envVariable", m_EnvVariable, ", use env var: ");
    }
    else {
      value = QuickInfoHelper.toString(this, "variableValue", Shortening.shortenEnd(m_VariableValue.getValue(), QuickInfoHelper.MAX_ARRAY_STRING_LENGTH), " = ");
      if (value != null)
	result += value;

      // further options
      options = new ArrayList<>();
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "valueType", m_ValueType));
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "expandValue", m_ExpandValue, "expand"));
      result += QuickInfoHelper.flatten(options);
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
    String	value;

    result = getOptionManager().ensureVariableForPropertyExists("variableName");
    if (result == null)
      result = getOptionManager().ensureVariableForPropertyExists("variableValue");

    value = null;
    if (result == null) {
      if (m_OverrideWithEnvVar) {
	if (m_EnvVariable.isEmpty()) {
	  result = "No environment variable specified!";
	}
	else {
	  value = System.getenv(m_EnvVariable);
	  if (value == null) {
	    if (!m_EnvVarOptional)
	      result = "Environment variable '" + m_EnvVariable + "' not set!";
	    else if (isLoggingEnabled())
	      getLogger().info("Environment variable '" + m_EnvVariable + "' not set?");
	  }
	}
      }
    }

    if (result == null) {
      if (value == null) {
	value = m_VariableValue.getValue();
	if (m_ExpandValue) {
	  value = getVariables().expand(value);
	  if (isLoggingEnabled())
	    getLogger().info("Expanded value: " + value);
	}
      }

      switch (m_ValueType) {
	case STRING:
	  // nothing to do
	  break;
	case MATH_EXPRESSION:
	  try {
	    value = "" + MathematicalExpression.evaluate(value, new HashMap());
	  }
	  catch (Exception e) {
	    result = handleException("Failed to parse mathematical expression: " + value, e);
	  }
	  break;
	case MATH_EXPRESSION_ROUND:
	  try {
	    value = "" + Math.round(MathematicalExpression.evaluate(value, new HashMap()));
	  }
	  catch (Exception e) {
	    result = handleException("Failed to parse mathematical expression: " + value, e);
	  }
	  break;
	case BOOL_EXPRESSION:
	  try {
	    value = "" + BooleanExpression.evaluate(value, new HashMap());
	  }
	  catch (Exception e) {
	    result = handleException("Failed to parse boolean expression: " + value, e);
	  }
	  break;
	case STRING_EXPRESSION:
	  try {
	    value = "" + StringExpression.evaluate(value, new HashMap());
	  }
	  catch (Exception e) {
	    result = handleException("Failed to parse string expression: " + value, e);
	  }
	  break;
	case FILE_FORWARD_SLASHES:
	  try {
	    value = FileUtils.useForwardSlashes(new PlaceholderFile(value).getAbsolutePath());
	  }
	  catch (Exception e) {
	    result = handleException("Failed to generate file using forward slashes: " + value, e);
	  }
	  break;
	default:
	  throw new IllegalStateException("Unhandled value type: " + m_ValueType);
      }

      getVariables().set(m_VariableName.getValue(), value);
      if (isLoggingEnabled())
	getLogger().info("Setting variable '" + m_VariableName + "': " + value);
    }

    return result;
  }
}
