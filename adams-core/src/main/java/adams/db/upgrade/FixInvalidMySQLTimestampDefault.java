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
 * FixInvalidMySQLTimestampDefault.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.db.upgrade;

import adams.core.Constants;
import adams.db.JDBC;
import adams.db.SQLUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;

/**
 * Replaces invalid MySQL default values of TIMESTAMP columns.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FixInvalidMySQLTimestampDefault
  extends AbstractTableUpgrade {

  private static final long serialVersionUID = -4959359509457015794L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Replaces invalid MySQL default values of TIMESTAMP columns, like '0000-00-00 00:00:00', with '" + Constants.TIMESTAMP_DEFAULT_MYSQL + "'";
  }

  /**
   * Performs the actual upgrade.
   */
  @Override
  protected void doUpgrade() {
    Connection		conn;
    DatabaseMetaData	meta;
    ResultSet		rs;
    String		catalog;
    String		tableName;
    String		columnName;
    String 		typeName;
    String 		columnDef;
    String		query;
    Statement		stmt;

    if (!JDBC.isMySQL(m_Connection)) {
      getLogger().severe("Not a MySQL database, skipping: " + m_Connection.getURL());
      return;
    }

    try {
      conn    = m_Connection.getConnection(true);
      catalog = conn.getCatalog();
      meta    = conn.getMetaData();
      rs      = meta.getColumns(catalog, null, null, null);
      while (rs.next()) {
	tableName  = rs.getString("TABLE_NAME");
	columnName = rs.getString("COLUMN_NAME");
	typeName   = rs.getString("TYPE_NAME");
	columnDef  = rs.getString("COLUMN_DEF");
	if ((typeName == null) || (columnDef == null))
	  continue;
	if (!typeName.equalsIgnoreCase("TIMESTAMP"))
	  continue;
	if (columnDef.compareTo(Constants.TIMESTAMP_DEFAULT_MYSQL) < 0) {
	  getLogger().info("Invalid default (catalog/table/column/default): " + catalog + "/" + tableName + "/" + columnName + "/" + columnDef);
	  try {
	    query = "ALTER TABLE `" + tableName + "` CHANGE COLUMN `" + columnName + "` `" + columnName + "` TIMESTAMP NOT NULL DEFAULT '" + Constants.TIMESTAMP_DEFAULT_MYSQL + "';";
	    stmt  = conn.createStatement();
	    stmt.execute(query);
	    SQLUtils.close(stmt);
	    getLogger().info("Updated default (catalog/table/column): " + catalog + "/" + tableName + "/" + columnName);
	  }
	  catch (Exception e) {
	    getLogger().log(Level.SEVERE, "Failed to fix invalid timestamp default for (catalog/table/column): " + catalog + "/" + tableName + "/" + columnName, e);
	  }
	}
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to fix invalid timestamp defaults!", e);
    }
  }
}
