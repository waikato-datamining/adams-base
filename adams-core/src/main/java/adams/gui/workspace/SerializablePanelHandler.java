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
 * SerializablePanelHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.workspace;

import java.io.Serializable;

/**
 * Ancestor for handlers for specific panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class SerializablePanelHandler<T extends AbstractWorkspacePanel>
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 8194827957975338306L;

  /**
   * Checks whether this handler can process the given panel.
   * 
   * @param panel	the panel to check
   * @return		true if it can be processed
   */
  public abstract boolean handles(T panel);

  /**
   * Generates a view of the panel that can be serialized.
   * 
   * @param panel	the panel to serialize
   * @return		the data to serialize
   */
  public abstract Object serialize(T panel);

  /**
   * Deserializes the data and configures the panel.
   * 
   * @param panel	the panel to update
   * @param data	the serialized data to restore the panel with
   */
  public abstract void deserialize(T panel, Object data);
}
