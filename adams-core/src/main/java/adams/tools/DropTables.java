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
 * DropTables.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.tools;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import adams.core.base.BaseRegExp;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.SQL;

/**
 <!-- globalinfo-start -->
 * Drops all tables that match a regular expression (matching sense can be inverted).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-regexp &lt;java.lang.String&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression used for matching the table names.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 * <pre>-invert (property: invert)
 * &nbsp;&nbsp;&nbsp;If set to true, then the matching sense is inverted.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DropTables
  extends AbstractDatabaseTool {

  /** for serialization. */
  private static final long serialVersionUID = 5980651808577627734L;

  /** the regular expression to match. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching sense. */
  protected boolean m_Invert;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Drops all tables that match a regular expression (matching sense "
      + "can be inverted).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "invert", "invert",
	    false);
  }

  /**
   * Returns the default database connection.
   *
   * @return		the database connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Sets the regular expression to match the table names against.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the table names against.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression used for matching the table names.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	true if inverting matching sense
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if matching sense is inverted
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If set to true, then the matching sense is inverted.";
  }

  /**
   * Attempt to load the file and save to db.
   * Exit java upon failure
   */
  @Override
  protected void doRun() {
    Connection		conn;
    DatabaseMetaData	metadata;
    ResultSet		rs;
    List<String>	tables;

    conn = getDatabaseConnection().getConnection(false);
    if (conn == null) {
      getLogger().severe("Failed to obtain database connection??");
      return;
    }

    try {
      // get metadata
      metadata = conn.getMetaData();

      // determine table names
      rs       = null;
      tables   = new ArrayList<String>();
      try {
	rs = metadata.getTables(null, null, null, new String[]{"TABLE"});
	while (rs.next())
	  tables.add(rs.getString("TABLE_NAME"));
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to retrieve list of tables:", e);
      }
      finally {
	if (rs != null)
	  SQL.closeAll(rs);
      }
      if (isLoggingEnabled())
	getLogger().info("Tables found: " + tables);

      // drop tables
      for (String table: tables) {
	if (m_Invert) {
	  if (m_RegExp.isMatch(table))
	    continue;
	}
	else {
	  if (!m_RegExp.isMatch(table))
	    continue;
	}
	getLogger().info("Dropping table '" + table + "': " + SQL.getSingleton(getDatabaseConnection()).drop(table));

	if (m_Stopped)
	  break;
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to drop tables:", e);
    }
  }
}
