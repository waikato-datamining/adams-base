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
 * ActorReferenceHandler.java
 * Copyright (C) 2012-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

/**
 * Interface for actors that manage callable actors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ActorReferenceHandler
  extends MutableActorHandler {

  /**
   * Returns the classes that are prohibited to appear before this reference
   * handler.
   *
   * @return		the classes
   */
  public Class[] getProhibitedPrecedingActorReferenceHandlers();

  /**
   * Ensures that the handlers appear in the correct order.
   *
   * @return		null if OK, otherwise error message
   * @see		#getProhibitedPrecedingActorReferenceHandlers()
   */
  public String checkActorReferenceHandlers();

  /**
   * Returns whether actors have to be referenced elsewhere in the flow
   * or whether it is optional.
   *
   * @return		true if required, false if optional
   */
  public boolean isReferencingRequired();
}
