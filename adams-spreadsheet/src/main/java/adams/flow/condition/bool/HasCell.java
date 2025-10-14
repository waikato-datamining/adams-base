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
 * HasCell.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetRowIndex;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Checks whether the specified cell is present in the spreadsheet and has a value. If a value is specified, it also checks whether the cell value is the same.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-row &lt;adams.data.spreadsheet.SpreadSheetRowIndex&gt; (property: row)
 * &nbsp;&nbsp;&nbsp;The row to check.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 *
 * <pre>-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column to check.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The optional value to check against, ignored if empty.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class HasCell
  extends AbstractBooleanCondition {

  private static final long serialVersionUID = -5437823119374204827L;

  /** the row. */
  protected SpreadSheetRowIndex m_Row;

  /** the column. */
  protected SpreadSheetColumnIndex m_Column;

  /** the optional value. */
  protected String m_Value;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the specified cell is present in the spreadsheet and has a value. "
	     + "If a value is specified, it also checks whether the cell value is the same.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "row", "row",
      new SpreadSheetRowIndex("1"));

    m_OptionManager.add(
      "column", "column",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "value", "value",
      "");
  }

  /**
   * Sets the row to check.
   *
   * @param value	the row
   */
  public void setRow(SpreadSheetRowIndex value) {
    m_Row = value;
    reset();
  }

  /**
   * Returns the row to check.
   *
   * @return		the row
   */
  public SpreadSheetRowIndex getRow() {
    return m_Row;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowTipText() {
    return "The row to check.";
  }

  /**
   * Sets the column to check.
   *
   * @param value	the column
   */
  public void setColumn(SpreadSheetColumnIndex value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column to check.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTipText() {
    return "The column to check.";
  }

  /**
   * Sets the optional value to check, ignored if empty.
   *
   * @param value	the value
   */
  public void setValue(String value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the optional value to check, ignored if empty.
   *
   * @return		the value
   */
  public String getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The optional value to check against, ignored if empty.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "row", m_Row, "row: ");
    result += QuickInfoHelper.toString(this, "column", m_Column, ", col: ");
    result += QuickInfoHelper.toString(this, "value", (m_Value.isEmpty() ? "-not checked-": m_Value), ", value: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return adams.flow.core.Unknown.class
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner the owning actor
   * @param token the current token passing through
   * @return the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    boolean	result;
    SpreadSheet	sheet;

    sheet = token.getPayload(SpreadSheet.class);
    m_Row.setSpreadSheet(sheet);
    m_Column.setSpreadSheet(sheet);
    result = (m_Row.getIntIndex() != -1)
	       && (m_Column.getIntIndex() != -1)
	       && sheet.hasCell(m_Row.getIntIndex(), m_Column.getIntIndex())
	       && !sheet.getCell(m_Row.getIntIndex(), m_Column.getIntIndex()).isEmpty();
    if (result && !m_Value.isEmpty())
      result = sheet.getCell(m_Row.getIntIndex(), m_Column.getIntIndex()).getContent().equals(m_Value);

    return result;
  }
}
