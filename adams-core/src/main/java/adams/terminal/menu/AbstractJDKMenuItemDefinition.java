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
 * AbstractJDKMenuItemDefinition.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.terminal.menu;

import adams.core.management.Java;
import adams.terminal.application.AbstractTerminalApplication;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

/**
 * Ancestor for menu items that require a JDK present.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractJDKMenuItemDefinition
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 5622223654165288462L;

  /**
   * Initializes the menu item with no owner.
   */
  public AbstractJDKMenuItemDefinition() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public AbstractJDKMenuItemDefinition(AbstractTerminalApplication owner) {
    super(owner);
  }

  /**
   * Performs the actual launch.
   *
   * @param context	the context to use
   */
  protected abstract void doLaunch(final WindowBasedTextGUI context);

  /**
   * Actual execution.
   *
   * @param context	the context to use
   */
  protected void doRun(WindowBasedTextGUI context) {
    if (!Java.isJDK()) {
      MessageDialog.showMessageDialog(context, getTitle(),
	"No JDK installed or JAVA_HOME does not point to it!\n"
	  + "JAVA_HOME: " + System.getenv("JAVA_HOME"));
      return;
    }

    doLaunch(context);
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
