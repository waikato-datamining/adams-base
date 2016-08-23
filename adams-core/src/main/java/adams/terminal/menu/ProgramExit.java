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
 * ProgramExit.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.terminal.menu;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

/**
 * Exits the application.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ProgramExit
  extends AbstractMenuItemDefinition {

  private static final long serialVersionUID = 4850496199819749023L;

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Exit";
  }


  /**
   * Actual execution.
   *
   * @param context	the context to use
   */
  protected void doRun(WindowBasedTextGUI context) {
    m_Owner.stop();
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
