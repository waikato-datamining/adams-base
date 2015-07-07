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
 * NewSpreadSheet.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;

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
 * <pre>-spreadsheet-name &lt;java.lang.String&gt; (property: sheetName)
 * &nbsp;&nbsp;&nbsp;The name for the spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-columns &lt;java.lang.String&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The comma-separated list of column names.
 * &nbsp;&nbsp;&nbsp;default: A,B,C
 * </pre>
 * 
 * <pre>-data-row-type &lt;DENSE|SPARSE&gt; (property: dataRowType)
 * &nbsp;&nbsp;&nbsp;The type of row to use for the data.
 * &nbsp;&nbsp;&nbsp;default: DENSE
 * </pre>
 * 
 * <pre>-spreadsheet-type &lt;adams.data.spreadsheet.SpreadSheet&gt; (property: spreadSheetType)
 * &nbsp;&nbsp;&nbsp;The type of spreadsheet to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.SpreadSheet
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NewSpreadSheet
  extends AbstractSimpleSource {

  /** for serialization. */
  private static final long serialVersionUID = 494594301273926225L;

  /** the spreadsheet name. */
  protected String m_SheetName;

  /** the comma-separated list of column headers. */
  protected String m_Columns;

  /** the data row type to use. */
  protected DataRow m_DataRowType;

  /** the type of spreadsheet to use. */
  protected SpreadSheet m_SpreadSheetType;

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
	    "A,B,C");

    m_OptionManager.add(
	    "data-row-type", "dataRowType",
	    new DenseDataRow());

    m_OptionManager.add(
	    "spreadsheet-type", "spreadSheetType",
	    new SpreadSheet());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "sheetName", m_SheetName, "name: ");
    result += QuickInfoHelper.toString(this, "columns", m_Columns, ", cols: ");
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
  public void setColumns(String value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the comma-separated list of column names.
   *
   * @return		the list
   */
  public String getColumns() {
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
    
    result = null;

    sheet = m_SpreadSheetType.newInstance();
    if (!m_SheetName.isEmpty())
      sheet.setName(m_SheetName);
    sheet.setDataRowClass(m_DataRowType.getClass());
    cols  = m_Columns.split(",");
    for (String col: cols)
      sheet.getHeaderRow().addCell("" + sheet.getColumnCount()).setContentAsString(col);
    
    m_OutputToken = new Token(sheet);
    
    return result;
  }
}
