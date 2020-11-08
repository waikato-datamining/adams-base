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
 * InteractionLogManager.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.interactionlogging;

import java.util.List;

/**
 * Interface for classes that manage interaction logs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface InteractionLogManager {

  /**
   * Clears the interaction log.
   */
  public void clearInteractionLog();

  /**
   * Adds the interaction event to the log.
   *
   * @param e		the event to add
   */
  public void addInteractionLog(InteractionEvent e);

  /**
   * Checks whether there have been any interactions recorded.
   *
   * @return		true if interactions are available
   */
  public boolean hasInteractionLog();

  /**
   * Returns the interaction log.
   *
   * @return		the log, null if nothing recorded
   */
  public List<InteractionEvent> getInteractionLog();
}
