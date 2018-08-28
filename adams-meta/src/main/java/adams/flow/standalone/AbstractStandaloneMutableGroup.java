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
 * AbstractStandaloneMutableGroup.java
 * Copyright (C) 2014-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;

/**
 * Ancestor for mutable groups of standalones.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractStandaloneMutableGroup<T extends Actor>
  extends AbstractStandaloneGroup<T>
  implements StandaloneMutableGroup<T> {

  /** for serialization. */
  private static final long serialVersionUID = -1626117689442141269L;

  /**
   * Inserts the actor at the end.
   *
   * @param actor	the actor to insert
   * @return		null if everything is fine, otherwise the error
   */
  @Override
  public String add(Actor actor) {
    String 	result;
    
    result = checkActor(actor);
    if (result == null) {
      m_Actors.add((T) actor);
      ActorUtils.uniqueName(actor, this, m_Actors.size() - 1);
      reset();
      updateParent();
    }
    else {
      getLogger().warning(result);
    }

    return result;
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   * @return		null if everything is fine, otherwise the error
   */
  @Override
  public String add(int index, Actor actor) {
    String 	result;
    
    result = checkActor(actor, index);
    if (result == null) {
      m_Actors.add(index, (T) actor);
      ActorUtils.uniqueName(actor, this, index);
      reset();
      updateParent();
    }
    else {
      getLogger().warning(result);
    }

    return result;
  }

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  @Override
  public Actor remove(int index) {
    return m_Actors.remove(index);
  }

  /**
   * Removes all actors.
   */
  @Override
  public void removeAll() {
    m_Actors.clear();
  }
}
