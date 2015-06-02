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
 * FlowExecutionListener.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import adams.core.CleanUpHandler;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 * Interface for classes that record flow execution events.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface FlowExecutionListener 
  extends CleanUpHandler {

  /**
   * Sets the owning flow.
   *
   * @param value       the owner
   */
  public void setOwner(Flow value);

  /**
   * Returns the owning flow.
   *
   * @return            the owner
   */
  public Flow getOwner();

  /**
   * Gets called when the flow execution starts.
   */
  public void startListening();
  
  /**
   * Gets called before the actor receives the token.
   * 
   * @param actor	the actor that will receive the token
   * @param token	the token that the actor will receive
   */
  public void preInput(Actor actor, Token token);
  
  /**
   * Gets called after the actor received the token.
   * 
   * @param actor	the actor that received the token
   */
  public void postInput(Actor actor);
  
  /**
   * Gets called before the actor gets executed.
   * 
   * @param actor	the actor that will get executed
   */
  public void preExecute(Actor actor);

  /**
   * Gets called after the actor was executed.
   * 
   * @param actor	the actor that was executed
   */
  public void postExecute(Actor actor);
  
  /**
   * Gets called before a token gets obtained from the actor.
   * 
   * @param actor	the actor the token gets obtained from
   */
  public void preOutput(Actor actor);
  
  /**
   * Gets called after a token was acquired from the actor.
   * 
   * @param actor	the actor that the token was acquired from
   * @param token	the token that was acquired from the actor
   */
  public void postOutput(Actor actor, Token token);
  
  /**
   * Gets called when the flow execution ends.
   */
  public void finishListening();
}
