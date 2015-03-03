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
 * SpreadSheetCopyColumns.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Copies a range of columns to a specific position in the spreadsheets coming through.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetCopyColumns
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
 * <pre>-columns &lt;adams.core.Range&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The range of columns to copy; A range is a comma-separated list of single 
 * &nbsp;&nbsp;&nbsp;1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts 
 * &nbsp;&nbsp;&nbsp;the range '...'; the following placeholders can be used as well: first, 
 * &nbsp;&nbsp;&nbsp;second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 * 
 * <pre>-position &lt;adams.core.Index&gt; (property: position)
 * &nbsp;&nbsp;&nbsp;The position where to insert the column; An index is a number starting with 
 * &nbsp;&nbsp;&nbsp;1; the following placeholders can be used as well: first, second, third, 
 * &nbsp;&nbsp;&nbsp;last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 * <pre>-after (property: after)
 * &nbsp;&nbsp;&nbsp;If enabled, the column is inserted after the position instead of at the 
 * &nbsp;&nbsp;&nbsp;position.
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use for the column headers, can be empty.
 * &nbsp;&nbsp;&nbsp;default: Copy-
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetCopyColumns
  extends AbstractSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 9030574317512531337L;
  
  /** the range of columns to copy. */
  protected SpreadSheetColumnRange m_Columns;
  
  /** the position where to insert the copied columns. */
  protected SpreadSheetColumnIndex m_Position;  
  
  /** whether to insert after the position instead of at. */
  protected boolean m_After;
  
  /** the prefix for the new columns. */
  protected String m_Prefix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Copies a range of columns to a specific position in the spreadsheets "
	+ "coming through.";
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
	    "columns", "columns",
	    new SpreadSheetColumnRange(Range.ALL));

    m_OptionManager.add(
	    "position", "position",
	    new SpreadSheetColumnIndex(Index.FIRST));

    m_OptionManager.add(
	    "after", "after",
	    false);

    m_OptionManager.add(
	    "prefix", "prefix",
	    "Copy-");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = QuickInfoHelper.toString(this, "columns", m_Columns, "columns: ");

    if (QuickInfoHelper.hasVariable(this, "after"))
      result += ", at/after: ";
    else if (m_After)
      result += ", after: ";
    else
      result += ", at: ";
    result += QuickInfoHelper.toString(this, "position", m_Position);
    value = QuickInfoHelper.toString(this, "prefix", (m_Prefix.length() > 0 ? m_Prefix : null), ", prefix: ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Sets the range of columns to copy.
   *
   * @param value	the range
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the range of columns to copy.
   *
   * @return		the position
   */
  public SpreadSheetColumnRange getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsTipText() {
    return "The range of columns to copy";
  }

  /**
   * Sets the position where to insert the columns.
   *
   * @param value	the position
   */
  public void setPosition(SpreadSheetColumnIndex value) {
    m_Position = value;
    reset();
  }

  /**
   * Returns the position where to insert the columns.
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
    return "The position where to insert the column.";
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
   * Sets the prefix to use for the copied column headers.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to use for the copied column headers.
   *
   * @return		the position
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return
        "The prefix to use for the column headers, can be empty.";
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
    int[]		fromPos;
    int[]		toPos;
    int			i;
    String		header;
    DataRow		rowNew;
    Cell		cell;

    result   = null;
    sheetOld = (SpreadSheet) m_InputToken.getPayload();
    sheetNew = sheetOld.getHeader();
    
    // determine position/ranges
    m_Columns.setSpreadSheet(sheetOld);
    fromPos = m_Columns.getIntIndices();
    toPos   = new int[fromPos.length];
    m_Position.setSpreadSheet(sheetOld);
    pos = m_Position.getIntIndex();
    if (m_After)
      pos++;
    for (i = 0; i < toPos.length; i++)
      toPos[i] = pos + i;
    
    // add new columns
    for (i = 0; i < fromPos.length; i++) {
      header = sheetOld.getHeaderRow().getContent(fromPos[i]);
      sheetNew.insertColumn(toPos[i], m_Prefix + header);
    }
    
    // copy data
    for (DataRow rowOld: sheetOld.rows()) {
      rowNew = sheetNew.addRow();
      // old cells
      for (String key: rowOld.cellKeys())
	rowNew.addCell(key).assign(rowOld.getCell(key));
      
      // cell copies
      for (i = 0; i < fromPos.length; i++) {
	cell = rowOld.getCell(fromPos[i]);
	rowNew.getCell(toPos[i]).assign(cell);
      }
    }
    
    m_OutputToken = new Token(sheetNew);
    
    return result;
  }
}
