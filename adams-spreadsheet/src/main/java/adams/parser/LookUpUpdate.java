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
 * LookUpUpdate.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.parser;

import adams.core.io.PlaceholderFile;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.parser.lookupupdate.Parser;
import adams.parser.lookupupdate.Scanner;
import java_cup.runtime.DefaultSymbolFactory;
import java_cup.runtime.SymbolFactory;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Evaluates lookup update rules updating the spreadsheet.<br>
 * <br>
 * The following grammar is used:<br>
 * <br>
 * expr_list ::= expr_list expr_part | expr_part<br>
 * <br>
 * expr_part ::= conditional | assignment<br>
 * <br>
 * conditional ::=   if expr then assignments end<br>
 *                 | if expr then assignments else assignments end<br>
 * <br>
 * assignments ::= assignments assignment | assignment<br>
 * assignment ::=<br>
 *                 VARIABLE := expr;<br>
 *               | all ( "regexp" ) := expr;<br>
 * <br>
 * expr ::=        ( expr )<br>
 *               | NUMBER<br>
 *               | STRING<br>
 *               | BOOLEAN<br>
 *               | VARIABLE<br>
 * <br>
 *               | true<br>
 *               | false<br>
 * <br>
 *               | -expr<br>
 * <br>
 *               | expr &lt; expr<br>
 *               | expr &lt;= expr<br>
 *               | expr &gt; expr<br>
 *               | expr &gt;= expr<br>
 *               | expr = expr<br>
 *               | expr != expr<br>
 * <br>
 *               | not expr<br>
 *               | expr and expr<br>
 *               | expr or expr<br>
 * <br>
 *               | expr + expr<br>
 *               | expr - expr<br>
 *               | expr * expr<br>
 *               | expr &#47; expr<br>
 *               | expr % expr<br>
 *               | expr ^ expr<br>
 * <br>
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
 * <br>
 * Notes:<br>
 * - Variables are either all alphanumeric and -&#47;_ (e.g., "ABc_1-2") or any character<br>
 *   apart from "'" enclosed by "'" and "'" (e.g., "'Hello World'").<br>
 * - The 'all' method applies the value to all the values in the lookup table<br>
 *   that match the regular expression.<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;The lookup update rules to evaluate.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-symbol &lt;adams.core.base.BaseString&gt; [-symbol ...] (property: symbols)
 * &nbsp;&nbsp;&nbsp;The symbols to initialize the parser with, key-value pairs: name=value.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-reader &lt;adams.data.io.input.SpreadSheetReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The spreadsheet reader for loading the spreadsheet to work on.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.CsvSpreadSheetReader -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 * 
 * <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The input file to load with the specified reader; ignored if pointing to 
 * &nbsp;&nbsp;&nbsp;directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-key-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: keyColumn)
 * &nbsp;&nbsp;&nbsp;The index of the column in the spreadsheet to use as key; An index is a 
 * &nbsp;&nbsp;&nbsp;number starting with 1; column names (case-sensitive) as well as the following 
 * &nbsp;&nbsp;&nbsp;placeholders can be used: first, second, third, last_2, last_1, last; numeric 
 * &nbsp;&nbsp;&nbsp;indices can be enforced by preceding them with '#' (eg '#12'); column names 
 * &nbsp;&nbsp;&nbsp;can be surrounded by double quotes.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-value-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: valueColumn)
 * &nbsp;&nbsp;&nbsp;The index of the column in the spreadsheet to use as value; An index is 
 * &nbsp;&nbsp;&nbsp;a number starting with 1; column names (case-sensitive) as well as the following 
 * &nbsp;&nbsp;&nbsp;placeholders can be used: first, second, third, last_2, last_1, last; numeric 
 * &nbsp;&nbsp;&nbsp;indices can be enforced by preceding them with '#' (eg '#12'); column names 
 * &nbsp;&nbsp;&nbsp;can be surrounded by double quotes.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-use-native &lt;boolean&gt; (property: useNative)
 * &nbsp;&nbsp;&nbsp;If enabled, native objects are used as value rather than strings.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 14588 $
 */
