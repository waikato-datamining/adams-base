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
 * SQLIdSupplier.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import java.sql.ResultSet;
import java.util.ArrayList;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.db.SQL;
import adams.db.SQLStatement;
import adams.flow.core.ActorUtils;

/**
 <!-- globalinfo-start -->
 * Executes an SQL statement for generating the IDs.<br>
 * Variables are automatically expanded.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SQLIdSupplier
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-output-array (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the IDs as array or one by one.
 * </pre>
 *
 * <pre>-sql &lt;adams.db.SQLStatement&gt; (property: SQL)
 * &nbsp;&nbsp;&nbsp;The SQL statement to run that generates the IDs.
 * &nbsp;&nbsp;&nbsp;default: select auto_id from table
 * </pre>
 *
 * <pre>-type &lt;INTEGER|STRING&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of IDs to generate.
 * &nbsp;&nbsp;&nbsp;default: INTEGER
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SQLIdSupplier
  extends AbstractDatabaseIdSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -2269772801929933064L;

  /**
   * The type of IDs to output.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Type {
    /** integer. */
    INTEGER,
    /** string. */
    STRING
  }

  /** the SQL statement to execute. */
  protected SQLStatement m_SQL;

  /** the type of IDs to generate. */
  protected Type m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Executes an SQL statement for generating the IDs.\n"
      + "Variables are automatically expanded.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "sql", "SQL",
	    new SQLStatement("select auto_id from table"));

    m_OptionManager.add(
	    "type", "type",
	    Type.INTEGER);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "type", m_Type);
    result += QuickInfoHelper.toString(this, "SQL", Utils.shorten(m_SQL.getValue().replaceAll("\\s", " ").replaceAll("[ ]+", " "), 50), ": ");

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
   * Sets the type of IDs to generate.
   *
   * @param value	the type
   */
  public void setType(Type value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of IDs to generate.
   *
   * @return 		the type
   */
  public Type getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of IDs to generate.";
  }

  /**
   * Returns the based class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    switch (m_Type) {
      case INTEGER:
	return Integer.class;
      case STRING:
	return String.class;
      default:
	throw new IllegalStateException("Unhandled type: " + m_Type);
    }
  }

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  @Override
  protected adams.db.AbstractDatabaseConnection getDatabaseConnection() {
    return ActorUtils.getDatabaseConnection(
	  this,
	  adams.flow.standalone.DatabaseConnection.class,
	  adams.db.DatabaseConnection.getSingleton());
  }

  /**
   * Returns the IDs from the database.
   *
   * @param errors	for storing any error messages
   * @return		the IDs
   */
  @Override
  protected ArrayList getIDs(StringBuilder errors) {
    ArrayList	result;
    SQL		sql;
    ResultSet	rs;
    String	query;

    result = new ArrayList();

    rs    = null;
    query = null;
    try {
      sql   = new SQL(getDatabaseConnection());
      query = m_SQL.getValue();
      // replace variables
      query = getVariables().expand(query);
      if (isLoggingEnabled())
	getLogger().info("Query: " + query);
      rs = sql.getResultSet(query);
      while (rs.next()) {
	switch (m_Type) {
	  case INTEGER:
	    result.add(rs.getInt(1));
	    break;
	  case STRING:
	    result.add(rs.getString(1));
	    break;
	  default:
	    throw new IllegalStateException("Unhandled type: " + m_Type);
	}
      }
      if (isLoggingEnabled())
	getLogger().info("--> " + result.size() + " IDs");
    }
    catch (Exception e) {
      errors.append(handleException("Failed to obtain IDs, using: " + ((query == null) ? m_SQL : query), e));
    }
    SQL.closeAll(rs);

    return result;
  }
}
