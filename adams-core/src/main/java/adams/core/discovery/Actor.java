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
 * ActorDiscoveryHandler.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

/**
 * Handles actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Actor
  extends AbstractDiscoveryHandler {

  private static final long serialVersionUID = -6202276435982373970L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Locates actors.";
  }

  /**
   * Checks whether this object is handled by this discovery handler.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  @Override
  protected boolean handles(Object obj) {
    return (obj instanceof adams.flow.core.Actor)
      || (obj instanceof adams.flow.core.AbstractActor);
  }
}
