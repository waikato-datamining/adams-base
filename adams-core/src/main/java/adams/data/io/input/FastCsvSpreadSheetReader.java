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
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Range;
import adams.core.Utils;
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
import java.util.regex.Pattern;

/**
 * Simplified CSV spreadsheet reader for loading large files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FastCsvSpreadSheetReader
  extends AbstractSpreadSheetReaderWithMissingValueSupport {

  private static final long serialVersionUID = -3348397672538189709L;

  /** the quote character. */
  protected String m_QuoteCharacter;

  /** the column separator. */
  protected String m_Separator;

  /** the columns to treat as numeric. */
  protected Range m_NumericColumns;

  /** whether to trim the cell content. */
  protected boolean m_Trim;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simplified CSV spreadsheet reader for loading large files.\n"
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
  }

  /**
   * Sets the character used for surrounding text.
   *
   * @param value	the quote character
   */
  public void setQuoteCharacter(String value) {
    if (value.length() == 1) {
      m_QuoteCharacter = value;
      reset();
    }
    else {
      getLogger().severe("Only one character allowed for quote character, provided: " + value);
    }
  }

  /**
   * Returns the string used as separator for the columns, '\t' for tab.
   *
   * @return		the separator
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
    return "The character to use for surrounding text cells.";
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
    char		sep;
    char		quote;
    String		line;
    String[]		cells;
    String		cell;
    boolean		header;
    TIntSet numeric;
    int			i;
    int			numCells;
    Pattern 		missing;

    result = getSpreadSheetType().newInstance();
    if (r instanceof BufferedReader)
      reader = (BufferedReader) r;
    else
      reader = new BufferedReader(r);

    sep      = (m_Separator.length() == 1 ? m_Separator.charAt(0) : ',');
    quote    = (m_QuoteCharacter.length() == 1 ? m_QuoteCharacter.charAt(0) : '"');
    header   = true;
    numCells = -1;
    numeric  = new TIntHashSet();
    missing  = m_MissingValue.patternValue();
    lineNo   = 1;
    try {
      while ((line = reader.readLine()) != null) {
        if (m_Stopped) {
          result = null;
	  break;
	}
        cells = SpreadSheetUtils.split(line, sep, true, quote, false);
        if (header) {
          header   = false;
          row      = result.getHeaderRow();
          numCells = cells.length;
          for (i = 0; i < cells.length; i++) {
            cell = cells[i];
            if (m_Trim && cell.length() > 0)
              cell = cell.trim();
	    row.addCell("" + i).setContentAsString(cell);
	  }
	  m_NumericColumns.setMax(numCells);
	  numeric.addAll(m_NumericColumns.getIntIndices());
	}
	else {
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
	lineNo++;
      }
    }
    catch (Exception e) {
      m_LastError = Utils.handleException(this, "Failed to read CSV data!", e);
    }

    return result;
  }
}
