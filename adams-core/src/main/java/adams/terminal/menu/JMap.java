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
 * JMap.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.terminal.menu;

import adams.core.management.ProcessUtils;
import adams.terminal.application.AbstractTerminalApplication;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

/**
 * Runs jmap and displays the result.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see adams.core.management.JMap
 */
public class JMap
  extends AbstractJDKMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -1502903491659697700L;

  /**
   * Initializes the menu item with no owner.
   */
  public JMap() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public JMap(AbstractTerminalApplication owner) {
    super(owner);
  }

  /**
   * Launches the functionality of the menu item.
   *
   * @param context	the context to use
   */
  protected void doLaunch(final WindowBasedTextGUI context) {
    String	options;
    String 	output;

    options = TextInputDialog.showDialog(
      context, getTitle(),
      "Enter the options for " + adams.core.management.JMap.EXECUTABLE + ":",
      adams.core.management.JMap.getDefaultOptions());
    if (options == null)
      return;

    output = adams.core.management.JMap.execute(options, ProcessUtils.getVirtualMachinePID());

    MessageDialog.showMessageDialog(context, getTitle(), output);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "JMap";
  }
}