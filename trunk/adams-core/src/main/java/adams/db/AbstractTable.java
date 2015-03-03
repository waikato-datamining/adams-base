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
 * AbstractTable.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import adams.core.ClassLister;
import adams.core.Properties;
import adams.env.Environment;
import adams.env.TableDefinition;
import adams.event.DatabaseConnectionChangeEvent;
import adams.event.DatabaseConnectionChangeListener;

/**
 * Ancestor for all table classes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTable
  extends SQL
  implements DatabaseConnectionChangeListener {

  /** for serialization. */
  private static final long serialVersionUID = 4511302757992864994L;

  /** the properties file. */
  protected static Properties m_Properties;

  /** name of the table. */
  protected String m_TableName;
  
  /**
   * Initializes the table.
   *
   * @param dbcon	the database context this table is used in
   * @param tableName	the name of the table
   */
  public AbstractTable(AbstractDatabaseConnection dbcon, String tableName) {
    super(dbcon);

    m_TableName = tableName;
    m_DatabaseConnection.addChangeListener(this);
    
    setDebug(getProperties().getBoolean(this.getClass().getName() + ".Debug", false));
    if (getDebug())
      getLogger().info(m_DatabaseConnection.toString());
  }
  
  /**
   * Returns whether the specified table is enabled and should get created if non-existent.
   * 
   * @param cls		the table class to check
   * @return		true if table is to be available
   */
  public static boolean isEnabled(Class cls) {
    return getProperties().getBoolean(cls.getName() + ".Enabled", false);
  }
  
  /**
   * Get name of table.
   *
   * @return	table name
   */
  public String getTableName() {
    return m_TableName;
  }

  /**
   * Checks that a given table exists.
   *
   * @return 		true if the table exists.
   */
  public boolean tableExists() {
    return tableExists(getTableName());
  }

  /**
   * Checks that a given column exists.
   *
   * @param column	the column to look for
   * @return 		true if the column exists.
   */
  public boolean columnExists(String column) {
    return columnExists(getTableName(), column);
  }

  /**
   * Initialize table, e.g., creating.
   *
   * @return 	successful initialization
   */
  public abstract boolean init();

  /**
   * A change in the database connection occurred. Derived classes can
   * override this method to react to changes in the connection.
   *
   * @param e		the event
   */
  public void databaseConnectionStateChanged(DatabaseConnectionChangeEvent e) {
  }

  /**
   * Returns a short string representation.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return getTableName() + ": " + getDatabaseConnection().toString();
  }

  /**
   * Returns a list with classnames of tables.
   *
   * @return		the table classnames
   */
  public static String[] getTables() {
    return ClassLister.getSingleton().getClassnames(AbstractTable.class);
  }

  /**
   * Returns the properties. Loads them if necessary.
   *
   * @return		the properties
   */
  protected static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(TableDefinition.KEY);

    return m_Properties;
  }
}
