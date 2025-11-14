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
 * AutoWidthTabularSpreadSheetReader.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.core.BasicDateTimeType;
import adams.core.Constants;
import adams.core.Range;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.base.BaseRegExp;
import adams.core.logging.LoggingHelper;
import adams.core.management.OptionHandlingLocaleSupporter;
import adams.data.DateFormatString;
import adams.data.io.output.AutoWidthTabularSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.io.BufferedReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 <!-- globalinfo-start -->
 * Reads simple tabular text files, using column widths as defined by the header row.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
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
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when reading using a reader, leave empty for
 * &nbsp;&nbsp;&nbsp;default.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 *
 * <pre>-min-spaces &lt;int&gt; (property: minSpaces)
 * &nbsp;&nbsp;&nbsp;The minimum number of spaces between columns.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-trim &lt;boolean&gt; (property: trim)
 * &nbsp;&nbsp;&nbsp;If enabled, the content of the cells gets trimmed before added.
 * &nbsp;&nbsp;&nbsp;default: true
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
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
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
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class AutoWidthTabularSpreadSheetReader
  extends AbstractSpreadSheetReaderWithMissingValueSupport
  implements OptionHandlingLocaleSupporter {

  private static final long serialVersionUID = 7625761549460086503L;

  /** the minimum number of spaces between columns. */
  protected int m_MinSpaces;

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

  /** whether to trim the cells. */
  protected boolean m_Trim;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads simple tabular text files, using column widths as defined by the header row.";
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
      "min-spaces", "minSpaces",
      1, 1, null);

    m_OptionManager.add(
      "trim", "trim",
      true);

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
   * Sets the minimum number of spaces between columns.
   *
   * @param value	the minimum
   */
  public void setMinSpaces(int value) {
    m_MinSpaces = value;
    reset();
  }

  /**
   * Returns the minimum number of spaces between columns.
   *
   * @return		the minimum
   */
  public int getMinSpaces() {
    return m_MinSpaces;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minSpacesTipText() {
    return "The minimum number of spaces between columns.";
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
    return "Auto-width tabular format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"txt", "txt.gz"};
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  public SpreadSheetWriter getCorrespondingWriter() {
    return new AutoWidthTabularSpreadSheetWriter();
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
   * Determines where the columns start based on the header.
   *
   * @param header	the header row
   * @param minSpaces 	the minimum number of spaces to use
   * @return		the start positions
   */
  protected int[] determineColStarts(String header, int minSpaces) {
    TIntList 	starts;
    TIntList	ends;
    int		i;
    boolean	isHeader;

    starts = new TIntArrayList();

    if (header.length() > 0) {
      ends     = new TIntArrayList();
      isHeader = header.charAt(0) != ' ';
      if (isHeader)
        starts.add(0);
      for (i = 1; i < header.length(); i++) {
        if (header.charAt(i) == ' ') {
          if (isHeader) {
            ends.add(i - 1);
            isHeader = false;
          }
        }
        else {
          if (!isHeader) {
            starts.add(i);
            isHeader = true;
          }
        }
      }

      // check for minimum spaces?
      if (minSpaces > 1) {
        i = 1;
        while (i < starts.size()) {
          if (starts.get(i) - ends.get(i - 1) - 1 < minSpaces) {
            starts.removeAt(i);
            ends.removeAt(i - 1);
          }
          else {
            i++;
          }
        }
      }
    }

    return starts.toArray();
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
    Cell		cell;
    BufferedReader 	reader;
    String		line;
    boolean		first;
    int[] 		colStarts;
    String[]		cells;
    int			i;
    int			start;
    int			end;
    Cell.ContentType[]	types;

    if (r instanceof BufferedReader)
      reader = (BufferedReader) r;
    else
      reader = new BufferedReader(r);

    result = getSpreadSheetType().newInstance();

    first     = true;
    colStarts = new int[0];
    cells     = new String[0];
    types     = new Cell.ContentType[0];
    try {
      while ((line = reader.readLine()) != null) {
        // determine column widths
        if (first) {
          colStarts = determineColStarts(line, m_MinSpaces);
          cells     = new String[colStarts.length];
          if (isLoggingEnabled())
            getLogger().info("Column starts: " + Utils.arrayToString(colStarts));

          // types
          types = new Cell.ContentType[colStarts.length];
          m_TextColumns.setMax(colStarts.length);
          m_DateTimeColumns.setMax(colStarts.length);
          for (i = 0; i < colStarts.length; i++) {
            types[i] = null;
            if (m_TextColumns.isInRange(i)) {
              types[i] = Cell.ContentType.STRING;
            }
            else if (m_DateTimeColumns.isInRange(i)) {
              switch (m_DateTimeType) {
                case TIME:
                  types[i] = Cell.ContentType.TIME;
                  break;
                case TIME_MSEC:
                  types[i] = Cell.ContentType.TIMEMSEC;
                  break;
                case DATE:
                  types[i] = Cell.ContentType.DATE;
                  break;
                case DATE_TIME:
                  types[i] = Cell.ContentType.DATETIME;
                  break;
                case DATE_TIME_MSEC:
                  types[i] = Cell.ContentType.DATETIMEMSEC;
                  break;
                default:
                  throw new IllegalStateException("Unhandled date/time type: " + m_DateTimeType);
              }
            }
          }
        }

        // parse line
        for (i = 0; i < colStarts.length; i++) {
          start = colStarts[i];
          if (i == colStarts.length - 1)
            end = line.length();
          else
            end = colStarts[i + 1];
          if (end >= line.length())
            end = line.length();
          if (start >= line.length())
            cells[i] = "";
          else
            cells[i] = line.substring(start, end);
          if (m_Trim)
            cells[i] = cells[i].trim();
        }

        // add row
        if (first) {
          row = result.getHeaderRow();
          for (i = 0; i < cells.length; i++)
            row.addCell("" + i).setContentAsString(cells[i]);
        }
        else {
          row = result.addRow();
          for (i = 0; i < cells.length; i++) {
            cell = row.addCell("" + i);
            if (m_MissingValue.isMatch(cells[i]) || (cells[i].isEmpty() && m_MissingValue.isEmpty())) {
              cell.setMissing();
            }
            else if (types[i] == null) {
              cell.setContent(cells[i]);
            }
            else {
              switch (types[i]) {
                case STRING:
                  cell.setContentAsString(cells[i]);
                  break;
                case TIME:
                case TIMEMSEC:
                case DATE:
                case DATETIME:
                case DATETIMEMSEC:
                  cell.setNative(cell.parseContent(cells[i], types[i]));
                  break;
                default:
                  throw new IllegalStateException("Unhandled cell type: " + types[i]);
              }
            }
          }
        }

        first = false;
      }
    }
    catch (Exception e) {
      m_LastError = LoggingHelper.handleException(this, "Failed to read data!", e);
      return null;
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
    runReader(Environment.class, AutoWidthTabularSpreadSheetReader.class, args);
  }
}
