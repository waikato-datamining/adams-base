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
 * UserModeUtils.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.option.UserMode;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.Child;

import java.awt.Container;

/**
 * Methods for handling user mode.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class UserModeUtils {

  /**
   * Determines the application frame.
   *
   * @return		the application frame, null if failed to determine
   */
  public static AbstractApplicationFrame getApplicationFrame(Container parent) {
    AbstractApplicationFrame	result;
    Child child;

    result = (AbstractApplicationFrame) GUIHelper.getParent(parent, AbstractApplicationFrame.class);
    if (result == null) {
      child = (Child) GUIHelper.getParent(parent, Child.class);
      if (child != null)
	result = child.getParentFrame();
    }

    return result;
  }

  /**
   * Returns the user mode to apply.
   *
   * @return		the user mode
   */
  public static UserMode getUserMode(Container parent) {
    UserMode			result;
    AbstractApplicationFrame	frame;

    result = UserMode.HIGHEST;
    frame = getApplicationFrame(parent);
    if (frame != null)
      result = frame.getUserMode();

    return result;
  }
}
