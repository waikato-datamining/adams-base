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
 * MySQL.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.opt.genetic.setupupload;

import adams.core.MessageCollection;
import adams.core.Shortening;
import adams.core.logging.LoggingHelper;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.JDBC;
import adams.db.SQLF;
import adams.db.SQLIntf;
import adams.db.SQLUtils;
import adams.flow.core.ActorUtils;
import adams.opt.genetic.AbstractGeneticAlgorithm;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Map;

/**
 * Stores the setup information in the specified MySQL table.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MySQL
  extends AbstractSetupUpload {

  private static final long serialVersionUID = 1825847990988418348L;

  public static final String KEY_SUCCESSFUL = "successful";

  /** the table to store the setups in. */
  protected String m_Table;

  /** the database connection. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Stores the setup information in the specified MySQL table.\n"
	+ "Uses the database available through the current flow context.\n"
	+ "If the table is not present, it gets automatically created.\n"
	+ "On completion of the algorithm run, a row with the key '" + KEY_SUCCESSFUL + "' "
	+ "gets inserted with an associated value of 'true' or 'false' depending on "
	+ "whether the algorithm run was successful. The fitness value is 'null' in this case.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "table", "table",
      "");
  }

  /**
   * Sets the table name to use.
   *
   * @param value 	the name
   */
  public void setTable(String value) {
    m_Table = value;
    reset();
  }

  /**
   * Returns the table name to use.
   *
   * @return 		the name
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
    return "The name of the table to receive the setups.";
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
   * Returns whether flow context is required.
   *
   * @return		true if required
   */
  @Override
  public boolean requiresFlowContext() {
    return true;
  }

  /**
   * Before Starting the uploads, ie the genetic algorithm run.
   *
   * @param algorithm	the algorithm initiating the run
   */
  protected void doStart(AbstractGeneticAlgorithm algorithm) {
  }

  /**
   * Initializes the database connection, if necessary.
   *
   * @return		null if successful or already initialized, otherwise error message
   */
  protected String initDatabase() {
    if (m_DatabaseConnection != null)
      return null;
    m_DatabaseConnection = ActorUtils.getDatabaseConnection(
      m_FlowContext, adams.flow.standalone.DatabaseConnectionProvider.class, new DatabaseConnection());
    if (m_DatabaseConnection == null)
      return "Failed to initialize database connection!";
    if (!JDBC.isMySQL(m_DatabaseConnection))
      return "Not a MySQL connection!";
    return null;
  }

  /**
   * Initializes the table.
   *
   * @return		null if successful or already initialized, otherwise error message
   */
  protected String initTable() {
    StringBuilder	create;
    SQLIntf 		sql;
    Boolean 		resultSet;

    sql = SQLF.getSingleton(m_DatabaseConnection);
    if (sql.tableExists(m_Table))
      return null;

    create = new StringBuilder()
      .append("CREATE TABLE ").append(m_Table).append(" (\n")
      .append("  ").append("experiment VARCHAR(255) NOT NULL").append(",\n")
      .append("  ").append("measure VARCHAR(255) NOT NULL").append(",\n")
      .append("  ").append("fitness DOUBLE").append(",\n")
      .append("  ").append("name VARCHAR(255) NOT NULL").append(",\n")
      .append("  ").append("value LONGTEXT").append(",\n")
      .append("  ").append("INDEX idx_experiment(experiment)").append(",\n")
      .append("  ").append("INDEX idx_measure(measure)").append(",\n")
      .append("  ").append("INDEX idx_fitness(fitness)").append(",\n")
      .append("  ").append("INDEX idx_name(name)").append("\n")
      .append(")");
    if (isLoggingEnabled())
      getLogger().info(create.toString());
    try {
      resultSet = sql.execute(create.toString());
      if ((resultSet == null) || resultSet)
	return "Failed to create table: " + create;
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed execute statement: " + create, e);
    }

    return null;
  }

  /**
   * Uploads the setup.
   *
   * @param setup	the setup data to upload
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doUpload(Map<String, Object> setup) {
    String		result;
    MessageCollection	errors;
    String		sql;
    PreparedStatement	stmt;
    String		value;

    result = initDatabase();

    if (result == null)
      result = initTable();

    // prepare statement
    stmt = null;
    if (result == null) {
      sql = "INSERT INTO " + m_Table + " (experiment, measure, fitness, name, value) "
	+ " VALUES(?, ?, ?, ?, ?)";
      try {
	stmt = m_DatabaseConnection.getConnection(true).prepareStatement(sql);
      }
      catch (Exception e) {
	result = LoggingHelper.handleException(this, "Failed to prepare statement: " + sql, e);
      }
    }

    // store setup
    if (stmt != null) {
      errors = new MessageCollection();
      for (String key: setup.keySet()) {
	if (key.equals(KEY_MEASURE) || key.equals(KEY_FITNESS))
	  continue;
	value = "" + setup.get(key);
	if (isLoggingEnabled())
	  getLogger().info("Inserting name=" + key + ", value=" + Shortening.shortenEnd(value, 30));
	try {
	  stmt.setString(1, m_Experiment);
	  stmt.setString(2, "" + setup.get(KEY_MEASURE));
	  if (setup.get(KEY_FITNESS) == null)
	    stmt.setNull(3, Types.DOUBLE);
	  else
	    stmt.setDouble(3, (Double) setup.get(KEY_FITNESS));
	  stmt.setString(4, key);
	  stmt.setString(5, value);
	  stmt.execute();
	}
	catch (Exception e) {
	  errors.add(LoggingHelper.handleException(this, "Failed to insert setup key/value:\n- key: " + key + "\n- value: " + value, e));
	}
      }
    }

    SQLUtils.close(stmt);

    return result;
  }

  /**
   * Finishing up the genetic algorithm run.
   *
   * @param algorithm		the algorithm that initiated the run
   * @param error  		null if successful, otherwise error message
   * @param params              the parameters to store
   */
  @Override
  protected void doFinish(AbstractGeneticAlgorithm algorithm, String error, Map<String,Object> params) {
    params.put(KEY_SUCCESSFUL, error);
    upload(params);
  }
}
