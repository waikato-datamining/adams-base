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
 * MutableControlActor.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.util.ArrayList;
import java.util.List;

import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.MutableActorHandler;

/**
 * Abstract superclass for actors that allow their sub-actors to modified
 * (adding, removing, editing). The sub-actors are not connected.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class MutableControlActor
  extends AbstractDirectedControlActor
  implements MutableActorHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5329012531879115063L;

  /** the actors. */
  protected List<AbstractActor> m_Actors;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Actors = new ArrayList<AbstractActor>();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "actor", "actors",
	    new AbstractActor[0]);
  }

  /**
   * Sets the flow items for this sequence.
   *
   * @param value 	the sequence items
   */
  public void setActors(AbstractActor[] value) {
    int		i;

    ActorUtils.uniqueNames(value);

    m_Actors.clear();
    for (i = 0; i < value.length; i++)
      m_Actors.add(value[i]);

    updateParent();
    reset();
  }

  /**
   * Returns the flow items of this sequence.
   *
   * @return 		the sequence items
   */
  public AbstractActor[] getActors() {
    return m_Actors.toArray(new AbstractActor[m_Actors.size()]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String actorsTipText();

  /**
   * Returns the size of the group.
   *
   * @return		the size
   */
  @Override
  public int size() {
    return m_Actors.size();
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  @Override
  public AbstractActor get(int index) {
    return m_Actors.get(index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  @Override
  public void set(int index, AbstractActor actor) {
    if ((index > -1) && (index < m_Actors.size())) {
      ActorUtils.uniqueName(actor, this, index);
      m_Actors.set(index, actor);
      reset();
      updateParent();
    }
    else {
      getLogger().severe("Index out of range (0-" + (m_Actors.size() - 1) + "): " + index);
    }
  }

  /**
   * Inserts the actor at the end.
   *
   * @param actor	the actor to insert
   */
  public void add(AbstractActor actor) {
    add(size(), actor);
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   */
  public void add(int index, AbstractActor actor) {
    ActorUtils.uniqueName(actor, this, index);
    m_Actors.add(index, actor);
    reset();
    updateParent();
  }

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  public AbstractActor remove(int index) {
    AbstractActor	result;

    result = m_Actors.remove(index);
    reset();

    return result;
  }

  /**
   * Removes all actors.
   */
  public void removeAll() {
    m_Actors.clear();
    reset();
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  @Override
  public int indexOf(String actor) {
    int		result;
    int		i;

    result = -1;

    for (i = 0; i < size(); i++) {
      if (get(i).getName().equals(actor)) {
	result = i;
	break;
      }
    }

    return result;
  }
}
