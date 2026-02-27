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
 * AbstractIndexedTable.java
 * Copyright (C) 2008-2026 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db;

import adams.core.ClassLister;
import adams.core.classmanager.ClassManager;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.db.indices.Index;
import adams.db.indices.IndexColumn;
import adams.db.indices.Indices;
import adams.db.types.ColumnType;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.logging.Level;

/**
 * Class to implement the features of a database table.
 * Subclass with column descriptions
 *
 * @author dale
 */
public abstract class AbstractIndexedTable
  extends AbstractTable {

  /** for serialization. */
  private static final long serialVersionUID = 2013793322024355971L;

  /** the debugging level. */
  private final static Logger LOGGER = LoggingHelper.getConsoleLogger(AbstractIndexedTable.class);

  /** has this object been initialised. */
  protected boolean m_init;

  /**
   * Constructor.
   *
   * @param dbcon	the database context this table is used in
   * @param tableName	the name of the table
   */
  protected AbstractIndexedTable(AbstractDatabaseConnection dbcon, String tableName) {
    super(dbcon, tableName);

    m_init = false;
  }

  /**
   * Implement this method to return required indices for this table.
   *
   * @return	Indices
   */
  abstract protected Indices getIndices();

  /**
   * Implement this method to return column names and data types.
   *
   * @return	Column Mapping
   */
  abstract protected ColumnMapping getColumnMapping();

  /**
   * Returns the SELECT part with all columns listed.
   *
   * @return	the SELECT part
   */
  public String getAllColumns() {
    StringBuilder	result;
    Enumeration<String>	enm;

    result = new StringBuilder();
    enm     = getColumnMapping().keys();
    while (enm.hasMoreElements()) {
      if (result.length() > 0)
	result.append(", ");
      result.append(enm.nextElement());
    }

    return result.toString();
  }

  /**
   * Determine if columns (name & datatype) of table match table specification.
   *
   * @param cm		the column mapping to use
   * @param print	whether to print error messages etc
   * @param tryAgain	whether to try again
   * @param addMissing	whether to add the missing columns (if possible)
   * @return		columns match?
   */
  protected boolean columnsMatchTest(ColumnMapping cm, boolean print, boolean tryAgain, boolean addMissing) {
    boolean 		result;
    Connection 		connection;
    SimpleResultSet 	rs;
    DatabaseMetaData 	meta;
    HashSet<String> 	columns;
    String 		cname;
    int 		type;
    int 		size;
    ColumnType 		columnType;
    ColumnType 		expectedColumn;
    Enumeration<String> keys;
    StringBuilder 	sql;

    result     = true;
    connection = getConnection(true);
    rs         = null;
    try {
      meta    = connection.getMetaData();
      rs      = new SimpleResultSet(meta.getColumns(connection.getCatalog(), null, updateTableName(meta, m_TableName), "%"));
      columns = new HashSet<>();
      while(rs.next()) {
	cname = rs.getString("COLUMN_NAME").toUpperCase();
	columns.add(cname);
	type = rs.getInt("DATA_TYPE");
	size = rs.getInt("COLUMN_SIZE");
	columnType = new ColumnType(type,size);
	expectedColumn = cm.getMapping(cname);
	if (expectedColumn == null) {
	  if (print)
	    getLogger().severe(
		"false because expectedColumn null for '" + cname + "' (" + getTableName() + ")");
	  result = false;
	}
	else if (!expectedColumn.equivalentTo(getDatabaseConnection(), columnType)) {
	  if (expectedColumn.isEncompassed(getDatabaseConnection(), columnType)) {
	    if (print && getDebug())
	      getLogger().warning(
		"column type different (but encompassed) for '" + cname + "': "
		  + expectedColumn.getCompareType(getDatabaseConnection())
		  + " < "
		  + columnType.getCompareType(getDatabaseConnection()) + " " + getTableName() + ")");
	  }
	  else {
	    if (print)
	      getLogger().severe(
		"false because column type different for '" + cname + "': "
		  + expectedColumn.getCompareType(getDatabaseConnection())
		  + " != "
		  + columnType.getCompareType(getDatabaseConnection()) + " " + getTableName() + ")");
	    result = false;
	  }
	}
      }

      // try adding missing columns
      if (columns.size() != cm.size()) {
	keys = cm.keys();
	while (keys.hasMoreElements()) {
	  cname = keys.nextElement();
	  if (columns.contains(cname))
	    continue;
	  if (!addMissing) {
	    result = false;
	    break;
	  }
	  columnType = cm.getMapping(cname);
	  sql = new StringBuilder("ALTER TABLE " + updateTableName(meta, m_TableName) + " ADD COLUMN ");
	  sql.append(quoteName(cname)).append(" ").append(columnType.getCreateType(getDatabaseConnection()));
	  try {
	    execute(sql.toString());
	  }
	  catch (Exception e) {
	    result = false;
	  }
	  if (print)
	    getLogger().severe("Adding column '" + cname + "' (" + getTableName() + "): " + result);
	  if (!result)
	    break;
	  columns.add(cname);
	}

	// still mismatch in column numbers? -> fail
	if (addMissing) {
	  if (columns.size() != cm.size()) {
	    if (print)
	      getLogger().severe(
		  "false because column count.columnCount=" + columns.size() + ", cmsize=" + cm.size() + " (" + getTableName() + ")");
	    result = false;
	  }
	}
      }
    }
    catch (SQLException e) {
      if (tryAgain) {
	SQLUtils.closeAll(rs);
	result = columnsMatchTest(cm, print, false, addMissing);
      }
    }
    catch (Exception e) {
      // ignored?
    }
    finally {
      SQLUtils.closeAll(rs);
    }
    return result;
  }

  /**
   * Determine if columns (name & datatype) of table match table specification.
   *
   * @param cm		the column mapping to use
   * @param print	whether to print error messages etc
   * @param addMissing	whether to add missing columns if possible
   * @return		columns match?
   */
  protected boolean columnsMatch(ColumnMapping cm, boolean print, boolean addMissing) {
    return columnsMatchTest(cm, print, true, addMissing);
  }

  /**
   * Initialise table. Create if necessary, else compare with spec.
   *
   * @return success?
   */
  @Override
  public synchronized boolean init() {
    boolean	result;

    // if exists, check columns match
    if (tableExists()) {
      result = columnsMatch(getColumnMapping(), true, true);
    }
    else {
      //attempt creation
      result = create();
      if (result)
	result = postCreate();
    }

    return result;
  }

  /**
   * Create the table in database according to column and index specs.
   *
   * @return	success?
   */
  protected boolean create() {
    StringBuilder 	sql;
    ColumnMapping 	cm;
    String 		cname;
    ColumnType 		type;
    Boolean 		retVal;
    int			i;
    Indices 		indices;
    Index 		index;
    int			j;
    IndexColumn 	ic;

    cm = getColumnMapping();
    sql = new StringBuilder("CREATE TABLE " + m_TableName + " (");
    for (Enumeration enum1 = cm.keys() ; enum1.hasMoreElements() ;) {
      cname = (String) enum1.nextElement();
      type = cm.getMapping(cname);
      sql.append(" ").append(quoteName(cname)).append(" ").append(type.getCreateType(getDatabaseConnection()));
      if (enum1.hasMoreElements()) {
	sql.append(",");
      }
      else {
	if (cm.hasPrimaryKey())
	  sql.append(", PRIMARY KEY(").append(quoteName(cm.getPrimaryKey())).append(")");
	sql.append(")");
      }
    }

    try {
      getLogger().info("Creating table: " + sql);
      retVal = execute(sql.toString());
      if ((retVal == null) || retVal)
	return false;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Error creating table: " + sql, e);
    }

    indices = getIndices();
    if (indices != null) {
      for (i = 0; i < indices.size(); i++) {
	sql = new StringBuilder("CREATE INDEX " + m_TableName + "_IND_" + i + " ON " + m_TableName + " (");
	index = indices.get(i);
	for (j = 0; j < index.size() - 1; j++) {
	  ic = index.get(j);
	  sql.append(" ").append(ic.toString(getDatabaseConnection())).append(",");
	}
	sql.append(" ").append(index.get(index.size() - 1).toString(getDatabaseConnection())).append(")");
	try {
	  getLogger().info("Creating indices: " + sql);
	  retVal = execute(sql.toString());
	  if ((retVal == null) || retVal)
	    return false;
	}
	catch(Exception e) {
	  getLogger().log(Level.SEVERE, "Error creating indices: " + sql, e);
	  return false;
	}
      }
    }
    return true;
  }

  /**
   * Optional post-create hook for filling the table with initial values.
   * The default implementation does nothing and just returns true.
   *
   * @return		true if successfully run
   */
  protected boolean postCreate() {
    return true;
  }

  /**
   * Returns true if this table holds data that satisfies 'condition'.
   *
   * @param condition  boolean SQL eg "JOBNO=100 AND SAMPLENO=2"
   * @return  true if condition holds for tablename
   */
  public boolean isThere(String condition) {
    try{
      SimpleResultSet rs = new SimpleResultSet(select("*", condition));
      if (!rs.next()) {
	SQLUtils.closeAll(rs);
	return(false);
      }
      else{
	SQLUtils.closeAll(rs);
	return(true);
      }
    }
    catch(Exception e) {
      return(false);
    }
  }

  /**
   * Update table.
   *
   * @param updateString 	comma separated updates. e.g weight='80',height=180
   * @param where		condition. e.g name='bob smith'
   * @return			number of rows affected
   * @throws Exception 		if something goes wrong
   */
  public int update(String updateString, String where) throws Exception{
    return update(updateString, getTableName(), where);
  }

  /**
   * Empty table.
   *
   * @return	success?
   */
  public boolean truncate() {
    return truncate(getTableName());
  }

  /**
   * Do a select on given columns for all data in table.
   *
   * @param columns	comma separated column list
   * @return 		resulset of data
   * @throws Exception 	if something goes wrong
   */
  public ResultSet select(String columns) throws Exception{
    return doSelect(false, columns, getTableName(), null);
  }

  /**
   * Do a select on given columns for all data in table, with condition.
   *
   * @param columns	columns to select
   * @param where	condition
   * @return		resultset of data
   * @throws Exception 	if something goes wrong
   */
  public ResultSet select(String columns, String where) throws Exception{
    return doSelect(false, columns, getTableName(), where);
  }

  /**
   * Do a select distinct on given columns for all data in table, with condition.
   *
   * @param columns	columns to select
   * @return		resultset of data
   * @throws Exception 	if something goes wrong
   */
  public ResultSet selectDistinct(String columns) throws Exception{
    return doSelect(true, columns, getTableName(), null);
  }

  /**
   * Do a select distinct on given columns for all data in table, with condition.
   *
   * @param columns	columns to select
   * @param where	condition
   * @return		resultset of data
   * @throws Exception 	if something goes wrong
   */
  public ResultSet selectDistinct(String columns, String where) throws Exception{
    return doSelect(true, columns, getTableName(), where);
  }

  /**
   * Returns a list with classnames of tables.
   *
   * @return		the table classnames
   */
  public static String[] getTables() {
    return ClassLister.getSingleton().getClassnames(AbstractIndexedTable.class);
  }

  /**
   * Initializes all tables.
   *
   * @param dbcon	the database context
   */
  public static void initTables(AbstractDatabaseConnection dbcon) {
    String[]	tables;
    int		i;
    Class	cls;
    Method	method;
    boolean	debug;
    boolean	enabled;

    debug  = getProperties().getBoolean("InitTables.Debug", false);
    tables = getTables();
    for (i = 0; i < tables.length; i++) {
      try {
	if (debug)
	  System.out.println("Initializing table: " + tables[i]);
	cls = ClassManager.getSingleton().forName(tables[i]);
	try {
	  method  = cls.getMethod("isEnabled", Class.class);
	  enabled = (Boolean) method.invoke(null, cls);
	  if (!enabled)
	    continue;
	}
	catch (Exception e) {
          LOGGER.log(Level.SEVERE, "Failed to check whether table '" + tables[i] + "' is enabled: ", e);
	}
	try {
	  method = cls.getMethod("initTable", AbstractDatabaseConnection.class);
	  method.invoke(null, dbcon);
	}
	catch (Exception e) {
          LOGGER.log(Level.SEVERE, "Failed to initialize table '" + tables[i] + "': ", e);
	}
      }
      catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error initializing table '" + tables[i] + "':", e);
      }
    }
  }
}
