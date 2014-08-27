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
 * CsvSpreadSheetWriter.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.data.DateFormatString;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Writes CSV files.
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
 * &nbsp;&nbsp;&nbsp;default: ,
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
 * <pre>-time-format &lt;adams.data.DateFormatString&gt; (property: timeFormat)
 * &nbsp;&nbsp;&nbsp;The format for times.
 * &nbsp;&nbsp;&nbsp;default: HH:mm:ss
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CsvSpreadSheetWriter
  extends AbstractFormattedSpreadSheetWriter
  implements AppendableSpreadSheetWriter, SpreadSheetWriterWithFormulaSupport, 
             IncrementalSpreadSheetWriter {

  /** for serialization. */
  private static final long serialVersionUID = -3549185519778801930L;

  /** the characters to backquote. */
  public final static char[] BACKQUOTE_CHARS = new char[]{'\t'};

  /** the backquoted string representations of characters to backquote. */
  public final static String[] BACKQUOTED_STRINGS = new String[]{"\\t"};

  /** the characters to escape. */
  public final static char[] ESCAPE_CHARS = new char[]{'\\', '\'', '\t', '\n', '\r', '"'};

  /** the escaped string representations of characters to backquote. */
  public final static String[] ESCAPE_STRINGS = new String[]{"\\\\", "\\'", "\\t", "\\n", "\\r", "\\\""};

  /** the line comment. */
  protected String m_Comment;

  /** whether to output the comments. */
  protected boolean m_OutputComments;

  /** whether to append spreadsheets. */
  protected boolean m_Appending;

  /** the header of the first spreadsheet written to file, if appending is active. */
  protected SpreadSheet m_Header;

  /** whether to keep existing files the first time the writer is called. */
  protected boolean m_KeepExisting;

  /** whether the file already exists. */
  protected boolean m_FileExists;

  /** whether to output the cells as displayed (disable to output formulas). */
  protected boolean m_OutputAsDisplayed;

  /** the quote character. */
  protected String m_QuoteCharacter;

  /** the column separator. */
  protected String m_Separator;

  /** the new line. */
  protected String m_NewLine;

  /** whether to always quote text cells. */
  protected boolean m_AlwaysQuoteText;
  
  /** whether to escape special characters like \t, \n and \r. */
  protected boolean m_EscapeSpecialChars;

  /** the format string for the dates. */
  protected DateFormatString m_DateFormat;

  /** the format string for the date/times. */
  protected DateFormatString m_DateTimeFormat;

  /** the format string for the times. */
  protected DateFormatString m_TimeFormat;

  /** the date formatter. */
  protected transient DateFormat m_DateFormatter;

  /** the date/time formatter. */
  protected transient DateFormat m_DateTimeFormatter;

  /** the time formatter. */
  protected transient DateFormat m_TimeFormatter;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes CSV files.";
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
	    "comment", "comment",
	    SpreadSheet.COMMENT);

    m_OptionManager.add(
	    "output-comments", "outputComments",
	    true);

    m_OptionManager.add(
	    "output-as-displayed", "outputAsDisplayed",
	    false);

    m_OptionManager.add(
	    "appending", "appending",
	    false);

    m_OptionManager.add(
	    "keep-existing", "keepExisting",
	    false);

    m_OptionManager.add(
	    "quote-char", "quoteCharacter",
	    "\"");

    m_OptionManager.add(
	    "separator", "separator",
	    ",");

    m_OptionManager.add(
	    "new-line", "newLine",
	    Utils.backQuoteChars(System.getProperty("line.separator")));

    m_OptionManager.add(
	    "always-quote-text", "alwaysQuoteText",
	    false);

    m_OptionManager.add(
	    "escape-special-chars", "escapeSpecialChars",
	    false);

    m_OptionManager.add(
	    "date-format", "dateFormat",
	    new DateFormatString(Constants.DATE_FORMAT));

    m_OptionManager.add(
	    "datetime-format", "dateTimeFormat",
	    new DateFormatString(Constants.TIMESTAMP_FORMAT));

    m_OptionManager.add(
	    "time-format", "timeFormat",
	    new DateFormatString(Constants.TIME_FORMAT));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Header     = null;
    m_FileExists = false;
  }

  /**
   * Resets the writer.
   */
  @Override
  public void reset() {
    super.reset();

    m_Header     = null;
    m_FileExists = false;
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
   * Sets whether to output the comments before the data.
   *
   * @param value	true if to output comments
   */
  public void setOutputComments(boolean value) {
    m_OutputComments = value;
    reset();
  }

  /**
   * Returns whether to output comments before the data.
   *
   * @return		true if to output comments
   */
  public boolean getOutputComments() {
    return m_OutputComments;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String outputCommentsTipText() {
    return "If enabled, any available comments are output before the actual data (using the 'comment' prefix).";
  }

  /**
   * Sets whether to output the cell content as displayed, ie, no formulas
   * but the result of formulas.
   *
   * @param value	true if to output as displayed
   */
  @Override
  public void setOutputAsDisplayed(boolean value) {
    m_OutputAsDisplayed = value;
    reset();
  }

  /**
   * Returns whether to output the cell content as displayed, ie, no formulas
   * but the result of formulas.
   *
   * @return		true if to output as displayed
   */
  @Override
  public boolean getOutputAsDisplayed() {
    return m_OutputAsDisplayed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  @Override
  public String outputAsDisplayedTipText() {
    return "If enabled, cells are output as displayed, ie, results of formulas instead of the formulas.";
  }

  /**
   * Checks whether we can append the specified spreadsheet to the existing
   * file.
   *
   * @param sheet	the spreadsheet to append to the existing one
   * @return		true if appending is possible
   */
  @Override
  public boolean canAppend(SpreadSheet sheet) {
    if (m_Header == null)
      return m_KeepExisting;
    return (m_Header.equalsHeader(sheet) == null);
  }

  /**
   * Sets whether the next write call is to append the data to the existing
   * file.
   *
   * @param value	true if to append
   */
  @Override
  public void setAppending(boolean value) {
    m_Appending = value;
    reset();
  }

  /**
   * Returns whether the next spreadsheet will get appended.
   *
   * @return		true if append is active
   */
  @Override
  public boolean isAppending() {
    return m_Appending;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  @Override
  public String appendingTipText() {
    return "If enabled, multiple spreadsheets with the same structure can be written to the same file.";
  }

  /**
   * Sets whether to keep any existing file on first execution.
   *
   * @param value	if true then existing file is kept
   */
  @Override
  public void setKeepExisting(boolean value) {
    m_KeepExisting = value;
    reset();
  }

  /**
   * Returns whether any existing file is kept on first execution.
   *
   * @return		true if existing file is kept
   */
  @Override
  public boolean getKeepExisting() {
    return m_KeepExisting;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String keepExistingTipText() {
    return
        "If enabled, any output file that exists when the writer is executed "
      + "for the first time won't get replaced with the current header; "
      + "useful when outputting data in multiple locations in the flow, but "
      + "one needs to be cautious as to not stored mixed content (eg varying "
      + "number of columns, etc).";
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
   * Sets whether to always surround text/formula cells by double quotes.
   *
   * @param value	true if to always quote
   */
  public void setAlwaysQuoteText(boolean value) {
    m_AlwaysQuoteText = value;
    reset();
  }

  /**
   * Returns whether to always surround text/formula cells by double quotes.
   *
   * @return		true if append to always quote
   */
  public boolean getAlwaysQuoteText() {
    return m_AlwaysQuoteText;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String alwaysQuoteTextTipText() {
    return "If enabled, text/formula cells always get surrounded by double quotes.";
  }

  /**
   * Sets whether to escape special chars like \\t, \\r and \\n with
   * a backslash.
   *
   * @param value	true if to escape special chars
   */
  public void setEscapeSpecialChars(boolean value) {
    m_EscapeSpecialChars = value;
    reset();
  }

  /**
   * Returns whether to escape special chars like \\t, \\r and \\n with
   * a backslash.
   *
   * @return		true if to escape special chars
   */
  public boolean getEscapeSpecialChars() {
    return m_EscapeSpecialChars;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String escapeSpecialCharsTipText() {
    return "If enabled, special characters get escaped with a backslash: " + Utils.flatten(ESCAPE_STRINGS, ", ");
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
   * Sets whether the output file already exists.
   *
   * @param value	true if the output file already exists
   */
  @Override
  public void setFileExists(boolean value) {
    m_FileExists = value;
  }

  /**
   * Returns whether the output file already exists.
   *
   * @return		true if the output file already exists
   */
  @Override
  public boolean getFileExists() {
    return m_FileExists;
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
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  public SpreadSheetReader getCorrespondingReader() {
    return new CsvSpreadSheetReader();
  }

  /**
   * Returns whether to write to an OutputStream rather than a Writer.
   *
   * @return		true if to write to an OutputStream
   */
  @Override
  protected boolean getUseOutputStream() {
    return false;
  }

  /**
   * Returns whether to automatically compress.
   *
   * @return		true if to automatically decompress
   */
  @Override
  protected boolean supportsCompressedOutput() {
    return !isAppending();
  }
  
  /**
   * Returns the formatter for dates.
   * 
   * @return		the formatter
   */
  protected DateFormat getDateFormatter() {
    if (m_DateFormatter == null)
      m_DateFormatter = m_DateFormat.toDateFormat();
    return m_DateFormatter;
  }
  
  /**
   * Returns the formatter for date/times.
   * 
   * @return		the formatter
   */
  protected DateFormat getDateTimeFormatter() {
    if (m_DateTimeFormatter == null)
      m_DateTimeFormatter = m_DateTimeFormat.toDateFormat();
    return m_DateTimeFormatter;
  }
  
  /**
   * Returns the formatter for times.
   * 
   * @return		the formatter
   */
  protected DateFormat getTimeFormatter() {
    if (m_TimeFormatter == null)
      m_TimeFormatter = m_TimeFormat.toDateFormat();
    return m_TimeFormatter;
  }

  /**
   * Quotes the string if necessary.
   *
   * @param s		the string to quote, if necessary
   * @return		the potentially quoted string
   */
  protected String quoteString(String s) {
    String	result;
    boolean	required;

    required =    m_AlwaysQuoteText
	       || s.contains(m_Separator)
	       || s.contains(" ")
	       || s.contains(m_QuoteCharacter)
	       || s.contains("\n")
	       || s.contains("\t")
	       || (s.length() == 0);

    if (m_EscapeSpecialChars)
      result = m_QuoteCharacter + Utils.backQuoteChars(s, ESCAPE_CHARS, ESCAPE_STRINGS) + m_QuoteCharacter;
    else if (required)
      result = Utils.doubleUpQuotes(s, m_QuoteCharacter.charAt(0), BACKQUOTE_CHARS, BACKQUOTED_STRINGS);
    else
      result = s;

    return result;
  }

  /**
   * Quotes the (numeric) string if necessary.
   *
   * @param s		the (numeric) string to quote, if necessary
   * @return		the potentially quoted string
   */
  protected String quoteNumber(String s) {
    String	result;
    boolean	required;

    required =    s.contains(m_Separator)
	       || (s.length() == 0);

    if (required)
      result = Utils.doubleUpQuotes(s, m_QuoteCharacter.charAt(0), BACKQUOTE_CHARS, BACKQUOTED_STRINGS);
    else
      result = s;

    return result;
  }

  /**
   * Writes the header.
   *
   * @param header	the header row to write
   * @param writer	the writer to write the header to
   */
  protected boolean doWriteHeader(Row header, Writer writer) {
    boolean	result;
    int		i;
    boolean	first;
    Cell	cell;
    
    result = true;
    
    try {
      // comments?
      if (m_OutputComments) {
	for (i = 0; i < header.getOwner().getComments().size(); i++)
	  writer.write(m_Comment + " " + header.getOwner().getComments().get(i) + m_NewLine);
      }

      // write header
      first = true;
      for (String key: header.cellKeys()) {
	cell = header.getCell(key);

	if (!first)
	  writer.write(m_Separator);
	if (cell.isMissing())
	  writer.write(quoteString(m_MissingValue));
	else
	  writer.write(quoteString(cell.getContent()));

	first = false;
      }
      writer.write(m_NewLine);
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed writing spreadsheet header", e);
    }
    
    return result;
  }

  /**
   * Writes the header.
   *
   * @param header	the header row to write
   * @param writer	the writer to write the header to
   */
  protected boolean writeHeader(Row header, Writer writer) {
    boolean	result;
    
    result = true;
    
    if (!m_FileExists || !m_KeepExisting)
      result = doWriteHeader(header, writer);
    
    return result;
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   *
   * @param content	the row to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  protected boolean doWrite(Row content, Writer writer) {
    boolean			result;
    boolean			first;
    Cell			cell;
    DateFormat			dformat;
    DateFormat			dtformat;
    DateFormat			tformat;

    result = true;

    try {
      dformat  = getDateFormatter();
      dtformat = getDateTimeFormatter();
      tformat  = getTimeFormatter();

      if (m_Header == null) {
	// keep header as reference
	if (m_Appending)
	  m_Header = content.getOwner().getHeader();
      }

      // write data rows
      first = true;
      for (String keyd: content.getOwner().getHeaderRow().cellKeys()) {
	cell = content.getCell(keyd);

	if (!first)
	  writer.write(m_Separator);
	if ((cell != null) && (cell.getContent() != null) && !cell.isMissing()) {
	  if (cell.isFormula() && !m_OutputAsDisplayed) {
	    writer.write(quoteString(cell.getFormula()));
	  }
	  else {
	    switch (cell.getContentType()) {
	      case STRING:
		writer.write(quoteString(cell.getContent()));
		break;
	      case LONG:
		writer.write(quoteNumber(cell.toLong().toString()));
		break;
	      case DOUBLE:
		writer.write(quoteNumber(format(cell.toDouble())));
		break;
	      case DATE:
		writer.write(quoteString(dformat.format(cell.toDate())));
		break;
	      case DATETIME:
		writer.write(quoteString(dtformat.format(cell.toDateTime())));
		break;
	      case TIME:
		writer.write(quoteString(tformat.format(cell.toTime())));
		break;
	      case BOOLEAN:
		writer.write(quoteString(cell.toBoolean().toString()));
		break;
	      default:
		writer.write(quoteString(cell.toString()));
		break;
	    }
	  }
	}
	else {
	  writer.write(quoteString(m_MissingValue));
	}

	first = false;
      }
      writer.write(m_NewLine);
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed writing spreadsheet data", e);
    }

    return result;
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
    
    m_Appending = true;
    result      = writeHeader(content.getHeaderRow(), writer);
    for (DataRow row: content.rows()) {
      result = doWrite(row, writer);
      if (!result)
	break;
    }
    
    return result;
  }

  /**
   * Returns whether the writer can write data incrementally.
   * 
   * @return		true if data can be written incrementally
   */
  public boolean isIncremental() {
    return true;
  }
  
  /**
   * Writes the given content to the specified file.
   *
   * @param content	the content to write
   * @param file	the file to write to
   * @return		true if successfully written
   */
  public boolean write(Row content, File file) {
    m_Appending = true;
    return write(content, file.getAbsolutePath());
  }

  /**
   * Writes the spreadsheet to the given file.
   *
   * @param content	the spreadsheet to write
   * @param filename	the file to write the spreadsheet to
   * @return		true if successfully written
   */
  public boolean write(Row content, String filename) {
    boolean			result;
    BufferedWriter		writer;
    OutputStream		output;

    result      = true;
    m_Appending = true;

    try {
      output = new FileOutputStream(filename, (m_Header != null));
      if (m_Encoding != null)
	writer = new BufferedWriter(new OutputStreamWriter(output, m_Encoding.charsetValue()));
      else
	writer = new BufferedWriter(new OutputStreamWriter(output));
      result = doWrite(content, writer);
      writer.flush();
      writer.close();
    }
    catch (Exception e) {
      result = false;
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Writes the spreadsheet to the given output stream. The caller
   * must ensure that the stream gets closed.
   *
   * @param content	the spreadsheet to write
   * @param stream	the output stream to write the spreadsheet to
   * @return		true if successfully written
   */
  public boolean write(Row content, OutputStream stream) {
    m_Appending = true;
    return doWrite(content, new OutputStreamWriter(stream));
  }

  /**
   * Writes the spreadsheet to the given writer. The caller
   * must ensure that the writer gets closed.
   *
   * @param content	the spreadsheet to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  public boolean write(Row content, Writer writer) {
    m_Appending = true;
    return doWrite(content, writer);
  }
}
