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
 * AbstractInstantiatableActor.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.Variables;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandler;
import adams.flow.core.InstantiatableActor;

/**
 * Abstract superclass for actors that allow a base actor to be instantiatable,
 * i.e., used as root element in the flow editor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInstantiatableActor
  extends AbstractActor
  implements InstantiatableActor, ActorHandler {

  /** for serialization. */
  private static final long serialVersionUID = 1979504358437781588L;

  /** the base actor itself. */
  protected AbstractActor m_BaseActor;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "actor", "actor",
	    getDefaultActor());
  }

  /**
   * Returns the default actor to use.
   *
   * @return		the default actor
   */
  protected abstract AbstractActor getDefaultActor();

  /**
   * Sets the base actor.
   *
   * @param value 	the actor
   */
  public void setActor(AbstractActor value) {
    m_BaseActor = value;
    m_BaseActor.setParent(this);

    reset();
  }

  /**
   * Returns the base actor.
   *
   * @return 		the actor
   */
  public AbstractActor getActor() {
    return m_BaseActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String actorTipText();

  /**
   * Sets whether the actor is to be run in headless mode, i.e., suppressing
   * GUI components.
   *
   * @param value	if true then GUI components will be suppressed
   */
  @Override
  public void setHeadless(boolean value) {
    super.setHeadless(value);
    m_BaseActor.setHeadless(value);
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
   * Checks whether the class' options can be inspected. By default, arrays
   * of actors (i.e., the control actor's sub-actors) won't be inspected, as
   * they do it themselves.
   *
   * @param cls		the class to check
   * @return		true if it can be inspected, false otherwise
   */
  @Override
  protected boolean canInspectOptions(Class cls) {
    if (cls == AbstractActor.class)
      return false;
    else
      return super.canInspectOptions(cls);
  }

  /**
   * Returns the size of the group.
   *
   * @return		always 1
   */
  public int size() {
    return 1;
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  public AbstractActor get(int index) {
    if (index == 0)
      return m_BaseActor;
    else
      throw new IllegalArgumentException("Invalid index: " + index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  public void set(int index, AbstractActor actor) {
    if (index == 0)
      m_BaseActor = actor;
    else
      throw new IllegalArgumentException("Invalid index: " + index);
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  public int indexOf(String actor) {
    if (actor.equals(m_BaseActor.getName()))
      return 0;
    else
      return -1;
  }

  /**
   * Returns the first non-skipped actor.
   *
   * @return		the first 'active' actor, null if none available
   */
  public AbstractActor firstActive() {
    if (!m_BaseActor.getSkip())
      return m_BaseActor;
    else
      return null;
  }

  /**
   * Returns the last non-skipped actor.
   *
   * @return		the last 'active' actor, null if none available
   */
  public AbstractActor lastActive() {
    if (!m_BaseActor.getSkip())
      return m_BaseActor;
    else
      return null;
  }
  
  /**
   * Updates the Variables instance in use.
   * <p/>
   * Use with caution!
   *
   * @param value	the instance to use
   */
  @Override
  protected void forceVariables(Variables value) {
    super.forceVariables(value);
    m_BaseActor.setVariables(value);
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null)
      result = m_BaseActor.setUp();

    return result;
  }

  /**
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    return m_BaseActor.execute();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    m_BaseActor.wrapUp();
    super.wrapUp();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_BaseActor.cleanUp();
    super.cleanUp();
  }
}
