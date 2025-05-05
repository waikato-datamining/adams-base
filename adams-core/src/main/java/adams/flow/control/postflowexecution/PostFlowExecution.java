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
 * PostFlowExecution.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control.postflowexecution;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.flow.core.Actor;

/**
 * Interface for class that configure an actor to execute after the flow got executed.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface PostFlowExecution
  extends QuickInfoSupporter {

  /**
   * Configures the actor to execute when the flow stops with an error (including calling setUp()).
   *
   * @param errors	for collecting errors during configuration
   * @return		the actor, null if none generated
   */
  public Actor configureExecution(MessageCollection errors);
}
