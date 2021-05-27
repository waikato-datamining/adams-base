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
 * SpreadSheetRowsToReport.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.report.Report;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetRowIndex;
import adams.data.spreadsheet.SpreadSheetRowRange;

/**
 <!-- globalinfo-start -->
 * Turns spreadsheet rows into report.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-row-report-names &lt;adams.data.spreadsheet.SpreadSheetRowIndex&gt; (property: rowReportNames)
 * &nbsp;&nbsp;&nbsp;The (optional) row that contains the report names.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 *
 * <pre>-report-names-in-header &lt;boolean&gt; (property: reportNamesInHeader)
 * &nbsp;&nbsp;&nbsp;Whether the report names are stored in the header.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-rows-report-values &lt;adams.data.spreadsheet.SpreadSheetRowRange&gt; (property: rowsReportValues)
 * &nbsp;&nbsp;&nbsp;The rows in the spreadsheet that contain the report values.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 *
 * <pre>-cols-report &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: columnsReport)
 * &nbsp;&nbsp;&nbsp;The columns that contain report.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetRowsToReport
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -258589003642261978L;

  /** the column with the report names. */
  protected SpreadSheetRowIndex m_RowReportNames;

  /** whether the report names are in the header row. */
  protected boolean m_ReportNamesInHeader;

  /** the rows with report values. */
  protected SpreadSheetRowRange m_RowsReportValues;

  /** the rows to get the report from. */
  protected SpreadSheetColumnRange m_ColumnsReport;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns spreadsheet rows into report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "row-report-names", "rowReportNames",
      new SpreadSheetRowIndex());

    m_OptionManager.add(
      "report-names-in-header", "reportNamesInHeader",
      false);

    m_OptionManager.add(
      "rows-report-values", "rowsReportValues",
      new SpreadSheetRowRange());

    m_OptionManager.add(
      "cols-report", "columnsReport",
      new SpreadSheetColumnRange());
  }

  /**
   * Sets the row that contains the report names.
   *
   * @param value	the row
   */
  public void setRowReportNames(SpreadSheetRowIndex value) {
    m_RowReportNames = value;
    reset();
  }

  /**
   * Returns the row that contains the report names.
   *
   * @return 		the row
   */
  public SpreadSheetRowIndex getRowReportNames() {
    return m_RowReportNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowReportNamesTipText() {
    return "The (optional) row that contains the report names.";
  }

  /**
   * Sets whether the wave numbers are in the header.
   *
   * @param value	true if in header
   */
  public void setReportNamesInHeader(boolean value) {
    m_ReportNamesInHeader = value;
    reset();
  }

  /**
   * Returns whether the report names are in the header.
   *
   * @return 		true if in header
   */
  public boolean getReportNamesInHeader() {
    return m_ReportNamesInHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String reportNamesInHeaderTipText() {
    return "Whether the report names are stored in the header.";
  }

  /**
   * Sets the columns with report.
   *
   * @param value	the columns
   */
  public void setColumnsReport(SpreadSheetColumnRange value) {
    m_ColumnsReport = value;
    reset();
  }

  /**
   * Returns the columns with report.
   *
   * @return 		the columns
   */
  public SpreadSheetColumnRange getColumnsReport() {
    return m_ColumnsReport;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsReportTipText() {
    return "The columns that contain report.";
  }

  /**
   * Sets the rows with the report values.
   *
   * @param value	the rows
   */
  public void setRowsReportValues(SpreadSheetRowRange value) {
    m_RowsReportValues = value;
    reset();
  }

  /**
   * Returns the rows with the report values.
   *
   * @return 		the rows
   */
  public SpreadSheetRowRange getRowsReportValues() {
    return m_RowsReportValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowsReportValuesTipText() {
    return "The rows in the spreadsheet that contain the report values.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Report[].class;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "columnsReport", (m_ColumnsReport.isEmpty() ? "-none-" : m_ColumnsReport.getRange()), "cols: ");
    result += QuickInfoHelper.toString(this, "reportNamesInHeader", m_ReportNamesInHeader, "SD names in header", ", ");
    result += QuickInfoHelper.toString(this, "rowsReportValues", (m_RowsReportValues.isEmpty() ? "-none-" : m_RowsReportValues.getRange()), ", rows: ");

    return result;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Report[]	result;
    SpreadSheet		sheet;
    int[] 		rowsValues;
    int			i;
    int			n;
    int 		rowMeta;
    int[] 		colsMeta;
    Row			row;
    Row			rowMetaObj;
    Cell 		cell;

    sheet = (SpreadSheet) m_Input;

    m_RowsReportValues.setSpreadSheet(sheet);
    rowsValues = m_RowsReportValues.getIntIndices();
    if (rowsValues.length == 0)
      throw new IllegalStateException("Failed to locate rows with report values: " + m_RowsReportValues);

    if (m_ReportNamesInHeader) {
      rowMetaObj = sheet.getHeaderRow();
    }
    else {
      m_RowReportNames.setSpreadSheet(sheet);
      rowMeta = m_RowReportNames.getIntIndex();
      if (rowMeta == -1)
        throw new IllegalStateException("Failed to locate row with report names: " + m_RowReportNames.getIndex());
      rowMetaObj = sheet.getRow(rowMeta);
    }

    m_ColumnsReport.setSpreadSheet(sheet);
    colsMeta = m_ColumnsReport.getIntIndices();
    if (colsMeta.length == 0)
      throw new IllegalStateException("Failed to locate columns with report: " + m_ColumnsReport.getRange());

    result = new Report[rowsValues.length];
    for (i = 0; i < rowsValues.length; i++) {
      row       = sheet.getRow(rowsValues[i]);
      result[i] = new Report();

      // report
      if (rowMetaObj != null) {
	for (n = 0; n < colsMeta.length; n++) {
	  if (row.hasCell(colsMeta[n]) && !row.getCell(colsMeta[n]).isMissing()) {
	    cell = row.getCell(colsMeta[n]);
	    if (cell.isNumeric())
	      result[i].setNumericValue(rowMetaObj.getCell(colsMeta[n]).getContent(), cell.toDouble());
	    else if (cell.isBoolean())
	      result[i].setBooleanValue(rowMetaObj.getCell(colsMeta[n]).getContent(), cell.toBoolean());
	    else
	      result[i].setStringValue(rowMetaObj.getCell(colsMeta[n]).getContent(), cell.getContent());
	  }
	}
      }
    }
    
    return result;
  }
}
