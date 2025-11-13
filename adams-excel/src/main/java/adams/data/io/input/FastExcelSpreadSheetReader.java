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
 * FastExcelSpreadSheetReader.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.data.io.output.FastExcelSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SheetRange;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.env.Environment;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellType;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FastExcelSpreadSheetReader
  extends AbstractExcelSpreadSheetReader<SheetRange> {

  /** for serialization. */
  private static final long serialVersionUID = 4755872204697328246L;

  /** whether to keep formulas. */
  protected boolean m_KeepFormulas;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads MS Excel files (using fast-excel).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "keep-formulas", "keepFormulas",
      false);
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
    return "MS Excel spreadsheets (fast-excel)";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"xlsx"};
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  public SpreadSheetWriter getCorrespondingWriter() {
    return new FastExcelSpreadSheetWriter();
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
   * Sets whether to keep formulas or just the displayed text.
   *
   * @param value	true if to keep
   */
  public void setKeepFormulas(boolean value) {
    m_KeepFormulas = value;
    reset();
  }

  /**
   * Returns whether to keep formulas or just the displayed text.
   *
   * @return		true if to keep
   */
  public boolean getKeepFormulas() {
    return m_KeepFormulas;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String keepFormulasTipText() {
    return "If enabled, will use the formulas instead of the displayed text.";
  }

  /**
   * Turns a numeric cell into a string. Tries to use "long" representation
   * if possible.
   *
   * @param cell	the cell to process
   * @return		the string representation
   */
  protected String numericToString(Cell cell) {
    double	dbl;
    long	lng;

    dbl = cell.asNumber().doubleValue();
    lng = (long) dbl;
    if (dbl == lng)
      return "" + lng;
    else
      return "" + dbl;
  }

  /**
   * Reads all the cells of the sheet.
   *
   * @param sheet	the sheet to read
   * @return		all the cells in the sheet
   * @throws Exception	if reading fails
   */
  protected List<List<Cell>> readSheet(Sheet sheet) throws Exception {
    final List<List<Cell>> result;

    result = new ArrayList<>();
    try (Stream<Row> rows = sheet.openStream()) {
      rows.forEach(r -> {
	result.add(new ArrayList<>());
	r.forEach(cell -> result.get(result.size() - 1).add(cell));
      });
    }

    return result;
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
    List<Sheet>			sheets;
    String[]			sheetNames;
    ReadableWorkbook 		workbook;
    Sheet			sheet;
    List<List<Cell>>		cells;
    SpreadSheet			spsheet;
    List<Cell> 			exRow;
    Cell 			exCell;
    adams.data.spreadsheet.Row	spRow;
    int 			i;
    int				n;
    CellType 			cellType;
    boolean			numeric;
    int                 	dataRowStart;
    int				firstRow;
    int 			lastRow;
    List<String>        	header;
    String			valueStr;

    result = new ArrayList<>();

    workbook = null;
    try {
      workbook   = new ReadableWorkbook(in);
      sheets     = workbook.getSheets().collect(Collectors.toList());
      sheetNames = new String[sheets.size()];
      for (i = 0; i < sheets.size(); i++)
	sheetNames[i] = sheets.get(i).getName();
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

	sheet = sheets.get(index);
	spsheet.setName(sheet.getName());

	cells = readSheet(sheet);
	if (cells.isEmpty()) {
	  getLogger().severe("No rows in sheet #" + index);
	  return null;
	}

	// header
	if (isLoggingEnabled())
	  getLogger().info("header row");
	exRow = cells.get(firstRow);
	spRow = spsheet.getHeaderRow();
	m_TextColumns.setMax(exRow.size());
	if (getNoHeader()) {
	  header = SpreadSheetUtils.createHeader(exRow.size(), m_CustomColumnHeaders);
	  for (i = 0; i < header.size(); i++)
	    spRow.addCell("" + (i + 1)).setContent(header.get(i));
	}
	else {
	  if (!m_CustomColumnHeaders.trim().isEmpty()) {
	    header = SpreadSheetUtils.createHeader(exRow.size(), m_CustomColumnHeaders);
	    for (i = 0; i < header.size(); i++)
	      spRow.addCell("" + (i + 1)).setContent(header.get(i));
	  }
	  else {
	    for (i = 0; i < exRow.size(); i++) {
	      if (m_Stopped)
		break;
	      exCell = exRow.get(i);
	      if (exCell == null) {
		spRow.addCell("" + (i + 1)).setMissing();
		continue;
	      }
	      numeric = !m_TextColumns.isInRange(i);
	      switch (exCell.getType()) {
		case EMPTY:
		case ERROR:
		  spRow.addCell("" + (i + 1)).setContent("column-" + (i + 1));
		  break;
		case NUMBER:
		  if (numeric)
		    spRow.addCell("" + (i + 1)).setContent(exCell.asNumber().doubleValue());
		  else
		    spRow.addCell("" + (i + 1)).setContentAsString(numericToString(exCell));
		  break;
		default:
		  spRow.addCell("" + (i + 1)).setContentAsString(exCell.getText());
	      }
	    }
	  }
	}

	// data
	if (spsheet.getColumnCount() > 0) {
	  if (m_NumRows < 1)
	    lastRow = cells.size() - 1;
	  else
	    lastRow = Math.min(firstRow + m_NumRows - 1, cells.size() - 1);
	  for (i = dataRowStart; i <= lastRow; i++) {
	    if (m_Stopped)
	      break;
	    if (isLoggingEnabled())
	      getLogger().info("data row: " + (i+1));
	    spRow = spsheet.addRow("" + spsheet.getRowCount());
	    exRow = cells.get(i);
	    if (exRow == null)
	      continue;
	    for (n = 0; n < exRow.size(); n++) {
	      // too few columns in header?
	      if ((n >= spsheet.getHeaderRow().getCellCount()) && m_AutoExtendHeader)
		spsheet.insertColumn(spsheet.getColumnCount(), "");

	      m_TextColumns.setMax(spsheet.getHeaderRow().getCellCount());
	      exCell = exRow.get(n);
	      if (exCell == null) {
		spRow.addCell(n).setMissing();
		continue;
	      }
	      cellType = exCell.getType();
	      numeric = !m_TextColumns.isInRange(n);
	      switch (cellType) {
		case EMPTY:
		  if (m_MissingValue.isEmpty())
		    spRow.addCell(n).setMissing();
		  else
		    spRow.addCell(n).setContent("");
		  break;
		case ERROR:
		  valueStr = "Error: " + exCell.getRawValue();
		  if (m_MissingValue.isMatch(valueStr))
		    spRow.addCell(n).setMissing();
		  else
		    spRow.addCell(n).setContentAsString(valueStr);
		  break;
		case NUMBER:
		  if (numeric)
		    spRow.addCell(n).setContent(exCell.asNumber().doubleValue());
		  else
		    spRow.addCell(n).setContentAsString(numericToString(exCell));
		  break;
		case BOOLEAN:
		  spRow.addCell(n).setContent(exCell.asBoolean());
		  break;
		case FORMULA:
		  if (m_KeepFormulas) {
		    valueStr = exCell.getFormula();
		    if (!valueStr.startsWith("="))
		      valueStr = "=" + valueStr;
		  }
		  else {
		    valueStr = exCell.getText();
		  }
		  if (m_MissingValue.isMatch(valueStr))
		    spRow.addCell(n).setMissing();
		  else
		    spRow.addCell(n).setFormula(valueStr);
		  break;
		case STRING:
		  valueStr = exCell.asString();
		  if (m_MissingValue.isMatch(valueStr))
		    spRow.addCell(n).setMissing();
		  else
		    spRow.addCell(n).setContentAsString(valueStr);
		default:
		  try {
		    valueStr = exCell.asString();
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
    finally {
      FileUtils.closeQuietly(workbook);
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
    runReader(Environment.class, FastExcelSpreadSheetReader.class, args);
  }
}
