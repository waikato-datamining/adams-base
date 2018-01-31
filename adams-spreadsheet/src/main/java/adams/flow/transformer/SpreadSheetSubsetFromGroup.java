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
 * SpreadSheetSubsetFromGroup.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetView;
import adams.data.spreadsheet.SpreadSheetViewCreator;
import adams.flow.core.Token;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 <!-- globalinfo-start -->
 * Splits the spreadsheet into subsets using the supplied column and then returns the specified range of rows from each generated subset.<br>
 * The spreadsheet is expected to be sorted on the grouping column.
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetSubsetFromGroup
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
 * <pre>-col &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column which unique string values identify the subsets; An index is
 * &nbsp;&nbsp;&nbsp;a number starting with 1; column names (case-sensitive) as well as the following
 * &nbsp;&nbsp;&nbsp;placeholders can be used: first, second, third, last_2, last_1, last; numeric
 * &nbsp;&nbsp;&nbsp;indices can be enforced by preceding them with '#' (eg '#12'); column names
 * &nbsp;&nbsp;&nbsp;can be surrounded by double quotes.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-row &lt;adams.core.Range&gt; (property: rows)
 * &nbsp;&nbsp;&nbsp;The rows of the subset to retrieve.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 *
 * <pre>-create-view &lt;boolean&gt; (property: createView)
 * &nbsp;&nbsp;&nbsp;If enabled, then only a view of the subset is created.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetSubsetFromGroup
  extends AbstractSpreadSheetTransformer
  implements SpreadSheetViewCreator {

  private static final long serialVersionUID = 5167598748037763968L;

  /** the string values of this column are used to split the sheet into groups. */
  protected SpreadSheetColumnIndex m_Column;

  /** the rows of the group to obtain. */
  protected Range m_Rows;

  /** whether to create a view only. */
  protected boolean m_CreateView;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Splits the spreadsheet into subsets using the supplied column and then returns "
	+ "the specified range of rows from each generated subset.\n"
	+ "The spreadsheet is expected to be sorted on the grouping column.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "col", "column",
      new SpreadSheetColumnIndex(Range.FIRST));

    m_OptionManager.add(
      "row", "rows",
      new Range(Range.ALL));

    m_OptionManager.add(
      "create-view", "createView",
      false);
  }

  /**
   * Sets the column that identifies the subsets.
   *
   * @param value	the column
   */
  public void setColumn(SpreadSheetColumnIndex value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column that identifies the subsets.
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
    return "The column which unique string values identify the subsets; " + m_Column.getExample();
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = QuickInfoHelper.toString(this, "column", m_Column, "col: ");
    result += QuickInfoHelper.toString(this, "rows", m_Rows, ", rows: ");
    value  = QuickInfoHelper.toString(this, "createView", m_CreateView, ", view only");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Transfers the indices from the current group into the final list of indices.
   *
   * @param group	the current group
   * @param indices	the final indices
   */
  protected void transfer(TIntList group, TIntList indices) {
    int[]	rows;
    int 	i;
    TIntList	subset;

    subset = new TIntArrayList();
    m_Rows.setMax(group.size());
    rows = m_Rows.getIntIndices();
    for (i = 0; i < rows.length; i++)
      subset.add(group.get(rows[i]));
    indices.addAll(subset);
    group.clear();
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    SpreadSheet		sheet;
    int			col;
    TIntList 		group;
    TIntList 		indices;
    SpreadSheet		subset;
    int			i;
    String		key;
    String		oldKey;
    boolean		alwaysEmpty;

    result      = null;
    sheet       = m_InputToken.getPayload(SpreadSheet.class);
    indices     = null;
    group       = new TIntArrayList();
    alwaysEmpty = true;
    m_Column.setSpreadSheet(sheet);
    col = m_Column.getIntIndex();
    if (col == -1)
      result = "Failed to locate column: " + m_Column.getIndex();

    if (result == null) {
      indices = new TIntArrayList();
      key     = null;
      for (i = 0; i < sheet.getRowCount(); i++) {
	oldKey = key;
	if (isStopped()) {
	  group.clear();
	  indices = null;
	  break;
	}

	// new group?
	key = sheet.getRow(i).getContent(col);
	if (oldKey == null) {
	  group.add(i);
	  continue;
	}
	if (key == null)
	  continue;

	if (!key.equals(oldKey)) {
	  if (group.size() > 0)
	    alwaysEmpty = false;
	  transfer(group, indices);
	}
	group.add(i);
      }

      // final group
      if ((group.size() > 0) && (indices != null)) {
	if (group.size() > 0)
	  alwaysEmpty = false;
	transfer(group, indices);
      }
    }

    if (indices != null) {
      if (m_CreateView) {
        subset = new SpreadSheetView(sheet, indices.toArray(), null);
      }
      else {
        subset = sheet.getHeader();
        for (i = 0; i < indices.size(); i++)
          subset.addRow().assign(sheet.getRow(indices.get(i)));
      }
      m_OutputToken = new Token(subset);
      if (alwaysEmpty)
        getLogger().warning("Never matched any rows!");
    }

    return result;
  }
}
