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
 * RemoteScriptingEngineUpdateEvent.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.event;

import java.util.EventObject;

/**
 * Event that gets sent if the remote scripting engine got updated.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteScriptingEngineUpdateEvent
  extends EventObject {

  private static final long serialVersionUID = -1232191938871689448L;

  /**
   * Constructs a prototypical Event.
   *
   * @param source The object on which the Event initially occurred.
   * @throws IllegalArgumentException if source is null.
   */
  public RemoteScriptingEngineUpdateEvent(Object source) {
    super(source);
  }
}
