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
 * SQLTableCleanUp.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import java.sql.Statement;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Executes an SQL statement to clean up the table.
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
 * <pre>-statement &lt;adams.db.SQLStatement&gt; (property: statement)
 * &nbsp;&nbsp;&nbsp;The SQL statement to use for cleaning up the table.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SQLTableCleanUp
  extends AbstractTableCleanUp {

  /** for serialization. */
  private static final long serialVersionUID = 2617358965818813327L;
  
  /** the SQL command to execute. */
  protected SQLStatement m_Statement;

  /**
   * Returns a string describing the object.
   * 
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes an SQL statement to clean up the table.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "statement", "statement",
	    new SQLStatement());
  }

  /**
   * Sets the SQL statement.
   *
   * @param value 	the statement
   */
  public void setStatement(SQLStatement value) {
    m_Statement = value;
  }

  /**
   * Returns the SQL statement.
   *
   * @return 		the statement
   */
  public SQLStatement getStatement() {
    return m_Statement;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statementTipText() {
    return "The SQL statement to use for cleaning up the table.";
  }
  
  /**
   * Performs checks before cleaning up the table.
   * 
   * @return		null if checks successful, otherwise error message
   */
  @Override
  protected String check() {
    String	result;
    
    result = super.check();
    
    if (result == null) {
      if (m_Statement.isEmpty())
	result = "SQL statement is empty!";
    }
    
    return result;
  }
  
  /**
   * Performs the actual clean up.
   * 
   * @return		null if successfully cleaned up, otherwise error message
   */
  @Override
  protected String doCleanUpTable() {
    String	result;
    Statement	stmt;
    String	msg;

    result = null;
    
    try {
      stmt = m_Connection.getConnection(false).createStatement();
      stmt.executeUpdate(m_Statement.getValue());
      SQL.close(stmt);
    }
    catch (Exception e) {
      msg    = "Failed to execute '" + m_Statement + "': ";
      result = msg + e;
      getLogger().log(Level.SEVERE, msg, e);
    }
    
    return result;
  }
}
