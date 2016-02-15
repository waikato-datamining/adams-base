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
 * AbstractMutableStandaloneGroupItem.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone;

import adams.core.logging.LoggingLevel;
import adams.flow.control.AbstractControlActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.MutableActorHandler;

/**
 * Ancestor for group items that can contain other actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of the enclosing group
 */
public abstract class AbstractMutableStandaloneGroupItem<T extends Actor>
  extends AbstractControlActor
  implements MutableActorHandler, StandaloneGroupItem<T> {

  private static final long serialVersionUID = -2130921331341838430L;

  /** the flow items. */
  protected MutableActorHandler m_Actors;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "actor", "actors",
      new Actor[0]);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Actors = newActorHandler();
  }

  /**
   * Creates an instance of the actor handler taking care of the sub-actors.
   *
   * @return		the handler
   */
  protected abstract MutableActorHandler newActorHandler();

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    m_Actors.setLoggingLevel(value);
  }

  /**
   * Returns the enclosing group.
   *
   * @return		the group, null if not available (eg if parent not set)
   */
  public T getEnclosingGroup() {
    if (getParent() == null)
      return null;

    if (getParent() instanceof StandaloneGroup)
      return (T) getParent();

    return null;
  }

  /**
   * Performs checks on the "sub-actors".
   *
   * @return		null if checks passed or null in case of an error
   */
  @Override
  public String check() {
    return checkSubActors(getActors());
  }

  /**
   * Checks the sub actor before it is set.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param index	the index the actor gets set
   * @param actor	the actor to check
   * @return		null if accepted, otherwise error message
   */
  protected abstract String checkSubActor(int index, Actor actor);

  /**
   * Checks the sub actors before they are set via the setSubActors method.
   * Returns an error message if the actors are not acceptable, null otherwise.
   *
   * @param actors	the actors to check
   * @return		null if accepted, otherwise error message
   */
  protected abstract String checkSubActors(Actor[] actors);

  /**
   * Sets the sub-actor.
   *
   * @param value	the actor
   */
  public void setActors(Actor[] value) {
    String	msg;

    msg = checkSubActors(value);
    if (msg == null) {
      m_Actors.removeAll();
      for (Actor actor: value)
        m_Actors.add(actor);
      reset();
      updateParent();
    }
    else {
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Returns the sub actors.
   *
   * @return		the actors
   */
  public Actor[] getActors() {
    Actor[]	result;
    int		i;

    result = new Actor[m_Actors.size()];
    for (i = 0; i < m_Actors.size(); i++)
      result[i] = m_Actors.get(i);

    return result;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String actorsTipText();

  /**
   * Updates the parent of all actors in this group.
   */
  @Override
  protected void updateParent() {
    m_Actors.setName(getName());
    m_Actors.setParent(null);
    m_Actors.setParent(getParent());
  }

  /**
   * Returns the size of the group.
   *
   * @return		always 1
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
  public Actor get(int index) {
    return m_Actors.get(index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   * @see		#checkSubActor(int, Actor)
   */
  @Override
  public void set(int index, Actor actor) {
    String	msg;

    msg = checkSubActor(index, actor);
    if (msg == null) {
      m_Actors.set(index, actor);
      reset();
      updateParent();
    }
    else {
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  @Override
  public int indexOf(String actor) {
    return m_Actors.indexOf(actor);
  }

  /**
   * Inserts the actor at the end.
   *
   * @param actor	the actor to insert
   */
  public void add(Actor actor) {
    add(size(), actor);
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   * @see		#checkSubActor(int, Actor)
   */
  public void add(int index, Actor actor) {
    String	msg;

    msg = checkSubActor(index, actor);
    if (msg == null) {
      m_Actors.add(index, actor);
      reset();
      updateParent();
    }
    else {
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  public Actor remove(int index) {
    Actor	result;

    result = m_Actors.remove(index);
    reset();

    return result;
  }

  /**
   * Removes all actors.
   */
  public void removeAll() {
    m_Actors.removeAll();
    reset();
  }

  /**
   * Returns the internal representation of the actors.
   *
   * @return		the internal actors
   */
  protected MutableActorHandler getInternalActors() {
    return m_Actors;
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return m_Actors.getActorHandlerInfo();
  }

  /**
   * Gets called in the setUp() method. Returns null if sub-actors are fine,
   * otherwise error message.
   *
   * @return		null if everything OK, otherwise error message
   */
  protected abstract String doSetUpSubActors();

  /**
   * Performs the setUp of the sub-actors.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String setUpSubActors() {
    String	result;

    result = null;

    if (!getSkip()) {
      updateParent();
      result = doSetUpSubActors();
      if (result == null)
	result = m_Actors.setUp();
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    return null;
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    m_Actors.cleanUp();
    super.cleanUp();
  }
}
