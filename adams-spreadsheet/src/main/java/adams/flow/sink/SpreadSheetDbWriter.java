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
 * SpreadSheetDbWriter.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.io.BatchSizeSupporter;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.ColumnNameConversion;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.sql.AbstractTypeMapper;
import adams.data.spreadsheet.sql.DefaultTypeMapper;
import adams.data.spreadsheet.sql.Writer;
import adams.db.SQL;
import adams.flow.core.ActorUtils;

/**
 <!-- globalinfo-start -->
 * Transfers a SpreadSheet object into a database.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.Row<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetDbWriter
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-table &lt;java.lang.String&gt; (property: table)
 * &nbsp;&nbsp;&nbsp;The table to write the data to (gets automatically created).
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
 * <pre>-batch-size &lt;int&gt; (property: batchSize)
 * &nbsp;&nbsp;&nbsp;The size of the batch when inserting the data; can help improve speed of
 * &nbsp;&nbsp;&nbsp;data import.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetDbWriter
  extends AbstractSink
  implements BatchSizeSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 393925191813730213L;

  /** the database connection. */
  protected adams.db.AbstractDatabaseConnection m_DatabaseConnection;

  /** the type mapper to use. */
  protected AbstractTypeMapper m_TypeMapper;

  /** the table to write the data to. */
  protected String m_Table;

  /** the type used for the table. */
  protected ContentType[] m_Types;

  /** the maximum length for column names. */
  protected int m_MaxColumnLength;

  /** the column names (shortened, disambiguated). */
  protected String[] m_ColumnNames;

  /** the column name conversion. */
  protected ColumnNameConversion m_ColumnNameConversion;

  /** the SQL type for string columns. */
  protected String m_StringColumnSQL;

  /** the maximum length for strings. */
  protected int m_MaxStringLength;

  /** the batch size. */
  protected int m_BatchSize;

  /** the writer for writing the data to the database. */
  protected Writer m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Transfers a SpreadSheet object into a database.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type-mapper", "typeMapper",
      new DefaultTypeMapper());

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
      "VARCHAR(" +  Writer.PLACEHOLDER_MAX + ")");

    m_OptionManager.add(
      "batch-size", "batchSize",
      1, 1, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_DatabaseConnection = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "table", m_Table, "table: ");
    result += QuickInfoHelper.toString(this, "columnNameConversion", m_ColumnNameConversion, ", conversion: ");
    result += QuickInfoHelper.toString(this, "maxStringLength", m_MaxStringLength, ", max string: ");
    result += QuickInfoHelper.toString(this, "stringColumnSQL", m_StringColumnSQL, ", string type: ");
    result += QuickInfoHelper.toString(this, "batchSize", m_BatchSize, ", batch: ");

    return result;
  }

  /**
   * Sets the type mapper to use.
   *
   * @param value	the mapper
   */
  public void setTypeMapper(AbstractTypeMapper value) {
    m_TypeMapper = value;
    reset();
  }

  /**
   * Returns the type mapper in use.
   *
   * @return		the mapper
   */
  public AbstractTypeMapper  getTypeMapper() {
    return m_TypeMapper;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeMapperTipText() {
    return "The type mapper to use for mapping spreadsheet and SQL types.";
  }

  /**
   * Sets the table to write the data to.
   *
   * @param value	the table name
   */
  public void setTable(String value) {
    m_Table = value;
    reset();
  }

  /**
   * Returns the table to write the data to.
   *
   * @return		the table name
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
    return "The table to write the data to (gets automatically created).";
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
    if (getOptionManager().isValid("maxStringLength", value)) {
      m_MaxStringLength = value;
      reset();
    }
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
	+ "as " + Writer.PLACEHOLDER_MAX + " in the 'stringColumnsSQL' property.";
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
	+ "you can use the " + Writer.PLACEHOLDER_MAX + " placeholder to tie the type "
	+ "to the 'naxStringLength' property; see also: http://en.wikipedia.org/wiki/SQL";
  }

  /**
   * Sets the batch size.
   *
   * @param value	the batch size
   */
  @Override
  public void setBatchSize(int value) {
    if (getOptionManager().isValid("batchSize", value)) {
      m_BatchSize = value;
      reset();
    }
  }

  /**
   * Returns the batch size.
   *
   * @return		the batch size
   */
  @Override
  public int getBatchSize() {
    return m_BatchSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String batchSizeTipText() {
    return
      "The size of the batch when inserting the data; can help improve speed of data import.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.spreadsheet.SpreadSheet.class, adams.data.spreadsheet.Row.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class, Row.class};
  }

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  protected adams.db.AbstractDatabaseConnection getDatabaseConnection() {
    return ActorUtils.getDatabaseConnection(
      this,
      adams.flow.standalone.DatabaseConnection.class,
      adams.db.DatabaseConnection.getSingleton());
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Row			row;
    SpreadSheet		sheet;
    SQL			sql;

    result = null;

    if (m_DatabaseConnection == null)
      m_DatabaseConnection = getDatabaseConnection();

    if (m_InputToken.getPayload() instanceof Row) {
      row   = (Row) m_InputToken.getPayload();
      sheet = row.getOwner().getClone();
      sheet.clear();
      sheet.addRow().assign(row);
    }
    else {
      sheet = (SpreadSheet) m_InputToken.getPayload();
    }
    sql = new SQL(m_DatabaseConnection);
    sql.setDebug(isLoggingEnabled());

    m_Writer = null;
    try {
      m_Writer = new Writer(
	sheet,
	m_TypeMapper,
	m_Table,
	sql.getMaxColumnNameLength(),
	m_ColumnNameConversion,
	m_StringColumnSQL,
	m_MaxStringLength,
	m_BatchSize);
      m_Writer.setLoggingLevel(getLoggingLevel());
    }
    catch (Exception e) {
      m_Writer = null;
      result = handleException("Failed to determine max column name length", e);
    }

    if (m_Writer != null) {
      if (!sql.tableExists(m_Table))
	result = m_Writer.createTable(sql);
      if (result == null)
	result = m_Writer.writeData(sql);
    }

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Writer != null)
      m_Writer.stopExecution();

    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    m_DatabaseConnection = null;

    super.wrapUp();
  }
}
