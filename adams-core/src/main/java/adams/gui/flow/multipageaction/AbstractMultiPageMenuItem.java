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
 * AbstractMultiPageMenuItem.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.multipageaction;

import adams.core.logging.LoggingObject;
import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowMultiPagePane;

import javax.swing.Icon;
import javax.swing.JMenuItem;

/**
 * Ancestor for menu item generators for the flow's multi-page pane.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMultiPageMenuItem
  extends LoggingObject
  implements Comparable<AbstractMultiPageMenuItem> {

  private static final long serialVersionUID = -1421960726075633479L;

  /**
   * The name for the menu item.
   *
   * @return		the name
   */
  public abstract String getName();

  /**
   * The name of the group this item belongs to.
   *
   * @return		the name
   */
  public abstract String getGroup();

  /**
   * The name of the icon to use.
   *
   * @return		the name
   */
  public abstract String getIconName();

  /**
   * Returns the icon to use.
   *
   * @return		the icon
   */
  protected Icon getIcon() {
    if (getIconName() == null)
      return GUIHelper.getEmptyIcon();
    else
      return GUIHelper.getIcon(getIconName());
  }

  /**
   * Creates the menu item.
   */
  public abstract JMenuItem getMenuItem(FlowMultiPagePane multi);

  /**
   * Returns a comparison based on the name of the menu items.
   *
   * @param o		the other item to compare against
   * @return		the result of the name comparison
   */
  @Override
  public int compareTo(AbstractMultiPageMenuItem o) {
    int		result;

    result = getGroup().compareTo(o.getGroup());
    if (result == 0)
      result = getName().compareTo(o.getName());

    return result;
  }

  /**
   * Returns whether the two objects are same.
   *
   * @param obj		the object to compare with
   * @return		true if the same
   * @see		#compareTo(AbstractMultiPageMenuItem)
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof AbstractMultiPageMenuItem) && (compareTo((AbstractMultiPageMenuItem) obj) == 0);
  }
}
