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
 * AbstractStandaloneGroup.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import java.util.List;

import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;

/**
 * Ancestor for fixed-sized groups.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of sub-actor
 */
public abstract class AbstractStandaloneGroup<T extends Actor>
  extends AbstractStandalone
  implements StandaloneGroup<T> {

  /** for serialization. */
  private static final long serialVersionUID = -739244942139022557L;
  
  /** the actors of the fixed group. */
  protected List<T> m_Actors;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Actors = getDefaultActors();
  }
  
  /**
   * Returns the list of default actors.
   * 
   * @return		the default actors
   */
  protected abstract List<T> getDefaultActors();

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(true, false, ActorExecution.UNDEFINED, false, new Class[]{StandaloneGroupItem.class});
  }

  /**
   * Updates the parent of all actors in this group.
   */
  protected void updateParent() {
    int		i;

    for (i = 0; i < size(); i++) {
      get(i).setParent(null);
      get(i).setParent(this);
    }
  }

  /**
   * Checks the actors whether they are of the correct type.
   * 
   * @param actors	the actor to check
   * @return		null if OK, otherwise the error message
   */
  protected String checkActors(AbstractActor[] actors) {
    int		i;
    String	msg;
    
    for (i = 0; i < actors.length; i++) {
      msg = checkActor(actors[i], i);
      if (msg != null)
	return msg;
    }
    
    return null;
  }

  /**
   * Checks the actor whether it is of the correct type.
   * 
   * @param actor	the actor to check
   * @return		null if OK, otherwise the error message
   */
  protected String checkActor(AbstractActor actor) {
    return checkActor(actor, -1);
  }

  /**
   * Checks the actor whether it is of the correct type.
   * 
   * @param actor	the actor to check
   * @param index	the index of actor, ignored if -1
   * @return		null if OK, otherwise the error message
   */
  protected abstract String checkActor(AbstractActor actor, int index);

  /**
   * Returns the size of the group.
   *
   * @return		the size, always 2
   */
  @Override
  public int size() {
    return m_Actors.size();
  }

  /**
   * Sets the actors to use.
   *
   * @param value	the actors
   */
  protected void setActors(AbstractActor[] value) {
    String	msg;
    
    msg = checkActors(value);
    if (msg != null) {
      getLogger().warning(msg);
      return;
    }
    
    m_Actors.clear();
    for (AbstractActor actor: value)
      m_Actors.add((T) actor);
    reset();
    updateParent();
  }

  /**
   * Returns the actors to use.
   *
   * @return		the actors
   */
  protected AbstractActor[] getActors() {
    return m_Actors.toArray(new AbstractActor[m_Actors.size()]);
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  @Override
  public AbstractActor get(int index) {
    return (AbstractActor) m_Actors.get(index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  @Override
  public void set(int index, AbstractActor actor) {
    String	msg;
    
    msg = checkActor(actor, index);
    if (msg == null) {
      m_Actors.set(index, (T) actor);
      reset();
      updateParent();
    }
    else {
      getLogger().severe(msg);
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
   * Performs checks on the "sub-actors".
   *
   * @return		null if everything is fine, otherwise the error
   */
  @Override
  public abstract String check();

  /**
   * Returns the first non-skipped actor.
   *
   * @return		the first 'active' actor, null if none available
   */
  @Override
  public AbstractActor firstActive() {
    AbstractActor	result;
    int			i;
    
    result = null;
    
    for (i = 0; i < m_Actors.size(); i++) {
      if (!m_Actors.get(i).getSkip()) {
	result = (AbstractActor) m_Actors.get(i);
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
  @Override
  public AbstractActor lastActive() {
    AbstractActor	result;
    int			i;
    
    result = null;
    
    for (i = m_Actors.size() - 1; i >= 0; i--) {
      if (!m_Actors.get(i).getSkip()) {
	result = (AbstractActor) m_Actors.get(i);
	break;
      }
    }
    
    return result;
  }
  
  /**
   * Finds the actor with the specified name recursively.
   * 
   * @param name	the name of the actor
   * @return		the actor, null if not found
   */
  public Actor find(String name) {
    Actor	result;
    
    result = null;
    
    for (Actor actor: m_Actors) {
      if (actor.getSkip())
	continue;
      if (actor.getName().equals(name)) {
	result = actor;
	break;
      }
      if (actor instanceof AbstractStandaloneGroup) {
	result = ((AbstractStandaloneGroup) actor).find(name);
	if (result != null)
	  break;
      }
    }
    
    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    int		i;
    
    result = super.setUp();
    
    if (result == null)
      result = check();
    
    if (result == null) {
      for (i = 0; i < m_Actors.size(); i++) {
	if (m_Actors.get(i).getSkip())
	  continue;
	result = m_Actors.get(i).setUp();
	if (result != null) {
	  result = "Actor #" + (i+1) + " failed setup: " + result;
	  break;
	}
      }
    }
    
    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected abstract String doExecute();
  
  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    int		i;
    
    for (i = 0; i < m_Actors.size(); i++) {
      if (m_Actors.get(i).getSkip())
	continue;
      if (isLoggingEnabled())
	getLogger().info("Stopping " + (i+1) + "/" + m_Actors.size() + ": " + m_Actors.get(i));
      m_Actors.get(i).stopExecution();
    }
    
    super.stopExecution();
  }
  
  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    int		i;
    
    for (i = 0; i < m_Actors.size(); i++) {
      if (m_Actors.get(i).getSkip())
	continue;
      if (isLoggingEnabled())
	getLogger().info("Wrapping up " + (i+1) + "/" + m_Actors.size() + ": " + m_Actors.get(i));
      m_Actors.get(i).wrapUp();
    }
    
    super.wrapUp();
  }
  
  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    int		i;
    
    for (i = 0; i < m_Actors.size(); i++) {
      if (m_Actors.get(i).getSkip())
	continue;
      if (isLoggingEnabled())
	getLogger().info("Cleaning up " + (i+1) + "/" + m_Actors.size());
      m_Actors.get(i).cleanUp();
    }
    
    super.cleanUp();
  }
}