public class LookUpUpdate
  extends AbstractSymbolEvaluator<SpreadSheet> {

  /** for serialization. */
  private static final long serialVersionUID = 8014316012335802585L;

  /** the lookup to use. */
  protected SpreadSheet m_Sheet;

  /** the spreadsheet reader for loading the spreadsheet. */
  protected SpreadSheetReader m_Reader;

  /** the spreadsheet file to read. */
  protected PlaceholderFile m_Input;

  /** the index of the column to use as key. */
  protected SpreadSheetColumnIndex m_KeyColumn;

  /** the index of the column to use as value. */
  protected SpreadSheetColumnIndex m_ValueColumn;

  /** whether to output native objects rather than strings. */
  protected boolean m_UseNative;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Evaluates lookup update rules updating the spreadsheet.\n\n"
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

    m_OptionManager.add(
      "key-column", "keyColumn",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "value-column", "valueColumn",
      new SpreadSheetColumnIndex("2"));

    m_OptionManager.add(
      "use-native", "useNative",
      false);
  }

  /**
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return
      "expr_list ::= expr_list expr_part | expr_part\n"
	+ "\n"
	+ "expr_part ::= conditional | assignment\n"
	+ "\n"
	+ "conditional ::=   if expr then assignments end\n"
	+ "                | if expr then assignments else assignments end\n"
	+ "\n"
	+ "assignments ::= assignments assignment | assignment\n"
	+ "assignment ::=\n"
	+ "                VARIABLE := expr;\n"
	+ "              | all ( \"regexp\" ) := expr;\n"
	+ "\n"
	+ "expr ::=        ( expr )\n"
	+ "              | NUMBER\n"
	+ "              | STRING\n"
	+ "              | BOOLEAN\n"
	+ "              | VARIABLE\n"
	+ "\n"
	+ "              | true\n"
	+ "              | false\n"
	+ "\n"
	+ "              | -expr\n"
	+ "\n"
	+ "              | expr < expr\n"
	+ "              | expr <= expr\n"
	+ "              | expr > expr\n"
	+ "              | expr >= expr\n"
	+ "              | expr = expr\n"
	+ "              | expr != expr\n"
	+ "\n"
	+ "              | not expr\n"
	+ "              | expr and expr\n"
	+ "              | expr or expr\n"
	+ "\n"
	+ "              | expr + expr\n"
	+ "              | expr - expr\n"
	+ "              | expr * expr\n"
	+ "              | expr / expr\n"
	+ "              | expr % expr\n"
	+ "              | expr ^ expr\n"
	+ "\n"
	+ "              | abs ( expr )\n"
	+ "              | sqrt ( expr )\n"
	+ "              | cbrt ( expr )\n"
	+ "              | log ( expr )\n"
	+ "              | log10 ( expr )\n"
	+ "              | exp ( expr )\n"
	+ "              | sin ( expr )\n"
	+ "              | sinh ( expr )\n"
	+ "              | cos ( expr )\n"
	+ "              | cosh ( expr )\n"
	+ "              | tan ( expr )\n"
	+ "              | tanh ( expr )\n"
	+ "              | atan ( expr )\n"
	+ "              | atan2 ( exprY , exprX )\n"
	+ "              | hypot ( exprX , exprY )\n"
	+ "              | signum ( expr )\n"
	+ "              | rint ( expr )\n"
	+ "              | floor ( expr )\n"
	+ "              | pow[er] ( expr , expr )\n"
	+ "              | ceil ( expr )\n"
	+ "              | min ( expr1 , expr2 )\n"
	+ "              | max ( expr1 , expr2 )\n"
	+ "\n"
	+ "Notes:\n"
	+ "- Variables are either all alphanumeric and -/_ (e.g., \"ABc_1-2\") or any character\n"
	+ "  apart from \"'\" enclosed by \"'\" and \"'\" (e.g., \"'Hello World'\").\n"
	+ "- The 'all' method applies the value to all the values in the lookup table\n"
        + "  that match the regular expression.\n"
      ;
  }

  /**
   * Returns the default expression to use.
   *
   * @return		the default expression
   */
  @Override
  protected String getDefaultExpression() {
    return "";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String expressionTipText() {
    return "The lookup update rules to evaluate.";
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
   * Sets the index of the column to act as key in the lookup table.
   *
   * @param value	the index
   */
  public void setKeyColumn(SpreadSheetColumnIndex value) {
    m_KeyColumn = value;
    reset();
  }

  /**
   * Returns the index of the column to act as key in the lookup table.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getKeyColumn() {
    return m_KeyColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyColumnTipText() {
    return "The index of the column in the spreadsheet to use as key; " + m_KeyColumn.getExample();
  }

  /**
   * Sets the index of the column to act as value in the lookup table.
   *
   * @param value	the index
   */
  public void setValueColumn(SpreadSheetColumnIndex value) {
    m_ValueColumn = value;
    reset();
  }

  /**
   * Returns the index of the column to act as value in the lookup table.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getValueColumn() {
    return m_ValueColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueColumnTipText() {
    return "The index of the column in the spreadsheet to use as value; " + m_ValueColumn.getExample();
  }

  /**
   * Sets whether to output native objects rather than strings.
   *
   * @param value	true if to output native objects
   */
  public void setUseNative(boolean value) {
    m_UseNative = value;
    reset();
  }

  /**
   * Returns whether native objects are output rather than strings.
   *
   * @return		true if native objects are used
   */
  public boolean getUseNative() {
    return m_UseNative;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useNativeTipText() {
    return "If enabled, native objects are used as value rather than strings.";
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
    m_KeyColumn.setData(m_Sheet);
    m_ValueColumn.setData(m_Sheet);
    return evaluate(m_Expression, symbols, m_Sheet, m_KeyColumn.getIntIndex(), m_ValueColumn.getIntIndex());
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
   * @param sheet	the spreadsheet to update
   * @param keyCol 	the key column (0-based)
   * @param valueCol	the value column (0-based)
   * @return		the evaluated result
   * @throws Exception	if something goes wrong
   */
  public static SpreadSheet evaluate(String expr, HashMap symbols, SpreadSheet sheet, int keyCol, int valueCol) throws Exception {
    SymbolFactory 		sf;
    ByteArrayInputStream 	parserInput;
    Parser 			parser;
    HashMap			updated;
    SpreadSheet			result;
    String			keyStr;
    boolean			found;
    Row				added;

    if (keyCol < 0)
      throw new IllegalArgumentException("No key column specified!");
    if (valueCol < 0)
      throw new IllegalArgumentException("No value column specified!");
    if (keyCol == valueCol)
      throw new IllegalArgumentException("Key and value column are the same: " + (keyCol+1));
    if (keyCol >= sheet.getColumnCount())
      throw new IllegalArgumentException("Key column out of range: " + (keyCol+1) + " > " + sheet.getColumnCount());
    if (valueCol >= sheet.getColumnCount())
      throw new IllegalArgumentException("Value column out of range: " + (valueCol+1) + " > " + sheet.getColumnCount());

    expr = expr.trim();
    if (expr.isEmpty())
      return sheet;

    for (Row row: sheet.rows()) {
      if (row.hasCell(keyCol) && row.hasCell(valueCol))
	symbols.put(row.getCell(keyCol).getContent(), row.getCell(valueCol).getNative());
    }

    sf          = new DefaultSymbolFactory();
    parserInput = new ByteArrayInputStream(expr.getBytes());
    parser      = new Parser(new Scanner(parserInput, sf), sf);
    parser.setSymbols(symbols);
    parser.parse();
    updated = parser.getSymbols();
    result  = sheet.getClone();
    for (Object key: updated.keySet()) {
      found  = false;
      keyStr = key.toString();
      for (Row row: result.rows()) {
	if (row.hasCell(keyCol) && row.hasCell(valueCol)) {
	  if (row.getCell(keyCol).getContent().equals(keyStr)) {
	    found = true;
	    row.getCell(valueCol).setNative(updated.get(key));
	    break;
	  }
	}
      }
      if (!found) {
	added = result.addRow();
	added.addCell(keyCol).setContentAsString(keyStr);
	added.addCell(valueCol).setNative(updated.get(key));
      }
    }
    return result;
  }

  /**
   * Runs the evaluator from command-line.
   *
   * @param args	the command-line options, use "-help" to list them
   */
  public static void main(String[] args) {
    runEvaluator(LookUpUpdate.class, args);
  }
}
