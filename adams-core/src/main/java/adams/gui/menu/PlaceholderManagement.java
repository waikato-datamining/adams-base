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
 * PlaceholderManagement.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.option.UserMode;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.core.GUIHelper;
import adams.gui.tools.PlaceholderManagementPanel;

/**
 * Opens the Placeholder management.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlaceholderManagement
  extends AbstractBasicMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 8649421185696649308L;

  /**
   * Initializes the menu item with no owner.
   */
  public PlaceholderManagement() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public PlaceholderManagement(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  public String getIconName() {
    return "placeholder.gif";
  }

  /**
   * Launches the functionality of the menu item.
   */
  public void launch() {
    createChildFrame(new PlaceholderManagementPanel(), GUIHelper.getDefaultSmallDialogDimension());
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public String getTitle() {
    return "Placeholder management";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  public boolean isSingleton() {
    return true;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  public UserMode getUserMode() {
    return UserMode.EXPERT;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  public String getCategory() {
    return CATEGORY_MAINTENANCE;
  }
}