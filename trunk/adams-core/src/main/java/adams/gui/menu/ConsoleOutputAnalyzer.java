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
 * ConsoleOutputAnalyzer.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.io.PlaceholderFile;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.UserMode;
import adams.gui.tools.ConsoleOutputAnalyzerPanel;

/**
 * Opens the Console output analyzer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConsoleOutputAnalyzer
  extends AbstractParameterHandlingMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 8649421185696649308L;

  /**
   * Initializes the menu item with no owner.
   */
  public ConsoleOutputAnalyzer() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public ConsoleOutputAnalyzer(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "loganalyzer.png";
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    ConsoleOutputAnalyzerPanel panel = new ConsoleOutputAnalyzerPanel();
    createChildFrame(panel, 1000, 800);
    if (m_Parameters.length == 1)
      panel.open(new PlaceholderFile(m_Parameters[0]));
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Console output analyzer";
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
    return UserMode.EXPERT;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_MAINTENANCE;
  }
}