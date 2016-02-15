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
 * AbstractModifyingProcessor.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.flow.core.Actor;

/**
 * Ancestor for processors that potentially modify flows that they are processing.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractModifyingProcessor
  extends AbstractActorProcessor
  implements ModifyingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 589249611194517455L;

  /** whether the flow was modified. */
  protected boolean m_Modified;

  /** the modified flow. */
  protected Actor m_ModifiedActor;

  /** whether to suppress copying the actor. */
  protected boolean m_NoCopy;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_NoCopy = false;
  }
  
  /**
   * Sets whether to suppress copying the actor first before processing.
   * 
   * @param value	if true, no copy of the actor is generated
   */
  public void setNoCopy(boolean value) {
    m_NoCopy = value;
  }
  
  /**
   * Returns whether the copying of the actor is suppressed.
   * 
   * @return		true if no copy generated
   */
  public boolean getNoCopy() {
    return m_NoCopy;
  }
  
  /**
   * Processes the actor.
   *
   * @param actor	the actor to process
   */
  @Override
  public void process(Actor actor) {
    m_Modified      = false;
    m_ModifiedActor = null;

    if (!getNoCopy())
      actor = actor.shallowCopy();
    
    super.process(actor);

    if (m_Modified)
      m_ModifiedActor = actor;
  }

  /**
   * Returns whether the actor was modified.
   *
   * @return		true if the actor was modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Returns the modified actor.
   *
   * @return		the modified actor, null if not modified
   */
  public Actor getModifiedActor() {
    return m_ModifiedActor;
  }
}
