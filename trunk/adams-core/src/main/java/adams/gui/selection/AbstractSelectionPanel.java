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
 * AbstractSelectionPanel.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.selection;

import javax.swing.JButton;

import adams.core.CleanUpHandler;
import adams.gui.core.BasePanel;

/**
 * Abstract ancestor for all selection panels.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSelectionPanel
  extends BasePanel
  implements CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -132005556563237265L;

  /** the default time in msec to wait for refresh to finish. */
  public final static int DEFAULT_REFRESH_TIMEOUT = 2000;

  /**
   * Waits for a button to become enabled again, for DEFAULT_REFRESH_TIMEOUT
   * milliseconds.
   *
   * @param button	the button to check
   * @return		true if the button became available
   * @see		#DEFAULT_REFRESH_TIMEOUT
   */
  protected boolean waitForEnabled(JButton button) {
    return waitForEnabled(button, DEFAULT_REFRESH_TIMEOUT);
  }

  /**
   * Waits for a button to become enabled again, for a maximum amount of time.
   *
   * @param button	the button to check
   * @param msec	the maximum amount of milliseconds to wait
   * @return		true if the button became available
   */
  protected boolean waitForEnabled(JButton button, int msec) {
    int		current;

    current = 0;

    while ((current < msec) && (!button.isEnabled())) {
      synchronized(this) {
	try {
	  wait(50);
	}
	catch (Exception e) {
	  // ignored
	}
	current += 50;
      }
    }

    return button.isEnabled();
  }

  /**
   * Sub-classes need to define which widget grabs the actual focus.
   */
  public abstract void grabFocus();
}
