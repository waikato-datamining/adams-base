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
 * Null.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control.postflowexecution;

import adams.core.MessageCollection;
import adams.flow.core.Actor;

/**
 * Does not generate an actor.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Null
  extends AbstractPostFlowExecution {

  private static final long serialVersionUID = 5687879090247811987L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Does not generate an actor.";
  }

  /**
   * Configures the actor to execute after the flow has run (without calling setUp()).
   *
   * @param errors for collecting errors during configuration
   * @return the actor, null if none generated
   */
  @Override
  protected Actor doConfigureExecution(MessageCollection errors) {
    return null;
  }
}
