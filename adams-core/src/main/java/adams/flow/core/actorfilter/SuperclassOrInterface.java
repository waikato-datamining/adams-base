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
 * SuperclassOrInterface.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.core.actorfilter;

import adams.flow.core.Actor;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Accepts actor classes that either implement the interface(s) or are derived from the superclass(es).
 */
public class SuperclassOrInterface
  implements ActorFilter {

  private static final long serialVersionUID = -3558268845213578619L;

  /** the classes to check against. */
  protected List<Class> m_Classes;

  /**
   * For checking against a single class only.
   *
   * @param cls	the class (superclass, interface) to check against
   */
  public SuperclassOrInterface(Class cls) {
    this(new Class[]{cls});
  }

  /**
   * For checking against a multiple classes.
   *
   * @param classes	the classes (superclasses, interfaces) to check against
   */
  public SuperclassOrInterface(Class[] classes) {
    m_Classes = new ArrayList<>(Arrays.asList(classes));
  }

  /**
   * Returns whether the actor should be kept.
   *
   * @param actor	the actor to check
   * @return		true if the actor class is in the specified list of classes
   */
  @Override
  public boolean accept(Actor actor) {
    boolean	result;

    result = false;

    for (Class cls: m_Classes) {
      if (ClassLocator.matches(cls, actor.getClass())) {
        result = true;
        break;
      }
    }

    return result;
  }
}
