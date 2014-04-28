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
 * ExcelSpreadSheetReader.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import adams.core.DateFormat;
import adams.core.DateTime;
import adams.core.DateUtils;
import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Reads MS Excel files (using DOM).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-data-row-type &lt;DENSE|SPARSE&gt; (property: dataRowType)
 * &nbsp;&nbsp;&nbsp;The type of row to use for the data.
 * &nbsp;&nbsp;&nbsp;default: DENSE
 * </pre>
 * 
 * <pre>-sheets &lt;adams.core.Range&gt; (property: sheetRange)
 * &nbsp;&nbsp;&nbsp;The range of sheets to load; A range is a comma-separated list of single 
 * &nbsp;&nbsp;&nbsp;1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts 
 * &nbsp;&nbsp;&nbsp;the range '...'; the following placeholders can be used as well: first, 
 * &nbsp;&nbsp;&nbsp;second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 * <pre>-missing &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The placeholder for missing values.
 * &nbsp;&nbsp;&nbsp;default: ?
 * </pre>
 * 
 * <pre>-no-auto-extend-header (property: autoExtendHeader)
 * &nbsp;&nbsp;&nbsp;If enabled, the header gets automatically extended if rows have more cells 
 * &nbsp;&nbsp;&nbsp;than the header.
 * </pre>
 * 
 * <pre>-text-columns &lt;java.lang.String&gt; (property: textColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to treat as text; A range is a comma-separated list 
 * &nbsp;&nbsp;&nbsp;of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(..
 * &nbsp;&nbsp;&nbsp;.)' inverts the range '...'; the following placeholders can be used as well:
 * &nbsp;&nbsp;&nbsp; first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExcelSpreadSheetReader
  extends AbstractExcelSpreadSheetReader {

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
    Workbook			workbook;
    Sheet 			sheet;
    SpreadSheet			spsheet;
    Row 			exRow;
    Cell 			exCell;
    adams.data.spreadsheet.Row	spRow;
    int 			i;
    int				n;
    int				cellType;
    DateFormat			dformat;
    boolean			numeric;

    result = new ArrayList<SpreadSheet>();

    workbook = null;
    dformat  = DateUtils.getTimestampFormatter();
    try {
      workbook = WorkbookFactory.create(in);
      m_SheetRange.setMax(workbook.getNumberOfSheets());
      indices = m_SheetRange.getIntIndices();
      for (int index: indices) {
	if (m_Stopped)
	  break;
	
	spsheet = m_SpreadSheetType.newInstance();
	spsheet.setDataRowClass(m_DataRowType.getRowClass());
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
	exRow = sheet.getRow(0);
	if (exRow == null) {
	  getLogger().warning("No data in sheet #" + (index + 1) + "?");
	}
	else if (exRow != null) {
	  spRow = spsheet.getHeaderRow();
	  m_TextColumns.setMax(exRow.getLastCellNum());
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
	      case Cell.CELL_TYPE_BLANK:
	      case Cell.CELL_TYPE_ERROR:
		spRow.addCell("" + (i + 1)).setContent("column-" + (i+1));
		break;
	      case Cell.CELL_TYPE_NUMERIC:
		if (HSSFDateUtil.isCellDateFormatted(exCell))
		  spRow.addCell("" + (i + 1)).setContent(new DateTime(HSSFDateUtil.getJavaDate(exCell.getNumericCellValue())));
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

	// data
	if (spsheet.getColumnCount() > 0) {
	  for (i = 1; i <= sheet.getLastRowNum(); i++) {
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
	      if (cellType == Cell.CELL_TYPE_FORMULA)
		cellType = exCell.getCachedFormulaResultType();
	      numeric = !m_TextColumns.isInRange(n);
	      switch (cellType) {
		case Cell.CELL_TYPE_BLANK:
		case Cell.CELL_TYPE_ERROR:
		  if ((m_MissingValue.length() == 0))
		    spRow.addCell(n).setMissing();
		  else
		    spRow.addCell(n).setContent("");
		  break;
		case Cell.CELL_TYPE_NUMERIC:
		  if (HSSFDateUtil.isCellDateFormatted(exCell))
		    spRow.addCell(n).setContent(dformat.format(HSSFDateUtil.getJavaDate(exCell.getNumericCellValue())));
		  else if (numeric)
		    spRow.addCell(n).setContent(exCell.getNumericCellValue());
		  else
		    spRow.addCell(n).setContentAsString(numericToString(exCell));
		  break;
		default:
		  if (exCell.getStringCellValue().equals(m_MissingValue))
		    spRow.addCell(n).setMissing();
		  else
		    spRow.addCell(n).setContentAsString(exCell.getStringCellValue());
	      }
	    }
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
