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
 * ODFSpreadSheetReader.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.Range;
import adams.core.Utils;
import adams.data.io.output.ODFSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import org.jopendocument.dom.ODPackage;
import org.jopendocument.dom.ODValueType;
import org.jopendocument.dom.spreadsheet.Sheet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Reads ODF (Open Document Format) spreadsheet files.<br>
 * If a row contains only empty cells, this is interpreted as the end of the sheet.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
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
 * <pre>-sheets &lt;adams.core.Range&gt; (property: sheetRange)
 * &nbsp;&nbsp;&nbsp;The range of sheets to load.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-missing &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The placeholder for missing values.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-text-columns &lt;java.lang.String&gt; (property: textColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to treat as text.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-no-header &lt;boolean&gt; (property: noHeader)
 * &nbsp;&nbsp;&nbsp;If enabled, all rows get added as data rows and a dummy header will get 
 * &nbsp;&nbsp;&nbsp;inserted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-custom-column-headers &lt;java.lang.String&gt; (property: customColumnHeaders)
 * &nbsp;&nbsp;&nbsp;The custom headers to use for the columns instead (comma-separated list);
 * &nbsp;&nbsp;&nbsp; ignored if empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-first-row &lt;int&gt; (property: firstRow)
 * &nbsp;&nbsp;&nbsp;The index of the first row to retrieve (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-num-rows &lt;int&gt; (property: numRows)
 * &nbsp;&nbsp;&nbsp;The number of data rows to retrieve; use -1 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ODFSpreadSheetReader
  extends AbstractMultiSheetSpreadSheetReaderWithMissingValueSupport
  implements NoHeaderSpreadSheetReader, WindowedSpreadSheetReader {

  /** for serialization. */
  private static final long serialVersionUID = 4755872204697328246L;

  /** the range of columns to force to be text. */
  protected Range m_TextColumns;

  /** whether the file has a header or not. */
  protected boolean m_NoHeader;

  /** the comma-separated list of column header names. */
  protected String m_CustomColumnHeaders;

  /** the first row to retrieve (1-based). */
  protected int m_FirstRow;

  /** the number of rows to retrieve (less than 1 = unlimited). */
  protected int m_NumRows;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Reads ODF (Open Document Format) spreadsheet files.\n"
      + "If a row contains only empty cells, this is interpreted as the end "
      + "of the sheet.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "text-columns", "textColumns",
      "");

    m_OptionManager.add(
      "no-header", "noHeader",
      false);

    m_OptionManager.add(
      "custom-column-headers", "customColumnHeaders",
      "");

    m_OptionManager.add(
      "first-row", "firstRow",
      1, 1, null);

    m_OptionManager.add(
      "num-rows", "numRows",
      -1, -1, null);
  }

  /**
   * Returns the default string for missing values.
   * 
   * @return		the default
   */
  @Override
  protected String getDefaultMissingValue() {
    return "";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "OpenDocument format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"ods"};
  }

  /**
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  public SpreadSheetWriter getCorrespondingWriter() {
    return new ODFSpreadSheetWriter();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_TextColumns = new Range();
  }

  /**
   * Sets the range of columns to treat as text.
   *
   * @param value	the range of columns
   */
  public void setTextColumns(String value) {
    m_TextColumns.setRange(value);
    reset();
  }

  /**
   * Returns the range of columns to treat as text.
   *
   * @return		the range of columns
   */
  public String getTextColumns() {
    return m_TextColumns.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String textColumnsTipText() {
    return "The range of columns to treat as text.";
  }

  /**
   * Sets whether the file contains a header row or not.
   *
   * @param value	true if no header row available
   */
  public void setNoHeader(boolean value) {
    m_NoHeader = value;
    reset();
  }

  /**
   * Returns whether the file contains a header row or not.
   *
   * @return		true if no header row available
   */
  public boolean getNoHeader() {
    return m_NoHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String noHeaderTipText() {
    return "If enabled, all rows get added as data rows and a dummy header will get inserted.";
  }

  /**
   * Sets the custom headers to use.
   *
   * @param value	the comma-separated list
   */
  public void setCustomColumnHeaders(String value) {
    m_CustomColumnHeaders = value;
    reset();
  }

  /**
   * Returns whether the file contains a header row or not.
   *
   * @return		the comma-separated list
   */
  public String getCustomColumnHeaders() {
    return m_CustomColumnHeaders;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String customColumnHeadersTipText() {
    return "The custom headers to use for the columns instead (comma-separated list); ignored if empty.";
  }

  /**
   * Sets the first row to return.
   *
   * @param value	the first row (1-based), greater than 0
   */
  public void setFirstRow(int value) {
    if (getOptionManager().isValid("firstRow", value)) {
      m_FirstRow = value;
      reset();
    }
  }

  /**
   * Returns the first row to return.
   *
   * @return		the first row (1-based), greater than 0
   */
  public int getFirstRow() {
    return m_FirstRow;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String firstRowTipText() {
    return "The index of the first row to retrieve (1-based).";
  }

  /**
   * Sets the number of data rows to return.
   *
   * @param value	the number of rows, -1 for unlimited
   */
  public void setNumRows(int value) {
    if (value < 0)
      m_NumRows = -1;
    else
      m_NumRows = value;
    reset();
  }

  /**
   * Returns the number of data rows to return.
   *
   * @return		the number of rows, -1 for unlimited
   */
  public int getNumRows() {
    return m_NumRows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numRowsTipText() {
    return "The number of data rows to retrieve; use -1 for unlimited.";
  }

  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return		how to read the data
   */
  @Override
  protected InputType getInputType() {
    return InputType.STREAM;
  }

  /**
   * Turns a numeric cell into a string. Tries to use "long" representation
   * if possible.
   *
   * @param s		the string to process
   * @return		the string representation
   */
  protected String numericToString(String s) {
    Double	dbl;
    long	lng;

    dbl = Utils.toDouble(s);
    lng = dbl.longValue();
    if (dbl == lng)
      return "" + lng;
    else
      return "" + dbl;
  }

  /**
   * Reads the spreadsheet content from the specified file.
   *
   * @param in		the input stream to read from
   * @return		the spreadsheets or null in case of an error
   */
  @Override
  protected List<SpreadSheet> doReadRange(InputStream in) {
    List<SpreadSheet>					result;
    org.jopendocument.dom.spreadsheet.SpreadSheet	spreadsheet;
    SpreadSheet		spsheet;
    Sheet		sheet;
    int			i;
    int			n;
    Row			row;
    Object[]		data;
    boolean		empty;
    String		cellStr;
    ODValueType		type;
    ContentType[]	cellTypes;
    int[]		indices;
    int                 dataRowStart;
    int			firstRow;
    int 		lastRow;
    List<String>        header;

    result = new ArrayList<>();

    try {
      spreadsheet = org.jopendocument.dom.spreadsheet.SpreadSheet.get(new ODPackage(in));
      m_SheetRange.setMax(spreadsheet.getSheetCount());
      indices = m_SheetRange.getIntIndices();
      firstRow = m_FirstRow - 1;
      dataRowStart = getNoHeader() ? firstRow : firstRow + 1;
      for (int index: indices) {
	spsheet = m_SpreadSheetType.newInstance();
	spsheet.setDataRowClass(m_DataRowType.getClass());
	result.add(spsheet);
	sheet   = spreadsheet.getSheet(index);
	spsheet.setName(sheet.getName());
	if (isLoggingEnabled())
	  getLogger().info("sheet: " + (index+1));

	// header
	if (isLoggingEnabled())
	  getLogger().info("header row");
	row = spsheet.getHeaderRow();
        if (getNoHeader()) {
          header = SpreadSheetUtils.createHeader(sheet.getColumnCount(), m_CustomColumnHeaders);
          for (i = 0; i < header.size(); i++)
            row.addCell("" + (i + 1)).setContent(header.get(i));
        }
        else {
          if (!m_CustomColumnHeaders.trim().isEmpty()) {
            header = SpreadSheetUtils.createHeader(sheet.getColumnCount(), m_CustomColumnHeaders);
            for (i = 0; i < header.size(); i++)
              row.addCell("" + (i + 1)).setContent(header.get(i));
          }
          else {
            for (i = 0; i < sheet.getColumnCount(); i++) {
              if (m_Stopped)
                break;
              cellStr = sheet.getCellAt(i, firstRow).getTextValue();
              if (cellStr.length() == 0)
                break;
              row.addCell("" + (i + 1)).setContent(cellStr);
            }
          }
        }

	// data
	m_TextColumns.setMax(spsheet.getColumnCount());
	if (m_NumRows < 1)
	  lastRow = sheet.getRowCount() - 1;
	else
	  lastRow = Math.min(firstRow + m_NumRows, sheet.getRowCount() - 1);
	for (n = dataRowStart; n <= lastRow; n++) {
	  if (m_Stopped)
	    break;
	  if (isLoggingEnabled())
	    getLogger().info("data row: " + (i+1));
	  data      = new Object[spsheet.getColumnCount()];
	  cellTypes = new ContentType[spsheet.getColumnCount()];
	  empty     = true;
	  for (i = 0; i < spsheet.getColumnCount(); i++) {
	    type    = sheet.getCellAt(i, n).getValueType();
	    cellStr = sheet.getCellAt(i, n).getTextValue();
	    if (m_TextColumns.isInRange(i)) {
	      switch (type) {
		case CURRENCY:
		case FLOAT:
		case PERCENTAGE:
		  data[i] = numericToString(cellStr);
		  break;
		default:
		  data[i] = cellStr;
		  break;
	      }
	      cellTypes[i] = ContentType.STRING;
	      empty        = false;
	    }
	    else if (cellStr.length() > 0) {
	      switch (type) {
		case DATE:
		  data[i]      = spsheet.getDateTimeFormat().parse(cellStr);
		  cellTypes[i] = ContentType.DATETIME;
		  break;
		case TIME:
		  data[i]      = spsheet.getTimeFormat().parse(cellStr);
		  cellTypes[i] = ContentType.TIME;
		  break;
		case CURRENCY:
		case PERCENTAGE:
		case FLOAT:
		  if (Utils.isLong(cellStr)) {
		    data[i]      = Long.parseLong(cellStr);
		    cellTypes[i] = ContentType.LONG;
		  }
		  else {
		    data[i]      = Utils.toDouble(cellStr);
		    cellTypes[i] = ContentType.DOUBLE;
		  }
		  break;
	      }
	      if (data[i] == null) {
		data[i]      = cellStr;
		cellTypes[i] = ContentType.STRING;
	      }
	      empty = false;
	    }
	    if ((data[i] != null) && data[i].equals(m_MissingValue))
	      data[i] = null;
	  }
	  // no more data?
	  if (empty)
	    break;
	  // add row
	  row = spsheet.addRow("" + spsheet.getRowCount());
	  for (i = 0; i < data.length; i++) {
	    if (data[i] == null)
	      row.addCell("" + (i+1)).setContent(SpreadSheet.MISSING_VALUE);
	    else if (data[i] instanceof Double)
	      row.addCell("" + (i+1)).setContent((Double) data[i]);
	    else if (data[i] instanceof Long)
	      row.addCell("" + (i+1)).setContent((Long) data[i]);
	    else
	      row.addCell("" + (i+1)).setContentAsString((String) data[i]);
	  }
	}
      }
    }
    catch (Exception ioe) {
      getLogger().log(Level.SEVERE, "Failed to read range '" + m_SheetRange + "':", ioe);
      result = null;
      m_LastError = "Failed to read range '" + m_SheetRange + "' from stream!\n" + Utils.throwableToString(ioe);
    }

    return result;
  }
}
