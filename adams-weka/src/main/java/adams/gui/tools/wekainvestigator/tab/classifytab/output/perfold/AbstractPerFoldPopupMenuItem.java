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
 * AbstractPerFoldPopupMenuItem.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output.perfold;

import adams.core.ClassLister;
import adams.gui.core.ConsolePanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.AbstractOutputGenerator;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Ancestor for classes that add menu items to the per-fold popup menu.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPerFoldPopupMenuItem {

  /** the originating output generator. */
  protected AbstractOutputGenerator m_OutputGenerator;

  /**
   * Sets the output generator.
   *
   * @param value	the generator
   */
  public void setOutputGenerator(AbstractOutputGenerator value) {
    m_OutputGenerator = value;
  }

  /**
   * Returns the output generator.
   *
   * @return		the generator
   */
  public AbstractOutputGenerator getOutputGenerator() {
    return m_OutputGenerator;
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
   * Creates the menu item to add to the pane's popup menu.
   *
   * @param pane	the per-fold panel this menu is for
   * @param indices	the selected indices
   * @return		the menu item, null if failed to generate
   */
  public abstract JMenuItem createMenuItem(PerFoldMultiPagePane pane, int[] indices);

  /**
   * Updates the menu.
   *
   * @param pane	the per-fold pane
   * @param owner 	the owning tab
   * @param generator 	the output generator that was used to generate the per-fold pane
   * @param indices	the currently selected indices
   * @param menu	the menu to update
   */
  public static void updatePopupMenu(PerFoldMultiPagePane pane, AbstractOutputGenerator generator, int[] indices, JPopupMenu menu) {
    Class[]				classes;
    List<AbstractPerFoldPopupMenuItem>	menuitems;
    String				category;
    JMenuItem item;

    // collect menuitems
    classes   = ClassLister.getSingleton().getClasses(AbstractPerFoldPopupMenuItem.class);
    menuitems = new ArrayList<>();
    for (Class cls: classes) {
      try {
	menuitems.add((AbstractPerFoldPopupMenuItem) cls.newInstance());
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(
	  Level.SEVERE, "Failed to instantiate menu item class: " + cls.getName(), e);
      }
    }
    Collections.sort(menuitems, new MenuItemComparator());

    // update menu
    category = "";
    for (AbstractPerFoldPopupMenuItem menuitem: menuitems) {
      if (!menuitem.getCategory().equals(category))
	menu.addSeparator();
      menuitem.setOutputGenerator(generator);
      category = menuitem.getCategory();
      item     = menuitem.createMenuItem(pane, indices);
      if (item != null)
	menu.add(item);
    }
  }
}
