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
 * SpreadSheetFormula.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.parser;

import adams.core.io.PlaceholderFile;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.parser.spreadsheetformula.Parser;
import adams.parser.spreadsheetformula.Scanner;
import java_cup.runtime.DefaultSymbolFactory;
import java_cup.runtime.SymbolFactory;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Evaluates mathematical expressions.<br>
 * <br>
 * The following grammar is used:<br>
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
 *               | cell<br>
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
 *               | abs ( expr | cell )<br>
 *               | sqrt ( expr | cell )<br>
 *               | log ( expr | cell )<br>
 *               | exp ( expr | cell )<br>
 *               | sin ( expr | cell )<br>
 *               | cos ( expr | cell )<br>
 *               | tan ( expr | cell )<br>
 *               | rint ( expr | cell )<br>
 *               | floor ( expr | cell )<br>
 *               | pow[er] ( expr | cell , expr | cell )<br>
 *               | ceil ( expr | cell )<br>
 *               | sum ( cell1 : cell2 )<br>
 *               | min ( cell1 : cell2 )<br>
 *               | max ( cell1 : cell2 )<br>
 *               | average ( cell1 : cell2 )<br>
 *               | stdev ( cell1 : cell2 )<br>
 *               | stdevp ( cell1 : cell2 )<br>
 *               | countif ( cell1 : cell2 ; expr )<br>
 *               | sumif ( cell1 : cell2 ; expr )<br>
 *               | sumif ( cell1 : cell2 ; expr : sumCell1 : sumCell2 )<br>
 *               | intercept ( cellY1 : cellY2 ; cellX1 : cellX2 )<br>
 *               | slope ( cellY1 : cellY2 ; cellX1 : cellX2 )<br>
 *               | countblank ( cell1 : cell2 )<br>
 *               | year ( expr | cell )<br>
 *               | month ( expr | cell )<br>
 *               | day ( expr | cell )<br>
 *               | hour ( expr | cell )<br>
 *               | minute ( expr | cell )<br>
 *               | second ( expr | cell )<br>
 *               | weekday ( expr | cell )<br>
 *               | weeknum ( expr | cell )<br>
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
 * # obtaining native cell content<br>
 *               | cellobj ( cell )<br>
 * <br>
 * # obtaining cell content as string<br>
 *               | cellstr ( cell )<br>
 * <br>
 * Notes:<br>
 * - Cells are denoted by column in letter and row in digit, e.g., 'C12'.<br>
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
 * Code example 1:
 * <pre>
 * String expr = "pow(BASE,EXPONENT)*MULT";
 * HashMap symbols = new HashMap();
 * symbols.put("BASE", new Double(2));
 * symbols.put("EXPONENT", new Double(9));
 * symbols.put("MULT", new Double(0.1));
 * double result = SpreadSheetFormula.evaluate(expr, symbols);
 * System.out.println(expr + " and " + symbols + " = " + result);
 * </pre>
 *
 * Code Example 2 (uses the "ifelse" construct):
 * <pre>
 * String expr = "ifelse(I<0,pow(BASE,I*0.5),pow(BASE,I))";
 * SpreadSheetFormula.TreeNode tree = SpreadSheetFormula.parse(expr);
 * HashMap symbols = new HashMap();
 * symbols.put("BASE", new Double(2));
 * for (int i = -10; i <= 10; i++) {
 *   symbols.put("I", new Double(i));
 *   double result = SpreadSheetFormula.evaluate(expr, symbols);
 *   System.out.println(expr + " and " + symbols + " = " + result);
 * }
 * </pre>
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
 * &nbsp;&nbsp;&nbsp;The spreadsheet formula to evaluate (must evaluate to a double).
 * &nbsp;&nbsp;&nbsp;default: = 42
 * </pre>
 * 
 * <pre>-symbol &lt;adams.core.base.BaseString&gt; [-symbol ...] (property: symbols)
 * &nbsp;&nbsp;&nbsp;The symbols to initialize the parser with, key-value pairs: name=value.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-reader &lt;adams.data.io.input.SpreadSheetReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The spreadsheet reader for loading the spreadsheet to work on.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.CsvSpreadSheetReader -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.SpreadSheet
 * </pre>
 * 
 * <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The input file to load with the specified reader; ignored if pointing to 
 * &nbsp;&nbsp;&nbsp;directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetFormula
  extends AbstractSymbolEvaluator<Object> {

  /** for serialization. */
  private static final long serialVersionUID = 8014316012335802585L;

  /** the spreadsheet to use. */
  protected SpreadSheet m_Sheet;
  
  /** the spreadsheet reader for loading the spreadsheet. */
  protected SpreadSheetReader m_Reader;
  
  /** the spreadsheet file to read. */
  protected PlaceholderFile m_Input;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates mathematical expressions.\n\n"
    + "The following grammar is used:\n\n"
    + getGrammar();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reader", "reader",
	    new CsvSpreadSheetReader());

    m_OptionManager.add(
	    "input", "input",
	    new PlaceholderFile("."));
  }

  /**
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return
        "expr_list ::= '=' expr_list expr_part | expr_part ;\n"
      + "expr_part ::=  expr ;\n"
      + "\n"
      + "expr      ::=   ( expr )\n"
      + "\n"
      + "# data types\n"
      + "              | number\n"
      + "              | string\n"
      + "              | boolean\n"
      + "              | date\n"
      + "              | cell\n"
      + "\n"
      + "# constants\n"
      + "              | true\n"
      + "              | false\n"
      + "              | pi\n"
      + "              | e\n"
      + "              | now()\n"
      + "              | today()\n"
      + "\n"
      + "# negating numeric value\n"
      + "              | -expr\n"
      + "\n"
      + "# comparisons\n"
      + "              | expr < expr\n"
      + "              | expr <= expr\n"
      + "              | expr > expr\n"
      + "              | expr >= expr\n"
      + "              | expr = expr\n"
      + "              | expr != expr (or: expr <> expr)\n"
      + "\n"
      + "# boolean operations\n"
      + "              | ! expr (or: not expr)\n"
      + "              | expr & expr (or: expr and expr)\n"
      + "              | expr | expr (or: expr or expr)\n"
      + "              | if[else] ( expr , expr (if true) , expr (if false) )\n"
      + "\n"
      + "# arithmetics\n"
      + "              | expr + expr\n"
      + "              | expr - expr\n"
      + "              | expr * expr\n"
      + "              | expr / expr\n"
      + "              | expr ^ expr (power of)\n"
      + "              | expr % expr (modulo)\n"
      + "              ;\n"
      + "\n"
      + "# numeric functions\n"
      + "              | abs ( expr | cell )\n"
      + "              | sqrt ( expr | cell )\n"
      + "              | log ( expr | cell )\n"
      + "              | exp ( expr | cell )\n"
      + "              | sin ( expr | cell )\n"
      + "              | cos ( expr | cell )\n"
      + "              | tan ( expr | cell )\n"
      + "              | rint ( expr | cell )\n"
      + "              | floor ( expr | cell )\n"
      + "              | pow[er] ( expr | cell , expr | cell )\n"
      + "              | ceil ( expr | cell )\n"
      + "              | sum ( cell1 : cell2 )\n"
      + "              | min ( cell1 : cell2 )\n"
      + "              | max ( cell1 : cell2 )\n"
      + "              | average ( cell1 : cell2 )\n"
      + "              | stdev ( cell1 : cell2 )\n"
      + "              | stdevp ( cell1 : cell2 )\n"
      + "              | countif ( cell1 : cell2 ; expr )\n"
      + "              | sumif ( cell1 : cell2 ; expr )\n"
      + "              | sumif ( cell1 : cell2 ; expr : sumCell1 : sumCell2 )\n"
      + "              | intercept ( cellY1 : cellY2 ; cellX1 : cellX2 )\n"
      + "              | slope ( cellY1 : cellY2 ; cellX1 : cellX2 )\n"
      + "              | countblank ( cell1 : cell2 )\n"
      + "              | year ( expr | cell )\n"
      + "              | month ( expr | cell )\n"
      + "              | day ( expr | cell )\n"
      + "              | hour ( expr | cell )\n"
      + "              | minute ( expr | cell )\n"
      + "              | second ( expr | cell )\n"
      + "              | weekday ( expr | cell )\n"
      + "              | weeknum ( expr | cell )\n"
      + "\n"
      + "# string functions\n"
      + "              | substr ( expr , start [, end] )\n"
      + "              | left ( expr , len )\n"
      + "              | mid ( expr , start , len )\n"
      + "              | right ( expr , len )\n"
      + "              | rept ( expr , count )\n"
      + "              | concatenate ( expr1 , expr2 [, expr3-5] )\n"
      + "              | lower[case] ( expr )\n"
      + "              | upper[case] ( expr )\n"
      + "              | trim ( expr )\n"
      + "              | matches ( expr , regexp )\n"
      + "              | trim ( expr )\n"
      + "              | len[gth] ( str )\n"
      + "              | find ( search , expr [, pos] )\n"
      + "              | replace ( str , pos , len , newstr )\n"
      + "              | substitute ( str , find , replace [, occurrences] )\n"
      + "              ;\n"
      + "\n"
      + "# obtaining native cell content\n"
      + "              | cellobj ( cell )\n"
      + "\n"
      + "# obtaining cell content as string\n"
      + "              | cellstr ( cell )\n"
      + "\n"
      + "Notes:\n"
      + "- Cells are denoted by column in letter and row in digit, e.g., 'C12'.\n"
      + "- 'start' and 'end' for function 'substr' are indices that start at 1.\n"
      + "- Index 'end' for function 'substr' is excluded (like Java's 'String.substring(int,int)' method)\n"
      + "- Line comments start with '#'.\n"
      + "- Semi-colons (';') or commas (',') can be used as separator in the formulas,\n"
      + "  e.g., 'pow(2,2)' is equivalent to 'pow(2;2)'\n"
      + "- dates have to be of format 'yyyy-MM-dd' or 'yyyy-MM-dd HH:mm:ss'\n"
      + "- times have to be of format 'HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss'\n"
      + "- the characters in square brackets in function names are optional:\n"
      + "  e.g. 'len(\"abc\")' is the same as 'length(\"abc\")'\n"
      + "\n"
      + "A lot of the functions have been modeled after LibreOffice:\n"
      + "  https://help.libreoffice.org/Calc/Functions_by_Category\n"
      + "\n"
      + "Additional functions:\n"
      + ParserHelper.getFunctionOverview() + "\n"
      + "\n"
      + "Additional procedures:\n"
      + ParserHelper.getProcedureOverview() + "\n"
      ;
  }

  /**
   * Returns the default expression to use.
   *
   * @return		the default expression
   */
  @Override
  protected String getDefaultExpression() {
    return "= 42";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String expressionTipText() {
    return "The spreadsheet formula to evaluate (must evaluate to a double).";
  }

  /**
   * Sets the spreadsheet reader that loads the sheet.
   *
   * @param value	the reader
   */
  public void setReader(SpreadSheetReader value) {
    m_Reader = value;
  }

  /**
   * Returns the spreadsheet reader that loads the sheet.
   *
   * @return		the reader
   */
  public SpreadSheetReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The spreadsheet reader for loading the spreadsheet to work on.";
  }

  /**
   * Sets the spreadsheet file to load, ignored if pointing to directory.
   *
   * @param value	the input file
   */
  public void setInput(PlaceholderFile value) {
    m_Input = value;
  }

  /**
   * Returns the spreadsheet file to load, ignored if pointing to directory.
   *
   * @return		the input file
   */
  public PlaceholderFile getInput() {
    return m_Input;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputTipText() {
    return "The input file to load with the specified reader; ignored if pointing to directory.";
  }

  /**
   * Sets the underlying spreadsheet.
   * 
   * @param value	the spreadsheet
   */
  public void setSheet(SpreadSheet value) {
    m_Sheet = value;
  }
  
  /**
   * Returns the underlying spreadsheet.
   * 
   * @return		the spreadsheet
   */
  public SpreadSheet getSheet() {
    return m_Sheet;
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
   * Performs the actual evaluation.
   *
   * @param symbols	the symbols to use
   * @return		the evaluation, or null in case of error
   * @throws Exception	if evaluation fails
   */
  @Override
  protected Object doEvaluate(HashMap symbols) throws Exception {
    return evaluate(m_Expression, symbols, m_Sheet);
  }

  /**
   * Loads the spreadsheet from disk, if possible.
   */
  protected void loadSheet() {
    if (m_Input.exists() && !m_Input.isDirectory())
      m_Sheet = m_Reader.read(m_Input);
  }
  
  /**
   * Performs the evaluation.
   *
   * @return		the evaluation, or null in case of error
   * @throws Exception	if evaluation fails
   */
  @Override
  public Object evaluate() throws Exception {
    loadSheet();
    return super.evaluate();
  }

  /**
   * Parses and evaluates the given expression.
   * Returns the result of the mathematical expression, based on the given
   * values of the symbols.
   *
   * @param expr	the expression to evaluate
   * @param symbols	the symbol/value mapping
   * @return		the evaluated result
   * @throws Exception	if something goes wrong
   */
  public static Object evaluate(String expr, HashMap symbols, SpreadSheet sheet) throws Exception {
    SymbolFactory 		sf;
    ByteArrayInputStream 	parserInput;
    Parser 			parser;

    // remove leading "="
    expr = expr.trim();
    if (expr.startsWith("="))
      expr = expr.substring(1);
    
    sf          = new DefaultSymbolFactory();
    parserInput = new ByteArrayInputStream(expr.getBytes());
    parser      = new Parser(new Scanner(parserInput, sf), sf);
    parser.setSymbols(symbols);
    parser.setSheet(sheet);
    parser.parse();

    return parser.getResult();
  }

  /**
   * Runs the evaluator from command-line.
   *
   * @param args	the command-line options, use "-help" to list them
   */
  public static void main(String[] args) {
    runEvaluator(SpreadSheetFormula.class, args);
  }
}
