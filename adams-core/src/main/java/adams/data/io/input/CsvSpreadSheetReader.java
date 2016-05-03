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
 * CsvSpreadSheetReader.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.DateTime;
import adams.core.DateTimeMsec;
import adams.core.Range;
import adams.core.Time;
import adams.core.TimeMsec;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.base.BaseRegExp;
import adams.core.management.LocaleHelper;
import adams.core.management.OptionHandlingLocaleSupporter;
import adams.data.DateFormatString;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import gnu.trove.set.hash.TIntHashSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;

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
 * <pre>-missing &lt;adams.core.base.BaseRegExp&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The placeholder for missing values.
 * &nbsp;&nbsp;&nbsp;default: ^(\\\\?|)$
 * </pre>
 * 
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when reading using a reader, leave empty for 
 * &nbsp;&nbsp;&nbsp;default.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 * <pre>-comment &lt;java.lang.String&gt; (property: comment)
 * &nbsp;&nbsp;&nbsp;The string denoting the start of a line comment (comments can only precede 
 * &nbsp;&nbsp;&nbsp;header row).
 * &nbsp;&nbsp;&nbsp;default: #
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
 * <pre>-date-columns &lt;adams.core.Range&gt; (property: dateColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to treat as date.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-date-format &lt;adams.data.DateFormatString&gt; (property: dateFormat)
 * &nbsp;&nbsp;&nbsp;The format for dates.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 * <pre>-date-lenient &lt;boolean&gt; (property: dateLenient)
 * &nbsp;&nbsp;&nbsp;Whether date parsing is lenient or not.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-datetime-columns &lt;adams.core.Range&gt; (property: dateTimeColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to treat as date&#47;time.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-datetime-format &lt;adams.data.DateFormatString&gt; (property: dateTimeFormat)
 * &nbsp;&nbsp;&nbsp;The format for date&#47;times.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd HH:mm:ss
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 * <pre>-datetime-lenient &lt;boolean&gt; (property: dateTimeLenient)
 * &nbsp;&nbsp;&nbsp;Whether date&#47;time parsing is lenient or not.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-datetimemsec-columns &lt;adams.core.Range&gt; (property: dateTimeMsecColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to treat as date&#47;time msec.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-datetimemsec-format &lt;adams.data.DateFormatString&gt; (property: dateTimeMsecFormat)
 * &nbsp;&nbsp;&nbsp;The format for date&#47;time msecs.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd HH:mm:ss.SSS
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 * <pre>-datetimemsec-lenient &lt;boolean&gt; (property: dateTimeMsecLenient)
 * &nbsp;&nbsp;&nbsp;Whether date&#47;time msec parsing is lenient or not.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-time-columns &lt;adams.core.Range&gt; (property: timeColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to treat as time.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-time-format &lt;adams.data.DateFormatString&gt; (property: timeFormat)
 * &nbsp;&nbsp;&nbsp;The format for times.
 * &nbsp;&nbsp;&nbsp;default: HH:mm:ss
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 * <pre>-time-lenient &lt;boolean&gt; (property: timeLenient)
 * &nbsp;&nbsp;&nbsp;Whether time parsing is lenient or not.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-time-msec-columns &lt;adams.core.Range&gt; (property: timeMsecColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to treat as time&#47;msec.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-time-msec-format &lt;adams.data.DateFormatString&gt; (property: timeMsecFormat)
 * &nbsp;&nbsp;&nbsp;The format for times&#47;msec.
 * &nbsp;&nbsp;&nbsp;default: HH:mm:ss.SSS
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 * <pre>-time-msec-lenient &lt;boolean&gt; (property: timeMsecLenient)
 * &nbsp;&nbsp;&nbsp;Whether time&#47;msec parsing is lenient or not.
 * &nbsp;&nbsp;&nbsp;default: false
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
 * <pre>-num-rows-col-type-discovery &lt;int&gt; (property: numRowsColumnTypeDiscovery)
 * &nbsp;&nbsp;&nbsp;The number of data rows to use for automatically determining the column 
 * &nbsp;&nbsp;&nbsp;(= speed up for large files with consistent cell types); use 0 to turn off 
 * &nbsp;&nbsp;&nbsp;feature.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-chunk-size &lt;int&gt; (property: chunkSize)
 * &nbsp;&nbsp;&nbsp;The maximum number of rows per chunk; using -1 will read put all data into 
 * &nbsp;&nbsp;&nbsp;a single spreadsheet object.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-parse-formulas &lt;boolean&gt; (property: parseFormulas)
 * &nbsp;&nbsp;&nbsp;Whether to try parsing formula-like cells.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CsvSpreadSheetReader
  extends AbstractSpreadSheetReaderWithMissingValueSupport
  implements ChunkedSpreadSheetReader, OptionHandlingLocaleSupporter,
  WindowedSpreadSheetReader, NoHeaderSpreadSheetReader {

  /** for serialization. */
  private static final long serialVersionUID = 4461796269354230002L;

  /**
   * Reads CSV files chunk by chunk.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ChunkReader
    implements Serializable {

    /** for serialization. */
    private static final long serialVersionUID = 26915431591885853L;

    /** the doubled up quotes to replace. */
    public final static String[] DOUBLED_UP_QUOTES = new String[]{"\"\""};

    /** the replacement for doubled up quotes. */
    public final static char[] COLLAPSED_QUOTES = new char[]{'"'};

    /** the owning reader. */
    protected CsvSpreadSheetReader m_Owner;

    /** the reader in use. */
    protected BufferedReader m_Reader;

    /** the header. */
    protected SpreadSheet m_Header;

    /** the missing value. */
    protected BaseRegExp m_MissingValue;

    /** whether any text columns are defined. */
    protected boolean m_HasTextCols;

    /** the text column indices. */
    protected TIntHashSet m_TextCols;

    /** whether any date columns are defined. */
    protected boolean m_HasDateCols;

    /** the date column indices. */
    protected TIntHashSet m_DateCols;

    /** whether any date/time columns are defined. */
    protected boolean m_HasDateTimeCols;

    /** the date/time column indices. */
    protected TIntHashSet m_DateTimeCols;

    /** whether any date/time msec columns are defined. */
    protected boolean m_HasDateTimeMsecCols;

    /** the date/time msec column indices. */
    protected TIntHashSet m_DateTimeMsecCols;

    /** whether any time columns are defined. */
    protected boolean m_HasTimeCols;

    /** whether any time/msec columns are defined. */
    protected boolean m_HasTimeMsecCols;

    /** the time column indices. */
    protected TIntHashSet m_TimeCols;

    /** the time/msec column indices. */
    protected TIntHashSet m_TimeMsecCols;

    /** the date format. */
    protected DateFormat m_DateFormat;

    /** the date/time format. */
    protected DateFormat m_DateTimeFormat;

    /** the date/time msec format. */
    protected DateFormat m_DateTimeMsecFormat;

    /** the time format. */
    protected DateFormat m_TimeFormat;

    /** the time/smec format. */
    protected DateFormat m_TimeMsecFormat;

    /** the number format. */
    protected NumberFormat m_NumberFormat;

    /** the chunk size. */
    protected int m_ChunkSize;

    /** the quote char. */
    protected char m_QuoteChar;

    /** the column separator. */
    protected char m_Separator;

    /** the comment string. */
    protected String m_Comment;

    /** whether to trim the cells. */
    protected boolean m_Trim;

    /** the header cells to use. */
    protected List<String> m_HeaderCells;

    /** the last character that was read too far. */
    protected char m_LastChar;

    /** the rows read so far. */
    protected int m_RowCount;

    /** the first row to retrieve (1-based). */
    protected int m_FirstRow;

    /** the number of rows to retrieve (less than 1 = unlimited). */
    protected int m_NumRows;

    /** the number of rows to use for automatically determining the column types. */
    protected int m_NumRowsAuto;

    /** whether to parse formula-like cells. */
    protected boolean m_ParseFormulas;

    /** the automatically determined column types. */
    protected ContentType[] m_AutoTypes;

    /**
     * Initializes the low-level reader.
     *
     * @param owner	the owning reader
     */
    public ChunkReader(CsvSpreadSheetReader owner) {
      m_Owner = owner;
    }

    /**
     * Unquotes the given string.
     *
     * @param s		the string to unquote, if necessary
     * @return		the processed string
     */
    protected String unquote(String s) {
      String	result;

      result = Utils.unquote(s, "" + m_QuoteChar);
      result = Utils.unbackQuoteChars(
        result,
        DOUBLED_UP_QUOTES,
        COLLAPSED_QUOTES);

      return result;
    }

    /**
     * Removes a trailing CR.
     *
     * @param current	the current buffer
     */
    protected void removeTrailingCR(StringBuilder current) {
      if (current.length() > 0) {
        if (current.charAt(current.length() - 1) == '\r')
          current.delete(current.length() - 1, current.length());
      }
    }

    /**
     * Adds the current string to the cells.
     *
     * @param current	the current string
     * @param cells	the cells to add to
     */
    protected void addCell(StringBuilder current, List<String> cells) {
      removeTrailingCR(current);
      if (m_Trim)
        cells.add(unquote(current.toString().trim()));
      else
        cells.add(unquote(current.toString()));
      if (current.length() > 0)
        current.delete(0, current.length());
    }

    /**
     * Reads a row and breaks it up into cells.
     *
     * @param reader		the reader to read from
     * @return			the cells, null if nothing could be read (EOF)
     * @throws IOException	if reading fails, e.g., due to IO error
     */
    protected List<String> readCells(Reader reader) throws IOException {
      ArrayList<String>	result;
      StringBuilder	current;
      boolean		escaped;
      char		escapeChr;
      char		chr;
      boolean		lineFinished;
      int		in;
      boolean		firstChr;
      boolean		inside;
      boolean		maybeNewLine;

      result       = new ArrayList<String>();
      current      = new StringBuilder();
      escaped      = false;
      escapeChr    = '\0';
      lineFinished = false;
      firstChr     = true;
      maybeNewLine = false;

      do {
        if (m_LastChar != '\0') {
          chr        = m_LastChar;
          m_LastChar = '\0';
        }
        else {
          in = reader.read();
          if (in == -1)
            break;
          firstChr = false;
          chr      = (char) in;
        }

        if (chr == m_Separator) {
          if (escaped) {
            current.append(chr);
          }
          else {
            addCell(current, result);
            escapeChr = '\0';
          }
        }
        else if (chr == m_QuoteChar) {
          if (chr == escapeChr) {
            escaped = !escaped;
          }
          else if (escapeChr == '\0') {
            inside = (current.length() > 0) && (current.charAt(current.length() - 1) != m_Separator);
            if (!inside) {
              escaped   = true;
              escapeChr = chr;
            }
          }
          current.append(chr);
        }
        else if (chr == '\r') {
          if (escaped)
            current.append(chr);
          else
            maybeNewLine = true;
        }
        else if (chr == '\n') {
          if (escaped) {
            removeTrailingCR(current);
            current.append(chr);
          }
          else {
            lineFinished = true;
            maybeNewLine = false;
          }
        }
        else {
          if (maybeNewLine) {
            m_LastChar   = chr;  // keep char for next read
            lineFinished = true;
            escapeChr    = '\0';
          }
          else {
            current.append(chr);
          }
        }
      }
      while (!lineFinished);

      // add last cell
      if (!firstChr)
        addCell(current, result);
      else
        result = null;

      return result;
    }

    /**
     * Reads the next chunk.
     *
     * @return		the next chunk
     */
    public SpreadSheet next() {
      SpreadSheet		result;
      List<String>		cells;
      boolean			isHeader;
      boolean			comments;
      Row			row;
      Cell			cell;
      int			i;
      boolean			canAdd;
      HashSet<ContentType>	types;
      Object			autoObj;
      boolean			autoSuccess;

      if (m_Header == null) {
        result = m_Owner.getSpreadSheetType().newInstance();
        result.setDateLenient(m_Owner.isDateLenient());
        result.setDateTimeLenient(m_Owner.isDateTimeLenient());
        result.setDateTimeMsecLenient(m_Owner.isDateTimeMsecLenient());
        result.setTimeLenient(m_Owner.isTimeLenient());
        result.setTimeMsecLenient(m_Owner.isTimeMsecLenient());
        result.setTimeZone(m_Owner.getTimeZone());
        result.setLocale(m_Owner.getLocale());
        result.setDataRowClass(m_Owner.getDataRowType().getClass());
      }
      else {
        result = m_Header.getHeader();
      }

      try {
        comments = (m_Header == null);

        while (!m_Owner.isStopped()) {
          row   = null;
          cells = readCells(m_Reader);
          if (cells == null) {
            close();
            break;
          }

          // still in comments section?
          if (comments) {
            // we don't count comments
            if (cells.get(0).trim().startsWith(m_Comment)) {
              result.addComment(Utils.flatten(cells, "" + m_Separator).trim().substring(m_Comment.length()).trim());
              continue;
            }
          }

          // actual data
          isHeader = false;
          comments = false;
          if ((cells.size() == 1) && (cells.get(0).trim().length() == 0))
            continue;
          if (m_HeaderCells == null) {
            isHeader = true;
            // create header?
            if (m_Owner.getNoHeader()) {
              m_HeaderCells = SpreadSheetUtils.createHeader(cells.size(), m_Owner.getCustomColumnHeaders());
              row           = result.getHeaderRow();
              for (i = 0; i < m_HeaderCells.size(); i++)
                row.addCell("" + i).setContentAsString(m_HeaderCells.get(i));
              row      = null;
              isHeader = false;
            }
            else {
              // custom header?
              if (m_Owner.getCustomColumnHeaders().trim().length() > 0)
                m_HeaderCells = SpreadSheetUtils.createHeader(cells.size(), m_Owner.getCustomColumnHeaders());
              else
                m_HeaderCells = cells;
              row = result.getHeaderRow();
            }

            m_Owner.getTextColumns().setMax(m_HeaderCells.size());
            m_Owner.getDateTimeColumns().setMax(m_HeaderCells.size());
            m_Owner.getDateTimeMsecColumns().setMax(m_HeaderCells.size());
            m_Owner.getDateColumns().setMax(m_HeaderCells.size());
            m_Owner.getTimeColumns().setMax(m_HeaderCells.size());
            m_Owner.getTimeMsecColumns().setMax(m_HeaderCells.size());

            m_TextCols.addAll(m_Owner.getTextColumns().getIntIndices());
            m_DateTimeCols.addAll(m_Owner.getDateTimeColumns().getIntIndices());
            m_DateTimeMsecCols.addAll(m_Owner.getDateTimeMsecColumns().getIntIndices());
            m_DateCols.addAll(m_Owner.getDateColumns().getIntIndices());
            m_TimeCols.addAll(m_Owner.getTimeColumns().getIntIndices());
            m_TimeMsecCols.addAll(m_Owner.getTimeMsecColumns().getIntIndices());

            m_HasTextCols         = (m_TextCols.size()     > 0);
            m_HasDateTimeCols     = (m_DateTimeCols.size() > 0);
            m_HasDateTimeMsecCols = (m_DateTimeMsecCols.size() > 0);
            m_HasDateCols         = (m_DateCols.size()     > 0);
            m_HasTimeCols         = (m_TimeCols.size()     > 0);
            m_HasTimeMsecCols     = (m_TimeMsecCols.size() > 0);
          }

          // window not yet reached?
          canAdd = true;
          if (!isHeader) {
            m_RowCount++;
            if (m_RowCount < m_FirstRow)
              canAdd = false;
          }

          if (canAdd) {
            if (row == null)
              row = result.addRow();
            for (i = 0; (i < m_HeaderCells.size()) && (i < cells.size()); i++) {
              if (!(m_MissingValue.isMatch(cells.get(i)) || (cells.get(i).isEmpty() && m_MissingValue.isEmpty()))) {
                if (isHeader) {
                  row.addCell("" + i).setContentAsString(cells.get(i));
                }
                else {
                  cell = row.addCell("" + i);
                  if (cell != null) {
                    autoSuccess = false;
                    if ((m_AutoTypes != null) && (m_AutoTypes[i] != null)) {
                      autoObj = cell.parseContent(cells.get(i), m_AutoTypes[i]);
                      if (!autoObj.equals(SpreadSheet.MISSING_VALUE)) {
                        cell.setNative(autoObj);
                        autoSuccess = true;
                      }
                    }
                    if (!autoSuccess) {
                      if (m_HasTextCols && m_TextCols.contains(i))
                        cell.setContentAsString(cells.get(i));
                      else if (m_HasDateTimeMsecCols && m_DateTimeMsecCols.contains(i) && m_DateTimeMsecFormat.check(cells.get(i)))
                        cell.setContent(new DateTimeMsec(m_DateTimeMsecFormat.parse(cells.get(i))));
                      else if (m_HasDateTimeCols && m_DateTimeCols.contains(i) && m_DateTimeFormat.check(cells.get(i)))
                        cell.setContent(new DateTime(m_DateTimeFormat.parse(cells.get(i))));
                      else if (m_HasDateCols && m_DateCols.contains(i) && m_DateFormat.check(cells.get(i)))
                        cell.setContent(m_DateFormat.parse(cells.get(i)));
                      else if (m_HasTimeCols && m_TimeCols.contains(i) && m_TimeFormat.check(cells.get(i)))
                        cell.setContent(new Time(m_TimeFormat.parse(cells.get(i))));
                      else if (m_HasTimeMsecCols && m_TimeMsecCols.contains(i) && m_TimeMsecFormat.check(cells.get(i)))
                        cell.setContent(new TimeMsec(m_TimeMsecFormat.parse(cells.get(i))));
                      else if (!m_ParseFormulas && cells.get(i).startsWith("="))
                        cell.setContentAsString(cells.get(i));
                      else
                        cell.setContent(cells.get(i));
                    }
                  }
                }
              }
            }
          }

          // keep as reference
          if (m_Header == null)
            m_Header = result.getHeader();

          // automatically determining column type?
          if ((m_NumRowsAuto > 0) && (m_RowCount >= m_NumRowsAuto) && (m_AutoTypes == null)) {
            m_AutoTypes = new ContentType[result.getColumnCount()];
            for (i = 0; i < m_AutoTypes.length; i++) {
              types = new HashSet<>(result.getContentTypes(i));
              types.remove(ContentType.MISSING);
              if (types.size() == 1)
                m_AutoTypes[i] = types.iterator().next();
            }
          }

          // end of window reached?
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
        m_Owner.setLastError("Failed to read data!\n" + Utils.throwableToString(e));
      }

      return result;
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
        m_Owner.setLastError("Failed to read data!\n" + Utils.throwableToString(e));
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
     * Reads the spreadsheet content from the specified reader.
     *
     * @param r		the reader to read from
     * @return		the spreadsheet or null in case of an error
     */
    public SpreadSheet read(Reader r) {
      if (r instanceof BufferedReader)
        m_Reader = (BufferedReader) r;
      else
        m_Reader = new BufferedReader(r);

      m_Header              = null;
      m_HeaderCells         = null;
      m_ChunkSize           = m_Owner.getChunkSize();
      m_MissingValue        = m_Owner.getMissingValue();
      m_QuoteChar           = m_Owner.getQuoteCharacter().charAt(0);
      m_Separator           = Utils.unbackQuoteChars(m_Owner.getSeparator()).charAt(0);
      m_Comment             = m_Owner.getComment().trim();
      m_HasTextCols         = false;
      m_TextCols            = new TIntHashSet();
      m_HasDateTimeCols     = false;
      m_DateTimeCols        = new TIntHashSet();
      m_HasDateTimeMsecCols = false;
      m_DateTimeMsecCols    = new TIntHashSet();
      m_HasDateCols         = false;
      m_DateCols            = new TIntHashSet();
      m_HasTimeCols         = false;
      m_TimeCols            = new TIntHashSet();
      m_HasTimeMsecCols     = false;
      m_TimeMsecCols        = new TIntHashSet();
      m_Trim                = m_Owner.getTrim();
      m_LastChar            = '\0';
      m_FirstRow            = m_Owner.getFirstRow();
      m_NumRows             = m_Owner.getNumRows();
      m_RowCount            = 0;
      m_AutoTypes           = null;
      m_NumRowsAuto         = m_Owner.getNumRowsColumnTypeDiscovery();
      m_ParseFormulas       = m_Owner.getParseFormulas();

      m_DateTimeMsecFormat = m_Owner.getDateTimeMsecFormat().toDateFormat();
      m_DateTimeMsecFormat.setLenient(m_Owner.isDateTimeMsecLenient());
      m_DateTimeMsecFormat.setTimeZone(m_Owner.getTimeZone());
      m_DateTimeFormat = m_Owner.getDateTimeFormat().toDateFormat();
      m_DateTimeFormat.setLenient(m_Owner.isDateTimeLenient());
      m_DateTimeFormat.setTimeZone(m_Owner.getTimeZone());
      m_DateFormat = m_Owner.getDateFormat().toDateFormat();
      m_DateFormat.setLenient(m_Owner.isDateLenient());
      m_DateFormat.setTimeZone(m_Owner.getTimeZone());
      m_TimeFormat = m_Owner.getTimeFormat().toDateFormat();
      m_TimeFormat.setLenient(m_Owner.isTimeLenient());
      m_TimeFormat.setTimeZone(m_Owner.getTimeZone());
      m_TimeMsecFormat = m_Owner.getTimeMsecFormat().toDateFormat();
      m_TimeMsecFormat.setLenient(m_Owner.isTimeMsecLenient());
      m_TimeMsecFormat.setTimeZone(m_Owner.getTimeZone());
      m_NumberFormat = LocaleHelper.getSingleton().getNumberFormat(m_Owner.getLocale());

      return next();
    }
  }

  /** the line comment. */
  protected String m_Comment;

  /** the quote character. */
  protected String m_QuoteCharacter;

  /** the column separator. */
  protected String m_Separator;

  /** the columns to treat as text. */
  protected Range m_TextColumns;

  /** the columns to treat as date. */
  protected Range m_DateColumns;

  /** the format string for the dates. */
  protected DateFormatString m_DateFormat;

  /** whether date parsing is lenient. */
  protected boolean m_DateLenient;

  /** the columns to treat as date/time. */
  protected Range m_DateTimeColumns;

  /** the format string for the date/times. */
  protected DateFormatString m_DateTimeFormat;

  /** whether date/time parsing is lenient. */
  protected boolean m_DateTimeLenient;

  /** the columns to treat as date/time msec. */
  protected Range m_DateTimeMsecColumns;

  /** the format string for the date/times. */
  protected DateFormatString m_DateTimeMsecFormat;

  /** whether date/time msec parsing is lenient. */
  protected boolean m_DateTimeMsecLenient;

  /** the columns to treat as time. */
  protected Range m_TimeColumns;

  /** the format string for the times. */
  protected DateFormatString m_TimeFormat;

  /** whether time parsing is lenient. */
  protected boolean m_TimeLenient;

  /** the columns to treat as time/msec. */
  protected Range m_TimeMsecColumns;

  /** the format string for the times/msec. */
  protected DateFormatString m_TimeMsecFormat;

  /** whether time/msec parsing is lenient. */
  protected boolean m_TimeMsecLenient;

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

  /** the first row to retrieve (1-based). */
  protected int m_FirstRow;

  /** the number of rows to retrieve (less than 1 = unlimited). */
  protected int m_NumRows;

  /** the number of rows to use for automatic discovery of column types (0 = off). */
  protected int m_NumRowsColumnTypeDiscovery;

  /** whether to parse formulas. */
  protected boolean m_ParseFormulas;

  /** for reading the actual data. */
  protected ChunkReader m_Reader;

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

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());

    m_OptionManager.add(
      "comment", "comment",
      SpreadSheet.COMMENT);

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
      "text-columns", "textColumns",
      new Range());

    m_OptionManager.add(
      "date-columns", "dateColumns",
      new Range());

    m_OptionManager.add(
      "date-format", "dateFormat",
      new DateFormatString(Constants.DATE_FORMAT));

    m_OptionManager.add(
      "date-lenient", "dateLenient",
      false);

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
      "datetimemsec-columns", "dateTimeMsecColumns",
      new Range());

    m_OptionManager.add(
      "datetimemsec-format", "dateTimeMsecFormat",
      new DateFormatString(Constants.TIMESTAMP_FORMAT_MSECS));

    m_OptionManager.add(
      "datetimemsec-lenient", "dateTimeMsecLenient",
      false);

    m_OptionManager.add(
      "time-columns", "timeColumns",
      new Range());

    m_OptionManager.add(
      "time-format", "timeFormat",
      new DateFormatString(Constants.TIME_FORMAT));

    m_OptionManager.add(
      "time-lenient", "timeLenient",
      false);

    m_OptionManager.add(
      "time-msec-columns", "timeMsecColumns",
      new Range());

    m_OptionManager.add(
      "time-msec-format", "timeMsecFormat",
      new DateFormatString(Constants.TIME_FORMAT_MSECS));

    m_OptionManager.add(
      "time-msec-lenient", "timeMsecLenient",
      false);

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
      "first-row", "firstRow",
      1, 1, null);

    m_OptionManager.add(
      "num-rows", "numRows",
      -1, -1, null);

    m_OptionManager.add(
      "num-rows-col-type-discovery", "numRowsColumnTypeDiscovery",
      0, 0, null);

    m_OptionManager.add(
      "chunk-size", "chunkSize",
      -1, -1, null);

    m_OptionManager.add(
      "parse-formulas", "parseFormulas",
      true);
  }

  /**
   * Returns the default missing value to use.
   *
   * @return		the default
   */
  protected BaseRegExp getDefaultMissingValue() {
    return new BaseRegExp("^(\\" + SpreadSheet.MISSING_VALUE + "|)$");
  }

  /**
   * Sets the string denoting the start of a line comment.
   *
   * @param value	the comment start
   */
  public void setComment(String value) {
    m_Comment = value;
    reset();
  }

  /**
   * Returns the string denoting the start of a line comment.
   *
   * @return		the comment start
   */
  public String getComment() {
    return m_Comment;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commentTipText() {
    return "The string denoting the start of a line comment (comments can only precede header row).";
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
   * Sets the range of columns to treat as date.
   *
   * @param value	the range
   */
  public void setDateColumns(Range value) {
    m_DateColumns = value;
    reset();
  }

  /**
   * Returns the range of columns to treat as date.
   *
   * @return		the range
   */
  public Range getDateColumns() {
    return m_DateColumns;
  }

  /**
   * Returns the tip date for this property.
   *
   * @return 		tip date for this property suitable for
   * 			displaying in the gui
   */
  public String dateColumnsTipText() {
    return "The range of columns to treat as date.";
  }

  /**
   * Sets the format for date columns.
   *
   * @param value	the format
   */
  public void setDateFormat(DateFormatString value) {
    m_DateFormat = value;
    reset();
  }

  /**
   * Returns the format for date columns.
   *
   * @return		the format
   */
  public DateFormatString getDateFormat() {
    return m_DateFormat;
  }

  /**
   * Returns the tip date for this property.
   *
   * @return 		tip date for this property suitable for
   * 			displaying in the gui
   */
  public String dateFormatTipText() {
    return "The format for dates.";
  }

  /**
   * Sets whether parsing of dates is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  public void setDateLenient(boolean value) {
    m_DateLenient = value;
    reset();
  }

  /**
   * Returns whether the parsing of dates is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  public boolean isDateLenient() {
    return m_DateLenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String dateLenientTipText() {
    return "Whether date parsing is lenient or not.";
  }

  /**
   * Sets the range of columns to treat as date/time.
   *
   * @param value	the range
   */
  public void setDateTimeColumns(Range value) {
    m_DateTimeColumns = value;
    reset();
  }

  /**
   * Returns the range of columns to treat as date/time.
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
    return "The range of columns to treat as date/time.";
  }

  /**
   * Sets the format for date/time columns.
   *
   * @param value	the format
   */
  public void setDateTimeFormat(DateFormatString value) {
    m_DateTimeFormat = value;
    reset();
  }

  /**
   * Returns the format for date/time columns.
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
    return "The format for date/times.";
  }

  /**
   * Sets whether parsing of date/times is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  public void setDateTimeLenient(boolean value) {
    m_DateTimeLenient = value;
    reset();
  }

  /**
   * Returns whether the parsing of date/times is lenient or not.
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
    return "Whether date/time parsing is lenient or not.";
  }

  /**
   * Sets the range of columns to treat as date/time msec.
   *
   * @param value	the range
   */
  public void setDateTimeMsecColumns(Range value) {
    m_DateTimeMsecColumns = value;
    reset();
  }

  /**
   * Returns the range of columns to treat as date/time msec.
   *
   * @return		the range
   */
  public Range getDateTimeMsecColumns() {
    return m_DateTimeMsecColumns;
  }

  /**
   * Returns the tip date for this property.
   *
   * @return 		tip date for this property suitable for
   * 			displaying in the gui
   */
  public String dateTimeMsecColumnsTipText() {
    return "The range of columns to treat as date/time msec.";
  }

  /**
   * Sets the format for date/time msec columns.
   *
   * @param value	the format
   */
  public void setDateTimeMsecFormat(DateFormatString value) {
    m_DateTimeMsecFormat = value;
    reset();
  }

  /**
   * Returns the format for date/time msec columns.
   *
   * @return		the format
   */
  public DateFormatString getDateTimeMsecFormat() {
    return m_DateTimeMsecFormat;
  }

  /**
   * Returns the tip date/time for this property.
   *
   * @return 		tip date for this property suitable for
   * 			displaying in the gui
   */
  public String dateTimeMsecFormatTipText() {
    return "The format for date/time msecs.";
  }

  /**
   * Sets whether parsing of date/time msecs is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  public void setDateTimeMsecLenient(boolean value) {
    m_DateTimeMsecLenient = value;
    reset();
  }

  /**
   * Returns whether the parsing of date/time msecs is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  public boolean isDateTimeMsecLenient() {
    return m_DateTimeMsecLenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String dateTimeMsecLenientTipText() {
    return "Whether date/time msec parsing is lenient or not.";
  }

  /**
   * Sets the range of columns to treat as time.
   *
   * @param value	the range
   */
  public void setTimeColumns(Range value) {
    m_TimeColumns = value;
    reset();
  }

  /**
   * Returns the range of columns to treat as time.
   *
   * @return		the range
   */
  public Range getTimeColumns() {
    return m_TimeColumns;
  }

  /**
   * Returns the tip time for this property.
   *
   * @return 		tip time for this property suitable for
   * 			displaying in the gui
   */
  public String timeColumnsTipText() {
    return "The range of columns to treat as time.";
  }

  /**
   * Sets the format for time columns.
   *
   * @param value	the format
   */
  public void setTimeFormat(DateFormatString value) {
    m_TimeFormat = value;
    reset();
  }

  /**
   * Returns the format for time columns.
   *
   * @return		the format
   */
  public DateFormatString getTimeFormat() {
    return m_TimeFormat;
  }

  /**
   * Returns the tip time for this property.
   *
   * @return 		tip time for this property suitable for
   * 			displaying in the gui
   */
  public String timeFormatTipText() {
    return "The format for times.";
  }

  /**
   * Sets whether parsing of times is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  public void setTimeLenient(boolean value) {
    m_TimeLenient = value;
    reset();
  }

  /**
   * Returns whether the parsing of times is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  public boolean isTimeLenient() {
    return m_TimeLenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String timeLenientTipText() {
    return "Whether time parsing is lenient or not.";
  }

  /**
   * Sets the range of columns to treat as time.
   *
   * @param value	the range
   */
  public void setTimeMsecColumns(Range value) {
    m_TimeMsecColumns = value;
    reset();
  }

  /**
   * Returns the range of columns to treat as time/msec.
   *
   * @return		the range
   */
  public Range getTimeMsecColumns() {
    return m_TimeMsecColumns;
  }

  /**
   * Returns the tip time for this property.
   *
   * @return 		tip time for this property suitable for
   * 			displaying in the gui
   */
  public String timeMsecColumnsTipText() {
    return "The range of columns to treat as time/msec.";
  }

  /**
   * Sets the format for time/msec columns.
   *
   * @param value	the format
   */
  public void setTimeMsecFormat(DateFormatString value) {
    m_TimeMsecFormat = value;
    reset();
  }

  /**
   * Returns the format for time/msec columns.
   *
   * @return		the format
   */
  public DateFormatString getTimeMsecFormat() {
    return m_TimeMsecFormat;
  }

  /**
   * Returns the tip time for this property.
   *
   * @return 		tip time for this property suitable for
   * 			displaying in the gui
   */
  public String timeMsecFormatTipText() {
    return "The format for times/msec.";
  }

  /**
   * Sets whether parsing of times/msec is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  public void setTimeMsecLenient(boolean value) {
    m_TimeMsecLenient = value;
    reset();
  }

  /**
   * Returns whether the parsing of times/msec is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  public boolean isTimeMsecLenient() {
    return m_TimeMsecLenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String timeMsecLenientTipText() {
    return "Whether time/msec parsing is lenient or not.";
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
   * Sets the first row to return.
   *
   * @param value	the first row (1-based), greater than 0
   */
  public void setFirstRow(int value) {
    if (value > 0) {
      m_FirstRow = value;
      reset();
    }
    else {
      getLogger().warning("First row must be > 0, provided: " + value);
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
   * Sets the number of data rows to use for automatically determining the
   * column type.
   *
   * @param value	the number of rows, 0 to turn off feature
   */
  public void setNumRowsColumnTypeDiscovery(int value) {
    if (getOptionManager().isValid("numRowsColumnTypeDiscovery", value)) {
      m_NumRowsColumnTypeDiscovery = value;
      reset();
    }
  }

  /**
   * Returns the number of data rows to use for automatically determining the
   * column type.
   *
   * @return		the number of rows, 0 to turn off feature
   */
  public int getNumRowsColumnTypeDiscovery() {
    return m_NumRowsColumnTypeDiscovery;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numRowsColumnTypeDiscoveryTipText() {
    return
      "The number of data rows to use for automatically determining the "
        + "column (= speed up for large files with consistent cell types); "
        + "use 0 to turn off feature.";
  }

  /**
   * Sets whether to parse formula-like cells.
   *
   * @param value	if true then formula-like cells get parsed
   */
  public void setParseFormulas(boolean value) {
    m_ParseFormulas = value;
    reset();
  }

  /**
   * Returns whether to parse formula-like cells.
   *
   * @return		true if to parse formula-like cells
   */
  public boolean getParseFormulas() {
    return m_ParseFormulas;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String parseFormulasTipText() {
    return "Whether to try parsing formula-like cells.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Comma-separated values files";
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
}
