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
 * SpreadSheetQuery.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.parser;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.logging.Level;

import java_cup.runtime.DefaultSymbolFactory;
import java_cup.runtime.SymbolFactory;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.parser.spreadsheetquery.Parser;
import adams.parser.spreadsheetquery.Scanner;

/**
 <!-- globalinfo-start -->
 * Evaluates spreadsheet subset queries.<br/>
 * <br/>
 * The following grammar is used:<br/>
 * <br/>
 * expr_list ::= expr_list expr_part | expr_part;<br/>
 * <br/>
 * expr_part ::= select | update | delete;<br/>
 * <br/>
 * select    ::=   SELECT col_list [limit]<br/>
 *               | SELECT col_list WHERE cond_list [limit]<br/>
 *               | SELECT col_list ORDER BY order_list [limit]<br/>
 *               | SELECT col_list WHERE cond_list ORDER BY order_list [limit]<br/>
 *               | SELECT agg_list<br/>
 *               | SELECT agg_list GROUP BY col_list<br/>
 *               | SELECT agg_list HAVING cond_list<br/>
 *               | SELECT agg_list GROUP BY col_list HAVING cond_list<br/>
 *               ;<br/>
 * <br/>
 * update    ::=   UPDATE SET upd_list<br/>
 *               | UPDATE SET upd_list WHERE cond_list<br/>
 *               ;<br/>
 * <br/>
 * delete    ::=   DELETE WHERE cond_list<br/>
 *               ;<br/>
 * <br/>
 * col_list  ::= col_list COMMA col | col;<br/>
 * <br/>
 * col       ::=   * <br/>
 *               | COLUMN<br/>
 *               | COLUMN AS COLUMN<br/>
 *               ;<br/>
 * <br/>
 * upd_list  ::= upd_list COMMA upd | upd;<br/>
 * <br/>
 * upd       ::=   COLUMN = value<br/>
 *               ;<br/>
 * <br/>
 * order_list::= order_list COMMA order | order;<br/>
 * <br/>
 * order     ::=   COLUMN<br/>
 *               | COLUMN ASC<br/>
 *               | COLUMN DESC<br/>
 *               ;<br/>
 *               <br/>
 * cond_list ::=   cond_list cond <br/>
 *               | cond<br/>
 *               ;<br/>
 * <br/>
 * cond      ::=   COLUMN &lt; comp_arg<br/>
 *               | COLUMN &lt;= comp_arg<br/>
 *               | COLUMN = comp_arg<br/>
 *               | COLUMN &lt;&gt; comp_arg<br/>
 *               | COLUMN &gt;= comp_arg<br/>
 *               | COLUMN &gt; comp_arg<br/>
 *               | COLUMN REGEXP STRING<br/>
 *               | COLUMN IS NULL<br/>
 *               | ( cond )<br/>
 *               | cond:c1 AND cond:c2<br/>
 *               | cond:c1 OR cond:c2<br/>
 *               | NOT cond<br/>
 *               ;<br/>
 * <br/>
 * comp_arg  ::=   NUMBER<br/>
 *               | STRING<br/>
 *               | PARSE ( "number" , STRING )<br/>
 *               | PARSE ( "date" , STRING )<br/>
 *               | PARSE ( "time" , STRING )<br/>
 *               | PARSE ( "timestamp" , STRING )<br/>
 *               ;<br/>
 * <br/>
 * value     ::=   NUMBER<br/>
 *               | STRING<br/>
 *               ;<br/>
 * <br/>
 * limit     ::=   LIMIT NUMBER:max<br/>
 *               | LIMIT NUMBER:offset , NUMBER:max<br/>
 *               ;<br/>
 * agg_list  ::=   agg_list COMMA agg <br/>
 *               | agg<br/>
 *               ;<br/>
 * <br/>
 * agg       ::=   COUNT [(*)] [AS COLUMN]<br/>
 *               | MIN ( COLUMN ) [AS COLUMN]<br/>
 *               | MAX ( COLUMN ) [AS COLUMN]<br/>
 *               | MEAN ( COLUMN ) [AS COLUMN]<br/>
 *               | AVERAGE ( COLUMN ) [AS COLUMN]<br/>
 *               | STDEV ( COLUMN ) [AS COLUMN]<br/>
 *               | STDEVP ( COLUMN ) [AS COLUMN]<br/>
 *               | SUM ( COLUMN ) [AS COLUMN]<br/>
 *               | IQR ( COLUMN ) [AS COLUMN]<br/>
 *               | INTERQUARTILE ( COLUMN ) [AS COLUMN]<br/>
 * <br/>
 * Notes:<br/>
 * - time format: 'HH:mm'<br/>
 * - date format: 'yyyy-MM-dd'<br/>
 * - timestamp format: 'yyyy-MM-dd HH:mm'<br/>
 * - STRING is referring to characters enclosed by double quotes<br/>
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
 * &nbsp;&nbsp;&nbsp;The spreadsheet query to evaluate.
 * &nbsp;&nbsp;&nbsp;default: SELECT *
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
public class SpreadSheetQuery
  extends AbstractSymbolEvaluator<SpreadSheet> {

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
        "Evaluates spreadsheet subset queries.\n\n"
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
	    "expr_list ::= expr_list expr_part | expr_part;\n"
	  + "\n"
	  + "expr_part ::= select | update | delete;\n"
	  + "\n"
	  + "select    ::=   SELECT col_list [limit]\n"
	  + "              | SELECT col_list WHERE cond_list [limit]\n"
	  + "              | SELECT col_list ORDER BY order_list [limit]\n"
	  + "              | SELECT col_list WHERE cond_list ORDER BY order_list [limit]\n"
	  + "              | SELECT agg_list\n"
	  + "              | SELECT agg_list GROUP BY col_list\n"
	  + "              | SELECT agg_list HAVING cond_list\n"
	  + "              | SELECT agg_list GROUP BY col_list HAVING cond_list\n"
	  + "              ;\n"
	  + "\n"
	  + "update    ::=   UPDATE SET upd_list\n"
	  + "              | UPDATE SET upd_list WHERE cond_list\n"
	  + "              ;\n"
	  + "\n"
	  + "delete    ::=   DELETE WHERE cond_list\n"
	  + "              ;\n"
	  + "\n"
	  + "col_list  ::= col_list COMMA col | col;\n"
	  + "\n"
	  + "col       ::=   * \n"
	  + "              | COLUMN\n"
	  + "              | COLUMN AS COLUMN\n"
	  + "              ;\n"
	  + "\n"
	  + "upd_list  ::= upd_list COMMA upd | upd;\n"
	  + "\n"
	  + "upd       ::=   COLUMN = value\n"
	  + "              ;\n"
	  + "\n"
	  + "order_list::= order_list COMMA order | order;\n"
	  + "\n"
	  + "order     ::=   COLUMN\n"
	  + "              | COLUMN ASC\n"
	  + "              | COLUMN DESC\n"
	  + "              ;\n"
	  + "              \n"
	  + "cond_list ::=   cond_list cond \n"
	  + "              | cond\n"
	  + "              ;\n"
	  + "\n"
	  + "cond      ::=   COLUMN < comp_arg\n"
	  + "              | COLUMN <= comp_arg\n"
	  + "              | COLUMN = comp_arg\n"
	  + "              | COLUMN <> comp_arg\n"
	  + "              | COLUMN >= comp_arg\n"
	  + "              | COLUMN > comp_arg\n"
	  + "              | COLUMN REGEXP STRING\n"
	  + "              | COLUMN IS NULL\n"
	  + "              | ( cond )\n"
	  + "              | cond:c1 AND cond:c2\n"
	  + "              | cond:c1 OR cond:c2\n"
	  + "              | NOT cond\n"
	  + "              ;\n"
	  + "\n"
	  + "comp_arg  ::=   NUMBER\n"
	  + "              | STRING\n"
	  + "              | PARSE ( \"number\" , STRING )\n"
	  + "              | PARSE ( \"date\" , STRING )\n"
	  + "              | PARSE ( \"time\" , STRING )\n"
	  + "              | PARSE ( \"timestamp\" , STRING )\n"
	  + "              ;\n"
	  + "\n"
	  + "value     ::=   NUMBER\n"
	  + "              | STRING\n"
	  + "              ;\n"
	  + "\n"
	  + "limit     ::=   LIMIT NUMBER:max\n"
	  + "              | LIMIT NUMBER:offset , NUMBER:max\n"
	  + "              ;\n"
	  + "agg_list  ::=   agg_list COMMA agg \n"
	  + "              | agg\n"
	  + "              ;\n"
	  + "\n"
	  + "agg       ::=   COUNT [(*)] [AS COLUMN]\n"
	  + "              | MIN ( COLUMN ) [AS COLUMN]\n"
	  + "              | MAX ( COLUMN ) [AS COLUMN]\n"
	  + "              | MEAN ( COLUMN ) [AS COLUMN]\n"
	  + "              | AVERAGE ( COLUMN ) [AS COLUMN]\n"
	  + "              | STDEV ( COLUMN ) [AS COLUMN]\n"
	  + "              | STDEVP ( COLUMN ) [AS COLUMN]\n"
	  + "              | SUM ( COLUMN ) [AS COLUMN]\n"
	  + "              | IQR ( COLUMN ) [AS COLUMN]\n"
	  + "              | INTERQUARTILE ( COLUMN ) [AS COLUMN]\n"
	  + "\n"
	  + "Notes:\n"
	  + "- time format: 'HH:mm'\n"
	  + "- date format: 'yyyy-MM-dd'\n"
	  + "- timestamp format: 'yyyy-MM-dd HH:mm'\n"
	  + "- STRING is referring to characters enclosed by double quotes\n"
	  ;
  }

  /**
   * Returns the default expression to use.
   *
   * @return		the default expression
   */
  @Override
  protected String getDefaultExpression() {
    return "SELECT *";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String expressionTipText() {
    return "The spreadsheet query to evaluate.";
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
  protected SpreadSheet doEvaluate(HashMap symbols) throws Exception {
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
  public SpreadSheet evaluate() throws Exception {
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
  public static SpreadSheet evaluate(String expr, HashMap symbols, SpreadSheet sheet) throws Exception {
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
    runEvaluator(SpreadSheetQuery.class, args);
  }
}
