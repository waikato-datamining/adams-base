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
 * LookUpUpdate.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.core.Token;
import adams.parser.LookUpUpdateText;

import java.util.HashMap;

/**
 <!-- globalinfo-start -->
 * Updates the lookup table (in form of a spreadsheet) that passes through using the specified rules.<br>
 * <br>
 * The rules use the following grammar:<br>
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
 *                  VARIABLE := expr;<br>
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
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
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
 * &nbsp;&nbsp;&nbsp;default: LookUpUpdate
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
 * <pre>-rules &lt;adams.parser.LookUpUpdateText&gt; (property: rules)
 * &nbsp;&nbsp;&nbsp;The rules for updating the lookup table.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LookUpUpdate
  extends AbstractSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 4182914190162129217L;

  /** the index of the column to use as key. */
  protected SpreadSheetColumnIndex m_KeyColumn;

  /** the index of the column to use as value. */
  protected SpreadSheetColumnIndex m_ValueColumn;

  /** the update rules. */
  protected LookUpUpdateText m_Rules;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Updates the lookup table (in form of a spreadsheet) that passes "
        + "through using the specified rules.\n\n"
        + "The rules use the following grammar:\n\n"
        + new adams.parser.LookUpUpdate().getGrammar();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key-column", "keyColumn",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "value-column", "valueColumn",
      new SpreadSheetColumnIndex("2"));

    m_OptionManager.add(
      "rules", "rules",
      new LookUpUpdateText(""));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "keyColumn", m_KeyColumn, "key: ");
    result += QuickInfoHelper.toString(this, "valueColumn", m_ValueColumn, ", value: ");

    return result;
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
   * Sets the rules to use for updating.
   *
   * @param value	the rules
   */
  public void setRules(LookUpUpdateText value) {
    m_Rules = value;
    reset();
  }

  /**
   * Returns the rules for updating.
   *
   * @return		the rules
   */
  public LookUpUpdateText getRules() {
    return m_Rules;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rulesTipText() {
    return "The rules for updating the lookup table.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    SpreadSheet			sheet;

    result = null;

    sheet  = (SpreadSheet) m_InputToken.getPayload();
    m_KeyColumn.setData(sheet);
    m_ValueColumn.setData(sheet);
    try {
      sheet = adams.parser.LookUpUpdate.evaluate(
	m_Rules.getValue(), new HashMap(), sheet, m_KeyColumn.getIntIndex(), m_ValueColumn.getIntIndex());
    }
    catch (Exception e) {
      result = handleException("Failed to update lookup table!", e);
    }

    if (result == null)
      m_OutputToken = new Token(sheet);

    return result;
  }
}
