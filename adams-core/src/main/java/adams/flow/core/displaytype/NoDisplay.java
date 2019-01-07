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
 * NoDisplay.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.core.displaytype;

import adams.flow.core.AbstractDisplay;

/**
 * Doesn't do anything.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NoDisplay
  extends AbstractDisplayType {

  private static final long serialVersionUID = 5979241280217828381L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Doesn't do anything.";
  }

  /**
   * Updates the options of the display actor, if necessary.
   *
   * @param actor	the actor to update
   */
  @Override
  public void updateOptions(AbstractDisplay actor) {
    // nothing
  }

  /**
   * Initializes the use of the display.
   *
   * @param actor	the actor to display
   */
  public void init(AbstractDisplay actor) {
    // nothing
  }

  /**
   * Shows the display.
   *
   * @param actor	the actor to display
   */
  @Override
  public void show(AbstractDisplay actor) {
    // nothing
  }

  /**
   * Performs wrap up operations.
   *
   * @param actor	the actor to wrap up
   */
  @Override
  public void wrapUp(AbstractDisplay actor) {
    // nothing
  }

  /**
   * Cleans up the GUI.
   *
   * @param actor	the actor to clean up
   */
  @Override
  public void cleanUpGUI(AbstractDisplay actor) {
    // nothing
  }
}
