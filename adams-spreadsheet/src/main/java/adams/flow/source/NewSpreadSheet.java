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
 * NewSpreadSheet.java
 * Copyright (C) 2012-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.base.BaseText;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetTypeHandler;
import adams.flow.core.Token;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Generates an empty spreadsheet.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
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
 * &nbsp;&nbsp;&nbsp;default: NewSpreadSheet
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
 * <pre>-spreadsheet-name &lt;java.lang.String&gt; (property: sheetName)
 * &nbsp;&nbsp;&nbsp;The name for the spreadsheet.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-columns &lt;adams.core.base.BaseText&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The comma-separated list of column names.
 * &nbsp;&nbsp;&nbsp;default: A,B,C
 * </pre>
 *
 * <pre>-columns-array &lt;adams.core.base.BaseString&gt; [-columns-array ...] (property: columnsArray)
 * &nbsp;&nbsp;&nbsp;The array of column names to use (takes precedence over comma-separated
 * &nbsp;&nbsp;&nbsp;list).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-data-row-type &lt;adams.data.spreadsheet.DataRow&gt; (property: dataRowType)
 * &nbsp;&nbsp;&nbsp;The type of row to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.DenseDataRow
 * </pre>
 *
 * <pre>-spreadsheet-type &lt;adams.data.spreadsheet.SpreadSheet&gt; (property: spreadSheetType)
 * &nbsp;&nbsp;&nbsp;The type of spreadsheet to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 *
 * <pre>-comments &lt;adams.core.base.BaseText&gt; (property: comments)
 * &nbsp;&nbsp;&nbsp;The comments to use.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class NewSpreadSheet
    extends AbstractSimpleSource
    implements SpreadSheetTypeHandler {

  /** for serialization. */
  private static final long serialVersionUID = 494594301273926225L;

  /** the spreadsheet name. */
  protected String m_SheetName;

  /** the comma-separated list of column headers. */
  protected BaseText m_Columns;

  /** the array of column headers to use. */
  protected BaseString[] m_ColumnsArray;

  /** the data row type to use. */
  protected DataRow m_DataRowType;

  /** the type of spreadsheet to use. */
  protected SpreadSheet m_SpreadSheetType;

  /** the initial comments to use. */
  protected BaseText m_Comments;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates an empty spreadsheet.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "spreadsheet-name", "sheetName",
        "");

    m_OptionManager.add(
        "columns", "columns",
        new BaseText("A,B,C"));

    m_OptionManager.add(
        "columns-array", "columnsArray",
        new BaseString[0]);

    m_OptionManager.add(
        "data-row-type", "dataRowType",
        new DenseDataRow());

    m_OptionManager.add(
        "spreadsheet-type", "spreadSheetType",
        new DefaultSpreadSheet());

    m_OptionManager.add(
        "comments", "comments",
        new BaseText());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "sheetName", (m_SheetName.isEmpty() ? "-none-" : m_SheetName), "name: ");
    result += QuickInfoHelper.toString(this, "columns", m_Columns, ", cols: ");
    result += QuickInfoHelper.toString(this, "columnsArray", m_ColumnsArray, ", cols array: ");
    result += QuickInfoHelper.toString(this, "dataRowType", m_DataRowType, ", row type: ");
    result += QuickInfoHelper.toString(this, "spreadSheetType", m_SpreadSheetType.getClass(), ", sheet: ");

    return result;
  }

  /**
   * Sets the name for the spreadsheet.
   *
   * @param value	the name
   */
  public void setSheetName(String value) {
    m_SheetName = value;
    reset();
  }

  /**
   * Returns the name for the spreadsheet
   *
   * @return		the name
   */
  public String getSheetName() {
    return m_SheetName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sheetNameTipText() {
    return "The name for the spreadsheet.";
  }

  /**
   * Sets the comma-separated list of column names.
   *
   * @param value	the list
   */
  public void setColumns(BaseText value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the comma-separated list of column names.
   *
   * @return		the list
   */
  public BaseText getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsTipText() {
    return "The comma-separated list of column names.";
  }

  /**
   * Sets the array of column names (takes precedence over comma-separated list).
   *
   * @param value	the array
   */
  public void setColumnsArray(BaseString[] value) {
    m_ColumnsArray = value;
    reset();
  }

  /**
   * Returns the array of column names (takes precedence over comma-separated list).
   *
   * @return		the array
   */
  public BaseString[] getColumnsArray() {
    return m_ColumnsArray;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsArrayTipText() {
    return "The array of column names to use (takes precedence over comma-separated list).";
  }

  /**
   * Sets the type of data row to use.
   *
   * @param value	the type
   */
  public void setDataRowType(DataRow value) {
    m_DataRowType = value;
    reset();
  }

  /**
   * Returns the type of data row to use.
   *
   * @return		the type
   */
  public DataRow getDataRowType() {
    return m_DataRowType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataRowTypeTipText() {
    return "The type of row to use for the data.";
  }

  /**
   * Sets the type of spreadsheet to use.
   *
   * @param value	the type
   */
  public void setSpreadSheetType(SpreadSheet value) {
    m_SpreadSheetType = value;
    reset();
  }

  /**
   * Returns the type of spreadsheet to use.
   *
   * @return		the type
   */
  public SpreadSheet getSpreadSheetType() {
    return m_SpreadSheetType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String spreadSheetTypeTipText() {
    return "The type of spreadsheet to use for the data.";
  }

  /**
   * Sets the comments to use.
   *
   * @param value	the comments
   */
  public void setComments(BaseText value) {
    m_Comments = value;
    reset();
  }

  /**
   * Returns the comments to use.
   *
   * @return		the comments
   */
  public BaseText getComments() {
    return m_Comments;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commentsTipText() {
    return "The comments to use.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
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
    String[]	cols;
    SpreadSheet	sheet;
    String[]    lines;

    result = null;

    sheet = m_SpreadSheetType.newInstance();
    if (!m_SheetName.isEmpty())
      sheet.setName(m_SheetName);
    sheet.setDataRowClass(m_DataRowType.getClass());

    // determine columns
    cols = null;
    if (m_ColumnsArray.length > 0)
      cols = BaseObject.toStringArray(m_ColumnsArray);
    else if (!m_Columns.isEmpty())
      cols = m_Columns.getValue().split(",");

    // create header
    if (cols != null) {
      for (String col : cols)
        sheet.getHeaderRow().addCell("" + sheet.getColumnCount()).setContentAsString(col);
    }

    // add comments
    if (!m_Comments.isEmpty()) {
      lines = Utils.split(m_Comments.getValue(), "\n");
      sheet.getComments().addAll(Arrays.asList(lines));
    }

    m_OutputToken = new Token(sheet);

    return result;
  }
}
