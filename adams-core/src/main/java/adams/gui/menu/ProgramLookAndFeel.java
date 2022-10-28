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
 * ProgramLookAndFeel.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.Utils;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.BaseMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.laf.AbstractLookAndFeel;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.util.logging.Level;

/**
 * Allows the user to switch the look and feel.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ProgramLookAndFeel
  extends AbstractBasicMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 2322866186840295800L;

  /**
   * Initializes the menu item with no owner.
   */
  public ProgramLookAndFeel() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public ProgramLookAndFeel(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Does nothing.
   */
  @Override
  public void launch() {
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Look'n'feel";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  public String getIconName() {
    return "lookandfeel.png";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return false;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_PROGRAM;
  }

  /**
   * Returns the JMenuItem to use.
   *
   * @return		the menu item
   * @see		#launch()
   */
  @Override
  public JMenuItem getMenuItem() {
    BaseMenu	result;
    JMenuItem	menuitem;

    result = new BaseMenu(getTitle());
    result.setIcon(ImageManager.getIcon(getIconName()));

    for (final Class cls: AbstractLookAndFeel.getLookAndFeels()) {
      try {
        final AbstractLookAndFeel laf = (AbstractLookAndFeel) cls.getDeclaredConstructor().newInstance();
        menuitem = new JMenuItem(laf.getName());
        menuitem.setEnabled(laf.isAvailable());
        menuitem.addActionListener((ActionEvent e) -> {
          AbstractLookAndFeel.installLookAndFeel(laf);
          GUIHelper.showInformationMessage(null, "Please restart the application for the look and feel to fully take effect.");
        });
        result.add(menuitem);
      }
      catch (Exception e) {
        getLogger().log(Level.SEVERE, "Failed to instantiate look and feel: " + Utils.classToString(cls));
      }
    }

    result.sort();

    return result;
  }
}