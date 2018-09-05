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
 * Null.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.flowrestart.notification;

import adams.flow.control.Flow;

/**
 * Dummy, does nothing.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Null
  extends AbstractNotification {

  private static final long serialVersionUID = -3143476257046586198L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, does nothing.";
  }

  /**
   * Sends a notification.
   *
   * @param flow	the flow that triggered the notification
   * @return		null if successfully notified, otherwise the error message
   */
  @Override
  public String notify(Flow flow) {
    return null;
  }
}
