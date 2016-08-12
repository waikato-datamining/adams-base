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
 * Writer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet.sql;

import adams.core.Stoppable;
import adams.core.Utils;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingObject;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.ColumnNameConversion;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.db.SQL;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.HashSet;

/**
 * For writing data to a database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13404 $
 */
public class Writer
  extends LoggingObject
  implements Stoppable {

  /** for serialization. */
  private static final long serialVersionUID = 1094278005436201843L;

  /** the placeholder for the maximum length for string values. */
  public final static String PLACEHOLDER_MAX = "@MAX";

  /** the type mapper to use. */
  protected AbstractTypeMapper m_TypeMapper;

  /** the underlying spreadm_Sheet. */
  protected SpreadSheet m_Sheet;

  /** the table to write the data to. */
  protected String m_Table;

  /** the type used for the table. */
  protected ContentType[] m_ContentTypes;

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

  /** whether the last action was stopped. */
  protected boolean m_Stopped;

  /**
   * Initializes the object.
   *
   * @param sheet	the underlying spreadm_Sheet
   * @param typeMapper  the type mapper to use
   * @param table	the table name
   * @param maxCol	the maximum length for column names
   * @param colName	the conversion for column names
   * @param stringCol	the SQL type for string columns
   * @param maxStr	the maximum length for strings (get truncated)
   */
  public Writer(SpreadSheet sheet, AbstractTypeMapper typeMapper, String table, int maxCol, ColumnNameConversion colName, String stringCol, int maxStr, int batchSize) {
    super();

    m_Sheet                = sheet;
    m_TypeMapper           = typeMapper;
    m_Table                = table;
    m_MaxColumnLength      = maxCol;
    m_ColumnNameConversion = colName;
    m_StringColumnSQL      = stringCol;
    m_MaxStringLength      = maxStr;
    m_BatchSize            = batchSize;

    generate();
  }

  /**
   * Generates the table setup.
   */
  protected void generate() {
    int				i;
    ContentType			type;
    Collection<ContentType> types;
    HashSet<String> names;
    String			name;
    String			prefix;
    int				count;

    // column types
    m_ContentTypes = new ContentType[m_Sheet.getColumnCount()];
    for (i = 0; i < m_Sheet.getColumnCount(); i++) {
      m_ContentTypes[i] = ContentType.STRING;
      if (m_Sheet.isNumeric(i)) {
	m_ContentTypes[i] = ContentType.DOUBLE;
	types             = m_Sheet.getContentTypes(i);
	if ((types.size() == 1) && types.contains(ContentType.LONG))
	  m_ContentTypes[i] = ContentType.LONG;
      }
      else {
	type = m_Sheet.getContentType(i);
	if (type == null)
	  type = ContentType.STRING;
	switch (type) {
	  case TIME:
	  case TIMEMSEC:
	  case DATE:
	  case DATETIME:
	  case DATETIMEMSEC:
	    m_ContentTypes[i] = type;
	    break;
	}
      }
    }

    // column names
    m_ColumnNames = new String[m_Sheet.getColumnCount()];
    names         = new HashSet<>();
    for (i = 0; i < m_Sheet.getColumnCount(); i++) {
      name   = m_Sheet.getHeaderRow().getCell(i).getContent();
      name   = fixColumnName(name);
      prefix = name;
      count  = 0;
      while (names.contains(name)) {
	count++;
	if ((prefix + count).length() > m_MaxColumnLength)
	  prefix = prefix.substring(0, prefix.length() - 1);
	name = prefix + count;
      }
      names.add(name);
      m_ColumnNames[i] = name;
    }
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel = value;
  }

  /**
   * Returns the type mapper in use.
   *
   * @return		the type mapper
   */
  public AbstractTypeMapper getTypeMapper() {
    return m_TypeMapper;
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
   * Returns how to convert the column headers into SQL table column names.
   *
   * @return		the conversion
   */
  public ColumnNameConversion getColumnNameConversion() {
    return m_ColumnNameConversion;
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
   * Returns the SQL type for string columns for the CREATE statement.
   *
   * @return		the SQL type
   */
  public String getStringColumnSQL() {
    return m_StringColumnSQL;
  }

  /**
   * Fixes the column name.
   *
   * @param s		the column name to fix
   * @return		the fixed name
   */
  protected String fixColumnName(String s) {
    String	result;
    int		i;
    char	chr;

    result = "";

    for (i = 0; i < s.length(); i++) {
      chr = s.charAt(i);
      if ((chr >= 'A') && (chr <= 'Z'))
	result += chr;
      else if ((chr >= 'a') && (chr <= 'z'))
	result += chr;
      if (i >= 0) {
	if ((chr >= '0') && (chr <= '9'))
	  result += chr;
	else if (chr == '_')
	  result += chr;
      }
    }

    // too long?
    if (result.length() > m_MaxColumnLength)
      result = result.substring(0, m_MaxColumnLength);

    // convert name
    switch (m_ColumnNameConversion) {
      case AS_IS:
	// nothing
	break;
      case LOWER_CASE:
	result = result.toLowerCase();
	break;
      case UPPER_CASE:
	result = result.toUpperCase();
	break;
      default:
	throw new IllegalStateException("Unhandled conversion type: " + m_ColumnNameConversion);
    }

    return result;
  }

  /**
   * Generates the CREATE TABLE statement.
   *
   * @return		the SQL statement
   */
  public String getCreateStatement() {
    StringBuilder	result;
    int			i;
    String		stringType;

    stringType = m_StringColumnSQL.replace(PLACEHOLDER_MAX, "" + m_MaxStringLength);
    result      = new StringBuilder("CREATE TABLE " + m_Table + "(");
    for (i = 0; i < m_Sheet.getColumnCount(); i++) {
      if (i > 0)
	result.append(", ");
      switch (m_ContentTypes[i]) {
	case LONG:
	  result.append(m_ColumnNames[i]).append(" INTEGER");
	  break;
	case DOUBLE:
	  result.append(m_ColumnNames[i]).append(" DOUBLE PRECISION");
	  break;
	case DATE:
	  result.append(m_ColumnNames[i]).append(" DATE");
	  break;
	case DATETIME:
	case DATETIMEMSEC:
	  result.append(m_ColumnNames[i]).append(" TIMESTAMP");
	  break;
	case TIME:
	case TIMEMSEC:
	  result.append(m_ColumnNames[i]).append(" TIME");
	  break;
	case BOOLEAN:
	  result.append(m_ColumnNames[i]).append(" BOOLEAN");
	  break;
	default:
	  result.append(m_ColumnNames[i]).append(" ").append(stringType);
	  break;
      }
    }
    result.append(");");

    return result.toString();
  }

  /**
   * Generates a statement for an insert statement.
   *
   * @return		the insert statement
   */
  public String getInsertStatement() {
    StringBuilder	result;
    int			i;

    result = new StringBuilder("INSERT INTO " + m_Table + "(");
    for (i = 0; i < m_Sheet.getColumnCount(); i++) {
      if (i > 0)
	result.append(", ");
      result.append(m_ColumnNames[i]);
    }
    result.append(") VALUES (");
    for (i = 0; i < m_Sheet.getColumnCount(); i++) {
      if (i > 0)
	result.append(", ");
      result.append("?");
    }
    result.append(");");

    return result.toString();
  }

  /**
   * Returns the generated column names.
   *
   * @return		the column names
   */
  public String[] getColumnNames() {
    return m_ColumnNames;
  }

  /**
   * Returns the content types of the columns.
   *
   * @return		the content types
   */
  public ContentType[] getContentTypes() {
    return m_ContentTypes;
  }

  /**
   * Creates the table.
   *
   * @param sql		for executing queries
   * @return		null if everything OK, otherwise error message
   */
  public String createTable(SQL sql) {
    String		result;
    Boolean		rs;
    String		query;

    result = null;
    query  = getCreateStatement();
    if (isLoggingEnabled())
      getLogger().info("Query: " + query);
    try {
      rs = sql.execute(query);
      if (rs == null)
	result = "Failed to create table, check console!";
    }
    catch (Exception e) {
      result = Utils.handleException(this, "Failed to create table '" + m_Table + "' using: " + query, e);
    }

    return result;
  }

  /**
   * Writes the data to the table.
   *
   * @param sql		for performing the writing
   * @return		null if everything OK, otherwise error message
   */
  public String writeData(SQL sql) {
    String		result;
    StringBuilder	query;
    PreparedStatement stmt;
    int		i;
    Cell cell;
    int		type;
    int		count;
    String		str;

    result = null;

    query = new StringBuilder("INSERT INTO " + m_Table + "(");
    for (i = 0; i < m_Sheet.getColumnCount(); i++) {
      if (i > 0)
	query.append(", ");
      query.append(m_ColumnNames[i]);
    }
    query.append(") VALUES (");
    for (i = 0; i < m_Sheet.getColumnCount(); i++) {
      if (i > 0)
	query.append(", ");
      query.append("?");
    }
    query.append(");");

    try {
      stmt = sql.prepareStatement(query.toString());
    }
    catch (Exception e) {
      result = Utils.handleException(this, "Failed to prepare statement: " + query, e);
      stmt   = null;
    }

    if ((result == null) && (stmt != null)) {
      m_Stopped = false;
      count     = 0;
      for (Row row: m_Sheet.rows()) {
	if (m_Stopped)
	  break;
	count++;
	try {
	  for (i = 0; i < m_Sheet.getColumnCount(); i++) {
	    cell = row.getCell(i);
	    if ((cell == null) || cell.isMissing()) {
	      type = m_TypeMapper.contentTypeToSqlType(m_ContentTypes[i]);
	      stmt.setNull(i + 1, type);
	    }
	    else {
	      switch (m_ContentTypes[i]) {
		case DATE:
		  stmt.setDate(i + 1, new java.sql.Date(cell.toAnyDateType().getTime()));
		  break;
		case DATETIME:
		case DATETIMEMSEC:
		  stmt.setTimestamp(i + 1, new java.sql.Timestamp(cell.toAnyDateType().getTime()));
		  break;
		case TIME:
		case TIMEMSEC:
		  stmt.setTime(i + 1, new java.sql.Time(cell.toAnyDateType().getTime()));
		  break;
		case DOUBLE:
		  stmt.setDouble(i + 1, cell.toDouble());
		  break;
		case LONG:
		  stmt.setInt(i + 1, cell.toLong().intValue());
		  break;
		case BOOLEAN:
		  stmt.setBoolean(i + 1, cell.toBoolean());
		  break;
		default:
		  str = cell.getContent();
		  if (str.length() > m_MaxStringLength)
		    str = str.substring(0, m_MaxStringLength);
		  stmt.setString(i + 1, str);
		  break;
	      }
	    }
	  }
	  if (m_BatchSize > 1) {
	    stmt.addBatch();
	    if ((count % m_BatchSize == 0) || (count == m_Sheet.getRowCount())) {
	      stmt.executeBatch();
	    }
	  }
	  else {
	    stmt.execute();
	  }
	}
	catch (Exception e) {
	  result = Utils.handleException(this, "Failed to insert data: " + row + "\nusing: " + stmt, e);
	  break;
	}
	if (count % 1000 == 0) {
	  getLogger().info(count + " rows processed");
	  System.out.println(count + " rows processed");
	}
      }
    }

    SQL.close(stmt);

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
  }
}
