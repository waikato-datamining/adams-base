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
 * FlowControlButtonsSupporter.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

/**
 * Interfaces for actors that can display flow control button(s).
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface FlowControlButtonsSupporter
  extends Actor {

  /**
   * Sets whether to show flow control button(s).
   *
   * @param value 	true if to show
   */
  public void setShowFlowControlButtons(boolean value);

  /**
   * Returns whether to show flow control button(s).
   *
   * @return 		true if to show
   */
  public boolean getShowFlowControlButtons();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showFlowControlButtonsTipText();
}
