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
 * AbstractBooleanDatabaseCondition.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;

/**
 * Ancestor for conditions that operate on a database.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBooleanDatabaseCondition
  extends AbstractBooleanCondition
  implements DatabaseConnectionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -569423875930411268L;
  
  /** the database connection in use. */
  protected AbstractDatabaseConnection m_DatabaseConnection;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_DatabaseConnection = getDefaultDatabaseConnection();
  }
  
  /**
   * Returns the default database connection to use.
   * 
   * @return		the default connection
   */
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }
  
  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DatabaseConnection;
  }

  /**
   * Sets the database connection object to use.
   *
   * @param value	the object to use
   */
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    m_DatabaseConnection = value;
  }

  /**
   * Returns the database connection from the flow.
   * 
   * @param actor	the actor to use for determining the connection
   * @return		the connection
   */
  protected AbstractDatabaseConnection getConnection(AbstractActor actor) {
    return ActorUtils.getDatabaseConnection(
	  actor,
	  adams.flow.standalone.DatabaseConnection.class,
	  getDefaultDatabaseConnection());
  }
  
  /**
   * Configures the condition.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp(Actor owner) {
    String	result;
    
    result = super.setUp(owner);
    
    if (result == null) {
      if (owner instanceof AbstractActor)
	setDatabaseConnection(getConnection((AbstractActor) owner));
    }
    
    return result;
  }
}
