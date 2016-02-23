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
 * AbstractSubMenuDefinition.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.application;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Ancestor for menu items that provide a submenu.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSubMenuDefinition
  extends AbstractMenuItemDefinition {

  private static final long serialVersionUID = 8078220243183072493L;

  /**
   * Initializes the menu item with no owner.
   */
  public AbstractSubMenuDefinition() {
    super();
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public AbstractSubMenuDefinition(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the menu items to add to the submenu.
   *
   * @return		the menu items
   */
  protected abstract JMenuItem[] getSubMenuItems();

  /**
   * Returns the JMenuItem to use.
   *
   * @return		the menu item
   */
  @Override
  public JMenuItem getMenuItem() {
    JMenu	result;
    ImageIcon icon;

    result = new JMenu(getTitle());
    icon   = getIcon();
    if (icon != null)
      result.setIcon(icon);

    for (JMenuItem item: getSubMenuItems())
      result.add(item);

    return result;
  }
}
