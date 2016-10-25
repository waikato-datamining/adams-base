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
 * TsvSpreadSheetReader.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.data.io.output.SpreadSheetWriter;
import adams.data.io.output.TsvSpreadSheetWriter;

/**
 <!-- globalinfo-start -->
 * Reads TSV (tab-separated values) files.<br>
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
 * <pre>-missing &lt;adams.core.base.BaseRegExp&gt; (property: missingValue)
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
 * &nbsp;&nbsp;&nbsp;default: \\t
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
public class TsvSpreadSheetReader
  extends SimpleCsvSpreadSheetReader {

  private static final long serialVersionUID = 2446979875221254720L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Reads TSV (tab-separated values) files.\n"
        + "It is possible to force columns to be text. In that case no "
        + "intelligent parsing is attempted to determine the type of data a "
        + "cell has.\n"
        + "For very large files, one can turn on chunking, which returns "
        + "spreadsheet objects till all the data has been read.";
  }

  /**
   * Returns the default separator.
   *
   * @return		the default
   */
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
    return "Tab-separated values files";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"tsv", "tsv.gz"};
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  public SpreadSheetWriter getCorrespondingWriter() {
    return new TsvSpreadSheetWriter();
  }
}
