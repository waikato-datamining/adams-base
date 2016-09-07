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
 * AbstractFileCommanderAction.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.filecommander;

import adams.core.ClassLister;
import adams.core.StatusMessageHandler;
import adams.gui.action.AbstractBaseAction;
import adams.gui.tools.FileCommanderPanel;

/**
 * Ancestor for actions for the {@link adams.gui.tools.FileCommanderPanel}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFileCommanderAction
  extends AbstractBaseAction
  implements StatusMessageHandler {

  private static final long serialVersionUID = -3555111594280198534L;

  /** the owner. */
  protected FileCommanderPanel m_Owner;

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(FileCommanderPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner, null if none set
   */
  public FileCommanderPanel getOwner() {
    return m_Owner;
  }

  /**
   * Updates the action.
   */
  public abstract void update();

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    if (m_Owner == null)
      return;
    m_Owner.showStatus(msg);
  }

  /**
   * Returns the available actions.
   *
   * @return		the action classnames
   */
  public static Class[] getActions() {
    return ClassLister.getSingleton().getClasses(AbstractFileCommanderAction.class);
  }
}
