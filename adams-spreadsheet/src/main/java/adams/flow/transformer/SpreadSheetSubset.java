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
 * SpreadSheetSubset.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetView;
import adams.data.spreadsheet.SpreadSheetViewCreator;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Extracts a subset of rows&#47;columns from a spreadsheet.
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetSubset
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-row &lt;adams.core.Range&gt; (property: rows)
 * &nbsp;&nbsp;&nbsp;The rows of the subset to retrieve.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-col &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The columns of the subset to retrieve; A range is a comma-separated list 
 * &nbsp;&nbsp;&nbsp;of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(..
 * &nbsp;&nbsp;&nbsp;.)' inverts the range '...'; column names (case-sensitive) as well as the 
 * &nbsp;&nbsp;&nbsp;following placeholders can be used: first, second, third, last_2, last_1,
 * &nbsp;&nbsp;&nbsp; last; numeric indices can be enforced by preceding them with '#' (eg '#12'
 * &nbsp;&nbsp;&nbsp;); column names can be surrounded by double quotes.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-create-view &lt;boolean&gt; (property: createView)
 * &nbsp;&nbsp;&nbsp;If enabled, only a view of the column subset is created.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetSubset
  extends AbstractSpreadSheetTransformer
  implements SpreadSheetViewCreator {

  /** for serialization. */
  private static final long serialVersionUID = -253714973019682939L;

  /** the rows of the subset to obtain. */
  protected Range m_Rows;

  /** the columns of the subset to obtain. */
  protected SpreadSheetColumnRange m_Columns;

  /** whether to create a view only. */
  protected boolean m_CreateView;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Extracts a subset of rows/columns from a spreadsheet.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "row", "rows",
      new Range(Range.ALL));

    m_OptionManager.add(
      "col", "columns",
      new SpreadSheetColumnRange(Range.ALL));

    m_OptionManager.add(
      "create-view", "createView",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Rows    = new Range();
    m_Columns = new SpreadSheetColumnRange();
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

    result  = QuickInfoHelper.toString(this, "rows", m_Rows, "rows: ");
    result += QuickInfoHelper.toString(this, "columns", m_Columns, "/cols: ");
    value  = QuickInfoHelper.toString(this, "createView", m_CreateView, ", view only");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Sets the rows of the subset.
   *
   * @param value	the rows
   */
  public void setRows(Range value) {
    m_Rows = value;
    reset();
  }

  /**
   * Returns the rows of the subset.
   *
   * @return		the rows
   */
  public Range getRows() {
    return m_Rows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowsTipText() {
    return "The rows of the subset to retrieve.";
  }

  /**
   * Sets the columns of the subset.
   *
   * @param value	the columns
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the columns of the subset.
   *
   * @return		the columns
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
    return "The columns of the subset to retrieve; " + m_Columns.getExample();
  }

  /**
   * Sets whether to create a view only.
   *
   * @param value	true if to create a view only
   */
  public void setCreateView(boolean value) {
    m_CreateView = value;
    reset();
  }

  /**
   * Returns whether to create only a view.
   *
   * @return		true if to create view only
   */
  public boolean getCreateView() {
    return m_CreateView;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String createViewTipText() {
    return "If enabled, then only a view of the subset is created.";
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
    SpreadSheet	subset;
    Row		row;
    Row		subrow;
    int[]	rows;
    int[]	cols;
    int		i;
    int		n;

    result = null;

    sheet = (SpreadSheet) m_InputToken.getPayload();
    m_Rows.setMax(sheet.getRowCount());
    m_Columns.setSpreadSheet(sheet);

    rows = m_Rows.getIntIndices();
    cols = m_Columns.getIntIndices();
    if (cols.length == 0) {
      result = "No columns selected!";
    }
    else {
      if (m_CreateView) {
	subset = new SpreadSheetView(sheet, rows, cols);
      }
      else {
	subset = sheet.newInstance();
	// header
	for (i = 0; i < cols.length; i++) {
	  subset.getHeaderRow().addCell("" + (i + 1)).setContent(
	    sheet.getHeaderRow().getCell(cols[i]).getContent());
	}
	// data
	for (n = 0; n < rows.length; n++) {
	  row = sheet.getRow(rows[n]);
	  subrow = subset.addRow("" + (subset.getRowCount()));
	  for (i = 0; i < cols.length; i++) {
	    subrow.addCell("" + (i + 1)).setContent(
	      row.getCell(cols[i]).getContent());
	  }
	}
      }

      m_OutputToken = new Token(subset);
    }

    return result;
  }
}
