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
 * ActorHandler.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

/**
 * For actors that handle "sub-actors".
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ActorHandler 
  extends Actor, Flushable {

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  public ActorHandlerInfo getActorHandlerInfo();

  /**
   * Performs checks on the "sub-actors".
   *
   * @return		null if everything is fine, otherwise the error
   */
  public String check();

  /**
   * Returns the size of the group.
   *
   * @return		the size
   */
  public int size();

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  public AbstractActor get(int index);

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  public void set(int index, AbstractActor actor);

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  public int indexOf(String actor);

  /**
   * Returns the first non-skipped actor.
   *
   * @return		the first 'active' actor, null if none available
   */
  public AbstractActor firstActive();

  /**
   * Returns the last non-skipped actor.
   *
   * @return		the last 'active' actor, null if none available
   */
  public AbstractActor lastActive();
  
  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution();
}
