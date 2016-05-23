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
 * AbstractNotificationAreaAction.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.notificationareaaction;

import adams.core.ClassLister;
import adams.gui.action.AbstractBaseAction;
import adams.gui.flow.FlowPanelNotificationArea;

/**
 * Ancestor for actions on the notification area of the flow editor.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractNotificationAreaAction
  extends AbstractBaseAction {

  private static final long serialVersionUID = -3555111594280198534L;

  /** the owner. */
  protected FlowPanelNotificationArea m_Owner;

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(FlowPanelNotificationArea value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner, null if none set
   */
  public FlowPanelNotificationArea getOwner() {
    return m_Owner;
  }

  /**
   * Updates the action.
   */
  public abstract void update();

  /**
   * Returns the available actions.
   *
   * @return		the action classnames
   */
  public static String[] getActions() {
    return ClassLister.getSingleton().getClassnames(AbstractNotificationAreaAction.class);
  }
}
