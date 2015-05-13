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
 * FlowAwarePaintlet.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.core;

import adams.flow.core.Actor;

/**
 * Interface for paintlets that need to be aware of the flow, e.g., for
 * accessing variables or storage.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface FlowAwarePaintlet
  extends Paintlet {

  /**
   * Sets the owning actor.
   *
   * @param actor	the actor this paintlet belongs to
   */
  public void setActor(Actor actor);

  /**
   * Returns the owning actor.
   *
   * @return		the actor this paintlet belongs to, null if none set
   */
  public Actor getActor();
}
