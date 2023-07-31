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
 * Terminal.java
 * Copyright (C) 2014-2023 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.management.User;
import adams.core.option.UserMode;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;

import java.io.File;

/**
 * Launches a terminal.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8366 $
 */
public class Terminal
  extends AbstractBasicMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -1476004907427664251L;

  /**
   * Initializes the menu item with no owner.
   */
  public Terminal() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public Terminal(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "terminal.png";
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    adams.core.management.Terminal.launch(new File(User.getCWD()));
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Terminal";
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
}