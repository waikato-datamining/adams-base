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
 * RemoteCommandOnFlow.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command;

/**
 * Interface for remote commands that operate on a specific flow, identified
 * by its ID.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see adams.flow.control.RunningFlowsRegistry
 * @see adams.flow.standalone.RegisterFlow
 */
public interface RemoteCommandOnFlow
  extends RemoteCommand {

  /**
   * Sets the ID of the flow.
   *
   * @param value	the ID (shortcut: -1 if there is only one)
   */
  public void setID(int value);

  /**
   * Returns the ID of the flow to get.
   *
   * @return		the ID (shortcut: -1 if there is only one)
   */
  public int getID();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String IDTipText();
}
