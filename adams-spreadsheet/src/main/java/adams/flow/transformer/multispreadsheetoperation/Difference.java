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
 * Difference.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.multispreadsheetoperation;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.RowIdentifier;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;

import java.util.HashSet;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Computes the difference of the numeric cells between two spreadsheets.<br>
 * The values of the second spreadsheet are subtracted from the first one.<br>
 * If no 'key' columns are defined, the current order of rows is used for comparison.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-key-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: keyColumns)
 * &nbsp;&nbsp;&nbsp;The columns to use as keys for identifying rows in the spreadsheets, if
 * &nbsp;&nbsp;&nbsp;empty the row index is used instead; A range is a comma-separated list of
 * &nbsp;&nbsp;&nbsp;single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)'
 * &nbsp;&nbsp;&nbsp; inverts the range '...'; column names (case-sensitive) as well as the following
 * &nbsp;&nbsp;&nbsp;placeholders can be used: first, second, third, last_2, last_1, last; numeric
 * &nbsp;&nbsp;&nbsp;indices can be enforced by preceding them with '#' (eg '#12'); column names
 * &nbsp;&nbsp;&nbsp;can be surrounded by double quotes.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Difference
  extends AbstractMultiSpreadSheetOperation<SpreadSheet> {

  /** for serialization. */
  private static final long serialVersionUID = -5056170789277731638L;

  /** the range of column indices to use as key for identifying a row. */
  protected SpreadSheetColumnRange m_KeyColumns;

  /** for locating the rows. */
  protected RowIdentifier[] m_Rows;

  /** the column indices to use. */
  protected int[] m_ColIndices;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Computes the difference of the numeric cells between two spreadsheets.\n"
        + "The values of the second spreadsheet are subtracted from the first one.\n"
        + "If no 'key' columns are defined, the current order of rows is used "
        + "for comparison.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key-columns", "keyColumns",
      new SpreadSheetColumnRange(""));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "keyColumns", m_KeyColumns, "key columns: ");
  }

  /**
   * Returns the minimum number of sheets that are required for the operation.
   *
   * @return the number of sheets that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumSheetsRequired() {
    return 2;
  }

  /**
   * Returns the maximum number of sheets that are required for the operation.
   *
   * @return the number of sheets that are required, <= 0 means no upper limit
   */
  @Override
  public int maxNumSheetsRequired() {
    return 2;
  }

  /**
   * Sets the colums that identify a row, use empty string to simply use row index.
   *
   * @param value	the range
   */
  public void setKeyColumns(SpreadSheetColumnRange value) {
    m_KeyColumns = value;
    reset();
  }

  /**
   * Returns the colums that identify a row, use empty string to simply use row index
   *
   * @return		the range
   */
  public SpreadSheetColumnRange getKeyColumns() {
    return m_KeyColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyColumnsTipText() {
    return
      "The columns to use as keys for identifying rows in the spreadsheets, if empty the row index is used instead; " + m_KeyColumns.getExample();
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		adams.core.io.SpreadSheet.class
   */
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Initializes the row lookup, if necessary.
   *
   * @param sheets	the sheets to generated lookup for
   */
  protected void initRowLookup(SpreadSheet[] sheets) {
    if (m_Rows != null)
      return;

    m_Rows       = new RowIdentifier[2];
    m_ColIndices = new int[0];

    if (m_KeyColumns.getRange().length() != 0) {
      m_KeyColumns.setSpreadSheet(sheets[0]);
      m_ColIndices = m_KeyColumns.getIntIndices();
      m_Rows[0]    = new RowIdentifier(m_KeyColumns);
      m_Rows[1]    = new RowIdentifier(m_KeyColumns);
      m_Rows[0].identify(sheets[0]);
      m_Rows[1].identify(sheets[1]);
    }
  }

  /**
   * Computes the difference between the two rows: actual difference is
   * computed for numeric cells. If cells are strings, then the results is
   * a missing value in case of differing strings, otherwise the same.
   *
   * @param output	the spreadsheet the new row will get added to
   * @param row1	the row from the first sheet
   * @param row2	the row from the second sheet
   * @return		the generated difference
   */
  protected Row difference(SpreadSheet output, Row row1, Row row2) {
    Row			result;
    Cell		cell1;
    Cell		cell2;
    int			index;
    Set<Integer> 	indices;

    result = row1.getClone(output);
    result.clear();

    indices = new HashSet<>();
    for (int i: m_ColIndices)
      indices.add(m_ColIndices[i]);

    for (String key: row1.cellKeys()) {
      index = row1.getOwner().getHeaderRow().indexOf(key);
      cell1 = row1.getCell(key);
      cell2 = row2.getCell(key);
      if (indices.contains(index)) {
        result.addCell(key).setContent(cell1.getContent());
      }
      else if ((cell1 == null) || (cell2 == null)) {
        result.addCell(key).setContent(SpreadSheet.MISSING_VALUE);
      }
      else if (cell1.isMissing() || cell2.isMissing()) {
        result.addCell(key).setContent(SpreadSheet.MISSING_VALUE);
      }
      else if (cell1.isNumeric() && cell2.isNumeric()){
        result.addCell(key).setContent(cell1.toDouble() - cell2.toDouble());
      }
      else {
        if (cell1.getContent().equals(cell2.getContent()))
          result.addCell(key).setContent(cell1.getContent());
        else
          result.addCell(key).setContent(SpreadSheet.MISSING_VALUE);
      }
    }

    return result;
  }

  /**
   * Generates a row and appends it to the output.
   *
   * @param output	the spreadsheet to receive the output
   * @param rowDiff	the difference row
   */
  protected void generateOutputRow(SpreadSheet output, Row rowDiff) {
    Row		rowNew;
    Row		header;
    int		n;
    String	key;

    header = output.getHeaderRow();
    rowNew = output.addRow();
    if (rowDiff != null) {
      for (n = 0; n < header.getCellCount(); n++) {
        key = header.getCellKey(n);
        if (rowDiff.hasCell(key) && !rowDiff.getCell(key).isMissing())
          rowNew.addCell(key).setContent(rowDiff.getCell(key).getContent());
        else
          rowNew.addCell(key).setContent(SpreadSheet.MISSING_VALUE);
      }
    }
  }

  /**
   * Performs the actual processing of the sheets.
   *
   * @param sheets the containers to process
   * @param errors for collecting errors
   * @return the generated data
   */
  @Override
  protected SpreadSheet doProcess(SpreadSheet[] sheets, MessageCollection errors) {
    SpreadSheet 	result;
    String 		msg;
    Row			row1;
    Row			row2;
    int			n;

    result = null;
    m_Rows = null;

    msg = sheets[0].equalsHeader(sheets[0]);
    if (msg != null)
      errors.add("Spreadsheets not compatible: " + msg);

    if (errors.isEmpty()) {
      result = sheets[0].getHeader();
      initRowLookup(sheets);
      if (m_ColIndices.length > 0) {
        for (String key: m_Rows[0].getKeys()) {
          row1 = sheets[0].getRow(m_Rows[0].getRows(key).get(0));
          row2 = null;
          if (m_Rows[1].getRows(key) != null)
            row2 = sheets[1].getRow(m_Rows[1].getRows(key).get(0));
          if (row2 != null)
            generateOutputRow(result, difference(result, row1, row2));
        }
      }
      else {
        for (n = 0; n < sheets[0].getRowCount() && n < sheets[1].getRowCount(); n++) {
          row1 = sheets[0].getRow(n);
          row2 = sheets[1].getRow(n);
          generateOutputRow(result, difference(result, row1, row2));
        }
      }
    }

    // clean up
    m_Rows       = null;
    m_ColIndices = null;

    return result;
  }
}
