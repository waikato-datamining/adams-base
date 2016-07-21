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
 * ExecSQL.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.db.SQL;
import adams.db.SQLStatement;
import adams.flow.core.ActorUtils;

import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Executes a SQL statement, which does not return a result set, like INSERT, UPDATE, DELETE, CREATE.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ExecSQL
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-sql &lt;adams.db.SQLStatement&gt; (property: SQL)
 * &nbsp;&nbsp;&nbsp;The SQL statement to run that generates the IDs.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-dry-run &lt;boolean&gt; (property: dryRun)
 * &nbsp;&nbsp;&nbsp;If enabled, the SQL statement is merely logged but not executed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExecSQL
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = -2766505525494708760L;

  /** the SQL statement to execute. */
  protected SQLStatement m_SQL;

  /** whether to simulate the SQL statement. */
  protected boolean m_DryRun;
  
  /** the database connection. */
  protected adams.db.AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Executes a SQL statement, which does not return a result set, like "
      + "INSERT, UPDATE, DELETE, CREATE.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "sql", "SQL",
	    new SQLStatement(""));

    m_OptionManager.add(
	    "dry-run", "dryRun",
	    false);
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
    String	value;
    
    result = QuickInfoHelper.toString(this, "SQL", Shortening.shortenEnd(m_SQL.getValue().replaceAll("\\s", " ").replaceAll("[ ]+", " "), 50));
    value  = QuickInfoHelper.toString(this, "dryRun", m_DryRun, "dry run", (result != null ? ", " : ""));
    if (value != null) {
      if (result == null)
	result = value;
      else
	result += value;
    }
    
    return result;
  }

  /**
   * Sets the SQL statement to run.
   *
   * @param value	the statement
   */
  public void setSQL(SQLStatement value) {
    m_SQL = value;
    reset();
  }

  /**
   * Returns the SQL statement to run.
   *
   * @return 		the statement
   */
  public SQLStatement getSQL() {
    return m_SQL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String SQLTipText() {
    return "The SQL statement to run that generates the IDs.";
  }

  /**
   * Sets whether to merely simulate the statement.
   *
   * @param value	true if to simulate the statement
   */
  public void setDryRun(boolean value) {
    m_DryRun = value;
    reset();
  }

  /**
   * Returns whether the statement is merely simulated.
   *
   * @return 		true if only simulated
   */
  public boolean getDryRun() {
    return m_DryRun;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String dryRunTipText() {
    return "If enabled, the SQL statement is merely logged but not executed.";
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
    String	result;
    SQL		sql;
    String	query;

    result = null;

    if (m_DatabaseConnection == null)
      m_DatabaseConnection = getDatabaseConnection();

    query  = m_SQL.getValue();
    // replace variables
    query  = getVariables().expand(query);

    try {
      sql = new SQL(getDatabaseConnection());
      if (isLoggingEnabled() || m_DryRun) {
	if (m_DryRun)
	  getLogger().setLevel(Level.INFO);
	getLogger().info("Query: " + query);
	if (m_DryRun)
	  getLogger().setLevel(getLoggingLevel().getLevel());
      }
      if (!m_DryRun) {
	if (sql.execute(query) == null)
	  result = "Failed to execute query: " + query;
      }
    }
    catch (Exception e) {
      result = handleException("Failed to execute query '" + query + "': ", e);
    }

    return result;
  }
}
