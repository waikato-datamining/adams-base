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
 * AbstractHistoryPopupMenuItem.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.history;

import adams.core.ClassLister;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.ConsolePanel;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Ancestor for classes that add menu items to the history popup menu.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractHistoryPopupMenuItem<H extends AbstractNamedHistoryPanel, T extends AbstractInvestigatorTab> {

  /** the owner. */
  protected T m_Owner;

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(T value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public T getOwner() {
    return m_Owner;
  }

  /**
   * The category for grouping menu items.
   *
   * @return		the group
   */
  public abstract String getCategory();

  /**
   * The menu item title.
   *
   * @return		the title
   */
  public abstract String getTitle();

  /**
   * Creates the menu item to add to the history's popup menu.
   *
   * @param history	the history panel this menu is for
   * @param indices	the selected indices
   * @return		the menu item, null if failed to generate
   */
  public abstract JMenuItem createMenuItem(H history, int[] indices);

  /**
   * Updates the menu for the specified superclass of menu items.
   *
   * @param history	the history panel
   * @param indices	the currently selected indices
   * @param menu	the menu to update
   * @param superclass	the superclass for the menu items
   */
  public static void updatePopupMenu(AbstractNamedHistoryPanel history, AbstractInvestigatorTab owner, int[] indices, JPopupMenu menu, Class superclass) {
    Class[]				classes;
    List<AbstractHistoryPopupMenuItem>	menuitems;
    String				category;
    JMenuItem				item;

    // collect menuitems
    classes   = ClassLister.getSingleton().getClasses(superclass);
    menuitems = new ArrayList<>();
    for (Class cls: classes) {
      try {
	menuitems.add((AbstractHistoryPopupMenuItem) cls.newInstance());
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(
	  Level.SEVERE, "Failed to instantiate menu item class: " + cls.getName(), e);
      }
    }
    Collections.sort(menuitems, new MenuItemComparator());

    // update menu
    category = "";
    for (AbstractHistoryPopupMenuItem menuitem: menuitems) {
      if (!menuitem.getCategory().equals(category))
	menu.addSeparator();
      menuitem.setOwner(owner);
      category = menuitem.getCategory();
      item     = menuitem.createMenuItem(history, indices);
      if (item != null)
	menu.add(item);
    }
  }
}
