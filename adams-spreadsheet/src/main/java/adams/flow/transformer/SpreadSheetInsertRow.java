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
 * SpreadSheetInsertRow.java
 * Copyright (C) 2012-2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.flow.core.Token;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Inserts a row at a specific position into spreadsheets coming through.<br>
 * The cells are initialized with a pre-defined value.
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetInsertRow
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
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-position &lt;adams.core.Index&gt; (property: position)
 * &nbsp;&nbsp;&nbsp;The position where to insert the column.
 * &nbsp;&nbsp;&nbsp;default: last
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 *
 * <pre>-after &lt;boolean&gt; (property: after)
 * &nbsp;&nbsp;&nbsp;If enabled, the column is inserted after the position instead of at the
 * &nbsp;&nbsp;&nbsp;position.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The value to initialize the cells with; you can use '\t' for tab, '\n' for
 * &nbsp;&nbsp;&nbsp;line-feed and '\r' for carriage-return.
 * &nbsp;&nbsp;&nbsp;default: ?
 * </pre>
 *
 * <pre>-value-type &lt;SINGLE_VALUE|COMMA_SEPARATED_VALUES|BLANK_SEPARATED_VALUES&gt; (property: valueType)
 * &nbsp;&nbsp;&nbsp;How to interpret the value string.
 * &nbsp;&nbsp;&nbsp;default: SINGLE_VALUE
 * </pre>
 *
 * <pre>-placeholder &lt;boolean&gt; (property: valueContainsPlaceholder)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic placeholder expansion for the value
 * &nbsp;&nbsp;&nbsp;string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-variable &lt;boolean&gt; (property: valueContainsVariable)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic variable expansion for the value string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-force-string &lt;boolean&gt; (property: forceString)
 * &nbsp;&nbsp;&nbsp;If enabled, the value is set as string, even if it resembles a number.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetInsertRow
  extends AbstractInPlaceSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 4198653233287560570L;

  /** how to parse the value. */
  public enum ValueType {
    SINGLE_VALUE,
    COMMA_SEPARATED_VALUES,
    BLANK_SEPARATED_VALUES,
  }

  /** the position where to insert the row. */
  protected Index m_Position;

  /** whether to insert after the position instead of at. */
  protected boolean m_After;

  /** the value to initialize the cells with. */
  protected String m_Value;

  /** the value type. */
  protected ValueType m_ValueType;

  /** whether the value string contains a placeholder, which needs to be
   * expanded first. */
  protected boolean m_ValueContainsPlaceholder;

  /** whether the value string contains a variable, which needs to be
   * expanded first. */
  protected boolean m_ValueContainsVariable;

  /** whether to set value as string. */
  protected boolean m_ForceString;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Inserts a row at a specific position into spreadsheets "
	+ "coming through.\n"
	+ "The cells are initialized with a pre-defined value.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Position = new Index();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "position", "position",
      new Index(Index.LAST));

    m_OptionManager.add(
      "after", "after",
      false);

    m_OptionManager.add(
      "value", "value",
      SpreadSheet.MISSING_VALUE);

    m_OptionManager.add(
      "value-type", "valueType",
      ValueType.SINGLE_VALUE);

    m_OptionManager.add(
      "placeholder", "valueContainsPlaceholder",
      false);

    m_OptionManager.add(
      "variable", "valueContainsVariable",
      false);

    m_OptionManager.add(
      "force-string", "forceString",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    if (QuickInfoHelper.hasVariable(this, "after"))
      result = "at/after: ";
    else if (m_After)
      result = "after: ";
    else
      result = "at: ";
    result += QuickInfoHelper.toString(this, "position", m_Position);

    result += QuickInfoHelper.toString(this, "value", "'" + getValue() + "'", ", insert: ");
    result += QuickInfoHelper.toString(this, "valueType", m_ValueType, ", type: ");

    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "valueContainsPlaceholder", m_ValueContainsPlaceholder, "PH"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "valueContainsVariable", m_ValueContainsVariable, "Var"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no copy"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "forceString", m_ForceString, "force string"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the position where to insert the string.
   *
   * @param value	the position
   */
  public void setPosition(Index value) {
    m_Position = value;
    reset();
  }

  /**
   * Returns the position where to insert the string.
   *
   * @return		the position
   */
  public Index getPosition() {
    return m_Position;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String positionTipText() {
    return
        "The position where to insert the column.";
  }

  /**
   * Sets whether to insert at or after the position.
   *
   * @param value	true if to add after
   */
  public void setAfter(boolean value) {
    m_After = value;
    reset();
  }

  /**
   * Returns whether to insert at or after the position.
   *
   * @return		true if to add after
   */
  public boolean getAfter() {
    return m_After;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String afterTipText() {
    return
        "If enabled, the column is inserted after the position instead of at "
	+ "the position.";
  }

  /**
   * Sets the value to insert.
   *
   * @param value	the value
   */
  public void setValue(String value) {
    m_Value = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the value to insert.
   *
   * @return		the value
   */
  public String getValue() {
    return Utils.backQuoteChars(m_Value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The value to initialize the cells with; you can use '\\t' for tab, '\\n' for line-feed and '\\r' for carriage-return.";
  }

  /**
   * Sets how to interpret the value.
   *
   * @param value	the type
   */
  public void setValueType(ValueType value) {
    m_ValueType = value;
    reset();
  }

  /**
   * Returns how to interpret the value.
   *
   * @return		the type
   */
  public ValueType getValueType() {
    return m_ValueType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTypeTipText() {
    return "How to interpret the value string.";
  }

  /**
   * Sets whether the value string contains a placeholder which needs to be
   * expanded first.
   *
   * @param value	true if value string contains a placeholder
   */
  public void setValueContainsPlaceholder(boolean value) {
    m_ValueContainsPlaceholder = value;
    reset();
  }

  /**
   * Returns whether the vaue string contains a placeholder which needs to be
   * expanded first.
   *
   * @return		true if value string contains a placeholder
   */
  public boolean getValueContainsPlaceholder() {
    return m_ValueContainsPlaceholder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueContainsPlaceholderTipText() {
    return "Set this to true to enable automatic placeholder expansion for the value string.";
  }

  /**
   * Sets whether the value string contains a variable which needs to be
   * expanded first.
   *
   * @param value	true if value string contains a variable
   */
  public void setValueContainsVariable(boolean value) {
    m_ValueContainsVariable = value;
    reset();
  }

  /**
   * Returns whether the value string contains a variable which needs to be
   * expanded first.
   *
   * @return		true if value string contains a variable
   */
  public boolean getValueContainsVariable() {
    return m_ValueContainsVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueContainsVariableTipText() {
    return "Set this to true to enable automatic variable expansion for the value string.";
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    SpreadSheet		sheetOld;
    SpreadSheet		sheetNew;
    int			pos;
    int			i;
    String		value;
    Row			rowNew;
    String[]		cells;

    result = null;
    sheetOld  = (SpreadSheet) m_InputToken.getPayload();

    // do we need to expand stuff?
    value = m_Value;
    if (m_ValueContainsVariable)
      value = getVariables().expand(value);
    if (m_ValueContainsPlaceholder)
      value = Placeholders.getSingleton().expand(value);

    // determine position
    pos = 0;
    if (sheetOld.getRowCount() > 0) {
      m_Position.setMax(sheetOld.getRowCount());
      pos = m_Position.getIntIndex();
      if (m_After)
        pos++;
    }

    // init sheet
    if (m_NoCopy)
      sheetNew = sheetOld;
    else
      sheetNew = sheetOld.getClone();
    rowNew = sheetNew.insertRow(pos);

    // data
    switch (m_ValueType) {
      case SINGLE_VALUE:
	for (i = 0; i < sheetNew.getColumnCount(); i++) {
	  if (m_ForceString)
	    rowNew.addCell(sheetNew.getHeaderRow().getCellKey(i)).setContentAsString(value);
	  else
	    rowNew.addCell(sheetNew.getHeaderRow().getCellKey(i)).setContent(value);
	}
        break;

      case COMMA_SEPARATED_VALUES:
        cells = SpreadSheetUtils.split(value, ',', true);
        for (i = 0; i < cells.length && i < sheetNew.getColumnCount(); i++) {
	  if (m_ForceString)
	    rowNew.addCell(sheetNew.getHeaderRow().getCellKey(i)).setContentAsString(cells[i]);
	  else
	    rowNew.addCell(sheetNew.getHeaderRow().getCellKey(i)).setContent(cells[i]);
	}
        break;

      case BLANK_SEPARATED_VALUES:
        cells = SpreadSheetUtils.split(value, ' ', true);
        for (i = 0; i < cells.length && i < sheetNew.getColumnCount(); i++) {
	  if (m_ForceString)
	    rowNew.addCell(sheetNew.getHeaderRow().getCellKey(i)).setContentAsString(cells[i]);
	  else
	    rowNew.addCell(sheetNew.getHeaderRow().getCellKey(i)).setContent(cells[i]);
	}
        break;

      default:
        result = "Unhandled value type: " + m_ValueType;
        break;
    }

    if (result == null)
      m_OutputToken = new Token(sheetNew);

    return result;
  }
}
