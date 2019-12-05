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
 * AbstractActorListingProcessor.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton,  Zealand
 */
package adams.flow.processor;

import adams.core.option.AbstractOption;
import adams.core.option.OptionHandler;
import adams.core.option.OptionTraversalPath;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.gui.flow.FlowPanel;

import java.awt.Component;

/**
 * Ancestor for processors that list full actor names and allow jumping to them.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractActorListingProcessor
  extends AbstractListingProcessor
  implements ActorProcessorWithFlowPanelContext {

  /** for serialization. */
  private static final long serialVersionUID = 7133896476260133469L;

  /** the current actor being processed. */
  protected transient Actor m_Current;

  /** the context. */
  protected FlowPanel m_Context;

  /**
   * Sets the FlowPanel context.
   *
   * @param value	the context, null if none
   */
  public void setContext(FlowPanel value) {
    m_Context = value;
  }

  /**
   * Returns the FlowPanel context.
   *
   * @return		the context, null if not set
   */
  public FlowPanel getContext() {
    return m_Context;
  }

  /**
   * Tries to locate the enclosing actor.
   *
   * @param owner	the option handler
   * @param path	the traversal path so far
   * @return		the actor, null if failed to locate
   */
  protected Actor findEnclosingActor(OptionHandler owner, OptionTraversalPath path, int index) {
    if (path.getObject(index) instanceof Actor)
      return (Actor) path.getObject(index);
    if (index > 0)
      return findEnclosingActor(owner, path, index - 1);
    return null;
  }

  /**
   * Tries to locate the enclosing actor.
   *
   * @param owner	the option handler
   * @param path	the traversal path so far
   * @return		the actor, null if failed to locate
   */
  protected Actor findEnclosingActor(OptionHandler owner, OptionTraversalPath path) {
    if (owner instanceof Actor)
      return (Actor) owner;
    else
      return findEnclosingActor(owner, path, path.size() - 1);
  }

  /**
   * Creates a location string used in the list.
   *
   * @param owner	the option handler
   * @param obj		the object where the name was located
   * @return		the generated location string
   */
  protected String createLocation(OptionHandler owner, Object obj, OptionTraversalPath path) {
    Actor	actor;

    actor = findEnclosingActor(owner, path);
    if (actor != null)
      return actor.getFullName();
    else
      return owner.getClass().getName();
  }

  /**
   * Returns the string representation of the object that is added to the list.
   *
   * @param option	the current option
   * @param obj		the object to turn into a string
   * @return		the string representation, null if to ignore the item
   */
  @Override
  protected String objectToString(AbstractOption option, Object obj, OptionTraversalPath path) {
    return createLocation(option.getOptionHandler(), obj, path);
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original for
   * 			processors implementing ModifyingProcessor)
   * @see		ModifyingProcessor
   */
  protected void processActor(Actor actor) {
    m_Current = actor;
    super.processActor(actor);
  }

  /**
   * Returns whether the list should be sorted.
   *
   * @return		true if the list should get sorted
   */
  @Override
  protected boolean isSortedList() {
    return false;
  }

  /**
   * Returns whether the list should not contain any duplicates.
   *
   * @return		true if the list contains no duplicates
   */
  @Override
  protected boolean isUniqueList() {
    return true;
  }

  /**
   * Returns the graphical output that was generated.
   *
   * @return		the graphical output
   */
  @Override
  public Component getGraphicalOutput() {
    final Flow			flow;
    Component			parentComp;
    ActorLocationsPanel		locationsPanel;

    if (m_Current instanceof Flow)
      flow = (Flow) m_Current;
    else if ((m_Current != null) && (m_Current.getRoot() instanceof Flow))
      flow = (Flow) m_Current.getRoot();
    else
      flow = null;

    // determine parent component
    parentComp = null;
    if (flow != null)
      parentComp = flow.getParentComponent();
    if ((parentComp == null) && (m_Context != null))
      parentComp = m_Context;

    locationsPanel = new ActorLocationsPanel(parentComp, m_List);
    return locationsPanel;
  }
}
