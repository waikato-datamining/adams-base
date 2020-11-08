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
 * All.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.interactionlogging;

/**
 * Logs all events.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class All
  extends AbstractInteractionLoggingFilter {

  private static final long serialVersionUID = -7834618782093662555L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Logs all events.";
  }

  /**
   * Logs the interaction.
   *
   * @param e		the interaction event
   */
  @Override
  protected void doFilterInteractionLog(InteractionEvent e) {
    e.getLogManager().addInteractionLog(e);
  }
}
