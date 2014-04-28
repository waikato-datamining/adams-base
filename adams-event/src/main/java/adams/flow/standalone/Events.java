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
 * Events.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import java.util.ArrayList;
import java.util.List;

import adams.flow.control.AbstractControlActor;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.DaemonEvent;
import adams.flow.core.Event;
import adams.flow.core.MutableActorHandler;

/**
 <!-- globalinfo-start -->
 * Container for event actors.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Events
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-actor &lt;adams.flow.core.AbstractActor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The managed events.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Events
  extends AbstractControlActor
  implements MutableActorHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8393224234458401716L;

  /** the events. */
  protected List<AbstractActor> m_Actors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Container for event actors.";
  }

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
   * Performs checks on the "sub-actors".
   *
   * @return		null if OK, otherwise errors message
   */
  @Override
  public String check() {
    String	result;
    int		i;
    
    result = super.check();
    
    if (result == null) {
      for (i = 0; i < size(); i++) {
	if (getScopeHandler() != null)
	  result = getScopeHandler().addGlobalName(get(i).getName());
      }
    }
    
    return result;
  }

  /**
   * Checks whether the actor is valid, throws an execption if not.
   *
   * @param actor			the actor to check
   * @return				the actor if valid
   * @throws IllegalArgumentException	if actor does not implement {@link Event}
   */
  protected AbstractActor checkActor(AbstractActor actor) {
    if (!(actor instanceof Event))
      throw new IllegalArgumentException(
	    "Only " + Event.class.getName() + " actors are accepted, "
	  + "provided: " + actor.getClass().getName());
    
    if ((getScopeHandler() != null) && getScopeHandler().isGlobalNameUsed(actor.getName()))
      throw new IllegalArgumentException(
	  getScopeHandler().addGlobalName(actor.getName()));

    return actor;
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
      m_Actors.add(checkActor(value[i]));

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
  public String actorsTipText() {
    return "The managed events.";
  }

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
      m_Actors.set(index, checkActor(actor));
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
    m_Actors.add(index, checkActor(actor));
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

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(true, true, ActorExecution.UNDEFINED, false, new Class[]{Event.class});
  }

  /**
   * Executes the flow item.
   *
   * @return		always null
   */
  @Override
  protected String doExecute() {
    String	result;
    int		i;

    result = null;

    for (i = 0; i < m_Actors.size(); i++) {
      if (m_Actors.get(i) instanceof DaemonEvent)
	result = m_Actors.get(i).execute();
      if (result != null)
	break;
    }

    return result;
  }
}
