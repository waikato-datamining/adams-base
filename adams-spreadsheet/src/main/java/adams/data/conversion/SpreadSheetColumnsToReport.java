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
 * SpreadSheetColumnsToReport.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.report.Report;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetRowRange;

/**
 <!-- globalinfo-start -->
 * Turns spreadsheet columns into reports.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-col-report-names &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: colReportNames)
 * &nbsp;&nbsp;&nbsp;The (optional) column that contains the report names.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-cols-report-values &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: colsReportValues)
 * &nbsp;&nbsp;&nbsp;The columns to get the report values from.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-rows-report &lt;adams.data.spreadsheet.SpreadSheetRowRange&gt; (property: rowsReport)
 * &nbsp;&nbsp;&nbsp;The rows that contain report.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetColumnsToReport
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -258589003642261978L;

  /** the column with the report names. */
  protected SpreadSheetColumnIndex m_ColReportNames;

  /** the columns with the report values. */
  protected SpreadSheetColumnRange m_ColsReportValues;

  /** the rows to get the report from. */
  protected SpreadSheetRowRange m_RowsReport;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns spreadsheet columns into reports.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "col-report-names", "colReportNames",
      new SpreadSheetColumnIndex());

    m_OptionManager.add(
      "cols-report-values", "colsReportValues",
      new SpreadSheetColumnRange());

    m_OptionManager.add(
      "rows-report", "rowsReport",
      new SpreadSheetRowRange());
  }

  /**
   * Sets the column that contains the report names.
   *
   * @param value	the column
   */
  public void setColReportNames(SpreadSheetColumnIndex value) {
    m_ColReportNames = value;
    reset();
  }

  /**
   * Returns the column that contains the report names.
   *
   * @return 		the column
   */
  public SpreadSheetColumnIndex getColReportNames() {
    return m_ColReportNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colReportNamesTipText() {
    return "The (optional) column that contains the report names.";
  }

  /**
   * Sets the rows with report.
   *
   * @param value	the rows
   */
  public void setRowsReport(SpreadSheetRowRange value) {
    m_RowsReport = value;
    reset();
  }

  /**
   * Returns the rows with report.
   *
   * @return 		the rows
   */
  public SpreadSheetRowRange getRowsReport() {
    return m_RowsReport;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowsReportTipText() {
    return "The rows that contain report.";
  }

  /**
   * Sets the columns to get the report values from.
   *
   * @param value	the columns
   */
  public void setColsReportValues(SpreadSheetColumnRange value) {
    m_ColsReportValues = value;
    reset();
  }

  /**
   * Returns the columns to get the report values from.
   *
   * @return 		the columns
   */
  public SpreadSheetColumnRange getColsReportValues() {
    return m_ColsReportValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colsReportValuesTipText() {
    return "The columns to get the report values from.";
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

    result = QuickInfoHelper.toString(this, "rowsReport", (m_RowsReport.isEmpty() ? "-none-" : m_RowsReport.getRange()), "rows: ");
    result += QuickInfoHelper.toString(this, "colReportNames", (m_ColReportNames.isEmpty() ? "-none-" : m_ColReportNames.getIndex()), ", names: ");
    result += QuickInfoHelper.toString(this, "colsReportValues", (m_ColsReportValues.isEmpty() ? "-none-" : m_ColsReportValues.getRange()), ", value cols: ");

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
    Report[]		result;
    SpreadSheet		sheet;
    int			i;
    int			n;
    int			colMeta;
    int[]		rowsMeta;
    int[]		colsValues;
    Row			row;
    Cell 		cell;

    sheet = (SpreadSheet) m_Input;

    m_ColReportNames.setSpreadSheet(sheet);
    colMeta = m_ColReportNames.getIntIndex();
    if (colMeta == -1)
      throw new IllegalStateException("No column for report names: " + m_ColReportNames.getIndex());

    m_ColsReportValues.setSpreadSheet(sheet);
    colsValues = m_ColsReportValues.getIntIndices();
    if (colsValues.length == 0)
      throw new IllegalStateException("No columns for report values: " + m_ColsReportValues.getRange());

    m_RowsReport.setSpreadSheet(sheet);
    rowsMeta = m_RowsReport.getIntIndices();
    if (rowsMeta.length == 0)
      throw new IllegalStateException("No rows for report: " + m_RowsReport.getRange());

    result = new Report[colsValues.length];
    for (i = 0; i < colsValues.length; i++) {
      result[i] = new Report();

      // report
      if (colMeta > -1) {
	for (n = 0; n < rowsMeta.length; n++) {
	  row = sheet.getRow(rowsMeta[n]);
	  if (row.hasCell(colsValues[i]) && !row.getCell(colsValues[i]).isMissing()) {
	    cell = row.getCell(colsValues[i]);
	    if (cell.isNumeric())
	      result[i].setNumericValue(row.getCell(colMeta).getContent(), cell.toDouble());
	    else if (cell.isBoolean())
	      result[i].setBooleanValue(row.getCell(colMeta).getContent(), cell.toBoolean());
	    else
	      result[i].setStringValue(row.getCell(colMeta).getContent(), cell.getContent());
	  }
	}
      }
    }
    
    return result;
  }
}
