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
 * SimpleCsvSpreadSheetReader.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.BasicDateTimeType;
import adams.core.Constants;
import adams.core.Range;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.base.BaseRegExp;
import adams.core.management.LocaleHelper;
import adams.core.management.OptionHandlingLocaleSupporter;
import adams.data.DateFormatString;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;

import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 <!-- globalinfo-start -->
 * Reads CSV files.<br>
 * It is possible to force columns to be text. In that case no intelligent parsing is attempted to determine the type of data a cell has.<br>
 * For very large files, one can turn on chunking, which returns spreadsheet objects till all the data has been read.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-missing &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The placeholder for missing values.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when reading using a reader, leave empty for 
 * &nbsp;&nbsp;&nbsp;default.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 * <pre>-quote-char &lt;java.lang.String&gt; (property: quoteCharacter)
 * &nbsp;&nbsp;&nbsp;The character to use for surrounding text cells.
 * &nbsp;&nbsp;&nbsp;default: \"
 * </pre>
 * 
 * <pre>-separator &lt;java.lang.String&gt; (property: separator)
 * &nbsp;&nbsp;&nbsp;The separator to use for the columns; use '\t' for tab.
 * &nbsp;&nbsp;&nbsp;default: ,
 * </pre>
 * 
 * <pre>-trim &lt;boolean&gt; (property: trim)
 * &nbsp;&nbsp;&nbsp;If enabled, the content of the cells gets trimmed before added.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-text-columns &lt;adams.core.Range&gt; (property: textColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to treat as text.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-datetime-columns &lt;adams.core.Range&gt; (property: dateTimeColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to treat as date&#47;time msec.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-datetime-format &lt;adams.data.DateFormatString&gt; (property: dateTimeFormat)
 * &nbsp;&nbsp;&nbsp;The format for date&#47;time msecs.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd HH:mm:ss
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 * <pre>-datetime-lenient &lt;boolean&gt; (property: dateTimeLenient)
 * &nbsp;&nbsp;&nbsp;Whether date&#47;time msec parsing is lenient or not.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-datetime-type &lt;TIME|TIME_MSEC|DATE|DATE_TIME|DATE_TIME_MSEC&gt; (property: dateTimeType)
 * &nbsp;&nbsp;&nbsp;How to interpret the date&#47;time data.
 * &nbsp;&nbsp;&nbsp;default: DATE_TIME
 * </pre>
 * 
 * <pre>-time-zone &lt;java.util.TimeZone&gt; (property: timeZone)
 * &nbsp;&nbsp;&nbsp;The time zone to use for interpreting dates&#47;times; default is the system-wide 
 * &nbsp;&nbsp;&nbsp;defined one.
 * </pre>
 * 
 * <pre>-locale &lt;java.util.Locale&gt; (property: locale)
 * &nbsp;&nbsp;&nbsp;The locale to use for parsing the numbers.
 * &nbsp;&nbsp;&nbsp;default: Default
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
 * <pre>-chunk-size &lt;int&gt; (property: chunkSize)
 * &nbsp;&nbsp;&nbsp;The maximum number of rows per chunk; using -1 will read put all data into 
 * &nbsp;&nbsp;&nbsp;a single spreadsheet object.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleCsvSpreadSheetReader
  extends AbstractSpreadSheetReaderWithMissingValueSupport
  implements ChunkedSpreadSheetReader, OptionHandlingLocaleSupporter,
             NoHeaderSpreadSheetReader {

  private static final long serialVersionUID = 2446979875221254720L;

  /** the quote character. */
  protected String m_QuoteCharacter;

  /** the column separator. */
  protected String m_Separator;

  /** the columns to treat as text. */
  protected Range m_TextColumns;

  /** the columns to treat as date/time. */
  protected Range m_DateTimeColumns;

  /** the format string for the date/times. */
  protected DateFormatString m_DateTimeFormat;

  /** whether date/time parsing is lenient. */
  protected boolean m_DateTimeLenient;

  /** the type of date/time. */
  protected BasicDateTimeType m_DateTimeType;

  /** the timezone to use. */
  protected TimeZone m_TimeZone;

  /** the locale to use. */
  protected Locale m_Locale;

  /** whether the file has a header or not. */
  protected boolean m_NoHeader;

  /** the comma-separated list of column header names. */
  protected String m_CustomColumnHeaders;

  /** the chunk size to use. */
  protected int m_ChunkSize;

  /** whether to trim the cells. */
  protected boolean m_Trim;

  /** the actual reader. */
  protected CsvSpreadSheetReader m_Reader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Reads CSV files.\n"
        + "It is possible to force columns to be text. In that case no "
        + "intelligent parsing is attempted to determine the type of data a "
        + "cell has.\n"
        + "For very large files, one can turn on chunking, which returns "
        + "spreadsheet objects till all the data has been read.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.removeByProperty("dataRowType");
    m_OptionManager.removeByProperty("spreadSheetType");

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());

    m_OptionManager.add(
      "quote-char", "quoteCharacter",
      "\"");

    m_OptionManager.add(
      "separator", "separator",
      getDefaultSeparator());

    m_OptionManager.add(
      "trim", "trim",
      false);

    m_OptionManager.add(
      "text-columns", "textColumns",
      new Range());

    m_OptionManager.add(
      "datetime-columns", "dateTimeColumns",
      new Range());

    m_OptionManager.add(
      "datetime-format", "dateTimeFormat",
      new DateFormatString(Constants.TIMESTAMP_FORMAT));

    m_OptionManager.add(
      "datetime-lenient", "dateTimeLenient",
      false);

    m_OptionManager.add(
      "datetime-type", "dateTimeType",
      BasicDateTimeType.DATE_TIME);

    m_OptionManager.add(
      "time-zone", "timeZone",
      TimeZone.getDefault(), false);

    m_OptionManager.add(
      "locale", "locale",
      LocaleHelper.getSingleton().getDefault());

    m_OptionManager.add(
      "no-header", "noHeader",
      false);

    m_OptionManager.add(
      "custom-column-headers", "customColumnHeaders",
      "");

    m_OptionManager.add(
      "chunk-size", "chunkSize",
      -1, -1, null);
  }

  /**
   * Returns the default missing value to use.
   *
   * @return		the default
   */
  @Override
  protected BaseRegExp getDefaultMissingValue() {
    return new BaseRegExp("");
  }

  /**
   * Returns the default separator.
   *
   * @return		the default
   */
  protected String getDefaultSeparator() {
    return ",";
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
   * Sets the range of columns to treat as text.
   *
   * @param value	the range
   */
  public void setTextColumns(Range value) {
    m_TextColumns = value;
    reset();
  }

  /**
   * Returns the range of columns to treat as text.
   *
   * @return		the range
   */
  public Range getTextColumns() {
    return m_TextColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String textColumnsTipText() {
    return "The range of columns to treat as text.";
  }

  /**
   * Sets the range of columns to treat as date/time msec.
   *
   * @param value	the range
   */
  public void setDateTimeColumns(Range value) {
    m_DateTimeColumns = value;
    reset();
  }

  /**
   * Returns the range of columns to treat as date/time msec.
   *
   * @return		the range
   */
  public Range getDateTimeColumns() {
    return m_DateTimeColumns;
  }

  /**
   * Returns the tip date for this property.
   *
   * @return 		tip date for this property suitable for
   * 			displaying in the gui
   */
  public String dateTimeColumnsTipText() {
    return "The range of columns to treat as date/time msec.";
  }

  /**
   * Sets the format for date/time msec columns.
   *
   * @param value	the format
   */
  public void setDateTimeFormat(DateFormatString value) {
    m_DateTimeFormat = value;
    reset();
  }

  /**
   * Returns the format for date/time msec columns.
   *
   * @return		the format
   */
  public DateFormatString getDateTimeFormat() {
    return m_DateTimeFormat;
  }

  /**
   * Returns the tip date/time for this property.
   *
   * @return 		tip date for this property suitable for
   * 			displaying in the gui
   */
  public String dateTimeFormatTipText() {
    return "The format for date/time msecs.";
  }

  /**
   * Sets whether parsing of date/time msecs is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  public void setDateTimeLenient(boolean value) {
    m_DateTimeLenient = value;
    reset();
  }

  /**
   * Returns whether the parsing of date/time msecs is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  public boolean isDateTimeLenient() {
    return m_DateTimeLenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String dateTimeLenientTipText() {
    return "Whether date/time msec parsing is lenient or not.";
  }

  /**
   * Sets the type for date/time columns.
   *
   * @param value	the type
   */
  public void setDateTimeType(BasicDateTimeType value) {
    m_DateTimeType = value;
    reset();
  }

  /**
   * Returns the type for date/time columns.
   *
   * @return		the type
   */
  public BasicDateTimeType getDateTimeType() {
    return m_DateTimeType;
  }

  /**
   * Returns the tip date/time for this property.
   *
   * @return 		tip date for this property suitable for
   * 			displaying in the gui
   */
  public String dateTimeTypeTipText() {
    return "How to interpret the date/time data.";
  }

  /**
   * Sets the time zone to use.
   *
   * @param value	the time zone
   */
  public void setTimeZone(TimeZone value) {
    m_TimeZone = value;
    reset();
  }

  /**
   * Returns the time zone in use.
   *
   * @return		the time zone
   */
  public TimeZone getTimeZone() {
    return m_TimeZone;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String timeZoneTipText() {
    return "The time zone to use for interpreting dates/times; default is the system-wide defined one.";
  }

  /**
   * Sets the locale to use.
   *
   * @param value	the locale
   */
  @Override
  public void setLocale(Locale value) {
    m_Locale = value;
    reset();
  }

  /**
   * Returns the locale in use.
   *
   * @return 		the locale
   */
  @Override
  public Locale getLocale() {
    return m_Locale;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String localeTipText() {
    return "The locale to use for parsing the numbers.";
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
    return "Comma-separated values files (simple)";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new CsvSpreadSheetReader().getFormatExtensions();
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
    return new CsvSpreadSheetReader().getInputType();
  }

  /**
   * Returns whether to automatically handle gzip compressed files
   * ({@link InputType#READER}, {@link InputType#STREAM}).
   *
   * @return		true if to automatically decompress
   */
  @Override
  protected boolean supportsCompressedInput() {
    return new CsvSpreadSheetReader().supportsCompressedInput();
  }

  /**
   * Reads the spreadsheet content from the specified file.
   *
   * @param r		the reader to read from
   * @return		the spreadsheet or null in case of an error
   */
  @Override
  protected SpreadSheet doRead(Reader r) {
    m_Reader = new CsvSpreadSheetReader();
    // hard-coded options
    m_Reader.setComment("#");
    m_Reader.setParseFormulas(false);
    m_Reader.setNumRowsColumnTypeDiscovery(100);
    // user-specified options
    m_Reader.setEncoding(getEncoding());
    m_Reader.setMissingValue(getMissingValue());
    m_Reader.setQuoteCharacter(getQuoteCharacter());
    m_Reader.setSeparator(getSeparator());
    m_Reader.setTrim(getTrim());
    m_Reader.setTextColumns(getTextColumns());
    m_Reader.setNoHeader(getNoHeader());
    m_Reader.setCustomColumnHeaders(getCustomColumnHeaders());
    m_Reader.setChunkSize(getChunkSize());
    m_Reader.setTimeZone(getTimeZone());
    m_Reader.setLocale(getLocale());
    // date/time type
    m_Reader.setTimeColumns(new Range());
    m_Reader.setTimeMsecColumns(new Range());
    m_Reader.setDateColumns(new Range());
    m_Reader.setDateTimeColumns(new Range());
    m_Reader.setDateTimeMsecColumns(new Range());
    switch (m_DateTimeType) {
      case TIME:
	m_Reader.setTimeColumns(getDateTimeColumns());
	m_Reader.setTimeFormat(getDateTimeFormat());
	m_Reader.setTimeLenient(isDateTimeLenient());
	break;
      case TIME_MSEC:
	m_Reader.setTimeMsecColumns(getDateTimeColumns());
	m_Reader.setTimeMsecFormat(getDateTimeFormat());
	m_Reader.setTimeMsecLenient(isDateTimeLenient());
	break;
      case DATE:
	m_Reader.setDateColumns(getDateTimeColumns());
	m_Reader.setDateFormat(getDateTimeFormat());
	m_Reader.setDateLenient(isDateTimeLenient());
	break;
      case DATE_TIME:
	m_Reader.setDateTimeColumns(getDateTimeColumns());
	m_Reader.setDateTimeFormat(getDateTimeFormat());
	m_Reader.setDateTimeLenient(isDateTimeLenient());
	break;
      case DATE_TIME_MSEC:
	m_Reader.setDateTimeMsecColumns(getDateTimeColumns());
	m_Reader.setDateTimeMsecFormat(getDateTimeFormat());
	m_Reader.setDateTimeMsecLenient(isDateTimeLenient());
	break;
      default:
	throw new IllegalStateException("Unhandled date/time type: " + m_DateTimeType);
    }
    return m_Reader.read(r);
  }

  /**
   * Checks whether there is more data to read.
   *
   * @return		true if there is more data available
   */
  @Override
  public boolean hasMoreChunks() {
    return m_Reader.hasMoreChunks();
  }

  /**
   * Returns the next chunk.
   *
   * @return		the next chunk, null if no data available
   */
  @Override
  public SpreadSheet nextChunk() {
    return m_Reader.nextChunk();
  }

  /**
   * Returns whether an error was encountered during the last read.
   *
   * @return		true if an error occurred
   */
  @Override
  public boolean hasLastError() {
    return (m_Reader != null) && m_Reader.hasLastError();
  }

  /**
   * Sets the value for the last error that occurred during read.
   *
   * @param value	the error string, null if none occurred
   */
  @Override
  protected void setLastError(String value) {
    super.setLastError(value);
    if (m_Reader != null)
      m_Reader.setLastError(value);
  }

  /**
   * Returns the error that occurred during the last read.
   *
   * @return		the error string, null if none occurred
   */
  @Override
  public String getLastError() {
    if (m_Reader != null)
      return m_Reader.getLastError();
    else
      return null;
  }

  /**
   * Stops the reading (might not be immediate, depending on reader).
   */
  @Override
  public void stopExecution() {
    if (m_Reader != null)
      m_Reader.stopExecution();
    super.stopExecution();
  }

  /**
   * Returns whether the reading was stopped.
   *
   * @return		true if stopped
   */
  @Override
  public boolean isStopped() {
    if (m_Reader != null)
      return m_Reader.isStopped();
    else
      return super.isStopped();
  }
}
