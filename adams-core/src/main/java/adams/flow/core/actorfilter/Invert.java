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
 * Invert.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.core.actorfilter;

import adams.flow.core.Actor;

/**
 * Inverts the matching sense of the base filter.
 */
public class Invert
  implements ActorFilter {

  private static final long serialVersionUID = -3558268845213578619L;

  /** the base filter. */
  protected ActorFilter m_Filter;

  /**
   * Initializes the filter.
   *
   * @param filter	the base filter to use
   */
  public Invert(ActorFilter filter) {
    m_Filter = filter;
  }

  /**
   * Returns whether the actor should be kept.
   *
   * @param actor	the actor to check
   * @return		the inverse of the base filter
   */
  @Override
  public boolean accept(Actor actor) {
    return !m_Filter.accept(actor);
  }
}
