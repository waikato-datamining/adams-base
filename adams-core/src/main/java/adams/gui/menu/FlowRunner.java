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
 * FlowRunner.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.io.PlaceholderFile;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.UserMode;
import adams.gui.flow.FlowRunnerPanel;

/**
 * Opens the Flow Runner.
 * You can load/run flows. If no prefix or prefixed with "load:" a file only
 * gets loaded. If prefixed with "run:" the file gets loaded and executed.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowRunner
  extends AbstractParameterHandlingMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -993239671663669946L;

  /**
   * Initializes the menu item with no owner.
   */
  public FlowRunner() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public FlowRunner(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  public String getIconName() {
    return "flowrunner.gif";
  }

  /**
   * Launches the functionality of the menu item.
   */
  public void launch() {
    FlowRunnerPanel panel = new FlowRunnerPanel();
    createChildFrame(panel, 640, 480);
    if (m_Parameters.length > 0) {
      if (m_Parameters[0].startsWith("run:"))
        panel.runUnsafe(new PlaceholderFile(m_Parameters[0].substring(4)));
      else if (m_Parameters[0].startsWith("load:"))
        panel.loadUnsafe(new PlaceholderFile(m_Parameters[0].substring(5)));
      else
        panel.loadUnsafe(new PlaceholderFile(m_Parameters[0]));
    }
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public String getTitle() {
    return "Flow runner";
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
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  public String getCategory() {
    return CATEGORY_TOOLS;
  }
}