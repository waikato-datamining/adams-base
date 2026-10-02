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
 * SpreadSheetSetHeaderCell.java
 * Copyright (C) 2013-2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Sets a single header cell value in a spreadsheet.
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetSetHeaderCell
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-col &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column of the header cell to set; An index is a number starting with
 * &nbsp;&nbsp;&nbsp;1; column names (case-sensitive) as well as the following placeholders can
 * &nbsp;&nbsp;&nbsp;be used: first, second, third, last_2, last_1, last; numeric indices can
 * &nbsp;&nbsp;&nbsp;be enforced by preceding them with '#' (eg '#12'); column names can be surrounded
 * &nbsp;&nbsp;&nbsp;by double quotes.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The value to set in the header cell.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-force-string &lt;boolean&gt; (property: forceString)
 * &nbsp;&nbsp;&nbsp;If enabled, the value is set as string, even if it resembles a number.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-expand-variables &lt;boolean&gt; (property: expandVariables)
 * &nbsp;&nbsp;&nbsp;If enabled, any variables in the value get expanded first before setting
 * &nbsp;&nbsp;&nbsp;it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetSetHeaderCell
  extends AbstractInPlaceSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -6250232085303020849L;

  /** the column of the cell to obtain. */
  protected SpreadSheetColumnIndex m_Column;

  /** the value to return if cell is empty. */
  protected String m_Value;

  /** whether to set value as string. */
  protected boolean m_ForceString;

  /** whether to expand variables. */
  protected boolean m_ExpandVariables;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sets a single header cell value in a spreadsheet.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "col", "column",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "value", "value",
      "");

    m_OptionManager.add(
      "force-string", "forceString",
      false);

    m_OptionManager.add(
      "expand-variables", "expandVariables",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Column = new SpreadSheetColumnIndex();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "column", m_Column, "col: ");
    result += QuickInfoHelper.toString(this, "value", "'" + m_Value + "'", ", value: ");
    result += QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no copy", ", ");
    result += QuickInfoHelper.toString(this, "forceString", m_ForceString, "force string", ", ");
    result += QuickInfoHelper.toString(this, "expandVariables", m_ExpandVariables, "expands vars", ", ");

    return result;
  }

  /**
   * Sets the column of the header cell.
   *
   * @param value	the column
   */
  public void setColumn(SpreadSheetColumnIndex value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column of the header cell.
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
    return "The column of the header cell to set; " + m_Column.getExample();
  }

  /**
   * Sets the value to set in the cell.
   *
   * @param value	the value
   */
  public void setValue(String value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the value to set in the cell.
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
    return "The value to set in the header cell.";
  }

  /**
   * Sets whether to force setting the value as string even if it resembles
   * a number.
   *
   * @param value	true if to force string
   */
  public void setForceString(boolean value) {
    m_ForceString = value;
    reset();
  }

  /**
   * Returns whether to force setting the value as string even if it resembles
   * a number.
   *
   * @return		true if string type is enforced
   */
  public boolean getForceString() {
    return m_ForceString;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String forceStringTipText() {
    return "If enabled, the value is set as string, even if it resembles a number.";
  }

  /**
   * Sets whether to expand any variable first before setting the value.
   *
   * @param value	true if to expand first
   */
  public void setExpandVariables(boolean value) {
    m_ExpandVariables = value;
    reset();
  }

  /**
   * Returns whether to expand any variable first before setting the value.
   *
   * @return		true if to expand first
   */
  public boolean getExpandVariables() {
    return m_ExpandVariables;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expandVariablesTipText() {
    return "If enabled, any variables in the value get expanded first before setting it.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    SpreadSheet	sheet;
    Row		row;
    Cell	cell;
    String	value;

    result = null;
    value  = m_Value;
    if (m_ExpandVariables)
      value = getVariables().expand(value);

    sheet = ((SpreadSheet) m_InputToken.getPayload());
    if (!m_NoCopy)
      sheet = sheet.getClone();
    m_Column.setSpreadSheet(sheet);

    if (m_Column.getIntIndex() == -1) {
      result = "Illegal column index ('" + m_Column.getIndex() + "')?";
      m_OutputToken = new Token(sheet);
    }
    else {
      row  = sheet.getHeaderRow();
      cell = row.getCell(m_Column.getIntIndex());
      if (cell == null)
	cell = row.addCell(m_Column.getIntIndex());
      if (m_ForceString)
	cell.setContentAsString(value);
      else
	cell.setContent(value);
      m_OutputToken = new Token(sheet);
    }

    return result;
  }
}
