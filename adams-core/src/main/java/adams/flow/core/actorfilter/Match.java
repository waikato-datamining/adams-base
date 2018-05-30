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
 * Match.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.core.actorfilter;

import adams.flow.core.Actor;
import adams.flow.core.Compatibility;

/**
 * Accepts any actor that is compatible with the specified classes
 * (subclasses or implementing interface).
 */
public class Match
  implements ActorFilter {

  private static final long serialVersionUID = -3558268845213578619L;

  /** for peforming the checks. */
  protected Compatibility m_Compatibility;

  /** the classes to check against. */
  protected Class[] m_Classes;

  /**
   * For checking against a single class only.
   *
   * @param cls	the class (superclass, interface) to check against
   */
  public Match(Class cls) {
    this(new Class[]{cls});
  }

  /**
   * For checking against a multiple classes.
   *
   * @param classes	the classes (superclasses, interfaces) to check against
   */
  public Match(Class[] classes) {
    m_Compatibility = new Compatibility();
    m_Classes       = classes;
  }

  /**
   * Returns whether the actor should be kept.
   *
   * @param actor	the actor to check
   * @return		always true
   */
  @Override
  public boolean accept(Actor actor) {
    return m_Compatibility.isCompatible(new Class[]{actor.getClass()}, m_Classes);
  }
}
