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
 * About.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.gui.application.AboutBoxPanel;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.ChildFrame;
import adams.gui.application.UserMode;

/**
 * Displays the infamous about box.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class About
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -771667287275117680L;

  /**
   * Initializes the menu item with no owner.
   */
  public About() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public About(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "help.gif";
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    AboutBoxPanel panel = new AboutBoxPanel();
    panel.addInfo("<html><font color='red'>A</font>dvanced <font color='red'>D</font>ata mining and <font color='red'>M</font>achine learning <font color='red'>S</font>ystem</html>");
    panel.addInfo("(c) 2009-2015 University of Waikato");
    panel.addInfo("Hamilton, New Zealand");
    ChildFrame frame = createChildFrame(panel);
    frame.setResizable(false);
    frame.pack();
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "About";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return true;
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
    return CATEGORY_HELP;
  }
}