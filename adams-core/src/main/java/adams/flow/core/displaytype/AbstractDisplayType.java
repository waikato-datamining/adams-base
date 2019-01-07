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
 * AbstractDisplayType.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.core.displaytype;

import adams.core.option.AbstractOptionHandler;
import adams.flow.core.AbstractDisplay;

/**
 * Ancestor.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDisplayType
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -8694040576737692918L;

  /**
   * Updates the options of the display actor, if necessary.
   *
   * @param actor	the actor to update
   */
  public abstract void updateOptions(AbstractDisplay actor);

  /**
   * Initializes the use of the display.
   *
   * @param actor	the actor to display
   */
  public abstract void init(AbstractDisplay actor);

  /**
   * Shows the display.
   *
   * @param actor	the actor to display
   */
  public abstract void show(AbstractDisplay actor);

  /**
   * Performs wrap up operations.
   *
   * @param actor	the actor to wrap up
   */
  public abstract void wrapUp(AbstractDisplay actor);

  /**
   * Cleans up the GUI.
   *
   * @param actor	the actor to clean up
   */
  public abstract void cleanUpGUI(AbstractDisplay actor);
}
