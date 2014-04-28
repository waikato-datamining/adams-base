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
 * TableExists.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.db.SQL;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Checks whether table(s) exist that match the given regular expression.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression used for matching the table names.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TableExists
  extends AbstractBooleanDatabaseCondition {

  /** for serialization. */
  private static final long serialVersionUID = -338472091205326476L;
  
  /** the regular expression for the table name to match. */
  protected BaseRegExp m_RegExp;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether table(s) exist that match the given regular expression.";
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
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "regExp", m_RegExp);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		Unknown
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		true if the condition evaluates to 'true'
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    boolean		result;
    Connection		conn;
    DatabaseMetaData	metadata;
    ResultSet		rs;
    List<String>	tables;

    result = false;
    
    conn = getDatabaseConnection().getConnection(false);
    if (conn == null) {
      getLogger().severe("Failed to obtain database connection??");
      return result;
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
	if (m_RegExp.isMatch(table)) {
	  result = true;
	  if (isLoggingEnabled())
	    getLogger().info("Table matches '" + m_RegExp + "': " + table);
	  break;
	}
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to check for tables:", e);
    }
    
    return result;
  }
}
