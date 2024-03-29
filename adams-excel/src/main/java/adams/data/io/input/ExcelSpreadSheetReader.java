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
 * ExcelSpreadSheetReader.java
 * Copyright (C) 2010-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.DateFormat;
import adams.core.DateTime;
import adams.core.DateUtils;
import adams.core.logging.LoggingHelper;
import adams.data.io.output.ExcelSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SheetRange;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.env.Environment;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Reads MS Excel files (using DOM).
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
 * <pre>-no-auto-extend-header &lt;boolean&gt; (property: autoExtendHeader)
 * &nbsp;&nbsp;&nbsp;If enabled, the header gets automatically extended if rows have more cells 
 * &nbsp;&nbsp;&nbsp;than the header.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-text-columns &lt;adams.core.Range&gt; (property: textColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to treat as text.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
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
 */
public class ExcelSpreadSheetReader
  extends AbstractExcelSpreadSheetReader<SheetRange> {

  /** for serialization. */
  private static final long serialVersionUID = 4755872204697328246L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads MS Excel files (using DOM).";
  }

  /**
   * Returns the default sheet range.
   *
   * @return the default
   */
  @Override
  protected SheetRange getDefaultSheetRange() {
    return new SheetRange(SheetRange.FIRST);
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "MS Excel spreadsheets";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"xls", "xlsx"};
  }

  /**
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  public SpreadSheetWriter getCorrespondingWriter() {
    return new ExcelSpreadSheetWriter();
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
   * @param cell	the cell to process
   * @return		the string representation
   */
  protected String numericToString(Cell cell) {
    Double	dbl;
    long	lng;

    dbl = cell.getNumericCellValue();
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
    List<SpreadSheet>		result;
    int[]			indices;
    String[]			sheetNames;
    Workbook			workbook;
    Sheet 			sheet;
    SpreadSheet			spsheet;
    Row 			exRow;
    Cell 			exCell;
    adams.data.spreadsheet.Row	spRow;
    int 			i;
    int				n;
    CellType 			cellType;
    DateFormat			dformat;
    boolean			numeric;
    int                 	dataRowStart;
    int				firstRow;
    int 			lastRow;
    List<String>        	header;
    String			valueStr;

    result = new ArrayList<>();

    workbook = null;
    dformat  = DateUtils.getTimestampFormatter();
    try {
      workbook = WorkbookFactory.create(in);
      sheetNames = new String[workbook.getNumberOfSheets()];
      for (i = 0; i < workbook.getNumberOfSheets(); i++)
        sheetNames[i] = workbook.getSheetName(i);
      m_SheetRange.setSheetNames(sheetNames);
      indices      = m_SheetRange.getIntIndices();
      firstRow     = m_FirstRow - 1;
      dataRowStart = getNoHeader() ? firstRow : firstRow + 1;
      for (int index: indices) {
	if (m_Stopped)
	  break;
	
	spsheet = m_SpreadSheetType.newInstance();
	spsheet.setDataRowClass(m_DataRowType.getClass());
	result.add(spsheet);

	if (isLoggingEnabled())
	  getLogger().info("sheet: " + (index+1));
	
	sheet = workbook.getSheetAt(index);
	if (sheet.getLastRowNum() == 0) {
	  getLogger().severe("No rows in sheet #" + index);
	  return null;
	}
	spsheet.setName(sheet.getSheetName());
	
	// header
	if (isLoggingEnabled())
	  getLogger().info("header row");
	exRow = sheet.getRow(firstRow);
	if (exRow == null) {
	  getLogger().warning("No data in sheet #" + (index + 1) + "?");
	}
	else if (exRow != null) {
	  spRow = spsheet.getHeaderRow();
	  m_TextColumns.setMax(exRow.getLastCellNum());
	  if (getNoHeader()) {
	    header = SpreadSheetUtils.createHeader(exRow.getLastCellNum(), m_CustomColumnHeaders);
	    for (i = 0; i < header.size(); i++)
	      spRow.addCell("" + (i + 1)).setContent(header.get(i));
	  }
	  else {
	    if (!m_CustomColumnHeaders.trim().isEmpty()) {
	      header = SpreadSheetUtils.createHeader(exRow.getLastCellNum(), m_CustomColumnHeaders);
	      for (i = 0; i < header.size(); i++)
		spRow.addCell("" + (i + 1)).setContent(header.get(i));
	    }
	    else {
	      for (i = 0; i < exRow.getLastCellNum(); i++) {
		if (m_Stopped)
		  break;
		exCell = exRow.getCell(i);
		if (exCell == null) {
		  spRow.addCell("" + (i + 1)).setMissing();
		  continue;
		}
		numeric = !m_TextColumns.isInRange(i);
		switch (exCell.getCellType()) {
		  case BLANK:
		  case ERROR:
		    spRow.addCell("" + (i + 1)).setContent("column-" + (i + 1));
		    break;
		  case NUMERIC:
		    if (DateUtil.isCellDateFormatted(exCell))
		      spRow.addCell("" + (i + 1)).setContent(new DateTime(DateUtil.getJavaDate(exCell.getNumericCellValue())));
		    else if (numeric)
		      spRow.addCell("" + (i + 1)).setContent(exCell.getNumericCellValue());
		    else
		      spRow.addCell("" + (i + 1)).setContentAsString(numericToString(exCell));
		    break;
		  default:
		    spRow.addCell("" + (i + 1)).setContentAsString(exCell.getStringCellValue());
		}
	      }
	    }
	  }
	}

	// data
	if (spsheet.getColumnCount() > 0) {
	  if (m_NumRows < 1)
	    lastRow = sheet.getLastRowNum();
	  else
	    lastRow = Math.min(firstRow + m_NumRows - 1, sheet.getLastRowNum());
	  for (i = dataRowStart; i <= lastRow; i++) {
	    if (m_Stopped)
	      break;
	    if (isLoggingEnabled())
	      getLogger().info("data row: " + (i+1));
	    spRow = spsheet.addRow("" + spsheet.getRowCount());
	    exRow = sheet.getRow(i);
	    if (exRow == null)
	      continue;
	    for (n = 0; n < exRow.getLastCellNum(); n++) {
	      // too few columns in header?
	      if ((n >= spsheet.getHeaderRow().getCellCount()) && m_AutoExtendHeader)
		spsheet.insertColumn(spsheet.getColumnCount(), "");

	      m_TextColumns.setMax(spsheet.getHeaderRow().getCellCount());
	      exCell = exRow.getCell(n);
	      if (exCell == null) {
		spRow.addCell(n).setMissing();
		continue;
	      }
	      cellType = exCell.getCellType();
	      if (cellType == CellType.FORMULA)
		cellType = exCell.getCachedFormulaResultType();
	      numeric = !m_TextColumns.isInRange(n);
	      switch (cellType) {
		case BLANK:
		  if (m_MissingValue.isEmpty())
		    spRow.addCell(n).setMissing();
		  else
		    spRow.addCell(n).setContent("");
		  break;
		case ERROR:
		  if (exCell instanceof XSSFCell)
		    valueStr = ((XSSFCell) exCell).getErrorCellString();
		  else
		    valueStr = "Error: " + exCell.getErrorCellValue();
		  if (m_MissingValue.isMatch(valueStr))
		    spRow.addCell(n).setMissing();
		  else
		    spRow.addCell(n).setContentAsString(valueStr);
		  break;
		case NUMERIC:
		  if (DateUtil.isCellDateFormatted(exCell))
		    spRow.addCell(n).setContent(dformat.format(DateUtil.getJavaDate(exCell.getNumericCellValue())));
		  else if (numeric)
		    spRow.addCell(n).setContent(exCell.getNumericCellValue());
		  else
		    spRow.addCell(n).setContentAsString(numericToString(exCell));
		  break;
		case BOOLEAN:
		  spRow.addCell(n).setContent(exCell.getBooleanCellValue());
		  break;
		case FORMULA:
		  valueStr = exCell.getCellFormula();
		  if (m_MissingValue.isMatch(valueStr))
		    spRow.addCell(n).setMissing();
		  else
		    spRow.addCell(n).setFormula(valueStr);
		  break;
		case STRING:
		  valueStr = exCell.getStringCellValue();
		  if (m_MissingValue.isMatch(valueStr))
		    spRow.addCell(n).setMissing();
		  else
		    spRow.addCell(n).setContentAsString(valueStr);
		default:
		  try {
		    valueStr = exCell.getStringCellValue();
		    if (m_MissingValue.isMatch(valueStr))
		      spRow.addCell(n).setMissing();
		    else
		      spRow.addCell(n).setContentAsString(valueStr);
		  }
		  catch (Exception e) {
		    spRow.addCell(n).setMissing();
		    // ignored
		  }
	      }
	    }
	  }
	}
      }
    }
    catch (Exception ioe) {
      getLogger().log(Level.SEVERE, "Failed to read range '" + m_SheetRange + "':", ioe);
      result = null;
      m_LastError = "Failed to read range '" + m_SheetRange + "' from stream!\n" + LoggingHelper.throwableToString(ioe);
    }

    return result;
  }

  /**
   * Runs the reader from the command-line.
   *
   * Use the option {@link #OPTION_INPUT} to specify the input file.
   * If the option {@link #OPTION_OUTPUT} is specified then the read sheet
   * gets output as .csv files in that directory.
   *
   * @param args	the command-line options to use
   */
  public static void main(String[] args) {
    runReader(Environment.class, ExcelSpreadSheetReader.class, args);
  }
}
