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
 * DatabaseMetaData.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.db.AbstractDatabaseConnection;
import adams.flow.core.ActorUtils;

/**
 <!-- globalinfo-start -->
 * Outputs the meta-data of the current database connection.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
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
 * &nbsp;&nbsp;&nbsp;default: DatabaseMetaData
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
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-meta-data-type &lt;BASIC|ATTRIBUTES|CATALOGS|CLIENT_INFO_PROPERTIES|COLUMN_PRIVILEGES|COLUMNS|EXPORTED_KEYS|FUNCTION_COLUMNS|FUNCTIONS|IMPORTED_KEYS|INDEX_INFO|PRIMARY_KEYS|PROCEDURE_COLUMNS|PROCEDURES|PSEUDO_COLUMNS|SCHEMAS|SUPER_TABLES|SUPER_TYPES|TABLES|TABLE_TYPES|TYPE_INFO|USER_DEFINED_TYPES|VERSION_COLUMNS&gt; (property: metaDataType)
 * &nbsp;&nbsp;&nbsp;The type of meta-data to retrieve.
 * &nbsp;&nbsp;&nbsp;default: BASIC
 * </pre>
 * 
 * <pre>-table &lt;java.lang.String&gt; (property: table)
 * &nbsp;&nbsp;&nbsp;The table to retrieve the information for (COLUMN_PRIVILEGES, EXPORTED_KEYS,
 * &nbsp;&nbsp;&nbsp; IMPORTED_KEYS, INDEX_INFO, PRIMARY_KEYS, VERSION_COLUMNS).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatabaseMetaData
  extends AbstractDatabaseMetaData {

  private static final long serialVersionUID = -6184309574477452862L;

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  @Override
  protected AbstractDatabaseConnection getDatabaseConnection() {
    return ActorUtils.getDatabaseConnection(
      this,
      adams.flow.standalone.DatabaseConnection.class,
      adams.db.DatabaseConnection.getSingleton());
  }
}
