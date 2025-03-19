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
 * SubProcessEventTrigger.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.subprocessevent;

import adams.flow.standalone.SubProcessEvent;

/**
 * Interface for triggers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @param <I> 	the data to receive
 * @param <O> 	the data to send
 */
public interface SubProcessEventTrigger<I, O> {

  /**
   * Configures the trigger.
   *
   * @param owner 	the owning event
   * @return		null if successfully configured, otherwise error message
   */
  public String setUp(SubProcessEvent owner);

  /**
   * Wraps up the trigger.
   */
  public void wrapUp();
}
