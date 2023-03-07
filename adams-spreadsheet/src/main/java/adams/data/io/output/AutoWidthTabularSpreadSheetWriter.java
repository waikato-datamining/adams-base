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
 * AutoWidthTabularSpreadSheetWriter.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.data.DateFormatString;
import adams.data.io.input.AutoWidthTabularSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import java.io.Writer;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Outputs the spreadsheet in a simple tabular format with column widths to fit the content in each row.
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
 * <pre>-missing &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The placeholder for missing values.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-locale &lt;java.util.Locale&gt; (property: locale)
 * &nbsp;&nbsp;&nbsp;The locale to use for formatting the numbers.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 *
 * <pre>-number-format &lt;java.lang.String&gt; (property: numberFormat)
 * &nbsp;&nbsp;&nbsp;The format for the numbers (see java.text.DecimalFormat), use empty string
 * &nbsp;&nbsp;&nbsp;for default 'double' output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when writing using a writer, use empty string
 * &nbsp;&nbsp;&nbsp;for default.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 *
 * <pre>-num-spaces &lt;int&gt; (property: numSpaces)
 * &nbsp;&nbsp;&nbsp;The number of spaces to use between columns.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-new-line &lt;java.lang.String&gt; (property: newLine)
 * &nbsp;&nbsp;&nbsp;The newline character(s) to use for the columns; use '\r' for carriage return
 * &nbsp;&nbsp;&nbsp;and '\n' for line feed; Linux&#47;Unix use '\n', Windows uses '\r\n' and old
 * &nbsp;&nbsp;&nbsp;Macs use '\r'.
 * &nbsp;&nbsp;&nbsp;default: \\n
 * </pre>
 *
 * <pre>-date-format &lt;adams.data.DateFormatString&gt; (property: dateFormat)
 * &nbsp;&nbsp;&nbsp;The format for dates.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 *
 * <pre>-datetime-format &lt;adams.data.DateFormatString&gt; (property: dateTimeFormat)
 * &nbsp;&nbsp;&nbsp;The format for date&#47;times.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd HH:mm:ss
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 *
 * <pre>-datetimemsec-format &lt;adams.data.DateFormatString&gt; (property: dateTimeMsecFormat)
 * &nbsp;&nbsp;&nbsp;The format for date&#47;time msecs.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd HH:mm:ss.SSS
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 *
 * <pre>-time-format &lt;adams.data.DateFormatString&gt; (property: timeFormat)
 * &nbsp;&nbsp;&nbsp;The format for times.
 * &nbsp;&nbsp;&nbsp;default: HH:mm:ss
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 *
 * <pre>-no-header &lt;boolean&gt; (property: noHeader)
 * &nbsp;&nbsp;&nbsp;If enabled, no header is output.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class AutoWidthTabularSpreadSheetWriter
  extends AbstractFormattedSpreadSheetWriter
  implements NoHeaderSpreadSheetWriter {

  private static final long serialVersionUID = -3173466155705306551L;

  /** the number of spaces between columns. */
  protected int m_NumSpaces;

  /** the new line. */
  protected String m_NewLine;

  /** the format string for the dates. */
  protected DateFormatString m_DateFormat;

  /** the format string for the date/times. */
  protected DateFormatString m_DateTimeFormat;

  /** the format string for the date/times msec. */
  protected DateFormatString m_DateTimeMsecFormat;

  /** the format string for the times. */
  protected DateFormatString m_TimeFormat;

  /** whether to skip outputting the header. */
  protected boolean m_NoHeader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Outputs the spreadsheet in a simple tabular format with column "
	+ "widths to fit the content in each row.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new AutoWidthTabularSpreadSheetReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new AutoWidthTabularSpreadSheetReader().getFormatExtensions();
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  public SpreadSheetReader getCorrespondingReader() {
    return new AutoWidthTabularSpreadSheetReader();
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
      "num-spaces", "numSpaces",
      1, 1, null);

    m_OptionManager.add(
      "new-line", "newLine",
      Utils.backQuoteChars(System.getProperty("line.separator")));

    m_OptionManager.add(
      "date-format", "dateFormat",
      new DateFormatString(Constants.DATE_FORMAT));

    m_OptionManager.add(
      "datetime-format", "dateTimeFormat",
      new DateFormatString(Constants.TIMESTAMP_FORMAT));

    m_OptionManager.add(
      "datetimemsec-format", "dateTimeMsecFormat",
      new DateFormatString(Constants.TIMESTAMP_FORMAT_MSECS));

    m_OptionManager.add(
      "time-format", "timeFormat",
      new DateFormatString(Constants.TIME_FORMAT));

    m_OptionManager.add(
      "no-header", "noHeader",
      false);
  }

  /**
   * Sets the number of spaces between columns.
   *
   * @param value	the spaces
   */
  public void setNumSpaces(int value) {
    m_NumSpaces = value;
    reset();
  }

  /**
   * Returns the number of spaces between columns.
   *
   * @return		the spaces
   */
  public int getNumSpaces() {
    return m_NumSpaces;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numSpacesTipText() {
    return "The number of spaces to use between columns.";
  }

  /**
   * Sets the string to use as newline.
   *
   * @param value	the character(s)
   */
  public void setNewLine(String value) {
    m_NewLine = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the string used as newline.
   *
   * @return		the character(s)
   */
  public String getNewLine() {
    return Utils.backQuoteChars(m_NewLine);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String newLineTipText() {
    return
      "The newline character(s) to use for the columns; use '\\r' for "
	+ "carriage return and '\\n' for line feed; Linux/Unix use '\\n', "
	+ "Windows uses '\\r\\n' and old Macs use '\\r'.";
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
   * Sets whether to use a header or not.
   *
   * @param value	true if to skip header
   */
  public void setNoHeader(boolean value) {
    m_NoHeader = value;
    reset();
  }

  /**
   * Returns whether to use a header or not.
   *
   * @return		true if no header
   */
  public boolean getNoHeader() {
    return m_NoHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String noHeaderTipText() {
    return "If enabled, no header is output.";
  }

  /**
   * Returns how the data is written.
   *
   * @return		the type
   */
  @Override
  protected OutputType getOutputType() {
    return OutputType.WRITER;
  }

  /**
   * Determines the column widths.
   *
   * @param content	the spreadsheet to use for calculation
   * @return		the widths
   */
  protected int[] determineColWidths(SpreadSheet content) {
    int[] 	result;
    int		i;
    Row		row;
    Cell	cell;
    int		missingLen;
    DateFormat 	dformat;
    DateFormat	dtformat;
    DateFormat	dtmformat;
    DateFormat	tformat;
    String	s;

    result     = new int[content.getColumnCount()];
    missingLen = m_MissingValue.length();

    dformat   = m_DateFormat.toDateFormat();
    dtformat  = m_DateTimeFormat.toDateFormat();
    dtmformat = m_DateTimeMsecFormat.toDateFormat();
    tformat   = m_TimeFormat.toDateFormat();

    // header
    if (!m_NoHeader) {
      row = content.getHeaderRow();
      for (i = 0; i < result.length; i++) {
	if (!row.hasCell(i) || row.getCell(i).isMissing())
	  result[i] = Math.max(result[i], missingLen);
	else
	  result[i] = Math.max(result[i], row.getCell(i).getContent().length());
      }
    }

    // data
    for (Row drow: content.rows()) {
      if (m_Stopped)
	return result;

      for (i = 0; i < content.getColumnCount(); i++) {
	if (!drow.hasCell(i) || drow.getCell(i).isMissing()) {
	  result[i] = Math.max(result[i], missingLen);
	}
	else {
	  cell = drow.getCell(i);
	  switch (cell.getContentType()) {
	    case LONG:
	      s = drow.getCell(i).toLong().toString();
	      break;
	    case DOUBLE:
	      s = format(drow.getCell(i).toDouble());
	      break;
	    case DATE:
	      s = dformat.format(drow.getCell(i).toDate());
	      break;
	    case DATETIME:
	      s = dtformat.format(drow.getCell(i).toDateTime());
	      break;
	    case DATETIMEMSEC:
	      s = dtmformat.format(drow.getCell(i).toDateTimeMsec());
	      break;
	    case TIME:
	      s = tformat.format(drow.getCell(i).toTime());
	      break;
	    case BOOLEAN:
	      s = drow.getCell(i).toBoolean().toString();
	      break;
	    default:
	      s = drow.getCell(i).getContent();
	      break;
	  }
	  result[i] = Math.max(result[i], s.length());
	}
      }
    }

    return result;
  }

  /**
   * Pads a string either on the left or the right hand side with blanks.
   *
   * @param s		the string to pad
   * @param width	the maximum width to pad to
   * @param leftPad	whether to pad on the left or right
   * @return		the padded string
   */
  protected String pad(String s, int width, boolean leftPad) {
    StringBuilder	result;

    result = new StringBuilder(s);

    while (result.length() < width) {
      if (leftPad)
	result.insert(0, " ");
      else
	result.append(" ");
    }

    return result.toString();
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   *
   * @param content	the spreadsheet to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(SpreadSheet content, Writer writer) {
    boolean	result;
    DateFormat 	dformat;
    DateFormat	dtformat;
    DateFormat	dtmformat;
    DateFormat	tformat;
    Cell 	cell;
    String[]	missing;
    Row 	row;
    int		i;
    int[]	widths;
    String	sep;

    result = true;

    // determine column widths
    widths = determineColWidths(content);

    // the separator between columns
    sep = "";
    for (i = 0; i < m_NumSpaces; i++)
      sep += " ";

    try {
      dformat   = m_DateFormat.toDateFormat();
      dtformat  = m_DateTimeFormat.toDateFormat();
      dtmformat = m_DateTimeMsecFormat.toDateFormat();
      tformat   = m_TimeFormat.toDateFormat();
      missing   = new String[content.getColumnCount()];
      for (i = 0; i < content.getColumnCount(); i++)
	missing[i] = pad(m_MissingValue, widths[i], false);

      // header
      if (!m_NoHeader) {
	row = content.getHeaderRow();
	for (i = 0; i < content.getColumnCount(); i++) {
	  if (i > 0)
	    writer.write(sep);
	  if (!row.hasCell(i) || row.getCell(i).isMissing())
	    writer.write(missing[i]);
	  else
	    writer.write(pad(row.getCell(i).getContent(), widths[i], false));
	}
	writer.write(m_NewLine);
      }

      // data
      for (Row drow: content.rows()) {
	if (m_Stopped)
	  return false;

	for (i = 0; i < content.getColumnCount(); i++) {
	  if (i > 0)
	    writer.write(sep);

	  if (!drow.hasCell(i) || drow.getCell(i).isMissing()) {
	    writer.write(missing[i]);
	  }
	  else {
	    cell = drow.getCell(i);
	    switch (cell.getContentType()) {
	      case LONG:
		writer.write(pad(drow.getCell(i).toLong().toString(), widths[i], true));
		break;
	      case DOUBLE:
		writer.write(pad(format(drow.getCell(i).toDouble()), widths[i], true));
		break;
	      case DATE:
		writer.write(pad(dformat.format(drow.getCell(i).toDate()), widths[i], false));
		break;
	      case DATETIME:
		writer.write(pad(dtformat.format(drow.getCell(i).toDateTime()), widths[i], false));
		break;
	      case DATETIMEMSEC:
		writer.write(pad(dtmformat.format(drow.getCell(i).toDateTimeMsec()), widths[i], false));
		break;
	      case TIME:
		writer.write(pad(tformat.format(drow.getCell(i).toTime()), widths[i], false));
		break;
	      case BOOLEAN:
		writer.write(pad(drow.getCell(i).toBoolean().toString(), widths[i], false));
		break;
	      default:
		writer.write(pad(drow.getCell(i).getContent(), widths[i], false));
		break;
	    }
	  }
	}
	writer.write(m_NewLine);
      }
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed writing spreadsheet data", e);
    }

    return result;
  }
}
