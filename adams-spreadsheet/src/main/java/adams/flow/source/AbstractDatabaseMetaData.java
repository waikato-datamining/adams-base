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
 * AbstractDatabaseMetaData.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.sql.Reader;
import adams.flow.core.Token;

import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.logging.Level;

/**
 * Ancestor for sources that output the database meta-data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6790 $
 */
public abstract class AbstractDatabaseMetaData
  extends AbstractSimpleSource {

  /** for serialization. */
  private static final long serialVersionUID = -8462709950859959951L;

  /**
   * The types of available meta-data.
   */
  public enum MetaDataType {
    BASIC,
    ATTRIBUTES,
    CATALOGS,
    CLIENT_INFO_PROPERTIES,
    COLUMN_PRIVILEGES,
    COLUMNS,
    EXPORTED_KEYS,
    FUNCTION_COLUMNS,
    FUNCTIONS,
    IMPORTED_KEYS,
    INDEX_INFO,
    PRIMARY_KEYS,
    PROCEDURE_COLUMNS,
    PROCEDURES,
    PSEUDO_COLUMNS,
    SCHEMAS,
    SUPER_TABLES,
    SUPER_TYPES,
    TABLES,
    TABLE_TYPES,
    TYPE_INFO,
    USER_DEFINED_TYPES,
    VERSION_COLUMNS
  }

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

  /** the type of meta-data to return. */
  protected MetaDataType m_MetaDataType;

  /** the table to retrieve the information for. */
  protected String m_Table;

  /** the database connection. */
  protected adams.db.AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the meta-data of the current database connection.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "meta-data-type", "metaDataType",
	    MetaDataType.BASIC);

    m_OptionManager.add(
	    "table", "table",
	    "");
  }

  /**
   * Sets the type of meta-data to retrieve.
   *
   * @param value	the type
   */
  public void setMetaDataType(MetaDataType value) {
    m_MetaDataType = value;
    reset();
  }

  /**
   * Returns the type of meta-data to retrieve.
   *
   * @return		the type
   */
  public MetaDataType getMetaDataType() {
    return m_MetaDataType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataTypeTipText() {
    return "The type of meta-data to retrieve.";
  }

  /**
   * Sets the table to retrieve the information for.
   *
   * @param value	the table
   */
  public void setTable(String value) {
    m_Table = value;
    reset();
  }

  /**
   * Returns the table to retrieve the information for.
   *
   * @return		the table
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
    MetaDataType[] types = new MetaDataType[]{
      MetaDataType.COLUMN_PRIVILEGES,
      MetaDataType.EXPORTED_KEYS,
      MetaDataType.IMPORTED_KEYS,
      MetaDataType.INDEX_INFO,
      MetaDataType.PRIMARY_KEYS,
      MetaDataType.VERSION_COLUMNS
    };
    return "The table to retrieve the information for (" + Utils.flatten(types, ", ") + ").";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "metaDataType", m_MetaDataType, "type: ");
    result += QuickInfoHelper.toString(this, "table", (m_Table.isEmpty() ? "-none-" : m_Table), ", table: ");

    return result;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DatabaseConnection = null;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  protected abstract adams.db.AbstractDatabaseConnection getDatabaseConnection();

  /**
   * Configures the database connection if necessary.
   *
   * @return		null if successful, otherwise error message
   */
  protected String setUpDatabaseConnection() {
    String	result;

    result = null;

    if (m_DatabaseConnection == null) {
      m_DatabaseConnection = getDatabaseConnection();
      if (m_DatabaseConnection == null)
	result = "No database connection available!";
    }

    return result;
  }

  /**
   * Adds a row to the sheet with the given key and value.
   *
   * @param sheet	the sheet to add the value to
   * @param key		the key of the value
   * @param value	the value to add
   */
  protected void addRow(SpreadSheet sheet, String key, Object value) {
    Row		row;

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
  protected void addRow(DatabaseMetaData metadata, SpreadSheet sheet, String key) {
    Method	method;

    try {
      method = metadata.getClass().getMethod(key, new Class[0]);
      if (key.startsWith("get"))
	key = key.substring(3);
      key = key.substring(0, 1).toUpperCase() + key.substring(1);
      addRow(sheet, key, method.invoke(metadata, new Object[0]));
    }
    catch (Exception e) {
      getLogger().log(Level.WARNING, "Failed to retrieve value for: " + key, e);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    ResultSet		rs;
    SpreadSheet		sheet;
    DatabaseMetaData	metadata;
    Row			row;
    Reader		reader;

    result = setUpDatabaseConnection();

    if (result == null) {
      rs    = null;
      sheet = null;
      try {
	metadata = m_DatabaseConnection.getConnection(false).getMetaData();
	reader   = new Reader(DenseDataRow.class);
	switch (m_MetaDataType) {
	  case BASIC:
	    sheet = new DefaultSpreadSheet();
	    row = sheet.getHeaderRow();
	    row.addCell("K").setContentAsString("Key");
	    row.addCell("V").setContentAsString("Value");
	    for (String value: JDBC_VALUES)
	      addRow(metadata, sheet, value);
	    break;
	  case ATTRIBUTES:
	    rs    = metadata.getAttributes(null, null, "%", null);
	    sheet = reader.read(rs);
	    break;
	  case CATALOGS:
	    rs    = metadata.getCatalogs();
	    sheet = reader.read(rs);
	    break;
	  case CLIENT_INFO_PROPERTIES:
	    rs    = metadata.getClientInfoProperties();
	    sheet = reader.read(rs);
	    break;
	  case COLUMN_PRIVILEGES:
	    rs    = metadata.getColumnPrivileges(null, null, m_Table, "%");
	    sheet = reader.read(rs);
	    break;
	  case COLUMNS:
	    rs    = metadata.getColumns(null, null, "%", "%");
	    sheet = reader.read(rs);
	    break;
	  case EXPORTED_KEYS:
	    rs    = metadata.getExportedKeys(null, null, m_Table);
	    sheet = reader.read(rs);
	    break;
	  case FUNCTION_COLUMNS:
	    rs    = metadata.getFunctionColumns(null, null, "%", "%");
	    sheet = reader.read(rs);
	    break;
	  case FUNCTIONS:
	    rs    = metadata.getFunctions(null, null, "%");
	    sheet = reader.read(rs);
	    break;
	  case IMPORTED_KEYS:
	    rs    = metadata.getImportedKeys(null, null, m_Table);
	    sheet = reader.read(rs);
	    break;
	  case INDEX_INFO:
	    rs    = metadata.getIndexInfo(null, null, m_Table, false, false);
	    sheet = reader.read(rs);
	    break;
	  case PRIMARY_KEYS:
	    rs    = metadata.getPrimaryKeys(null, null, m_Table);
	    sheet = reader.read(rs);
	    break;
	  case PROCEDURE_COLUMNS:
	    rs    = metadata.getProcedureColumns(null, null, "%", "%");
	    sheet = reader.read(rs);
	    break;
	  case PROCEDURES:
	    rs    = metadata.getProcedures(null, null, "%");
	    sheet = reader.read(rs);
	    break;
	  case PSEUDO_COLUMNS:
	    rs    = metadata.getPseudoColumns(null, null, "%", "%");
	    sheet = reader.read(rs);
	    break;
	  case SCHEMAS:
	    rs    = metadata.getSchemas();
	    sheet = reader.read(rs);
	    break;
	  case SUPER_TABLES:
	    rs    = metadata.getSuperTables(null, null, "%");
	    sheet = reader.read(rs);
	    break;
	  case SUPER_TYPES:
	    rs    = metadata.getSuperTypes(null, null, "%");
	    sheet = reader.read(rs);
	    break;
	  case TABLE_TYPES:
	    rs    = metadata.getTableTypes();
	    sheet = reader.read(rs);
	    break;
	  case TABLES:
	    rs    = metadata.getTables(null, null, "%", null);
	    sheet = reader.read(rs);
	    break;
	  case TYPE_INFO:
	    rs    = metadata.getTypeInfo();
	    sheet = reader.read(rs);
	    break;
	  case USER_DEFINED_TYPES:
	    rs    = metadata.getUDTs(null, null, "%", null);
	    sheet = reader.read(rs);
	    break;
	  case VERSION_COLUMNS:
	    rs    = metadata.getVersionColumns(null, null, m_Table);
	    sheet = reader.read(rs);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled meta-data type: " + m_MetaDataType);
	}
      }
      catch (Exception e) {
	result = handleException("Failed to obtain database meta-data!", e);
      }

      if (sheet != null)
	m_OutputToken = new Token(sheet);
    }

    return result;
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
