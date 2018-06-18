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
 * ActorFilter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.core.sizeof;

import adams.flow.core.Actor;
import nz.ac.waikato.cms.locator.ClassLocator;
import sizeof.agent.Filter;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * For filtering actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ActorFilter
  implements Serializable, Filter {

  private static final long serialVersionUID = -7040304204477763181L;

  /**
   * Checks whether to skip this superclass (and everything upwards).
   *
   * @param superclass the class to check
   * @return true if to skip, otherwise we will contained traversing the hierarchy
   */
  @Override
  public boolean skipSuperClass(Class superclass) {
    return false;
  }

  /**
   * Returns whether to skip the object.
   *
   * @param obj the object to check
   * @return true if to skip the object, otherwise it will get inspected
   */
  @Override
  public boolean skipObject(Object obj) {
    boolean result;
    if (obj.getClass().isArray())
      result = (ClassLocator.matches(Actor.class, obj.getClass().getComponentType()));
    else
      result = (ClassLocator.matches(Actor.class, obj.getClass()));
    return result;
  }

  /**
   * Returns whether to skip the field.
   *
   * @param field the field to check
   * @return true if to skip the field, otherwise it will get inspected
   */
  @Override
  public boolean skipField(Field field) {
    boolean result;
    if (field.getType().isArray())
      result = (ClassLocator.matches(Actor.class, field.getType().getComponentType()));
    else
      result = (ClassLocator.matches(Actor.class, field.getType()));
    return result;
  }
}