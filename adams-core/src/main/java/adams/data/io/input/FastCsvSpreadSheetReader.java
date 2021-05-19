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
 * FastCsvSpreadSheetReader.java
 * Copyright (C) 2019-2021 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Range;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Simplified CSV spreadsheet reader for loading large files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FastCsvSpreadSheetReader
  extends AbstractSpreadSheetReaderWithMissingValueSupport
  implements WindowedSpreadSheetReader, NoHeaderSpreadSheetReader {

  private static final long serialVersionUID = -3348397672538189709L;

  /** the quote character. */
  protected String m_QuoteCharacter;

  /** the column separator. */
  protected String m_Separator;

  /** the columns to treat as numeric. */
  protected Range m_NumericColumns;

  /** whether to trim the cell content. */
  protected boolean m_Trim;

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
    return "Simplified CSV spreadsheet reader for loading large files.\n"
      + "By default assumes that cells are text, numeric columns have to be explicitly specified.\n"
      + "Assumes English locale for numbers, ie decimal point.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "quote-char", "quoteCharacter",
      "\"");

    m_OptionManager.add(
      "separator", "separator",
      ",");

    m_OptionManager.add(
      "trim", "trim",
      false);

    m_OptionManager.add(
      "numeric-columns", "numericColumns",
      new Range());

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
   * Sets the character used for surrounding text.
   *
   * @param value	the quote character
   */
  public void setQuoteCharacter(String value) {
    if (value.length() <= 1) {
      m_QuoteCharacter = value;
      reset();
    }
    else {
      getLogger().severe("At most one character allowed for quote character, provided: " + value);
    }
  }

  /**
   * Returns the string used for surrounding text.
   *
   * @return		the quote character
   */
  public String getQuoteCharacter() {
    return m_QuoteCharacter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String quoteCharacterTipText() {
    return "The character to use for surrounding text cells; can be empty.";
  }

  /**
   * Sets the string to use as separator for the columns, use '\t' for tab.
   *
   * @param value	the separator
   */
  public void setSeparator(String value) {
    if (Utils.unbackQuoteChars(value).length() == 1) {
      m_Separator = Utils.unbackQuoteChars(value);
      reset();
    }
    else {
      getLogger().severe("Only one character allowed (or two, in case of backquoted ones) for separator, provided: " + value);
    }
  }

  /**
   * Returns the string used as separator for the columns, '\t' for tab.
   *
   * @return		the separator
   */
  public String getSeparator() {
    return Utils.backQuoteChars(m_Separator);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String separatorTipText() {
    return "The separator to use for the columns; use '\\t' for tab.";
  }

  /**
   * Sets the range of columns to treat as numeric.
   *
   * @param value	the range
   */
  public void setNumericColumns(Range value) {
    m_NumericColumns = value;
    reset();
  }

  /**
   * Returns the range of columns to treat as numeric.
   *
   * @return		the range
   */
  public Range getNumericColumns() {
    return m_NumericColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String numericColumnsTipText() {
    return "The range of columns to treat as numeric.";
  }

  /**
   * Sets whether to trim the cell content.
   *
   * @param value	if true the content gets trimmed
   */
  public void setTrim(boolean value) {
    m_Trim = value;
    reset();
  }

  /**
   * Returns whether to trim the cell content.
   *
   * @return	true if to trim content
   */
  public boolean getTrim() {
    return m_Trim;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String trimTipText() {
    return "If enabled, the content of the cells gets trimmed before added.";
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
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Comma-separated values files (fast I/O)";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"csv", "csv.gz"};
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  public SpreadSheetWriter getCorrespondingWriter() {
    return new CsvSpreadSheetWriter();
  }

  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return		how to read the data
   */
  @Override
  protected InputType getInputType() {
    return InputType.READER;
  }

  /**
   * Returns whether to automatically handle gzip compressed files
   * ({@link InputType#READER}, {@link InputType#STREAM}).
   *
   * @return		true if to automatically decompress
   */
  @Override
  protected boolean supportsCompressedInput() {
    return true;
  }

  /**
   * Reads the spreadsheet content from the specified file.
   *
   * @param r		the reader to read from
   * @return		the spreadsheet or null in case of an error
   */
  @Override
  protected SpreadSheet doRead(Reader r) {
    SpreadSheet		result;
    Row			row;
    BufferedReader	reader;
    int			lineNo;
    int			lineRead;
    char		sep;
    char		quote;
    String		line;
    String[]		cells;
    List<String> 	hcells;
    String		cell;
    boolean		header;
    TIntSet 		numeric;
    int			i;
    int			numCells;
    Pattern 		missing;

    result = getSpreadSheetType().newInstance();
    if (r instanceof BufferedReader)
      reader = (BufferedReader) r;
    else
      reader = new BufferedReader(r);

    sep      = (m_Separator.length() == 1 ? m_Separator.charAt(0) : ',');
    quote    = (m_QuoteCharacter.length() == 1 ? m_QuoteCharacter.charAt(0) : '\0');
    header   = true;
    numCells = -1;
    numeric  = new TIntHashSet();
    missing  = m_MissingValue.patternValue();
    lineNo   = 1;
    lineRead = 0;
    try {
      while ((line = reader.readLine()) != null) {
        if (m_Stopped) {
          result = null;
	  break;
	}

        if (line.isEmpty())
          continue;

	// skip row?
	if (lineNo < m_FirstRow) {
          lineNo++;
          continue;
        }

        // parse cells
        cells = SpreadSheetUtils.split(line, sep, true, quote, false);
        if (header) {
          header   = false;
          row      = result.getHeaderRow();
          numCells = cells.length;
          if (m_NoHeader) {
            hcells = SpreadSheetUtils.createHeader(numCells, m_CustomColumnHeaders);
          }
          else {
            if (m_CustomColumnHeaders.isEmpty())
	      hcells = new ArrayList<>(Arrays.asList(cells));
            else
	      hcells = SpreadSheetUtils.createHeader(numCells, m_CustomColumnHeaders);
            cells = null;
          }
	  for (i = 0; i < hcells.size(); i++) {
	    cell = hcells.get(i);
	    if (m_Trim && cell.length() > 0)
	      cell = cell.trim();
	    row.addCell("" + i).setContentAsString(cell);
	  }
	  m_NumericColumns.setMax(numCells);
	  numeric.addAll(m_NumericColumns.getIntIndices());
	}

	// add data row
        if (cells != null) {
          row = result.addRow();
          for (i = 0; i < cells.length && i < numCells; i++) {
            cell = cells[i];
            if (m_Trim && (cell.length() > 0))
              cell = cell.trim();
            if (missing.matcher(cell).matches()) {
              if (row.hasCell(i))
                row.getCell(i).setMissing();
            }
            else {
              if ((numeric.size() > 0) && (numeric.contains(i)))
                row.addCell(i).setContentAs(cell, ContentType.DOUBLE);
              else
                row.addCell(i).setContentAsString(cell);
            }
          }
        }

	if (isLoggingEnabled() && (lineNo % 100 == 0))
	  getLogger().info("Parsed #" + lineNo + " lines...");

        // all lines read?
        if ((m_NumRows >= 0) && (lineRead >= m_NumRows))
          break;

	lineNo++;
	lineRead++;
      }
    }
    catch (Exception e) {
      m_LastError = LoggingHelper.handleException(this, "Failed to read CSV data!", e);
    }

    return result;
  }
}
