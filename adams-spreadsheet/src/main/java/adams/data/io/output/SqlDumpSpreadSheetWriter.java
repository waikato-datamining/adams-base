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
 * SqlDumpSpreadSheetWriter.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.DateFormat;
import adams.core.Utils;
import adams.core.management.LocaleHelper;
import adams.data.io.input.SpreadSheetReader;
import adams.data.io.input.SqlDumpSpreadSheetReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.ColumnNameConversion;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SqlUtils;

import java.io.Writer;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Generates an SQL dump from the spreadsheet, which can be imported into a database.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-appending (property: appending)
 * &nbsp;&nbsp;&nbsp;If enabled, multiple spreadsheets with the same structure can be written
 * &nbsp;&nbsp;&nbsp;to the same file.
 * </pre>
 *
 * <pre>-keep-existing (property: keepExisting)
 * &nbsp;&nbsp;&nbsp;If enabled, any output file that exists when the writer is executed for
 * &nbsp;&nbsp;&nbsp;the first time won't get replaced with the current header; useful when outputting
 * &nbsp;&nbsp;&nbsp;data in multiple locations in the flow, but one needs to be cautious as
 * &nbsp;&nbsp;&nbsp;to not stored mixed content (eg varying number of columns, etc).
 * </pre>
 *
 * <pre>-table &lt;java.lang.String&gt; (property: table)
 * &nbsp;&nbsp;&nbsp;The name of the table.
 * &nbsp;&nbsp;&nbsp;default: blah
 * </pre>
 *
 * <pre>-column-name-conversion &lt;AS_IS|LOWER_CASE|UPPER_CASE&gt; (property: columnNameConversion)
 * &nbsp;&nbsp;&nbsp;How to convert the column headers into SQL table column names.
 * &nbsp;&nbsp;&nbsp;default: UPPER_CASE
 * </pre>
 *
 * <pre>-max-string-length &lt;int&gt; (property: maxStringLength)
 * &nbsp;&nbsp;&nbsp;The maximum length for strings to enforce; can be used as &#64;MAX in the 'stringColumnsSQL'
 * &nbsp;&nbsp;&nbsp; property.
 * &nbsp;&nbsp;&nbsp;default: 50
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-string-column-sql &lt;java.lang.String&gt; (property: stringColumnSQL)
 * &nbsp;&nbsp;&nbsp;The SQL type to use for STRING columns in the CREATE statement; you can
 * &nbsp;&nbsp;&nbsp;use the &#64;MAX placeholder to tie the type to the 'naxStringLength' property;
 * &nbsp;&nbsp;&nbsp; see also: http:&#47;&#47;en.wikipedia.org&#47;wiki&#47;SQL
 * &nbsp;&nbsp;&nbsp;default: VARCHAR(&#64;MAX)
 * </pre>
 *
 * <pre>-add-create-table (property: addCreateTable)
 * &nbsp;&nbsp;&nbsp;If enabled, a CREATE TABLE statement is output as well.
 * </pre>
 *
 * <pre>-use-backslashes (property: useBackslashes)
 * &nbsp;&nbsp;&nbsp;If enabled, backslashes are used to escape single quotes, rather than doubling
 * &nbsp;&nbsp;&nbsp;up the single quotes.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SqlDumpSpreadSheetWriter
  extends AbstractSpreadSheetWriter
  implements AppendableSpreadSheetWriter {

  /** for serialization. */
  private static final long serialVersionUID = -3643934248575351045L;

  /** whether to append spreadsheets. */
  protected boolean m_Appending;

  /** the header of the first spreadsheet written to file, if appending is active. */
  protected SpreadSheet m_Header;

  /** whether to keep existing files the first time the writer is called. */
  protected boolean m_KeepExisting;

  /** whether the file already exists. */
  protected boolean m_FileExists;

  /** the name of the table. */
  protected String m_Table;

  /** the type used for the table. */
  protected ContentType[] m_Types;

  /** the column names (shortened, disambiguated). */
  protected String[] m_ColumnNames;

  /** the column name conversion. */
  protected ColumnNameConversion m_ColumnNameConversion;

  /** the SQL type for string columns. */
  protected String m_StringColumnSQL;

  /** the maximum length for strings. */
  protected int m_MaxStringLength;

  /** whether to add a CREATE TABLE statement. */
  protected boolean m_AddCreateTable;

  /** whether to use backslashes for escaping. */
  protected boolean m_UseBackslashes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates an SQL dump from the spreadsheet, which can be imported into a database.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "appending", "appending",
	    false);

    m_OptionManager.add(
	    "keep-existing", "keepExisting",
	    false);

    m_OptionManager.add(
	    "table", "table",
	    "blah");

    m_OptionManager.add(
	    "column-name-conversion", "columnNameConversion",
	    ColumnNameConversion.UPPER_CASE);

    m_OptionManager.add(
	    "max-string-length", "maxStringLength",
	    50, 1, null);

    m_OptionManager.add(
	    "string-column-sql", "stringColumnSQL",
	    "VARCHAR(" + SqlUtils.Writer.PLACEHOLDER_MAX + ")");

    m_OptionManager.add(
	    "add-create-table", "addCreateTable",
	    false);

    m_OptionManager.add(
	    "use-backslashes", "useBackslashes",
	    false);
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
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "SQL dump";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"sql"};
  }

  /**
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  public SpreadSheetReader getCorrespondingReader() {
    return new SqlDumpSpreadSheetReader();
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
   * Sets the name of the table.
   *
   * @param value	the name
   */
  public void setTable(String value) {
    m_Table = value;
    reset();
  }

  /**
   * Returns the name of the table.
   *
   * @return		the name
   */
  public String getTable() {
    return m_Table;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tableTipText() {
    return "The name of the table.";
  }

  /**
   * Sets how to convert the column headers into SQL table column names.
   *
   * @param value	the conversion
   */
  public void setColumnNameConversion(ColumnNameConversion value) {
    m_ColumnNameConversion = value;
    reset();
  }

  /**
   * Returns how to convert the column headers into SQL table column names.
   *
   * @return		the conversion
   */
  public ColumnNameConversion getColumnNameConversion() {
    return m_ColumnNameConversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnNameConversionTipText() {
    return "How to convert the column headers into SQL table column names.";
  }

  /**
   * Sets the maximum length for strings.
   *
   * @param value	the maximum
   */
  public void setMaxStringLength(int value) {
    m_MaxStringLength = value;
    reset();
  }

  /**
   * Returns the maximum length for strings.
   *
   * @return		the maximum
   */
  public int getMaxStringLength() {
    return m_MaxStringLength;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxStringLengthTipText() {
    return
	"The maximum length for strings to enforce; can be used "
	+ "as " + SqlUtils.Writer.PLACEHOLDER_MAX + " in the 'stringColumnsSQL' property.";
  }

  /**
   * Sets the SQL type for string columns for the CREATE statement.
   *
   * @param value	the SQL type
   */
  public void setStringColumnSQL(String value) {
    m_StringColumnSQL = value;
    reset();
  }

  /**
   * Returns the SQL type for string columns for the CREATE statement.
   *
   * @return		the SQL type
   */
  public String getStringColumnSQL() {
    return m_StringColumnSQL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stringColumnSQLTipText() {
    return
	"The SQL type to use for STRING columns in the CREATE statement; "
	+ "you can use the " + SqlUtils.Writer.PLACEHOLDER_MAX + " placeholder to tie the type "
	+ "to the 'naxStringLength' property; see also: http://en.wikipedia.org/wiki/SQL";
  }

  /**
   * Sets whether to add a CREATE TABLE statement.
   *
   * @param value	if true then a CREATE TABLE statement is output as well
   */
  public void setAddCreateTable(boolean value) {
    m_AddCreateTable = value;
    reset();
  }

  /**
   * Returns whether a CREATE TABLE statement is output.
   *
   * @return		true if statement is output
   */
  public boolean getAddCreateTable() {
    return m_AddCreateTable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addCreateTableTipText() {
    return "If enabled, a CREATE TABLE statement is output as well.";
  }

  /**
   * Sets whether to use backslashes for escaping quotes rather than doubling
   * them.
   *
   * @param value	if true then backslashes are used
   */
  public void setUseBackslashes(boolean value) {
    m_UseBackslashes = value;
    reset();
  }

  /**
   * Returns whether to use backslashes for escaping quotes rather than doubling
   * them.
   *
   * @return		true if backslashes are used
   */
  public boolean getUseBackslashes() {
    return m_UseBackslashes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useBackslashesTipText() {
    return
        "If enabled, backslashes are used to escape single quotes, rather "
	+ "than doubling up the single quotes.";
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
   * Formats the number according to the format and returns the generated
   * textual representation.
   *
   * @param value	the double value to turn into a string
   * @return		the generated string
   */
  protected synchronized String format(double value) {
    return Utils.doubleToString(value, 12, LocaleHelper.getSingleton().getEnUS());
  }

  /**
   * Quotes the string if necessary.
   *
   * @param s		the string to quote, if necessary
   * @return		the potentially quoted string
   */
  protected String quoteString(String s) {
    String	result;

    if (m_UseBackslashes)
      result = Utils.quote(s, "'");
    else
      result = Utils.doubleUpQuotes(s, '\'', new char[]{'\t', '\n'}, new String[]{"\\t", "\\n"});

    return result;
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
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   *
   * @param content	the spreadsheet to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(SpreadSheet content, Writer writer) {
    boolean		result;
    String		newline;
    int			i;
    boolean		first;
    Cell		cell;
    SqlUtils.Writer	wrter;
    DateFormat		dformat;
    DateFormat		dtformat;
    DateFormat		dtmformat;
    DateFormat		tformat;

    result = true;

    try {
      newline   = System.getProperty("line.separator");
      dformat   = content.getDateFormat();
      dtformat  = content.getDateTimeFormat();
      dtmformat = content.getDateTimeMsecFormat();
      tformat   = content.getTimeFormat();

      if (m_Header == null) {
	// comments?
	for (i = 0; i < content.getComments().size(); i++)
	  writer.write("-- " + content.getComments().get(i) + newline);
	writer.write(newline);

	wrter         = new SqlUtils.Writer(content, m_Table, 255, m_ColumnNameConversion, m_StringColumnSQL, m_MaxStringLength);
	m_Types       = wrter.getContentTypes();
	m_ColumnNames = wrter.getColumnNames();

	// write header
	if (m_AddCreateTable) {
	  writer.write(wrter.getCreateStatement());
	  writer.write(newline);
	  writer.write(newline);
	}

	// keep header as reference
	if (m_Appending)
	  m_Header = content.getHeader();
      }

      // write data rows
      for (DataRow row: content.rows()) {
	first = true;
	writer.write("INSERT INTO " + m_Table + " VALUES(");
	for (String keyd: content.getHeaderRow().cellKeys()) {
	  cell = row.getCell(keyd);

	  if (!first)
	    writer.write(",");
	  if ((cell != null) && (cell.getContent() != null) && !cell.isMissing()) {
	    if (cell.isFormula()) {
	      writer.write(format(cell.toDouble()));
	    }
	    else {
	      switch (cell.getContentType()) {
		case STRING:
		  writer.write(quoteString(cell.getContent()));
		  break;
		case LONG:
		  writer.write(format(cell.toLong()));
		  break;
		case DOUBLE:
		  writer.write(format(cell.toDouble()));
		  break;
		case DATE:
		  writer.write(quoteString(dformat.format(cell.toDate())));
		  break;
		case DATETIME:
		  writer.write(quoteString(dtformat.format(cell.toDateTime())));
		  break;
                case DATETIMEMSEC:
		  writer.write(quoteString(dtmformat.format(cell.toDateTime())));
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
	    writer.write("NULL");
	  }

	  first = false;
	}
	writer.write(");");
	writer.write(newline);
      }
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed to write data!", e);
    }

    return result;
  }
}
