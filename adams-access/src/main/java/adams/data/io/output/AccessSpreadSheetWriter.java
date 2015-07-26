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
 * AccessSpreadSheetWriter.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.data.io.input.AccessSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import com.healthmarketscience.jackcess.ColumnBuilder;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Database.FileFormat;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.TableBuilder;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Writes spreadsheet data to a MS Access database.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-file-format &lt;V1997 [VERSION_3]|V2000 [VERSION_4]|V2003 [VERSION_4]|V2007 [VERSION_12]|V2010 [VERSION_14]|MSISAM [MSISAM]&gt; (property: fileFormat)
 * &nbsp;&nbsp;&nbsp;The MS Access file format to use when creating a new database.
 * &nbsp;&nbsp;&nbsp;default: V2010 [VERSION_14]
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AccessSpreadSheetWriter
  extends AbstractSpreadSheetWriter
  implements AppendableSpreadSheetWriter, IncrementalSpreadSheetWriter {

  /** for serialization. */
  private static final long serialVersionUID = -3549185519778801930L;

  /** the file format to use. */
  protected FileFormat m_FileFormat;

  /** the table name to use. */
  protected String m_TableName;

  /** whether to append spreadsheets. */
  protected boolean m_Appending;

  /** the header of the first spreadsheet written to file, if appending is active. */
  protected SpreadSheet m_Header;

  /** whether to keep existing files the first time the writer is called. */
  protected boolean m_KeepExisting;

  /** whether the file already exists. */
  protected boolean m_FileExists;

  /** the database object to use. */
  protected transient Database m_Database;

  /** the table object to use. */
  protected transient Table m_Table;

  /** the column types. */
  protected HashMap<Integer,DataType> m_ColumnTypes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes spreadsheet data to a MS Access database.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "file-format", "fileFormat",
      FileFormat.V2010);

    m_OptionManager.add(
      "table-name", "tableName",
      "");

    m_OptionManager.add(
      "appending", "appending",
      false);

    m_OptionManager.add(
      "keep-existing", "keepExisting",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Header      = null;
    m_FileExists  = false;
    m_Database    = null;
    m_Table       = null;
    m_ColumnTypes = new HashMap<>();
  }

  /**
   * Resets the writer.
   */
  @Override
  public void reset() {
    super.reset();

    m_Header     = null;
    m_FileExists = false;
    m_Database   = null;
    m_Table      = null;
    m_ColumnTypes.clear();
  }

  /**
   * Sets the file format to use when creating a database.
   *
   * @param value	the file format
   */
  public void setFileFormat(FileFormat value) {
    m_FileFormat = value;
    reset();
  }

  /**
   * Returns the file format in use when creating a database.
   *
   * @return		the file format
   */
  public FileFormat getFileFormat() {
    return m_FileFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String fileFormatTipText() {
    return "The MS Access file format to use when creating a new database.";
  }

  /**
   * Sets the table name to use. If empty the spreadsheet's name is used.
   *
   * @param value	the table name
   */
  public void setTableName(String value) {
    m_TableName = value;
    reset();
  }

  /**
   * Returns the table name to use. If empty the spreadsheet's name is used.
   *
   * @return		the table name
   */
  public String getTableName() {
    return m_TableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String tableNameTipText() {
    return "The table name to use; if empty the spreadsheet's name is used.";
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
    return new String[]{"mdb", "accdb"};
  }

  /**
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  public SpreadSheetReader getCorrespondingReader() {
    return new AccessSpreadSheetReader();
  }

  /**
   * Returns how the data is written.
   *
   * @return		the type
   */
  @Override
  protected OutputType getOutputType() {
    return OutputType.FILE;
  }

  /**
   * Determines the name for the table. Uses the project name if sheet has
   * no name set and no table is set.
   *
   * @param sheet       the sheet to determine the name for
   * @return            the name for the table
   * @see               #getTableName()
   */
  protected String determineTableName(SpreadSheet sheet) {
    String    result;

    if (!m_TableName.isEmpty())
      return m_TableName;
    else
      return sheet.getName() == null ? Environment.getInstance().getProject() : sheet.getName();
  }

  /**
   * Writes the header.
   *
   * @param header	the header row to write
   * @param filename	the file to write the header to
   */
  protected boolean doWriteHeader(Row header, String filename) {
    boolean	          result;
    int		          i;
    SpreadSheet           sheet;
    TableBuilder          builder;
    String                name;
    HashSet<ContentType>  types;
    ContentType           type;
    DataType              colType;

    result = true;
    
    try {
      if (!FileUtils.fileExists(filename))
        m_Database = DatabaseBuilder.create(m_FileFormat, new File(filename));
      else
        m_Database = DatabaseBuilder.open(new File(filename));

      sheet = header.getOwner();
      name  = determineTableName(sheet);
      m_ColumnTypes.clear();
      if (!m_Database.getTableNames().contains(sheet.getName())) {
        builder = new TableBuilder(name);
        for (i = 0; i < sheet.getColumnCount(); i++) {
          types = new HashSet<>(sheet.getContentTypes(i));
          if (types.size() == 1)
            type = types.toArray(new ContentType[1])[0];
          else if ((types.size() > 0) && (sheet.isNumeric(i)))
            type = ContentType.DOUBLE;
          else
            type = ContentType.STRING;

          switch (type) {
            case BOOLEAN:
              colType = DataType.BOOLEAN;
              break;
            case TIME:
            case DATE:
            case DATETIME:
            case DATETIMEMSEC:
              colType = DataType.SHORT_DATE_TIME;
              break;
            case DOUBLE:
              colType = DataType.DOUBLE;
              break;
            case LONG:
              colType = DataType.LONG;
              break;
            default:
              colType = DataType.TEXT;
              break;
          }

          m_ColumnTypes.put(i, colType);
          builder.addColumn(new ColumnBuilder(sheet.getColumnName(i), colType));
        }
        m_Table = builder.toTable(m_Database);
      }
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed creating m_Table!", e);
    }
    
    return result;
  }

  /**
   * Writes the header, if necessary.
   *
   * @param header	the header row to write
   * @param filename	the file to write the header to
   */
  protected boolean writeHeader(Row header, String filename) {
    boolean	result;
    
    result = true;
    
    if (m_Header == null) {
      if (!m_FileExists || !m_KeepExisting) {
	result = doWriteHeader(header, filename);
	// keep header as reference
	if (m_Appending)
	  m_Header = header.getOwner().getHeader();
      }
    }
    
    return result;
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   *
   * @param content	the row to write
   * @param filename	the file to write the spreadsheet to
   * @return		true if successfully written
   */
  protected boolean doWrite(Row content, String filename) {
    boolean     result;
    SpreadSheet sheet;
    String      name;
    Cell        cell;
    Object[]    cells;
    int         i;

    result = true;

    try {
      sheet   = content.getOwner();
      if (m_Table == null) {
        name    = determineTableName(sheet);
        m_Table = m_Database.getTable(name);
      }
      cells = new Object[sheet.getColumnCount()];
      for (i = 0; i < sheet.getColumnCount(); i++) {
        cell     = content.getCell(sheet.getHeaderRow().getCellKey(i));
        cells[i] = null;
	if ((cell != null) && (cell.getContent() != null) && !cell.isMissing()) {
          switch (m_ColumnTypes.get(i)) {
            case BOOLEAN:
              cells[i] = cell.toBoolean();
              break;
            case LONG:
              cells[i] = cell.toLong();
              break;
            case DOUBLE:
              cells[i] = cell.toDouble();
              break;
            case SHORT_DATE_TIME:
              cells[i] = cell.toAnyDateType();
              break;
            case TEXT:
              cells[i] = cell.toString();
              break;
            default:
              throw new IllegalStateException("Unhandled SQL column type: " + m_ColumnTypes.get(i));
          }
        }
      }
      m_Table.addRow(cells);
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
   * <br><br>
   * Default implementation returns always false.
   *
   * @param content	the spreadsheet to write
   * @param filename	the file to write the spreadsheet to
   * @return		true if successfully written
   */
  protected boolean doWrite(SpreadSheet content, String filename) {
    boolean	result;

    m_Appending = true;
    result      = writeHeader(content.getHeaderRow(), filename);
    for (DataRow row: content.rows()) {
      result = doWrite(row, filename);
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
    boolean	result;

    m_Appending = true;
    result      = writeHeader(content.getOwner().getHeaderRow(), filename);
    if (result)
      result = doWrite(content, filename);

    return result;
  }

  /**
   * Writes the spreadsheet to the given output stream. The caller
   * must ensure that the stream gets closed.
   *
   * @param content	the spreadsheet to write
   * @param stream	the output stream to write the spreadsheet to
   * @return		always false
   * @see               #write(adams.data.spreadsheet.Row, String)
   */
  public boolean write(Row content, OutputStream stream) {
    return false;
  }

  /**
   * Writes the spreadsheet to the given writer. The caller
   * must ensure that the writer gets closed.
   *
   * @param content	the spreadsheet to write
   * @param writer	the writer to write the spreadsheet to
   * @return		always false
   * @see               #write(adams.data.spreadsheet.Row, String)
   */
  public boolean write(Row content, Writer writer) {
    return false;
  }
}
