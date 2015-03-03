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
 * AbstractDatabaseIdSupplier.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

/**
 * Abstract ancestor for ID suppliers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDatabaseIdSupplier
  extends AbstractIdSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -8462709950859959951L;

  /** the database connection. */
  protected adams.db.AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DatabaseConnection = null;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to output the IDs as array or one by one.";
  }

  /**
   * Returns the based class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return Integer.class;
  }

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  protected abstract adams.db.AbstractDatabaseConnection getDatabaseConnection();

  /**
   * Configures the database connection if necessary.
   *
   * @return		null if successful, otherwise error message
   */
  protected String setUpDatabaseConnection() {
    String	result;

    result = null;

    if (m_DatabaseConnection == null) {
      m_DatabaseConnection = getDatabaseConnection();
      if (m_DatabaseConnection == null)
	result = "No database connection available!";
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = setUpDatabaseConnection();

    if (result == null)
      result = super.doExecute();

    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    m_DatabaseConnection = null;

    super.wrapUp();
  }
}
