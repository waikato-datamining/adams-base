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
 * TsvSpreadSheetWriter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.data.io.input.SpreadSheetReader;
import adams.data.io.input.TsvSpreadSheetReader;

/**
 <!-- globalinfo-start -->
 * Writes TSV (tab-separated values) files,
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
 * <pre>-comment &lt;java.lang.String&gt; (property: comment)
 * &nbsp;&nbsp;&nbsp;The string denoting the start of a line comment (comments can only precede 
 * &nbsp;&nbsp;&nbsp;header row).
 * &nbsp;&nbsp;&nbsp;default: #
 * </pre>
 * 
 * <pre>-output-comments &lt;boolean&gt; (property: outputComments)
 * &nbsp;&nbsp;&nbsp;If enabled, any available comments are output before the actual data (using 
 * &nbsp;&nbsp;&nbsp;the 'comment' prefix).
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-output-as-displayed &lt;boolean&gt; (property: outputAsDisplayed)
 * &nbsp;&nbsp;&nbsp;If enabled, cells are output as displayed, ie, results of formulas instead 
 * &nbsp;&nbsp;&nbsp;of the formulas.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-no-header &lt;boolean&gt; (property: noHeader)
 * &nbsp;&nbsp;&nbsp;If enabled, no header is output.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-check-file-exists &lt;boolean&gt; (property: checkFileExists)
 * &nbsp;&nbsp;&nbsp;If enabled, it is checked each time whether the file exists; expensive test 
 * &nbsp;&nbsp;&nbsp;if processing only one row at a time.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-appending &lt;boolean&gt; (property: appending)
 * &nbsp;&nbsp;&nbsp;If enabled, multiple spreadsheets with the same structure can be written 
 * &nbsp;&nbsp;&nbsp;to the same file.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-keep-existing &lt;boolean&gt; (property: keepExisting)
 * &nbsp;&nbsp;&nbsp;If enabled, any output file that exists when the writer is executed for 
 * &nbsp;&nbsp;&nbsp;the first time won't get replaced with the current header; useful when outputting 
 * &nbsp;&nbsp;&nbsp;data in multiple locations in the flow, but one needs to be cautious as 
 * &nbsp;&nbsp;&nbsp;to not stored mixed content (eg varying number of columns, etc).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-quote-char &lt;java.lang.String&gt; (property: quoteCharacter)
 * &nbsp;&nbsp;&nbsp;The character to use for surrounding text cells.
 * &nbsp;&nbsp;&nbsp;default: \"
 * </pre>
 * 
 * <pre>-separator &lt;java.lang.String&gt; (property: separator)
 * &nbsp;&nbsp;&nbsp;The separator to use for the columns; use '\t' for tab.
 * &nbsp;&nbsp;&nbsp;default: \\t
 * </pre>
 * 
 * <pre>-new-line &lt;java.lang.String&gt; (property: newLine)
 * &nbsp;&nbsp;&nbsp;The newline character(s) to use for the columns; use '\r' for carriage return 
 * &nbsp;&nbsp;&nbsp;and '\n' for line feed; Linux&#47;Unix use '\n', Windows uses '\r\n' and old 
 * &nbsp;&nbsp;&nbsp;Macs use '\r'.
 * &nbsp;&nbsp;&nbsp;default: \\n
 * </pre>
 * 
 * <pre>-always-quote-text &lt;boolean&gt; (property: alwaysQuoteText)
 * &nbsp;&nbsp;&nbsp;If enabled, text&#47;formula cells always get surrounded by double quotes.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-escape-special-chars &lt;boolean&gt; (property: escapeSpecialChars)
 * &nbsp;&nbsp;&nbsp;If enabled, special characters get escaped with a backslash: \\, \', \t, 
 * &nbsp;&nbsp;&nbsp;\n, \r, \"
 * &nbsp;&nbsp;&nbsp;default: false
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
 * <pre>-datetimemsec-format &lt;adams.data.DateFormatString&gt; (property: dateTimeMsecFormat)
 * &nbsp;&nbsp;&nbsp;The format for date&#47;time msecs.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd HH:mm:ss.SSS
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 * <pre>-time-format &lt;adams.data.DateFormatString&gt; (property: timeFormat)
 * &nbsp;&nbsp;&nbsp;The format for times.
 * &nbsp;&nbsp;&nbsp;default: HH:mm:ss
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 * <pre>-timemsec-format &lt;adams.data.DateFormatString&gt; (property: timeMsecFormat)
 * &nbsp;&nbsp;&nbsp;The format for times with milli-seconds.
 * &nbsp;&nbsp;&nbsp;default: HH:mm:ss.SSS
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 * <pre>-time-zone &lt;java.util.TimeZone&gt; (property: timeZone)
 * &nbsp;&nbsp;&nbsp;The time zone to use for interpreting dates&#47;times; default is the system-wide 
 * &nbsp;&nbsp;&nbsp;defined one.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TsvSpreadSheetWriter
  extends CsvSpreadSheetWriter {

  private static final long serialVersionUID = 899390367241611793L;

  @Override
  public String globalInfo() {
    return "Writes TSV (tab-separated values) files,";
  }

  /**
   * Returns the default separator.
   *
   * @return		the default
   */
  @Override
  protected String getDefaultSeparator() {
    return "\\t";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new TsvSpreadSheetReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new TsvSpreadSheetReader().getFormatExtensions();
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  public SpreadSheetReader getCorrespondingReader() {
    return new TsvSpreadSheetReader();
  }
}
