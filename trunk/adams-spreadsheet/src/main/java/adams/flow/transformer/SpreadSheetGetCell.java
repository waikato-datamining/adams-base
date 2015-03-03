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
 * SpreadSheetGetCell.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;

/**
 <!-- globalinfo-start -->
 * Extracts a single value from a spreadsheet.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetGetCell
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the cell values in an array or one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-row &lt;adams.core.Range&gt; (property: row)
 * &nbsp;&nbsp;&nbsp;The row(s) of the cell(s) to retrieve.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-col &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column(s) of the cell(s) to retrieve; A range is a comma-separated list 
 * &nbsp;&nbsp;&nbsp;of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(..
 * &nbsp;&nbsp;&nbsp;.)' inverts the range '...'; apart from column names (case-sensitive), the 
 * &nbsp;&nbsp;&nbsp;following placeholders can be used as well: first, second, third, last_2,
 * &nbsp;&nbsp;&nbsp; last_1, last
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-empty &lt;java.lang.String&gt; (property: empty)
 * &nbsp;&nbsp;&nbsp;The value to return in case the cell is empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-use-native &lt;boolean&gt; (property: useNative)
 * &nbsp;&nbsp;&nbsp;If enabled, native objects are output rather than strings.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetGetCell
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -253714973019682939L;

  /** the row of the cell to obtain. */
  protected Range m_Row;

  /** the column of the cell to obtain. */
  protected SpreadSheetColumnRange m_Column;

  /** the value to return if cell is empty. */
  protected String m_Empty;
  
  /** whether to output native objects rather than strings. */
  protected boolean m_UseNative;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Extracts a single value from a spreadsheet.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "row", "row",
	    new Range("1"));

    m_OptionManager.add(
	    "col", "column",
	    new SpreadSheetColumnRange("1"));

    m_OptionManager.add(
	    "empty", "empty",
	    "");

    m_OptionManager.add(
	    "use-native", "useNative",
	    false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Row    = new Range();
    m_Column = new SpreadSheetColumnRange();
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

    result  = QuickInfoHelper.toString(this, "row", m_Row, "row: ");
    result += QuickInfoHelper.toString(this, "column", m_Column, "/col: ");
    value = QuickInfoHelper.toString(this, "empty", (m_Empty.length() > 0 ? m_Empty : null), ", empty: ");
    if (value != null)
      result += value;
    value = QuickInfoHelper.toString(this, "useNative", m_UseNative, ", native");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    if (m_UseNative)
      return Object.class;
    else
      return String.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to output the cell values in an array or one-by-one.";
  }

  /**
   * Sets the row(s) of the cell(s).
   *
   * @param value	the row(s)
   */
  public void setRow(Range value) {
    m_Row = value;
    reset();
  }

  /**
   * Returns the row(s) of the cell(s).
   *
   * @return		the row(s)
   */
  public Range getRow() {
    return m_Row;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowTipText() {
    return "The row(s) of the cell(s) to retrieve.";
  }

  /**
   * Sets the column(s) of the cell(s).
   *
   * @param value	the column(s)
   */
  public void setColumn(SpreadSheetColumnRange value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column(s) of the cell(s).
   *
   * @return		the column
   */
  public SpreadSheetColumnRange getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTipText() {
    return "The column(s) of the cell(s) to retrieve; " + m_Column.getExample();
  }

  /**
   * Sets the value to return if cell is empty.
   *
   * @param value	the empty value
   */
  public void setEmpty(String value) {
    m_Empty = value;
    reset();
  }

  /**
   * Returns the value to return if cell is empty.
   *
   * @return		the empty value
   */
  public String getEmpty() {
    return m_Empty;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String emptyTipText() {
    return "The value to return in case the cell is empty.";
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
    return "If enabled, native objects are output rather than strings.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.spreadsheet.SpreadSheet.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
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
    Object	value;
    Cell	cell;
    int[]	rows;
    int[]	cols;

    result = null;

    sheet = (SpreadSheet) m_InputToken.getPayload();
    m_Row.setMax(sheet.getRowCount());
    m_Column.setSpreadSheet(sheet);

    rows = m_Row.getIntIndices();
    cols = m_Column.getIntIndices();
    if (rows.length == 0) {
      result = "No rows selected?";
    }
    else if (cols.length == 0) {
      result = "No columns selected?";
    }
    else {
      for (int r: rows) {
	for (int c: cols) {
	  cell = sheet.getCell(r, c);
	  if (cell == null)
	    value = m_Empty;
	  else if (cell.isMissing())
	    value = SpreadSheet.MISSING_VALUE;
	  else if (m_UseNative)
	    value = cell.getNative();
	  else
	    value = cell.getContent();
	  m_Queue.add(value);
	}
      }
    }

    return result;
  }
}
