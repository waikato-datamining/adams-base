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
 * MetaDataUtils.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.db;

import adams.core.MessageCollection;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.sql.AbstractTypeMapper;
import adams.data.spreadsheet.sql.Reader;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.logging.Level;

/**
 * Utility class for database meta-data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MetaDataUtils {

  /** for logging. */
  protected static Logger LOGGER = LoggingHelper.getLogger(MetaDataUtils.class);

  public static String[] JDBC_VALUES = new String[]{
    "allProceduresAreCallable",
    "allTablesAreSelectable",
    "autoCommitFailureClosesAllResultSets",
    "dataDefinitionCausesTransactionCommit",
    "dataDefinitionIgnoredInTransactions",
    "doesMaxRowSizeIncludeBlobs",
    "generatedKeyAlwaysReturned",
    "getCatalogSeparator",
    "getCatalogTerm",
    "getDatabaseMajorVersion",
    "getDatabaseMinorVersion",
    "getDatabaseProductName",
    "getDatabaseProductVersion",
    "getDefaultTransactionIsolation",
    "getDriverMajorVersion",
    "getDriverMinorVersion",
    "getDriverName",
    "getDriverVersion",
    "getExtraNameCharacters",
    "getIdentifierQuoteString",
    "getJDBCMajorVersion",
    "getJDBCMinorVersion",
    "getMaxBinaryLiteralLength",
    "getMaxCatalogNameLength",
    "getMaxCharLiteralLength",
    "getMaxColumnNameLength",
    "getMaxColumnsInGroupBy",
    "getMaxColumnsInIndex",
    "getMaxColumnsInOrderBy",
    "getMaxColumnsInSelect",
    "getMaxColumnsInTable",
    "getMaxConnections",
    "getMaxCursorNameLength",
    "getMaxIndexLength",
    "getMaxLogicalLobSize",
    "getMaxProcedureNameLength",
    "getMaxRowSize",
    "getMaxSchemaNameLength",
    "getMaxStatementLength",
    "getMaxStatements",
    "getMaxTableNameLength",
    "getMaxTablesInSelect",
    "getMaxUserNameLength",
    "getNumericFunctions",
    "getProcedureTerm",
    "getResultSetHoldability",
    "getRowIdLifetime",
    "getSchemaTerm",
    "getSearchStringEscape",
    "getSQLKeywords",
    "getSQLStateType",
    "getStringFunctions",
    "getSystemFunctions",
    "getTimeDateFunctions",
    "getURL",
    "getUserName",
    "isCatalogAtStart",
    "isReadOnly",
    "locatorsUpdateCopy",
    "nullPlusNonNullIsNull",
    "nullsAreSortedAtEnd",
    "nullsAreSortedAtStart",
    "nullsAreSortedHigh",
    "nullsAreSortedLow",
    "storesLowerCaseIdentifiers",
    "storesLowerCaseQuotedIdentifiers",
    "storesMixedCaseIdentifiers",
    "storesMixedCaseQuotedIdentifiers",
    "storesUpperCaseIdentifiers",
    "storesUpperCaseQuotedIdentifiers",
    "supportsAlterTableWithAddColumn",
    "supportsAlterTableWithDropColumn",
    "supportsANSI92EntryLevelSQL",
    "supportsANSI92FullSQL",
    "supportsANSI92IntermediateSQL",
    "supportsBatchUpdates",
    "supportsCatalogsInDataManipulation",
    "supportsCatalogsInIndexDefinitions",
    "supportsCatalogsInPrivilegeDefinitions",
    "supportsCatalogsInProcedureCalls",
    "supportsCatalogsInTableDefinitions",
    "supportsColumnAliasing",
    "supportsConvert",
    "supportsCoreSQLGrammar",
    "supportsCorrelatedSubqueries",
    "supportsDataDefinitionAndDataManipulationTransactions",
    "supportsDataManipulationTransactionsOnly",
    "supportsDifferentTableCorrelationNames",
    "supportsExpressionsInOrderBy",
    "supportsExtendedSQLGrammar",
    "supportsFullOuterJoins",
    "supportsGetGeneratedKeys",
    "supportsGroupBy",
    "supportsGroupByBeyondSelect",
    "supportsGroupByUnrelated",
    "supportsIntegrityEnhancementFacility",
    "supportsLikeEscapeClause",
    "supportsLimitedOuterJoins",
    "supportsMinimumSQLGrammar",
    "supportsMixedCaseIdentifiers",
    "supportsMixedCaseQuotedIdentifiers",
    "supportsMultipleOpenResults",
    "supportsMultipleResultSets",
    "supportsMultipleTransactions",
    "supportsNamedParameters",
    "supportsNonNullableColumns",
    "supportsOpenCursorsAcrossCommit",
    "supportsOpenCursorsAcrossRollback",
    "supportsOpenStatementsAcrossCommit",
    "supportsOpenStatementsAcrossRollback",
    "supportsOrderByUnrelated",
    "supportsOuterJoins",
    "supportsPositionedDelete",
    "supportsPositionedUpdate",
    "supportsRefCursors",
    "supportsSavepoints",
    "supportsSchemasInDataManipulation",
    "supportsSchemasInIndexDefinitions",
    "supportsSchemasInPrivilegeDefinitions",
    "supportsSchemasInProcedureCalls",
    "supportsSchemasInTableDefinitions",
    "supportsSelectForUpdate",
    "supportsStatementPooling",
    "supportsStoredFunctionsUsingCallSyntax",
    "supportsStoredProcedures",
    "supportsSubqueriesInComparisons",
    "supportsSubqueriesInExists",
    "supportsSubqueriesInIns",
    "supportsSubqueriesInQuantifieds",
    "supportsTableCorrelationNames",
    "supportsTransactions",
    "supportsUnion",
    "supportsUnionAll",
    "usesLocalFilePerTable",
    "usesLocalFiles",
  };

  /**
   * Adds a row to the sheet with the given key and value.
   *
   * @param sheet	the sheet to add the value to
   * @param key		the key of the value
   * @param value	the value to add
   */
  protected static void addRow(SpreadSheet sheet, String key, Object value) {
    Row row;

    row = sheet.addRow();
    row.addCell("K").setContentAsString(key);
    row.addCell("V").setNative(value);
  }

  /**
   * Adds a row to the sheet with the given key. Obtains the value using
   * the associated method name in the meta-data
   *
   * @param metadata	the meta-data to use
   * @param sheet	the sheet to add the value to
   * @param key		the key of the value
   */
  protected static void addRow(DatabaseMetaData metadata, SpreadSheet sheet, String key) {
    Method method;

    try {
      method = metadata.getClass().getMethod(key, new Class[0]);
      if (key.startsWith("get"))
	key = key.substring(3);
      key = key.substring(0, 1).toUpperCase() + key.substring(1);
      addRow(sheet, key, method.invoke(metadata, new Object[0]));
    }
    catch (Exception e) {
      LOGGER.log(Level.WARNING, "Failed to retrieve value for: " + key, e);
    }
  }

  /**
   * Returns the metadata types that require a table.
   *
   * @return		the types
   */
  public static MetaDataType[] typesRequireTable() {
    return new MetaDataType[]{
      MetaDataType.COLUMN_PRIVILEGES,
      MetaDataType.EXPORTED_KEYS,
      MetaDataType.IMPORTED_KEYS,
      MetaDataType.INDEX_INFO,
      MetaDataType.PRIMARY_KEYS,
      MetaDataType.VERSION_COLUMNS
    };
  }

  /**
   * Generates spreadsheet with the metadata.
   *
   * @param conn 	the database connection to use
   * @param mapper	the mapper for types
   * @param type 	the type of metadata to return
   * @param table 	the table to use (only some metadata types, see @see)
   * @param errors 	for collecting error messages
   * @return		the metadata, null if failed to generate
   * @see #typesRequireTable()
   */
  public static SpreadSheet getMetaData(AbstractDatabaseConnection conn, AbstractTypeMapper mapper, MetaDataType type, String table, MessageCollection errors) {
    SpreadSheet 	result;
    ResultSet 		rs;
    DatabaseMetaData	metadata;
    Connection		dbconn;
    Row			row;
    Reader 		reader;
    String		db;

    result = null;
    rs     = null;
    try {
      dbconn   = conn.getConnection(false);
      metadata = dbconn.getMetaData();
      reader   = new Reader(mapper, DenseDataRow.class);
      switch (type) {
	case CONNECTION:
	  result = new DefaultSpreadSheet();
	  row = result.getHeaderRow();
	  row.addCell("K").setContentAsString("Key");
	  row.addCell("V").setContentAsString("Value");
	  db = conn.getURL().replaceAll(".*\\/", "");
	  if (db.contains("?"))
	    db = db.substring(0, db.indexOf("?"));
	  addRow(result, "URL", conn.getURL());
	  addRow(result, "Database", db);
	  addRow(result, "User", conn.getUser());
	  addRow(result, "Password", conn.getPassword().getValue());
	  break;
	case BASIC:
	  result = new DefaultSpreadSheet();
	  row = result.getHeaderRow();
	  row.addCell("K").setContentAsString("Key");
	  row.addCell("V").setContentAsString("Value");
	  for (String value: MetaDataUtils.JDBC_VALUES)
	    addRow(metadata, result, value);
	  break;
	case ATTRIBUTES:
	  rs    = metadata.getAttributes(dbconn.getCatalog(), null, "%", null);
	  result = reader.read(rs);
	  break;
	case CATALOGS:
	  rs    = metadata.getCatalogs();
	  result = reader.read(rs);
	  break;
	case CLIENT_INFO_PROPERTIES:
	  rs    = metadata.getClientInfoProperties();
	  result = reader.read(rs);
	  break;
	case COLUMN_PRIVILEGES:
	  rs    = metadata.getColumnPrivileges(dbconn.getCatalog(), null, table, "%");
	  result = reader.read(rs);
	  break;
	case COLUMNS:
	  rs    = metadata.getColumns(dbconn.getCatalog(), null, "%", "%");
	  result = reader.read(rs);
	  break;
	case EXPORTED_KEYS:
	  rs    = metadata.getExportedKeys(dbconn.getCatalog(), null, table);
	  result = reader.read(rs);
	  break;
	case FUNCTION_COLUMNS:
	  rs    = metadata.getFunctionColumns(dbconn.getCatalog(), null, "%", "%");
	  result = reader.read(rs);
	  break;
	case FUNCTIONS:
	  rs    = metadata.getFunctions(dbconn.getCatalog(), null, "%");
	  result = reader.read(rs);
	  break;
	case IMPORTED_KEYS:
	  rs    = metadata.getImportedKeys(dbconn.getCatalog(), null, table);
	  result = reader.read(rs);
	  break;
	case INDEX_INFO:
	  rs    = metadata.getIndexInfo(dbconn.getCatalog(), null, table, false, false);
	  result = reader.read(rs);
	  break;
	case PRIMARY_KEYS:
	  rs    = metadata.getPrimaryKeys(dbconn.getCatalog(), null, table);
	  result = reader.read(rs);
	  break;
	case PROCEDURE_COLUMNS:
	  rs    = metadata.getProcedureColumns(dbconn.getCatalog(), null, "%", "%");
	  result = reader.read(rs);
	  break;
	case PROCEDURES:
	  rs    = metadata.getProcedures(dbconn.getCatalog(), null, "%");
	  result = reader.read(rs);
	  break;
	case PSEUDO_COLUMNS:
	  rs    = metadata.getPseudoColumns(dbconn.getCatalog(), null, "%", "%");
	  result = reader.read(rs);
	  break;
	case SCHEMAS:
	  rs    = metadata.getSchemas();
	  result = reader.read(rs);
	  break;
	case SUPER_TABLES:
	  rs    = metadata.getSuperTables(dbconn.getCatalog(), null, "%");
	  result = reader.read(rs);
	  break;
	case SUPER_TYPES:
	  rs    = metadata.getSuperTypes(dbconn.getCatalog(), null, "%");
	  result = reader.read(rs);
	  break;
	case TABLE_TYPES:
	  rs    = metadata.getTableTypes();
	  result = reader.read(rs);
	  break;
	case TABLES:
	  rs    = metadata.getTables(dbconn.getCatalog(), null, "%", null);
	  result = reader.read(rs);
	  break;
	case TYPE_INFO:
	  rs    = metadata.getTypeInfo();
	  result = reader.read(rs);
	  break;
	case USER_DEFINED_TYPES:
	  rs    = metadata.getUDTs(dbconn.getCatalog(), null, "%", null);
	  result = reader.read(rs);
	  break;
	case VERSION_COLUMNS:
	  rs    = metadata.getVersionColumns(dbconn.getCatalog(), null, table);
	  result = reader.read(rs);
	  break;
	default:
	  throw new IllegalStateException("Unhandled meta-data type: " + type);
      }
    }
    catch (Exception e) {
      errors.add("Failed to obtain database meta-data!", e);
    }

    SQLUtils.closeAll(rs);

    if (!errors.isEmpty())
      return null;

    return result;
  }
}
