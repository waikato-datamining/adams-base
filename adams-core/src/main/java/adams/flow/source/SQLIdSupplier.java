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
 * SQLIdSupplier.java
 * Copyright (C) 2011-20124 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.db.SQLF;
import adams.db.SQLIntf;
import adams.db.SQLStatement;
import adams.db.SQLUtils;
import adams.flow.core.ActorUtils;

import java.sql.ResultSet;
import java.util.ArrayList;

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
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SQLIdSupplier
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the IDs as array or one by one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-lenient &lt;boolean&gt; (property: lenient)
 * &nbsp;&nbsp;&nbsp;If enabled, the source no longer reports an error when not finding any IDs.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-sql &lt;adams.db.SQLStatement&gt; (property: SQL)
 * &nbsp;&nbsp;&nbsp;The SQL statement to run that generates the IDs; variables get expanded
 * &nbsp;&nbsp;&nbsp;automatically.
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
 */
public class SQLIdSupplier
  extends AbstractDatabaseIdSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -2269772801929933064L;

  /**
   * The type of IDs to output.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
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
    result += QuickInfoHelper.toString(this, "SQL", Shortening.shortenEnd(m_SQL.getValue().replaceAll("\\s", " ").replaceAll("[ ]+", " "), 50), ": ");
    result += QuickInfoHelper.toStringOutputArray(this);

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
    return "The SQL statement to run that generates the IDs; variables get expanded automatically.";
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
      adams.flow.standalone.DatabaseConnectionProvider.class,
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
    SQLIntf 	sql;
    ResultSet	rs;
    String	query;

    result = new ArrayList();

    rs    = null;
    query = null;
    try {
      sql   = SQLF.getSingleton(m_DatabaseConnection);
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
    finally {
      SQLUtils.closeAll(rs);
    }

    return result;
  }
}
