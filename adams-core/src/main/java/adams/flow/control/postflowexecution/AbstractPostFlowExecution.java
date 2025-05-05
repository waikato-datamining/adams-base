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
 * AbstractFlowErrorExecution.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control.postflowexecution;

import adams.core.MessageCollection;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;

/**
 * Ancestor for plugins that generate/configure actors to execute
 * when the flow stops with an error.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPostFlowExecution
  extends AbstractOptionHandler
  implements PostFlowExecution {

  private static final long serialVersionUID = -4437469176744041096L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Configures the actor to execute after the flow has run (without calling setUp()).
   *
   * @param errors	for collecting errors during configuration
   * @return		the actor, null if none generated
   */
  protected abstract Actor doConfigureExecution(MessageCollection errors);

  /**
   * Configures the actor to execute after the flow has run (including calling setUp()).
   *
   * @param errors	for collecting errors during configuration
   * @return		the actor, null if none generated
   */
  public Actor configureExecution(MessageCollection errors) {
    Actor	result;
    String	msg;

    result = doConfigureExecution(errors);
    if (result != null) {
      msg = result.setUp();
      if (msg != null) {
	errors.add(msg);
	result = null;
      }
    }

    return result;
  }
}
