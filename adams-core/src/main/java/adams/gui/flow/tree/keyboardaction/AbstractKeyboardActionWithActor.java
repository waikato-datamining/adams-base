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
 * AbstractKeyboardActionWithActor.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.keyboardaction;

import adams.flow.core.AbstractActor;

/**
 * Ancestor for actions that do something with a specified actor.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractKeyboardActionWithActor
  extends AbstractKeyboardAction {

  private static final long serialVersionUID = 9158512844896786075L;

  /** the actor to use. */
  protected AbstractActor m_Actor;

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
   * Returns the default actor of the action.
   *
   * @return 		the default
   */
  protected abstract AbstractActor getDefaultActor();

  /**
   * Sets the actor of the action.
   *
   * @param value 	the actor
   */
  public void setActor(AbstractActor value) {
      m_Actor = value;
    reset();
  }

  /**
   * Returns the actor of the action.
   *
   * @return 		the actor
   */
  public AbstractActor getActor() {
    return m_Actor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String actorTipText();
}
