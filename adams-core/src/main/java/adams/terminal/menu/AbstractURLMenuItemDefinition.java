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
 * AbstractURLMenuItemDefinition.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.terminal.menu;

import adams.terminal.application.AbstractTerminalApplication;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

/**
 * Ancestor for menu item definitions that open a URL in a browser.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractURLMenuItemDefinition
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 4757574306275240997L;

  /**
   * Initializes the menu item with no owner.
   */
  public AbstractURLMenuItemDefinition() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public AbstractURLMenuItemDefinition(AbstractTerminalApplication owner) {
    super(owner);
  }

  /**
   * The URL to open in a browser.
   *
   * @return		the URL
   */
  protected abstract String getURL();

  /**
   * Actual execution.
   *
   * @param context	the context to use
   */
  protected void doRun(WindowBasedTextGUI context) {
    MessageDialog.showMessageDialog(context, getTitle(), getURL());
  }
}