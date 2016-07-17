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
 * SpreadSheetInsertColumn.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.core.Token;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Inserts a column at a specific position into spreadsheets coming through.<br>
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
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetInsertColumn
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-no-copy (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * </pre>
 * 
 * <pre>-position &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: position)
 * &nbsp;&nbsp;&nbsp;The position where to insert the column; An index is a number starting with 
 * &nbsp;&nbsp;&nbsp;1; apart from column names (case-sensitive), the following placeholders 
 * &nbsp;&nbsp;&nbsp;can be used as well: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: last
 * </pre>
 * 
 * <pre>-after (property: after)
 * &nbsp;&nbsp;&nbsp;If enabled, the column is inserted after the position instead of at the 
 * &nbsp;&nbsp;&nbsp;position.
 * </pre>
 * 
 * <pre>-header &lt;java.lang.String&gt; (property: header)
 * &nbsp;&nbsp;&nbsp;The name of the new column.
 * &nbsp;&nbsp;&nbsp;default: New
 * </pre>
 * 
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The value to intialize the cells with; you can use '\t' for tab, '\n' for 
 * &nbsp;&nbsp;&nbsp;line-feed and '\r' for carriage-return.
 * &nbsp;&nbsp;&nbsp;default: ?
 * </pre>
 * 
 * <pre>-placeholder (property: valueContainsPlaceholder)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic placeholder expansion for the value 
 * &nbsp;&nbsp;&nbsp;string.
 * </pre>
 * 
 * <pre>-variable (property: valueContainsVariable)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic variable expansion for the value string.
 * </pre>
 * 
 * <pre>-force-string (property: forceString)
 * &nbsp;&nbsp;&nbsp;If enabled, the value is set as string, even if it resembles a number.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetInsertColumn
  extends AbstractInPlaceSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 9030574317512531337L;
  
  /** the position where to insert the column. */
  protected SpreadSheetColumnIndex m_Position;  
  
  /** whether to insert after the position instead of at. */
  protected boolean m_After;
  
  /** the column header. */
  protected String m_Header;
  
  /** the value to initialize the cells with. */
  protected String m_Value;

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
	"Inserts a column at a specific position into spreadsheets "
	+ "coming through.\n"
	+ "The cells are initialized with a pre-defined value.";
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Position = new SpreadSheetColumnIndex();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "position", "position",
	    new SpreadSheetColumnIndex(Index.LAST));

    m_OptionManager.add(
	    "after", "after",
	    false);

    m_OptionManager.add(
	    "header", "header",
	    "New");

    m_OptionManager.add(
	    "value", "value",
	    SpreadSheet.MISSING_VALUE);

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

    result = QuickInfoHelper.toString(this, "header", "'" + m_Header + "'", "header: ");

    if (QuickInfoHelper.hasVariable(this, "after"))
      result += ", at/after: ";
    else if (m_After)
      result += ", after: ";
    else
      result += ", at: ";
    result += QuickInfoHelper.toString(this, "position", m_Position);

    result += QuickInfoHelper.toString(this, "value", "'" + getValue() + "'", ", insert: ");

    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "valueContainsPlaceholder", m_ValueContainsPlaceholder, "PH"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "valueContainsVariable", m_ValueContainsVariable, "Var"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "forceString", m_ForceString, "string"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no copy"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the position where to insert the column.
   *
   * @param value	the position
   */
  public void setPosition(SpreadSheetColumnIndex value) {
    m_Position = value;
    reset();
  }

  /**
   * Returns the position where to insert the column.
   *
   * @return		the position
   */
  public SpreadSheetColumnIndex getPosition() {
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
        "The position where to insert the column; " + m_Position.getExample();
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
   * Sets the name of the column.
   *
   * @param value	the name
   */
  public void setHeader(String value) {
    m_Header = value;
    reset();
  }

  /**
   * Returns the name of the column.
   *
   * @return		the name
   */
  public String getHeader() {
    return m_Header;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String headerTipText() {
    return "The name of the new column.";
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
    return "The value to intialize the cells with; you can use '\\t' for tab, '\\n' for line-feed and '\\r' for carriage-return.";
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
    String		value;

    result = null;
    sheetOld  = (SpreadSheet) m_InputToken.getPayload();
    
    // do we need to expand stuff?
    value = m_Value;
    if (m_ValueContainsVariable)
      value = getVariables().expand(value);
    if (m_ValueContainsPlaceholder)
      value = Placeholders.getSingleton().expand(value).replace("\\", "/");
    
    // determine position
    pos = 0;
    if (sheetOld.getColumnCount() > 0) {
      m_Position.setSpreadSheet(sheetOld);
      pos = m_Position.getIntIndex();
      if (m_After)
        pos++;
    }
    
    // add column
    if (m_NoCopy)
      sheetNew = sheetOld;
    else
      sheetNew = sheetOld.getClone();
    sheetNew.insertColumn(pos, m_Header, value);
    
    m_OutputToken = new Token(sheetNew);
    
    return result;
  }
}
