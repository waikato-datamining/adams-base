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
 * MutableActorHandler.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

/**
 * Interface for actors that allow to add/remove sub-actors as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MutableActorHandler
  extends ActorHandler {

  /**
   * Inserts the actor at the end.
   *
   * @param actor	the actor to insert
   */
  public void add(AbstractActor actor);

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   */
  public void add(int index, AbstractActor actor);

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  public AbstractActor remove(int index);

  /**
   * Removes all actors.
   */
  public void removeAll();
}
