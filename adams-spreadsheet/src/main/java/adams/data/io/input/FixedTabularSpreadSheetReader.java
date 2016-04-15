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
 * FixedTabularSpreadSheetReader.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.BasicDateTimeType;
import adams.core.Constants;
import adams.core.Range;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.base.BaseInteger;
import adams.core.management.OptionHandlingLocaleSupporter;
import adams.data.DateFormatString;
import adams.data.io.output.FixedTabularSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import java.io.BufferedReader;
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
 * <pre>-column-width &lt;adams.core.base.BaseInteger&gt; [-column-width ...] (property: columnWidth)
 * &nbsp;&nbsp;&nbsp;The width in characters to use for the columns; if only one is specified 
 * &nbsp;&nbsp;&nbsp;then this is used for all columns.
 * &nbsp;&nbsp;&nbsp;default: 10
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedTabularSpreadSheetReader
  extends AbstractSpreadSheetReaderWithMissingValueSupport
  implements OptionHandlingLocaleSupporter, NoHeaderSpreadSheetReader {

  private static final long serialVersionUID = 2446979875221254720L;

  /** the column width. */
  protected BaseInteger[] m_ColumnWidth;

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

  /** whether to trim the cells. */
  protected boolean m_Trim;

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
      "column-width", "columnWidth",
      new BaseInteger[]{new BaseInteger(10)});

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
      "no-header", "noHeader",
      false);

    m_OptionManager.add(
      "custom-column-headers", "customColumnHeaders",
      "");
  }

  /**
   * Sets the column width.
   *
   * @param value	the width in characters
   */
  public void setColumnWidth(BaseInteger[] value) {
    m_ColumnWidth = value;
    reset();
  }

  /**
   * Returns the column width.
   *
   * @return		the width in characters
   */
  public BaseInteger[] getColumnWidth() {
    return m_ColumnWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnWidthTipText() {
    return "The width in characters to use for the columns; if only one is specified then this is used for all columns.";
  }

  /**
   * Returns the default missing value to use.
   *
   * @return		the default
   */
  @Override
  protected String getDefaultMissingValue() {
    return "";
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
    return new FixedTabularSpreadSheetWriter().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new FixedTabularSpreadSheetWriter().getFormatExtensions();
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  public SpreadSheetWriter getCorrespondingWriter() {
    return new FixedTabularSpreadSheetWriter();
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
    int			i;
    String[]		custom;
    BufferedReader	reader;
    String		line;
    boolean		first;
    String[]		cells;
    int[]		cols;
    ContentType[]	types;
    Cell 		cell;

    result = m_SpreadSheetType.newInstance();
    result.setDataRowClass(getDataRowType().getClass());
    //result.setLocale(m_Locale);
    //result.setTimeZone(m_TimeZone);

    // header
    row = result.getHeaderRow();
    if (m_NoHeader) {
      if (!m_CustomColumnHeaders.isEmpty()) {
	custom = m_CustomColumnHeaders.split(",");
	if (m_ColumnWidth.length != custom.length)
	  throw new IllegalStateException(
	    "Number of Column widths and custom headers differ: "
	      + m_ColumnWidth.length + " != " + custom.length);
	for (i = 0; i < m_ColumnWidth.length; i++)
	  row.addCell("" + i).setContentAsString(custom[i]);
      }
    }
    else {
      // actual headers will get filled in later
      for (i = 0; i < m_ColumnWidth.length; i++)
	row.addCell("" + i);
    }

    // types
    types = new ContentType[m_ColumnWidth.length];
    m_TextColumns.setMax(m_ColumnWidth.length);
    m_DateTimeColumns.setMax(m_ColumnWidth.length);
    for (i = 0; i < m_ColumnWidth.length; i++) {
      types[i] = null;
      if (m_TextColumns.isInRange(i)) {
        types[i] = ContentType.STRING;
      }
      else if (m_DateTimeColumns.isInRange(i)) {
        switch (m_DateTimeType) {
          case TIME:
            types[i] = ContentType.TIME;
            break;
          case TIME_MSEC:
            types[i] = ContentType.TIMEMSEC;
            break;
          case DATE:
            types[i] = ContentType.DATE;
            break;
          case DATE_TIME:
            types[i] = ContentType.DATETIME;
            break;
          case DATE_TIME_MSEC:
            types[i] = ContentType.DATETIMEMSEC;
            break;
          default:
            throw new IllegalStateException("Unhandled date/time type: " + m_DateTimeType);
        }
      }
    }

    // data
    cols = new int[m_ColumnWidth.length + 1];
    for (i = 0; i < m_ColumnWidth.length; i++)
      cols[i+1] = cols[i] + m_ColumnWidth[i].intValue();

    first = true;

    if (r instanceof BufferedReader)
      reader = (BufferedReader) r;
    else
      reader = new BufferedReader(r);

    try {
      cells = new String[m_ColumnWidth.length];
      while ((line = reader.readLine()) != null) {
	// split into cells
	for (i = 0; i < cols.length - 1; i++) {
          cells[i] = line.substring(cols[i], cols[i + 1]);
          if (m_Trim)
            cells[i] = cells[i].trim();
        }

	// header?
	if (!m_NoHeader && first) {
	  first = false;
	  row   = result.getHeaderRow();
	  for (i = 0; i < cells.length; i++)
	    row.getCell(i).setContentAsString(cells[i]);
	}
	else {
	  row = result.addRow();
	  for (i = 0; i < cells.length; i++) {
	    cell = row.addCell(i);
	    if (cells[i].equals(m_MissingValue)) {
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
      }
    }
    catch (Exception e) {
      m_LastError = Utils.handleException(this, "Failed to read data!", e);
      return null;
    }

    return result;
  }
}
