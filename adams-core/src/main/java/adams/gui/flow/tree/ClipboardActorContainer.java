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
 * ClipboardActorContainer.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree;

import adams.core.Variables;
import adams.core.option.AbstractOptionProducer;
import adams.core.option.NestedConsumer;
import adams.core.option.NestedProducer;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.MutableActorHandler;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple container for multiple actors, for easy copying to and retrieving
 * from the clipboard.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClipboardActorContainer
  extends AbstractActor
  implements MutableActorHandler {

  /** for serialization. */
  private static final long serialVersionUID = 8826708859698352085L;

  /** the actors. */
  protected ArrayList<Actor> m_Actors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Container for copying actors to and pasting from the clipboard";
  }

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

    m_Actors = new ArrayList<>();
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(true, ActorExecution.UNDEFINED, false);
  }

  /**
   * Performs checks on the "sub-actors".
   *
   * @return		always null
   */
  public String check() {
    return null;
  }

  /**
   * Returns the size of the group.
   *
   * @return		the size
   */
  public int size() {
    return m_Actors.size();
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  public Actor get(int index) {
    return m_Actors.get(index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  public void set(int index, Actor actor) {
    m_Actors.set(index, actor);
    reset();
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  public int indexOf(String actor) {
    int		result;
    int		i;

    result = -1;
    for (i = 0; i < m_Actors.size(); i++) {
      if (m_Actors.get(i).getName().equals(actor)) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Returns the first non-skipped actor.
   *
   * @return		the first 'active' actor, null if none available
   */
  public Actor firstActive() {
    Actor	result;
    int		i;

    result = null;

    for (i = 0; i < m_Actors.size(); i++) {
      if (!m_Actors.get(i).getSkip()) {
	result = m_Actors.get(i);
	break;
      }
    }

    return result;
  }

  /**
   * Returns the last non-skipped actor.
   *
   * @return		the last 'active' actor, null if none available
   */
  public Actor lastActive() {
    Actor	result;
    int		i;

    result = null;

    for (i = m_Actors.size() - 1; i >= 0; i--) {
      if (!m_Actors.get(i).getSkip()) {
	result = m_Actors.get(i);
	break;
      }
    }

    return result;
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
   */
  public void add(int index, Actor actor) {
    m_Actors.add(index, actor);
    reset();
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
    m_Actors.clear();
    reset();
  }

  /**
   * Sets the actors. Replaces all previous actors.
   *
   * @param value	the actors to set
   */
  public void setActors(Actor[] value) {
    m_Actors.clear();
    m_Actors.addAll(Arrays.asList(value));
    reset();
  }

  /**
   * Returns the actors as array.
   *
   * @return		the actors
   */
  public Actor[] getActors() {
    return m_Actors.toArray(new Actor[m_Actors.size()]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorsTipText() {
    return "The actors.";
  }

  /**
   * Updates the Variables instance in use.
   * <br><br>
   * Use with caution!
   *
   * @param value	the instance to use
   */
  @Override
  public synchronized void setVariables(Variables value) {
    int		i;
    
    super.setVariables(value);
    
    for (i = 0; i < size(); i++)
      get(i).setVariables(value);
  }

  /**
   * Does nothing.
   *
   * @return		always null
   */
  @Override
  protected String doExecute() {
    return null;
  }
  
  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
  }

  /**
   * Returns a string representation of the options.
   *
   * @return		 a string representation
   */
  public String toNestedString() {
    return AbstractOptionProducer.toString(NestedProducer.class, this);
  }

  /**
   * Creates, if possible, an actor container from the given string (in nested
   * format).
   *
   * @param s		the string parse
   * @return		the parsed actor or null if not possible
   */
  public static ClipboardActorContainer fromNestedString(String s) {
    ClipboardActorContainer	result;
    Object			obj;
    NestedConsumer		consumer;

    consumer = new NestedConsumer();
    consumer.setQuiet(true);
    obj = consumer.fromString(s);
    consumer.cleanUp();

    result = null;
    if ((obj != null) && (obj instanceof ClipboardActorContainer)) {
      result = (ClipboardActorContainer) obj;
    }
    else if (obj instanceof Actor) {
      result = new ClipboardActorContainer();
      result.setActors(new Actor[]{(Actor) obj});
    }

    return result;
  }
}
