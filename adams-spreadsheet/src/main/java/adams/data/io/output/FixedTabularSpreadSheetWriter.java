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
 * FixedTabularSpreadSheetWriter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import java.io.Writer;
import java.util.logging.Level;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.data.DateFormatString;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Outputs the spreadsheet in a simple tabular format with fixed column width, as used by dot matrix printers in days gone by.
 * <p/>
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
 * <pre>-enncoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when writing using a writer, use empty string 
 * &nbsp;&nbsp;&nbsp;for default.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 * <pre>-column-width &lt;int&gt; (property: columnWidth)
 * &nbsp;&nbsp;&nbsp;The width in characters to use for each column.
 * &nbsp;&nbsp;&nbsp;default: 10
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
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 * <pre>-datetime-format &lt;adams.data.DateFormatString&gt; (property: dateTimeFormat)
 * &nbsp;&nbsp;&nbsp;The format for date&#47;times.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd HH:mm:ss
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 * <pre>-time-format &lt;adams.data.DateFormatString&gt; (property: timeFormat)
 * &nbsp;&nbsp;&nbsp;The format for times.
 * &nbsp;&nbsp;&nbsp;default: HH:mm:ss
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 * <pre>-only-float &lt;boolean&gt; (property: onlyFloat)
 * &nbsp;&nbsp;&nbsp;If enabled, all numbers are treated as float rather than distinguishing 
 * &nbsp;&nbsp;&nbsp;between long and double.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedTabularSpreadSheetWriter
  extends AbstractFormattedSpreadSheetWriter {

  /** for serialization. */
  private static final long serialVersionUID = 3420511187768902829L;

  /** the column width. */
  protected int m_ColumnWidth;
  
  /** the new line. */
  protected String m_NewLine;

  /** the format string for the dates. */
  protected DateFormatString m_DateFormat;

  /** the format string for the date/times. */
  protected DateFormatString m_DateTimeFormat;

  /** the format string for the times. */
  protected DateFormatString m_TimeFormat;

  /** whether to treat all numbers as float. */
  protected boolean m_OnlyFloat;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Outputs the spreadsheet in a simple tabular format with fixed column "
	+ "width, as used by dot matrix printers in days gone by.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Fixed tabular format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"txt"};
  }

  /**
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  public SpreadSheetReader getCorrespondingReader() {
    return null;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "enncoding", "encoding",
	    new BaseCharset());

    m_OptionManager.add(
	    "column-width", "columnWidth",
	    10, 1, null);

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
	    "time-format", "timeFormat",
	    new DateFormatString(Constants.TIME_FORMAT));

    m_OptionManager.add(
	    "only-float", "onlyFloat",
	    false);
  }

  /**
   * Sets the column width.
   *
   * @param value	the width in characters
   */
  public void setColumnWidth(int value) {
    m_ColumnWidth = value;
    reset();
  }

  /**
   * Returns the column width.
   *
   * @return		the width in characters
   */
  public int getColumnWidth() {
    return m_ColumnWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnWidthTipText() {
    return "The width in characters to use for each column.";
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
   * Sets whether to treat all numbers as float rather than distinguishing 
   * between long and double.
   *
   * @param value	true if to treat all numbers as float
   */
  public void setOnlyFloat(boolean value) {
    m_OnlyFloat = value;
    reset();
  }

  /**
   * Returns whether to treat all numbers as float rather than distinguishing 
   * between long and double.
   *
   * @return		true if all numbers are treated as float
   */
  public boolean getOnlyFloat() {
    return m_OnlyFloat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onlyFloatTipText() {
    return 
	"If enabled, all numbers are treated as float rather than "
	+ "distinguishing between long and double.";
  }

  /**
   * Returns whether to write to an OutputStream rather than a Writer when
   * using a file name.
   *
   * @return		true if to write to an OutputStream
   */
  @Override
  protected boolean getUseOutputStream() {
    return false;
  }
  
  /**
   * Adds a separator line to the output.
   * 
   * @param writer	the writer to add the line to
   * @throws Exception	if writing to writer fails
   */
  protected void addSeparatorLine(SpreadSheet content, Writer writer) throws Exception {
    StringBuilder	col;
    int			i;
    
    col = new StringBuilder();
    for (i = 0; i < m_ColumnWidth; i++)
      col.append("-");
    
    writer.write("+");
    for (i = 0; i < content.getColumnCount(); i++) {
      writer.write(col.toString());
      writer.write("+");
    }
    writer.write(m_NewLine);
  }
  
  /**
   * Pads a string either on the left or the right hand side with blanks.
   * Shortens it if necessary.
   * 
   * @param s		the string to pad
   * @param leftPad	whether to pad on the left or right
   * @return		the padded string
   */
  protected String pad(String s, boolean leftPad) {
    StringBuilder	result;
    
    result = new StringBuilder(s);

    while (result.length() < m_ColumnWidth) {
      if (leftPad)
	result.insert(0, " ");
      else
	result.append(" ");
    }
      
    if (result.length() > m_ColumnWidth) {
      if (leftPad)
	result.delete(0, result.length() - m_ColumnWidth);
      else
	result.delete(m_ColumnWidth, result.length());
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
    DateFormat	dformat;
    DateFormat	dtformat;
    DateFormat	tformat;
    Cell	cell;
    String	missing;
    Row		row;
    int		i;

    result = true;

    try {
      dformat  = m_DateFormat.toDateFormat();
      dtformat = m_DateTimeFormat.toDateFormat();
      tformat  = m_TimeFormat.toDateFormat();
      missing  = pad(m_MissingValue, false);
      
      // header
      addSeparatorLine(content, writer);
      row = content.getHeaderRow();
      writer.write("|");
      for (i = 0; i < content.getColumnCount(); i++) {
	if (!row.hasCell(i) || row.getCell(i).isMissing())
	  writer.write(missing);
	else
	  writer.write(pad(row.getCell(i).getContent(), false));
	writer.write("|");
      }
      writer.write(m_NewLine);

      // separator
      addSeparatorLine(content, writer);

      // data
      for (Row drow: content.rows()) {
	writer.write("|");
	for (i = 0; i < content.getColumnCount(); i++) {
	  if (!drow.hasCell(i) || drow.getCell(i).isMissing()) {
	    writer.write(missing);
	  }
	  else {
	    cell = drow.getCell(i);
	    switch (cell.getContentType()) {
	      case LONG:
		if (m_OnlyFloat)
		  writer.write(pad(format(drow.getCell(i).toDouble()), true));
		else
		  writer.write(pad(drow.getCell(i).toLong().toString(), true));
		break;
	      case DOUBLE:
		writer.write(pad(format(drow.getCell(i).toDouble()), true));
		break;
	      case DATE:
		writer.write(pad(dformat.format(drow.getCell(i).toDate()), false));
		break;
	      case DATETIME:
		writer.write(pad(dtformat.format(drow.getCell(i).toDateTime()), false));
		break;
	      case TIME:
		writer.write(pad(tformat.format(drow.getCell(i).toTime()), false));
		break;
	      case BOOLEAN:
		writer.write(pad(drow.getCell(i).toBoolean().toString(), false));
		break;
	      default:
		writer.write(pad(drow.getCell(i).getContent(), false));
		break;
	    }
	  }
	  writer.write("|");
	}
	writer.write(m_NewLine);
      }
      
      addSeparatorLine(content, writer);
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed writing spreadsheet data", e);
    }
    
    return result;
  }
}
