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
 * AbstractStdOutProcessor.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command.stdout;

import adams.core.option.AbstractOptionHandler;
import adams.flow.core.FlowContextHandler;

/**
 * Ancestor for processing the command output received on stdout.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractStdOutProcessor
  extends AbstractOptionHandler
  implements StdOutProcessor {

  private static final long serialVersionUID = 7977194867431996321L;

  /** the owning command. */
  protected FlowContextHandler m_Owner;

  /**
   * Configures the handler.
   *
   * @param owner 	the owning command
   * @return 		null if successfully setup, otherwise error message
   */
  public String setUp(FlowContextHandler owner) {
    if (owner == null)
      return "No owner set!";

    m_Owner = owner;

    return null;
  }

  /**
   * Processes the stdout output received when in async mode.
   *
   * @param output	the output to process
   */
  public abstract void processAsync(String output);

  /**
   * Processes the stdout output received when in blocking mode.
   *
   * @param output	the output to process
   */
  public abstract void processBlocking(String output);

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_Owner = null;
  }
}
