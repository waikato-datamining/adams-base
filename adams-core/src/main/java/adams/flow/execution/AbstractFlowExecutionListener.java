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
 * AbstractFlowExecutionListener.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import adams.core.ClassLister;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 * Ancestor for execution listeners.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFlowExecutionListener
  extends AbstractOptionHandler
  implements Comparable, FlowExecutionListener, ShallowCopySupporter<FlowExecutionListener> {

  /** for serialization. */
  private static final long serialVersionUID = 476112131534239867L;

  /** the owner. */
  protected Flow m_Owner;

  /**
   * Sets the owning flow.
   *
   * @param value       the owner
   */
  public void setOwner(Flow value) {
    m_Owner = value;
  }

  /**
   * Returns the owning flow.
   *
   * @return            the owner
   */
  public Flow getOwner() {
    return m_Owner;
  }

  /**
   * Gets called when the flow execution starts.
   * <br><br>
   * Default implementation does nothing.
   */
  public void startListening() {
  }
  
  /**
   * Gets called before the actor receives the token.
   * <br><br>
   * Default implementation does nothing.
   * 
   * @param actor	the actor that will receive the token
   * @param token	the token that the actor will receive
   */
  public void preInput(Actor actor, Token token) {
  }
  
  /**
   * Gets called after the actor received the token.
   * <br><br>
   * Default implementation does nothing.
   * 
   * @param actor	the actor that received the token
   */
  public void postInput(Actor actor) {
  }
  
  /**
   * Gets called before the actor gets executed.
   * <br><br>
   * Default implementation does nothing.
   * 
   * @param actor	the actor that will get executed
   */
  public void preExecute(Actor actor) {
  }

  /**
   * Gets called after the actor was executed.
   * <br><br>
   * Default implementation does nothing.
   * 
   * @param actor	the actor that was executed
   */
  public void postExecute(Actor actor) {
  }
  
  /**
   * Gets called before a token gets obtained from the actor.
   * <br><br>
   * Default implementation does nothing.
   * 
   * @param actor	the actor the token gets obtained from
   */
  public void preOutput(Actor actor) {
  }
  
  /**
   * Gets called after a token was acquired from the actor.
   * <br><br>
   * Default implementation does nothing.
   * 
   * @param actor	the actor that the token was acquired from
   * @param token	the token that was acquired from the actor
   */
  public void postOutput(Actor actor, Token token) {
  }

  /**
   * Cleans up data structures, frees up memory.
   * <br><br>
   * Default implementation cleans up options.
   */
  public void cleanUp() {
    cleanUpOptions();
  }
  
  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public FlowExecutionListener shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public FlowExecutionListener shallowCopy(boolean expand) {
    return (FlowExecutionListener) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of processors.
   *
   * @return		the processor classnames
   */
  public static String[] getListeners() {
    return ClassLister.getSingleton().getClassnames(FlowExecutionListener.class);
  }
  
  /**
   * Gets called when the flow execution ends.
   * <br><br>
   * Default implementation only unsets the owner.
   */
  public void finishListening() {
    m_Owner = null;
  }
}
