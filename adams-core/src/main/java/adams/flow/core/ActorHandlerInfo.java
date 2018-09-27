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
 * ActorInfo.java
 * Copyright (C) 2011-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.Utils;

import java.io.Serializable;


/**
 * Container for information about an ActorHandler.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ActorHandlerInfo
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -6507471599929125550L;

  /** whether the actor can contain standalone sub-actors. */
  protected boolean m_CanContainStandalones;

  /** whether the actor can contain a source sub-actor. */
  protected boolean m_CanContainSource;

  /** whether the actor can be used for enclosing other actors. */
  protected boolean m_CanEncloseActors;

  /** how the sub-actors are executed/oriented. */
  protected ActorExecution m_ActorExecution;

  /** whether the actor handler forwards input to the sub-actors. */
  protected boolean m_ForwardsInput;

  /** further class/interface restrictions. */
  protected Class[] m_Restrictions;

  /**
   * Initializes the info.
   */
  public ActorHandlerInfo() {
    super();
    allowStandalones(false);
    allowSource(true);
    allowEncloseActors(true);
    forwardsInput(false);
    restrictions(null);
    actorExecution(ActorExecution.UNDEFINED);
  }

  /**
   * Initializes the info with the provided info object.
   *
   * @param info 	the other info object to initialize with
   */
  public ActorHandlerInfo(ActorHandlerInfo info) {
    super();
    allowStandalones(info.canContainStandalones());
    allowSource(info.canContainSource());
    allowEncloseActors(info.canEncloseActors());
    forwardsInput(info.getForwardsInput());
    restrictions(info.getRestrictions());
    actorExecution(info.getActorExecution());
  }

  /**
   * Sets whether to allow standalones.
   *
   * @param value 	true if to allow
   * @return		itself
   */
  public ActorHandlerInfo allowStandalones(boolean value) {
    m_CanContainStandalones = value;
    return this;
  }

  /**
   * Returns whether standalones are allowed in this group or not.
   *
   * @return		true if standalones are allowed
   */
  public boolean canContainStandalones() {
    return m_CanContainStandalones;
  }

  /**
   * Sets whether to allow source.
   *
   * @param value 	true if to allow
   * @return		itself
   */
  public ActorHandlerInfo allowSource(boolean value) {
    m_CanContainSource = value;
    return this;
  }

  /**
   * Returns whether a source is allowed in this group or not.
   *
   * @return		true if a source is allowed
   */
  public boolean canContainSource() {
    return m_CanContainSource;
  }

  /**
   * Sets whether to allow enclosing of actors.
   *
   * @param value 	true if to allow
   * @return		itself
   */
  public ActorHandlerInfo allowEncloseActors(boolean value) {
    m_CanEncloseActors = value;
    return this;
  }

  /**
   * Returns whether this actor can be used to enclose others.
   *
   * @return		true if other actors can be enclose
   */
  public boolean canEncloseActors() {
    return m_CanEncloseActors;
  }

  /**
   * Sets how the actors are executed.
   *
   * @param value 	the execution
   * @return		itself
   */
  public ActorHandlerInfo actorExecution(ActorExecution value) {
    m_ActorExecution = value;
    return this;
  }

  /**
   * Returns the how the actors are executed.
   *
   * @return		how the actors are executed
   */
  public ActorExecution getActorExecution() {
    return m_ActorExecution;
  }

  /**
   * Sets whether to actor forwards the input.
   *
   * @param value 	true if it forwards
   * @return		itself
   */
  public ActorHandlerInfo forwardsInput(boolean value) {
    m_ForwardsInput = value;
    return this;
  }

  /**
   * Returns whether the handler forwards the input.
   *
   * @return		true if handler forwards the input
   */
  public boolean getForwardsInput() {
    return m_ForwardsInput;
  }

  /**
   * Sets the restrictions in terms of classes/interfaces.
   *
   * @param value 	the restrictions, null or empty array for none
   * @return		itself
   */
  public ActorHandlerInfo restrictions(Class[] value) {
    if (value == null)
      value = new Class[0];
    m_Restrictions = value;
    return this;
  }

  /**
   * Returns whether there are further restrictions in regards to classes
   * and/or interfaces.
   *
   * @return		true if there are restrictions
   */
  public boolean hasRestrictions() {
    return (m_Restrictions.length > 0);
  }

  /**
   * Returns the restrictions on classes and/or interfaces.
   *
   * @return		the restrictions, if any
   */
  public Class[] getRestrictions() {
    return m_Restrictions;
  }

  /**
   * Returns a short string representation of the info object.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return
      "standalones=" + canContainStandalones() + ", "
	+ "source=" + canContainSource()
	+ "execution=" + getActorExecution() + ", "
	+ "forwardsInput=" + getForwardsInput() + ", "
	+ "encloseActors=" + canEncloseActors() + ", "
	+ "restrictions=" + Utils.arrayToString(getRestrictions());
  }
}
