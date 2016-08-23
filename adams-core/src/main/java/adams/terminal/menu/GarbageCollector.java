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
 * GarbageCollector.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.terminal.menu;

import adams.terminal.application.AbstractTerminalApplication;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

/**
 * Runs the garbage collector.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GarbageCollector
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -2688798836965476377L;

  /**
   * Initializes the menu item with no owner.
   */
  public GarbageCollector() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public GarbageCollector(AbstractTerminalApplication owner) {
    super(owner);
  }

  /**
   * Actual execution.
   *
   * @param context	the context to use
   */
  protected void doRun(WindowBasedTextGUI context) {
    System.gc();
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public String getTitle() {
    return "Garbage collector";
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  public String getCategory() {
    return CATEGORY_HELP;
  }
}