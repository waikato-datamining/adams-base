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
 * Copyright (C) 2019-2024 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Range;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.logging.LoggingHelper;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.env.Environment;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Simplified CSV spreadsheet reader for loading large files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FastCsvSpreadSheetReader
  extends AbstractSpreadSheetReaderWithMissingValueSupport
  implements WindowedSpreadSheetReader, NoHeaderSpreadSheetReader, ChunkedSpreadSheetReader {

  private static final long serialVersionUID = -3348397672538189709L;

  /**
   * Reads CSV files chunk by chunk.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class ChunkReader
    implements Serializable {

    private static final long serialVersionUID = 7291891674724362161L;

    /** the owning reader. */
    protected FastCsvSpreadSheetReader m_Owner;

    /** the reader in use. */
    protected BufferedReader m_Reader;

    /** the header. */
    protected SpreadSheet m_Header;

    /** the missing value. */
    protected BaseRegExp m_MissingValue;

    /** the quote character. */
    protected String m_QuoteCharacter;

    /** the column separator. */
    protected String m_Separator;

    /** the columns to treat as numeric. */
    protected Range m_NumericColumns;

    /** the numeric columns. */
    protected boolean[] m_NumericCols;

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

    /** the chunk size to use. */
    protected int m_ChunkSize;

    /** the rows read so far. */
    protected int m_RowCount;

    /**
     * Initializes the low-level reader.
     *
     * @param owner	the owning reader
     */
    public ChunkReader(FastCsvSpreadSheetReader owner) {
      m_Owner = owner;
    }

    /**
     * Closes the reader.
     */
    protected void close() {
      try {
	m_Reader.close();
	m_Reader = null;
      }
      catch (Exception e) {
	m_Owner.getLogger().log(Level.SEVERE, "Failed to read data!", e);
	m_Owner.setLastError("Failed to read data!\n" + LoggingHelper.throwableToString(e));
      }
    }

    /**
     * Returns whether there is more data to be read.
     *
     * @return		true if more data available
     */
    public boolean hasNext() {
      return (m_Reader != null);
    }

    /**
     * Reads the next chunk.
     *
     * @return		the next chunk
     */
    public SpreadSheet next() {
      SpreadSheet	result;
      Row		row;
      char		sep;
      char		quote;
      String		line;
      String[]		cells;
      List<String> 	hcells;
      String		cell;
      boolean		header;
      int		i;
      int		numCells;
      Pattern 		missing;

      header   = (m_Header == null);
      sep      = (m_Separator.length() == 1 ? m_Separator.charAt(0) : ',');
      quote    = (m_QuoteCharacter.length() == 1 ? m_QuoteCharacter.charAt(0) : '\0');
      missing  = m_MissingValue.patternValue();

      if (m_Header == null) {
	result   = m_Owner.getSpreadSheetType().newInstance();
	numCells = -1;
      }
      else {
	result   = m_Header.getHeader();
	numCells = m_Header.getColumnCount();
      }

      try {
	while (!m_Owner.isStopped()) {
	  line = m_Reader.readLine();
	  if (line == null) {
	    close();
	    break;
	  }

	  if (line.isEmpty())
	    continue;

	  // skip row?
	  if (!header) {
	    m_RowCount++;
	    if (m_RowCount < m_FirstRow)
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
	      if (m_Trim && !cell.isEmpty())
		cell = cell.trim();
	      row.addCell("" + i).setContentAsString(cell);
	    }
	    m_NumericColumns.setMax(numCells);
	    m_NumericCols = new boolean[result.getColumnCount()];
	    for (int index: m_NumericColumns.getIntIndices())
	      m_NumericCols[index] = true;
	    m_Header = result.getHeader();
	  }

	  // add data row
	  if (cells != null) {
	    row = result.addRow();
	    for (i = 0; i < cells.length && i < numCells; i++) {
	      cell = cells[i];
	      if (m_Trim && !cell.isEmpty())
		cell = cell.trim();
	      if (missing.matcher(cell).matches()) {
		if (row.hasCell(i))
		  row.getCell(i).setMissing();
	      }
	      else {
		if (m_NumericCols[i])
		  row.addCell(i).setContentAs(cell, ContentType.DOUBLE);
		else
		  row.addCell(i).setContentAsString(cell);
	      }
	    }
	  }

	  if (m_Owner.isLoggingEnabled() && (m_RowCount % 100 == 0))
	    m_Owner.getLogger().info("Parsed #" + m_RowCount + " lines...");

	  // all lines read?
	  if (m_NumRows > -1) {
	    if (m_RowCount >= m_FirstRow + m_NumRows - 1) {
	      close();
	      break;
	    }
	  }

	  // chunk limit reached?
	  if ((m_ChunkSize > 0) && (result.getRowCount() == m_ChunkSize))
	    break;
	}
      }
      catch (Exception e) {
	result = null;
	m_Owner.getLogger().log(Level.SEVERE, "Failed to read data!", e);
	m_Owner.setLastError("Failed to read data!\n" + LoggingHelper.throwableToString(e));
      }

      return result;
    }

    /**
     * Reads the spreadsheet content from the specified reader.
     *
     * @param r		the reader to read from
     * @return		the spreadsheet or null in case of an error
     */
    public SpreadSheet read(Reader r) {
      m_MissingValue        = m_Owner.getMissingValue();
      m_QuoteCharacter      = m_Owner.getQuoteCharacter();
      m_Separator           = m_Owner.getSeparator();
      m_NumericColumns      = new Range(m_Owner.getNumericColumns().getRange());
      m_Trim                = m_Owner.getTrim();
      m_NoHeader            = m_Owner.getNoHeader();
      m_CustomColumnHeaders = m_Owner.getCustomColumnHeaders();
      m_FirstRow            = m_Owner.getFirstRow();
      m_NumRows             = m_Owner.getNumRows();
      m_ChunkSize           = m_Owner.getChunkSize();
      m_RowCount            = 0;

      if (r instanceof BufferedReader)
	m_Reader = (BufferedReader) r;
      else
	m_Reader = new BufferedReader(r);

      return next();
    }
  }

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

  /** the chunk size to use. */
  protected int m_ChunkSize;

  /** the low-level reader. */
  protected ChunkReader m_Reader;

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

    m_OptionManager.add(
      "chunk-size", "chunkSize",
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
   * Sets the maximum chunk size.
   *
   * @param value	the size of the chunks, &lt; 1 denotes infinity
   */
  @Override
  public void setChunkSize(int value) {
    if (value < 1)
      value = -1;
    m_ChunkSize = value;
    reset();
  }

  /**
   * Returns the current chunk size.
   *
   * @return	the size of the chunks, &lt; 1 denotes infinity
   */
  @Override
  public int getChunkSize() {
    return m_ChunkSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  @Override
  public String chunkSizeTipText() {
    return "The maximum number of rows per chunk; using -1 will read put all data into a single spreadsheet object.";
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
    m_Reader = new ChunkReader(this);
    return m_Reader.read(r);
  }

  /**
   * Checks whether there is more data to read.
   *
   * @return		true if there is more data available
   */
  @Override
  public boolean hasMoreChunks() {
    return (m_Reader != null) && m_Reader.hasNext();
  }

  /**
   * Returns the next chunk.
   *
   * @return		the next chunk, null if no data available
   */
  @Override
  public SpreadSheet nextChunk() {
    if ((m_Reader == null) || !m_Reader.hasNext())
      return null;
    else
      return m_Reader.next();
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
    runReader(Environment.class, FastCsvSpreadSheetReader.class, args);
  }
}
