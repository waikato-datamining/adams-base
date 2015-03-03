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
 * ActorInfo.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import java.io.Serializable;

import adams.core.Utils;


/**
 * Container for information about an ActorHandler.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
   * Initializes the info. Allows source actors.
   *
   * @param canContainStandalones	whether the actor can manage standalones
   * @param actorExecution		the execution/orientation of the actors
   * @param forwardsInput		whether the actor handler forwards input to the sub-actors
   */
  public ActorHandlerInfo(boolean canContainStandalones, ActorExecution actorExecution, boolean forwardsInput) {
    this(canContainStandalones, true, actorExecution, forwardsInput);
  }

  /**
   * Initializes the info.
   *
   * @param canContainStandalones	whether the actor can manage standalones
   * @param canContainSource		whether the actor can contain a source
   * @param actorExecution		the execution/orientation of the actors
   * @param forwardsInput		whether the actor handler forwards input to the sub-actors
   */
  public ActorHandlerInfo(boolean canContainStandalones, boolean canContainSource, ActorExecution actorExecution, boolean forwardsInput) {
    this(canContainStandalones, canContainSource, actorExecution, forwardsInput, null);
  }

  /**
   * Initializes the info.
   *
   * @param canContainStandalones	whether the actor can manage standalones
   * @param canContainSource		whether the actor can contain a source
   * @param actorExecution		the execution/orientation of the actors
   * @param forwardsInput		whether the actor handler forwards input to the sub-actors
   * @param restrictions		further class/interface restrictions, use null or empty array for no restrictions
   */
  public ActorHandlerInfo(boolean canContainStandalones, boolean canContainSource, ActorExecution actorExecution, boolean forwardsInput, Class[] restrictions) {
    this(canContainStandalones, canContainSource, actorExecution, forwardsInput, restrictions, true);
  }

  /**
   * Initializes the info.
   *
   * @param canContainStandalones	whether the actor can manage standalones
   * @param canContainSource		whether the actor can contain a source
   * @param actorExecution		the execution/orientation of the actors
   * @param forwardsInput		whether the actor handler forwards input to the sub-actors
   * @param restrictions		further class/interface restrictions, use null or empty array for no restrictions
   * @param canEncloseActors		whether this actor can be used to enclose other actors
   */
  public ActorHandlerInfo(boolean canContainStandalones, boolean canContainSource, ActorExecution actorExecution, boolean forwardsInput, Class[] restrictions, boolean canEncloseActors) {
    super();

    m_CanContainStandalones = canContainStandalones;
    m_CanContainSource      = canContainSource;
    m_CanEncloseActors      = canEncloseActors;
    m_ActorExecution        = actorExecution;
    m_ForwardsInput         = forwardsInput;
    if (restrictions == null)
      m_Restrictions = new Class[0];
    else
      m_Restrictions = restrictions.clone();
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
   * Returns whether a source is allowed in this group or not.
   *
   * @return		true if a source is allowed
   */
  public boolean canContainSource() {
    return m_CanContainSource;
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
   * Returns the how the actors are executed.
   *
   * @return		how the actors are executed
   */
  public ActorExecution getActorExecution() {
    return m_ActorExecution;
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
        "standalones=" + m_CanContainStandalones + ", "
      + "execution=" + m_ActorExecution + ", "
      + "forwardsInput=" + m_ForwardsInput + ", "
      + "restrictions=" + Utils.arrayToString(m_Restrictions);
  }
}
