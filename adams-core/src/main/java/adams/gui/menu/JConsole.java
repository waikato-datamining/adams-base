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
 * JConsole.java
 * Copyright (C) 2010-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import javax.swing.JOptionPane;

import adams.core.management.ProcessUtils;
import adams.gui.application.AbstractApplicationFrame;

/**
 * Starts up jconsole.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see adams.core.management.JConsole
 */
public class JConsole
  extends AbstractJDKMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 7557643894279210336L;

  /**
   * Initializes the menu item with no owner.
   */
  public JConsole() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public JConsole(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  protected void doLaunch() {
    // query for options
    String options = JOptionPane.showInputDialog(
	null, "Enter the options for " + adams.core.management.JConsole.EXECUTABLE + ":",
	adams.core.management.JConsole.getDefaultOptions());
    if (options == null)
      return;

    final long fPid = ProcessUtils.getVirtualMachinePID();
    final String fOptions = options;
    Thread thread = new Thread(new Runnable() {
      public void run() {
	adams.core.management.JConsole.execute(fOptions, fPid);
      }
    });
    thread.start();
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "JConsole";
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
}