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
 * AcceptAll.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.core.actorfilter;

import adams.flow.core.Actor;

/**
 * Accepts any actor.
 */
public class AcceptAll
  implements ActorFilter {

  private static final long serialVersionUID = -3558268845213578619L;

  /**
   * Returns whether the actor should be kept.
   *
   * @param actor	the actor to check
   * @return		always true
   */
  @Override
  public boolean accept(Actor actor) {
    return true;
  }
}
