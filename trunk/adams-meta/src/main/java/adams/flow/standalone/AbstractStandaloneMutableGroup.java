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
 * AbstractStandaloneMutableGroup.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;

/**
 * Ancestor for mutable groups of standalones.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
   */
  @Override
  public void add(AbstractActor actor) {
    String	msg;
    
    msg = checkActor(actor);
    if (msg == null) {
      m_Actors.add((T) actor);
      reset();
      updateParent();
    }
    else {
      getLogger().warning(msg);
    }
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   */
  @Override
  public void add(int index, AbstractActor actor) {
    String	msg;
    
    msg = checkActor(actor, index);
    if (msg == null) {
      m_Actors.add(index, (T) actor);
      reset();
      updateParent();
    }
    else {
      getLogger().warning(msg);
    }
  }

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  @Override
  public AbstractActor remove(int index) {
    return (AbstractActor) m_Actors.remove(index);
  }

  /**
   * Removes all actors.
   */
  @Override
  public void removeAll() {
    m_Actors.clear();
  }
}
