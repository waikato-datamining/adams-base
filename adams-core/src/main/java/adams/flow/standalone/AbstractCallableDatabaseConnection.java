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
 * AbstractCallableDatabaseConnection.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.flow.core.AbstractCallableActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import nz.ac.waikato.cms.locator.ClassLocator;

/**
 * Ancestor for callable database connection standalones.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 12532 $
 */
public abstract class AbstractCallableDatabaseConnection
  extends AbstractCallableActor
  implements AbstractDatabaseConnectionProvider {

  /** for serialization. */
  private static final long serialVersionUID = -4898610818562897692L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "References a database connection provider of type " + getRequiredDatabaseConnectionProvider().getName() + ".";
  }

  /**
   * Returns the required database connection provider class.
   *
   * @return		the class
   */
  protected abstract Class getRequiredDatabaseConnectionProvider();

  /**
   * Tries to find the callable actor referenced by its name.
   * Makes sure that the actor produces output.
   *
   * @return		the callable actor or null if not found
   */
  @Override
  protected Actor findCallableActor() {
    Actor 	result;
    Class	required;

    result = super.findCallableActor();

    if (result != null) {
      if (!(ActorUtils.isStandalone(result))) {
        m_FindCallableActorError = "Callable actor '" + result.getFullName() + "' is not a standalone " + (m_CallableActor == null ? "!" : m_CallableActor.getClass().getName());
	getLogger().severe(m_FindCallableActorError);
	result = null;
      }
      else {
	required = getRequiredDatabaseConnectionProvider();
        if (!ClassLocator.hasInterface(required, result.getClass()) && !ClassLocator.isSubclass(required, result.getClass())) {
          m_FindCallableActorError = "Callable actor '" + result.getFullName() + "' is not implementing " + required.getName() + ": " + result.getClass().getName();
          getLogger().severe(m_FindCallableActorError);
          result = null;
        }
      }
    }

    return result;
  }

  /**
   * Executes the callable actor.
   *
   * @return		null if no error, otherwise error message
   */
  @Override
  protected String executeCallableActor() {
    return m_CallableActor.execute();
  }

  /**
   * Returns the database connection in use. Reconnects the database, to make
   * sure that the database connection is the correct one.
   *
   * @return		the connection object
   */
  public adams.db.AbstractDatabaseConnection getConnection() {
    return ((AbstractDatabaseConnectionProvider) m_CallableActor).getConnection();
  }
}
